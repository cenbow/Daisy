package com.yourong.core.lottery;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Optional;
import com.yourong.common.enums.RemarksEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.StringUtil;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.PopularityInOutLogManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.lottery.container.LotteryContainer;
import com.yourong.core.lottery.model.RuleBody;
import com.yourong.core.lottery.validation.impl.VerificationByParticipate;
import com.yourong.core.lottery.validation.impl.VerificationByPopularity;
import com.yourong.core.mc.dao.ActivityLotteryResultMapper;
import com.yourong.core.mc.dao.ActivityLotteryResultNewMapper;
import com.yourong.core.mc.manager.CouponManager;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.mc.model.ActivityLotteryResult;
import com.yourong.core.mc.model.ActivityLotteryResultNew;
import com.yourong.core.mc.model.Coupon;

/**
 * 抽奖抽象类
 * 
 * @author wangyanji
 *
 */
@Component
public abstract class LotteryBase {

	@Autowired
	private BalanceManager balanceManager;

	@Autowired
	private PopularityInOutLogManager popularityInOutLogManager;

	@Autowired
	private CouponManager couponManager;

	@Autowired
	private ActivityLotteryResultMapper activityLotteryResultMapper;
	
	@Autowired
	private ActivityLotteryResultNewMapper activityLotteryResultNewMapper;

	@Autowired
	private static final Logger logger = LoggerFactory.getLogger(LotteryBase.class);

	@Autowired
	private VerificationByPopularity verificationByPopularity;

	@Autowired
	private VerificationByParticipate verificationByParticipate;

