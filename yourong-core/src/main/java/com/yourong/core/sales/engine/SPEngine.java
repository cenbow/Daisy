package com.yourong.core.sales.engine;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.alibaba.fastjson.JSONObject;
import com.yourong.common.cache.RedisActivityClient;
import com.yourong.common.cache.RedisManager;
import com.yourong.common.constant.RedisConstant;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.SpringContextHolder;
import com.yourong.core.mc.manager.ActivityAfterTransactionManager;
import com.yourong.core.mc.manager.ActivityHistoryManager;
import com.yourong.core.mc.manager.ActivityManager;
import com.yourong.core.mc.model.biz.ActivityBiz;
import com.yourong.core.sales.SPGift;
import com.yourong.core.sales.SPParameter;
import com.yourong.core.sales.SPRule;
import com.yourong.core.sales.SalesPromotionModel;
import com.yourong.core.sales.rule.SPRuleBase;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.Member;

/**
 * 促销引擎
 */
public class SPEngine {
	private static final Logger logger = LoggerFactory.getLogger(SPEngine.class);
	private static final SPEngine spEngine = new SPEngine();
	private static ActivityManager activityManager = SpringContextHolder.getBean(ActivityManager.class);
	private static MemberManager memberManager = SpringContextHolder.getBean(MemberManager.class);
	private static ActivityHistoryManager activityHistoryManager = SpringContextHolder.getBean(ActivityHistoryManager.class);
	private static ThreadPoolTaskExecutor taskExecutor = SpringContextHolder.getBean(ThreadPoolTaskExecutor.class);
	private static ActivityAfterTransactionManager activityAfterTransactionManager = SpringContextHolder
			.getBean(ActivityAfterTransactionManager.class);
	
	private SPEngine(){
	}
	
	public static SPEngine getSPEngineInstance(){
		return spEngine;
	}
	
	/**
	 * 活动引擎
	 * @param parameter 活动必要的参数(当前用户必须填写)
	 */
	public void run(final SPParameter parameter){
		final List<SalesPromotionModel> spm =  loadSalesPromotion();
		if(Collections3.isNotEmpty(spm)){
			taskExecutor.execute(new Runnable() {
				@Override
				public void run() {
					// 触发定制活动
					if (parameter.getBizType() != null && parameter.getBizType().intValue() > 0 && parameter.getBiz() != null) {
						excuteByBizType(parameter.getBizType(), parameter.getBiz());
					}
					// 活动引擎主程序
					execute(parameter, spm);
				}
			});
		}
	}
	
	/**
	 * 活动引擎执行
	 * @param parameter 活动必要的参数
	 * @param salesPromotion 进行中的活动
	 */
	private void execute(SPParameter parameter, List<SalesPromotionModel> salesPromotion){
		if(Collections3.isNotEmpty(salesPromotion) && parameter != null && parameter.getMemberId() != null){
			logger.info("用户:"+parameter.getMemberId()+"活动引擎");
			Long activityId = null;
			try {
				Member member = null;
				for(SalesPromotionModel m : salesPromotion){
					activityId = m.getId();
					//如果活动结束时间少于当前时间，则清空缓存且continue
					if(m.getEndTime().getTime() < DateUtils.getCurrentDate().getTime()){
						RedisActivityClient.removeProgressActivity();
						continue;
					}
					
					//检查用户是否已经参与活动
					logger.info("用户:"+parameter.getMemberId()+",是否参与活动："+m.getName());
					boolean isParticipateInActivity = isParticipateInActivity(parameter.getMemberId(), m.getId());

					//参加过的用户是否需要判断邀请他的用户是否满足活动
					
					//检查领取条件是否通过
					boolean isSuccess = true;
					
					member = memberManager.selectByPrimaryKey(parameter.getMemberId());
					//如果用户没参与活动
					if(!isParticipateInActivity){
						logger.info("用户:"+parameter.getMemberId()+",开始运行参与活动："+m.getName());
						parameter.setStartTime(m.getStartTime());
						parameter.setEndTime(m.getEndTime());
						parameter.setProjects(m.getProjects());
						parameter.setActivityId(m.getId());
						for(SPRule rule : m.getElements()){
							SPRuleBase spRuleBase = SPRuleFactory.createSalesRule(rule.getKey());
							parameter.setSpRuleMethod(rule.getValue());
							isSuccess = spRuleBase.execute(parameter);
							if(!isSuccess){
								break;
							}
						}
						if(isSuccess){
							//用户参与活动记录&发送活动礼包
							activityManager.sendGifts(m.getId(), m.getName(),
									parameter.getMemberId(), m.getGiftList(),
									parameter.getSpRuleMethod().get(0).getName());
						}
					}
				}
				//触发好友
				if(member !=null && member.getReferral() != null){
//					logger.info("检查用户"+member.getId()+"的好友"+member.getReferral()+"是否有满足条件的活动");
					parameter.setMemberId(member.getReferral());
					execute(parameter, salesPromotion);
				}
			} catch(DuplicateKeyException mysqlE) {
				logger.error("活动引擎执行过程出现异常, 重复参加活动！memberId={}, activityId={}", parameter.getMemberId(), activityId);
			} catch (Exception e) {
				logger.error("活动引擎执行过程出现异常", e);
			}
		}
	}
	
