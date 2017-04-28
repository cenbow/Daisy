package com.yourong.core.mc.manager.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yourong.core.mc.model.biz.*;
import com.yourong.core.uc.manager.MemberCheckManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import rop.thirdparty.com.google.common.collect.Lists;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.yourong.common.cache.RedisActivityClient;
import com.yourong.common.cache.RedisManager;
import com.yourong.common.constant.ActivityConstant;
import com.yourong.common.constant.Constant;
import com.yourong.common.constant.RedisConstant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ActivityEnum;
import com.yourong.common.enums.MessageEnum;
import com.yourong.common.enums.RemarksEnum;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.CryptHelper;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;
import com.yourong.common.util.LotteryUtil;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.common.util.RandomUtils;
import com.yourong.common.util.StringUtil;
import com.yourong.core.common.MessageClient;
import com.yourong.core.fin.dao.PopularityInOutLogMapper;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.PopularityInOutLogManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.ic.manager.ProjectExtraManager;
import com.yourong.core.lottery.container.LotteryContainer;
import com.yourong.core.lottery.draw.DrawByGroupProbability;
import com.yourong.core.lottery.draw.DrawByPopularityRedBagCreate;
import com.yourong.core.lottery.draw.DrawByPrizeDirectly;
import com.yourong.core.lottery.draw.DrawByProbability;
import com.yourong.core.lottery.model.PopularityRedBag;
import com.yourong.core.lottery.model.RewardsBase;
import com.yourong.core.lottery.model.RewardsBodyForProbility;
import com.yourong.core.lottery.model.RuleBody;
import com.yourong.core.lottery.validation.impl.VerificationByPopularity;
import com.yourong.core.mc.dao.ActivityDataMapper;
import com.yourong.core.mc.dao.ActivityGroupMapper;
import com.yourong.core.mc.dao.ActivityLotteryMapper;
import com.yourong.core.mc.dao.ActivityLotteryResultMapper;
import com.yourong.core.mc.dao.ActivityLotteryResultNewMapper;
import com.yourong.core.mc.manager.ActivityGroupManager;
import com.yourong.core.mc.manager.ActivityLotteryManager;
import com.yourong.core.mc.manager.ActivityLotteryPretreatManager;
import com.yourong.core.mc.manager.ActivityLotteryResultManager;
import com.yourong.core.mc.manager.ActivityManager;
import com.yourong.core.mc.manager.ActivityMessageManager;
import com.yourong.core.mc.manager.ActivityRuleManager;
import com.yourong.core.mc.manager.CouponManager;
import com.yourong.core.mc.manager.CouponTemplateManager;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.mc.model.ActivityData;
import com.yourong.core.mc.model.ActivityGroup;
import com.yourong.core.mc.model.ActivityLottery;
import com.yourong.core.mc.model.ActivityLotteryResult;
import com.yourong.core.mc.model.ActivityLotteryResultNew;
import com.yourong.core.mc.model.ActivityRule;
import com.yourong.core.mc.model.Coupon;
import com.yourong.core.mc.model.CouponTemplate;
import com.yourong.core.mc.model.query.ActivityLotteryResultQuery;
import com.yourong.core.tc.dao.TransactionMapper;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.biz.TransactionForActivity;
import com.yourong.core.tc.model.biz.TransactionForFirstInvestAct;
import com.yourong.core.tc.model.query.TransactionQuery;
import com.yourong.core.uc.dao.MemberCheckMapper;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberCheck;
import com.yourong.core.uc.model.query.MemberCheckQuery;
import rop.thirdparty.com.google.common.collect.Maps;

@Component
public class ActivityLotteryManagerImpl implements ActivityLotteryManager {

	private static final Logger logger = LoggerFactory.getLogger(ActivityLotteryManagerImpl.class);

	@Autowired
	private ActivityLotteryMapper activityLotteryMapper;
	
	@Autowired
	private ActivityLotteryResultMapper activityLotteryResultMapper;
	
	@Autowired
	private ActivityLotteryResultNewMapper activityLotteryResultNewMapper;

	@Autowired
	private ActivityRuleManager activityRuleManager;

	@Autowired
	private CouponManager couponManager;

	@Autowired
	private TransactionManager transactionManager;

	@Autowired
	private BalanceManager balanceManager;

	@Autowired
	private PopularityInOutLogManager popularityInOutLogManager;

	@Autowired
	private ActivityManager activityManager;

	@Autowired
	private DrawByGroupProbability drawByGroupProbability;

	@Autowired
	private DrawByPrizeDirectly drawByPrizeDirectly;

	@Autowired
	private DrawByProbability drawByProbability;

	@Autowired
	private ActivityMessageManager activityMessageManager;

	@Autowired
	private ProjectExtraManager projectExtraManager;

	@Autowired
	private ActivityLotteryPretreatManager activityLotteryPretreatManager;

	@Autowired
	private MemberManager memberManager;

	@Autowired
	private DrawByPopularityRedBagCreate drawByPopularityRedBagCreate;
	
	@Autowired
	private VerificationByPopularity verificationByPopularity;
	
	@Autowired
	private CouponTemplateManager couponTemplateManager;
	
	@Autowired
	private ActivityGroupManager activityGroupManager;
	
	@Autowired
	private ActivityLotteryResultManager activityLotteryResultManager;
	
	@Autowired
	private ActivityGroupMapper activityGroupMapper;
	
	@Autowired
	private ActivityDataMapper activityDataMapper;
	
	@Autowired
	private PopularityInOutLogMapper popularityInOutLogMapper;

	@Autowired
	private TransactionMapper transactionMapper;

	@Autowired
	private MemberCheckMapper memberCheckMapper;

	@Autowired
	private MemberCheckManager memberCheckManager;
	