	/**
	 * 判断活动操作是否在活动时间内
	 * 
	 * @return
	 */
	public boolean isInActivityTime(Long activityId) throws Exception {
		if (activityId != null) {
			Optional<Activity> act = LotteryContainer.getInstance().getActivity(activityId);
			if (act.isPresent()) {
				if (DateUtils.isDateBetween(DateUtils.getCurrentDate(), act.get().getStartTime(), act.get().getEndTime())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 校验
	 * 
	 * @param model
	 * @param validateType
	 * @return
	 * @throws Exception
	 */
	public abstract boolean validate(RuleBody model, String validateType) throws Exception;

	/**
	 * 加载奖品
	 */
	protected abstract Object loadRewards(RuleBody rb) throws Exception;

	/**
	 * 抽奖
	 */
	public abstract Object drawLottery(RuleBody model, Object inputCode, String validateType) throws Exception;

	/**
	 * 兑奖
	 */
	protected void prize(RuleBody model, int rewardType, String rewardInfo, String rewardResult, Long templateId) throws Exception {
		ActivityLotteryResult alr = new ActivityLotteryResult();
		// 对优惠券、人气值发放在基类直接实现
		if (rewardType == TypeEnum.ACTIVITY_LOTTERY_TYPE_POPULARITY.getType()) {
			// 人气值
			Balance balance = balanceManager.increaseBalance(TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY, new BigDecimal(rewardResult),
					model.getMemberId());
			String activiyName = null;
			if (StringUtil.isNotBlank(model.getActivityName())) {
				activiyName = model.getActivityName();
			} else {
				activiyName = LotteryContainer.getInstance().getActivity(model.getActivityId()).get().getActivityName();
			}
			// 记录人气值资金流水
			popularityInOutLogManager.insert(model.getMemberId(), TypeEnum.FIN_POPULARITY_TYPE_ACTIVITY, new BigDecimal(rewardResult),
					null, balance.getAvailableBalance(), model.getActivityId(),
					activiyName + RemarksEnum.ACTIVITY_LOTTERY_FOR_POPULARITY.getRemarks());
			balanceManager.incrGivePlatformTotalPoint(new BigDecimal(rewardResult));
			alr.setStatus(1);
		} else if (rewardType == TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType()
				|| rewardType == TypeEnum.ACTIVITY_LOTTERY_TYPE_ANNUALIZED.getType()) {
			// 收益券or现金券
			Coupon c = couponManager.receiveCoupon(model.getMemberId(), model.getActivityId(), templateId, -1L);
			if (c == null) {
				// 优惠券赠送失败;
				logger.error("优惠券赠送失败，接口返回Coupon=null!, memberId={}, activityId={}, templateId={}", model.getMemberId(),
						model.getActivityId(), templateId);
				alr.setStatus(0);
			} else {
				alr.setStatus(1);
			}
		} else if (rewardType == TypeEnum.ACTIVITY_LOTTERY_TYPE_NOREWARD.getType()) {
			alr.setStatus(1);
		} else {
			alr.setStatus(0);
		}
		// 记录日志
		alr.setActivityId(model.getActivityId());
		alr.setMemberId(model.getMemberId());
		alr.setCycleStr(model.getCycleStr());
		alr.setChip(model.getDeductValue());
		alr.setRewardInfo(rewardInfo);
		alr.setRewardResult(rewardResult);
		alr.setRewardType(rewardType);
		alr.setRewardId(model.getRewardId());
		if (StringUtil.isNotBlank(model.getDeductRemark())) {
			alr.setRemark(model.getDeductRemark());
		}
		logger.info("活动发放奖励, activityId={}, memberId={}, cycleStr={}, rewardInfo={}", model.getActivityId(), model.getMemberId(),
				model.getCycleStr(), rewardInfo);
		activityLotteryResultMapper.insertSelective(alr);
	}
	
	
	protected void prizeNew(RuleBody model, int rewardType, String rewardInfo, String rewardResult, Long templateId) throws Exception {
		ActivityLotteryResultNew alr = new ActivityLotteryResultNew();
		// 对优惠券、人气值发放在基类直接实现
		if (rewardType == TypeEnum.ACTIVITY_LOTTERY_TYPE_POPULARITY.getType()) {
			// 人气值
			Balance balance = balanceManager.increaseBalance(TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY, new BigDecimal(rewardResult),
					model.getMemberId());
			String activiyName = null;
			if (StringUtil.isNotBlank(model.getActivityName())) {
				activiyName = model.getActivityName();
			} else {
				activiyName = LotteryContainer.getInstance().getActivity(model.getActivityId()).get().getActivityName();
			}
			// 记录人气值资金流水
			popularityInOutLogManager.insert(model.getMemberId(), TypeEnum.FIN_POPULARITY_TYPE_ACTIVITY, new BigDecimal(rewardResult),
					null, balance.getAvailableBalance(), model.getActivityId(),
					activiyName + RemarksEnum.ACTIVITY_LOTTERY_FOR_POPULARITY.getRemarks());
			balanceManager.incrGivePlatformTotalPoint(new BigDecimal(rewardResult));
			alr.setStatus(1);
		} else if (rewardType == TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType()
				|| rewardType == TypeEnum.ACTIVITY_LOTTERY_TYPE_ANNUALIZED.getType()) {
			// 收益券or现金券
			Coupon c = couponManager.receiveCoupon(model.getMemberId(), model.getActivityId(), templateId, -1L);
			if (c == null) {
				// 优惠券赠送失败;
				logger.error("优惠券赠送失败，接口返回Coupon=null!, memberId={}, activityId={}, templateId={}", model.getMemberId(),
						model.getActivityId(), templateId);
				alr.setStatus(0);
			} else {
				alr.setStatus(1);
			}
		} else if (rewardType == TypeEnum.ACTIVITY_LOTTERY_TYPE_NOREWARD.getType()) {
			alr.setStatus(1);
		} else {
			alr.setStatus(0);
		}
		// 记录日志
		alr.setActivityId(model.getActivityId());
		alr.setMemberId(model.getMemberId());
		alr.setCycleStr(model.getCycleStr());
		alr.setChip(model.getDeductValue());
		alr.setRewardInfo(rewardInfo);
		alr.setRewardResult(rewardResult);
		alr.setRewardType(rewardType);
		alr.setRewardId(model.getRewardId());
		if (StringUtil.isNotBlank(model.getDeductRemark())) {
			alr.setRemark(model.getDeductRemark());
		}
		logger.info("活动发放奖励, activityId={}, memberId={}, cycleStr={}, rewardInfo={}", model.getActivityId(), model.getMemberId(),
				model.getCycleStr(), rewardInfo);
		activityLotteryResultNewMapper.insertSelective(alr);
	}

	

}
