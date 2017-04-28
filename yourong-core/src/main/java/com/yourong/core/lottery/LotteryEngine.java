package com.yourong.core.lottery;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.google.common.base.Optional;
import com.yourong.common.cache.RedisActivityClient;
import com.yourong.common.constant.RedisConstant;
import com.yourong.common.enums.RemarksEnum;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.SpringContextHolder;
import com.yourong.core.common.MessageClient;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.PopularityInOutLogManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.lottery.container.LotteryContainer;
import com.yourong.core.lottery.draw.DrawByPrizeDirectly;
import com.yourong.core.lottery.model.RewardsBase;
import com.yourong.core.lottery.model.RuleBody;
import com.yourong.core.mc.manager.ActivityLotteryPretreatManager;
import com.yourong.core.mc.manager.CouponManager;
import com.yourong.core.mc.model.ActivityLotteryPretreat;
import com.yourong.core.mc.model.Coupon;

/**
 * 抽奖补发
 */
public class LotteryEngine {
	private static final Logger logger = LoggerFactory.getLogger(LotteryEngine.class);
	private static ThreadPoolTaskExecutor taskExecutor = SpringContextHolder.getBean(ThreadPoolTaskExecutor.class);
	private static DrawByPrizeDirectly drawByPrizeDirectly = SpringContextHolder.getBean(DrawByPrizeDirectly.class);
	private static ActivityLotteryPretreatManager activityLotteryPretreatManager = SpringContextHolder
			.getBean(ActivityLotteryPretreatManager.class);
	private static BalanceManager balanceManager = SpringContextHolder.getBean(BalanceManager.class);
	private static PopularityInOutLogManager popularityInOutLogManager = SpringContextHolder.getBean(PopularityInOutLogManager.class);
	private static CouponManager couponManager = SpringContextHolder.getBean(CouponManager.class);

	private LotteryEngine() {
	}

	private static class LotteryEngineHolder {
		private static final LotteryEngine instance = new LotteryEngine();
	}

	public static final LotteryEngine getEngineInstance() {
		return LotteryEngineHolder.instance;
	}

