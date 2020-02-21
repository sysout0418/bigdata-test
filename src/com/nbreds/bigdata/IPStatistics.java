package com.nbreds.bigdata;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nbreds.bigdata.ipstatistics.job.IPStatisticsDayJob;
import com.nbreds.bigdata.ipstatistics.job.IPStatisticsHourJob;
import com.nbreds.bigdata.ipstatistics.job.IPStatisticsMonthJob;
import com.nbreds.bigdata.ipstatistics.job.IPStatisticsWeekJob;
import com.nbreds.bigdata.ipstatistics.job.IPStatisticsYearJob;

public class IPStatistics {
	
	private static final Logger logger = LoggerFactory.getLogger(IPStatistics.class);
	
	/**********************
	 * <pre>
	 * 최초 실행 메서드
	 * </pre>
	 * 
	 * @param args
	 **********************/
	public static void main(String[] args) {
		
		logger.info("IPStatistics Start");
		
		System.setProperty("SERVER_TYPE", "dev");
		IPStatistics scheduler = new IPStatistics();
		scheduler.startScheduler();
		
	}

	/**********************
	 * <pre>
	 * scheduler 시작
	 * </pre>
	 **********************/
	private void startScheduler() {

		try {
			
			Scheduler scheduler = new StdSchedulerFactory().getScheduler();
			scheduler.start();
			
			// 시간 단위 IP 통계
			this.ipStatisticsHourRun(scheduler);
			
//			// 일 단위 IP 통계
			this.ipStatisticsDayRun(scheduler);
//			
//			// 주 단위 IP 통계
			this.ipStatisticsWeekRun(scheduler);
//			
//			// 월 단위 IP 통계
			this.ipStatisticsMonthRun(scheduler);
//			
//			// 년 단위 IP 통계
			this.ipStatisticsYearRun(scheduler);
			
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		
	}

	/**********************
	 * <pre>
	 * ipStatisticsHourRun
	 * 매 시간 정각 마다
	 * </pre>
	 * 
	 * @param scheduler
	 **********************/
	private void ipStatisticsHourRun(Scheduler scheduler) throws SchedulerException {

		System.out.println("----- ----- ----- ----- ipStatisticsTimeRun ----- ----- ----- -----");
		
		JobDetail jobDetail = JobBuilder.newJob(IPStatisticsHourJob.class).withIdentity("ipStatisticsHourRun").build();
		Trigger legacyMonitoringTrigger = TriggerBuilder.newTrigger().withIdentity("ipStatisticsHourRun").withSchedule(CronScheduleBuilder.cronSchedule("0 0 */1 * * ?")).build();
		
		scheduler.scheduleJob(jobDetail, legacyMonitoringTrigger);

	}
	
	
	/**********************
	 * <pre>
	 * ipStatisticsDayRun
	 * 매일 01시 마다
	 * </pre>
	 * 
	 * @param scheduler
	 **********************/
	private void ipStatisticsDayRun(Scheduler scheduler) throws SchedulerException {

		System.out.println("----- ----- ----- ----- ipStatisticsDayRun ----- ----- ----- -----");
		
		JobDetail jobDetail = JobBuilder.newJob(IPStatisticsDayJob.class).withIdentity("ipStatisticsDayRun").build();
		Trigger legacyMonitoringTrigger = TriggerBuilder.newTrigger().withIdentity("ipStatisticsDayRun").withSchedule(CronScheduleBuilder.cronSchedule("0 0 1 * * ?")).build();
		
		scheduler.scheduleJob(jobDetail, legacyMonitoringTrigger);

	}
	
	
	/**********************
	 * <pre>
	 * ipStatisticsWeekRun
	 * 매주 월요일 02시
	 * </pre>
	 * 
	 * @param scheduler
	 **********************/
	private void ipStatisticsWeekRun(Scheduler scheduler) throws SchedulerException {

		System.out.println("----- ----- ----- ----- ipStatisticsWeekRun ----- ----- ----- -----");
		
		JobDetail jobDetail = JobBuilder.newJob(IPStatisticsWeekJob.class).withIdentity("ipStatisticsWeekRun").build();
		Trigger legacyMonitoringTrigger = TriggerBuilder.newTrigger().withIdentity("ipStatisticsWeekRun").withSchedule(CronScheduleBuilder.cronSchedule("0 0 2 ? * 2")).build();
		
		scheduler.scheduleJob(jobDetail, legacyMonitoringTrigger);

	}
	
	
	/**********************
	 * <pre>
	 * ipStatisticsMonthRun
	 * 매달 1일 02시 마다
	 * </pre>
	 * 
	 * @param scheduler
	 **********************/
	private void ipStatisticsMonthRun(Scheduler scheduler) throws SchedulerException {

		System.out.println("----- ----- ----- ----- ipStatisticsMonthRun ----- ----- ----- -----");
		
		JobDetail jobDetail = JobBuilder.newJob(IPStatisticsMonthJob.class).withIdentity("ipStatisticsMonthRun").build();
		Trigger legacyMonitoringTrigger = TriggerBuilder.newTrigger().withIdentity("ipStatisticsMonthRun").withSchedule(CronScheduleBuilder.cronSchedule("0 0 2 1 * ?")).build();
		
		scheduler.scheduleJob(jobDetail, legacyMonitoringTrigger);

	}
	
	
	/**********************
	 * <pre>
	 * ipStatisticsYearRun
	 * 매년 1월 1일 03시 마다
	 * </pre>
	 * 
	 * @param scheduler
	 **********************/
	private void ipStatisticsYearRun(Scheduler scheduler) throws SchedulerException {

		System.out.println("----- ----- ----- ----- ipStatisticsYearRun ----- ----- ----- -----");
		
		JobDetail jobDetail = JobBuilder.newJob(IPStatisticsYearJob.class).withIdentity("ipStatisticsYearRun").build();
		Trigger legacyMonitoringTrigger = TriggerBuilder.newTrigger().withIdentity("ipStatisticsYearRun").withSchedule(CronScheduleBuilder.cronSchedule("0 0 3 1 1 ?")).build();
		
		scheduler.scheduleJob(jobDetail, legacyMonitoringTrigger);

	}
	
	
}
