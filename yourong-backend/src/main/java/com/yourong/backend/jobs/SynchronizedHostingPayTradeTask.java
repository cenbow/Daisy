package com.yourong.backend.jobs;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.yourong.backend.tc.service.TransactionService;


/**
 * 同步代付交易状态
 * @author Leon Ray
 * 2014年10月11日-上午10:24:27
 */
public class SynchronizedHostingPayTradeTask {
	@Resource
	private TransactionService transactionService;

	private static final Logger logger = LoggerFactory.getLogger(SynchronizedHostingPayTradeTask.class);

	@Resource
	private ThreadPoolTaskExecutor taskExecutor;

	public void work() {
		logger.info("同步批次代付交易状态定时任务执行 start");
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {

				logger.info("同步批次代付交易状态task start");
				transactionService.SynchronizedBatchHostingPayTrade();
				logger.info("同步批次代付交易状态task end");
				
				logger.info("同步代付交易状态task start");
				transactionService.SynchronizedHostingPayTrade();
				logger.info("同步代付交易状态task end");
			}
		});
		logger.info("同步批次代付交易状态定时任务执行 end");

	}
}
