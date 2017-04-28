package com.yourong.core.sales.manager;

import java.math.BigDecimal;
import java.util.Date;

public interface TransactionRuleManager {
	/**
	 * 是否首次投资
	 * @param memberId
	 * @return
	 */
	public boolean isFirstInvestment(Long memberId, Date startTime, Date endTime);
	
	/**
	 * 首次投资金额
	 * @param memberId
	 * @param amount
	 * @return
	 */
	public BigDecimal firstInvestmentAmount(Long memberId, Date startTime, Date endTime);
	
	/**
	 * 累计投资金额
	 * @param memberId
	 * @param amount
	 * @return
	 */
	public BigDecimal countInvestmentAmount(Long memberId);
	
	/**
	 * 单笔投资最高金额
	 * @param memberId
	 * @param amount
	 * @return
	 */
	public BigDecimal largestInvestmentAmount(Long memberId, Date startTime, Date endTime);
	
	/**
	 * 累计投资项目
	 * @param memberId
	 * @param number
	 * @return
	 */
	public int countInvestmentProject(Long memberId);
	
	/**
	 * 第一个投资
	 * @param memberId
	 * @return
	 */
	public boolean toDayFirstInvestment(Long memberId);
	
	/**
	 * 统计好友投资笔数
	 * @param memberId
	 * @return
	 */
	public int countFriendsInvestmentNum(Long memberId, Date startTime, Date endTime);
	
	/**
	 * 好友首次投资金额
	 * @param memberId
	 * @return
	 */
	public BigDecimal friendsFirstInvestmentAmount(Long memberId, Date startTime, Date endTime);
	
	/**
	 * 好友累计投资金额
	 * @param memberId
	 * @return
	 */
	public BigDecimal friendsCountInvestmentAmount(Long memberId);
	
	/**
	 * 好友单笔投资最大金额
	 * @param memberId
	 * @return
	 */
	public BigDecimal friendsInvestmentMaxAmount(Long memberId, Date startTime, Date endTime);
	
	/**
	 * 好友累计投资项目
	 * @param memberId
	 * @return
	 */
	public int friendsCountInvestmentProjectNum(Long memberId);
	
	/**
	 * 整点投资
	 * @param memberId
	 * @param punctuality
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public BigDecimal integralPointInvestment(Long memberId, String punctuality , Date startTime, Date endTime);
	
	/**
	 * 查询用户首次在APP直接充值记录
	 * @param memberId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public BigDecimal queryAppFirstRechargeAmount(Long memberId, Date startTime, Date endTime);
}
