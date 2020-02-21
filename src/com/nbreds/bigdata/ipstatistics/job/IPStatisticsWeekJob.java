package com.nbreds.bigdata.ipstatistics.job;

import java.util.Calendar;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.nbreds.bigdata.common.CommonConstants;
import com.nbreds.bigdata.common.util.CommonUtil;
import com.nbreds.bigdata.ipstatistics.dao.IPStatisticsDao;
import com.nbreds.bigdata.ipstatistics.vo.IPStatisticsVo;

public class IPStatisticsWeekJob implements Job {

	protected static final Logger logger = Logger.getLogger(IPStatisticsWeekJob.class);

//	public static void main(String[] args) {
//
//		IPStatisticsWeekJob job = new IPStatisticsWeekJob();
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
		
		if (CommonConstants.IPStatisticsWeekIsRunning) {
			
			logger.info("Process Still Running....");
			CommonConstants.IPStatisticsWeekRunCheckCount++;
			/**
			 * 싱글톤 방식이나 설정된 최대 스킵 카운트 이상으로 동작하지 못했을경우 프로세스이상으로 간주하고 새로 시작
			 */
			if (CommonConstants.IPStatisticsWeekRunCheckCount > CommonConstants.maxRunCheckCount) {
				CommonConstants.IPStatisticsWeekRunCheckCount = 0;
			} else {
				return;
			}
			
		} else {
			CommonConstants.IPStatisticsWeekIsRunning = true;
		}

		try {
			
			int specificWeek = CommonUtil.getSpecificWeek();
			String startDate = CommonUtil.getPreviousTime("yyyyMMdd", Calendar.DAY_OF_MONTH, -7);
			String endDate = CommonUtil.getPreviousTime("yyyyMMdd", Calendar.DAY_OF_MONTH, -1);
			
			IPStatisticsDao ipStatisticsDao = IPStatisticsDao.getInstance();
			IPStatisticsVo iPStatisticsVo = new IPStatisticsVo();
			iPStatisticsVo.setStat_type("W");
			iPStatisticsVo.setSpecific_week(specificWeek);
			iPStatisticsVo.setStart_date(startDate);
			iPStatisticsVo.setEnd_date(endDate);
			
			ipStatisticsDao.insertIPStatistics(iPStatisticsVo);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CommonConstants.IPStatisticsWeekIsRunning = false;
		}
		
	}

}
