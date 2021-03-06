package com.nbreds.bigdata.ipstatistics.job;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.nbreds.bigdata.common.CommonConstants;
import com.nbreds.bigdata.ipstatistics.dao.IPStatisticsDao;
import com.nbreds.bigdata.ipstatistics.vo.IPStatisticsVo;

public class IPStatisticsMonthJob implements Job {

	protected static final Logger logger = Logger.getLogger(IPStatisticsMonthJob.class);

//	public static void main(String[] args) {
//
//		IPStatisticsMonthJob job = new IPStatisticsMonthJob();
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
		
		if (CommonConstants.IPStatisticsMonthIsRunning) {
			
			logger.info("Process Still Running....");
			CommonConstants.IPStatisticsMonthRunCheckCount++;
			/**
			 * 싱글톤 방식이나 설정된 최대 스킵 카운트 이상으로 동작하지 못했을경우 프로세스이상으로 간주하고 새로 시작
			 */
			if (CommonConstants.IPStatisticsMonthRunCheckCount > CommonConstants.maxRunCheckCount) {
				CommonConstants.IPStatisticsMonthRunCheckCount = 0;
			} else {
				return;
			}
			
		} else {
			CommonConstants.IPStatisticsMonthIsRunning = true;
		}

		try {
			
			IPStatisticsDao ipStatisticsDao = IPStatisticsDao.getInstance();
			IPStatisticsVo iPStatisticsVo = new IPStatisticsVo();
			iPStatisticsVo.setStat_type("M");
			
			ipStatisticsDao.insertIPStatistics(iPStatisticsVo);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CommonConstants.IPStatisticsMonthIsRunning = false;
		}
		
	}

}