	/**
	 * 新会员的站外奖励发放
	 * 
	 * @param parameter
	 *            活动必要的参数
	 */
	public void runForOutsider(final Long memberId, final Long mobile) {
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				executeForOutsider(memberId, mobile);
				// 从mc_activity_lottery_pretreat表获取中奖记录
				prizeFromPretreate(memberId, mobile);
			}
		});
	}

	/**
	 * 循环送奖
	 * 
	 * @param memberId
	 * @param mobile
	 * @param activity
	 */
	private void executeForOutsider(Long memberId, Long mobile) {
		logger.info("注册发送站外奖励开始memberId={}, mobile={}", memberId, mobile);
		if (memberId != null && mobile != null) {
			// 从redis获取中奖记录
			Map<String, String> allRewards = RedisActivityClient.getOutsiderAllRewards(mobile);
			if (MapUtils.isNotEmpty(allRewards)) {
				for (Map.Entry<String, String> entry : allRewards.entrySet()) {
					String[] keyArr = entry.getKey().split(RedisConstant.REDIS_SEPERATOR);
					Long activityId = Long.parseLong(keyArr[0]);
					String cycleStr = keyArr[1];
					logger.info("注册发送站外奖励 memberId={}, activityId={}, reward={}", memberId, activityId, entry.getValue());
					try {
						prizeFromRedis(activityId, memberId, mobile, cycleStr, entry.getValue());
					} catch (DuplicateKeyException mysqlE) {
						logger.error("抽奖补发重复领取, memberId={}, mobile={}, activity={}", memberId, mobile, activityId);
					} catch (Exception e) {
						logger.error("循环送奖失败, memberId={}, mobile={}, activity={}", memberId, mobile, activityId, e);
					}
				}
			}
		}
		logger.info("注册发送站外奖励结束memberId={}, mobile={}", memberId, mobile);
	}

	/**
	 * 发放奖励
	 * 
	 * @param activityId
	 * @param memberId
	 * @param cycleStr
	 */
	private void prizeFromRedis(Long activityId, Long memberId, Long mobile, String cycleStr, String rewardCode) throws Exception {
		try {
			Optional<List<Object>> retOpt = LotteryContainer.getInstance().getRewardsListMap(activityId, RewardsBase.class);
			if (retOpt.isPresent()) {
				// 生成通用活动规则
				RuleBody ruleBody = new RuleBody();
				ruleBody.setActivityId(activityId);
				ruleBody.setMemberId(memberId);
				ruleBody.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
				ruleBody.setCycleStr(cycleStr);
				RewardsBase rBase = null;
				for (Object obj : retOpt.get()) {
					RewardsBase rb = (RewardsBase) obj;
					if (rb.getRewardCode().equals(rewardCode)) {
						rBase = rb;
						break;
					}
				}
				if (rBase != null) {
					if (drawByPrizeDirectly.validate(ruleBody, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
						drawByPrizeDirectly.drawLottery(ruleBody, rBase, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode());
					}
					// 已经发放, 删除redis缓存
					RedisActivityClient.delOutsiderAllRewards(activityId, mobile, cycleStr);
				}
			} else {
				logger.error("未找到活动activityId={}", activityId);
			}
		} catch (Exception e) {
			logger.error("发放奖励失败, activityId={}, memberId={}, cycleStr={}", activityId, memberId, cycleStr, e);
			throw e;
		}
	}

	/**
	 * 
	 * @Description:从mc_activity_lottery_pretreat表获取中奖记录进行补发
	 * @param mobile
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年1月18日 下午5:15:30
	 */
	private int prizeFromPretreate(Long memberId, Long mobile) {
		try {
			ActivityLotteryPretreat query = new ActivityLotteryPretreat();
			query.setMobile(mobile);
			List<ActivityLotteryPretreat> pretreateList = activityLotteryPretreatManager.selectUnclaimByMobile(query);
			if (Collections3.isEmpty(pretreateList)) {
				// 没有需要补发的奖励
				return 0;
			}
			for (ActivityLotteryPretreat pretreat : pretreateList) {
				// 发放奖励
				boolean flag = prizeByPretreat(pretreat, memberId);
				if (flag) {
					// 更新领取标记
					activityLotteryPretreatManager.receiveById(pretreat.getId());
				}
			}
		} catch (Exception e) {
			logger.error("从mc_activity_lottery_pretreat表补发失败, memberId={}", memberId, e);
		}
		return 0;
	}

	/**
	 * 兑奖
	 */
	private boolean prizeByPretreat(ActivityLotteryPretreat pretreat, Long memberId) {
		try {
			// 对优惠券、人气值发放在基类直接实现
			if (pretreat.getRewardType() == TypeEnum.ACTIVITY_LOTTERY_TYPE_POPULARITY.getType()) {
				BigDecimal income = new BigDecimal(pretreat.getRewardValue());
				// 人气值
				Balance balance = balanceManager.increaseBalance(TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY, income, memberId);
				// 记录人气值资金流水
				popularityInOutLogManager.insert(memberId, TypeEnum.FIN_POPULARITY_TYPE_ACTIVITY, income, null,
						balance.getAvailableBalance(), pretreat.getActivityId(), pretreat.getRemark()
								+ RemarksEnum.ACTIVITY_LOTTERY_FOR_POPULARITY.getRemarks());
				balanceManager.incrGivePlatformTotalPoint(income);
				// 发送站内信
				MessageClient.sendMsgForSPEnginByReceiveTime(memberId, pretreat.getRemark(), pretreat.getRewardInfo(),
						pretreat.getClaimTime());
			} else if (pretreat.getRewardType() == TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType()
					|| pretreat.getRewardType() == TypeEnum.ACTIVITY_LOTTERY_TYPE_ANNUALIZED.getType()) {
				// 收益券or现金券
				Coupon c = couponManager.receiveCoupon(memberId, pretreat.getActivityId(), pretreat.getCouponTemplateId(), -1L);
				if (c == null) {
					// 优惠券赠送失败;
					logger.error("优惠券赠送失败，接口返回Coupon=null!, memberId={}, activityId={}, templateId={}", memberId, pretreat.getActivityId(),
							pretreat.getCouponTemplateId());
					return false;
				} else {
					// 发送站内信
					MessageClient.sendMsgForSPEnginByReceiveTime(memberId, pretreat.getRemark(), pretreat.getRewardInfo(),
							pretreat.getClaimTime());
				}
			}
			return true;
		} catch (Exception e) {
			logger.error("从mc_activity_lottery_pretreat表获取中奖记录进行补发失败, pretreatId={}, memberId={}", pretreat.getId(), memberId, e);
			return false;
		}
	}
}
