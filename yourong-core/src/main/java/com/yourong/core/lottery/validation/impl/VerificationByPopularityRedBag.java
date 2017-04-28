package com.yourong.core.lottery.validation.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.ic.manager.ProjectExtraManager;
import com.yourong.core.lottery.model.PopularityRedBag;
import com.yourong.core.lottery.model.RuleBody;
import com.yourong.core.lottery.validation.Verification;

/**
 * 根据周期约束判断会员是否参加过抽奖
 * 
 * @author wangyanji
 *
 */
@Component
public class VerificationByPopularityRedBag implements Verification {

	private static final Logger logger = LoggerFactory.getLogger(VerificationByPopularityRedBag.class);

	@Autowired
	private ProjectExtraManager projectExtraManager;

	@Override
	public boolean validate(RuleBody model) throws Exception {
		Long sourceId = null;
		boolean returnBool = true;
		try {
			PopularityRedBag rule = (PopularityRedBag) model.getVerificationObj();
			sourceId = rule.getSourceId();
			// 判断投资额是否满足
			if (rule.getMinInvestAmount() != null) {
				if (rule.getMinInvestAmount().compareTo(rule.getRedBagAmount()) == 1) {
					return false;
				}
			}
			// 判断是否排除活动项目
			if (rule.getExceptActivitySign() != null && rule.getExceptActivitySign().length > 0) {
				returnBool = verificationExceptActivitySign(rule.getProjectId(), rule.getExceptActivitySign());
			}
			return returnBool;
		} catch (Exception e) {
			logger.error("人气值红包校验失败, activityId={}, memberId={}, sourceId={}", model.getActivityId(), model.getMemberId(), sourceId, e);
			throw e;
		}
	}

	@Override
	public void prepare(RuleBody model) throws ManagerException {

	}

	/**
	 * 
	 * @Description:判断是否排除活动项目
	 * @param projectId
	 * @return
	 * @author: wangyanji
	 * @throws ManagerException
	 * @time:2016年1月7日 下午9:04:36
	 */
	private boolean verificationExceptActivitySign(Long projectId, int[] exceptActivitySigns) throws ManagerException {
		int sign = projectExtraManager.getProjectActivitySign(projectId);
		for (int n : exceptActivitySigns) {
			if (sign == n) {
				return false;
			}
		}
		return true;
	}
}
