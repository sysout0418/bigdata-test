package com.nbreds.bigdata.errorstatistics.job;

import java.util.Calendar;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.nbreds.bigdata.common.CommonConstants;
import com.nbreds.bigdata.common.util.CommonUtil;
import com.nbreds.bigdata.errorstatistics.dao.ErrorStatisticsDao;
import com.nbreds.bigdata.errorstatistics.vo.ErrorStatisticsVo;

public class ErrorStatisticsWeekJob implements Job {

	protected static final Logger logger = Logger.getLogger(ErrorStatisticsWeekJob.class);

	public static void main(String[] args) {
		System.setProperty("SERVER_TYPE", "dev");
		ErrorStatisticsWeekJob job = new ErrorStatisticsWeekJob();
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
		if (CommonConstants.errorStatisticsWeekIsRunning) {
			logger.info("Process Still Running....");
			CommonConstants.errorStatisticsWeekRunCheckCount++;
			/**
			 * 싱글톤 방식이나 설정된 최대 스킵 카운트 이상으로 동작하지 못했을경우 프로세스이상으로 간주하고 새로 시작
			 */
			if (CommonConstants.errorStatisticsWeekRunCheckCount > CommonConstants.maxRunCheckCount) {
				CommonConstants.errorStatisticsWeekRunCheckCount = 0;
			} else {
				return;
			}
		} else {
			CommonConstants.errorStatisticsWeekIsRunning = true;
		}

		try {
			ErrorStatisticsDao dao = ErrorStatisticsDao.getInstance();
			int specificWeek = CommonUtil.getSpecificWeek();
			String startDate = CommonUtil.getPreviousTime("yyyyMMdd", Calendar.DAY_OF_MONTH, -7);
			String endDate = CommonUtil.getPreviousTime("yyyyMMdd", Calendar.DAY_OF_MONTH, -1);
			
			ErrorStatisticsVo errorStatVo = new ErrorStatisticsVo();
			errorStatVo.setStat_type("W");
			errorStatVo.setSpecific_week(specificWeek);
			errorStatVo.setStart_date(startDate);
			errorStatVo.setEnd_date(endDate);
			
			dao.insertErrorStatistics(errorStatVo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CommonConstants.errorStatisticsWeekIsRunning = false;
		}
	}
	

}
