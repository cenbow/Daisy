package com.yourong.core.lottery.validation.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.thirdparty.sinapay.common.util.StringUtil;
import com.yourong.core.lottery.model.RuleBody;
import com.yourong.core.lottery.validation.Verification;
import com.yourong.core.mc.dao.ActivityLotteryMapper;
import com.yourong.core.mc.model.ActivityLottery;

/**
 * 根据周期约束判断会员是否参加过抽奖
 * 
 * @author wangyanji
 *
 */
@Component
public class VerificationByParticipate implements Verification {

	private static final Logger logger = LoggerFactory.getLogger(VerificationByParticipate.class);

	@Autowired
	private ActivityLotteryMapper activityLotteryMapper;

	@Override
	public boolean validate(RuleBody model) throws Exception {
		try {
			if (model == null || model.getActivityId() == null || model.getMemberId() == null || StringUtil.isEmpty(model.getCycleStr())) {
				return false;
			}
			Map<String, Object> paraMap = new HashMap<String, Object>();
			paraMap.put("activityId", model.getActivityId());
			paraMap.put("memberId", model.getMemberId());
			paraMap.put("cycleConstraint", model.getCycleStr());
			ActivityLottery al = activityLotteryMapper.selectByMemberActivity(paraMap);
			if (al != null) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			logger.error("根据周期约束判断会员是否参加过抽奖失败, activityId={}, memberId={}, cycleStr={}", model.getActivityId(), model.getMemberId(),
					model.getCycleStr(), e);
			throw e;
		}
	}

	@Override
	public void prepare(RuleBody model) throws ManagerException {
		try {
			// 记录抽奖记录
			addLotteryRecord(model.getActivityId(), model.getMemberId(), 1, 0, model.getCycleStr());
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 记录抽奖记录
	 * 
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
			activityLotteryMapper.insertSelective(model);
		} catch (DuplicateKeyException mysqlE) {
			throw new ManagerException("重复参加活动");
		} catch (Exception e) {
			throw e;
		}
	}

}
