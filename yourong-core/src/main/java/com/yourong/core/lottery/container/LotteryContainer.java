package com.yourong.core.lottery.container;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Optional;
import com.yourong.common.cache.RedisManager;
import com.yourong.common.constant.ActivityConstant;
import com.yourong.common.constant.RedisConstant;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.SpringContextHolder;
import com.yourong.common.util.StringUtil;
import com.yourong.core.mc.manager.ActivityManager;
import com.yourong.core.mc.manager.ActivityRuleManager;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.mc.model.ActivityRule;
import com.yourong.core.mc.model.biz.ActivityBiz;

/**
 * 抽奖容器类
 * 
 * @author wangyanji
 *
 */
public class LotteryContainer {

	private static final Logger logger = LoggerFactory.getLogger(LotteryContainer.class);
	private static ActivityManager activityManager = SpringContextHolder.getBean(ActivityManager.class);
	private static ActivityRuleManager activityRuleManager = SpringContextHolder.getBean(ActivityRuleManager.class);

	private LotteryContainer() {
	}

	private static class LotteryContainerHolder {
		private static final LotteryContainer instance = new LotteryContainer();
	}

	public static final LotteryContainer getInstance() {
		return LotteryContainerHolder.instance;
	}

	/**
	 * 获取活动基本信息
	 */
	public Optional<Activity> getActivity(Long activityId) throws Exception {
		try {
			if (activityId != null) {
				String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR + RedisConstant.REDIS_KEY_ACTIVITY_BASIC_INFO
						+ RedisConstant.REDIS_SEPERATOR + activityId;
				boolean isExit = RedisManager.isExitByObjectKey(key);
				if (isExit) {
					Activity ac = (Activity) RedisManager.getObject(key);
					getActivityStatusFromTime(ac);
					return Optional.fromNullable(ac);
				} else {
					Activity act = activityManager.selectByPrimaryKey(activityId);
					if (act != null) {
						RedisManager.putObject(key, act);
						RedisManager.expireObject(key, ActivityConstant.activityKeyShortExpire);
						return Optional.of(act);
					}
				}
			}
			return Optional.absent();
		} catch (ManagerException e) {
			logger.error("获取活动基本信息失败, activityId={}", activityId, e);
			throw e;
		}
	}

	/**
	 * 获取活动基本信息
	 */
	public Optional<Activity> getActivityByName(String activityName) {
		try {
			if (StringUtil.isNotBlank(activityName)) {
				String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR + RedisConstant.REDIS_KEY_ACTIVITY_BASIC_INFO
						+ RedisConstant.REDIS_SEPERATOR + activityName;
				boolean isExit = RedisManager.isExitByObjectKey(key);
				if (isExit) {
					Activity ac = (Activity) RedisManager.getObject(key);
					getActivityStatusFromTime(ac);
					return Optional.of(ac);
				}
				Activity activity = new Activity();
				activity.setActivityName(activityName);
				List<Activity> actList = activityManager.getActivityBySelective(activity);
				if (Collections3.isNotEmpty(actList)) {
					Activity ac = actList.get(0);
					getActivityStatusFromTime(ac);
					RedisManager.putObject(key, ac);
					RedisManager.expireObject(key, ActivityConstant.activityKeyShortExpire);
					return Optional.of(ac);
				}
			}
		} catch (ManagerException e) {
			logger.error("获取活动基本信息失败, activityName={}", activityName, e);
		}
		return Optional.absent();
	}

	/**
	 * 获取指定活动的奖品序列
	 */
	@SuppressWarnings("unchecked")
	public Optional<List<Object>> getRewardsListMap(Long activityId, Class<?> className) throws Exception {
		try {
			if (activityId != null) {
				String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR
						+ RedisConstant.REDIS_KEY_ACTIVITY_LOTTERY_REWARDS + RedisConstant.REDIS_SEPERATOR + activityId;
				boolean isExit = RedisManager.isExitByObjectKey(key);
				if (isExit) {
					try {
						String jsonStr = (String) RedisManager.getObject(key);
						List<Object> list = (List<Object>) JSON.parseArray(jsonStr, className);
						if (Collections3.isNotEmpty(list)) {
							return Optional.of(list);
						}
					} catch (Exception e) {
						logger.error("读取抽奖奖品序列失败，刷新缓存！activityId={}", activityId);
					}
				}
				ActivityRule rule = activityRuleManager.findRuleByActivityId(activityId);
				String jsonStr = rule.getRuleParameter();
				if (StringUtil.isNotBlank(jsonStr)) {
					RedisManager.putObject(key, jsonStr);
					RedisManager.expireObject(key, ActivityConstant.activityKeyShortExpire);
					List<Object> list = (List<Object>) JSON.parseArray(jsonStr, className);
					if (Collections3.isNotEmpty(list)) {
						return Optional.of(list);
					}
				}
			}
			return Optional.absent();
		} catch (ManagerException e) {
			logger.error("获取活动基本信息失败, activityId={}", activityId, e);
			throw e;
		}
	}

