package com.nbreds.bigdata;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.nbreds.bigdata.sms.job.SmsJob;

public class SmsScheduler {
	/**********************
	 * <pre>
	 * 최초 실행 메서드
	 * </pre>
	 * @param args
	 **********************/
	public static void main(String[] args) {
		System.setProperty("SERVER_TYPE", "dev");
		SmsScheduler scheduler = new SmsScheduler();
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
			this.smsRun(scheduler);
    	} catch (SchedulerException e) {
    		e.printStackTrace();
    	}
	}
	
	/**********************
  	 * <pre>
  	 * smsRun
  	 * </pre>
  	 * @param scheduler
  	 **********************/
	private void smsRun(Scheduler scheduler) throws SchedulerException {
		System.out.println("----------------------------smsRun----------------------------");
    	JobDetail job = JobBuilder
    			.newJob(SmsJob.class)
    			.withIdentity("smsRun")
    			.build();
    	
    	Trigger trigger = TriggerBuilder.newTrigger()
    			.withIdentity("smsRun")
    			.withSchedule(CronScheduleBuilder.cronSchedule("*/5 * * * * ?"))
    			.build();
    	
    	scheduler.scheduleJob(job, trigger);
	}
	
}
