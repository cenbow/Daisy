package com.yourong.core.tc.model.biz;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.domain.AbstractBaseObject;

public class TransactionForActivity extends AbstractBaseObject {

	/** 交易号 **/
	private Long transactionId;

	/** 投资金额 **/
	private BigDecimal investAmount;

	/** 交易时间 **/
	private Date transactionTime;

	/** 活动标识(参考数据字典project_activity_sign) **/
	private Integer activitySign;

	public BigDecimal getInvestAmount() {
		return investAmount;
	}

	public void setInvestAmount(BigDecimal investAmount) {
		this.investAmount = investAmount;
	}

	public Date getTransactionTime() {
		return transactionTime;
	}

	public void setTransactionTime(Date transactionTime) {
		this.transactionTime = transactionTime;
	}

	public Integer getActivitySign() {
		return activitySign;
	}

	public void setActivitySign(Integer activitySign) {
		this.activitySign = activitySign;
	}

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

}