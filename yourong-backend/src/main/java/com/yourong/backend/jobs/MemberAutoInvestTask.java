package com.yourong.backend.jobs;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.yourong.backend.tc.service.OrderService;

/**
 * 
 * @Description 用户自动投标定时任务
 * @author luwenshan
 * @time 2016年8月18日
 *
 */
public class MemberAutoInvestTask {
	
	@Resource
	private OrderService orderService;
	
	@Resource
	private ThreadPoolTaskExecutor taskExecutor;

	private static final Logger logger = LoggerFactory.getLogger(MemberAutoInvestTask.class);
	
	public void work() {
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					logger.info("用户自动投标定时任务 start");
					orderService.schedueAutoIntest();
					logger.info("用户自动投标定时任务 end");
				} catch (Exception e) {
					logger.error("关闭订单定时任务异常", e);
				}
			}
		});
		
	}
	
}
