package com.nbreds.bigdata;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.nbreds.bigdata.scanerror.job.ScanErrorJob;

public class ScanError {
	/**********************
	 * <pre>
	 * 최초 실행 메서드
	 * </pre>
	 * @param args
	 **********************/
	public static void main(String[] args) {
		System.setProperty("SERVER_TYPE", "dev");
		ScanError scheduler = new ScanError();
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
			this.scanErrorRun(scheduler);
    	} catch (SchedulerException e) {
    		e.printStackTrace();
    	}
	}
	
	/**********************
  	 * <pre>
  	 * scanErrorRun
  	 * </pre>
  	 * @param scheduler
  	 **********************/
	private void scanErrorRun(Scheduler scheduler) throws SchedulerException {
		System.out.println("----------------------------scanErrorRun----------------------------");
    	JobDetail scanErrorJob = JobBuilder
    			.newJob(ScanErrorJob.class)
    			.withIdentity("scanErrorRun")
    			.build();
    	
    	Trigger legacyMonitoringTrigger = TriggerBuilder.newTrigger()
    			.withIdentity("scanErrorRun")
    			.withSchedule(CronScheduleBuilder.cronSchedule("*/10 * * * * ?"))
    			.build();
    	scheduler.scheduleJob(scanErrorJob, legacyMonitoringTrigger);
		
	}
}
