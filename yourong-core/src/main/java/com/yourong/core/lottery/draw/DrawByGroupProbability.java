package com.yourong.core.lottery.draw;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rop.thirdparty.com.google.common.collect.Lists;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.yourong.common.cache.RedisManager;
import com.yourong.common.constant.RedisConstant;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.Collections3;
import com.yourong.core.lottery.LotteryBase;
import com.yourong.core.lottery.container.LotteryContainer;
import com.yourong.core.lottery.model.RewardsBodyForProbility;
import com.yourong.core.lottery.model.RuleBody;
import com.yourong.core.lottery.validation.impl.VerificationByParticipate;
import com.yourong.core.lottery.validation.impl.VerificationByPopularity;
import com.yourong.core.mc.manager.ActivityLotteryResultManager;

/**
 * 分组概率抽奖类
 * 
 * @author wangyanji
 *
 */
@Component
public class DrawByGroupProbability extends LotteryBase {

	private static final Logger logger = LoggerFactory.getLogger(DrawByGroupProbability.class);

	@Autowired
	private VerificationByPopularity verificationByPopularity;

	@Autowired
	private VerificationByParticipate verificationByParticipate;

	@Autowired
	private ActivityLotteryResultManager activityLotteryResultManager;

	@Override
	public boolean validate(RuleBody model, String validateType) throws Exception {
		if (Optional.of(model).isPresent()) {
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
	protected RewardsBodyForProbility loadRewards(RuleBody rb) throws Exception {
		return reloadProbabilityRewardsList(rb);
	}

	@Override
	public Object drawLottery(RuleBody model, Object inputCode, String validateType) throws Exception {
		try {
			if (TypeEnum.ACTIVITY_LOTTERY_VALIDATE_POPULARITY.getCode().equals(validateType)) {
				verificationByPopularity.prepare(model);
			}
			if (TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode().equals(validateType)) {
				verificationByParticipate.prepare(model);
			}
			// 加载
			RewardsBodyForProbility rfp = loadRewards(model);
			// 抽奖
			Integer rewardIndex = null;
			double randomNumber = Math.random();
			for (int j = 0; j < rfp.getProbilityList().size(); j++) {
				if (randomNumber <= rfp.getProbilityList().get(j).doubleValue()) {
					rewardIndex = new Integer(j);
					break;
				}
			}
			RewardsBodyForProbility resultRfp = null;
			// 中奖
			resultRfp = rfp.getRewardsList().get(rewardIndex.intValue());
			// 兑奖
			super.prize(model, resultRfp.getRewardType(), resultRfp.getRewardName(), getResultReward(resultRfp, inputCode) + "",
					resultRfp.getTemplateId());
			return resultRfp;
		} catch (Exception e) {
			logger.error("抽奖主程序失败, memberId={}, activityId={}", model.getMemberId(), model.getActivityId());
			throw e;
		}
	}

	/**
	 * 生成抽奖序列
	 * 
	 * @param rb
	 * @throws Exception
	 */
	private RewardsBodyForProbility reloadProbabilityRewardsList(RuleBody rb) throws Exception {

		String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR + RedisConstant.REDIS_KEY_ACTIVITY_LOTTERY_REWARDS
				+ RedisConstant.REDIS_SEPERATOR + RedisConstant.REDIS_KEY_ACTIVITY_LOTTERY_REWARDS_TOTAL + RedisConstant.REDIS_SEPERATOR
				+ rb.getActivityId() + RedisConstant.REDIS_SEPERATOR + rb.getRewardGroup();
		boolean isExit = RedisManager.isExitByObjectKey(key);
		if (isExit) {
			return (RewardsBodyForProbility) RedisManager.getObject(key);
		}
		RewardsBodyForProbility rfp = reloadRewardsByProbability(rb);
		if (rfp != null) {
			RedisManager.putObject(key, rfp);
			RedisManager.expireObject(key, 1200);
		}
		return rfp;

	}

	/**
	 * 
	 * @Description:按照配置的概率加载抽奖序列
	 * @param rb
	 * @return
	 * @throws Exception
	 * @author: wangyanji
	 * @time:2015年11月26日 上午10:11:13
	 */
	private RewardsBodyForProbility reloadRewardsByProbability(final RuleBody rb) throws Exception {
		RewardsBodyForProbility rfp = new RewardsBodyForProbility();
		Optional<List<Object>> opt = LotteryContainer.getInstance().getRewardsListMap(rb.getActivityId(), RewardsBodyForProbility.class);
		if (!opt.isPresent()) {
			throw new Exception("生成奖品序列失败, 未找到");
		}
		List<Object> list = opt.get();
		List<RewardsBodyForProbility> rewardList = new ArrayList<RewardsBodyForProbility>();
		BeanCopyUtil.copy(list, rewardList);
		Collection<RewardsBodyForProbility> filterList = Collections2.filter(rewardList, new Predicate<RewardsBodyForProbility>() {

			@Override
			public boolean apply(RewardsBodyForProbility input) {
				if (input.getRewardsGroup().equals(rb.getRewardGroup())) {
					return true;
				}
				return false;
			}
		});
		if (Collections3.isNotEmpty(filterList)) {
			ImmutableList<RewardsBodyForProbility> resultList = ImmutableList.copyOf(filterList);
			rfp.setRewardsList(resultList);
			// 拼装概率数组
			BigDecimal start = new BigDecimal(0d);
			List<BigDecimal> rewards = Lists.newArrayListWithCapacity(resultList.size());
			for (int i = 0, size = resultList.size(); i < size; i++) {
				start = start.add(new BigDecimal(resultList.get(i).getProbability().toString()));
				rewards.add(i, start);
			}
			if (start.doubleValue() != 1d) {
				logger.error("生成奖品序列失败, 最终概率不等于100%, 实际概率={}", start);
				throw new Exception("生成奖品序列失败, 最终概率不等于100%");
			}
			rfp.setProbilityList(rewards);
		} else {
			logger.error("生成奖品序列失败, 未找到匹配的分组 rewardGroup={}", rb.getRewardGroup());
			throw new Exception("生成奖品序列失败, 未找到匹配的分组");
		}
		return rfp;
	}

	/**
	 * 运算中奖结果
	 * 
	 * @param model
	 * @param inputCode
	 * @return
	 */
	private int getResultReward(RewardsBodyForProbility model, Object inputCode) {
		if (model.getRewardType() != TypeEnum.ACTIVITY_LOTTERY_TYPE_POPULARITY.getType()) {
			return 0;
		}
		int retValue = model.getRewardValue().intValue();
		if (inputCode != null && inputCode.getClass() == Integer.class) {
			switch (model.getOperator()) {
			case "add":
				retValue += ((Integer) inputCode).intValue();
				break;
			case "subtract":
				retValue -= ((Integer) inputCode).intValue();
				break;
			case "multiply":
				retValue *= ((Integer) inputCode).intValue();
				break;
			case "divide":
				retValue /= ((Integer) inputCode).intValue();
				break;
			case "equal":
				// ...
				break;
			default:
				break;
			}
		}
		return retValue;
	}

}
