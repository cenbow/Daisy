package com.yourong.backend.jobs;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.yourong.common.constant.ActivityConstant;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.util.Collections3;
import com.yourong.core.mc.manager.ActivityLotteryResultManager;
import com.yourong.core.mc.manager.ActivityManager;
import com.yourong.core.mc.model.Activity;
public class PlayOlympicTask {
	
	private static final Logger logger = LoggerFactory.getLogger(PlayOlympicTask.class);
	@Autowired
	private ActivityManager activityManager;
	@Resource
	private ThreadPoolTaskExecutor taskExecutor;
	@Autowired
	private ActivityLotteryResultManager activityLotteryResultManager;
	
	public void work() {
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				logger.info("玩转奥运猜奥运送现金券和人气值定时任务 start");
				try {
					Activity activity = new Activity();
					activity.setActivityName(ActivityConstant.ACTIVITY_PLAY_OLYMPIC);
					List<Activity> actList = activityManager.getActivityBySelective(activity);
					if (Collections3.isNotEmpty(actList)) {
						Activity ac = actList.get(0);
						if(ac.getActivityStatus()==StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()){
							//activityLotteryResultManager.guessOlympicSendCoupon();
						}
					}
				} catch (Exception e) {
					logger.error("玩转奥运猜奥运送现金券和人气值定时任务出现异常" , e);
				}
			}
		});
	}
}
