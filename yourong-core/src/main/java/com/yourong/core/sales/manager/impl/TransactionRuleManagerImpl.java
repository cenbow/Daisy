package com.yourong.core.sales.manager.impl;

import java.math.BigDecimal;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.fin.manager.RechargeLogManager;
import com.yourong.core.fin.model.RechargeLog;
import com.yourong.core.sales.manager.TransactionRuleManager;
import com.yourong.core.tc.manager.TransactionManager;

@Service
public class TransactionRuleManagerImpl implements TransactionRuleManager {
	private static final Logger logger = LoggerFactory.getLogger(TransactionRuleManagerImpl.class);
	@Autowired
	TransactionManager transactionManager;
	@Autowired
	RechargeLogManager rechargeLogManager;
	
	@Override
	public boolean isFirstInvestment(Long memberId, Date startTime, Date endTime) {
		try {
			int count = transactionManager.getTransactionCountByMember(memberId);
			if(count >0){//交易笔数一笔且是在活动范围内投资的，则算首次投资。
				// 如果活动之前有投资就不送了
				BigDecimal beforeStartAmount = transactionManager.firstInvestmentAmount(memberId, null, startTime);
				if (beforeStartAmount != null && beforeStartAmount.compareTo(BigDecimal.ZERO) > 0) {
					return false;
				}
				//TODO:待优化
				BigDecimal investmentAmount = firstInvestmentAmount(memberId, startTime, endTime);
				if(investmentAmount != null && investmentAmount.compareTo(BigDecimal.ZERO) > 0){
					return true;
				}
			}
		} catch (Exception e) {
			logger.error("判断会员是否首次投资出现异常", e);
		}
		return false;
	}

	@Override
	public BigDecimal firstInvestmentAmount(Long memberId, Date startTime, Date endTime) {
		try {
			BigDecimal investmentAmount = transactionManager.firstInvestmentAmount(memberId, startTime, endTime);
			return investmentAmount;
		} catch (Exception e) {
			logger.error("查询会员首次投资金额出现异常", e);
		}
		return null;
	}

	@Override
	public BigDecimal countInvestmentAmount(Long memberId) {
		try {
			BigDecimal investmentAmount = transactionManager.countInvestmentAmount(memberId);
			return investmentAmount;
		} catch (Exception e) {
			logger.error("查询会员累计投资金额出现异常", e);
		}
		return null;
	}

	@Override
	public BigDecimal largestInvestmentAmount(Long memberId, Date startTime, Date endTime) {
		try {
			BigDecimal largestInvestmentAmount = transactionManager.largestInvestmentAmount(memberId, startTime, endTime);
			return largestInvestmentAmount;
		} catch (Exception e) {
			logger.error("查询会员单笔投资最高金额出现异常", e);
		}
		return null;
	}

	@Override
	public int countInvestmentProject(Long memberId) {
		try {
			return transactionManager.countInvestmentProject(memberId);
		} catch (Exception e) {
			logger.error("查询会员投资项目个数出现异常", e);
		}
		return 0;
	}

	@Override
	public boolean toDayFirstInvestment(Long memberId) {
		try {
			Long mid = transactionManager.getTodayFirstInvestmentMemberId();
			if(mid != null && mid.equals(memberId)){
				return true;
			}
		} catch (Exception e) {
			logger.error("查询当天第一位投资人出现异常", e);
		}
		return false;
	}

	@Override
	public int countFriendsInvestmentNum(Long memberId, Date startTime, Date endTime) {
		try {
			int count = transactionManager.countFriendsInvestmentNum(memberId, startTime, endTime);
			return count;
		} catch (Exception e) {
			logger.error("统计好友投资笔数出现异常", e);
		}
		return 0;
	}

	@Override
	public BigDecimal friendsFirstInvestmentAmount(Long memberId, Date startTime, Date endTime) {
		try {
			BigDecimal friendsFirstInvestmentAmount = transactionManager.friendsFirstInvestmentAmount(memberId, startTime, endTime);
			return friendsFirstInvestmentAmount;
		} catch (Exception e) {
			logger.error("好友首次投资金额出现异常", e);
		}
		return null;
	}

	@Override
	public BigDecimal friendsCountInvestmentAmount(Long memberId) {
		try {
			BigDecimal friendsCountInvestmentAmount = transactionManager.friendsCountInvestmentAmount(memberId);
			return friendsCountInvestmentAmount;
		} catch (Exception e) {
			logger.error("好友累计投资金额出现异常", e);
		}
		return null;
	}

	@Override
	public BigDecimal friendsInvestmentMaxAmount(Long memberId, Date startTime, Date endTime) {
		try {
			BigDecimal friendsInvestmentMaxAmount = transactionManager.friendsInvestmentMaxAmount(memberId, startTime, endTime);
			return friendsInvestmentMaxAmount;
		} catch (Exception e) {
			logger.error("好友单笔投资最大金额出现异常", e);
		}
		return null;
	}

	@Override
	public int friendsCountInvestmentProjectNum(Long memberId) {
		try {
			int count = transactionManager.friendsCountInvestmentProjectNum(memberId);
			return count;
		} catch (Exception e) {
			logger.error("好友累计投资项目出现异常", e);
		}
		return 0;
	}

	@Override
	public BigDecimal integralPointInvestment(Long memberId,
			String punctuality , Date startTime, Date endTime) {
		try {
			BigDecimal friendsInvestmentMaxAmount = transactionManager.integralPointInvestment(memberId, punctuality, startTime, endTime);
			return friendsInvestmentMaxAmount;
		} catch (Exception e) {
			logger.error("查询整点投资异常", e);
		}
		return null;
	}

	@Override
	public BigDecimal queryAppFirstRechargeAmount(Long memberId,
			Date startTime, Date endTime) {
		try {
			RechargeLog rechargeLog = rechargeLogManager.queryAppFirstRechargeAmount(memberId, startTime, endTime);
			if(rechargeLog != null){
				return rechargeLog.getAmount();
			}
		} catch (ManagerException e) {
			logger.error("查询用户首次在APP直接充值记录异常", e);
		}
		return null;
	}

}
