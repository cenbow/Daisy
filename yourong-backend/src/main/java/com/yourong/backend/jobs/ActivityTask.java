package com.yourong.backend.jobs;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.yourong.common.cache.RedisActivityClient;
import com.yourong.common.exception.ManagerException;
import com.yourong.core.mc.manager.ActivityManager;

public class ActivityTask {

	@Resource
	private ActivityManager activityManager;

	@Resource
	private ThreadPoolTaskExecutor taskExecutor;

	private static final Logger logger = LoggerFactory.getLogger(ActivityTask.class);
	
	public void work() {
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					autoStartActivityJob();
					autoEndActivityJob();
				} catch (Exception e) {
					logger.error("活动定时器执行出现异常");
				}
			}
		});
	}
	
	private void autoStartActivityJob() throws ManagerException{
		logger.info("【开始】定时把活动改为进行中状态");
		int num = activityManager.autoStartActivityJob();
		if(num > 0){
			logger.info("新活动上线："+num+"，清空线上活动缓存");
			RedisActivityClient.removeProgressActivity();
		}
		logger.info("【结束】定时把活动改为进行中状态数量："+num);
	}
	
	private void autoEndActivityJob() throws ManagerException{
		logger.info("【开始】定时把活动改为结束状态");
		int num = activityManager.autoEndActivityJob();
		if(num > 0){
			logger.info("活动下线："+num+"，清空线上活动缓存");
			RedisActivityClient.removeProgressActivity();
		}
		logger.info("【结束】定时把活动改为结束状态数量："+num);
		
	}
}
