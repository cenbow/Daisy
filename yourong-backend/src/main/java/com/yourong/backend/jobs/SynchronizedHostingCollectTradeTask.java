package com.yourong.backend.jobs;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.yourong.backend.tc.service.TransactionService;


/**
 * 同步代收交易状态
 * @author Leon Ray
 * 2014年10月11日-上午10:24:27
 */
public class SynchronizedHostingCollectTradeTask {

	@Resource
	private TransactionService transactionService;

	@Resource
	private ThreadPoolTaskExecutor taskExecutor;

	private static final Logger LOGGER = LoggerFactory.getLogger(SynchronizedHostingCollectTradeTask.class);

	public void work() {
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				LOGGER.info("同步代收交易状态task start");
				transactionService.SynchronizedHostingCollectTrade();
				LOGGER.info("同步代收交易状态task end");
			}
		});

	}
}
