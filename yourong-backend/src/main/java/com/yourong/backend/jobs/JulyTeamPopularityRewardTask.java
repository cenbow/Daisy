package com.yourong.backend.jobs;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.yourong.common.constant.ActivityConstant;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.Collections3;
import com.yourong.core.mc.manager.ActivityGroupManager;
import com.yourong.core.mc.manager.ActivityManager;
import com.yourong.core.mc.model.Activity;
/**
 * 
 * @desc 七月战队 人气值奖励
 * @author chaisen
 * 2016年7月4日上午10:30:44	
 */
public class JulyTeamPopularityRewardTask {
	private static final Logger logger = LoggerFactory.getLogger(JulyTeamPopularityRewardTask.class);
	
	@Autowired
	private ActivityGroupManager activityGroupManager;
	
	@Autowired
	private ActivityManager activityManager;
	
	public void work() throws Exception{
		logger.info("统计战队投资总额奖励人气值定时任务 start");
		try {
			Activity activity = new Activity();
			activity.setActivityName(ActivityConstant.ACTIVITY_JULY_TEAM);
			List<Activity> actList = activityManager.getActivityBySelective(activity);
			if (Collections3.isNotEmpty(actList)) {
				Activity ac = actList.get(0);
				if(ac.getActivityStatus()==StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()){
					//activityGroupManager.totalTeamInvestAmount();
				}
			}
		} catch (ManagerException e) {
			logger.error("统计战队投资总额奖励人气值定时任务出现异常" , e);
		}
		logger.info("统计战队投资总额奖励人气值定时任务 end");
	}
}
