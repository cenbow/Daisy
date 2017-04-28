package com.yourong.backend.jobs;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 
 * @desc 定时任务集合
 * @author wangyanji 2016年7月19日上午11:12:37
 */
public class TaskContainer {

	private static final Logger logger = LoggerFactory.getLogger(TaskContainer.class);

	@Resource
	private ThreadPoolTaskExecutor taskExecutor;

	@Autowired
	private ProjectAutoStartTask projectAutoStartTask;

	@Autowired
	private CloseOrderTask closeOrderTask;
	
	@Autowired
	private CoponAutoExpireTask coponAutoExpireTask;
	
	@Autowired
	private SynchronizedHostingCollectTradeTask synchronizedHostingCollectTradeTask;

	@Autowired
	private BannerAutoExpireTask bannerAutoExpireTask;

	@Autowired
	private ActivityTask activityTask;

	@Autowired
	private CustomMessageTask customMessageTask;
	
	@Autowired
	private PaymentPlatformTask paymentPlatformTask;

	@Autowired
	private ProjectMonitorTask projectMonitorTask;
	
	@Autowired
	private ShelvesGoodsTask shelvesGoodsTask;

	public void workByMinute() {
		try {
			logger.info("按分钟执行定时任务集合 start...");
			// 定时发布项目
			projectAutoStartTask.work();
			// 定时关闭订单
			closeOrderTask.work();
			// 同步代收交易
			synchronizedHostingCollectTradeTask.work();
			// 定时banner过期
			bannerAutoExpireTask.work();
			// 定时活动上线&结束
			activityTask.work();
			// 定时处理消息 每分钟执行一次
			customMessageTask.work();
			// 定时刷新支付管理平台状态
			paymentPlatformTask.work();
			// 定时监控支付确认中的项目
			projectMonitorTask.work();
			//定时上下架商品
			shelvesGoodsTask.work();
			logger.info("按分钟执行定时任务集合 end");
		} catch (Exception e) {
			logger.error("按分钟执行定时任务集合异常：", e);
		}
	}

}
