package com.nbreds.bigdata.sms.job;

import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.nbreds.bigdata.common.CommonConstants;
import com.nbreds.bigdata.sms.dao.SmsDao;
import com.nbreds.bigdata.sms.vo.SmsVo;

public class SmsJob implements Job {

	protected static final Logger logger = Logger.getLogger(SmsJob.class);
	
	public static void main(String[] args) {
		System.setProperty("SERVER_TYPE", "dev");
		SmsJob job = new SmsJob();
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
		if (CommonConstants.smsIsRunning) {
			logger.info("Process Still Running....");
			CommonConstants.smsRunCheckCount++;
			/**
			 * 싱글톤 방식이나 설정된 최대 스킵 카운트 이상으로 동작하지 못했을경우 프로세스이상으로 간주하고 새로 시작
			 */
			if (CommonConstants.smsRunCheckCount > CommonConstants.maxRunCheckCount) {
				CommonConstants.smsRunCheckCount = 0;
			} else {
				return;
			}
		} else {
			CommonConstants.smsIsRunning = true;
		}

		try {
			SmsDao dao = SmsDao.getInstance();
			List<SmsVo> smsList = dao.getSmsList();
			
			if (!smsList.isEmpty()) {
				logger.info("전송할 SMS 있음. 전송 시작");
//				RestClient client = new RestClient(CommonConfig.getInstance().getString("username"), CommonConfig.getInstance().getString("token"));
//				TMNewMessage m = client.getResource(TMNewMessage.class);
//				
//				for (SmsVo smsVo : smsList) {
//					m.setText("[" + smsVo.getTitle() + "]\n" + smsVo.getContent());
//					m.setPhones(Arrays.asList(smsVo.getRecevie_mdn().split(",")));
//					m.send();
//				}
//				logger.info("Message ID : " + m.getId());
				logger.info("전송할 SMS 있음. 전송 완료");
			} else {
				logger.info("전송할 SMS 없음.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CommonConstants.smsIsRunning = false;
		}
	}

}
