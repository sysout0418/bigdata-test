package com.nbreds.bigdata.countip.job;

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
import com.nbreds.bigdata.countip.dao.CountIPDao;
import com.nbreds.bigdata.sms.dao.SmsDao;

import scala.Tuple2;

public class CountIPJob implements Job, Serializable {

	private static final long serialVersionUID = 1L;
	protected static final Logger logger = Logger.getLogger(CountIPJob.class);

//	public static void main(String[] args) {
//
//		// Test
//		System.setProperty("SERVER_TYPE", "dev");
//		CountIPJob job = new CountIPJob();
//
//		try {
//			job.execute(null);
//		} catch (JobExecutionException e) {
//			e.printStackTrace();
//		}
//
//	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		String className = this.getClass().getName().substring(this.getClass().getName().lastIndexOf(".") + 1, this.getClass().getName().length());
		logger.info(className + " Start");

		if (CommonConstants.countIPIsRunning) {

			logger.info("Process Still Running....");
			CommonConstants.countIPRunCheckCount++;
			/**
			 * 싱글톤 방식이나 설정된 최대 스킵 카운트 이상으로 동작하지 못했을경우 프로세스이상으로 간주하고 새로 시작
			 */
			if (CommonConstants.countIPRunCheckCount > CommonConstants.maxRunCheckCount) {
				CommonConstants.countIPRunCheckCount = 0;
			} else {
				return;
			}

		} else {
			CommonConstants.countIPIsRunning = true;
		}

		try {

			// 조회용 parameter
			Map<String, Object> params = new HashMap<String, Object>();

			// 조회조건 설정
			setParam(params);

			// Spark 연동하여 countIP
			getIPCount(params);

			// DB 기준값과 비교하여 알람 발송
			checkIPCount(params);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CommonConstants.countIPIsRunning = false;
		}

	}

	private void setParam(Map<String, Object> params) {

		// Time setting
		String date = CommonUtil.getDateTime("yyyy-MM-dd");
		String hour = CommonUtil.getDateTime("HH");

		// 현재 시간으로부터 5분 단위 과거 시간
		Calendar cal = Calendar.getInstance(Locale.KOREA);
		long endDate = cal.getTime().getTime();
		cal.add(Calendar.MINUTE, -30);
		long startDate = cal.getTime().getTime();

		params.put("NOW_DATE", date);
		params.put("NOW_HOUR", hour);
		params.put("START_DATE", startDate);
		params.put("END_DATE", endDate);

		SimpleDateFormat sdt = new SimpleDateFormat("HHmm");
		SimpleDateFormat edt = new SimpleDateFormat("HHmm");
		String start_time = sdt.format(startDate);
		String end_time = edt.format(endDate);

		params.put("start_time", start_time);
		params.put("end_time", end_time);
		params.put("week_num", cal.get(Calendar.DAY_OF_WEEK) - 1);
		
	}

	@SuppressWarnings("serial")
	private void getIPCount(Map<String, Object> params) {

		System.setProperty("hadoop.home.dir", "E:/server/winutils");
		SparkContext context = new SparkContext(this.getClass(), CommonConfig.getInstance().getString("jar.path"));

		JavaRDD<String> input = context.textFile("/logs/" + String.valueOf(params.get("NOW_DATE")) + "/" + String.valueOf(params.get("NOW_HOUR")) + "/SERVER-01.*.log");

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
			
		}).flatMap(new FlatMapFunction<String, String>() {

			@Override
			public Iterator<String> call(String e) throws Exception {
				return Arrays.asList(e.substring(e.indexOf(" - ") + 3, e.indexOf(" - ") + 18).replaceAll("[a-zA-Z\\s\"\\/-]", "")).iterator();
			}
			
		}).filter(new Function<String, Boolean>() { 

			@Override
			public Boolean call(String e) throws Exception {
				return e.matches("([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})") ? Boolean.TRUE : Boolean.FALSE;
			}
			
		});

		// IP별 카운트
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
			params.put("COUNT_IP_RESULT", counts.collect());
		}

		context.getContext().stop();
		context.getContext().close();
	}

	@SuppressWarnings("unchecked")
	private void checkIPCount(Map<String, Object> params) throws SQLException {

		List<Tuple2<String, Integer>> countIPList = (List<Tuple2<String, Integer>>) params.get("COUNT_IP_RESULT");

		if (countIPList != null) {

			CountIPDao countIPDao = CountIPDao.getInstance();
			List<Map<String, Object>> alarmTargetList = countIPDao.checkCountIP(params);

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

		for (Map<String, Object> map : (List<Map<String, Object>>) params.get("ALARM_TARGET_LIST")) {

			String[] mdnArray = String.valueOf(map.get("mdn_array")).split(",");
			params.put("CONTENT", String.valueOf(map.get("alarm_content")));
			params.put("TITLE", String.valueOf(map.get("alarm_name")));

			for (int i = 0; i < mdnArray.length; i++) {
				params.put("RECEIVE_MDN", mdnArray[i]);
				smsDao.insertSms(params);
			}

		}

	}

}
