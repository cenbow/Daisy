package com.yourong.backend.jobs;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.msg.manager.CustomMessageManager;

/**
 * 消息定时任务处理
 * @author Administrator
 *
 */
public class CustomMessageTask {

	@Autowired
	private CustomMessageManager customMessageManager;
	
	@Resource
	private ThreadPoolTaskExecutor taskExecutor;

	private static final Logger logger = LoggerFactory.getLogger(CustomMessageTask.class);
	
	public void work() {
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					logger.info("消息 task start");
					updateMessageStatusTo4();
					logger.info("消息 task end");
				} catch (Exception e) {
					logger.error("项目相关定时任务执行异常：", e);
				}
			}
		});
	}
	
	/**
	 * 把消息置为发布状态
	 * @throws ManagerException
	 */
	private void updateMessageStatusTo4() throws ManagerException{
		int count = customMessageManager.updateMessageStatusTo4();
		logger.info("把消息置为发布状态数量："+count);
	}
}