	@Override
	public void addMemberLotteryNumber(Long activityId, Long memberId, int inputNum, String jsonCondition) throws ManagerException {
		try {
			if (inputNum > 0) {
				// 解析活动周期等约束条件
				ActivityLotteryBiz biz = resolveActivityLotteryRule(jsonCondition);
				String cycle = biz.getCycle();
				if ("all".equals(cycle)) {
					ActivityLottery model = new ActivityLottery();
					model.setActivityId(activityId);
					model.setMemberId(memberId);
					model.setTotalCount(inputNum);
					model.setRealCount(inputNum);
					model.setCycleConstraint(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate()));
					if (activityLotteryMapper.checkExistLottery(model) == null) {
						// 第一次获得抽奖机会
						activityLotteryMapper.insertSelective(model);
					} else {
						// 累积抽奖机会
						activityLotteryMapper.updateByActivityAndMember(model);
					}
				} else if ("day".equals(cycle)) {
					// 一天一抽
					ActivityLottery model = new ActivityLottery();
					model.setActivityId(activityId);
					model.setMemberId(memberId);
					model.setTotalCount(inputNum);
					model.setRealCount(inputNum);
					model.setCycleConstraint(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate()));
					activityLotteryMapper.insertSelective(model);
				}
			}
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int getMemberRealLotteryNumber(Long activityId, Long memberId, String cycleConstraint) throws ManagerException {
		try {
			Map<String, Object> paraMap = new HashMap<String, Object>();
			paraMap.put("memberId", memberId);
			paraMap.put("activityId", activityId);
			if (StringUtils.isNotEmpty(cycleConstraint)) {
				paraMap.put("cycleConstraint", cycleConstraint);
			}
			ActivityLottery model = activityLotteryMapper.selectByMemberActivity(paraMap);
			if (model == null) {
				return 0;
			} else {
				return model.getRealCount();
			}
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateRealCount(Long activityId, Long memberId, String cycleConstraint) throws ManagerException {
		try {
			ActivityLottery model = new ActivityLottery();
			model.setActivityId(activityId);
			model.setMemberId(memberId);
			model.setCycleConstraint(cycleConstraint);
			return activityLotteryMapper.updateRealCount(model);
		} catch (Exception e) {
			logger.error("抽奖SQL语句执行失败, activityId={}, memberId={}", activityId, memberId, e);
			return 0;
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ActivityLotteryBiz drawLotteryByProbability(Long activityId, Long memberId) throws ManagerException {
		try {
			// 先校验是否在活动有效期内
			ActivityBiz activityBiz = checkValidity(activityId);
			if (activityBiz.isValidityFlag()) {
				// 提取一次抽奖机会
				int num = drawRealCount(activityId, memberId, activityBiz.getObtainConditionsJson());
				if (num == 1) {
					// 成功扣除一次抽奖机会
					ActivityRule rule = activityRuleManager.findRuleByActivityId(activityId);
					String jsonStr = rule.getRuleParameter();
					// 抽奖
					String rewardCode = drawLotteryByProbabilityUtil(jsonStr);
					// 获取抽奖规则
					List<ActivityLotteryBiz> list = JSON.parseArray(jsonStr, ActivityLotteryBiz.class);
					Map map = Collections3.extractToMap(list, "rewardCode");
					ActivityLotteryBiz lotteryBiz = new ActivityLotteryBiz();
					BeanCopyUtil.copy(map.get(rewardCode), lotteryBiz);
					// 中奖结果
					logger.info("中奖结果:rewardCode={}, activityId={}, memberId={}", lotteryBiz.getRewardCode(), activityId, memberId);
					ActivityLotteryResult rewardLog = new ActivityLotteryResult();
					rewardLog.setActivityId(activityId);
					rewardLog.setMemberId(memberId);
					rewardLog.setRewardType(lotteryBiz.getRewardType());
					rewardLog.setRewardInfo(lotteryBiz.getRewardName());
					if (lotteryBiz.getRewardType() == TypeEnum.ACTIVITY_LOTTERY_TYPE_POPULARITY.getType()) {
						// 调用赠送人气值接口
						// 人气值
						// Balance balance =
						// balanceManager.increaseBalance(TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY,
						// new BigDecimal(lotteryBiz.getRewardValue()),
						// memberId);
						// // 记录人气值资金流水
						// popularityInOutLogManager.insert(memberId,
						// TypeEnum.FIN_POPULARITY_TYPE_ACTIVITY, new
						// BigDecimal(lotteryBiz.getRewardValue()), null,
						// balance.getAvailableBalance(), activityId,
						// activityBiz.getName() +
						// RemarksEnum.ACTIVITY_LOTTERY_FOR_POPULARITY.getRemarks()
						// );
						transactionManager.givePopularity(activityId, memberId, TypeEnum.FIN_POPULARITY_TYPE_ACTIVITY, new BigDecimal(
								lotteryBiz.getRewardValue()), RemarksEnum.ACTIVITY_LOTTERY_FOR_POPULARITY.getRemarks());
						rewardLog.setStatus(1);
					} else if (lotteryBiz.getRewardType() == TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType()
							|| lotteryBiz.getRewardType() == TypeEnum.ACTIVITY_LOTTERY_TYPE_ANNUALIZED.getType()) {
						// 收益券or现金券
						Coupon c = couponManager.receiveCoupon(memberId, activityId, Long.parseLong(lotteryBiz.getRewardId()), -1L);
						if (c != null) {
							// 优惠券赠送成功
							rewardLog.setStatus(1);
						} else {
							// 优惠券赠送失败;
							throw new NullPointerException("优惠券赠送失败，接口返回Coupon=null!");
						}
					} else if (lotteryBiz.getRewardType() == TypeEnum.ACTIVITY_LOTTERY_TYPE_GIFT.getType()) {
						// 礼品,标记未领取
						rewardLog.setStatus(0);
					} else if (lotteryBiz.getRewardType() == TypeEnum.ACTIVITY_LOTTERY_TYPE_OTHER.getType()) {
						// 其他,标记未领取
						rewardLog.setStatus(0);
					}
					String cycleStr = null;
					ActivityLotteryBiz biz = resolveActivityLotteryRule(activityBiz.getObtainConditionsJson());
					if ("day".equals(biz.getCycle())) {
						cycleStr = DateUtils.getDateStrFromDate(DateUtils.getCurrentDate());
						rewardLog.setCycleStr(cycleStr);
					}
					activityLotteryResultMapper.insertSelective(rewardLog);
					// 发送站内信
					MessageClient.sendMsgForYiRoad(memberId, lotteryBiz.getRewardName(), activityBiz.getName());
					// 隐藏抽奖概率
					lotteryBiz.setProbability(null);
					// 显示剩余抽奖次数
					lotteryBiz.setRealCount(getMemberRealLotteryNumber(activityId, memberId, cycleStr));
					return lotteryBiz;
				} else {
					// 返回剩余抽奖次数0
					ActivityLotteryBiz lotteryBiz = new ActivityLotteryBiz();
					lotteryBiz.setRealCount(0);
					return lotteryBiz;
				}
			} else {
				// 活动已过期
				ActivityLotteryBiz biz = new ActivityLotteryBiz();
				biz.setRewardCode("activityClosed");
				return biz;
			}
		} catch (Exception e) {
			logger.error("抽奖失败, activityId={}, memberId={}", activityId, memberId, e);
			throw new ManagerException(e);
		}
	}

	@Override
	public List<ActivityLotteryResult> queryNewLotteryResult(Long activityId, String remark, int rowNum) throws ManagerException {
		try {
			return activityLotteryResultMapper.queryNewLotteryResult(activityId, remark, rowNum);
		} catch (Exception e) {
			logger.error("查询最新中奖结果失败, activityId={}", activityId, e);
			throw new ManagerException(e);
		}
	}

	@Override
	public void yiRoadAddLotteryNumber(Long memberId, Long transactionId, BigDecimal investAmount) {
		try {
			Long activityId = Long.parseLong(PropertiesUtil.getProperties("activity.yiLotteryId"));
			ActivityBiz biz = checkValidity(activityId);
			if (biz.isValidityFlag()) {
				int addNumber = investAmount
						.divide(new BigDecimal(Long.parseLong(PropertiesUtil.getProperties("activity.yiLotteryAmount"))), 0,
								BigDecimal.ROUND_FLOOR).intValue();
				addMemberLotteryNumber(activityId, memberId, addNumber, biz.getObtainConditionsJson());
				logger.info("亿路上有你活动增加用户抽奖成功, memberId={}, transactionId={}, 获得次数{}", memberId, transactionId, addNumber);
			}
		} catch (Exception e) {
			logger.error("亿路上有你活动增加用户抽奖次数失败, memberId={}, transactionId={}", memberId, transactionId, e);
		}
	}

	@Override
	public ActivityBiz checkValidity(Long activityId) throws ManagerException {
		try {
			long now = DateUtils.getCurrentDate().getTime();
			ActivityBiz activityBiz = activityManager.findActivityById(activityId);
			if (now >= activityBiz.getStartTime().getTime() && now <= activityBiz.getEndTime().getTime()) {
				activityBiz.setValidityFlag(true);
			}
			return activityBiz;
		} catch (Exception e) {
			logger.error("判断是否在活动有效期失败, activityId={}", activityId, e);
			ActivityBiz biz = new ActivityBiz();
			biz.setValidityFlag(false);
			return biz;
		}
	}

	@Override
	public void yiRoadAddShareNumber(Long activityId, Long memberId) throws ManagerException {
		try {
			ActivityBiz biz = checkValidity(activityId);
			if (biz.isValidityFlag()) {
				addMemberLotteryNumber(activityId, memberId, 1, biz.getObtainConditionsJson());
			}
		} catch (Exception e) {
			logger.info("亿路上有你活动重复分享, memberId={}, activityId={}", memberId, activityId, e);
		}
	}

	/**
	 * 概率抽奖
	 * 
	 * @param jsonStr
	 *            json串包含奖品code,和奖品概率：{"rewardCode":"PopularityFor25",
	 *            "probability":0.05}
	 * @return
	 */
	private String drawLotteryByProbabilityUtil(String jsonStr) throws Exception {
		double randomNumber = Math.random();
		List<ActivityLotteryBiz> list = JSON.parseArray(jsonStr, ActivityLotteryBiz.class);
		double[] rewards = createProbability(list);
		for (int i = 0; i < rewards.length; i++) {
			if (randomNumber <= rewards[i]) {
				return list.get(i).getRewardCode();
			}
		}
		return null;
	}

	/**
	 * 生成奖品概率
	 * 
	 * @param list
	 * @return
	 */
	private double[] createProbability(List<ActivityLotteryBiz> list) throws Exception {
		double[] rewards = new double[list.size()];
		double start = 0d;
		for (int i = 0; i < list.size(); i++) {
			start = FormulaUtil.doubleAdd(start, list.get(i).getProbability().doubleValue());
			rewards[i] = start;
		}
		if (start > 1d) {
			throw new IllegalArgumentException("概率设定不合法 num={" + start + "}，总概率>100%");
		}
		return rewards;
	}

	/**
	 * 解析抽奖活动规则
	 *
	 * @param biz
	 * @return
	 */
	private ActivityLotteryBiz resolveActivityLotteryRule(String jsonCondition) {
		ActivityLotteryBiz activityLotteryBiz = JSON.parseObject(jsonCondition, ActivityLotteryBiz.class);
		return activityLotteryBiz;
	}

	/**
	 * 提取一次抽奖机会
	 * 
	 * @param activityId
	 * @param memberId
	 * @param jsonCondition
	 * @return
	 */
	private int drawRealCount(Long activityId, Long memberId, String jsonCondition) {
		ActivityLotteryBiz biz = resolveActivityLotteryRule(jsonCondition);
		if ("all".equals(biz.getCycle())) {
			ActivityLottery paraModel = new ActivityLottery();
			paraModel.setActivityId(activityId);
			paraModel.setMemberId(memberId);
			return activityLotteryMapper.updateRealCount(paraModel);
		} else if ("day".equals(biz.getCycle())) {
			ActivityLottery paraModel = new ActivityLottery();
			paraModel.setActivityId(activityId);
			paraModel.setMemberId(memberId);
			paraModel.setCycleConstraint(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate()));
			return activityLotteryMapper.updateRealCount(paraModel);
		} else {
			return 0;
		}
	}

	@Override
	public int showYiRoadShareFlag(Long activityId, Long memberId) throws ManagerException {
		try {
			Map<String, Object> paraMap = new HashMap<String, Object>();
			paraMap.put("memberId", memberId);
			paraMap.put("activityId", activityId);
			paraMap.put("cycleConstraint", DateUtils.getDateStrFromDate(DateUtils.getCurrentDate()));
			ActivityLottery model = activityLotteryMapper.selectByMemberActivity(paraMap);
			if (model == null) {
				return 1;
			} else {
				return 0;
			}
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int insertActivityLottery(ActivityLottery model) throws ManagerException {
		try {
			return activityLotteryMapper.insertSelective(model);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<ActivityLottery> queryLotteryByMemberAndActivity(Map<String, Object> map) throws ManagerException {
		try {
			return activityLotteryMapper.queryLotteryByMemberAndActivity(map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public void sendMillionCoupon(Long memberId, Long transactionId, BigDecimal investAmount) {
		try {
			Long activityId = Long.parseLong(PropertiesUtil.getProperties("activity.millionCoupon.id"));
			Optional<Activity> optAct = LotteryContainer.getInstance().getActivity(activityId);
			if (optAct.isPresent() && optAct.get().getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()
					&& RedisActivityClient.millionCoupon(null) > 0) {
				List<ActivityForMillionCoupon> list = JSON.parseArray(optAct.get().getObtainConditionsJson(),
						ActivityForMillionCoupon.class);
				// 调用赠送活动奖励方法
				doMillionCoupon(list, memberId, transactionId, investAmount, activityId);
			} else if (!optAct.isPresent()) {
				logger.error("百万现金券活动送券未找到活动, activityId={}, memberId={}, transactionId={}", activityId, memberId, transactionId);
			}
		} catch (Exception e) {
			logger.error("百万现金券活动送券失败, memberId={}, transactionId={}", memberId, transactionId, e);
		}
	}

	/**
	 * 
	 * @Description:百万现金券活动送券
	 * @param list
	 * @param memberId
	 * @param transactionId
	 * @param investAmount
	 * @param activityId
	 * @throws Exception
	 * @author: wangyanji
	 * @time:2016年1月5日 上午9:47:01
	 */
	private void doMillionCoupon(List<ActivityForMillionCoupon> list, Long memberId, Long transactionId, final BigDecimal investAmount,
			Long activityId) throws Exception {
		Collection<ActivityForMillionCoupon> filterList = Collections2.filter(list, new Predicate<ActivityForMillionCoupon>() {

			@Override
			public boolean apply(ActivityForMillionCoupon input) {
				if (input.getMaxInvestAmount() != null && investAmount.compareTo(input.getMinInvestAmount()) > -1
						&& investAmount.compareTo(input.getMaxInvestAmount()) < 1) {
					return true;
				} else if (input.getMaxInvestAmount() == null && investAmount.compareTo(input.getMinInvestAmount()) > -1) {
					return true;
				}
				return false;
			}
		});
		if (Collections3.isNotEmpty(filterList) && RedisActivityClient.millionCoupon(null) > 0) {
			ImmutableList<ActivityForMillionCoupon> modelList = ImmutableList.copyOf(filterList);
			ActivityForMillionCoupon model = modelList.get(0);
			String rewardGroup = model.getRewardsGroup();
			RuleBody ruleBody = new RuleBody();
			ruleBody.setActivityId(activityId);
			ruleBody.setMemberId(memberId);
			ruleBody.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
			ruleBody.setCycleStr(transactionId.toString());
			ruleBody.setRewardGroup(rewardGroup);
			ruleBody.setDeductValue(investAmount.intValue());
			RewardsBodyForProbility rb = (RewardsBodyForProbility) drawByGroupProbability.drawLottery(ruleBody, null, null);
			RedisActivityClient.millionCoupon(new BigDecimal(rb.getRewardValue()));
			MessageClient.sendMsgForSPEngin(memberId, "【百万现金券大放送】", rb.getRewardName());
			// 清除缓存
			String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR
					+ RedisConstant.REDIS_KEY_ACTIVITY_MILLIONCOUPON_NAME;
			RedisManager.removeObject(key);
		}
	}

	@Override
	public ResultDO<Object> springFestivalReceiveCoupon(Long memberId) throws ManagerException {
		ResultDO<Object> rDO = new ResultDO<Object>();
		try {
			// 获取春节活动
			Optional<Activity> optOfSpring = LotteryContainer.getInstance().getActivityByName(Constant.SPRING_FESTIVAL_NAME);
			if (!optOfSpring.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			// 匹配除夕领券时间
			ActivityForSpringFestival springFestival = JSON.parseObject(optOfSpring.get().getObtainConditionsJson(),
					ActivityForSpringFestival.class);
			if (!DateUtils.isDateBetween(DateUtils.getCurrentDate(), springFestival.getReceiveStartTime(),
					springFestival.getReceiveEndTime())) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
				return rDO;
			}
			// 判断是否已经留言
			boolean messageFlag = activityMessageManager.checkMessageByActivityIdAndMemberId(optOfSpring.get().getId(), memberId);
			if (!messageFlag) {
				rDO.setResultCode(ResultCode.ACTIVITY_SPRINGFESTIVAL_CHECKMESSAGE_ERROR);
				return rDO;
			}
			// 送券
			RuleBody ruleBody = new RuleBody();
			ruleBody.setActivityId(optOfSpring.get().getId());
			ruleBody.setMemberId(memberId);
			ruleBody.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
			ruleBody.setCycleStr(springFestival.getCouponTemplateId().toString());
			ruleBody.setDeductRemark(Constant.SPRING_COUPON);
			RewardsBase rBase = new RewardsBase();
			rBase.setRewardCode("xianJinQuan118");
			rBase.setRewardName(Constant.SPRING_COUPON_VALUE);
			rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
			rBase.setTemplateId(springFestival.getCouponTemplateId());
			rBase.setRewardValue(118);
			if (drawByPrizeDirectly.validate(ruleBody, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
				drawByPrizeDirectly.drawLottery(ruleBody, rBase, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode());
				// 发送站内信
				MessageClient.sendMsgForSPEngin(memberId, Constant.SPRING_COUPON, Constant.SPRING_COUPON_VALUE);
				rDO.setSuccess(true);
				return rDO;
			} else {
				rDO.setResultCode(ResultCode.ACTIVITY_XIAOMING_STORY_YET_JOIN_ACTIVITY_ERROR);
				return rDO;
			}
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int countNewLotteryResult(ActivityLotteryResult model) throws ManagerException {
		try {
			return activityLotteryResultMapper.countNewLotteryResult(model);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public ResultDO<Object> createRedBagUrlFromTransaction(Activity activity, Long transactionId) throws ManagerException {
		ResultDO<Object> rDO = new ResultDO<Object>();
		try {
			rDO = checkRedBagRule(activity, transactionId);
			if (rDO.isError()) {
				return rDO;
			}
			// 生产红包加密链接
			String code = encryptRedBag(activity.getId(), transactionId);
			if (StringUtil.isBlank(code)) {
				rDO.setResultCode(ResultCode.ACTIVITY_REDBAG_SHARECODE_ERROR);
				return rDO;
			}
			rDO.setResult(code);
			return rDO;
		} catch (Exception e) {
			logger.error("生产红包连接失败, activityName={} transactionId={}", activity.getActivityName(), transactionId, e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
			return rDO;
		}
	}

	@Override
	public ResultDO<Object> checkRedBagRule(Activity activity, Long transactionId) throws ManagerException {
		ResultDO<Object> rDO = new ResultDO<Object>();
		if (activity.getActivityStatus() != StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
			rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
			return rDO;
		}
		TransactionForActivity query = new TransactionForActivity();
		query.setTransactionId(transactionId);
		TransactionForActivity t = transactionManager.selectForProjectExtra(query);
		if (t == null) {
			rDO.setResultCode(ResultCode.TRANSACTION_NOT_EXIST_ERROR);
			return rDO;
		}
		if (!DateUtils.isDateBetween(t.getTransactionTime(), activity.getStartTime(), activity.getEndTime())) {
			rDO.setResultCode(ResultCode.ANNIVERSARY_SHARE_TIMEOUT_ERROR);
			return rDO;
		}
		// 校验活动规则
		PopularityRedBag redBag = LotteryContainer.getInstance().getObtainConditions(activity, PopularityRedBag.class,
				RedisConstant.REDIS_KEY_ACTIVITY_REDBAG);
		if (redBag.getMinInvestAmount() != null && t.getInvestAmount().compareTo(redBag.getMinInvestAmount()) < 0) {
			// 没有达到活动要求的起投金额
			rDO.setResultCode(ResultCode.ACTIVITY_REDBAG_LESSINVEST_ERROR);
			return rDO;
		}
		// 判断是否排除活动项目
		if (redBag.getExceptActivitySign() != null && redBag.getExceptActivitySign().length > 0 && t.getActivitySign() > 0) {
			for (int n : redBag.getExceptActivitySign()) {
				if (t.getActivitySign().equals(n)) {
					rDO.setResultCode(ResultCode.ACTIVITY_REDBAG_PROJECTSIGN_ERROR);
					return rDO;
				}
			}
		}
		return rDO;
	}

	/**
	 * 
	 * @Description:红包加密
	 * @param activityName
	 * @param transactionId
	 * @return
	 * @author: wangyanji
	 * @throws ManagerException
	 * @throws UnsupportedEncodingException
	 * @time:2016年1月10日 下午10:57:03
	 */
	@Override
	public String encryptRedBag(Long activityId, Long transactionId) throws ManagerException {
		StringBuffer codeBuffer = new StringBuffer();
		codeBuffer.append(activityId).append(ActivityConstant.redBagCodeSplit).append(transactionId);
		String encryptCode = CryptHelper.encryptByase(codeBuffer.toString());
		if (encryptCode.equals(codeBuffer.toString())) {
			logger.error("红包加密失败, activityId={}, transactionId={}", activityId, transactionId);
			throw new ManagerException("加密失败 transactionId=" + transactionId);
		}
		return encryptCode;
	}

	@Override
	public List<ActivityLotteryResult> queryNewLotteryResultAndProject(Long activityId, String remark, int rowNum) throws ManagerException {
		try {
			return activityLotteryResultMapper.queryNewLotteryResultAndProject(activityId, remark, rowNum);
		} catch (Exception e) {
			logger.error("显示最新中奖结果集和项目名称失败, activityId={}", activityId, e);
			throw new ManagerException(e);
		}
	}

	@Override
	public void prizeByCheck(Member member) {
		try {
			Optional<Activity> optForActivity = LotteryContainer.getInstance().getActivityByName(
					ActivityConstant.ACTIVITY_QINGMING_NAME);
			if (optForActivity.isPresent()
					&& optForActivity.get().getActivityStatus().equals(StatusEnum.ACTIVITY_STATUS_IS_START.getStatus())) {
				RuleBody ruleBody = new RuleBody();
				ruleBody.setActivityId(optForActivity.get().getId());
				ruleBody.setMemberId(member.getId());
				ruleBody.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
				ruleBody.setCycleStr(DateUtils.formatDatetoString(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3));
				ruleBody.setActivityName(ActivityConstant.ACTIVITY_QINGMING_NAME);
				if (drawByPrizeDirectly.validate(ruleBody, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
					Optional<List<Object>> retList = LotteryContainer.getInstance().getRewardsListMap(optForActivity.get().getId(),
							RewardsBase.class);
					RewardsBase rewardsBase = (RewardsBase) (retList.get().get(0));
					drawByPrizeDirectly.drawLottery(ruleBody, rewardsBase, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode());
					// 发送站内信
					MessageClient.sendMsgForSPEngin(member.getId(), ruleBody.getActivityName(), rewardsBase.getRewardName());
					// 发送短信
					MessageClient.sendShortMessageByMobile(member.getMobile(), ActivityConstant.ACTIVITY_QINGMING_NAME_MSG);
				}
			}
		} catch (Exception e) {
			logger.error("签到送奖励失败, memberId={}", member.getId(), e);
		}
	}

	@Override
	public ActivityLottery selectByMemberActivity(Map<String, Object> queryMap) throws ManagerException {
		try {
			return activityLotteryMapper.selectByMemberActivity(queryMap);
		} catch (Exception e) {
			logger.error("自定义查询抽奖表失败", e);
			throw new ManagerException(e);
		}
	}

	@Override
	public ActivityLottery getRecordForLock(Long id) throws ManagerException {
		try {
			return activityLotteryMapper.getRecordForLock(id);
		} catch (Exception e) {
			logger.error("自定义查询抽奖表失败", e);
			throw new ManagerException(e);
		}
	}

	@Override
	public ResultDO<ActivityForAnniversary> receiveCelebrationA(Long memberId) {
		ResultDO<ActivityForAnniversary> rDO = new ResultDO<ActivityForAnniversary>();
		ActivityForAnniversary activity=new ActivityForAnniversary();
		if (memberId == null) {
			rDO.setResultCode(ResultCode.ACTIVITY_REPORT_IS_NOT_LOGIN);
			return rDO;
		}
		rDO.setSuccess(false);
		try {
			String activityIdStr = PropertiesUtil.getProperties("activity.celebrationA.id");
			Long actId = Long.parseLong(activityIdStr);
			// 判断是否在活动期间内
			if (drawByProbability.isInActivityTime(actId)) {
				RuleBody rb = new RuleBody();
				rb.setActivityId(actId);
				rb.setMemberId(memberId);
				rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
				rb.setCycleStr(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate()));
				rb.setDeductRemark("融光焕发");
				rb.setRewardsAvailableNum(2);
				rb.setRewardsPoolMaxNum(2);
				rb.setActivityName(ActivityConstant.ACTIVITY_CELEBRATE_A_NAME);
				// 校验
				if (drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
					Optional<List<Object>> retList = LotteryContainer.getInstance().getRewardsListMap(actId, RewardsBase.class);
					if (retList.isPresent()) {
						RewardsBodyForProbility rfp = (RewardsBodyForProbility) drawByProbability.drawLottery(rb, null,
								TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode());
						//RewardsBase rewardsBase = (RewardsBase) (retList.get().get(0));
						// 发送站内信
						MessageClient.sendMsgForSPEngin(memberId, rb.getActivityName(), rfp.getRewardName());
						rDO.setSuccess(true);
						activity.setPopularityVaule(rfp.getRewardValue());
						rDO.setResult(activity);
						return rDO;
					} else {
						rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
						return rDO;
					}
				} else {
					rDO.setResultCode(ResultCode.ACTIVITY_TODAY_RED_HAD_RECEIVED_ERROR);
					return rDO;
				}

			} else {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
				return rDO;
			}
		} catch (Exception e) {
			logger.error("庆A轮活动-领取红包错误, memberId={}", memberId, e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}
	/**
	 * 
	 * @desc 猜奖牌数量是奇数还是偶数
	 * @param memberId
	 * @param medalType
	 * @return
	 * @author chaisen
	 * @time 2016年7月12日 上午9:52:06
	 *
	 */
	@Override
	public ResultDO<ActivityForOlympicReturn> guessMedal(Long memberId, int medalType) {
		ResultDO<ActivityForOlympicReturn> rDO = new ResultDO<ActivityForOlympicReturn>();
		ActivityForOlympicReturn activityOlympic=new ActivityForOlympicReturn();
		try {
			if (memberId == null) {
				rDO.setResultCode(ResultCode.ACTIVITY_REPORT_IS_NOT_LOGIN);
				return rDO;
			}
			rDO.setResult(activityOlympic);
			Optional<Activity> olympicActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_PLAY_OLYMPIC);
			if (!olympicActivity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			if(medalType<1){
				rDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
				return rDO;
			}
			Activity activity=olympicActivity.get();
			if (activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
				ActivityForOlympicDate olympicDate = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForOlympicDate.class,
						ActivityConstant.ACTIVITY_PLAY_OLYMPIC_KEY);
				//8.6到8.21 猜可以猜
				if(!DateUtils.isDateBetween(DateUtils.getCurrentDate(), DateUtils.zerolizedTime(DateUtils.getDateTimeFromString(olympicDate.getGuessMedalStartTime())), DateUtils.getEndTime(DateUtils.getDateTimeFromString(olympicDate.getGuessMedalEndTime())))){
					rDO.setResultCode(ResultCode.ACTIVITY_PLAYOLYMPIC_GUESSMEDALTIME_ERROR);
					return rDO;
				}
				//是否在当天的竞猜时间内  每日9:00~19:00
				if(!DateUtils.isDateBetween(DateUtils.getCurrentDate(), DateUtils.getSpecialTime(DateUtils.getCurrentDate(),olympicDate.getGuessStartTime()), DateUtils.getSpecialTime(DateUtils.getCurrentDate(),olympicDate.getGuessEndTime()))){
					//竞猜总人数
					activityOlympic.setGuessTotalNumber(activityLotteryMapper.countActivityLotteryByActivityId(olympicActivity.get().getId(), DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+ActivityConstant.ACTIVITY_OLYMPIC_GUESS_MEDAL));
					rDO.setResultCode(ResultCode.ACTIVITY_PLAYOLYMPIC_GUESSMEDAL_ERROR);
					return rDO;
				}
				RuleBody rb = new RuleBody();
				rb.setActivityId(olympicActivity.get().getId());
				rb.setMemberId(memberId);
				rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
				rb.setCycleStr(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+ActivityConstant.ACTIVITY_OLYMPIC_GUESS_MEDAL);
				rb.setDeductRemark(ActivityConstant.ACTIVITY_PLAY_OLYMPIC);
				rb.setDeductValue(olympicDate.getDeductPopularvalue());
				rb.setActivityName(ActivityConstant.ACTIVITY_PLAY_OLYMPIC);
				// 校验
				if (!drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
					rDO.setResultCode(ResultCode.ACTIVITY_PLAYOLYMPIC_GUESSMEDAL_HADONCE_ERROR);
					return rDO;
				}
				rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_POPULAR.getStatus());
				if (drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_POPULARITY.getCode())) {
					//扣除人气值，记录竞猜记录
					verificationByPopularity.prepare(rb);
					ActivityLotteryResult betResult = new ActivityLotteryResult();
					betResult.setMemberId(memberId);
					betResult.setActivityId(olympicActivity.get().getId());
					betResult.setChip(olympicDate.getDeductPopularvalue());
					betResult.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_POPULARITY.getType());
					betResult.setRemark(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+ActivityConstant.ACTIVITY_OLYMPIC_GUESS_MEDAL+medalType);
					betResult.setRewardId(memberId+ActivityConstant.ACTIVITY_OLYMPIC_GUESS_MEDAL);
					betResult.setRewardResult("0");
					betResult.setStatus(0);
					betResult.setRewardInfo(olympicDate.getDeductPopularvalue() + ActivityConstant.popularityDesc);
					betResult.setCycleStr(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+ActivityConstant.ACTIVITY_OLYMPIC_GUESS_MEDAL);
					activityLotteryResultMapper.insertSelective(betResult);
					//竞猜记录
					activityOlympic.setGuessMedalRecord(getGuessMedalRecord(memberId,olympicActivity.get().getId()));
					//竞猜总人数
					activityOlympic.setGuessTotalNumber(activityLotteryMapper.countActivityLotteryByActivityId(olympicActivity.get().getId(), DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+ActivityConstant.ACTIVITY_OLYMPIC_GUESS_MEDAL));
					return rDO;
				}else{
					rDO.setResultCode(ResultCode.ACTIVITY_POPULARITY__ERROR);
					return rDO;
				}
			}else{
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
				return rDO;
			}
			
		} catch (Exception e) {
			logger.error("玩转奥运-竞猜奖牌失败, memberId={}", memberId, e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}
	/**
	 * 
	 * @Description:获取竞猜记录
	 * @param memberId
	 * @return
	 * @author: chaisen
	 * @time:2016年7月12日 上午11:29:30
	 */
	@Override
	public List<ActivityForOlympicGuess> getGuessMedalRecord(Long memberId,Long activityId) {
		List<ActivityForOlympicGuess> guessList=Lists.newArrayList();
		ActivityLotteryResult lotteryResult=new ActivityLotteryResult();
		lotteryResult.setActivityId(activityId);
		lotteryResult.setMemberId(memberId);
		lotteryResult.setRewardId(memberId+ActivityConstant.ACTIVITY_OLYMPIC_GUESS_MEDAL);
		List<ActivityLotteryResult> resultList=activityLotteryResultMapper.getLotteryResultBySelective(lotteryResult);
		if(Collections3.isEmpty(resultList)){
			return guessList;
		}
		for(ActivityLotteryResult biz:resultList){
			ActivityForOlympicGuess guess=new ActivityForOlympicGuess();
			guess.setGuessTime(biz.getCreateTime());
			String temp=biz.getRemark();
			if(temp.substring(temp.length()-1, temp.length()).contains("1")){
				guess.setGuessResult(1);
			}else{
				guess.setGuessResult(2);
			}
			guess.setRealResult(Integer.parseInt(biz.getRewardResult()));
			guessList.add(guess);
		}
		return guessList;
	}
	/**
	 * 
	 * @Description:获取用户集偶
	 * @param activityId
	 * @param memberId
	 * @return
	 * @author: chaisen
	 * @time:2016年7月20日 下午1:32:43
	 */
	@Override
	public Integer getMedalType(Long activityId,Long memberId){
		ActivityLotteryResult lotteryResult=new ActivityLotteryResult();
		lotteryResult.setActivityId(activityId);
		lotteryResult.setMemberId(memberId);
		lotteryResult.setRewardId(memberId+ActivityConstant.ACTIVITY_OLYMPIC_GUESS_MEDAL);
		lotteryResult.setGuessStartTime(DateUtils.zerolizedTime(DateUtils.getCurrentDate()));
		lotteryResult.setGuessEndTime(DateUtils.getEndTime(DateUtils.getCurrentDate()));
		ActivityLotteryResult bean=activityLotteryResultMapper.getLotteryResultGuessMedalType(lotteryResult);
		int type=0;
		if(bean!=null){
			if(bean.getRemark().contains(ActivityConstant.ACTIVITY_OLYMPIC_GUESS_MEDAL)){
				String temp=bean.getRemark();
				String type_str = temp.substring(temp.length()-1, temp.length());
				type=Integer.parseInt(type_str);
			}
		}
		return type;
	}
	/**
	 * 
	 * @Description:获取金牌的竞猜记录
	 * @param memberId
	 * @param activityId
	 * @return
	 * @author: chaisen
	 * @time:2016年7月12日 下午2:03:38
	 */
	@Override
	public List<ActivityForOlympicGuess> getGuessGoldRecord(Long memberId, Long activityId,ActivityForOlympicDate olympicDate,Date startTime) {
		List<ActivityForOlympicGuess> guessList=Lists.newArrayList();
		ActivityLotteryResult lotteryResult=new ActivityLotteryResult();
		lotteryResult.setActivityId(activityId);
		lotteryResult.setMemberId(memberId);
		lotteryResult.setRewardId(memberId+ActivityConstant.ACTIVITY_OLYMPIC_GUESS_GOLE);
		List<ActivityLotteryResult> resultList=activityLotteryResultMapper.getLotteryResultBySelective(lotteryResult);
		if(Collections3.isEmpty(resultList)){
			return guessList;
		}
		for(ActivityLotteryResult biz:resultList){
			ActivityForOlympicGuess guess=new ActivityForOlympicGuess();
			guess.setGuessTime(biz.getCreateTime());
			guess.setPopularityValue(biz.getChip());
			guess.setGoldNumber(Integer.parseInt(biz.getRewardResult()));
			if(DateUtils.isDateBetween(biz.getCreateTime(), startTime, DateUtils.getDateTimeFromString(olympicDate.getSixEndTime()))){
				guess.setType(5);
			}else if(DateUtils.isDateBetween(biz.getCreateTime(), DateUtils.getDateTimeFromString(olympicDate.getSevenStartTime()), DateUtils.getDateTimeFromString(olympicDate.getEightEndTime()))){
				guess.setType(3);
			}else if(DateUtils.isDateBetween(biz.getCreateTime(), DateUtils.getDateTimeFromString(olympicDate.getNineStartTime()), DateUtils.getDateTimeFromString(olympicDate.getGuessGoldEndTime()))){
				guess.setType(2);
			}
			guessList.add(guess);
		}
		return guessList;
	}
	/**
	 * 
	 * @desc 猜金牌
	 * @param memberId
	 * @param popularityValue
	 * @param goldNumber
	 * @return
	 * @author chaisen
	 * @time 2016年7月12日 下午1:33:32
	 *
	 */
	@Override
	public ResultDO<ActivityForOlympicReturn> guessGold(Long memberId, int popularityValue, int goldNumber) {
		ResultDO<ActivityForOlympicReturn> rDO = new ResultDO<ActivityForOlympicReturn>();
		ActivityForOlympicReturn activityOlympic=new ActivityForOlympicReturn();
		try {
			if (memberId == null) {
				rDO.setResultCode(ResultCode.ACTIVITY_REPORT_IS_NOT_LOGIN);
				return rDO;
			}
			Optional<Activity> olympicActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_PLAY_OLYMPIC);
			if (!olympicActivity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			Activity activity=olympicActivity.get();
			if(goldNumber<0||popularityValue<1){
				rDO.setResultCode(ResultCode.ACTIVITY_PARAM_WRONG_ERROR);
				return rDO;
			}
			if (activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
				ActivityForOlympicDate olympicDate = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForOlympicDate.class,
						ActivityConstant.ACTIVITY_PLAY_OLYMPIC_KEY);
				//是否在8月5日00:00~8月12日23:59时间内
				if(!DateUtils.isDateBetween(DateUtils.getCurrentDate(),DateUtils.getDateTimeFromString(olympicDate.getGuessGoldStartTime()), DateUtils.getDateTimeFromString(olympicDate.getGuessGoldEndTime()))){
					rDO.setResultCode(ResultCode.ACTIVITY_PLAYOLYMPIC_GUESSGOLD_OUTTIME_ERROR);
					return rDO;
				}
				RuleBody rb = new RuleBody();
				rb.setActivityId(olympicActivity.get().getId());
				rb.setMemberId(memberId);
				rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
				rb.setCycleStr(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+ActivityConstant.ACTIVITY_OLYMPIC_GUESS_GOLE);
				rb.setDeductRemark(ActivityConstant.ACTIVITY_PLAY_OLYMPIC);
				rb.setRewardsAvailableNum(2);
				rb.setRewardsPoolMaxNum(2);
				rb.setDeductValue(popularityValue);
				rb.setActivityName(ActivityConstant.ACTIVITY_PLAY_OLYMPIC);
				// 校验
				if (!drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
					rDO.setResultCode(ResultCode.ACTIVITY_PLAYOLYMPIC_GUESSMEDAL_HADONCE_ERROR);
					return rDO;
				}
				rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_POPULAR.getStatus());
				if (drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_POPULARITY.getCode())) {
					//扣除人气值，记录竞猜记录
					verificationByPopularity.prepare(rb);
					ActivityLotteryResult betResult = new ActivityLotteryResult();
					betResult.setMemberId(memberId);
					betResult.setActivityId(olympicActivity.get().getId());
					betResult.setChip(popularityValue);
					betResult.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_POPULARITY.getType());
					betResult.setRemark(ActivityConstant.ACTIVITY_OLYMPIC_GUESS_GOLE);
					betResult.setRewardId(memberId+ActivityConstant.ACTIVITY_OLYMPIC_GUESS_GOLE);
					//金牌数量
					betResult.setRewardResult(Integer.toString(goldNumber));
					betResult.setStatus(0);
					betResult.setRewardInfo(popularityValue+ ActivityConstant.popularityDesc);
					betResult.setCycleStr(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+ActivityConstant.ACTIVITY_OLYMPIC_GUESS_GOLE);
					activityLotteryResultMapper.insertSelective(betResult);
					//竞猜记录
					activityOlympic.setGuessGoldRecord(getGuessGoldRecord(memberId,olympicActivity.get().getId(),olympicDate,activity.getStartTime()));
					rDO.setResult(activityOlympic);
					rDO.setSuccess(true);
					return rDO;
				}else{
					rDO.setResultCode(ResultCode.ACTIVITY_POPULARITY__ERROR);
					return rDO;
				}
			}else{
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
				return rDO;
			}
			
		} catch (Exception e) {
			logger.error("玩转奥运-竞猜金牌失败, memberId={}", memberId, e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}

	/**
	 * 
	 * @desc 竞猜人数
	 * @param activityId
	 * @param cycleConstraint
	 * @return
	 * @author chaisen
	 * @throws ManagerException 
	 * @time 2016年7月12日 上午11:54:01
	 *
	 */
	@Override
	public int countActivityLotteryByActivityId(Long activityId, String cycleConstraint) throws ManagerException {
		try {
			return activityLotteryMapper.countActivityLotteryByActivityId(activityId,cycleConstraint);
		} catch (Exception e) {
			logger.error("竞猜人数统计失败", e);
			throw new ManagerException(e);
		}
	}
	/**
	 * 
	 * @desc 集奥运兑换奖品
	 * @param memberId
	 * @param puzzle
	 * @return
	 * @author chaisen
	 * @time 2016年7月12日 下午3:08:14
	 *
	 */
	@Override
	public ResultDO<ActivityForOlympicReturn> setOlympic(Long memberId, String param) {
		ResultDO<ActivityForOlympicReturn> rDO = new ResultDO<ActivityForOlympicReturn>();
		ActivityForOlympicReturn activityOlympic=new ActivityForOlympicReturn();
		ActivityForOlympic model = new ActivityForOlympic();
		try {
			if (memberId == null) {
				rDO.setResultCode(ResultCode.ACTIVITY_REPORT_IS_NOT_LOGIN);
				return rDO;
			}
			rDO.setResult(activityOlympic);
			Optional<Activity> olympicActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_PLAY_OLYMPIC);
			if (!olympicActivity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			if(!StringUtils.isNotBlank(param)){
				rDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
				return rDO;
			}
			String[] puzzle=param.split(",");
			if(puzzle==null||puzzle.length==0){
				rDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
				return rDO;
			}
		    if(StringUtil.isSame(puzzle)){
		    	rDO.setResultCode(ResultCode.ACTIVITY_PARAM_SAME_ERROR);
				return rDO;
		    }
		    Activity activity=olympicActivity.get();
		    if (activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
				//校验拼图剩余数量
				ActivityLottery activityLottery=new ActivityLottery();
				activityLottery.setActivityId(activity.getId());
				activityLottery.setMemberId(memberId);
				for(int i=0;i<puzzle.length;i++){
					activityLottery.setCycleConstraint(memberId+ActivityConstant.redBagCodeSplit+ActivityConstant.ACTIVITY_OLYMPIC_SET_OLYMPIC+ActivityConstant.redBagCodeSplit+puzzle[i]);
					ActivityLottery lotteryRemind=activityLotteryMapper.checkExistLottery(activityLottery);
					if(lotteryRemind==null){
						rDO.setResultCode(ResultCode.ACTIVITY_PLAYOLYMPIC_PUZZE_NO_ERROR);
						return rDO;
					}
					if(lotteryRemind.getRealCount()<1){
						rDO.setResultCode(ResultCode.ACTIVITY_PLAYOLYMPIC_PUZZE_NO_ERROR);
						return rDO;
					}
				}
				RuleBody rb = new RuleBody();
				rb.setActivityId(activity.getId());
				rb.setMemberId(memberId);
				rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_COIN.getStatus());
				rb.setActivityName(ActivityConstant.ACTIVITY_PLAY_OLYMPIC);
				rb.setCycleStr(activityLottery.getCycleConstraint());
				ActivityForOlympicDate olympicDate = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForOlympicDate.class,
						ActivityConstant.ACTIVITY_PLAY_OLYMPIC_KEY);
				Long templateId=1L;
				if(puzzle.length==1){
					templateId=olympicDate.getTemplateId().get(0);
				}else if(puzzle.length==2){
					templateId=olympicDate.getTemplateId().get(2);
				}else if(puzzle.length==3){
					templateId=olympicDate.getTemplateId().get(3);
				//实物大奖
				}else if(puzzle.length==4){
					ActivityLotteryResult betResult = new ActivityLotteryResult();
					betResult.setMemberId(memberId);
					betResult.setActivityId(olympicActivity.get().getId());
					betResult.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_GIFT.getType());
					betResult.setRewardInfo("奥运吉祥物拼图-兑换荣耀手环zero一个");
					betResult.setRemark(ActivityConstant.ACTIVITY_OLYMPIC_REAL_PRIZE);
					betResult.setCycleStr(memberId+ActivityConstant.redBagCodeSplit+ActivityConstant.ACTIVITY_OLYMPIC_SET_OLYMPIC+ActivityConstant.redBagCodeSplit+puzzle[3]);
					activityLotteryResultMapper.insertSelective(betResult);
					//发放成功减去吉祥物张数
					for(int i=0;i<puzzle.length;i++){
						activityLottery.setRealCount(-1);
						activityLottery.setCycleConstraint(memberId+ActivityConstant.redBagCodeSplit+ActivityConstant.ACTIVITY_OLYMPIC_SET_OLYMPIC+ActivityConstant.redBagCodeSplit+puzzle[i]);
						activityLotteryMapper.updateRealCount(activityLottery);
					}
					//幸运儿-实物大奖列表
					activityOlympic.setLuckyList(getRealPrize(activity.getId()));
					//返回吉祥物剩余数量
					//activityOlympic.setPuzzleRemind(this.getPuzzleRemind(activity.getId(),memberId,olympicDate));
					model=getPuzzleRemind(activity.getId(),memberId,olympicDate,model);
					if(model!=null){
						activityOlympic.setPuzzle1(model.getPuzzle1());
						activityOlympic.setPuzzle2(model.getPuzzle2());
						activityOlympic.setPuzzle3(model.getPuzzle3());
						activityOlympic.setPuzzle4(model.getPuzzle4());
					}
					//发送短信
					MessageClient.sendMsgForCommon(memberId,Constant.MSG_TEMPLATE_TYPE_PHONE, MessageEnum.SET_OLYMPIC.getCode(),
							ActivityConstant.ACTIVITY_PLAY_OLYMPIC);
					return rDO;
				}
				CouponTemplate c = couponTemplateManager.selectByPrimaryKey(templateId);
				if(c==null){
					rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_COUPON_NOTEXITS_ERROR);
					return rDO;
				}
				// 发放奖励
				RewardsBase rBase = new RewardsBase();
				rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
				rBase.setRewardValue(c.getAmount().intValue());
				rBase.setTemplateId(templateId);
				rBase.setRewardName(c.getAmount().intValue() + ActivityConstant.couponDesc);
				drawByPrizeDirectly.drawLottery(rb, rBase, "");
				//站内信
				MessageClient.sendMsgForSPEngin(memberId, ActivityConstant.ACTIVITY_OLYMPIC_SET_NAME, rBase.getRewardName());
				//发放成功减去吉祥物张数
				for(int i=0;i<puzzle.length;i++){
					activityLottery.setRealCount(-1);
					activityLottery.setCycleConstraint(memberId+ActivityConstant.redBagCodeSplit+ActivityConstant.ACTIVITY_OLYMPIC_SET_OLYMPIC+ActivityConstant.redBagCodeSplit+puzzle[i]);
					activityLotteryMapper.updateRealCount(activityLottery);
				}
				//返回实物大奖列表
				activityOlympic.setLuckyList(getRealPrize(activity.getId()));
				//返回吉祥物剩余数量
				//activityOlympic.setPuzzleRemind(this.getPuzzleRemind(activity.getId(),memberId,olympicDate));
				model=getPuzzleRemind(activity.getId(),memberId,olympicDate,model);
				if(model!=null){
					activityOlympic.setPuzzle1(model.getPuzzle1());
					activityOlympic.setPuzzle2(model.getPuzzle2());
					activityOlympic.setPuzzle3(model.getPuzzle3());
					activityOlympic.setPuzzle4(model.getPuzzle4());
				}
				return rDO;
			}else{
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
				return rDO;
			}
			
		} catch (Exception e) {
			logger.error("玩转奥运-兑换奖品, memberId={}", memberId, e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}
	/**
	 * 
	 * @Description:实物大奖列表
	 * @param id
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年7月12日 下午5:50:27
	 */
	@Override
	public List<ActivityForOlympicGuess> getRealPrize(Long activityId) throws ManagerException {
		List<ActivityForOlympicGuess> guessList=Lists.newArrayList();
		ActivityLotteryResult lotteryResult=new ActivityLotteryResult();
		lotteryResult.setActivityId(activityId);
		lotteryResult.setRemark(ActivityConstant.ACTIVITY_OLYMPIC_REAL_PRIZE);
		List<ActivityLotteryResult> resultList=activityLotteryResultMapper.getLotteryResultBySelectiveOrderBy(lotteryResult);
		if(Collections3.isEmpty(resultList)){
			return guessList;
		}
		for(ActivityLotteryResult biz:resultList){
			ActivityForOlympicGuess guess=new ActivityForOlympicGuess();
			Member member=memberManager.selectByPrimaryKey(biz.getMemberId());
			if(member!=null){
				guess.setAvatars(member.getAvatars());
				guess.setUsername(StringUtil.maskUserNameOrMobile(member.getUsername(), member.getMobile()));
			}
			guessList.add(guess);
		}
		return guessList;
	}
	/**
	 * 
	 * @desc 吉祥物剩余数量
	 * @param activityId
	 * @param memberId
	 * @param olympicDate
	 * @return
	 * @author chaisen
	 * @time 2016年7月8日 下午4:30:46
	 *
	 */
	@Override
	public ActivityForOlympic getPuzzleRemind(Long activityId, Long memberId,ActivityForOlympicDate olympicDate,ActivityForOlympic oly) {
		if(olympicDate.getPuzzle()==null){
			return null;
		}
		ActivityLottery model = new ActivityLottery();
		ActivityLottery biz=new ActivityLottery();
		model.setActivityId(activityId);
		model.setMemberId(memberId);
		model.setCycleConstraint(memberId+ActivityConstant.redBagCodeSplit+ActivityConstant.ACTIVITY_OLYMPIC_SET_OLYMPIC+ActivityConstant.redBagCodeSplit+olympicDate.getPuzzle().get(0));
		biz=activityLotteryMapper.checkExistLottery(model);
		if(biz!=null&&biz.getRealCount()>0){
			oly.setPuzzle1(biz.getRealCount());
		}
		model.setCycleConstraint(memberId+ActivityConstant.redBagCodeSplit+ActivityConstant.ACTIVITY_OLYMPIC_SET_OLYMPIC+ActivityConstant.redBagCodeSplit+olympicDate.getPuzzle().get(1));
		biz=activityLotteryMapper.checkExistLottery(model);
		if(biz!=null&&biz.getRealCount()>0){
			oly.setPuzzle2(biz.getRealCount());
		}
		model.setCycleConstraint(memberId+ActivityConstant.redBagCodeSplit+ActivityConstant.ACTIVITY_OLYMPIC_SET_OLYMPIC+ActivityConstant.redBagCodeSplit+olympicDate.getPuzzle().get(2));
		biz=activityLotteryMapper.checkExistLottery(model);
		if(biz!=null&&biz.getRealCount()>0){
			oly.setPuzzle3(biz.getRealCount());
		}
		model.setCycleConstraint(memberId+ActivityConstant.redBagCodeSplit+ActivityConstant.ACTIVITY_OLYMPIC_SET_OLYMPIC+ActivityConstant.redBagCodeSplit+olympicDate.getPuzzle().get(3));
		biz=activityLotteryMapper.checkExistLottery(model);
		if(biz!=null&&biz.getRealCount()>0){
			oly.setPuzzle4(biz.getRealCount());
		}
		return oly;
	}

	@Override
	public ActivityLottery checkExistLottery(ActivityLottery activityLottery) throws ManagerException {
		try {
			return activityLotteryMapper.checkExistLottery(activityLottery);
		} catch (Exception e) {
			logger.error("查询拼图剩余数量失败, activityId={}", activityLottery, e);
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateByActivityAndMember(ActivityLottery activityLottery) throws ManagerException {
		try {
			return activityLotteryMapper.updateByActivityAndMember(activityLottery);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateRealCount(ActivityLottery activityLottery) throws ManagerException {
		try {
			return activityLotteryMapper.updateRealCount(activityLottery);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 
	 * @desc 加入战队
	 * @param memberId
	 * @return
	 * @author chaisen
	 * @time 2016年6月30日 下午5:56:51
	 *
	 */
	@Override
	public ResultDO<ActivityForJulyRetrun> julyTeamJoin(Long memberId) {
		ResultDO<ActivityForJulyRetrun> rDO = new ResultDO<ActivityForJulyRetrun>();
		ActivityForJulyRetrun activityReturn=new ActivityForJulyRetrun();
		try {
			if (memberId == null) {
				rDO.setResultCode(ResultCode.ACTIVITY_REPORT_IS_NOT_LOGIN);
				return rDO;
			}
			rDO.setSuccess(false);
			Optional<Activity> teamActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_JULY_TEAM);
			if (!teamActivity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			Activity activity=teamActivity.get();
			if(!DateUtils.isDateBetween(DateUtils.getCurrentDate(), activity.getStartTime(), activity.getEndTime())){
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
				return rDO;
			}
			ActivityForJulyDate julyDate = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForJulyDate.class,
					ActivityConstant.ACTIVITY_JULY_TEAM_KEY);
			
			if(!DateUtils.isDateBetween(DateUtils.getCurrentDate(), DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getJoinStartTime()), DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getJoinEndTime()))){
				rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_WAIT_TIME_ERROR);
				return rDO;
			}
			if(activityGroupManager.checkGroupByActivityIdAndMemberId(teamActivity.get().getId(), memberId)){
				rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_HAD_JOINED_ERROR);
				return rDO;
			}
			int groupType=RandomUtils.getRandomNumberByRange(ActivityEnum.JULYTEAM_GROUPTYPE_JYSH.getCode(),ActivityEnum.JULYTEAM_GROUPTYPE_QLYX.getCode());
			ActivityGroup group=new ActivityGroup();
			group.setMemberId(memberId);
			group.setActivityId(teamActivity.get().getId());
			group.setGroupType(groupType);
			group.setCreateTime(DateUtils.getCurrentDate());
			group.setDelFlag(1);
			int i=activityGroupMapper.insertSelective(group);
			if(i>0){
				ActivityForJulyBet couponRemind=activityGroupManager.getRemindCoupon(activity,groupType);
				activityReturn.setCouponRemind(couponRemind);
				List<TransactionForFirstInvestAct> currentGroupFirstTen=currentGroupFirstTen=activityGroupManager.selectTopTenInvestByGroupType(groupType,activity.getId(),DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountStartTime()),DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountEndTime()));
				activityReturn.setCurrentGroupType(groupType);
				activityReturn.setJulyTeamContribution(currentGroupFirstTen);
				rDO.setResult(activityReturn);
				rDO.setSuccess(true);
				return rDO;
			}
		} catch (Exception e) {
			logger.error("组团大作战-加入战队失败, memberId={}", memberId, e);
		}
		return rDO;
	}

	@Override
	public ResultDO<ActivityForJulyRetrun> betJulyTeam(Long memberId,int popularityValue ,int groupType) {
		ResultDO<ActivityForJulyRetrun> rDO = new ResultDO<ActivityForJulyRetrun>();
		ActivityForJulyRetrun activityJuly=new ActivityForJulyRetrun();
		try {
			if (memberId == null) {
				rDO.setResultCode(ResultCode.ACTIVITY_REPORT_IS_NOT_LOGIN);
				return rDO;
			}
			rDO.setSuccess(false);
			Optional<Activity> teamActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_JULY_TEAM);
			if (!teamActivity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			if(popularityValue>20){
				rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_CANT_BETED_TWENTY_ERROR);
				return rDO;
			}
			if(popularityValue==0||groupType==0){
				rDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
				return rDO;
			}
			if (drawByProbability.isInActivityTime(teamActivity.get().getId())) {
				Activity activity=teamActivity.get();
				ActivityForJulyDate julyDate = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForJulyDate.class,
						ActivityConstant.ACTIVITY_JULY_TEAM_KEY);
				//是否在当天的押注时间内
				if(!DateUtils.isDateBetween(DateUtils.getCurrentDate(), DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getBetStartTime()), DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getBetEndTime()))){
					rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_BET_TIME_ERROR);
					return rDO;
				}
				RuleBody rb = new RuleBody();
				rb.setActivityId(teamActivity.get().getId());
				rb.setMemberId(memberId);
				rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
				rb.setCycleStr(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+"-betjulyteam");
				rb.setDeductRemark(activity.getActivityDesc());
				rb.setRewardsAvailableNum(2);
				rb.setRewardsPoolMaxNum(2);
				rb.setDeductValue(popularityValue);
				rb.setActivityName(ActivityConstant.ACTIVITY_JULY_TEAM);
				// 校验
				if (!drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
					rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_HAD_BETED_ERROR);
					return rDO;
				}
				rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_POPULAR.getStatus());
				if (drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_POPULARITY.getCode())) {
					//扣除人气值，记录押注记录
					verificationByPopularity.prepare(rb);
					ActivityLotteryResult betResult = new ActivityLotteryResult();
					betResult.setMemberId(memberId);
					betResult.setActivityId(teamActivity.get().getId());
					betResult.setChip(popularityValue);
					betResult.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_POPULARITY.getType());
					betResult.setRemark(Integer.toString(groupType));
					betResult.setRewardId(memberId.toString());
					betResult.setCycleStr(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+"-betjulyteam");
					betResult.setStatus(0);
					betResult.setRewardInfo(popularityValue + ActivityConstant.popularityDesc);
					activityLotteryResultMapper.insertSelective(betResult);
					// 刷新人气值
					Balance balance = balanceManager.queryBalance(memberId, TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY);
					if (balance != null) {
						activityJuly.setPopularityValue(balance.getAvailableBalance().intValue());
					}
					ActivityLotteryResult activityResult=new ActivityLotteryResult();
					activityResult.setActivityId(activity.getId());
					activityResult.setMemberId(memberId);
					activityResult.setRewardId(memberId.toString());
					//押注记录
					List<ActivityForJulyBet> betList=activityLotteryResultManager.getBetRecordList(activityResult);
					activityJuly.setBetList(betList);
					activityJuly.setBetTotals(countBetTotal(activity.getId(),DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+"-betjulyteam"));
					rDO.setResult(activityJuly);
					rDO.setSuccess(true);
					return rDO;
				}else{
					rDO.setResultCode(ResultCode.ACTIVITY_POPULARITY__ERROR);
					return rDO;
				}
			}else{
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
				return rDO;
			}
			
		} catch (Exception e) {
			logger.error("组团大作战-押注失败, memberId={}", memberId, e);
		}
		return rDO;
	}
	
	public boolean checkPopularityValue(Long memberId,int popularityValue) throws ManagerException{
		//校验当前人气值是否够扣
		Balance balance = balanceManager.queryBalance(memberId, TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY);
		if(balance != null){
			if(balance.getAvailableBalance().intValue() >= popularityValue) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 
	 * @desc 领取红包
	 * @param memberId
	 * @param couponAmount
	 * @return
	 * @author chaisen
	 * @time 2016年7月1日 下午2:29:28
	 *
	 */
	@Override
	public ResultDO<ActivityForJulyRetrun> receiveJulyTeamCoupon(Long memberId, int couponAmount) {
		ResultDO<ActivityForJulyRetrun> rDO = new ResultDO<ActivityForJulyRetrun>();
		ActivityForJulyRetrun activityJuly=new ActivityForJulyRetrun();
		try {
			if (memberId == null) {
				rDO.setResultCode(ResultCode.ACTIVITY_REPORT_IS_NOT_LOGIN);
				return rDO;
			}
			rDO.setSuccess(false);
			Optional<Activity> teamActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_JULY_TEAM);
			if (!teamActivity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			Activity activity=teamActivity.get();
			int groupType=activityGroupManager.getGroupTypeByMemberId(activity.getId(), memberId);
			if (drawByProbability.isInActivityTime(activity.getId())) {
				ActivityForJulyDate julyDate = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForJulyDate.class,
						ActivityConstant.ACTIVITY_JULY_TEAM_KEY);
				//是否加入过战队
				if(!activityGroupManager.checkGroupByActivityIdAndMemberId(teamActivity.get().getId(), memberId)){
					rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_COUPON_JOIN_ERROR);
					return rDO;
				}
				//是否在当天的领取时间内
				if(!DateUtils.isDateBetween(DateUtils.getCurrentDate(), DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getReceiveStartTime()), DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getReceiveEndTime()))){
					rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_RECEIVE_TIME_ERROR);
					return rDO;
				}
				RuleBody rb = new RuleBody();
				rb.setActivityId(activity.getId());
				rb.setMemberId(memberId);
				rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_COIN.getStatus());
				rb.setActivityName(activity.getActivityDesc());
				rb.setCycleStr(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate()));
				rb.setDeductRemark(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate()));
				// 校验剩余次数
				ActivityLottery lotter=new ActivityLottery();
				lotter.setActivityId(activity.getId());
				lotter.setMemberId(memberId);
				lotter.setCycleConstraint(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate()));
				lotter.setRealCount(-1);
				Long templateId=1L;
				int totalCoupon=0;
				int receivedCoupon=0;
				for(int i=0;i<julyDate.getCouponAmount().size();i++){
					if(julyDate.getCouponAmount().get(i)==couponAmount){
						templateId=julyDate.getTemplateId().get(i);
						totalCoupon=julyDate.getTotalCoupon().get(i);
						break;
					}
				}
				String totalKey = RedisConstant.ACTIVITY_SETP_TEAM_CURRENT_COUPONAMOUNT + RedisConstant.REDIS_SEPERATOR + DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+ RedisConstant.REDIS_SEPERATOR+couponAmount+ RedisConstant.REDIS_SEPERATOR+groupType;
				if(RedisManager.isExit(totalKey)){
					String total=RedisManager.get(totalKey);
					if(total!=null){
						receivedCoupon=Integer.parseInt(total);
					}
				}
				if(receivedCoupon>totalCoupon){
					rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_COUPON_HADEND_ERROR);
					return rDO;
				}
				ActivityLottery activityLottery=activityLotteryMapper.checkExistLottery(lotter);
				if(activityLottery==null){
					rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_COUPON_ERROR);
					return rDO;
				}
				if(activityLottery.getRealCount()<1){
					rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_COUPON_ERROR);
					return rDO;
				}
				
				if(totalCoupon-getRemindCouponNumber(lotter,templateId,groupType)<=0){
					rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_COUPON_HADEND_ERROR);
					return rDO;
				}
				// 发放奖励
				RewardsBase rBase = new RewardsBase();
				CouponTemplate c = couponTemplateManager.selectByPrimaryKey(templateId);
				if(c==null){
					rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_COUPON_NOTEXITS_ERROR);
					return rDO;
				}
				rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
				rBase.setRewardName(c.getAmount().intValue() + ActivityConstant.couponDesc);
				rBase.setRewardValue(c.getAmount().intValue());
				rBase.setTemplateId(templateId);
				rb.setRewardId(templateId.toString());
				drawByPrizeDirectly.drawLottery(rb, rBase, "");
				RedisManager.incrby(totalKey, 1L);
				activityLotteryMapper.updateByActivityAndMember(lotter);
				activityLottery=activityLotteryMapper.checkExistLottery(lotter);
				if(activityLottery!=null){
					activityJuly.setCouponValue(activityLottery.getRealCount());
				}
				ActivityForJulyBet couponRemind=activityGroupManager.getRemindCoupon(activity,groupType);
				//站内信
				MessageClient.sendMsgForSPEngin(memberId, activity.getActivityDesc(), activity.getReleaseReason());
				activityJuly.setCouponRemind(couponRemind);
				rDO.setResult(activityJuly);
				rDO.setSuccess(true);
				return rDO;
			}else{
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
				return rDO;
			}
			
		} catch (Exception e) {
			logger.error("组团大作战-领取红包, memberId={}", memberId, e);
		}
		return rDO;
	}
	/**
	 * 
	 * @Description:获取红包剩余数量
	 * @param rb
	 * @param totalCoupon
	 * @return
	 * @author: chaisen
	 * @time:2016年7月1日 下午4:50:15
	 */
	@Override
	public Integer getRemindCouponNumber(ActivityLottery rb,Long templateId,int groupType){
		int count=activityLotteryResultMapper.countRemindBetLotteryResult(rb.getActivityId(),rb.getCycleConstraint(),templateId.toString(),groupType);
		return count;
	}
	/**
	 * 
	 * @desc 统计本来押注记录
	 * @param activityId
	 * @param cycleConstraint
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年7月4日 下午5:25:03
	 *
	 */
	@Override
	public Integer countBetTotal(Long activityId, String cycleConstraint) throws ManagerException {
		try {
			return activityLotteryMapper.countBetTotal(activityId, cycleConstraint);
		} catch (Exception e) {
			logger.error("统计本来押注记录失败, activityId={}", activityId, e);
			throw new ManagerException(e);
		}
	}
	/**
	 * 
	 * @desc 领取红包
	 * @param memberId
	 * @param couponAmount
	 * @return
	 * @author chaisen
	 * @time 2016年10月17日 下午2:19:10
	 *
	 */
	@Override
	public ResultDO<ActivityForAnniversaryRetrun> receiveCouponAnniversary(Long memberId, int couponAmount,int type) throws ManagerException{
		ResultDO<ActivityForAnniversaryRetrun> rDO = new ResultDO<ActivityForAnniversaryRetrun>();
		ActivityForAnniversaryRetrun activityJuly=new ActivityForAnniversaryRetrun();
		try {
			if (memberId == null) {
				rDO.setResultCode(ResultCode.ACTIVITY_REPORT_IS_NOT_LOGIN);
				return rDO;
			}
			if(couponAmount==-1||type==0){
				rDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
				return rDO;
			}
			rDO.setSuccess(false);
			Optional<Activity> annerActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_ANNIVERSARY_CELEBRATE_NAME);
			if (!annerActivity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			Activity activity=annerActivity.get();
			Long templateId=0L;
			if (activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
				ActivityForAnniversaryCelebrate anniverDate = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForAnniversaryCelebrate.class,
						ActivityConstant.ACTIVITY_ANNIVERSARY_CELEBRATE_KEY);
				if(type==1){
				//88元红包
				if(couponAmount==88){
					templateId=anniverDate.getTemplateId88();
					//88元红包10点开抢
					if(!DateUtils.isDateBetween(DateUtils.getCurrentDate(),anniverDate.getEightCouponStartTime(),DateUtils.getEndTime(DateUtils.getCurrentDate()))){
						rDO.setResultCode(ResultCode.ACTIVITY_ANNIVERSARY_NO_START_ERROR);
						return rDO;
					}
					//是否领取过
					RuleBody rb = new RuleBody();
					rb.setActivityId(activity.getId());
					rb.setMemberId(memberId);
					rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
					rb.setCycleStr(activity.getId().toString()+":coupon88");
					if (!drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
						rDO.setResultCode(ResultCode.ACTIVITY_ANNIVERSARY_JUST_ONE_ERROR);
						return rDO;
					}
					//红包是否还有剩余
					int eightTotal=0;
					ActivityData activityData=activityDataMapper.selectActivityDateByActivityId(activity.getId(), null);
					if(activityData!=null){
						eightTotal=activityData.getDataGole();
					}
					if(RedisActivityClient.getReceivedCouponEightNum()>=eightTotal){
						rDO.setResultCode(ResultCode.ACTIVITY_ANNIVERSARY_HAD_END_ERROR);
						return rDO;
					}
					
					//存钱罐余额是否大于2000
					Balance balance=null;
					try {
						balance = balanceManager.synchronizedBalance(memberId, TypeEnum.BALANCE_TYPE_PIGGY);
					} catch (Exception e) {
						logger.error("同步用户存钱罐余额失败, activityId={}, memberId={}", activity.getId(),
								memberId, e);
					}
					if(balance==null||balance.getAvailableBalance().intValue()<anniverDate.getAvailableBalance().intValue()){
						rDO.setResultCode(ResultCode.OVERDUE_REPAY_PIGGY_BALANCE_LACKING);
						return rDO;
					}
					
					// 发放奖励
					RewardsBase rBase = new RewardsBase();
					CouponTemplate c = couponTemplateManager.selectByPrimaryKey(templateId);
					if(c==null){
						rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_COUPON_NOTEXITS_ERROR);
						return rDO;
					}
					rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
					rBase.setRewardName(c.getAmount().intValue() + ActivityConstant.couponDesc);
					rBase.setRewardValue(c.getAmount().intValue());
					rBase.setTemplateId(templateId);
					rb.setRewardId(templateId.toString());
					drawByPrizeDirectly.drawLottery(rb, rBase, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode());
					//站内信
					MessageClient.sendMsgForSPEngin(memberId, activity.getActivityDesc(), rBase.getRewardName());
					RedisActivityClient.incrEightAnnCelebCoupon();
					activityJuly.setCoupon88Remind(eightTotal-RedisActivityClient.getReceivedCouponEightNum());
					rDO.setResult(activityJuly);
					rDO.setSuccess(true);
					
				}else{
					RuleBody rb = new RuleBody();
					rb.setActivityId(activity.getId());
					rb.setMemberId(memberId);
					rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_COIN.getStatus());
					rb.setActivityName(activity.getActivityDesc());
					rb.setCycleStr(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate()));
					rb.setDeductRemark(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate()));
					// 校验剩余次数
					ActivityLottery lotter=new ActivityLottery();
					lotter.setActivityId(activity.getId());
					lotter.setMemberId(memberId);
					lotter.setCycleConstraint(activity.getId()+":"+memberId+":coupon");
					lotter.setRealCount(-1);
					for(int i=0;i<anniverDate.getCouponAmount().size();i++){
						if(anniverDate.getCouponAmount().get(i)==couponAmount){
							templateId=anniverDate.getTemplateId().get(i);
							break;
						}
					}
					ActivityLottery activityLottery=activityLotteryMapper.checkExistLottery(lotter);
					if(activityLottery==null||activityLottery.getRealCount()<1){
						rDO.setResultCode(ResultCode.ACTIVITY_ANNIVERSARY_INVEST_RECEIVE_ERROR);
						return rDO;
					}
					// 发放奖励
					RewardsBase rBase = new RewardsBase();
					CouponTemplate c = couponTemplateManager.selectByPrimaryKey(templateId);
					if(c==null){
						rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_COUPON_NOTEXITS_ERROR);
						return rDO;
					}
					rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
					rBase.setRewardName(c.getAmount().intValue() + ActivityConstant.couponDesc);
					rBase.setRewardValue(c.getAmount().intValue());
					rBase.setTemplateId(templateId);
					rb.setRewardId(templateId.toString());
					drawByPrizeDirectly.drawLottery(rb, rBase, "");
					activityLotteryMapper.updateByActivityAndMember(lotter);
					//站内信
					MessageClient.sendMsgForSPEngin(memberId, activity.getActivityDesc(),rBase.getRewardName());
					rDO.setResult(activityJuly);
					rDO.setSuccess(true);
					return rDO;
					
				}
				
		   }else if(type==2){
			   BigDecimal totalInvestAmount =transactionManager.getMemberTotalInvestByMemberId(memberId,activity.getStartTime(),activity.getEndTime(),anniverDate.getTotalDays());	
			   BigDecimal totalAmount=BigDecimal.ZERO;
			   int popularValueData=0;
			   for(int i=0;i<anniverDate.getCouponAmount().size();i++){
					if(anniverDate.getCouponAmount().get(i)==couponAmount){
						templateId=anniverDate.getPtemplateId().get(i);
						totalAmount=anniverDate.getTotalAmount().get(i);
						popularValueData=anniverDate.getPopularValueRealData().get(i);
						break;
					}
				}
			   //判断是否领取
			    RuleBody rb = new RuleBody();
				rb.setActivityId(activity.getId());
				rb.setMemberId(memberId);
				rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
				if(couponAmount==6){
					rb.setCycleStr("iphone7");
				}else{
					rb.setCycleStr("popularity"+popularValueData);
				}
				if (!drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
					rDO.setResultCode(ResultCode.ACTIVITY_ANNIVERSARY_HAD_RECEIVE_ERROR);
					return rDO;
				}
			   if(totalInvestAmount.compareTo(totalAmount)<0){
				   rDO.setResultCode(ResultCode.ACTIVITY_ANNIVERSARY_TOTAL_INVEST_ERROR);
				   return rDO;
			   }
			   //领取iphone7
			   if(couponAmount==6){
				   ActivityLotteryResult model=new ActivityLotteryResult();
				   model.setActivityId(activity.getId());
				   model.setMemberId(memberId);
				   model.setRemark("iphone7");
				   model.setCycleStr("iphone7");
				   int i=activityLotteryResultMapper.countNewLotteryResult(model);
				   if(i>0){
					   rDO.setResultCode(ResultCode.ACTIVITY_ANNIVERSARY_HAD_RECEIVE_ERROR);
					   return rDO;
				   }
				   activityLotteryResultMapper.insertSelective(model);
			   }
			   //else{
				 	//发放现金券
					CouponTemplate couponTemplate = couponTemplateManager.selectByPrimaryKey(templateId);
					if(couponTemplate==null){
						rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_COUPON_NOTEXITS_ERROR);
						return rDO;
					}
					RewardsBase rBase = new RewardsBase();
					rBase.setTemplateId(templateId);
					rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_ANNUALIZED.getType());
					rBase.setRewardName(couponTemplate.getAmount() + ActivityConstant.annualizedDesc);
					//rBase.setRewardValue(couponTemplate.getAmount());
					rb.setDeductRemark(memberId+"coupon"+couponTemplate.getAmount().intValue());
					drawByPrizeDirectly.drawLottery(rb, rBase, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode());
					MessageClient.sendMsgForSPEngin(memberId, activity.getActivityDesc(),rBase.getRewardName());
					 if(couponAmount!=6){
						//送人气值
						rb.setDeductRemark(activity.getActivityDesc());
						rBase.setRewardName(popularValueData + ActivityConstant.popularityDesc);
						rBase.setRewardValue(popularValueData);
						rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_POPULARITY.getType());
						rb.setDeductRemark("popularity"+popularValueData);
						drawByPrizeDirectly.drawLottery(rb, rBase, "");
						MessageClient.sendMsgForSPEngin(memberId, activity.getActivityDesc(),rBase.getRewardName());
					 }
					rDO.setSuccess(true);
			   //}
		   }
			}else{
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
				return rDO;
			}
		} catch (Exception e) {
			logger.error("【四季变换，有你相伴】-领取红包, memberId={}", memberId, e);
		}
		return rDO;
	}

	
	@Override
	public List<ActivityLottery> selectActivityLotteryByMemberId(Long memberId, String remark) throws ManagerException {
		try {
			return activityLotteryMapper.selectActivityLotteryByMemberId(memberId, remark);
		} catch (Exception e) {
			logger.error("查询用户抽奖次数失败, memberId={}", memberId, e);
			throw new ManagerException(e);
		}
	}

	@Override
	public Integer getQuickLotteryNumber(Long memberId) {
		ActivityLottery record=new ActivityLottery();
		record.setMemberId(memberId);
		record.setRemark(ActivityConstant.DIRECT_COUNT_LOTTERY_KEY);
		return activityLotteryMapper.sumLotteryNumByMemberId(record);
	}

	@Override
	public ResultDO<ActivityForAnniversaryRetrun> receiveRewardAnniversary(
			Long memberId, int popularValue, int chip) throws ManagerException{
		ResultDO<ActivityForAnniversaryRetrun> rDO = new ResultDO<ActivityForAnniversaryRetrun>();
		ActivityForAnniversaryRetrun returnModel=new ActivityForAnniversaryRetrun();
		try {
			if (memberId == null) {
				rDO.setResultCode(ResultCode.ACTIVITY_REPORT_IS_NOT_LOGIN);
				return rDO;
			}
			if(popularValue==-1||chip==0){
				rDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
				return rDO;
			}
			rDO.setSuccess(false);
			Optional<Activity> annerActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_ANNIVERSARY_CELEBRATE_NAME);
			
			Optional<Activity> annerActivity30 = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_ANNIVERSARY_CELEBRATE_GIFT_NAME);
			
			if (!annerActivity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			Activity activity=annerActivity.get();
			
			Activity activity30=annerActivity30.get();
			int postion=chip;
			if (activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
				
				RuleBody rb = new RuleBody();
				rb.setActivityId(activity.getId());
			 	rb.setMemberId(memberId);
				rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
				rb.setCycleStr(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+":reward");
				rb.setDeductRemark(memberId+":"+DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+":reward");
				rb.setRewardsAvailableNum(5);
				rb.setRewardsPoolMaxNum(5);
				rb.setDeductValue(popularValue);
				rb.setActivityName(ActivityConstant.ACTIVITY_ANNIVERSARY_CELEBRATE_NAME);
				// 校验是否参加过
				if (!drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
					rDO.setResultCode(ResultCode.ACTIVITY_ANNIVERSARY_NO_CHANCE_ERROR);
					return rDO;
				}
				if(popularValue==10){
					rb.setActivityId(activity30.getId());
					// 校验是否参加过
					if (!drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
						rDO.setResultCode(ResultCode.ACTIVITY_ANNIVERSARY_NO_CHANCE_ERROR);
						return rDO;
					}
					rb.setActivityId(activity.getId());
				}
				
				if(popularValue==30){
					rb.setActivityId(activity30.getId());
					// 校验是否参加过
					if (!drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
						rDO.setResultCode(ResultCode.ACTIVITY_ANNIVERSARY_NO_CHANCE_ERROR);
						return rDO;
					}
				}
				//校验当前人气值是否够扣
				Balance balance = balanceManager.queryBalance(memberId, TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY);
				if(balance == null||balance.getAvailableBalance().intValue()<popularValue){
					rDO.setResultCode(ResultCode.ACTIVITY_POPULARITY__ERROR);
					return rDO;
				}
				rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_POPULAR.getStatus());
				if (drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_POPULARITY.getCode())) {
					// 抽奖
					RewardsBodyForProbility rfp = (RewardsBodyForProbility) drawByProbability.drawLottery(rb, chip,
							TypeEnum.ACTIVITY_LOTTERY_VALIDATE_POPULARITY.getCode());
					ActivityLotteryResult model=new ActivityLotteryResult();
					model.setRewardResult(rfp.getRewardCode());
					model.setActivityId(rb.getActivityId());
					model.setMemberId(memberId);
					model.setRemark(memberId+":"+DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+":reward");
					model.setChip(postion);
					activityLotteryResultMapper.updateLotteryRewardResultByActivityId(model);
					Long sourceId=0L;
					if(popularValue==10){
						sourceId=activity.getId();
					}else{
						sourceId=activity30.getId();
					}
					popularityInOutLogMapper.updatePopularityByMemberId(activity.getActivityDesc()+"消耗人气值", memberId, model.getRemark(),sourceId);
					returnModel.setRewardResult(rfp.getRewardCode());
					returnModel.setPosition(postion);
					rDO.setResult(returnModel);
					rDO.setSuccess(true);
					
				}else{
					rDO.setResultCode(ResultCode.ACTIVITY_POPULARITY__ERROR);
					return rDO;
				}
			}else{
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
				return rDO;
			}
			
			
		} catch (Exception e) {
			logger.error("【四季变换，有你相伴】-翻牌赢豪礼失败, memberId={}", memberId, e);
		}
		return rDO;
	}
	
	@Override
	public void signSendCoupon(Long memberId) throws ManagerException{
			Optional<Activity> signActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_SIGN_SEND_COUPON_NAME);
			if (signActivity.isPresent()) {
			Activity activity=signActivity.get();
			Long templateId=0L;
			if (activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
				ActivityForJulyBet anniverDate = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForJulyBet.class,
						ActivityConstant.ACTIVITY_SIGN_SEND_COUPON_KEY);
				try {
					templateId=anniverDate.getTemplateId();
					//是否领取过
					RuleBody rb = new RuleBody();
					rb.setActivityId(activity.getId());
					rb.setMemberId(memberId);
					rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
					rb.setCycleStr(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate()));
					if (drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
						// 发放奖励
						RewardsBase rBase = new RewardsBase();
						CouponTemplate c = couponTemplateManager.selectByPrimaryKey(templateId);
						if(c!=null){
							rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
							rBase.setRewardName(c.getAmount().intValue() + ActivityConstant.couponDesc);
							rBase.setRewardValue(c.getAmount().intValue());
							rBase.setTemplateId(templateId);
							rb.setRewardId(templateId.toString());
							drawByPrizeDirectly.drawLottery(rb, rBase, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode());
							//站内信
							//MessageClient.sendMsgForSPEngin(memberId, activity.getActivityDesc(), rBase.getRewardName());
						}
						
					}
			} catch (Exception e) {
				logger.error("【签到送现金券】失败, memberId={}", memberId, e);
			}
		}
	}
	}			
	@Override
	public ResultDO<ActivityForDouble> doubleReceiveCoupon(Long memberId) throws ManagerException{
		ResultDO<ActivityForDouble> rDO = new ResultDO<ActivityForDouble>();
		ActivityForDouble activityDouble=new ActivityForDouble();
		try {
			if(memberId == null) {
				rDO.setResultCode(ResultCode.ACTIVITY_REPORT_IS_NOT_LOGIN);
				return rDO;
			}
			rDO.setSuccess(false);
			Optional<Activity> annerActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_DOUBLE_NAME);
			if (!annerActivity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			Activity activity=annerActivity.get();
			Long templateId=0L;
			if (activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
				ActivityForDouble anniverDate = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForDouble.class,
						ActivityConstant.ACTIVITY_DOUBLE_KEY);
					//10点抢
					int i=0;
					Integer param=0;
					if(DateUtils.isDateBetween(DateUtils.getCurrentDate(),DateUtils.getSpecialTime(DateUtils.getCurrentDate(), anniverDate.getTen()),DateUtils.getTimes(DateUtils.getSpecialTime(DateUtils.getCurrentDate(), anniverDate.getFifTeen())))){
						try {
						i=LotteryUtil.getLotteryResult(anniverDate.getProbabilityListTen());
						}catch (Exception e) {
							logger.error("【双旦狂欢惠】 抢红包 获取概率失败, memberId={}", memberId, e);
						}
						param=anniverDate.getTen();
						if(i==0){
							templateId=anniverDate.getCouponTemplate().get(0);
						}else if(i==1){
							templateId=anniverDate.getCouponTemplate().get(1);
						}else{
							
						}
					}else if(DateUtils.isDateBetween(DateUtils.getCurrentDate(),DateUtils.getSpecialTime(DateUtils.getCurrentDate(), anniverDate.getFifTeen()),DateUtils.getEndTime(DateUtils.getCurrentDate()))){
						try {
						i=LotteryUtil.getLotteryResult(anniverDate.getProbabilityListFifTeen());
						}catch (Exception e) {
							logger.error("【双旦狂欢惠】 抢红包 获取概率失败, memberId={}", memberId, e);
						}
						param=anniverDate.getFifTeen();
						if(i==0){
							templateId=anniverDate.getCouponTemplate().get(2);
						}else if(i==1){
							templateId=anniverDate.getCouponTemplate().get(3);
						}else{
							
						}
					}else{
						rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
						return rDO;
					}
					//是否领取过
					RuleBody rb = new RuleBody();
					rb.setActivityId(activity.getId());
					rb.setMemberId(memberId);
					rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
					rb.setCycleStr(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+":double");
					if (!drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
						rDO.setResultCode(ResultCode.ACTIVITY_ANNIVERSARY_HAD_RECEIVE_ERROR);
						return rDO;
					}
					//红包是否还有剩余
					if(RedisActivityClient.getReceivedCouponTotal(param)>=anniverDate.getTotalRed()){
						rDO.setResultCode(ResultCode.ACTIVITY_ANNIVERSARY_HAD_END_ERROR);
						return rDO;
					}
					// 发放奖励
					RewardsBase rBase = new RewardsBase();
					CouponTemplate c = couponTemplateManager.selectByPrimaryKey(templateId);
					if(c==null){
						rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_COUPON_NOTEXITS_ERROR);
						return rDO;
					}
					rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
					rBase.setRewardName(c.getAmount().intValue() + ActivityConstant.couponDesc);
					rBase.setRewardValue(c.getAmount().intValue());
					rBase.setTemplateId(templateId);
					rb.setRewardId(templateId.toString());
					drawByPrizeDirectly.drawLottery(rb, rBase, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode());
					//站内信
					MessageClient.sendMsgForSPEngin(memberId, activity.getActivityDesc(), rBase.getRewardName());
					RedisActivityClient.incrTotalRedForDouble(param);
					activityDouble.setCouponAmount(c.getAmount());
					activityDouble.setRedRemind(anniverDate.getTotalRed()-RedisActivityClient.getReceivedCouponTotal(param));
					rDO.setResult(activityDouble);
					rDO.setSuccess(true);
			}else{
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
				return rDO;
			}
		} catch (Exception e) {
			logger.error("【双旦狂欢惠】普天同庆 抢红包, memberId={}", memberId, e);
		}
		return rDO;
	}

	@Override
	public ResultDO<ActivityForNewYear> newYearLuckyMoney(Long memberId,Long templateId) throws ManagerException {
		ResultDO<ActivityForNewYear> rDO = new ResultDO<ActivityForNewYear>();
		ActivityForNewYear activityForNewYear=new ActivityForNewYear();
		try {
			if(memberId == null) {
				rDO.setResultCode(ResultCode.ACTIVITY_REPORT_IS_NOT_LOGIN);
				return rDO;
			}
			rDO.setSuccess(false);
			Optional<Activity> annerActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_NEWYEAR);
			if (!annerActivity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			Activity activity=annerActivity.get();
			if (activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
				ActivityForNewYear newyear = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForNewYear.class,
						ActivityConstant.ACTIVITY_NEWYEAR_KEY);

				String cycleConstraint = activity.getId() + "-" + memberId + "-" + templateId+":LuckyMoney";
				RuleBody rb = new RuleBody();
				rb.setActivityId(activity.getId());
				rb.setMemberId(memberId);
				rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_COIN.getStatus());
				rb.setCycleStr(cycleConstraint);

				Map<String, Object> paraMap = new HashMap<String, Object>();
				paraMap.put("activityId", activity.getId());
				paraMap.put("memberId", memberId);
				paraMap.put("cycleConstraint", cycleConstraint);
				ActivityLottery al= activityLotteryMapper.selectByMemberActivity(paraMap);

				boolean truetemplateId=false;
				String[] strings= newyear.getLuckyMoneyTemplateIds().split(",");
				for (String s:strings) {
					if (s.equals(templateId.toString())){
						truetemplateId=true;
					}
				}
				//判断传入优惠券模板id是否在活动中
				if (!truetemplateId){
					rDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM);
					return rDO;
				}

				if (al!=null&&al.getRealCount()<1){
					rDO.setResultCode(ResultCode.ACTIVITY_LUCKYMONEY__ERROR);
					return rDO;
				}

				// 发放奖励
				RewardsBase rBase = new RewardsBase();
				CouponTemplate c = couponTemplateManager.selectByPrimaryKey(templateId);
				if(c==null){
					rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_COUPON_NOTEXITS_ERROR);
					return rDO;
				}
				rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
				rBase.setRewardName(c.getAmount().intValue() + ActivityConstant.couponDesc);
				rBase.setRewardValue(c.getAmount().intValue());
				rBase.setTemplateId(templateId);
				rb.setRewardId(templateId.toString());

				drawByPrizeDirectly.drawLottery(rb, rBase,"");
				//更新领取红包资格
				if (al==null){
					ActivityLottery model = new ActivityLottery();
					model.setActivityId(activity.getId());
					model.setMemberId(memberId);
					model.setTotalCount(1);
					model.setRealCount(0);
					model.setCycleConstraint(cycleConstraint);
					activityLotteryMapper.insertSelective(model);
				}else {
					al.setTotalCount(0);
					al.setRealCount(-1);
					activityLotteryMapper.updateByActivityAndMember(al);
				}
				activityForNewYear.setLuckyMoneyAmount(c.getAmount());
				rDO.setResult(activityForNewYear);
				rDO.setSuccess(true);
			}else{
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
				return rDO;
			}
		} catch (Exception e) {
			logger.error("【鸡年新年大吉】喜领压岁钱 抢红包, memberId={}", memberId, e);
		}
		return rDO;
	}

	@Override
	public ResultDO<ActivityForNewYear> newYearLuckyBag(Long memberId) throws ManagerException {
		ResultDO<ActivityForNewYear> rDO = new ResultDO<ActivityForNewYear>();
		ActivityForNewYear activityForNewYear=new ActivityForNewYear();
		try {
			if(memberId == null) {
				rDO.setResultCode(ResultCode.ACTIVITY_REPORT_IS_NOT_LOGIN);
				return rDO;
			}
			rDO.setSuccess(false);
			Optional<Activity> annerActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_NEWYEAR);
			if (!annerActivity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			Activity activity=annerActivity.get();
			if (activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
				ActivityForNewYear newyear = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForNewYear.class,
						ActivityConstant.ACTIVITY_NEWYEAR_KEY);

				String cycleConstraint = activity.getId() + "-" + memberId + ":LuckyBag";
				RuleBody rb = new RuleBody();

				rb.setActivityId(activity.getId());
				rb.setMemberId(memberId);
				rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_COIN.getStatus());
				rb.setCycleStr(cycleConstraint);

				Map<String, Object> paraMap = new HashMap<String, Object>();
				paraMap.put("activityId", activity.getId());
				paraMap.put("memberId", memberId);
				paraMap.put("cycleConstraint", cycleConstraint);
				ActivityLottery al= activityLotteryMapper.selectByMemberActivity(paraMap);

				//没有记录发放登录福袋
				if (al!=null){
					rDO.setResultCode(ResultCode.ACTIVITY_LUCKYBAG__ERROR);
					return rDO;
				}
				String[] strings= newyear.getLuckyBagTemplateIds().split(",");
				for (String s:strings) {
					if (!StringUtils.isEmpty(s)){
						// 发放奖励
						RewardsBase rBase = new RewardsBase();
						CouponTemplate c = couponTemplateManager.selectByPrimaryKey(Long.parseLong(s));
						if(c==null){
							rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_COUPON_NOTEXITS_ERROR);
							return rDO;
						}
						rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
						rBase.setRewardName(c.getAmount() + ActivityConstant.annualizedUnit + ActivityConstant.annualizedDesc);
						rBase.setTemplateId(Long.parseLong(s));
						rb.setRewardId(s);
						drawByPrizeDirectly.drawLottery(rb, rBase,"");
					}
				}
				//登录福袋发放完毕记录已领取
				ActivityLottery model = new ActivityLottery();
				model.setActivityId(activity.getId());
				model.setMemberId(memberId);
				model.setTotalCount(1);
				model.setRealCount(1);
				model.setCycleConstraint(cycleConstraint);
				activityLotteryMapper.insertSelective(model);
				rDO.setResult(activityForNewYear);
				rDO.setSuccess(true);
			}else{
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
				return rDO;
			}
		} catch (Exception e) {
			logger.error("【鸡年新年大吉】喜领压岁钱 抢红包, memberId={}", memberId, e);
		}
		return rDO;
	}

	@Override
	public ResultDO<Activity> newYearGrab(Long memberId) throws ManagerException {
		ResultDO<Activity> rDO = new ResultDO<Activity>();
		try {
			if(memberId == null) {
				rDO.setResultCode(ResultCode.ACTIVITY_REPORT_IS_NOT_LOGIN);
				return rDO;
			}
			rDO.setSuccess(false);
			Optional<Activity> annerActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_GRABBAG);
			if (!annerActivity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			Activity activity=annerActivity.get();
			if (activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
				ActivityForNewYearGrab grab = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForNewYearGrab.class,
						ActivityConstant.ACTIVITY_GRABBAG_KEY);
				try {
					//标示用户是否已经抢过key
					String key = RedisConstant.REDIS_KEY_GRAB+RedisConstant.REDIS_SEPERATOR+memberId;
					//已抢红包的数量
					String grabedCount=RedisConstant.REDIS_KEY_GRAB+RedisConstant.REDIS_SEPERATOR+RedisConstant.REDIS_KEY_GRAB_COUNT;
					//记录用户抢成功列表key
					String grabedMember=RedisConstant.REDIS_KEY_GRAB+RedisConstant.REDIS_SEPERATOR+RedisConstant.REDIS_KEY_GRAB_MEMBER;

					if (RedisManager.isExit(key)){
                        rDO.setResultCode(ResultCode.ACTIVITY_GRAB_ERROR);
                        return rDO;
                    }
					//已抢红包数量key不存在进行真实数字校验
					if (!RedisManager.isExit(grabedCount)){
						Integer count= activityLotteryMapper.queryCountByActivityId(activity.getId());
						RedisManager.hset(grabedCount,RedisConstant.REDIS_KEY_GRAB_COUNT,count.toString());
					}
					//已抢压岁钱数目+1 过期时间设置保存一天
					Long grabdeCount= RedisManager.hincrBy(grabedCount,RedisConstant.REDIS_KEY_GRAB_COUNT,1L);
					RedisManager.expire(grabedCount,60*60*24);
					if (grabdeCount>grab.getGrabCount()){
						rDO.setResultCode(ResultCode.ACTIVITY_GRAB_COUNT_ERROR);
						return rDO;
					}
					RedisManager.set(key,"1");
					RedisManager.expire(key,60*60*24);
					//保存用户中奖列表有效期设定为7天
					RedisManager.rpush(grabedMember,memberId.toString());
					RedisManager.expire(grabedMember,60*60*24*7);
				} catch (Exception e) {
					logger.error("【鸡年新年大吉】喜领压岁钱 抢红包redis出现异常", e);
				}
				//插入抽奖表
				try {
					ActivityLottery model = new ActivityLottery();
					String cycleConstraint = activity.getId() + "-" + memberId + ":newYearGrab";
					model.setActivityId(activity.getId());
					model.setMemberId(memberId);
					model.setTotalCount(1);
					model.setRealCount(1);
					model.setCycleConstraint(cycleConstraint);
					model.setCreateTime(new Date());
					model.setUpdateTime(new Date());
					activityLotteryMapper.insertSelective(model);
				} catch (Exception e) {
					rDO.setResultCode(ResultCode.ACTIVITY_GRAB_ERROR);
					return rDO;
				}
				rDO.setResult(activity);
				rDO.setSuccess(true);
			}else{
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
				return rDO;
			}
		} catch (Exception e) {
			logger.error("【鸡年新年大吉】喜领压岁钱 抢红包, memberId={}", memberId, e);
		}
		return rDO;
	}

	@Override
	public List<ActivityLottery> queryActivityLotteryByActivityId(Long activityId) {
		return activityLotteryMapper.queryActivityLotteryByActivityId(activityId);
	}

	@Override
	public int queryCountByActivityId(Long activityId) {
		return activityLotteryMapper.queryCountByActivityId(activityId);
	}

	@Override
	public ResultDO<Activity> lanternFestival() throws ManagerException {
		ResultDO<Activity> rDO = new ResultDO<Activity>();
		try {
			rDO.setSuccess(false);
			Optional<Activity> annerActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_LANTERNFESTIVAL);
			if (!annerActivity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			Activity activity=annerActivity.get();
			rDO.setResult(activity);
			if (activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
				rDO.setSuccess(true);
				return rDO;
			}else{
				if (new Date().before(activity.getStartTime())){
					rDO.setResultCode(ResultCode.ACTIVITY_BIRTHDAY_IS_NOT_START);
					return rDO;
				}
				if (new Date().after(activity.getEndTime())){
					rDO.setResultCode(ResultCode.ACTIVITY_YET_END_ERROR);
					return rDO;
				}
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
				return rDO;
			}
		} catch (Exception e) {
			logger.error("【元宵情人节活动】异常", e);
		}
		return rDO;
	}

	@Override
	public ResultDO<Activity> newPreferential() throws ManagerException {
		ResultDO<Activity> rDO = new ResultDO<Activity>();
		try {
			rDO.setSuccess(false);
			Optional<Activity> annerActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_NEWPREFERENTIAL);
			if (!annerActivity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			Activity activity=annerActivity.get();
			rDO.setResult(activity);
			if (activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
				rDO.setSuccess(true);
				return rDO;
			}else{
				if (new Date().before(activity.getStartTime())){
					rDO.setResultCode(ResultCode.ACTIVITY_BIRTHDAY_IS_NOT_START);
					return rDO;
				}
				if (new Date().after(activity.getEndTime())){
					rDO.setResultCode(ResultCode.ACTIVITY_YET_END_ERROR);
					return rDO;
				}
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
				return rDO;
			}
		} catch (Exception e) {
			logger.error("【新品特惠】异常", e);
		}
		return rDO;
	}

	@Override
	public ResultDO<Activity> valentineDay(Long memberId) throws ManagerException {
		ResultDO<Activity> rDO = new ResultDO<Activity>();
		try {
			rDO.setSuccess(false);
			Optional<Activity> annerActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_LANTERNFESTIVAL);
			if (!annerActivity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			Activity activity=annerActivity.get();
			if (activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
				ActivityForLanternFestivalAndValentine lanternFestivalAndValentine = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForLanternFestivalAndValentine.class,
						ActivityConstant.ACTIVITY_LANTERNFESTIVAL_KEY);
				if (DateUtils.getStrFromDate(new Date(),"yyyy-MM-dd").equals(lanternFestivalAndValentine.getValentineDate())){
					String cycleConstraint = activity.getId() + "-" + memberId + ":FestivalAndValentine";
					RuleBody rb = new RuleBody();

					rb.setActivityId(activity.getId());
					rb.setMemberId(memberId);
					rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_COIN.getStatus());
					rb.setCycleStr(cycleConstraint);
					String[] strings= lanternFestivalAndValentine.getValentineTemplateIds().split(",");
					for (String s:strings) {
						if (!StringUtils.isEmpty(s)){
							// 发放奖励
							RewardsBase rBase = new RewardsBase();
							CouponTemplate c = couponTemplateManager.selectByPrimaryKey(Long.parseLong(s));
							if(c==null){
								rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_COUPON_NOTEXITS_ERROR);
								continue;
							}
							rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_ANNUALIZED.getType());
							rBase.setRewardName(c.getAmount() + ActivityConstant.annualizedUnit + ActivityConstant.annualizedDesc);
							rBase.setTemplateId(Long.parseLong(s));
							rb.setRewardId(s);
							drawByPrizeDirectly.drawLottery(rb, rBase,"");
							//发送站内信
							MessageClient.sendMsgForSPEngin(memberId, ActivityConstant.ACTIVITY_LANTERNFESTIVAL, rBase.getRewardName());
						}
					}
				}
				rDO.setResult(activity);
				rDO.setSuccess(true);
				return rDO;
			}else{
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
				return rDO;
			}
		} catch (Exception e) {
			logger.error("【元宵情人节活动】异常", e);
		}
		return rDO;
	}


	
	@Override
	public ResultDO<ActivityForFiveBillionRetrun> receiveLuckBag(Long memberId,
			int couponAmount) throws ManagerException {
		ResultDO<ActivityForFiveBillionRetrun> rDO = new ResultDO<ActivityForFiveBillionRetrun>();
		ActivityForFiveBillionRetrun returnModel=new ActivityForFiveBillionRetrun();
		try {
			if (memberId == null) {
				rDO.setResultCode(ResultCode.ACTIVITY_REPORT_IS_NOT_LOGIN);
				return rDO;
			}
			if(couponAmount==-1){
				rDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
				return rDO;
			}
			rDO.setSuccess(false);
			Optional<Activity> fiveActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_FIVEBILLION_NAME);
			
			if (!fiveActivity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			Activity activity=fiveActivity.get();
			if (activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
				
				ActivityForFiveBillion fiveBillion = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForFiveBillion.class,
						ActivityConstant.ACTIVITY_FIVEBILLION_KEY);
				
				RuleBody rb = new RuleBody();
				rb.setActivityId(activity.getId());
			 	rb.setMemberId(memberId);
				rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
				rb.setCycleStr(activity.getId()+":"+memberId);
				rb.setDeductRemark(activity.getId().toString());
				rb.setActivityName(ActivityConstant.ACTIVITY_ANNIVERSARY_CELEBRATE_NAME);
				// 校验是否参加过
				if (!drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
					rDO.setResultCode(ResultCode.ACTIVITY_ANNIVERSARY_HAD_RECEIVE_ERROR);
					return rDO;
				}
				Long templateId=0L;
				for(int i=0;i<fiveBillion.getCouponAmount().size();i++){
					if(fiveBillion.getCouponAmount().get(i)==couponAmount){
						templateId=fiveBillion.getTemplateId().get(i);
						break;
					}
				}
				CouponTemplate couponTemplate = couponTemplateManager.selectByPrimaryKey(templateId);
				if(couponTemplate==null){
					rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_COUPON_NOTEXITS_ERROR);
					return rDO;
				}
				//发送现金券
				RewardsBase rBase = new RewardsBase();
				rBase.setTemplateId(templateId);
				rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
				rBase.setRewardName(couponTemplate.getAmount() + ActivityConstant.couponDesc);
				rBase.setRewardValue(couponTemplate.getAmount().intValue());
				rb.setDeductRemark(memberId+":luckBag");
				rb.setRewardId(templateId.toString());
				rb.setDeductValue(couponAmount);
				drawByPrizeDirectly.drawLottery(rb, rBase, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode());
				//发送收益券
				couponTemplate = couponTemplateManager.selectByPrimaryKey(fiveBillion.getTemplateId15());
				if(couponTemplate==null){
					rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_COUPON_NOTEXITS_ERROR);
					return rDO;
				}
				rBase.setTemplateId(fiveBillion.getTemplateId15());
				rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_ANNUALIZED.getType());
				rBase.setRewardName(couponTemplate.getAmount() + ActivityConstant.annualizedDesc);
				rb.setRewardId(rBase.getTemplateId().toString());
				drawByPrizeDirectly.drawLottery(rb, rBase, "");
				returnModel.setReward(couponAmount);
				rDO.setSuccess(true);
				rDO.setResult(returnModel);
				return rDO;
				//MessageClient.sendMsgForSPEngin(memberId, activity.getActivityDesc(),rBase.getRewardName());
			}else{
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
				return rDO;
			}
		} catch (Exception e) {
			logger.error("【福临50亿】-领取福袋失败, memberId={}", memberId, e);
		}
		return rDO;
	}

	/**
	 * 50亿活动 抽奖
	 */
	@Override
	public ResultDO<ActivityForFiveBillionRetrun> lotteryLuckBoth(Long memberId,int type)
			throws ManagerException {
		ResultDO<ActivityForFiveBillionRetrun> rDO = new ResultDO<ActivityForFiveBillionRetrun>();
		ActivityForFiveBillionRetrun model=new ActivityForFiveBillionRetrun();
		Optional<Activity> fiveActivity = LotteryContainer.getInstance()
				.getActivityByName(ActivityConstant.ACTIVITY_FIVEBILLION_NAME);
		
		if (!fiveActivity.isPresent()) {
			rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
			return rDO;
		}
		if (memberId == null) {
			rDO.setResultCode(ResultCode.ACTIVITY_REPORT_IS_NOT_LOGIN);
			return rDO;
		}
		if(type==-1){
			rDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
			return rDO;
		}
		Activity activity=fiveActivity.get();
		if (activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
			ActivityForFiveBillion fiveBillion = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForFiveBillion.class,
					ActivityConstant.ACTIVITY_FIVEBILLION_KEY);
			//校验是否有抽奖机会
			ActivityLotteryResultNew lottery=new ActivityLotteryResultNew();
			lottery.setActivityId(activity.getId());
			lottery.setMemberId(memberId);
			lottery.setRemark("luckLotteryBag");
			lottery.setLotteryStatus(0);
			int count=activityLotteryResultNewMapper.countNewLotteryResult(lottery);
			Long templateId=0L;
			if(count<1){
				rDO.setResultCode(ResultCode.PROJECT_DIRECT_LOTTERY_NUM_ERROR);
				return rDO;
			}
			String key = ActivityConstant.ACTIVITY_FIVEBILLION_MYLOTTERY_KEY;
			RewardsBase rBase = new RewardsBase();
			RuleBody rb = new RuleBody();
			ActivityLotteryResultNew lotteryNumber=new ActivityLotteryResultNew();
			int j=-1;
			//福禄双全抽奖
			if(type==1){
				key=key+":luckBoth";
				lotteryNumber.setNumber(1);
				j=LotteryUtil.getLotteryResult(fiveBillion.getProbabilityLuckBoth());
				//实物奖品
				if(j==7){
					ActivityLotteryResultNew alr=new ActivityLotteryResultNew();
					alr.setMemberId(memberId);
					alr.setActivityId(activity.getId());
					alr.setRewardInfo(fiveBillion.getLuckBothRewardInfo().get(j));
					alr.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_GIFT.getType());
					alr.setRemark("luckBoth");
					alr.setChip(j);
					alr.setStatus(1);
					alr.setCycleStr(activity.getId()+":luckBoth");
					int i=activityLotteryResultNewMapper.insertSelective(alr);
					if(i>0){
						//更新抽奖次数
						lotteryNumber.setActivityId(activity.getId());
						lotteryNumber.setMemberId(memberId);
						lotteryNumber.setRemark("luckLotteryBag");
						lotteryNumber.setLotteryStatus(0);
						//int update=activityLotteryResultNewMapper.updateLotteryResultStatus(lotteryNumber);
						List<ActivityLotteryResultNew> r=activityLotteryResultNewMapper.getLotteryResultBySelective(lotteryNumber);
						int update=0;
						if(Collections3.isNotEmpty(r)){
							for(ActivityLotteryResultNew n:r){
								update=activityLotteryResultNewMapper.updateLotteryById(n.getId());
							}
						}
						if(update>0){
							model.setReward(j);
							model.setRewardInfo(fiveBillion.getLuckBothRewardInfo().get(j));
							rDO.setResult(model);
							RedisManager.removeObject(key);
							rDO.setSuccess(true);
							return rDO;
						}
					}
				//人气值
				}else if(j==2){
					rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_POPULARITY.getType());
					rBase.setRewardValue(fiveBillion.getLuckBothTemplateId().get(j).intValue());
				}else{
					templateId=fiveBillion.getLuckBothTemplateId().get(j);
					CouponTemplate couponTemplate = couponTemplateManager.selectByPrimaryKey(templateId);
					if(couponTemplate==null){
						rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_COUPON_NOTEXITS_ERROR);
						return rDO;
					}
					if(couponTemplate.getCouponType()==TypeEnum.COUPON_TYPE_CASH.getType()){
						rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
					}else{
						rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_ANNUALIZED.getType());
					}
				}
				rBase.setRewardName(fiveBillion.getLuckBothRewardInfo().get(j));
				rb.setDeductRemark("luckBoth");
				rb.setCycleStr(activity.getId()+":luckBoth");
			//福禄天齐抽奖
			}else if(type==2){
				key=key+":luckMonkey";
				if(count<fiveBillion.getNumber()){
					rDO.setResultCode(ResultCode.PROJECT_DIRECT_LOTTERY_NUM_ERROR);
					return rDO;
				}
				j=LotteryUtil.getLotteryResult(fiveBillion.getProbabilityLuckMonkey());
				if(j==0||j==2||j==5){
					ActivityLotteryResultNew alr=new ActivityLotteryResultNew();
					alr.setMemberId(memberId);
					alr.setActivityId(activity.getId());
					alr.setRewardInfo(fiveBillion.getLuckMonkeyRewardInfo().get(j));
					alr.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_GIFT.getType());
					alr.setRemark("luckMonkey");
					alr.setChip(j);
					alr.setStatus(1);
					alr.setRewardId(memberId.toString());
					alr.setCycleStr(activity.getId()+":luckMonkey");
					int i=activityLotteryResultNewMapper.insertSelective(alr);
					if(i>0){
						//更新抽奖次数
						lotteryNumber.setActivityId(activity.getId());
						lotteryNumber.setMemberId(memberId);
						lotteryNumber.setRemark("luckLotteryBag");
						lotteryNumber.setLotteryStatus(0);
						lotteryNumber.setNumber(fiveBillion.getNumber());
						//int update=activityLotteryResultNewMapper.updateLotteryResultStatus(lotteryNumber);
						List<ActivityLotteryResultNew> rr=activityLotteryResultNewMapper.getLotteryResultBySelective(lotteryNumber);
						int update=0;
						if(Collections3.isNotEmpty(rr)){
							for(ActivityLotteryResultNew n:rr){
								update=activityLotteryResultNewMapper.updateLotteryById(n.getId());
							}
						}
						if(update>0){
							model.setReward(j);
							model.setRewardInfo(fiveBillion.getLuckBothRewardInfo().get(j));
							rDO.setResult(model);
							rDO.setSuccess(true);
							RedisManager.removeObject(key);
							return rDO;
						}
					}
				}else if(j==7){
					rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_POPULARITY.getType());
					rBase.setRewardValue(fiveBillion.getLuckMonkeyTemplateId().get(j).intValue());
				}else{
					templateId=fiveBillion.getLuckMonkeyTemplateId().get(j);
					CouponTemplate couponTemplate = couponTemplateManager.selectByPrimaryKey(templateId);
					if(couponTemplate==null){
						rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_COUPON_NOTEXITS_ERROR);
						return rDO;
					}
					if(couponTemplate.getCouponType()==TypeEnum.COUPON_TYPE_CASH.getType()){
						rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
					}else{
						rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_ANNUALIZED.getType());
					}
				}
				rBase.setRewardName(fiveBillion.getLuckMonkeyRewardInfo().get(j));
				rb.setDeductRemark("luckMonkey");
				rb.setCycleStr(activity.getId()+":luckMonkey");
				lotteryNumber.setNumber(fiveBillion.getNumber());
			}else{
				rDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
				return rDO;
			}
			rb.setActivityId(activity.getId());
		 	rb.setMemberId(memberId);
		 	rb.setRewardId(memberId.toString());
			rBase.setTemplateId(templateId);
			rb.setDeductValue(j);
			try {
				//发送奖励
				drawByPrizeDirectly.drawLotteryNew(rb, rBase,"");
				//更新抽奖次数
				lotteryNumber.setActivityId(activity.getId());
				lotteryNumber.setMemberId(memberId);
				lotteryNumber.setRemark("luckLotteryBag");
				lotteryNumber.setLotteryStatus(0);
				//int update=activityLotteryResultNewMapper.updateLotteryResultStatus(lotteryNumber);
				List<ActivityLotteryResultNew> g=activityLotteryResultNewMapper.getLotteryResultBySelective(lotteryNumber);
				int update=0;
				if(Collections3.isNotEmpty(g)){
					for(ActivityLotteryResultNew n:g){
						update=activityLotteryResultNewMapper.updateLotteryById(n.getId());
					}
				}
				if(update>0){
					model.setReward(j);
					model.setRewardInfo(rBase.getRewardName());
					rDO.setResult(model);
					rDO.setSuccess(true);
					RedisManager.removeObject(key);
					return rDO;
				}
			} catch (Exception e) {
				logger.error("【福临50亿】-抽奖失败, memberId={}", memberId, e);
			}
			return rDO;
		}else{
			rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
			return rDO;
		}
	}
	
	
	@Override
	public Page<ActivityForFiveBillionRetrun> activityLotteryResultListByPage(ActivityLotteryResultQuery activityLotteryResultQuery) throws ManagerException {
		try {
			List<ActivityForFiveBillionRetrun> lotteryRecord=Lists.newArrayList();
			int count = activityLotteryResultNewMapper.activityLotteryResultForPaginTotalCount(activityLotteryResultQuery);
			if (count > 0) {
				List<ActivityLotteryResultNew> listLuck = activityLotteryResultNewMapper.activityLotteryResultForPage(activityLotteryResultQuery);
				if(Collections3.isNotEmpty(listLuck)){
					for(ActivityLotteryResultNew bean :listLuck){
						ActivityForFiveBillionRetrun acInfo=new ActivityForFiveBillionRetrun();
						acInfo.setRewardInfo(bean.getRewardInfo());
						acInfo.setHappenTime(bean.getCreateTime());
						lotteryRecord.add(acInfo);
					}
				}
			}
			Page<ActivityForFiveBillionRetrun> page = new Page<ActivityForFiveBillionRetrun>();
			page.setiTotalDisplayRecords(count);
			page.setPageNo(activityLotteryResultQuery.getCurrentPage());
			page.setiDisplayLength(activityLotteryResultQuery.getPageSize());
			page.setiTotalRecords(count);
			page.setData(lotteryRecord);
			return page;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	

	@Override
	public ResultDO<Activity> subscription(Long memberId) {
		ResultDO<Activity> rDO = new ResultDO<Activity>();
		try {
			rDO.setSuccess(false);
			Optional<Activity> annerActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_SUBSCRIPTION);
			if (!annerActivity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			Activity activity=annerActivity.get();
			rDO.setResult(activity);
			if (activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
				ActivityForSubscription subscription = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForSubscription.class,
						ActivityConstant.ACTIVITY_SUBSCRIPTION_KEY);
				String cycleConstraint = activity.getId() + "-" + memberId + "-" + DateUtils.getDateStrFromDate(new Date()) + ":Subscription";
				RuleBody rb = new RuleBody();

				rb.setActivityId(activity.getId());
				rb.setMemberId(memberId);
				rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_COIN.getStatus());
				rb.setCycleStr(cycleConstraint);

				Map<String, Object> paraMap = new HashMap<String, Object>();
				paraMap.put("activityId", activity.getId());
				paraMap.put("memberId", memberId);
				paraMap.put("cycleConstraint", cycleConstraint);
				ActivityLottery al= activityLotteryMapper.selectByMemberActivity(paraMap);

				//没有记录发放
				if (al!=null){
					rDO.setResultCode(ResultCode.ACTIVITY_TODAY_RED_HAD_RECEIVED_ERROR);
					return rDO;
				}
				String[] strings= subscription.getTemplateIds().split(",");
				for (String s:strings) {
					if (!StringUtils.isEmpty(s)){
						// 发放奖励
						RewardsBase rBase = new RewardsBase();
						CouponTemplate c = couponTemplateManager.selectByPrimaryKey(Long.parseLong(s));
						if(c==null){
							rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_COUPON_NOTEXITS_ERROR);
							return rDO;
						}
						if (c.getCouponType().equals(1)){
							rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
							rBase.setRewardName(c.getAmount() + ActivityConstant.couponDesc);
						}else {
							rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_ANNUALIZED.getType());
							rBase.setRewardName(c.getAmount() + ActivityConstant.annualizedUnit + ActivityConstant.annualizedDesc);
						}
						rBase.setTemplateId(Long.parseLong(s));
						rb.setRewardId(s);
						drawByPrizeDirectly.drawLottery(rb, rBase,"");
					}
				}
				//发放完毕记录已领取
				ActivityLottery model = new ActivityLottery();
				model.setActivityId(activity.getId());
				model.setMemberId(memberId);
				model.setTotalCount(1);
				model.setRealCount(1);
				model.setCycleConstraint(cycleConstraint);
				activityLotteryMapper.insertSelective(model);
				rDO.setResult(activity);
				rDO.setSuccess(true);
				return rDO;
			}else{
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
				return rDO;
			}
		} catch (Exception e) {
			logger.error("【订阅号活动】异常", e);
		}
		return rDO;
	}

	@Override
	public ResultDO<ActivityForSubscription> subscriptionData(Long memberId) {
		ResultDO<ActivityForSubscription> rDO = new ResultDO<ActivityForSubscription>();
		ActivityForSubscription activityForSubscription=new ActivityForSubscription();
		try {
			Optional<Activity> annerActivity = LotteryContainer.getInstance()
                    .getActivityByName(ActivityConstant.ACTIVITY_SUBSCRIPTION);
			if (!annerActivity.isPresent()) {
                rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
                return rDO;
            }
			Activity activity=annerActivity.get();
			activityForSubscription.setStatus(activity.getActivityStatus());
			activityForSubscription.setStartTime(activity.getStartTime());
			activityForSubscription.setEndTime(activity.getEndTime());
			if (activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()){
				if(memberId != null) {
					String cycleConstraint = activity.getId() + "-" + memberId + "-" + DateUtils.getDateStrFromDate(new Date()) + ":Subscription";
					Map<String, Object> paraMap = new HashMap<String, Object>();
					paraMap.put("activityId", activity.getId());
					paraMap.put("memberId", memberId);
					paraMap.put("cycleConstraint", cycleConstraint);
					ActivityLottery al= activityLotteryMapper.selectByMemberActivity(paraMap);
					if (al!=null){
						activityForSubscription.setReceive(false);
					}else {
						activityForSubscription.setReceive(true);
					}
				}
			}
		} catch (Exception e) {
			logger.error("【订阅号活动】异常", e);
		}
		rDO.setResult(activityForSubscription);
		return rDO;
	}

	@Override
	public ResultDO<Activity> womensDaySign(Long memberId) throws ManagerException {
		ResultDO<Activity> rDO = new ResultDO<Activity>();
		try {
			rDO.setSuccess(false);
			Optional<Activity> annerActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_WOMENSDAY);
			if (!annerActivity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			Activity activity=annerActivity.get();
			if (activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
				ActivityForWomensDay activityForWomensDay = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForWomensDay.class,
						ActivityConstant.ACTIVITY_WOMENSDAY_KEY);

				String cycleConstraint = activity.getId() + "-" + memberId + "-"+DateUtils.getStrFromDate(new Date(),"yyyy-MM-dd") + ":womensDaySign";
				RuleBody rb = new RuleBody();
				rb.setActivityId(activity.getId());
				rb.setMemberId(memberId);
				rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
				rb.setCycleStr(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate()));
				rb.setDeductRemark("女神节，绽放你的美");
				rb.setRewardsAvailableNum(2);
				rb.setRewardsPoolMaxNum(2);
				rb.setActivityName(ActivityConstant.ACTIVITY_CELEBRATE_A_NAME);
				rb.setCycleStr(cycleConstraint);
				// 校验
				if (drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
					Optional<List<Object>> retList = LotteryContainer.getInstance().getRewardsListMap(activity.getId(), RewardsBase.class);
					if (retList.isPresent()) {
						RewardsBodyForProbility rfp = (RewardsBodyForProbility) drawByProbability.drawLottery(rb, null,
								TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode());
						// 发放收益券
						RewardsBase rBase = new RewardsBase();
						CouponTemplate c = couponTemplateManager.selectByPrimaryKey(Long.parseLong(activityForWomensDay.getSignTemplateIds()));
						rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_ANNUALIZED.getType());
						rBase.setRewardName(c.getAmount() + ActivityConstant.annualizedUnit + ActivityConstant.annualizedDesc);
						rBase.setTemplateId(c.getId());
						rb.setRewardId(activityForWomensDay.getSignTemplateIds());
						String cycleConstraint1 = activity.getId() + "-" + memberId + "-"+activityForWomensDay.getSignTemplateIds() + "-" +DateUtils.getStrFromDate(new Date(),"yyyy-MM-dd") + ":womensDaySign";
						rb.setCycleStr(cycleConstraint1);
						drawByPrizeDirectly.drawLottery(rb, rBase,TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode());
						// 发送站内信
						MessageClient.sendMsgForSPEngin(memberId, activity.getActivityDesc(), rfp.getRewardName());
						MessageClient.sendMsgForSPEngin(memberId, activity.getActivityDesc(), rBase.getRewardName());
						rDO.setResult(activity);
						rDO.setSuccess(true);
						return rDO;
					} else {
						rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
						return rDO;
					}
				} else {
					rDO.setResultCode(ResultCode.ACTIVITY_TODAY_RED_HAD_RECEIVED_ERROR);
					return rDO;
				}
			}else{
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
				return rDO;
			}
		} catch (Exception e) {
			logger.error("【元宵情人节活动】异常", e);
		}
		return rDO;
	}

	@Override
	public ResultDO<ActivityForWomensDay> womensDayBag(Long memberId) throws ManagerException {
		ResultDO<ActivityForWomensDay> rDO = new ResultDO<ActivityForWomensDay>();
		ActivityForWomensDay activityForWomensDay=new ActivityForWomensDay();
		try {
			if(memberId == null) {
				rDO.setResultCode(ResultCode.ACTIVITY_REPORT_IS_NOT_LOGIN);
				return rDO;
			}
			Member member= memberManager.selectByPrimaryKey(memberId);
			if (member==null){
				rDO.setResultCode(ResultCode.ACTIVITY_REPORT_IS_NOT_LOGIN);
				return rDO;
			}
			if (StringUtil.getSexByIdentityNumber(member.getIdentityNumber())==1){
				rDO.setResultCode(ResultCode.ACTIVITY_MAN_BAG_ERROR);
				return rDO;
			}
			if (StringUtil.getSexByIdentityNumber(member.getIdentityNumber())==-1){
				rDO.setResultCode(ResultCode.ACTIVITY_REAL_BAG_ERROR);
				return rDO;
			}
			rDO.setSuccess(false);
			Optional<Activity> annerActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_WOMENSDAY);
			if (!annerActivity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			Activity activity=annerActivity.get();
			if (activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
				ActivityForWomensDay womensDay = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForWomensDay.class,
						ActivityConstant.ACTIVITY_WOMENSDAY_KEY);

				if (!DateUtils.getStrFromDate(new Date(),"yyyy-MM-dd").equals(womensDay.getWomensDate())){
					rDO.setResultCode(ResultCode.ACTIVITY_DATE_ERROR);
					return rDO;
				}
				String cycleConstraint = activity.getId() + "-" + memberId + ":WomensDayBag";
				RuleBody rb = new RuleBody();

				rb.setActivityId(activity.getId());
				rb.setMemberId(memberId);
				rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_COIN.getStatus());
				rb.setCycleStr(cycleConstraint);

				Map<String, Object> paraMap = new HashMap<String, Object>();
				paraMap.put("activityId", activity.getId());
				paraMap.put("memberId", memberId);
				paraMap.put("cycleConstraint", cycleConstraint);
				ActivityLottery al= activityLotteryMapper.selectByMemberActivity(paraMap);

				//没有记录发放大礼包
				if (al!=null){
					rDO.setResultCode(ResultCode.ACTIVITY_LUCKYBAG__ERROR);
					return rDO;
				}
				// 发放收益券
				RewardsBase rBase = new RewardsBase();
				CouponTemplate c = couponTemplateManager.selectByPrimaryKey(Long.parseLong(womensDay.getLoginTemplateIds()));
				if(c==null){
					rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_COUPON_NOTEXITS_ERROR);
					return rDO;
				}
				rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
				rBase.setRewardName(c.getAmount() + ActivityConstant.annualizedUnit + ActivityConstant.annualizedDesc);
				rBase.setTemplateId(c.getId());
				rb.setRewardId(womensDay.getLoginTemplateIds());
				drawByPrizeDirectly.drawLottery(rb, rBase,"");
				MessageClient.sendMsgForSPEngin(memberId, activity.getActivityDesc(), rBase.getRewardName());
				//发放人气值
				rBase.setRewardName(womensDay.getPopularity() + ActivityConstant.popularityDesc);
				rBase.setRewardValue(womensDay.getPopularity());
				rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_POPULARITY.getType());
				drawByPrizeDirectly.drawLottery(rb, rBase, "");
				//站内信
				MessageClient.sendMsgForSPEngin(memberId, activity.getActivityDesc(), rBase.getRewardName());
				//登录大礼包发放完毕记录已领取
				ActivityLottery model = new ActivityLottery();
				model.setActivityId(activity.getId());
				model.setMemberId(memberId);
				model.setTotalCount(1);
				model.setRealCount(1);
				model.setCycleConstraint(cycleConstraint);
				activityLotteryMapper.insertSelective(model);
				rDO.setResult(activityForWomensDay);
				rDO.setSuccess(true);
			}else{
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
				return rDO;
			}
		} catch (Exception e) {
			logger.error("【女神节，绽放你的美】发放大礼包异常, memberId={}", memberId, e);
		}
		return rDO;
	}

	@Override
	public ResultDO<ActivityForWomensDay> womensDayData(Long memberId) {
		ResultDO<ActivityForWomensDay> rDO = new ResultDO<ActivityForWomensDay>();
		ActivityForWomensDay activityForWomensDay=new ActivityForWomensDay();
		try {
			rDO.setSuccess(false);
			Optional<Activity> annerActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_WOMENSDAY);
			if (!annerActivity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			Activity activity=annerActivity.get();
			ActivityForWomensDay womensDay = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForWomensDay.class,
					ActivityConstant.ACTIVITY_WOMENSDAY_KEY);
			activityForWomensDay.setStartDate(activity.getStartTime());
			activityForWomensDay.setEndDate(activity.getEndTime());
			activityForWomensDay.setWomensDate(womensDay.getWomensDate());
			TransactionQuery query=new TransactionQuery();
			if (memberId!=null&&memberId>0){
				Member member= memberManager.selectByPrimaryKey(memberId);
				if (member!=null){
					activityForWomensDay.setSex(StringUtil.getSexByIdentityNumber(member.getIdentityNumber()));
				}
				if (activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()){
					//查询今日投资额
					query.setMemberId(memberId);
					query.setTransactionStartTime(DateUtils.getDateFromString(DateUtils.getStrFromDate(new Date(),"yyyy-MM-dd HH:mm:ss")));
					query.setTransactionEndTime(DateUtils.getDateFromString(DateUtils.getStrFromDate(DateUtils.addDate(new Date(),1),"yyyy-MM-dd HH:mm:ss")));
					BigDecimal invest= transactionMapper.getMemberTotalInvestNoTransfer(memberId,DateUtils.getDateFromString(DateUtils.getStrFromDate(new Date(),"yyyy-MM-dd HH:mm:ss")),
							DateUtils.getDateFromString(DateUtils.getStrFromDate(DateUtils.addDate(new Date(),1),"yyyy-MM-dd HH:mm:ss")));
					activityForWomensDay.setInvestment(invest);
				}
				//查询签到记录
				MemberCheckQuery memberCheckQuery=new MemberCheckQuery();
				memberCheckQuery.setMemberId(memberId);
				memberCheckQuery.setStartTime(activity.getStartTime());
				memberCheckQuery.setEndTime(activity.getEndTime());
				List<MemberCheck> list= memberCheckMapper.queryMemberCheckListByQuery(memberCheckQuery);
				List<Date> signlist=new ArrayList<>();
				Date checkDate=null;
				for (MemberCheck mc:list) {
					checkDate=new Date();
					checkDate=mc.getCheckDate();
					signlist.add(checkDate);
				}
				activityForWomensDay.setSignDate(signlist);

				Map<String, Object> paraMap = new HashMap<String, Object>();
				String cycleConstraint = activity.getId() + "-" + memberId + ":WomensDayBag";
				paraMap.put("activityId", activity.getId());
				paraMap.put("memberId", memberId);
				paraMap.put("cycleConstraint", cycleConstraint);
				ActivityLottery al= activityLotteryMapper.selectByMemberActivity(paraMap);
				if (al==null){
					activityForWomensDay.setBag(true);
				}else {
					activityForWomensDay.setBag(false);
				}
			}
			rDO.setResult(activityForWomensDay);
			rDO.setSuccess(true);
		} catch (Exception e) {
			logger.error("【女神节，绽放你的美】发放大礼包异常, memberId={}", memberId, e);
		}
		return rDO;
	}

	@Override
	public ResultDO<Object> receiveCouponGold(Long memberId, int couponAmount,String way) {
		ResultDO<Object> rDO = new ResultDO<Object>();
		ActivityForDayDrop model=new ActivityForDayDrop();
		try {
			if (memberId == null) {
				rDO.setResultCode(ResultCode.ACTIVITY_REPORT_IS_NOT_LOGIN);
				return rDO;
			}
			Optional<Activity> activity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_DAY_DROP_GOLD_NAME);
			if (!activity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			
		    Activity goldActivity=activity.get();
		    if (goldActivity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
		    	ActivityForDayDrop dayDrop = LotteryContainer.getInstance().getObtainConditions(goldActivity, ActivityForDayDrop.class,
						ActivityConstant.ACTIVITY_DAY_DROP_GOLD_KEY);
		    	int red=dayDrop.getTotalRed()-RedisActivityClient.getReceivedCouponTotalForDayDrop();
				if(red<1){
					rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_COUPON_HADEND_ERROR);
					return rDO;
				}
				//校验领取次数
				ActivityLottery activityLottery=new ActivityLottery();
				activityLottery.setActivityId(goldActivity.getId());
				activityLottery.setMemberId(memberId);
				activityLottery.setCycleConstraint("receiveNum");
				ActivityLottery lotteryRemind=activityLotteryMapper.checkExistLottery(activityLottery);
				if(lotteryRemind==null){
					rDO.setResultCode(ResultCode.ACTIVITY_RECEIVE_TOTAL_NUM_ERROR);
					return rDO;
				}
				if(lotteryRemind.getRealCount()<1){
					rDO.setResultCode(ResultCode.ACTIVITY_RECEIVE_TOTAL_NUM_ERROR);
					return rDO;
				}
				RuleBody rb = new RuleBody();
				rb.setActivityId(goldActivity.getId());
				rb.setMemberId(memberId);
				rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_COIN.getStatus());
				rb.setActivityName(ActivityConstant.ACTIVITY_DAY_DROP_GOLD_NAME);
				rb.setCycleStr(activityLottery.getCycleConstraint());
				rb.setDeductRemark(way);
				Long templateId=1L;
				for(int j=0;j<dayDrop.getCouponAmount().size();j++){
					if(couponAmount==dayDrop.getCouponAmount().get(j)){
						templateId=dayDrop.getTemplateIds().get(j);
						break;
					}
				}
				CouponTemplate c = couponTemplateManager.selectByPrimaryKey(templateId);
				if(c==null){
					rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_COUPON_NOTEXITS_ERROR);
					return rDO;
				}
				//红包数量记录redis
				RedisActivityClient.incrTotalRedForDayDrop();
				if (RedisActivityClient.getReceivedCouponTotalForDayDrop()>dayDrop.getTotalRed()){
					rDO.setResultCode(ResultCode.ACTIVITY_REDBAG_NULL_ERROR);
					return rDO;
				}
				// 发放奖励
				RewardsBase rBase = new RewardsBase();
				rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
				rBase.setRewardValue(c.getAmount().intValue());
				rBase.setTemplateId(templateId);
				rBase.setRewardName(c.getAmount().intValue() + ActivityConstant.couponDesc);
				drawByPrizeDirectly.drawLotteryNew(rb, rBase, "");
				//发放成功减去领取次数
				activityLottery.setRealCount(-1);
				activityLottery.setCycleConstraint(activityLottery.getCycleConstraint());
				int i=activityLotteryMapper.updateRealCount(activityLottery);
				if(i>0){
					rDO.setSuccess(true);
					model.setTemplateIdName(c.getAmount().intValue());
					model.setTotalRed(dayDrop.getTotalRed()-RedisActivityClient.getReceivedCouponTotalForDayDrop());
					rDO.setResult(model);
					return rDO;
				}
			}else{
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
				return rDO;
			}
			
		} catch (Exception e) {
			logger.error("【天降金喜】, memberId={}", memberId, e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}

	@Override
	public ResultDO<ActivityForInviteFriend> inviteFriendData(Long memberId) {
		ResultDO<ActivityForInviteFriend> rDO = new ResultDO<ActivityForInviteFriend>();
		ActivityForInviteFriend activityForInviteFriend=new ActivityForInviteFriend();
		try {
			rDO.setSuccess(false);
			Optional<Activity> annerActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_INVITEFRIENDS_NAME);
			Activity activity=annerActivity.get();
			activityForInviteFriend.setStartDate(activity.getStartTime());
			activityForInviteFriend.setEndDate(activity.getEndTime());

			List<ActivityForInviteFriendList> lists=new ArrayList<>();
			String key="inviteFriendData:lists";
			boolean isExit = RedisManager.isExitByObjectKey(key);
			if (isExit){
				try {
					lists = (List<ActivityForInviteFriendList>) RedisManager.getObject(key);
				} catch (Exception e) {
					logger.error("活动参加条件Json转Class失败！activityId={}", activity.getId());
				}
			}else {
				lists= activityLotteryResultNewMapper.queryInviteFriendData();
				RedisManager.putObject(key, lists);
				RedisManager.expireObject(key, 60);
			}

			activityForInviteFriend.setInviteFriendLists(lists);
			if (memberId != null){
				//奖金
				BigDecimal reward= activityLotteryResultNewMapper.queryInviteFriendReward(memberId);
				activityForInviteFriend.setReward(reward);
				ActivityForInviteFriendList activityForInviteFriendList = activityLotteryResultNewMapper.queryInviteFriendDataByMemberId(memberId);
				if (activityForInviteFriendList!=null){
					activityForInviteFriend.setInvest(activityForInviteFriendList.getInvest());
					activityForInviteFriend.setInviteCount(activityForInviteFriendList.getReferralCount());
				}
			}
			rDO.setResult(activityForInviteFriend);
			rDO.setSuccess(true);
			return rDO;
		} catch (Exception e) {
			logger.error("【邀请好友】获取数据异常, memberId={}", memberId, e);
		}
		return null;
	}

	@Override
	public ResultDO<ActivityForInviteFriend> inviteFriendNewUser(Long memberId) {
		ResultDO<ActivityForInviteFriend> rDO = new ResultDO<ActivityForInviteFriend>();
		ActivityForInviteFriend activityForInviteFriend=new ActivityForInviteFriend();
		try {
			rDO.setSuccess(false);
			Optional<Activity> inviteFriend = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_INVITEFRIENDS_NAME);

			if (!inviteFriend.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			Activity activity = inviteFriend.get();
			if (activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()){
				activityForInviteFriend = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForInviteFriend.class,
						ActivityConstant.ACTIVITY_INVITEFRIENDS_KEY);

				Member member= memberManager.selectByPrimaryKey(memberId);
				Long referral = member.getReferral();
				Integer transactionCount= transactionMapper.queryMemberTransactionCountEndTime(referral,activityForInviteFriend.getTransactionDate());
				//投资时间之后没有投资的不参与
				if (transactionCount==null||transactionCount<1){
					rDO.setResultCode(ResultCode.ACTIVITY_ANNIVERSARY_TOTAL_INVEST_ERROR);
					return rDO;
				}

				RuleBody rb = new RuleBody();
				RewardsBase rBase = new RewardsBase();
				CouponTemplate c = null;
				try {

					try {
						c = couponTemplateManager.selectByPrimaryKey(activityForInviteFriend.getNewTemplateId());
					} catch (ManagerException e) {
						logger.error("【邀请好友】 发放新用户收益券不存在 templateId={},memberId={}", activityForInviteFriend.getNewTemplateId(),
								memberId, e);
					}
					if(c==null){
						rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_COUPON_NOTEXITS_ERROR);
						return rDO;
					}
					//给被推荐新用户发放收益券
					rb.setCycleStr(activity.getId()+"-" + memberId + ":InviteFriendNewUser");
					rb.setActivityId(activity.getId());
					rb.setMemberId(memberId);
					rb.setDeductRemark("InviteFriendNewUser");
					rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_ANNUALIZED.getType());
					rBase.setRewardName(c.getAmount() + ActivityConstant.annualizedDesc + ActivityConstant.annualizedUnit);
					rBase.setTemplateId(activityForInviteFriend.getNewTemplateId());
					rBase.setRewardValue(c.getAmount().intValue());
					drawByPrizeDirectly.drawLotteryNew(rb, rBase,TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode());
				} catch (Exception e) {
					logger.error("【邀请好友】 发放新用户收益券异常 memberId={}", memberId, e);
				}
				rDO.setSuccess(true);
				return rDO;
			}
		} catch (Exception e) {
			logger.error("【邀请好友】发放新用户收益券异常, memberId={}", memberId, e);
		}
		return rDO;
	}

	@Override
	public ResultDO<Activity> receiveFlowers(Long memberId) {
		ResultDO<Activity> rDO = new ResultDO<Activity>();
		try {
			rDO.setSuccess(false);
			Optional<Activity> annerActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_FLOWERS_NAME);
			if (!annerActivity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			Activity activity=annerActivity.get();
			if (activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
				ActivityForSubscription subscription = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForSubscription.class,
						ActivityConstant.ACTIVITY_FLOWERS_KEY);

				RuleBody rb = new RuleBody();
				rb.setActivityId(activity.getId());
				rb.setMemberId(memberId);
				try {
					String cycleConstraint = activity.getId() + "-" + memberId + "-" + DateUtils.getStrFromDate(new Date(),"yyyy-MM-dd") + ":FlowersBag";
					ActivityLottery model = new ActivityLottery();
					model.setActivityId(activity.getId());
					model.setMemberId(memberId);
					model.setTotalCount(1);
					model.setRealCount(1);
					model.setCycleConstraint(cycleConstraint);
					activityLotteryMapper.insertSelective(model);
				} catch (Exception e) {
					logger.error("【女神送花】重复领取, memberId={}", memberId, e);
					rDO.setResultCode(ResultCode.ACTIVITY_REDBAG_JOINED_ERROR);
					return rDO;
				}

				String[] strings= subscription.getTemplateIds().split(",");
				for (String s:strings) {
					if (!StringUtils.isEmpty(s)){
						// 发放奖励
						RewardsBase rBase = new RewardsBase();
						CouponTemplate c = couponTemplateManager.selectByPrimaryKey(Long.parseLong(s));
						if(c==null){
							logger.error("【女神送花】发放优惠券不存在, templateId={}", s);
							continue;
						}
						String cycleConstraint = activity.getId() + "-" + s + "-" + memberId + "-" + DateUtils.getStrFromDate(new Date(),"yyyy-MM-dd") + ":FlowersBag";
						rb.setCycleStr(cycleConstraint);
						rb.setRewardId(s);
						rBase.setTemplateId(Long.parseLong(s));
						rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
						rBase.setRewardValue(c.getAmount().intValue());
						rBase.setRewardName(c.getAmount().intValue() + ActivityConstant.couponDesc);
						drawByPrizeDirectly.drawLotteryNew(rb, rBase,TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode());
					}
				}
				rDO.setSuccess(true);
				return rDO;
			}else{
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
				return rDO;
			}
		} catch (Exception e) {
			logger.error("【女神送花】发放大礼包异常, memberId={}", memberId, e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}

	@Override
	public ResultDO<ActivityForSubscription> flowersData(Long memberId) {
		ResultDO<ActivityForSubscription> rDO = new ResultDO<ActivityForSubscription>();
		ActivityForSubscription subscription=new ActivityForSubscription();
		try {
			rDO.setSuccess(false);
			Optional<Activity> annerActivity = LotteryContainer.getInstance()
                    .getActivityByName(ActivityConstant.ACTIVITY_FLOWERS_NAME);
			if (!annerActivity.isPresent()) {
                rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
                return rDO;
            }
			Activity activity=annerActivity.get();
			subscription.setStartTime(activity.getStartTime());
			subscription.setEndTime(activity.getEndTime());
			if (memberId!=null){
				Map<String, Object> paraMap = new HashMap<String, Object>();
				String cycleConstraint = activity.getId() + "-" + memberId + "-" + DateUtils.getStrFromDate(new Date(),"yyyy-MM-dd") + ":FlowersBag";
				paraMap.put("activityId", activity.getId());
				paraMap.put("memberId", memberId);
				paraMap.put("cycleConstraint", cycleConstraint);
				ActivityLottery al= activityLotteryMapper.selectByMemberActivity(paraMap);
				if (al!=null){
					subscription.setReceive(false);
				}else {
					subscription.setReceive(true);
				}
			}
			rDO.setSuccess(true);
			rDO.setResult(subscription);
			return rDO;
		} catch (Exception e) {
			rDO.setSuccess(false);
			logger.error("【女神送花】初始化数据异常, memberId={}", memberId, e);
		}
		return rDO;
	}
	
	/**
	 * 60亿活动，登录就送1%收益券一张
	 * @throws Exception 
	 */
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void sixBillionActAfterLogin(Member member) throws Exception {
		Optional<Activity> activity = LotteryContainer.getInstance().getActivityByName(ActivityConstant.ACTIVITY_SIXBILLION_NAME);
		if (!activity.isPresent()) {
			return ;
		}
		Activity sixActivity = activity.get();
		if (StatusEnum.ACTIVITY_STATUS_IS_START.getStatus() != sixActivity.getActivityStatus()) {
			return ;
		}
		//获取活动规则
		ActivityForSixBillion sixBillionActivity = LotteryContainer.getInstance().getObtainConditions(sixActivity, ActivityForSixBillion.class,ActivityConstant.ACTIVITY_SIXBILLION_KEY);
		if(sixBillionActivity==null){
			return ;
		}
		
		long activityId = sixActivity.getId();
		long memberId = member.getId();
		
		List<Long> templateIdList = sixBillionActivity.getTemplateId();
		for (Long templateId:templateIdList) {

			String cycleConstraint = activityId + "-" + templateId + "-"+memberId+ "-"+":SixBillionTransaction";
			Map<String, Object> paraMap = new HashMap<String, Object>();
			paraMap.put("activityId", activityId);
			paraMap.put("memberId", memberId);
			paraMap.put("cycleConstraint", cycleConstraint);
			//判断是否领过1%收益券
			ActivityLottery al = this.selectByMemberActivity(paraMap);
			if (al==null){
				// 发放奖励
				RuleBody rb = new RuleBody();
				rb.setActivityId(activityId);
				rb.setMemberId(memberId);
				rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_COIN.getStatus());
				rb.setCycleStr("");
				RewardsBase rBase = new RewardsBase();
				CouponTemplate couponTemplate = null;
				try {
					couponTemplate = couponTemplateManager.selectByPrimaryKey(templateId);
				} catch (ManagerException e) {
					e.printStackTrace();
				}
				if(couponTemplate==null){
					logger.info("【庆60亿，抢标奖励翻6倍！】 收益券模板id不存在, activityId={}, memberId={}, templateId={}, ", activityId,memberId,templateId);
					return;
				}
				rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_ANNUALIZED.getType());
				rBase.setRewardName(couponTemplate.getAmount() + ActivityConstant.annualizedUnit + ActivityConstant.annualizedDesc);
				rBase.setTemplateId(templateId);
				rb.setRewardId(templateId.toString());
				//记录已领取
				ActivityLottery model = new ActivityLottery();
				model.setActivityId(activityId);
				model.setMemberId(memberId);
				model.setTotalCount(1);
				model.setRealCount(1);
				model.setCycleConstraint(cycleConstraint);
				activityLotteryMapper.insertSelective(model);
				
				drawByPrizeDirectly.drawLotteryNew(rb, rBase,"");
				//站内信
				MessageClient.sendMsgForSPEngin(memberId, sixActivity.getActivityDesc(), rBase.getRewardName());
			}
		}
	}

	@Override
	public ResultDO<Object> springComingReceiveCoupon(Long memberId, Long templateId) {
		ResultDO<Object> rDO = new ResultDO<Object>();
		try {
			if (memberId == null) {
				rDO.setResultCode(ResultCode.ACTIVITY_REPORT_IS_NOT_LOGIN);
				return rDO;
			}

			if(!memberCheckManager.isChecked(memberId)){
				rDO.setResultCode(ResultCode.MEMBER_IS_CHECK_NO);
				return rDO;
			}

			Optional<Activity> springActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.SPRING_ACTIVITY_TRANSACTION);
			if (!springActivity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}

			Activity activity = springActivity.get();
			if (activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {

				ActivityForSpringCome model = LotteryContainer.getInstance().getObtainConditions(activity,
						ActivityForSpringCome.class, ActivityConstant.SPRING_ACTIVITY_TRANSACTION);
				Long tempId = null;
				String[] strings= model.getInvestTemplateIds().split(",");
				for (String s:strings) {
					if (!StringUtils.isEmpty(s)){
						if (templateId!=null&&s.equals(templateId.toString())){
							tempId = templateId;
							break;
						}
					}
				}
				if(tempId==null){
					rDO.setResultCode(ResultCode.COUPONTEMPLATE_NOT_EXIST_ERROR);
					return rDO;
				}

				String cycleConstraint=memberId+DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+activity.getId();
				ActivityLottery insertmodel = new ActivityLottery();
				insertmodel.setActivityId(activity.getId());
				insertmodel.setMemberId(memberId);
				insertmodel.setTotalCount(1);
				insertmodel.setRealCount(0);
				insertmodel.setRemark(templateId.toString());
				insertmodel.setCycleConstraint(cycleConstraint);
				Boolean flag = false;
				try {
					if(this.checkExistLottery(insertmodel) == null) {
						this.insertActivityLottery(insertmodel);
						flag = true;
					}else{
						rDO.setResultCode(ResultCode.ACTIVITY_YET_JOIN_ACTIVITY_ERROR);
						return rDO;
					}
				} catch (Exception e) {
					logger.error("【好春来活动领券】重复领取, memberId={}, cycleConstraint={},templateId={}",
							memberId, cycleConstraint,templateId);
					rDO.setResultCode(ResultCode.ACTIVITY_YET_JOIN_ACTIVITY_ERROR);
					return rDO;
				}
				if(flag){
					try {
						Coupon c = couponManager.receiveCoupon(memberId, activity.getId(), Long.valueOf(templateId), -1L);
						if (c == null) {
							// 优惠券赠送失败;
							logger.error("【好春来活动领券】优惠券赠送失败,接口返回Coupon=null!,memberId={},activityId={},templateId={}",
									memberId,activity.getId(), templateId);
						}
					} catch (ManagerException e) {
						logger.error("【好春来活动领券】优惠券赠送失败,memberId={}, activityId={}, templateId={}",
								memberId,activity.getId(), templateId);
					}
				}

			}else{
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
				return rDO;
			}
		} catch (Exception e) {
			logger.error("【好春来活动领券】, memberId={}", memberId, e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}

	@Override
	public ResultDO<Object> springComingInit(Long memberId) {
		ResultDO<Object> rDO = new ResultDO<Object>();
		ActivityForSpringComing obj = new ActivityForSpringComing();
		try {

			Optional<Activity> springActivity = LotteryContainer.getInstance().getActivityByName(
					ActivityConstant.SPRING_ACTIVITY_TRANSACTION);
			if (!springActivity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
				return rDO;
			}
			Activity activity = springActivity.get();
			obj.setStatus(activity.getActivityStatus());
			obj.setStartDate(activity.getStartTime());
			obj.setEndDate(activity.getEndTime());

			ActivityForSpringCome model = LotteryContainer.getInstance().getObtainConditions(activity,
					ActivityForSpringCome.class, ActivityConstant.SPRING_ACTIVITY_TRANSACTION);
			//优惠券信息
			String[] strings= model.getInvestTemplateIds().split(",");
			List <CouponTemplate> couponList = Lists.newArrayList();
			for (String s:strings) {
				if (!StringUtils.isEmpty(s)){
					CouponTemplate c = couponTemplateManager.selectByPrimaryKey(Long.valueOf(s));
					if(c==null){
						continue;
					}
					couponList.add(c);
				}
			}
			obj.setCouponList(couponList);

			//总投资额
			BigDecimal totalAmount = BigDecimal.ZERO;
			Integer pacNum = 0;
			if (memberId != null) {

				if (StatusEnum.ACTIVITY_STATUS_IS_START.getStatus() == activity.getActivityStatus()) {
					//当日领取优惠券
					String cycleConstraint=memberId+DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+activity.getId();
					ActivityLottery querymodel = new ActivityLottery();
					querymodel.setActivityId(activity.getId());
					querymodel.setMemberId(memberId);
					querymodel.setCycleConstraint(cycleConstraint);
					ActivityLottery activityLottery = this.checkExistLottery(querymodel);
					if(activityLottery!=null){
						obj.setTemplate(Long.valueOf(activityLottery.getRemark()));
					}
				}
				//【好春来活动-累计送券】
				Optional<Activity> springTotalInvestActivity = LotteryContainer.getInstance().getActivityByName(
						ActivityConstant.SPRING_ACTIVITY_TOTAL_INVEST);
				if (springTotalInvestActivity.isPresent()) {
					Activity totalInvestactivity = springTotalInvestActivity.get();
					totalAmount= transactionMapper.getMemberTotalInvestNoTransfer(memberId,
							totalInvestactivity.getStartTime(),totalInvestactivity.getEndTime());

					Map<String, Object> map = Maps.newHashMap();
					map.put("memberId",memberId);
					map.put("activityId",totalInvestactivity.getId());
					List<ActivityLottery>  activityList = this.queryLotteryByMemberAndActivity(map);
					pacNum = activityList.size();
				}

			}
			obj.setPacNum(pacNum);
			obj.setTotalAmount(totalAmount);
			rDO.setResult(obj);
		} catch (Exception e) {
			logger.error("【好春来初始化】, memberId={}", memberId, e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}

	@Override
	public ResultDO<Activity> laborSign(Long memberId) {
		ResultDO<Activity> rDO = new ResultDO<Activity>();
		try {
			rDO.setSuccess(false);
			Optional<Activity> annerActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_LABOR_NAME);
			if (!annerActivity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			Activity activity=annerActivity.get();
			if (activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()){
				Map map = LotteryContainer.getInstance().getObtainConditions(activity, Map.class,
						ActivityConstant.ACTIVITY_LABOR_KEY);
				//当前时间在活动开始的第二天开始处理连续两天签到送券
				if (DateUtils.addDate(new Date(),-1).after(activity.getStartTime())){
					String lastDate = DateUtils.addDays(new Date(),-1,"yyyy-MM-dd");
					MemberCheck memberCheck = memberCheckMapper.queryMemberCheckToday(memberId,lastDate);
					Map<String, Object> paraMap = new HashMap<String, Object>();
					String cycleConstraint = activity.getId() + "-" + memberId + "-" + lastDate + ":laborSign";
					paraMap.put("activityId", activity.getId());
					paraMap.put("memberId", memberId);
					paraMap.put("cycleConstraint", cycleConstraint);
					ActivityLottery activityLottery = activityLotteryMapper.selectByMemberActivity(paraMap);
					//前一天已经签到且未发放的进行发放连续两天签到奖励
					if (memberCheck != null && activityLottery == null){
						try {
							String cycle = activity.getId() + "-" + memberId + "-" + DateUtils.getStrFromDate(new Date(),"yyyy-MM-dd") + ":laborSign";
							RuleBody rb = new RuleBody();
							rb.setActivityId(activity.getId());
							rb.setMemberId(memberId);
							rb.setCycleStr(cycle);
							RewardsBase rBase = new RewardsBase();
							CouponTemplate c = couponTemplateManager.selectByPrimaryKey(Long.parseLong(map.get("towSign").toString()));
							if(c==null){
								logger.error("【劳动最光荣】发放优惠券不存在, templateId={}", map.get("towSign").toString());
							}
							rBase.setTemplateId(c.getId());
							rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
							rBase.setRewardValue(c.getAmount().intValue());
							rBase.setRewardName(c.getAmount().intValue() + ActivityConstant.annualizedUnit + ActivityConstant.annualizedDesc);
							drawByPrizeDirectly.drawLotteryNew(rb,rBase,TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode());
						} catch (Exception e) {
							logger.error("【劳动最光荣】签到两天发放异常", e.toString());
						}
					}
				}

				MemberCheckQuery memberCheckQuery = new MemberCheckQuery();
				memberCheckQuery.setMemberId(memberId);
				memberCheckQuery.setStartTime(activity.getStartTime());
				memberCheckQuery.setEndTime(DateUtils.addDate(new Date(),-1));
				List<MemberCheck> checked = memberCheckMapper.queryMemberCheckListByQuery(memberCheckQuery);
				//用户昨天之前的签到数达到了6开始发放累计签到7天的礼包
				if (checked != null && checked.size() >= 6){
					try {
						String cycle = activity.getId() + "-" + memberId + "-Seven:laborSign";
						RuleBody rb = new RuleBody();
						rb.setActivityId(activity.getId());
						rb.setMemberId(memberId);
						rb.setCycleStr(cycle);
						RewardsBase rBase = new RewardsBase();
						CouponTemplate c = couponTemplateManager.selectByPrimaryKey(Long.parseLong(map.get("sevenSign").toString()));
						if(c==null){
							logger.error("【劳动最光荣】发放优惠券不存在, templateId={}", map.get("sevenSign").toString());
						}
						rBase.setTemplateId(c.getId());
						rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
						rBase.setRewardValue(c.getAmount().intValue());
						rBase.setRewardName(c.getAmount().intValue() + ActivityConstant.couponDesc);
						drawByPrizeDirectly.drawLotteryNew(rb,rBase,TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode());
					} catch (Exception e) {
						logger.error("【劳动最光荣】签到大礼包发放异常", e.toString());
					}
					//发放奖章
					try {
						String medalConstraint = activity.getId() + "-" + memberId + ":WorkerMedal";
						ActivityLottery model = new ActivityLottery();
						model.setActivityId(activity.getId());
						model.setMemberId(memberId);
						model.setTotalCount(1);
						model.setRealCount(1);
						model.setCycleConstraint(medalConstraint);
						activityLotteryMapper.insertSelective(model);
					} catch (Exception e) {
						logger.error("【劳动最光荣】签到劳模奖章已经发放, memberId={}", memberId, e);
					}
				}
				//开始奖章换奖励
				try {
					laborMedal(memberId,activity.getId(),activity.getActivityDesc(),Integer.parseInt(map.get("giftPopularity").toString()),
                            Long.parseLong(map.get("giftTemplateId").toString()),Integer.parseInt(map.get("giftCount").toString()));
				} catch (NumberFormatException e) {
					logger.error("【劳动最光荣】奖章换奖励异常, memberId={}", memberId, e);
				}
			}
			rDO.setSuccess(true);
			return rDO;
		} catch (Exception e) {
			rDO.setSuccess(false);
			logger.error("【劳动最光荣】签到异常, memberId={}", memberId, e);
		}
		return rDO;
	}

	@Override
	public ResultDO<Activity> laborGift(Long memberId) {
		ResultDO<Activity> rDO = new ResultDO<Activity>();
		try {
			rDO.setSuccess(false);
			if (memberId == null) {
				rDO.setResultCode(ResultCode.ACTIVITY_REPORT_IS_NOT_LOGIN);
				return rDO;
			}
			Optional<Activity> annerActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_LABOR_NAME);
			if (!annerActivity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			Activity activity=annerActivity.get();
			if (activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
				Map map = LotteryContainer.getInstance().getObtainConditions(activity, Map.class,
						ActivityConstant.ACTIVITY_LABOR_KEY);

				RuleBody rb = new RuleBody();
				rb.setActivityId(activity.getId());
				rb.setMemberId(memberId);
				//查询是否有资格
				Map<String, Object> paraMap = new HashMap<String, Object>();
				paraMap.put("activityId", activity.getId());
				paraMap.put("memberId", memberId);
				paraMap.put("cycleConstraint", activity.getId() + "-" + memberId + "-" + DateUtils.getStrFromDate(new Date(),"yyyy-MM-dd") + ":gifts");
				ActivityLottery al= activityLotteryMapper.selectByMemberActivity(paraMap);
				if (al!=null){
					String[] strings= map.get("workTemplateId").toString().split(",");
					try {
						for (String s:strings) {
							if (!StringUtils.isEmpty(s)){
								// 发放奖励
								RewardsBase rBase = new RewardsBase();
								CouponTemplate c = couponTemplateManager.selectByPrimaryKey(Long.parseLong(s));
								if(c==null){
									logger.error("【劳动最光荣】发放优惠券不存在, templateId={}", s);
									continue;
								}
								String cycleConstraint = activity.getId() + "-" + s + "-" + memberId + "-" + DateUtils.getStrFromDate(new Date(),"yyyy-MM-dd") + ":gifts";
								rb.setCycleStr(cycleConstraint);
								rb.setRewardId(s);
								rBase.setTemplateId(Long.parseLong(s));
								rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
								rBase.setRewardValue(c.getAmount().intValue());
								rBase.setRewardName(c.getAmount().intValue() + ActivityConstant.couponDesc);
								drawByPrizeDirectly.drawLotteryNew(rb, rBase,TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode());
							}
						}
					} catch (Exception e) {
						logger.error("【劳动最光荣】礼包重复领取, templateId={}", e);
						rDO.setResultCode(ResultCode.ACTIVITY_REDBAG_JOINED_ERROR);
						return rDO;
					}
					//发放完510礼包进行维护礼包发放数据
					try {
						String giftConstraint = activity.getId() + "-" + DateUtils.getStrFromDate(new Date(),"yyyy-MM-dd") + "-" + memberId + ":giftspackage";
						ActivityLottery model = new ActivityLottery();
						model.setActivityId(activity.getId());
						model.setMemberId(memberId);
						model.setTotalCount(1);
						model.setRealCount(1);
						model.setCycleConstraint(giftConstraint);
						model.setRemark(DateUtils.getStrFromDate(new Date(),"yyyy-MM-dd"));
						activityLotteryMapper.insertSelective(model);
					} catch (Exception e) {
						logger.error("【劳动最光荣】先锋奖章重复发放, memberId={}", memberId, e);
					}

					//累计领取7个大礼包获得五一先锋奖章
					int giftsCount = activityLotteryMapper.queryCountMemberReceiveLikeCycle(activity.getId(),memberId,memberId+":giftspackage");
					if (giftsCount >= 7){
						//发放五一先锋奖章
						try {
							String medalConstraint = activity.getId() + "-" + memberId + ":PioneerMedal";
							ActivityLottery model = new ActivityLottery();
							model.setActivityId(activity.getId());
							model.setMemberId(memberId);
							model.setTotalCount(1);
							model.setRealCount(1);
							model.setCycleConstraint(medalConstraint);
							activityLotteryMapper.insertSelective(model);
						} catch (Exception e) {
							logger.error("【劳动最光荣】先锋奖章重复发放, memberId={}", memberId, e);
						}
					}
				}
				//开始奖章换奖励
				try {
					laborMedal(memberId,activity.getId(),activity.getActivityDesc(),Integer.parseInt(map.get("giftPopularity").toString()),
							Long.parseLong(map.get("giftTemplateId").toString()),Integer.parseInt(map.get("giftCount").toString()));
				} catch (NumberFormatException e) {
					logger.error("【劳动最光荣】奖章换奖励异常, memberId={}", memberId, e);
				}
				rDO.setSuccess(true);
				return rDO;
			}else{
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
				return rDO;
			}
		} catch (Exception e) {
			logger.error("【劳动最光荣】发放大礼包异常, memberId={}", memberId, e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}


	private void laborMedal(Long memberId,Long activityId,String activityDesc,Integer giftPopularity,Long giftTemplateId,Integer giftCount){
		Map<String, Object> medalMap = new HashMap<String, Object>();
		medalMap.put("activityId", activityId);
		medalMap.put("memberId", memberId);
		//五一劳模奖章
		medalMap.put("cycleConstraint", activityId + "-" + memberId + ":WorkerMedal");
		ActivityLottery workerMedal= activityLotteryMapper.selectByMemberActivity(medalMap);
		if (workerMedal==null){
			return;
		}
		//五一先锋奖章
		medalMap.put("cycleConstraint", activityId + "-" + memberId + ":PioneerMedal");
		ActivityLottery pioneerMedal= activityLotteryMapper.selectByMemberActivity(medalMap);
		if (pioneerMedal == null){
			return;
		}
		//五一敬业奖章
		medalMap.put("cycleConstraint", activityId + "-" + memberId + ":DedicatedMedal");
		ActivityLottery dedicatedMedal= activityLotteryMapper.selectByMemberActivity(medalMap);
		if (dedicatedMedal == null){
			return;
		}
		//发放人气值
		RuleBody medalRb = null;
		RewardsBase medalBase = null;
		try {
			medalRb = new RuleBody();
			medalBase = new RewardsBase();
			medalRb.setActivityId(activityId);
			medalRb.setMemberId(memberId);
			medalRb.setActivityName(activityDesc);
			medalRb.setCycleStr(activityId + "-" + memberId + ":BigGiftPopularity");
			medalBase.setRewardName(giftPopularity + ActivityConstant.popularityDesc);
			medalBase.setRewardValue(giftPopularity);
			medalBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_POPULARITY.getType());
			drawByPrizeDirectly.drawLotteryNew(medalRb, medalBase, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode());
		} catch (Exception e) {
			logger.error("【劳动最光荣】奖章换豪礼人气值发放重复 memberId={}",memberId, e);
		}
		//发放两张收益券
		for (int i=0;i<giftCount;i++){
			medalRb=new RuleBody();
			medalBase = new RewardsBase();
			medalRb.setActivityId(activityId);
			medalRb.setMemberId(memberId);
			medalRb.setActivityName(activityDesc);
			medalRb.setCycleStr(activityId + "-" + memberId +"-" + i + ":BigGiftCoupon");

			CouponTemplate c = null;
			try {
				c = couponTemplateManager.selectByPrimaryKey(giftTemplateId);
			} catch (ManagerException e) {
				logger.error("【劳动最光荣】奖章换豪礼优惠券不存在 memberId={}",memberId, e);
				continue;
			}
			if(c==null){
				return;
			}
			try {
				medalBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_ANNUALIZED.getType());
				medalBase.setRewardName(c.getAmount() + ActivityConstant.annualizedUnit + ActivityConstant.annualizedDesc);
				medalBase.setTemplateId(c.getId());
				medalBase.setRewardValue(c.getAmount().intValue());
				drawByPrizeDirectly.drawLotteryNew(medalRb, medalBase, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode());
			} catch (Exception e) {
				logger.error("【劳动最光荣】奖章换豪礼优惠券第" + i + "次重复发放 transactionId={},memberId={}",memberId, e);
			}
		}
	}

	@Override
	public ResultDO<ActivityForLabor> laborInit(Long memberId) {
		ResultDO<ActivityForLabor> rDO = new ResultDO<ActivityForLabor>();
		ActivityForLabor activityForLabor=new ActivityForLabor();
		try {
			rDO.setSuccess(false);
			Optional<Activity> annerActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_LABOR_NAME);
			if (!annerActivity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			Activity activity=annerActivity.get();
			Map map = LotteryContainer.getInstance().getObtainConditions(activity, Map.class,
					ActivityConstant.ACTIVITY_LABOR_KEY);
			activityForLabor.setWorkInvest(new BigDecimal(map.get("workInvest").toString()));
			activityForLabor.setStartTime(activity.getStartTime());
			activityForLabor.setEndTime(activity.getEndTime());
			if (memberId != null && memberId > 0){
				//查询签到记录
				MemberCheckQuery memberCheckQuery=new MemberCheckQuery();
				memberCheckQuery.setMemberId(memberId);
				memberCheckQuery.setStartTime(activity.getStartTime());
				memberCheckQuery.setEndTime(activity.getEndTime());
				List<MemberCheck> list= memberCheckMapper.queryMemberCheckListByQuery(memberCheckQuery);
				if (list!=null && list.size()>0){
					List<Date> signlist=new ArrayList<>();
					Date checkDate=null;
					for (MemberCheck mc:list) {
						checkDate=new Date();
						checkDate=mc.getCheckDate();
						signlist.add(checkDate);
					}
					activityForLabor.setSignDate(signlist);
				}
				//查询礼包领取日期
				List<ActivityLottery> activityLotteryList = activityLotteryMapper.queryMemberReceiveLikeCycle(activity.getId(),memberId,memberId+":giftspackage");
				if (activityLotteryList != null && activityLotteryList.size() > 0){
					List<Date> giftsdates=new ArrayList<>();
					Date giftdate=null;
					for (ActivityLottery result:activityLotteryList) {
						giftdate=new Date();
						giftdate=DateUtils.getDateFromString(result.getRemark());
						giftsdates.add(giftdate);
					}
					activityForLabor.setGiftsDate(giftsdates);
				}
				//查询今日投资额
				if (activity.getStartTime().before(new Date())){
					String startdate="";
					String enddate="";
					if(activity.getEndTime().before(new Date())){
						startdate = DateUtils.getStrFromDate(activity.getEndTime(),"yyyy-MM-dd") + " 00:00:00";
						enddate = DateUtils.addDays(activity.getEndTime(),1,"yyyy-MM-dd") + " 00:00:00";
					}else {
						startdate = DateUtils.getStrFromDate(new Date(),"yyyy-MM-dd") + " 00:00:00";
						enddate = DateUtils.addDays(new Date(),1,"yyyy-MM-dd") + " 00:00:00";
					}
					BigDecimal invest = transactionMapper.getMemberTotalInvestNoTransfer(memberId,DateUtils.getDateFromString(startdate),
							DateUtils.getDateFromString(enddate));
					activityForLabor.setInvest(invest);
				}
				Map<String, Object> paraMap = new HashMap<String, Object>();
				paraMap.put("activityId", activity.getId());
				paraMap.put("memberId", memberId);

				//查询是否有资格领取礼包
				paraMap.put("cycleConstraint", activity.getId() + "-" + memberId + "-" + DateUtils.getStrFromDate(new Date(),"yyyy-MM-dd") + ":gifts");
				ActivityLottery al= activityLotteryMapper.selectByMemberActivity(paraMap);
				if (al!=null){
					activityForLabor.setWorkRight(true);
				}else {
					activityForLabor.setWorkRight(false);
				}
				//五一劳模奖章
				paraMap.put("cycleConstraint", activity.getId() + "-" + memberId + ":WorkerMedal");
				ActivityLottery workerMedal= activityLotteryMapper.selectByMemberActivity(paraMap);
				if (workerMedal!=null){
					activityForLabor.setReceiveWorker(true);
				}
				//五一先锋奖章
				paraMap.put("cycleConstraint", activity.getId() + "-" + memberId + ":PioneerMedal");
				ActivityLottery pioneerMedal= activityLotteryMapper.selectByMemberActivity(paraMap);
				if (pioneerMedal != null){
					activityForLabor.setReceivePioneer(true);
				}
				//五一敬业奖章
				paraMap.put("cycleConstraint", activity.getId() + "-" + memberId + ":DedicatedMedal");
				ActivityLottery dedicatedMedal= activityLotteryMapper.selectByMemberActivity(paraMap);
				if (dedicatedMedal != null){
					activityForLabor.setReceiveDedicated(true);
				}
			}
			rDO.setSuccess(true);
			rDO.setResult(activityForLabor);
			return rDO;
		} catch (Exception e) {
			rDO.setSuccess(false);
			logger.error("【劳动最光荣】初始化数据异常, memberId={}", memberId, e);
		}
		return rDO;
	}
}