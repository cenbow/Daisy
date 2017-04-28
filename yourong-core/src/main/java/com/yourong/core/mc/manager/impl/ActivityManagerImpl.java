package com.yourong.core.mc.manager.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import com.yourong.common.constant.ActivityConstant;
import com.yourong.common.constant.Constant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.DebtEnum;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.core.common.MessageClient;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.PopularityInOutLogManager;
import com.yourong.core.ic.manager.DebtManager;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.model.Debt;
import com.yourong.core.ic.model.Project;
import com.yourong.core.lottery.container.LotteryContainer;
import com.yourong.core.mc.dao.ActivityDataMapper;
import com.yourong.core.mc.dao.ActivityHistoryMapper;
import com.yourong.core.mc.dao.ActivityLotteryMapper;
import com.yourong.core.mc.dao.ActivityMapper;
import com.yourong.core.mc.manager.ActivityLotteryManager;
import com.yourong.core.mc.manager.ActivityManager;
import com.yourong.core.mc.manager.ActivityRuleManager;
import com.yourong.core.mc.manager.CouponManager;
import com.yourong.core.mc.manager.CouponTemplatePrintManager;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.mc.model.ActivityAfterEngineListenerObject;
import com.yourong.core.mc.model.ActivityData;
import com.yourong.core.mc.model.ActivityHistory;
import com.yourong.core.mc.model.ActivityLottery;
import com.yourong.core.mc.model.ActivityRule;
import com.yourong.core.mc.model.Coupon;
import com.yourong.core.mc.model.CouponTemplatePrint;
import com.yourong.core.mc.model.biz.ActivityBiz;
import com.yourong.core.mc.model.biz.ActivityForInvest;
import com.yourong.core.mc.model.biz.RedFridayRuleBiz;
import com.yourong.core.sales.SPGift;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.Transaction;

@Component
public class ActivityManagerImpl implements ActivityManager {
	@Autowired
	private ActivityMapper activityMapper;
	@Autowired
	private ActivityHistoryMapper activityHistoryMapper;
	@Autowired
	private CouponManager couponManager;
	@Autowired
	private BalanceManager balanceManager;
	@Autowired
	private PopularityInOutLogManager popularityInOutLogManager;
	@Autowired
	private ProjectManager projectManager;
	@Autowired
	private DebtManager debtManager;
	@Autowired
	private CouponTemplatePrintManager couponTemplatePrintManager;
	@Autowired
	private TransactionManager transactionManager;
	@Autowired
	private ActivityRuleManager activityRuleManager;
	@Autowired
	private ActivityLotteryManager activityLotteryManager;
	@Autowired
	private ActivityLotteryMapper activityLotteryMapper;
	@Autowired
	private EventBus eventBus;
	
	@Autowired
	private ActivityDataMapper activityDataMapper;

	private static Logger logger = LoggerFactory.getLogger(ActivityManagerImpl.class);

