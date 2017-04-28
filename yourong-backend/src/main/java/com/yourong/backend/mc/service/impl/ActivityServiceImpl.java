package com.yourong.backend.mc.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.yourong.backend.mc.service.ActivityService;
import com.yourong.common.cache.RedisActivityClient;
import com.yourong.common.cache.RedisManager;
import com.yourong.common.constant.ActivityConstant;
import com.yourong.common.constant.RedisConstant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.CryptHelper;
import com.yourong.core.mc.manager.ActivityAfterTransactionManager;
import com.yourong.core.mc.manager.ActivityManager;
import com.yourong.core.mc.manager.ActivityRuleManager;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.mc.model.ActivityData;
import com.yourong.core.mc.model.ActivityRule;
import com.yourong.core.mc.model.biz.ActivityBiz;
import com.yourong.core.sales.SPParameter;
import com.yourong.core.sales.engine.SPEngine;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.Transaction;

@Service
public class ActivityServiceImpl implements ActivityService {
	private static Logger logger = LoggerFactory.getLogger(ActivityServiceImpl.class);
	@Autowired
	private ActivityManager activityManager;
	@Autowired
	private ActivityRuleManager activityRuleManager;
	@Autowired
	private TransactionManager transactionManager;
	
	@Autowired
	private ActivityAfterTransactionManager activityAfterTransactionManager;

	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO addActivity(ActivityBiz activityBiz) throws ManagerException {
		ResultDO resultDO = new ResultDO<>();
		Activity activity = new Activity();
		activity.setActivityName(activityBiz.getName());
		activity.setStartTime(activityBiz.getStartTime());
		activity.setEndTime(activityBiz.getEndTime());
		activity.setObtainConditionsJson(activityBiz.getObtainConditionsJson());
		activity.setType(activityBiz.getType());
		activity.setActivityStatus(0);

		activityManager.addActivity(activity);

		ActivityRule activityRule = new ActivityRule();
		activityRule.setRuleParameter(activityBiz.getRuleParameterJson());
		activityRule.setActivityId(activity.getId());
		activityRule.setRuleType("salesPromotionRule");
		activityRule.setDescription(activityBiz.getName());
		activityRule.setVersion("1");
		activityRuleManager.addActivityRule(activityRule);
		// 添加到活动

		// 添加到活动Rule

		return resultDO;
	}