	/**
	 * 
	 * @Description:根据活动时间返回状态，适用于读取活动缓存无法精确得到活动状态的场景
	 * @param activity
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月17日 上午10:20:12
	 */
	public Activity getActivityStatusFromTime(Activity activity) {
		if (DateUtils.getCurrentDate().before(activity.getStartTime())) {
			activity.setActivityStatus(StatusEnum.ACTIVITY_STATUS_IS_AUDIT.getStatus());
		} else if (DateUtils.getCurrentDate().after(activity.getEndTime())) {
			activity.setActivityStatus(StatusEnum.ACTIVITY_STATUS_IS_END.getStatus());
		} else {
			activity.setActivityStatus(StatusEnum.ACTIVITY_STATUS_IS_START.getStatus());
		}
		return activity;
	}

	/**
	 * 
	 * @Description:活动参加条件Json转Class(Cache)
	 * @param activity
	 * @param t
	 * @param cacheKey
	 * @return
	 * @author: wangyanji
	 * @time:2016年1月25日 下午2:20:30
	 */
	@SuppressWarnings("unchecked")
	public <T> T getObtainConditions(Activity activity, Class<T> t, String cacheKey) {
		T instance = null;
		String key = RedisConstant.REDIS_KEY_ACTIVITY_OBTAIN_CONDITIONS + cacheKey;
		boolean isExit = RedisManager.isExitByObjectKey(key);
		if (isExit) {
			try {
				instance = (T) RedisManager.getObject(key);
				return instance;
			} catch (Exception e) {
				logger.error("活动参加条件Json转Class失败！activityId={}", activity.getId());
			}
		}
		instance = JSON.parseObject(activity.getObtainConditionsJson(), t);
		if (instance != null) {
			RedisManager.putObject(key, instance);
			RedisManager.expireObject(key, ActivityConstant.activityKeyShortExpire);
			return instance;
		}
		return null;
	}

	/**
	 * 
	 * @Description:活动rule条件Json转Class(Cache)
	 * @param activity
	 * @param t
	 * @param cacheKey
	 * @return
	 * @author: wangyanji
	 * @time:2016年1月25日 下午2:20:30
	 */
	@SuppressWarnings("unchecked")
	public <T> T getRuleConditions(ActivityBiz activityBiz, Class<T> t, String cacheKey) {
		T instance = null;
		String key = RedisConstant.REDIS_KEY_ACTIVITY_RULE_CONDITIONS + cacheKey;
		boolean isExit = RedisManager.isExitByObjectKey(key);
		if (isExit) {
			try {
				instance = (T) RedisManager.getObject(key);
				return instance;
			} catch (Exception e) {
				logger.error("活动rule条件Json转Class(Cache)失败！activityId={}", activityBiz.getId());
			}
		}
		instance = JSON.parseObject(activityBiz.getRuleParameterJson(), t);
		if (instance != null) {
			RedisManager.putObject(key, instance);
			RedisManager.expireObject(key, ActivityConstant.activityKeyShortExpire);
			return instance;
		}
		return null;
	}
	
	
	@SuppressWarnings("unchecked")
	public <T> T getRuleConditionsByActivityId(Activity activity, Class<T> t) {
		T instance = null;
		String key = RedisConstant.REDIS_KEY_ACTIVITY_OBTAIN_CONDITIONS + activity.getProjectId();
		boolean isExit = RedisManager.isExitByObjectKey(key);
		if (isExit) {
			try {
				instance = (T) RedisManager.getObject(key);
				return instance;
			} catch (Exception e) {
				logger.error("活动参加条件Json转Class失败！activityId={}", activity.getId());
			}
		}
		instance = JSON.parseObject(activity.getObtainConditionsJson(), t);
		if (instance != null) {
			RedisManager.putObject(key, instance);
			RedisManager.expireObject(key, ActivityConstant.activityKeyShortExpire);
			return instance;
		}
		return null;
	}

}
