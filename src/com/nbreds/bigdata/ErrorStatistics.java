package com.nbreds.bigdata;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.nbreds.bigdata.errorstatistics.job.ErrorStatisticsDayJob;
import com.nbreds.bigdata.errorstatistics.job.ErrorStatisticsHourJob;
import com.nbreds.bigdata.errorstatistics.job.ErrorStatisticsMonthJob;
import com.nbreds.bigdata.errorstatistics.job.ErrorStatisticsWeekJob;
import com.nbreds.bigdata.errorstatistics.job.ErrorStatisticsYearJob;

public class ErrorStatistics {
	
	/**********************
	 * <pre>
	 * 최초 실행 메서드
	 * </pre>
	 * 
	 * @param args
	 **********************/
	public static void main(String[] args) {
		
		System.setProperty("SERVER_TYPE", "dev");
		ErrorStatistics scheduler = new ErrorStatistics();
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
			
			// 시간 단위 에러 통계
			this.errorStatisticsHourRun(scheduler);
			
			// 일 단위 에러 통계
			this.errorStatisticsDayRun(scheduler);
			
			// 주 단위 에러 통계
			this.errorStatisticsWeekRun(scheduler);
			
			// 월 단위 에러 통계
			this.errorStatisticsMonthRun(scheduler);
			
			// 년 단위 에러 통계
			this.errorStatisticsYearRun(scheduler);
		
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	/**********************
	 * <pre>
	 * errorStatisticsHourRun
	 * 매 시간 정각 마다
	 * </pre>
	 * 
	 * @param scheduler
	 **********************/
	private void errorStatisticsHourRun(Scheduler scheduler) throws SchedulerException {
		System.out.println("----------------------------errorStatisticsHourRun----------------------------");
		JobDetail job = JobBuilder
				.newJob(ErrorStatisticsHourJob.class)
				.withIdentity("errorStatisticsHourRun")
				.build();

		Trigger trigger = TriggerBuilder
				.newTrigger()
				.withIdentity("errorStatisticsHourRun")
				.withSchedule(CronScheduleBuilder.cronSchedule("0 0 */1 * * ?"))
				.build();

		scheduler.scheduleJob(job, trigger);
	}
	
	/**********************
	 * <pre>
	 * errorStatisticsDayRun
	 * 매일 01시 마다
	 * </pre>
	 * 
	 * @param scheduler
	 **********************/
	private void errorStatisticsDayRun(Scheduler scheduler) throws SchedulerException {
		System.out.println("----------------------------errorStatisticsDayRun----------------------------");
		JobDetail job = JobBuilder
				.newJob(ErrorStatisticsDayJob.class)
				.withIdentity("errorStatisticsDayRun")
				.build();

		Trigger trigger = TriggerBuilder
				.newTrigger()
				.withIdentity("errorStatisticsDayRun")
				.withSchedule(CronScheduleBuilder.cronSchedule("0 0 1 * * ?"))
				.build();

		scheduler.scheduleJob(job, trigger);
	}
	
	/**********************
	 * <pre>
	 * errorStatisticsWeekRun
	 * 매주 월요일 02시
	 * </pre>
	 * 
	 * @param scheduler
	 **********************/
	private void errorStatisticsWeekRun(Scheduler scheduler) throws SchedulerException {
		System.out.println("----------------------------errorStatisticsWeekRun----------------------------");
		JobDetail job = JobBuilder
				.newJob(ErrorStatisticsWeekJob.class)
				.withIdentity("errorStatisticsWeekRun")
				.build();

		Trigger trigger = TriggerBuilder
				.newTrigger()
				.withIdentity("errorStatisticsWeekRun")
				.withSchedule(CronScheduleBuilder.cronSchedule("0 0 2 ? * 2"))
				.build();

		scheduler.scheduleJob(job, trigger);
	}
	
	/**********************
	 * <pre>
	 * errorStatisticsMonthRun
	 * 매달 1일 02시 마다
	 * </pre>
	 * 
	 * @param scheduler
	 **********************/
	private void errorStatisticsMonthRun(Scheduler scheduler) throws SchedulerException {
		System.out.println("----------------------------errorStatisticsMonthRun----------------------------");
		JobDetail job = JobBuilder
				.newJob(ErrorStatisticsMonthJob.class)
				.withIdentity("errorStatisticsMonthRun")
				.build();

		Trigger trigger = TriggerBuilder
				.newTrigger()
				.withIdentity("errorStatisticsMonthRun")
				.withSchedule(CronScheduleBuilder.cronSchedule("0 0 2 1 * ?"))
				.build();

		scheduler.scheduleJob(job, trigger);
	}
	
	/**********************
	 * <pre>
	 * errorStatisticsYearRun
	 * 매년 1월 1일 03시 마다
	 * </pre>
	 * 
	 * @param scheduler
	 **********************/
	private void errorStatisticsYearRun(Scheduler scheduler) throws SchedulerException {
		System.out.println("----------------------------errorStatisticsYearRun----------------------------");
		JobDetail job = JobBuilder
				.newJob(ErrorStatisticsYearJob.class)
				.withIdentity("errorStatisticsYearRun")
				.build();

		Trigger trigger = TriggerBuilder
				.newTrigger()
				.withIdentity("errorStatisticsYearRun")
				.withSchedule(CronScheduleBuilder.cronSchedule("0 0 3 1 1 ?"))
				.build();

		scheduler.scheduleJob(job, trigger);
	}

}