	@Override
	public ActivityBiz findActivityById(Long activityId) {
		try {
			ActivityBiz biz = activityManager.findActivityById(activityId);
			return biz;
		} catch (ManagerException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Page<Activity> findByPage(Page<Activity> pageRequest, Map<String, Object> map) {
		try {
			activityManager.findByPage(pageRequest, map);
		} catch (ManagerException e) {
			logger.error("findByPage", e);
		}
		return pageRequest;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO updateActivity(ActivityBiz activityBiz) throws ManagerException {
		ResultDO resultDO = new ResultDO<>();
		Activity activity = activityManager.selectByPrimaryKey(activityBiz.getId());
		if (activity != null) {
			activity.setActivityName(activityBiz.getName());
			activity.setStartTime(activityBiz.getStartTime());
			activity.setEndTime(activityBiz.getEndTime());
			activity.setObtainConditionsJson(activityBiz.getObtainConditionsJson());
			activity.setType(activityBiz.getType());
			activityManager.updateByPrimaryKeySelective(activity);

			ActivityRule activityRule = activityRuleManager.findRuleByActivityId(activityBiz.getId());
			activityRule.setRuleParameter(activityBiz.getRuleParameterJson());
			activityRule.setRuleType("salesPromotionRule");
			activityRule.setDescription(activityBiz.getName());
			activityRule.setVersion("2");
			activityRuleManager.updateByPrimaryKeySelective(activityRule);

			RedisActivityClient.removeProgressActivity();
		}
		return resultDO;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO deleteActivityById(Long activityId) throws ManagerException {
		ResultDO resultDO = new ResultDO<>();
		int dataRow = activityManager.deleteByPrimaryKey(activityId);
		if (dataRow > 0) {
			activityRuleManager.deleteActivityRuleByActivityId(activityId);
		}
		return resultDO;
	}

	@Override
	public ResultDO reviewActivityById(Long activityId, Long userId) throws ManagerException {
		ResultDO resultDO = new ResultDO<>();
		Activity activity = new Activity();
		activity.setId(activityId);
		activity.setAuditId(userId);

		activityManager.reviewActivityById(activity);
		return resultDO;
	}

	@Override
	public ResultDO submittedForReview(Long id) {
		ResultDO resultDO = new ResultDO<>();
		try {
			activityManager.submittedForReview(id);
		} catch (ManagerException e) {
			e.printStackTrace();
		}
		return resultDO;
	}

	@Override
	public Page<ActivityBiz> showCustomActivityPages(Page<ActivityBiz> pageRequest, Map<String, Object> map) {
		try {
			activityManager.showCustomActivityPages(pageRequest, map);
		} catch (ManagerException e) {
			logger.error("后台查询定制活动失败", e);
		}
		return pageRequest;
	}

	@Override
	public Object saveRule(ActivityBiz activityBiz) {
		ResultDO<Object> rDO = new ResultDO<Object>();
		try {
			activityManager.saveRule(activityBiz);
		} catch (ManagerException e) {
			logger.error("后台定制活动修改规则失败", e);
			rDO.setSuccess(false);
		}
		// 清除对应活动的缓存
		String basicInfo = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR + RedisConstant.REDIS_KEY_ACTIVITY_BASIC_INFO
				+ RedisConstant.REDIS_SEPERATOR + activityBiz.getId();
		RedisManager.removeObject(basicInfo);
		String rule = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR + RedisConstant.REDIS_KEY_ACTIVITY_LOTTERY_REWARDS
				+ RedisConstant.REDIS_SEPERATOR + activityBiz.getId();
		RedisManager.removeObject(rule);
		return rDO;
	}

	@Override
	public Object createRedPackage(Long transactionId) {
		ResultDO<Object> rDO = new ResultDO<Object>();
		try {
			Transaction transaction = transactionManager.selectTransactionById(transactionId);
			if (transaction == null) {
				rDO.setResultCode(ResultCode.TRANSACTION_NOT_EXIST_ERROR);
				return rDO;
			}
			SPParameter parameter = new SPParameter();
			parameter.setMemberId(transaction.getMemberId());
			parameter.setBizType(TypeEnum.ACTIVITY_BIZ_TYPE_TRANSACTION.getType());
			parameter.setBiz(transaction);
			SPEngine.getSPEngineInstance().run(parameter);
		} catch (Exception e) {
			logger.error("后台定制活动修改规则失败", e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}

	@Override
	public Page<ActivityData> showActivityDataPages(Page<ActivityData> pageRequest, Map<String, Object> map) {
		try {
			activityManager.showActivityDataPages(pageRequest, map);
		} catch (ManagerException e) {
			logger.error("findByPage", e);
		}
		return pageRequest;
	}

	@Override
	public  ResultDO<ActivityData> updateByPrimaryKeySelective(ActivityData activityData) {
    	try {
    		return activityManager.updateByPrimaryKeySelective(activityData );
		} catch (Exception e) {
			logger.error("更活动数据失败，activityData=" + activityData, e);
		}
        return null;
    }

	@Override
	public  ResultDO<ActivityData> insertSelective(ActivityData activityData) {
    	try {
    		return activityManager.insertSelective(activityData );
		} catch (Exception e) {
			logger.error("保存动数据失败，activityData=" + activityData, e);
		}
        return null;
    }

	@Override
	public ActivityData selectByPrimaryKey(long id) {
		try {
			return activityManager.selectDataByPrimaryKey(id);
		} catch (Exception e) {
			logger.error("id", e);
		}
		return null;
	}

	@Override
	public Object findTransactionId(String redBagCode) {
		ResultDO<Object> rDO = new ResultDO<Object>();
		try {
			// 红包解密
			String	decryptCode = CryptHelper.decryptByase(redBagCode);
			if (decryptCode == null) {
				rDO.setResultCode(ResultCode.TRANSACTION_NOT_EXIST_ERROR);
				return rDO;
			}
			String[] decryptArr = decryptCode.split(ActivityConstant.redBagCodeSplit);
			rDO.setResult(decryptArr[1]);
		} catch (Exception e) {
			logger.error("根据红包加密串查询交易号失败", e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}

	@Override
	public Object sendQuickDirectLottery(Long projectId) {
		ResultDO<Object> rDO = new ResultDO<Object>();
		try {
			Transaction transaction=transactionManager.selectLastTransactionByProject(projectId);
			if(transaction==null){
				rDO.setSuccess(false);
				return rDO;
			}
			activityAfterTransactionManager.directSendReward(transaction);
			rDO.setSuccess(true);
		} catch (Exception e) {
			logger.error("补发快投有奖失败", e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}
}
