package com.nbreds.bigdata.counterror.job;

import java.io.Serializable;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.nbreds.bigdata.common.CommonConfig;
import com.nbreds.bigdata.common.CommonConstants;
import com.nbreds.bigdata.common.util.CommonUtil;
import com.nbreds.bigdata.common.util.SparkContext;
import com.nbreds.bigdata.counterror.dao.CountErrorDao;
import com.nbreds.bigdata.sms.dao.SmsDao;

import scala.Tuple2;

public class CountErrorJob implements Job, Serializable {

	private static final long serialVersionUID = 1L;
	protected static final Logger logger = Logger.getLogger(CountErrorJob.class);
	
	public static void main(String[] args) {
		System.setProperty("SERVER_TYPE", "dev");
		CountErrorJob job = new CountErrorJob();
		try {
			job.execute(null);
		} catch (JobExecutionException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String className = this.getClass().getName().substring(this.getClass().getName().lastIndexOf(".") + 1, this.getClass().getName().length());
		logger.info(className + " Start");
		if (CommonConstants.countErrorIsRunning) {
			logger.info("Process Still Running....");
			CommonConstants.countErrorRunCheckCount++;
			/**
			 * 싱글톤 방식이나 설정된 최대 스킵 카운트 이상으로 동작하지 못했을경우 프로세스이상으로 간주하고 새로이 시작
			 */
			if (CommonConstants.countErrorRunCheckCount > CommonConstants.maxRunCheckCount) {
				CommonConstants.countErrorRunCheckCount = 0;
			} else {
				return;
			}
		} else {
			CommonConstants.countErrorIsRunning = true;
		}
		
		try {
			// 조회용 parameter
			Map<String, Object> params = new HashMap<String, Object>();
			
			// 조회조건 설정
			setParam(params);
			
			// Spark 연동하여 countError
			getErrorCount(params);
			
			// DB 기준값과 비교하여 알람 발송
			checkErrorCount(params);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			CommonConstants.countErrorIsRunning = false;
		}
	}
	
	private void setParam(Map<String, Object> params) {
		// Time setting
		String date = CommonUtil.getDateTime("yyyy-MM-dd");
		String hour = CommonUtil.getDateTime("HH");
		
		// 현재 시간으로부터 5분 단위 과거 시간
		Calendar cal = Calendar.getInstance(Locale.KOREA);
		long endDate = cal.getTime().getTime();
		cal.add(Calendar.MINUTE, -5);
		long startDate = cal.getTime().getTime();
		
		params.put("NOW_DATE", date);
		params.put("NOW_HOUR", hour);
		params.put("START_DATE", startDate);
		params.put("END_DATE", endDate);
	}
	
	@SuppressWarnings("serial")
	private void getErrorCount(Map<String, Object> params) {
		System.setProperty("hadoop.home.dir", "D:/nbreds");
		SparkContext context = new SparkContext(this.getClass(), CommonConfig.getInstance().getString("jar.path"));
		
		JavaRDD<String> input = context.textFile(
				"/logs/" + String.valueOf(params.get("NOW_DATE"))
				+ "/" + String.valueOf(params.get("NOW_HOUR")) + "/SERVER-01.*.log");
		
		long startDate = Long.parseLong(String.valueOf(params.get("START_DATE")));
		long endDate = Long.parseLong(String.valueOf(params.get("END_DATE")));
		
		// 데이터에서 최근 5분전 데이터만 필터
		JavaRDD<String> data = input.filter(new Function<String, Boolean>() {
			
			@Override
			public Boolean call(String e) throws Exception {
				String strLogTime = e.substring(1, 20);
				long logTime = 0l;
				try {
					logTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strLogTime).getTime();
				} catch (ParseException pe) {
					logger.warn("로그형식 불일치로 인한 날짜 파싱오류... 계속 진행");
				}
				return logTime >= startDate && logTime <= endDate ? Boolean.TRUE : Boolean.FALSE;
			}
		}).filter(new Function<String, Boolean>() { // 데이터에서 상태코드 포함된 내용만 필터 (이 부분은 필요없을 수도??)

			@Override
			public Boolean call(String e) throws Exception {
				return e.contains("statusCode:") ? Boolean.TRUE : Boolean.FALSE;
			}
		}).flatMap(new FlatMapFunction<String, String>() {

			@Override
			public Iterator<String> call(String e) throws Exception {
				return Arrays.asList(e.substring(e.indexOf("statusCode:") + 11, e.indexOf("statusCode:") + 14)).iterator();
			}
		});
		
		// 에러코드별 카운트
		JavaPairRDD<String, Integer> counts = data.mapToPair(new PairFunction<String, String, Integer>() {

			@Override
			public Tuple2<String, Integer> call(String g) throws Exception {
				return new Tuple2<String, Integer>(g, 1);
			}
		}).reduceByKey(new Function2<Integer, Integer, Integer>() {
			public Integer call(Integer x, Integer y) throws Exception {
				return x + y;
			}
		});
		
		if (counts != null) {
			params.put("COUNT_ERROR_RESULT", counts.collect());
		}
		
		context.getContext().stop();
		context.getContext().close();
	}
	
	@SuppressWarnings("unchecked")
	private void checkErrorCount(Map<String, Object> params) throws SQLException {
		List<Tuple2<String, Integer>> countErrorList = (List<Tuple2<String, Integer>>) params.get("COUNT_ERROR_RESULT");
		
		if (countErrorList != null) {
			CountErrorDao countErrorDao = CountErrorDao.getInstance();
			List<Map<String, Object>> alarmTargetList = countErrorDao.checkCountError(params);
			
			// 결과값이 있는 경우 알람 발송
			if (!alarmTargetList.isEmpty()) {
				params.put("ALARM_TARGET_LIST", alarmTargetList);
				logger.info("발송할 알람 있음. 알람 발송 시작");
				sendAlarm(params);
				logger.info("발송할 알람 있음. 알람 발송 종료");
			} else {
				logger.info("발송할 알람 없음. 정상");
			}
		} else {
			logger.info("에러 발생 없음. 정상");
		}
	}
	
	@SuppressWarnings("unchecked")
	private void sendAlarm(Map<String, Object> params) throws SQLException {
		SmsDao smsDao = SmsDao.getInstance();
		List<Tuple2<String, Integer>> countErrorList = (List<Tuple2<String, Integer>>) params.get("COUNT_ERROR_RESULT");
		
		for (Tuple2<String, Integer> tuple : countErrorList) {
			String errorCode = tuple._1();
			int errorCnt = tuple._2();
			
			for (Map<String, Object> map : (List<Map<String, Object>>) params.get("ALARM_TARGET_LIST")) {
				if (errorCode.equals(map.get("ERROR_CODE"))) {
					String[] mdnArray = String.valueOf(map.get("MDN_ARRAY")).split(",");
					params.put("CONTENT", String.valueOf(map.get("ALARM_CONTENT")) + " : " + errorCnt + "건");
					params.put("TITLE", String.valueOf(map.get("ALARM_NAME")));
					
					for (int i = 0; i < mdnArray.length; i++) {
						params.put("RECEIVE_MDN", mdnArray[i]);
						smsDao.insertSms(params);
					}
				}
			}
		}
	}
	
}
