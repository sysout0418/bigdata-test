package com.nbreds.bigdata.ipstatistics.job;

import java.io.Serializable;
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
import com.nbreds.bigdata.ipstatistics.dao.IPStatisticsDao;
import com.nbreds.bigdata.ipstatistics.vo.IPStatisticsVo;

import scala.Tuple2;

public class IPStatisticsHourJob implements Job, Serializable {

	private static final long serialVersionUID = 1L;
	protected static final Logger logger = Logger.getLogger(IPStatisticsHourJob.class);

//	public static void main(String[] args) {
//
//		System.setProperty("SERVER_TYPE", "dev");
//		IPStatisticsHourJob job = new IPStatisticsHourJob();
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

		if (CommonConstants.IPStatisticsHourIsRunning) {

			logger.info("Process Still Running....");
			CommonConstants.IPStatisticsHourRunCheckCount++;
			/**
			 * 싱글톤 방식이나 설정된 최대 스킵 카운트 이상으로 동작하지 못했을경우 프로세스이상으로 간주하고 새로 시작
			 */
			if (CommonConstants.IPStatisticsHourRunCheckCount > CommonConstants.maxRunCheckCount) {
				CommonConstants.IPStatisticsHourRunCheckCount = 0;
			} else {
				return;
			}
		} else {
			CommonConstants.IPStatisticsHourIsRunning = true;
		}

		try {
			
			// 조회용 parameter
			Map<String, Object> params = new HashMap<String, Object>();

			// 조회조건 설정
			setParam(params);

			// Spark 연동하여 IP Count
			getIPCount(params);

			// DB IP 통계 테이블에 입력
			insertIPStatistics(params);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CommonConstants.IPStatisticsHourIsRunning = false;
		}

	}

	private void setParam(Map<String, Object> params) {

		// Time setting
		String date = CommonUtil.getDateTime("yyyy-MM-dd");
		String hour = CommonUtil.getPreviousTime("HH", Calendar.HOUR, -6);

		params.put("NOW_DATE", date);
		params.put("NOW_HOUR", hour);

	}

	@SuppressWarnings("serial")
	private void getIPCount(Map<String, Object> params) {

		SparkContext context = new SparkContext(this.getClass(), CommonConfig.getInstance().getString("jar.path"));

		JavaRDD<String> input = context.textFile("/logs/" + String.valueOf(params.get("NOW_DATE")) + "/" + String.valueOf(params.get("NOW_HOUR")) + "/SERVER-01.*.log");

		logger.info("input ===> " + input);

		JavaRDD<String> data = input.flatMap(new FlatMapFunction<String, String>() {

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
	private void insertIPStatistics(Map<String, Object> params) throws SQLException {

		List<Tuple2<String, Integer>> countIPList = (List<Tuple2<String, Integer>>) params.get("COUNT_IP_RESULT");

		if (countIPList != null) {

			IPStatisticsDao iPStatisticsDao = IPStatisticsDao.getInstance();
			IPStatisticsVo iPStatisticsVo = new IPStatisticsVo();
			
			for (Tuple2<String, Integer> tuple : countIPList) {

				iPStatisticsVo.setStat_type("T");
				iPStatisticsVo.setReg_date(String.valueOf(params.get("NOW_DATE")).replaceAll("-", "")+ String.valueOf(params.get("NOW_HOUR")));
				iPStatisticsVo.setIp_address(tuple._1());
				iPStatisticsVo.setService_type(CommonConfig.getInstance().getString("service.type"));
				iPStatisticsVo.setIp_count(tuple._2());
				iPStatisticsVo.setInserted_id("Master");
				
				iPStatisticsDao.insertIPStatistics(iPStatisticsVo);

			}

		}

	}

}
