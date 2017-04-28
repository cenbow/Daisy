package com.yourong.backend.jobs;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.cms.manager.CmsArticleManager;
/**
 * 发布文章定时任务
 * @author Administrator
 *
 */
public class PublishCmsArticleTask {
	private static final Logger logger = LoggerFactory.getLogger(PublishCmsArticleTask.class);
	
	@Autowired
	private CmsArticleManager cmsArticleManager;
	
	@Resource
	private ThreadPoolTaskExecutor taskExecutor;

	public void work(){
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				logger.info("发布文章定时任务 start");
				try {
					cmsArticleManager.updateArticlePubState();
				} catch (ManagerException e) {
					logger.error("发布文章定时任务出现异常", e);
				}
				logger.info("发布文章定时任务 end");
			}
		});
	}
}
