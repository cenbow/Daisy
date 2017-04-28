package com.yourong.core.lottery.validation.impl;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.PopularityInOutLogManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.lottery.model.RuleBody;
import com.yourong.core.lottery.validation.Verification;
import com.yourong.core.mc.dao.ActivityLotteryMapper;
import com.yourong.core.mc.model.ActivityLottery;

/**
 * 根据人气值判断会员是否参加过抽奖
 * @author wangyanji
 *
 */
@Component
public class VerificationByPopularity implements Verification {
	
	private static final Logger logger = LoggerFactory.getLogger(VerificationByPopularity.class);
	
	@Autowired
	private BalanceManager balanceManager;
	
	@Autowired
	private ActivityLotteryMapper activityLotteryMapper;
	
	@Autowired
	private PopularityInOutLogManager popularityInOutLogManager;
	
	@Override
	public boolean validate(RuleBody model) throws Exception {
		try {
			if(model == null || model.getActivityId() == null || model.getMemberId() == null || model.getDeductMode() == null || model.getDeductValue() == null) {
				return false;
			}
			if(model.getDeductMode().intValue() == StatusEnum.LOTTERY_DEDUCT_MODE_POPULAR.getStatus()) {
				//校验当前人气值是否够扣
				Balance balance = balanceManager.queryBalance(model.getMemberId(), TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY);
				if(balance != null){
					if(balance.getAvailableBalance().intValue() >= model.getDeductValue().intValue()) {
						return true;
					}
				}
			}
		} catch(ManagerException e) {
			logger.error("抽奖扣除人气值校验失败, activityId={}, memberId={}", model.getActivityId(), model.getMemberId(), e);
			throw e;
		}
		return false;
	}
	
	@Override
	public void prepare(RuleBody model) throws Exception {
		try {
			//扣除人气值即可参加
			BigDecimal deductValue = new BigDecimal(model.getDeductValue());
			// 扣减人气值
			balanceManager.updateByIdAndTypeForLotty(deductValue, model.getMemberId());
			// 记录人气值资金流水
			Balance balance = balanceManager.queryBalance(model.getMemberId(), TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY);
			popularityInOutLogManager.insert(model.getMemberId(), TypeEnum.FIN_POPULARITY_TYPE_ACTIVITY, null,
					deductValue, balance.getAvailableBalance(), model.getActivityId(), model.getDeductRemark());
			//记录抽奖记录
			addLotteryRecord(model.getActivityId(), model.getMemberId(), 1, 0, model.getCycleStr());
		} catch(ManagerException e) {
			logger.error("抽奖扣除人气值失败, activityId={}, memberId={}", model.getActivityId(), model.getMemberId(), e);
			throw e;
		}
	}
	
	/**
	 * 记录抽奖记录
	 * @param activityId
	 * @param memberId
	 * @param totalNum
	 * @param realNum
	 * @param jsonCondition
	 * @throws ManagerException
	 */
	private void addLotteryRecord(Long activityId, Long memberId, int totalNum, int realNum, String jsonCondition) throws ManagerException {
		try {
			ActivityLottery model = new ActivityLottery();
			model.setActivityId(activityId);
			model.setMemberId(memberId);
			model.setTotalCount(totalNum);
			model.setRealCount(realNum);
			model.setCycleConstraint(jsonCondition);
			if(activityLotteryMapper.checkExistLottery(model) == null) {
				//第一次获得抽奖机会
				activityLotteryMapper.insertSelective(model);
			} else {
				//累积抽奖机会
				activityLotteryMapper.updateByActivityAndMember(model);
			}
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

}
