package com.yourong.core.mc.manager;

import java.util.Map;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.mc.model.ActivityRule;

public interface ActivityRuleManager {
	Integer deleteByPrimaryKey(Long id) throws ManagerException;

	Integer insert(ActivityRule activityRule) throws ManagerException;

	ActivityRule selectByPrimaryKey(Long id) throws ManagerException;

	Integer updateByPrimaryKey(ActivityRule activityRule) throws ManagerException;

	Integer updateByPrimaryKeySelective(ActivityRule activityRule) throws ManagerException;

	Page<ActivityRule> findByPage(Page<ActivityRule> pageRequest, Map<String, Object> map) throws ManagerException;

	ActivityRule findRuleByActivityId(Long activityId) throws ManagerException;

	public void addActivityRule(ActivityRule rule) throws ManagerException;

	/**
	 * 根据活动ID删除活动规则
	 * 
	 * @param activityId
	 * @return
	 * @throws ManagerException
	 */
	public int deleteActivityRuleByActivityId(Long activityId) throws ManagerException;

	/**
	 * 
	 * @Description:根据活动ID修改规则
	 * @param activityId
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2015年12月8日 下午5:59:17
	 */
	public int updateRuleByActivityId(ActivityRule activityRule) throws ManagerException;
}