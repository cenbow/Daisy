package com.yourong.core.lottery.draw;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Optional;
import com.yourong.common.cache.RedisManager;
import com.yourong.common.constant.ActivityConstant;
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
import com.yourong.core.mc.model.ActivityLotteryResult;

/**
 * 概率抽奖类
 * 
 * @author wangyanji
 *
 */
@Component
public class DrawByProbability extends LotteryBase {

	private static final Logger logger = LoggerFactory.getLogger(DrawByProbability.class);

	@Autowired
	private VerificationByPopularity verificationByPopularity;

	@Autowired
	private VerificationByParticipate verificationByParticipate;

	@Autowired
	private ActivityLotteryResultManager activityLotteryResultManager;

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
	protected RewardsBodyForProbility loadRewards(RuleBody rb) throws Exception {
		return reloadProbabilityRewardsList(rb);
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
			if (rewardIndex.intValue() < model.getRewardsAvailableNum().intValue()) {
				// 中奖
				resultRfp = rfp.getRewardsList().get(rewardIndex.intValue());
			} else {
				// 谢谢惠顾
				resultRfp = new RewardsBodyForProbility();
				resultRfp.setRewardCode("noReward");
				resultRfp.setRewardName("谢谢惠顾");
				resultRfp.setRewardType(0);
			}
			Integer rewardValue = 0;
			if (TypeEnum.ACTIVITY_LOTTERY_TYPE_POPULARITY.getType() == resultRfp.getRewardType()) {
				rewardValue = getResultReward(resultRfp, inputCode);
			} else if (TypeEnum.ACTIVITY_LOTTERY_TYPE_ANNUALIZED.getType() == resultRfp.getRewardType()) {
				rewardValue = 0;
			} else {
				rewardValue = resultRfp.getRewardValue();
			}
			// 兑奖
			super.prize(model, resultRfp.getRewardType(), resultRfp.getRewardName(), rewardValue + "",
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

		if (rb.isProbabilityByAverage()) {
			// 等分概率
			return reloadRewardsByAverage(rb);
		} else {
			String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR
					+ RedisConstant.REDIS_KEY_ACTIVITY_LOTTERY_REWARDS + RedisConstant.REDIS_SEPERATOR
					+ RedisConstant.REDIS_KEY_ACTIVITY_LOTTERY_REWARDS_TOTAL + RedisConstant.REDIS_SEPERATOR + rb.getActivityId();
			boolean isExit = RedisManager.isExitByObjectKey(key);
			if (isExit) {
				return (RewardsBodyForProbility) RedisManager.getObject(key);
			}
			RewardsBodyForProbility rfp = reloadRewardsByProbability(rb);
			if (rfp != null) {
				RedisManager.putObject(key, rfp);
				RedisManager.expireObject(key, ActivityConstant.activityKeyExpire);
			}
			return rfp;
		}
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
	private RewardsBodyForProbility reloadRewardsByProbability(RuleBody rb) throws Exception {
		RewardsBodyForProbility rfp = new RewardsBodyForProbility();
		Optional<List<Object>> opt = LotteryContainer.getInstance().getRewardsListMap(rb.getActivityId(), RewardsBodyForProbility.class);
		if (!opt.isPresent()) {
			throw new Exception("生成奖品序列失败, 未找到");
		}
		List<Object> list = opt.get();
		if (list.size() > rb.getRewardsPoolMaxNum()) {
			throw new Exception("生成奖品序列失败, 奖品序列超过最大设置");
		}
		List<RewardsBodyForProbility> newList = BeanCopyUtil.mapList(list, RewardsBodyForProbility.class);
		rfp.setRewardsList(newList);
		// 拼装概率数组
		BigDecimal start = new BigDecimal(0d);
		List<BigDecimal> rewards = null;
		if (newList.size() < rb.getRewardsPoolMaxNum()) {
			// 自动填充"谢谢惠顾"奖项
			rewards = new ArrayList<BigDecimal>(rb.getRewardsPoolMaxNum());
			for (int i = 0; i < rb.getRewardsPoolMaxNum(); i++) {
				if (i < newList.size()) {
					BigDecimal b = new BigDecimal(Float.toString(newList.get(i).getProbability()));
					start = start.add(b);
					rewards.add(i, start);
				} else {
					break;
				}
			}
			BigDecimal noRewardsNum = new BigDecimal(rb.getRewardsPoolMaxNum().intValue() - newList.size());
			BigDecimal probility = new BigDecimal(1d).subtract(start);
			BigDecimal unitPro = probility.divide(noRewardsNum, 2, BigDecimal.ROUND_DOWN);
			for (int i = newList.size(); i < rb.getRewardsPoolMaxNum() - 1; i++) {
				start = start.add(unitPro);
				rewards.add(i, start);
			}
			BigDecimal last = new BigDecimal(1).subtract(start);
			start = start.add(last);
			rewards.add((rb.getRewardsPoolMaxNum() - 1), start);
		} else {
			rewards = new ArrayList<BigDecimal>(newList.size());
			for (int i = 0; i < newList.size(); i++) {
				start = start.add(new BigDecimal(newList.get(i).getProbability().toString()));
				rewards.add(i, start);
			}
		}
		if (start.doubleValue() != 1d) {
			logger.error("生成奖品序列失败, 最终概率不等于100%, 实际概率={}", start);
			throw new Exception("生成奖品序列失败, 最终概率不等于100%");
		}
		rfp.setProbilityList(rewards);
		return rfp;
	}

	/**
	 * 
	 * @Description:等分概率加载抽奖序列
	 * @param rb
	 * @return
	 * @throws Exception
	 * @author: wangyanji
	 * @time:2015年11月26日 上午10:11:47
	 */
	private RewardsBodyForProbility reloadRewardsByAverage(RuleBody rb) throws Exception {
		RewardsBodyForProbility rfp = new RewardsBodyForProbility();
		Optional<List<Object>> opt = LotteryContainer.getInstance().getRewardsListMap(rb.getActivityId(), RewardsBodyForProbility.class);
		if (!opt.isPresent()) {
			throw new Exception("生成奖品序列失败, 未找到");
		}
		List<RewardsBodyForProbility> newList = BeanCopyUtil.mapList(opt.get(), RewardsBodyForProbility.class);
		if (rb.isExceptDrawedRewards()) {
			exceptDrawedRewards(newList, rb);

		}
		rb.setRewardsPoolMaxNum(newList.size());
		rb.setRewardsAvailableNum(newList.size());
		rfp.setRewardsList(newList);
		// 拼装等分概率数组
		BigDecimal unitPro = new BigDecimal("1").divide(new BigDecimal(newList.size()), 4, BigDecimal.ROUND_DOWN);
		List<BigDecimal> rewards = new ArrayList<BigDecimal>(newList.size());
		BigDecimal start = new BigDecimal(0d);
		for (int i = 0; i < newList.size() - 1; i++) {
			start = start.add(new BigDecimal(unitPro.toString()));
			rewards.add(i, start);
		}
		start = new BigDecimal(1);
		rewards.add(newList.size() - 1, start);
		if (start.doubleValue() != 1d) {
			logger.error("生成奖品序列失败, 最终概率不等于100%, 实际概率={}", start);
			throw new Exception("生成奖品序列失败, 最终概率不等于100%");
		}
		rfp.setProbilityList(rewards);
		return rfp;
	}

	/**
	 * 排除历史已抽奖品
	 * 
	 * @Description:TODO
	 * @param list
	 * @param rb
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月24日 下午5:59:18
	 */
	private List<RewardsBodyForProbility> exceptDrawedRewards(List<RewardsBodyForProbility> list, RuleBody rb) throws Exception {
		// 排除历史奖项
		ActivityLotteryResult model = new ActivityLotteryResult();
		model.setActivityId(rb.getActivityId());
		model.setMemberId(rb.getMemberId());
		List<ActivityLotteryResult> resList = activityLotteryResultManager.getLotteryResultBySelective(model);
		if (Collections3.isEmpty(resList)) {
			return list;
		}
		for (ActivityLotteryResult lr : resList) {
			for (RewardsBodyForProbility rp : list) {
				if (lr.getRewardInfo().equals(rp.getRewardName())) {
					list.remove(rp);
					break;
				}
			}
		}
		return list;
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
