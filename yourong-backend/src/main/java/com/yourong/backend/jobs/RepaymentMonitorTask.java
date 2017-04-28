package com.yourong.backend.jobs;

import javax.annotation.Resource;

import com.yourong.core.repayment.manager.SynchronizedHostingCollectTradeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yourong.backend.tc.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * 还本付息监控定时任务
 * @author Leon Ray
 * 2016年6月16日-下午1:23:58
 */
public class RepaymentMonitorTask {

	@Resource
	private TransactionService transactionService;

	private static final Logger LOGGER = LoggerFactory.getLogger(RepaymentMonitorTask.class);

	public void work() {
		LOGGER.info("还本付息监控定时任务task start");
		
		LOGGER.info("还本付息监控定时任务task end");

	}
}
