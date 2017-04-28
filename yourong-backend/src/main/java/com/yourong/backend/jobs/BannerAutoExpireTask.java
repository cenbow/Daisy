package com.yourong.backend.jobs;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.cms.manager.BannerManager;

/**
 * banner到期后删除的定时任务
 * @author Administrator
 *
 */
public class BannerAutoExpireTask {

	@Resource
	private BannerManager bannerManager;

	@Resource
	private ThreadPoolTaskExecutor taskExecutor;

	private static final Logger logger = LoggerFactory
			.getLogger(BannerAutoExpireTask.class);

	public void work() {
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					expireBannerTask();
				} catch (Exception e) {
					logger.error("banner到期后删除的定时任务异常：", e);
				}
			}
		});
	}

	/**
	 * banner到期后删除的定时任务
	 * @throws ManagerException
	 */
	private void expireBannerTask() throws ManagerException {
		int expireNum = bannerManager.expireBannerTask();
		logger.info("定时把banner设置为删除的数量：" + expireNum);
	}

}
