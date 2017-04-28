package com.yourong.api.dto;

import java.math.BigDecimal;
import java.util.Date;

public class TransactionDetailForMemberDto {

	/** 交易编号 **/
	private Long transactionId;

	/** 项目ID **/
	private Long projectId;

	/** 项目名称 **/
	private String projectName;

	/** 投资金额 **/
	private BigDecimal investAmount;
	
	/** 订单编号 **/
	private String orderNo;

	/** 交易时间 **/
	private Date transactionTime;

	/** 项目到期日 **/
	private Date endDate;
	
	/**收益天数**/
	private int totalDays;
	
	/** 年化收益 **/
	private BigDecimal annualizedRate;
	
	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public BigDecimal getInvestAmount() {
		return investAmount;
	}

	public void setInvestAmount(BigDecimal investAmount) {
		this.investAmount = investAmount;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Date getTransactionTime() {
		return transactionTime;
	}

	public void setTransactionTime(Date transactionTime) {
		this.transactionTime = transactionTime;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getTotalDays() {
		return totalDays;
	}

	public void setTotalDays(int totalDays) {
		this.totalDays = totalDays;
	}

	public BigDecimal getAnnualizedRate() {
		return annualizedRate;
	}

	public void setAnnualizedRate(BigDecimal annualizedRate) {
		this.annualizedRate = annualizedRate;
	}
	
	
}
