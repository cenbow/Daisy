package com.yourong.backend.jobs;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.yourong.common.exception.ManagerException;

public class DirectProjectLoanTask {
	
	@Resource
	private ThreadPoolTaskExecutor taskExecutor;

	private static final Logger logger = LoggerFactory.getLogger(DirectProjectLoanTask.class);
	
	public void work() {
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					logger.info("直投项目放款 task start");
					directProjectLoanTask();
					logger.info("直投项目放款  task end");
				} catch (ManagerException e) {
					logger.error("直投项目放款定时任务执行异常：", e);
				}
			}
		});
	}
	
	/**
	 * 把消息置为发布状态
	 * @throws ManagerException
	 */
	private void directProjectLoanTask() throws ManagerException{
		
		logger.info("直投项目放款");
	}
}
