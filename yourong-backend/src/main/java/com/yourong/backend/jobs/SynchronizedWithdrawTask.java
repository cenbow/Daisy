package com.yourong.backend.jobs;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.yourong.backend.fin.service.WithdrawLogService;
/**
 * 同步提现定时任务
 * @author Administrator
 *
 */
public class SynchronizedWithdrawTask {

	private static final Logger logger = LoggerFactory.getLogger(SynchronizedWithdrawTask.class);

	@Autowired
	private WithdrawLogService withdrawLogService;
	
	@Resource
	private ThreadPoolTaskExecutor taskExecutor;

	public void work(){
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				logger.info("同步提现状态task start");
				withdrawLogService.synchronizedWithdraw();
				logger.info("同步提现状态task end");
			}
		});

	}
}
