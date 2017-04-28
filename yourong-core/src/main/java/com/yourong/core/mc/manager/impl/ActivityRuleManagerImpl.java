package com.yourong.core.mc.manager.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.mc.dao.ActivityRuleMapper;
import com.yourong.core.mc.manager.ActivityRuleManager;
import com.yourong.core.mc.model.ActivityRule;

@Component
public class ActivityRuleManagerImpl implements ActivityRuleManager {
	@Autowired
	private ActivityRuleMapper activityRuleMapper;

	public Integer deleteByPrimaryKey(Long id) throws ManagerException {
		try {
			int result = activityRuleMapper.deleteByPrimaryKey(id);
			return result;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public Integer insert(ActivityRule activity) throws ManagerException {
		try {
			int result = activityRuleMapper.insert(activity);
			return result;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public ActivityRule selectByPrimaryKey(Long id) throws ManagerException {
		try {
			return activityRuleMapper.selectByPrimaryKey(id);

		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public Integer updateByPrimaryKey(ActivityRule activity) throws ManagerException {
		try {

			return activityRuleMapper.updateByPrimaryKey(activity);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public Integer updateByPrimaryKeySelective(ActivityRule activity) throws ManagerException {
		try {

			return activityRuleMapper.updateByPrimaryKeySelective(activity);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public Page<ActivityRule> findByPage(Page<ActivityRule> pageRequest, Map<String, Object> map) throws ManagerException {
		try {
			return activityRuleMapper.findByPage(pageRequest, map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 根据活动ID获取活动规则（一对一）
	 */
	@Override
	public ActivityRule findRuleByActivityId(Long activityId) throws ManagerException {
		try {
			return activityRuleMapper.findRuleByActivityId(activityId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public void addActivityRule(ActivityRule rule) throws ManagerException {
		try {
			activityRuleMapper.insertSelective(rule);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int deleteActivityRuleByActivityId(Long activityId) throws ManagerException {
		try {
			return activityRuleMapper.deleteActivityRuleByActivityId(activityId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateRuleByActivityId(ActivityRule activityRule) throws ManagerException {
		try {
			return activityRuleMapper.updateRuleByActivityId(activityRule);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

}