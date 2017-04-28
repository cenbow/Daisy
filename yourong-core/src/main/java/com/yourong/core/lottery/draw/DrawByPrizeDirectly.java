package com.yourong.core.lottery.draw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.yourong.common.enums.TypeEnum;
import com.yourong.core.lottery.LotteryBase;
import com.yourong.core.lottery.model.RewardsBase;
import com.yourong.core.lottery.model.RuleBody;
import com.yourong.core.lottery.validation.impl.VerificationByParticipate;
import com.yourong.core.lottery.validation.impl.VerificationByPopularity;

/**
 * 直接奖励抽奖类
 * 
 * @author wangyanji
 *
 */
@Component
public class DrawByPrizeDirectly extends LotteryBase {

	private static final Logger logger = LoggerFactory.getLogger(DrawByPrizeDirectly.class);

	@Autowired
	private VerificationByPopularity verificationByPopularity;

	@Autowired
	private VerificationByParticipate verificationByParticipate;

	@Override
	public boolean validate(RuleBody model, String validateType) throws Exception {
		if (model != null) {
			if (TypeEnum.ACTIVITY_LOTTERY_VALIDATE_POPULARITY.getCode().equals(validateType)) {
				return verificationByPopularity.validate(model);
			}
			if (TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode().equals(validateType)) {
				return verificationByParticipate.validate(model);
			}
		}
		return false;
	}

	@Override
	protected Object loadRewards(RuleBody rb) throws Exception {
		return null;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Object drawLottery(RuleBody model, Object inputCode, String validateType) throws Exception {
		try {
			if (TypeEnum.ACTIVITY_LOTTERY_VALIDATE_POPULARITY.getCode().equals(validateType)) {
				verificationByPopularity.prepare(model);
			}
			if (TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode().equals(validateType)) {
				verificationByParticipate.prepare(model);
			}
			// 加载
			RewardsBase rb = (RewardsBase) inputCode;
			// 兑奖
			super.prize(model, rb.getRewardType().intValue(), rb.getRewardName(), rb.getRewardValue() == null ? null : rb.getRewardValue()
					.toString(), rb.getTemplateId());
			return rb;
		} catch (Exception e) {
			logger.info("抽奖主程序失败, memberId={}, activityId={}", model.getMemberId(), model.getActivityId());
			throw e;
		}
	}
	
	
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Object drawLotteryNew(RuleBody model, Object inputCode, String validateType) throws Exception {
		try {
			if (TypeEnum.ACTIVITY_LOTTERY_VALIDATE_POPULARITY.getCode().equals(validateType)) {
				verificationByPopularity.prepare(model);
			}
			if (TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode().equals(validateType)) {
				verificationByParticipate.prepare(model);
			}
			// 加载
			RewardsBase rb = (RewardsBase) inputCode;
			// 兑奖
			super.prizeNew(model, rb.getRewardType().intValue(), rb.getRewardName(), rb.getRewardValue() == null ? null : rb.getRewardValue()
					.toString(), rb.getTemplateId());
			return rb;
		} catch (Exception e) {
			logger.info("抽奖主程序失败, memberId={}, activityId={}", model.getMemberId(), model.getActivityId());
			throw e;
		}
	}

}