	public Integer deleteByPrimaryKey(Long id) throws ManagerException {
		try {
			int result = activityMapper.deleteByPrimaryKey(id);
			return result;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public Integer insert(Activity activity) throws ManagerException {
		try {
			int result = activityMapper.insert(activity);
			return result;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public Activity selectByPrimaryKey(Long id) throws ManagerException {
		try {
			return activityMapper.selectByPrimaryKey(id);

		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public Integer updateByPrimaryKey(Activity activity) throws ManagerException {
		try {

			return activityMapper.updateByPrimaryKey(activity);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public Integer updateByPrimaryKeySelective(Activity activity) throws ManagerException {
		try {

			return activityMapper.updateByPrimaryKeySelective(activity);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public Page<Activity> findByPage(Page<Activity> pageRequest, Map<String, Object> map) throws ManagerException {
		try {
			map.put(Constant.STARTROW, pageRequest.getiDisplayStart());
			map.put(Constant.PAGESIZE, pageRequest.getiDisplayLength());
			int totalCount = activityMapper.selectForPaginTotalCount(map);
			pageRequest.setiTotalDisplayRecords(totalCount);
			pageRequest.setiTotalRecords(totalCount);
			List<Activity> selectForPagin = activityMapper.selectForPagin(map);
			pageRequest.setData(selectForPagin);
			return pageRequest;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public void addActivity(Activity activity) throws ManagerException {
		try {
			activityMapper.insertSelective(activity);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<ActivityBiz> findInProgressActivity() throws ManagerException {
		try {
			return activityMapper.findInProgressActivity();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void sendGifts(Long activityId, String activityName, Long memberId, List<SPGift> giftList, String methodName) throws Exception {
		Long total = activityHistoryMapper.totalMemberByActivityId(activityId);
		// 记录
		ActivityHistory activityHistory = new ActivityHistory();
		activityHistory.setActivityId(activityId);
		activityHistory.setMemberId(memberId);
		int row = activityHistoryMapper.insert(activityHistory);
		if (row > 0) {
			for (SPGift gift : giftList) {
				switch (gift.getType()) {
				case "shouYiQuan":
					if (total < gift.getNumber() || gift.getNumber() <= 0) {// 如果为少于或等于零就不限上限
						Coupon coupon = couponManager.receiveCoupon(memberId, activityId, getTemplateId(gift.getTemplateId()), -1L);
						if (coupon == null) {
							logger.error("用户：" + memberId + "，参加活动[" + activityName + "],赠送收益券失败");
						}
					} else {
						logger.error("收益券已经赠送完毕。用户：" + memberId + "，无法获得活动[" + activityName + "]赠送的收益券");
					}
					break;
				case "xianJinQuan":
					if (total < gift.getNumber() || gift.getNumber() <= 0) {
						Coupon coupon = couponManager.receiveCoupon(memberId, activityId, getTemplateId(gift.getTemplateId()), -1L);
						if (coupon == null) {
							logger.error("用户：" + memberId + "，参加活动[" + activityName + "],赠送现金券失败");
						}
					} else {
						logger.error("现金券已经赠送完毕。用户：" + memberId + "，无法获得活动[" + activityName + "]赠送的现金券");
					}
					break;
				case "renQiZhi":
					if (total < gift.getNumber() || gift.getNumber() <= 0) {
						// 调用赠送人气值接口
						BigDecimal value = new BigDecimal(gift.getValue());
						// Balance balance =
						// balanceManager.increaseBalance(TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY,
						// value, memberId);
						// popularityInOutLogManager.insert(memberId,
						// TypeEnum.FIN_POPULARITY_TYPE_ACTIVITY,
						// value, null, balance.getAvailableBalance(),
						// gift.getTemplateId(), "【活动赠送】"+activityName);

						transactionManager.givePopularity(gift.getTemplateId(), memberId, TypeEnum.FIN_POPULARITY_TYPE_ACTIVITY, value,
								"活动赠送：" + activityName);

						// logger.info("用户："+memberId+"，参加活动["+activityName+"],赠送人气值:"+value);
					} else {
						logger.error("人气值已经赠送完毕。用户：" + memberId + "，无法获得活动[" + activityName + "]赠送的人气值");
					}
					break;
				case "qiTa":
					logger.info("用户：" + memberId + "，参加活动[" + activityName + "],可获得其它礼品");
					break;
				}
				if (gift.isSendMsgFlag()) {
					MessageClient.sendMsgForSPEngin(memberId, activityName, gift.getRewardName());
				}
			}
			if (activityName.indexOf("完善信息") > 0) {
				MessageClient.sendMsgForPerfectInformation(memberId);
			} else if (activityName.indexOf("绑定邮箱") > 0) {
				MessageClient.sendMsgForBindEmail(memberId);
			} else if (activityName.indexOf("手当其充") > 0) {
				MessageClient.sendMsgForAppFirstRecharge(memberId);
			} else if (activityName.indexOf("首次关注微信") > 0) {
				MessageClient.sendMsgForFollowWechat(memberId);
			} else if (activityName.indexOf("首次投资") > 0) {
				MessageClient.sendMsgForInvestProject(memberId);
			} else if (activityName.indexOf("首次体验APP") > 0) {
				MessageClient.sendMsgForUserApp(memberId);
			} else if (activityName.indexOf("新人抢红包送现金券") > -1) {
				MessageClient.sendMsgForSPEngin(memberId, ActivityConstant.ACTIVITY_REDPACKAGE_NAME,
						ActivityConstant.ACTIVITY_REDPACKAGE_REGISTER);
			}

			if ("isParticipateInActivity".equals(methodName)) {
				// 抽奖的发放以后更新抽奖剩余次数
				reduceLottery(memberId, activityId, activityId.toString());
			}
			// 活动后续事件处理
			ActivityAfterEngineListenerObject activityAfterEngineListenerObject = new ActivityAfterEngineListenerObject(memberId,
					activityId, activityName);
			eventBus.post(activityAfterEngineListenerObject);
		}
	}

	@Override
	public ActivityBiz findActivityById(Long activityId) throws ManagerException {
		try {
			return activityMapper.findActivityById(activityId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int autoStartActivityJob() throws ManagerException {
		try {
			return activityMapper.autoStartActivityJob();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int autoEndActivityJob() throws ManagerException {
		try {
			return activityMapper.autoEndActivityJob();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int reviewActivityById(Activity activity) throws ManagerException {
		try {
			return activityMapper.reviewActivityById(activity);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int submittedForReview(Long id) throws ManagerException {
		try {
			return activityMapper.submittedForReview(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 为了发布，模板编号变更
	 * 
	 * @param templateId
	 * @return
	 */
	private Long getTemplateId(Long templateId) {
		if (DateUtils.getCurrentDate().getTime() <= DateUtils.getDateTimeFromString("2015-06-30 23:59:59").getTime()) {
			if (templateId.longValue() == 69) {// 首次完善信息送10元现金券
				return 14L;
			} else if (templateId.longValue() == 70) {// 首次绑定邮箱送10元现金券
				return 13L;
			} else if (templateId.longValue() == 71) {// 注册送30元现金券
				return 60L;
			}
		}
		return templateId;
	}

	@Override
	public List<ActivityBiz> showNotFinishActivityList() throws ManagerException {
		try {
			return activityMapper.showNotFinishActivityList();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public void investHouseReceiveCoupon(Transaction model) {
		try {
			String activityIdStr = PropertiesUtil.getProperties("activity.investHouseHappy.activityId");
			Activity ac = selectByPrimaryKey(Long.parseLong(activityIdStr));
			if (ac != null && ac.getActivityStatus() == 4
					&& DateUtils.isDateBetween(DateUtils.getCurrentDate(), ac.getStartTime(), ac.getEndTime())) {
				Project p = projectManager.selectByPrimaryKey(model.getProjectId());
				Debt d = debtManager.selectByPrimaryKey(p.getDebtId());
				if (DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_HOUSE.getCode().equals(d.getGuarantyType())
						|| DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_HOUSERECORD.getCode().equals(d.getGuarantyType())) {
					// 判断是否已经领用现金券
					String templateId = PropertiesUtil.getProperties("activity.investHouseHappy.couponTemplateId");
					List<Coupon> checkCoupon = couponManager.getCouponByMemberIdAndCouponTemplateId(model.getMemberId(),
							Long.parseLong(templateId));
					if (Collections3.isEmpty(checkCoupon)) {
						String investAmountLimitStr = PropertiesUtil.getProperties("activity.investHouseHappy.investAmountLimit");
						BigDecimal investAmountLimit = new BigDecimal(investAmountLimitStr);
						ActivityForInvest param = new ActivityForInvest();
						param.setStartTime(ac.getStartTime());
						param.setEndTime(ac.getEndTime());
						param.setQueryLimit(1);
						param.setGuarantyType(new String[] { "house", "houseRecord" });
						param.setMemberId(model.getMemberId());
						List<Transaction> retList = transactionManager.getInvestHouseList(param);
						if (Collections3.isNotEmpty(retList) && retList.get(0).getInvestAmount().compareTo(investAmountLimit) > -1) {
							String couponTemplateId = PropertiesUtil.getProperties("activity.investHouseHappy.couponTemplateId");
							Long cti = Long.parseLong(couponTemplateId);
							CouponTemplatePrint ctp = couponTemplatePrintManager.selectByTemplateId(cti);
							int printNum = ctp.getPrintNum();
							int receivedNum = ctp.getReceivedNum() == null ? 0 : ctp.getReceivedNum();
							if (printNum > receivedNum) {
								couponManager.receiveCoupon(model.getMemberId(), Long.parseLong(activityIdStr), cti, -1L);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			try {
				logger.error("投房有喜领取优惠券失败, memberId={}, transactionId={}, projectId={}", model.getMemberId(), model.getId(),
						model.getProjectId(), e);
			} catch (Exception ex) {
				logger.error("投房有喜领取优惠券失败, 非法入参", e);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public String reciveCoupon(Long memberId, Long activityId) throws ManagerException {
		// 根据活动id查找活动
		Activity activity = selectByPrimaryKey(activityId);
		if (activity == null) {
			return "-2";
		}
		// 判断活动是否已经开始
		if (DateUtils.getCurrentDate().before(activity.getStartTime())) {
			return "-1";
		}
		if (DateUtils.isDateBetween(DateUtils.getCurrentDate(), activity.getStartTime(), activity.getEndTime())) {
			ActivityRule activityRule = activityRuleManager.findRuleByActivityId(activityId);
			List<RedFridayRuleBiz> redFridayRuleBizList = JSONObject.parseArray(activityRule.getRuleParameter(), RedFridayRuleBiz.class);
			Long couponTemplateId = redFridayRuleBizList.get(0).getCouponTemplateId();
			// 判断用户本期是否已经领取
			List<Coupon> coupons = couponManager.getCouponByMemberIdAndCouponTemplateId(memberId, couponTemplateId);
			if (Collections3.isNotEmpty(coupons) && coupons.size() > 0) {
				return "0";
			}
			// 领取优惠券
			Coupon coupon = couponManager.receiveCoupon(memberId, activityId, couponTemplateId, -1L);
			if (coupon != null) {
				ActivityHistory activityHistory = new ActivityHistory();
				activityHistory.setActivityId(activityId);
				activityHistory.setMemberId(memberId);
				activityHistoryMapper.insert(activityHistory);
				return "1";
			} else {
				return "2";
			}
		} else {
			return "3";
		}
	}

	@Override
	public ImmutableList<Activity> selectByParentId(Long parentId, int childGroup) throws ManagerException {
		try {
			List<Activity> list = activityMapper.selectByParentId(parentId, childGroup);
			return ImmutableList.copyOf(list);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	private void reduceLottery(Long memberId, Long activityId, String cycleStr) throws Exception {
		// 校验是否是抽奖
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("memberId", memberId);
		paraMap.put("activityId", activityId);
		paraMap.put("cycleConstraint", cycleStr);
		paraMap.put("realCount", 1);
		ActivityLottery al = activityLotteryMapper.selectByMemberActivity(paraMap);
		if (al != null) {
			activityLotteryManager.updateRealCount(activityId, memberId, cycleStr);
		}
	}

	@Override
	public Page<ActivityBiz> showCustomActivityPages(Page<ActivityBiz> pageRequest, Map<String, Object> map) throws ManagerException {
		try {
			return activityMapper.showCustomActivityPages(pageRequest, map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public void saveRule(ActivityBiz activityBiz) throws ManagerException {
		try {
			Activity ac = new Activity();
			ac.setId(activityBiz.getId());
			ac.setStartTime(activityBiz.getStartTime());
			ac.setEndTime(activityBiz.getEndTime());
			// 刷新活动状态
			LotteryContainer.getInstance().getActivityStatusFromTime(ac);
			activityMapper.updateByPrimaryKeySelective(ac);
			ActivityRule ar = new ActivityRule();
			ar.setActivityId(activityBiz.getId());
			ar.setRuleParameter(activityBiz.getRuleParameterJson());
			activityRuleManager.updateRuleByActivityId(ar);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<Activity> getActivityBySelective(Activity activity) throws ManagerException {
		try {
			return activityMapper.getActivityBySelective(activity);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<ActivityBiz> findInProgressActivityByBizType(ActivityBiz activityBiz) throws ManagerException {
		try {
			return activityMapper.findInProgressActivityByBizType(activityBiz);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Page<ActivityData> showActivityDataPages(Page<ActivityData> pageRequest, Map<String, Object> map) throws ManagerException {
		try {
			return activityDataMapper.showActivityDataPages(pageRequest, map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public  ResultDO<ActivityData> updateByPrimaryKeySelective(ActivityData activityData) {
		 ResultDO<ActivityData> result = new ResultDO<ActivityData>();
		int i=activityDataMapper.updateByPrimaryKeySelective(activityData);
		if(i>0){
			result.setSuccess(true);
		}
		return result;
	}

	@Override
	public  ResultDO<ActivityData> insertSelective(ActivityData activityData) {
		ResultDO<ActivityData> result = new ResultDO<ActivityData>();
		activityData.setDelFlag(1);
		activityData.setRemark(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate()));
		/*Optional<Activity> olympicActivity = LotteryContainer.getInstance()
				.getActivityByName(ActivityConstant.ACTIVITY_PLAY_OLYMPIC);
		if (!olympicActivity.isPresent()) {
			result.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
			return result;
		}
		Activity activity=olympicActivity.get();
		activityData.setActivityId(activity.getId());
		int j=activityDataMapper.countActivityDateByActivityId(activity.getId(),DateUtils.getDateStrFromDate(DateUtils.getCurrentDate()));
		if(j>0){
			result.setResultCode(ResultCode.ACTIVITY_PLAYOLYMPIC_CURRENT_HADADD_ERROR);
			return result;
		}*/
		int i=activityDataMapper.insertSelective(activityData);
		if(i>0){
			result.setSuccess(true);
		}
		return result;
	}

	@Override
	public ActivityData selectDataByPrimaryKey(long id) throws ManagerException {
		try {
			return activityDataMapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Activity getNewActivityByActivityName(String activityName) throws ManagerException {
		try {
			return activityMapper.getNewActivityByActivityName(activityName);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	

}