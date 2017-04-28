package com.yourong.core.sales.rule;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.util.DateUtils;
import com.yourong.common.util.SpringContextHolder;
import com.yourong.core.sales.SPParameter;
import com.yourong.core.sales.SPRuleMethod;
import com.yourong.core.sales.annotation.SPMethod;
import com.yourong.core.sales.manager.TransactionRuleManager;

public class TransactionSalesRule extends SPRuleBase{
	private static TransactionRuleManager transactionRuleManager = SpringContextHolder.getBean(TransactionRuleManager.class);
	
	@Override
	public boolean execute(SPParameter parameter) {
		return super.build(parameter);
	}

	
	/**
	 * 是否首次投资
	 * @param parameter
	 * @return
	 */
	@SPMethod(name="shouCiTouzi")
	public boolean isFirstInvestment(SPParameter parameter){
		return transactionRuleManager.isFirstInvestment(parameter.getMemberId(), parameter.getStartTime(), parameter.getEndTime());
	}
	
	/**
	 * 首次投资金额
	 * @param parameter
	 * @param method
	 * @return
	 */
	@SPMethod(name="shouCiTouZiYuan")
	public boolean firstInvestmentAmount(SPParameter parameter, SPRuleMethod method){
		if(isFirstInvestment(parameter)){
			BigDecimal investmentAmount = transactionRuleManager.firstInvestmentAmount(parameter.getMemberId(), parameter.getStartTime(), parameter.getEndTime());
			BigDecimal amount = new BigDecimal(method.getValue());
			if(investmentAmount != null && investmentAmount.compareTo(amount) >=0){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 累计投资金额
	 * @param parameter
	 * @param method
	 * @return
	 */
	@SPMethod(name="leiJiTouZiYuan")
	public boolean countInvestmentAmount(SPParameter parameter, SPRuleMethod method){
		BigDecimal investmentAmount = transactionRuleManager.countInvestmentAmount(parameter.getMemberId());
		BigDecimal amount = new BigDecimal(method.getValue());
		if(investmentAmount != null && investmentAmount.compareTo(amount) >=0){
			return true;
		}
		return false;
	}
	
	/**
	 * 单笔投资最高金额
	 * @param parameter
	 * @param method
	 * @return
	 */
	@SPMethod(name="danBiTouZiYuan")
	public boolean largestInvestmentAmount(SPParameter parameter, SPRuleMethod method){
		BigDecimal investmentAmount = transactionRuleManager.largestInvestmentAmount(parameter.getMemberId(), parameter.getStartTime(), parameter.getEndTime());
		BigDecimal amount = new BigDecimal(method.getValue());
		if(investmentAmount != null && investmentAmount.compareTo(amount) >=0){
			return true;
		}
		return false;
	}
	
	/**
	 * 累计投资项目
	 * @param parameter
	 * @param method
	 * @return
	 */
	@SPMethod(name="leiJiTouZiXM")
	public boolean countInvestmentProject(SPParameter parameter, SPRuleMethod method){
		int num = transactionRuleManager.countInvestmentProject(parameter.getMemberId());
		int shuLiang = Integer.parseInt(method.getValue());
		if(num >= shuLiang){
			return true;
		}
		return false;
	}
	
	/**
	 * 每天第一个投资
	 * @param parameter
	 * @return
	 */
	@SPMethod(name="meiTianDiYiTZ")
	public boolean toDayFirstInvestment(SPParameter parameter){
		return transactionRuleManager.toDayFirstInvestment(parameter.getMemberId());
	}
	
	/**
	 * 好友首次投资
	 * @param parameter
	 * @return
	 */
	@SPMethod(name="haoYouShouCiTZ")
	public boolean countFriendsInvestmentNum(SPParameter parameter){
		int num = transactionRuleManager.countFriendsInvestmentNum(parameter.getMemberId(), DateUtils.getDateFromString("2014-11-19 00:00:00"), DateUtils.getDateFromString("2088-11-19 00:00:00"));
		if(num == 1){
			if(transactionRuleManager.countFriendsInvestmentNum(parameter.getMemberId(), parameter.getStartTime(), parameter.getEndTime()) == 1){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 好友首次投资金额
	 * @param parameter
	 * @param method
	 * @return
	 */
	@SPMethod(name="haoYouShouCiTZY")
	public boolean friendsFirstInvestmentAmount(SPParameter parameter, SPRuleMethod method){
		int num = transactionRuleManager.countFriendsInvestmentNum(parameter.getMemberId(), DateUtils.getDateFromString("2014-11-19 00:00:00"), DateUtils.getDateFromString("2088-11-19 00:00:00"));
		if(num == 1){
			BigDecimal friendsFirstInvestmentAmount = transactionRuleManager.friendsFirstInvestmentAmount(parameter.getMemberId(), parameter.getStartTime(), parameter.getEndTime());
			BigDecimal amount = new BigDecimal(method.getValue());
			if(friendsFirstInvestmentAmount != null && friendsFirstInvestmentAmount.compareTo(amount) >=0){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 好友累计投资金额
	 * @param parameter
	 * @param method
	 * @return
	 */
	@SPMethod(name="haoYouLeiJiTZY")
	public boolean friendsCountInvestmentAmount(SPParameter parameter, SPRuleMethod method){
		BigDecimal friendsCountInvestmentAmount = transactionRuleManager.friendsCountInvestmentAmount(parameter.getMemberId());
		BigDecimal amount = new BigDecimal(method.getValue());
		if(friendsCountInvestmentAmount != null && friendsCountInvestmentAmount.compareTo(amount) >=0){
			return true;
		}
		return false;
	}
	
	/**
	 * 好友单笔投资最大金额
	 * @param parameter
	 * @param method
	 * @return
	 */
	@SPMethod(name="haoYouDanBiTZY")
	public boolean friendsInvestmentMaxAmount(SPParameter parameter, SPRuleMethod method){
		BigDecimal friendsInvestmentMaxAmount = transactionRuleManager.friendsInvestmentMaxAmount(parameter.getMemberId(), parameter.getStartTime(), parameter.getEndTime());
		BigDecimal amount = new BigDecimal(method.getValue());
		if(friendsInvestmentMaxAmount != null && friendsInvestmentMaxAmount.compareTo(amount) >=0){
			return true;
		}
		return false;
	}
	
	/**
	 * 好友累计投资项目
	 * @param parameter
	 * @param method
	 * @return
	 */
	@SPMethod(name="haoYouLeiJiTZXM")
	public boolean friendsCountInvestmentProjectNum(SPParameter parameter, SPRuleMethod method){
		int num = transactionRuleManager.friendsCountInvestmentProjectNum(parameter.getMemberId());
		int shuLiang = Integer.parseInt(method.getValue());
		if(num >= shuLiang){
			return true;
		}
		return false;
	}
	
	/**
	 * 整点投资
	 * @param parameter
	 * @param method
	 * @return
	 */
	@SPMethod(name="zhengDianTouZi")
	public boolean integralPointInvestment(SPParameter parameter, SPRuleMethod method){
		BigDecimal integralPointInvestment = transactionRuleManager.integralPointInvestment(parameter.getMemberId(), method.getValue(), parameter.getStartTime(), parameter.getEndTime());
		if(integralPointInvestment != null && integralPointInvestment.compareTo(BigDecimal.ZERO) > 0){
			return true;
		}
		return false;
	}
	
	/**
	 * 查询用户首次在APP直接充值记录
	 * @param parameter
	 * @param method
	 * @return
	 */
	@SPMethod(name="appShouCiChongZhi")
	public boolean queryAppFirstRechargeAmount(SPParameter parameter, SPRuleMethod method){
		BigDecimal rechargeAmount = transactionRuleManager.queryAppFirstRechargeAmount(parameter.getMemberId(), parameter.getStartTime(), parameter.getEndTime());
		if(rechargeAmount != null && rechargeAmount.compareTo(new BigDecimal(method.getValue())) >= 0){
			return true;
		}
		return false;
	}
	
}
