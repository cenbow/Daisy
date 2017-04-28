package com.yourong.backend.jobs;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.ic.manager.DebtInterestManager;

/**
 * @desc 产生逾期记录定时任务
 * @author fuyili 2016年6月1日上午10:34:18
 */
public class GenerateOverdueTask {

	private static final Logger logger = LoggerFactory.getLogger(GenerateOverdueTask.class);

	@Resource
	private ThreadPoolTaskExecutor taskExecutor;

	@Autowired
	private DebtInterestManager debtInterestManager;

	public void work() {
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					logger.info("产生逾期记录定时任务 start");
					generateGeneralOverdue();
					generateUnderWriteOverdue();
					logger.info("产生逾期记录定时任务 end");
				} catch (ManagerException e) {
					logger.error("产生逾期记录定时任务异常：", e);
				}
			}
		});
	}
	
	/**
	 * @Description:普通逾期
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2016年6月1日 上午11:43:17
	 */
	private void generateGeneralOverdue() throws ManagerException {
		debtInterestManager.generateGeneralOverdue();
	}

	/**
	 * @Description:垫资逾期
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2016年6月1日 上午11:45:35
	 */
	private void generateUnderWriteOverdue() throws ManagerException {
		debtInterestManager.generateUnderWriteOverdue();
	}

}
