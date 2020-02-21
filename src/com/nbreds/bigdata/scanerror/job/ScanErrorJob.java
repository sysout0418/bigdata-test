package com.nbreds.bigdata.scanerror.job;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nbreds.bigdata.common.CommonConstants;
import com.nbreds.bigdata.common.dao.CommonCodeDao;
import com.nbreds.bigdata.common.util.CommonUtil;
import com.nbreds.bigdata.common.util.SparkContext;

public class ScanErrorJob implements Job{
	private static final Logger logger = LoggerFactory.getLogger(ScanErrorJob.class);
	
	private static int MAX_READ_FILE_TO_MINUTE = 5;

	@SuppressWarnings("unchecked")
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		String className = this.getClass().getName().substring(this.getClass().getName().lastIndexOf(".")+1, this.getClass().getName().length());
		logger.info(className+" Start");
		if(CommonConstants.scanErrorIsRunning) {
			logger.info("Process Still Running....");
			CommonConstants.scanErrorRunCheckCount++;
			/**
			 * 싱글톤 방식이나 설정된 최대 스킵 카운트 이상으로 동작하지 못했을경우 프로세스이상으로 간주하고 새로이 시작
			 */
			if(CommonConstants.scanErrorRunCheckCount > CommonConstants.maxRunCheckCount) {
				CommonConstants.scanErrorRunCheckCount = 0;
			}else {
				return;
			}
		}else {
			CommonConstants.scanErrorIsRunning = true;
		}

		try {
			System.setProperty("hadoop.home.dir", "A:/Workspace/batch");
			
			// Time setting
			String date = CommonUtil.getDateTime("yyyy-MM-dd");
			String hour = CommonUtil.getDateTime("HH");
			String minute = CommonUtil.getDateTime("mm");
			String saveMinute = minute;
			
			// 기본 테스트용 세팅
			// 실행시간 기준 5분 전까지 File read
			// Tomcat Access log
			JavaRDD<String>[] file = new JavaRDD[MAX_READ_FILE_TO_MINUTE];
			// WAS log
			JavaRDD<String>[] fileWas = new JavaRDD[MAX_READ_FILE_TO_MINUTE];

			SparkContext context = new SparkContext(this.getClass());
			
			
			// HDFS File read
			for(int i = 0 ; i < MAX_READ_FILE_TO_MINUTE ; i++) {
				logger.info("textFile read : " + "/logs/"+ date +"/"+ hour +"/SERVER-01-"+ minute +"*.log");
				file[i] = context.textFile("/"+ date +"/"+ hour +"/SERVER-01-"+ minute +"*.log");
				logger.info("textFile read : " + "/logs/"+ date +"/"+ hour +"/SERVER-01-WAS-"+ minute +"*.log");
				fileWas[i] = context.textFile("/logs/"+ date +"/"+ hour +"/SERVER-01-WAS-"+ minute +"*.log");
				
				
				if(Integer.parseInt(minute) <= 0) {
					minute = "59";
				}else {
					String tmpMin = String.valueOf(Integer.parseInt(minute) - 1);
					if(tmpMin.length() <= 1) {
						tmpMin = "0" + tmpMin;
					}
					minute = tmpMin;
				}
			}
			
			
			// Error Filtering
			List<String> errorList = new ArrayList<String>();
			List<String> errorWasList = new ArrayList<String>();

			JavaRDD<String> rowRDD = null;
			
			CommonUtil.setFilterValue("500");
			for(int i = 0 ; i < MAX_READ_FILE_TO_MINUTE ; i++) {
				try {
					file[i].first();
				}catch(Exception e) {
					if(e.getMessage().indexOf("matches 0 files") > 0) {
						logger.info("file is not exist");
						continue;
					}else {
						logger.info("Fatal Error");
						throw e;
					}
				}
				
				if(!file[i].isEmpty()) {
					rowRDD = file[i].filter(CommonUtil.sparkFilter);
				}

				if(rowRDD != null && !rowRDD.isEmpty()) {
					logger.info("500 Error Catched!!");
					errorList.addAll(rowRDD.collect());
					errorWasList.addAll(fileWas[i].collect());
				}
			}
			
			context.getContext().close();
			
			
			// Error log Save
			if(errorList.size() > 0) {
				CommonCodeDao commonDao = new CommonCodeDao();
				HashMap<String, Object> resultMap = commonDao.getSaveErrorLogConfig();
				
				if(resultMap == null || resultMap.size() <= 0) {
					logger.error("CommonConfig select error");
					return;
				}
				
				// accesslog
				String saveDir = String.valueOf(resultMap.get("ERROR_LOG_SAVE_DIR"));
				String fileAccessLogPrefix = String.valueOf(resultMap.get("ACCESSLOG_ERROR_PREFIX"));
				String fileAccessLogSuffix = String.valueOf(resultMap.get("ACCESSLOG_ERROR_SUFFIX"));
				
				// waslog
				String fileWasLogPrefix = String.valueOf(resultMap.get("WASLOG_ERROR_PREFIX"));
				String fileWasLogSuffix = String.valueOf(resultMap.get("WASLOG_ERROR_SUFFIX"));
				
				logger.info("Error log save (access log) : " + saveDir + fileAccessLogSuffix + "*" + fileAccessLogPrefix);
				logger.info("Error log save (was log) : " + saveDir + fileWasLogSuffix + "*" + fileWasLogPrefix);
				
				try {
					File saveLogFile = new File(saveDir + fileAccessLogSuffix + date + "." + hour + saveMinute + fileAccessLogPrefix);
					BufferedWriter bw = new BufferedWriter(new FileWriter(saveLogFile));
					
					File saveWasLogFile = new File(saveDir + fileWasLogSuffix + date + "." + hour + saveMinute + fileWasLogPrefix);
					BufferedWriter bwWas = new BufferedWriter(new FileWriter(saveWasLogFile));
					
					for(int i = 0 ; i < errorList.size() ; i++) {
						bw.write(errorList.get(i));
					}
					
					bw.close();

					for(int i = 0 ; i < errorWasList.size() ; i++) {
						bwWas.write(errorWasList.get(i));
					}
					
					bwWas.close();
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			
			logger.info("end");
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			CommonConstants.scanErrorIsRunning = false;
		}
		
	}
}
