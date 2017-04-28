package com.yourong.core.sales.manager.impl;

import java.math.BigDecimal;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.sales.manager.ProjectRuleManager;
import com.yourong.core.tc.manager.TransactionManager;

@Service
public class ProjectRuleManagerImpl implements ProjectRuleManager {
	private static final Logger logger = LoggerFactory.getLogger(ProjectRuleManagerImpl.class);
	@Autowired
	TransactionManager transactionManager;

	@Override
	public Long firstInvestmentProjectMember(Long projectId, Date startTime, Date endTime) {
		try {
			return transactionManager.firstInvestmentProjectMember(projectId, startTime, endTime);
		} catch (ManagerException e) {
			logger.error("查询项目第一个投资用户异常", e);
		}
		return -1L;
	}

	@Override
	public Long lastInvestmentProjectMember(Long projectId, Date startTime, Date endTime) {
		try {
			return transactionManager.lastInvestmentProjectMember(projectId, startTime, endTime);
		} catch (ManagerException e) {
			logger.error("查询项目最后一个投资用户异常", e);
		}
		return -1L;
	}

	@Override
	public BigDecimal investmentMaxAmountProject(Long projectId, Long memberId) {
		try {
			return transactionManager.investmentMaxAmountProject(projectId, memberId);
		} catch (ManagerException e) {
			logger.error("查询用户投资项目最大金额异常", e);
		}
		return null;
	}

	@Override
	public BigDecimal friendsInvestmentMaxAmountProject(Long projectId,
			Long memberId) {
		try {
			return transactionManager.friendsInvestmentMaxAmountProject(projectId, memberId);
		} catch (ManagerException e) {
			logger.error("查询好友单笔投资项目最大金额异常", e);
		}
		return null;
	}

	@Override
	public BigDecimal integralPointInvestmentProject(Long projectId,
			Long memberId, String punctuality , Date startTime, Date endTime) {
		try {
			return transactionManager.integralPointInvestmentProject(projectId, memberId, punctuality, startTime, endTime);
		} catch (ManagerException e) {
			logger.error("查询项目整点投资数据异常", e);
		}
		return null;
	}

}
