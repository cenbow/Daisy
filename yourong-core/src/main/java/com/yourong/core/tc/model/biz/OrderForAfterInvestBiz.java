package com.yourong.core.tc.model.biz;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.util.DateUtils;
import com.yourong.core.tc.model.Order;

/**
 * 
 * @desc 订单支付以后的业务类
 * @author wangyanji 2016年4月8日上午10:28:05
 */

public class OrderForAfterInvestBiz extends AbstractBaseObject {
	private Order order;

	/** 交易号 **/
	private Long transactionId;

	/** P2P收益周期 **/
	private String profitPeriod;

	/** 投资类型（1-债权；2-直投） **/
	private Integer investType;

	/** 收益天数 **/
	private int profitDays;

	/** 收益类型 **/
	private String profitType;

	/**
	 * 是否发送红包标记
	 */
	private boolean hasRedPackage;

	/**
	 * 项目前置名
	 */
	private String prefixProjectName;
	
	/**
	 * 预期收益
	 */
	private BigDecimal expectAmount;
	
	/**
	 * 募集进度
	 */
	private String progress;
	/**
	 * 	首次还款日期
	 */
	private Date endDate;

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public boolean isHasRedPackage() {
		return hasRedPackage;
	}

	public void setHasRedPackage(boolean hasRedPackage) {
		this.hasRedPackage = hasRedPackage;
	}

	public String getProfitPeriod() {
		return profitPeriod;
	}

	public void setProfitPeriod(String profitPeriod) {
		this.profitPeriod = profitPeriod;
	}

	public Integer getInvestType() {
		return investType;
	}

	public void setInvestType(Integer investType) {
		this.investType = investType;
	}

	public int getProfitDays() {
		return profitDays;
	}

	public void setProfitDays(int profitDays) {
		this.profitDays = profitDays;
	}

	public String getProfitType() {
		return profitType;
	}

	public void setProfitType(String profitType) {
		this.profitType = profitType;
	}

	public String getPrefixProjectName() {
		return prefixProjectName;
	}

	public void setPrefixProjectName(String prefixProjectName) {
		this.prefixProjectName = prefixProjectName;
	}

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public BigDecimal getExpectAmount() {
		return expectAmount;
	}

	public void setExpectAmount(BigDecimal expectAmount) {
		this.expectAmount = expectAmount;
	}

	public String getProgress() {
		return progress;
	}

	public void setProgress(String progress) {
		this.progress = progress;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getEndDateStr(){
		return DateUtils.formatDatetoString(endDate, DateUtils.DATE_FMT_3);
	}
	
}
