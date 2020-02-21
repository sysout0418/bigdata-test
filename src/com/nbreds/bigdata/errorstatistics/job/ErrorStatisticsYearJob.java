package com.nbreds.bigdata.errorstatistics.job;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.nbreds.bigdata.common.CommonConstants;
import com.nbreds.bigdata.errorstatistics.dao.ErrorStatisticsDao;
import com.nbreds.bigdata.errorstatistics.vo.ErrorStatisticsVo;

public class ErrorStatisticsYearJob implements Job {

	protected static final Logger logger = Logger.getLogger(ErrorStatisticsYearJob.class);

	public static void main(String[] args) {
		System.setProperty("SERVER_TYPE", "dev");
		ErrorStatisticsYearJob job = new ErrorStatisticsYearJob();
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
		if (CommonConstants.errorStatisticsYearIsRunning) {
			logger.info("Process Still Running....");
			CommonConstants.errorStatisticsYearRunCheckCount++;
			/**
			 * 싱글톤 방식이나 설정된 최대 스킵 카운트 이상으로 동작하지 못했을경우 프로세스이상으로 간주하고 새로이 시작
			 */
			if (CommonConstants.errorStatisticsYearRunCheckCount > CommonConstants.maxRunCheckCount) {
				CommonConstants.errorStatisticsYearRunCheckCount = 0;
			} else {
				return;
			}
		} else {
			CommonConstants.errorStatisticsYearIsRunning = true;
		}

		try {
			ErrorStatisticsDao dao = ErrorStatisticsDao.getInstance();
			ErrorStatisticsVo errorStatVo = new ErrorStatisticsVo();
			errorStatVo.setStat_type("Y");
			
			dao.insertErrorStatistics(errorStatVo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CommonConstants.errorStatisticsYearIsRunning = false;
		}
	}

}
