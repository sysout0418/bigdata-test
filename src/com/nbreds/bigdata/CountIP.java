package com.nbreds.bigdata;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.nbreds.bigdata.countip.job.CountIPJob;

public class CountIP {

	/**********************
	 * <pre>
	 * 최초 실행 메서드
	 * </pre>
	 * 
	 * @param args
	 **********************/
	public static void main(String[] args) {
		
		System.setProperty("SERVER_TYPE", "dev");
		CountIP scheduler = new CountIP();
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
			this.countIPRun(scheduler);
			
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		
	}

	/**********************
	 * <pre>
	 * countIPRun
	 * </pre>
	 * 
	 * @param scheduler
	 **********************/
	private void countIPRun(Scheduler scheduler) throws SchedulerException {
		
		JobDetail jobDetail = JobBuilder.newJob(CountIPJob.class).withIdentity("countIPRun").build();
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity("countIPRun").withSchedule(CronScheduleBuilder.cronSchedule("0 */10 * * * ?")).build();

		scheduler.scheduleJob(jobDetail, trigger);
		
	}

}
