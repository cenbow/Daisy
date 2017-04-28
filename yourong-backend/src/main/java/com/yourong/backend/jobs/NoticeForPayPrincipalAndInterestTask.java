package com.yourong.backend.jobs;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.fin.manager.PayPrincipalInterestManager;

/**
 * 还本付息到期通知（距离到期日3天邮件通知、距离到期日1天短信通知）
 * 
 * @author fuyili 2015年1月28日下午2:07:42
 */
public class NoticeForPayPrincipalAndInterestTask {

	@Resource
	private PayPrincipalInterestManager payPrincipalInterestManager;

	@Resource
	private ThreadPoolTaskExecutor taskExecutor;

	private static final Logger logger = LoggerFactory.getLogger(CoponAutoExpireTask.class);

	public void work() {
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					logger.info("还本付息到期通知 start");
					senndMailAndSmsTask();
					logger.info("还本付息到期通知 end");
				} catch (ManagerException e) {
					logger.error("定时发送还本付息到期通知邮件异常：", e);
				}
			}
		});
	}

	/**
	 * 已过期优惠券
	 * 
	 * @throws ManagerException
	 */
	private void senndMailAndSmsTask() throws ManagerException {
		int mailProNum = payPrincipalInterestManager.sendMailThree2EndDateProject();
		logger.info("距离到期日3天需要邮件通知的还本付息债权项目个数：" + mailProNum);
		int mailProDirectNum = payPrincipalInterestManager.sendMailThree2EndDateDirectProject();
		logger.info("距离到期日3天需要邮件通知的还本付息直投项目个数：" + mailProDirectNum);
		int smsProNum = payPrincipalInterestManager.sendSmsOne2EndDateProject();
		logger.info("距离到期日1天需要短信通知的还本付息项目个数:" + smsProNum);
		int smsOneDirectNum = payPrincipalInterestManager.sendSmsOne2EndDateDirectProject();
		logger.info("直投项目还本付息距离到期日1天，需要短信通知的会员个数：" + smsOneDirectNum);
		int smsThreeDirectNum = payPrincipalInterestManager.sendSmsThree2EndDateDirectProject();
		logger.info("直投项目还本付息距离到期日3天，需要短信通知的会员个数：" + smsThreeDirectNum);
		
		
	}

}
