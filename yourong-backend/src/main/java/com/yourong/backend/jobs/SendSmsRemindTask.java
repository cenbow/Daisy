package com.yourong.backend.jobs;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.yourong.core.sys.manager.SysDictManager;
/**
 * 余额不足短信提醒定时任务	
 * @author Administrator
 *
 */
public class SendSmsRemindTask {
	private static final Logger logger = LoggerFactory.getLogger(SendSmsRemindTask.class);
	
	@Autowired
	private SysDictManager sysDictManager;
	
	@Resource
	private ThreadPoolTaskExecutor taskExecutor;

	public void work(){
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				logger.info("余额不足短信提醒定时任务 start");
				try {
					sysDictManager.sendSmsRemind();
				} catch (Exception e) {
					logger.error("余额不足短信提醒定时任务出现异常", e);
				}
				logger.info("余额不足短信提醒定时任务 end");
			}
		});
	}
}