	/**
	 * 加载所有正在进行中的促销
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<SalesPromotionModel> loadSalesPromotion(){
		List<SalesPromotionModel> salesPromotionModelList= null;
		try {
			String key = RedisConstant.REDIS_ACTIVITY_LIST;
			if(!RedisManager.isExitByObjectKey(key)){
				List<ActivityBiz> activityBiz = activityManager.findInProgressActivity();
				if(Collections3.isNotEmpty(activityBiz)){
					salesPromotionModelList = new ArrayList<SalesPromotionModel>();
					for(ActivityBiz activity : activityBiz){
						SalesPromotionModel model = (SalesPromotionModel)JSONObject.parseObject(activity.getObtainConditionsJson(), SalesPromotionModel.class);
						List<SPGift> giftList = JSONObject.parseArray(activity.getRuleParameterJson(), SPGift.class);
						model.setName(activity.getName());
						model.setId(activity.getId());
						model.setStartTime(activity.getStartTime());
						model.setEndTime(activity.getEndTime());
						salesPromotionModelList.add(model);
						model.setGiftList(giftList);
					}
					RedisManager.putObject(key, salesPromotionModelList);
				}
			}else{
				salesPromotionModelList = (List<SalesPromotionModel>)RedisManager.getObject(key);
			}
		} catch (ManagerException e) {
			logger.error("加载促销活动过程出现异常", e);
		}
		return salesPromotionModelList;
	}
	
	/**
	 * 检查用户是否已经参与活动
	 * @param memberId
	 * @param activityId
	 * @return
	 * @throws ManagerException
	 */
	private boolean isParticipateInActivity(Long memberId, Long activityId) throws ManagerException{
		boolean flag = RedisActivityClient.isParticipateInActivity(activityId, memberId);
		if(!flag){
			flag = activityHistoryManager.isParticipateInActivity(memberId, activityId);
			if(flag){
				RedisActivityClient.setActivitiesMember(activityId, memberId);
			}
		}
		return flag;
	}
	
	/**
	 * 
	 * @Description:触发定制活动
	 * @param triggleType
	 * @return
	 * @author: wangyanji
	 * @time:2016年3月16日 下午5:04:11
	 */
	private boolean excuteByBizType(int bizType, Object biz) {
		try {
			ActivityBiz activityBiz = new ActivityBiz();
			activityBiz.setBizType(bizType);
			List<ActivityBiz> actList = activityManager.findInProgressActivityByBizType(activityBiz);
			if (Collections3.isEmpty(actList)) {
				return false;
			}
			if (bizType == TypeEnum.ACTIVITY_BIZ_TYPE_TRANSACTION.getType()) {
				Transaction tranasction = (Transaction) biz;
				// 交易后触发的活动
				for (ActivityBiz activity : actList) {
					logger.info("触发定制活动...activityAfterTransactionManager.{}()", activity.getRuleType());
					MethodUtils.invokeExactMethod(activityAfterTransactionManager, activity.getRuleType(), activity, tranasction);
				}
			}
			return true;
		} catch (Exception e) {
			logger.error("加载正在进行中的定制类活动 bizType={}", bizType, e);
			return false;
		}
	}

}
