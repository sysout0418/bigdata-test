package com.nbreds.bigdata;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.nbreds.bigdata.counterror.job.CountErrorJob;

public class CountError {

	/**********************
	 * <pre>
	 * 최초 실행 메서드
	 * </pre>
	 * 
	 * @param args
	 **********************/
	public static void main(String[] args) {
		System.setProperty("SERVER_TYPE", "dev");
		CountError scheduler = new CountError();
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
			this.countErrorRun(scheduler);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	
	/**********************
  	 * <pre>
  	 * countErrorRun
  	 * </pre>
  	 * @param scheduler
  	 **********************/
	private void countErrorRun(Scheduler scheduler) throws SchedulerException {
		JobDetail job = JobBuilder
    			.newJob(CountErrorJob.class)
    			.withIdentity("countErrorRun")
    			.build();
		
		Trigger trigger = TriggerBuilder.newTrigger()
    			.withIdentity("countErrorRun")
    			.withSchedule(CronScheduleBuilder.cronSchedule("0 */5 * * * ?"))
    			.build();
		
    	scheduler.scheduleJob(job, trigger);
	}

}
