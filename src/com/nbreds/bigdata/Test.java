package com.nbreds.bigdata;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.nbreds.bigdata.test.job.TestJob;

public class Test {
	/**********************
	 * <pre>
	 * 최초 실행 메서드
	 * </pre>
	 * @param args
	 **********************/
	public static void main(String[] args) {
		Test scheduler = new Test();
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
			this.testRun(scheduler);
    	} catch (SchedulerException e) {
    		e.printStackTrace();
    	}
	}
	
	/**********************
  	 * <pre>
  	 * testRun
  	 * </pre>
  	 * @param scheduler
  	 **********************/
	private void testRun(Scheduler scheduler) throws SchedulerException {
		System.out.println("----------------------------testRun----------------------------");
		JobDataMap jobMap = new JobDataMap();
		jobMap.put("USER", "TEST");
		jobMap.put("GETNAME", "127.0.0.1");
		jobMap.put("GETURL", "hdfs://10.0.2.15:9000/logs/2018-03-28/*/SERVER-01.*.log");
    	JobDetail serviceMonitoringJob = JobBuilder
    			.newJob(TestJob.class)
    			.withIdentity("testRun")
    			.setJobData(jobMap)
    			.build();
    	
    	Trigger legacyMonitoringTrigger = TriggerBuilder.newTrigger()
    			.withIdentity("testRun")
    			.withSchedule(CronScheduleBuilder.cronSchedule("*/10 * * * * ?"))
    			.build();
    	scheduler.scheduleJob(serviceMonitoringJob, legacyMonitoringTrigger);
		
	}
}
