package com.nbreds.bigdata.errorstatistics.job;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import com.nbreds.bigdata.errorstatistics.dao.ErrorStatisticsDao;
import com.nbreds.bigdata.errorstatistics.vo.ErrorStatisticsVo;

import scala.Tuple2;

public class ErrorStatisticsHourJob implements Job {

	protected static final Logger logger = Logger.getLogger(ErrorStatisticsHourJob.class);

	public static void main(String[] args) {
		
		System.setProperty("SERVER_TYPE", "dev");
		ErrorStatisticsHourJob job = new ErrorStatisticsHourJob();
		
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
		if (CommonConstants.errorStatisticsHourIsRunning) {
			logger.info("Process Still Running....");
			CommonConstants.errorStatisticsHourRunCheckCount++;
			/**
			 * 싱글톤 방식이나 설정된 최대 스킵 카운트 이상으로 동작하지 못했을경우 프로세스이상으로 간주하고 새로이 시작
			 */
			if (CommonConstants.errorStatisticsHourRunCheckCount > CommonConstants.maxRunCheckCount) {
				CommonConstants.errorStatisticsHourRunCheckCount = 0;
			} else {
				return;
			}
		} else {
			CommonConstants.errorStatisticsHourIsRunning = true;
		}

		try {
			// 조회용 parameter
			Map<String, Object> params = new HashMap<String, Object>();
			
			// 조회조건 설정
			setParam(params);
			
			// Spark 연동하여 countError
			getErrorCount(params);
			
			// DB error 통계 테이블에 입력
			insertErrorStatistics(params);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CommonConstants.errorStatisticsHourIsRunning = false;
		}
	}
	
	private void setParam(Map<String, Object> params) {
		// Time setting
		String date = CommonUtil.getDateTime("yyyy-MM-dd");
		String hour = CommonUtil.getPreviousTime("HH", Calendar.HOUR, -1);
		
		params.put("NOW_DATE", date);
		params.put("NOW_HOUR", hour);
	}
	
	@SuppressWarnings("serial")
	private void getErrorCount(Map<String, Object> params) {
		
		SparkContext context = new SparkContext(this.getClass(), CommonConfig.getInstance().getString("jar.path"));
		
		JavaRDD<String> input = context.textFile(
				"/logs/" + String.valueOf(params.get("NOW_DATE"))
				+ "/" + String.valueOf(params.get("NOW_HOUR")) + "/SERVER-01.*.log");
		
		JavaRDD<String> data = input.filter(new Function<String, Boolean>() {
			
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
	private void insertErrorStatistics(Map<String, Object> params) throws SQLException {
		List<Tuple2<String, Integer>> countErrorList = (List<Tuple2<String, Integer>>) params.get("COUNT_ERROR_RESULT");
		
		if (countErrorList != null) {
			ErrorStatisticsDao dao = ErrorStatisticsDao.getInstance();
			ErrorStatisticsVo errorStatVo = new ErrorStatisticsVo();
			
			for (Tuple2<String, Integer> tuple : countErrorList) {
				errorStatVo.setStat_type("T");
				errorStatVo.setReg_date(String.valueOf(params.get("NOW_DATE")).replaceAll("-", "") + String.valueOf(params.get("NOW_HOUR")));
				errorStatVo.setError_code(tuple._1());
				errorStatVo.setError_count(tuple._2());
				
				dao.insertErrorStatistics(errorStatVo);
			}
		}
	}
	

}
