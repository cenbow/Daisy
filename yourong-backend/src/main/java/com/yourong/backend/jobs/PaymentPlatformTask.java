package com.yourong.backend.jobs;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.yourong.backend.bsc.service.PaymentPlatformService;
import com.yourong.backend.ic.service.LvgouService;
import com.yourong.common.exception.ManagerException;

/**
 * 定时更新支付平台信息
 * 
 * @author wangyanji
 */
public class PaymentPlatformTask {

	@Resource
	private PaymentPlatformService paymentPlatformService;

	@Resource
	private LvgouService lvgouService;
	
	@Resource
	private ThreadPoolTaskExecutor taskExecutor;

	private static final Logger logger = LoggerFactory
			.getLogger(PaymentPlatformTask.class);

	public void work() {
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					logger.info("定时刷新支付平台状态 start");
					flushStatus();
					logger.info("定时刷新支付平台状态 end");
				} catch (Exception e) {
					logger.error("定时更新支付平台信息异常：", e);
				}
			}
		});
	}

	/**
	 * 定时更新支付平台信息
	 * 
	 * @author wangyanji
	 * @throws ManagerException
	 */
	private void flushStatus() throws Exception {
		paymentPlatformService.flushStatus();
	}

}
