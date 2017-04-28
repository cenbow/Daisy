package com.yourong.core.tc.model.biz;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * 收益日历交易本息的DO
 * 
 * @author Leon Ray 2014年9月26日-下午5:53:14
 */
public class TransactionInterestForDate extends AbstractBaseObject {
	
	/** 还款日期 **/
	private String dateStr;
	/** 还款状态 **/
	private Integer repaymentStatus;
	
	/** 投资，当日可还款标记 **/
	private Integer investRepaymentStatus;

	public String getDateStr() {
		return dateStr;
	}

	public void setDateStr(String dateStr) {
		this.dateStr = dateStr;
	}

	public Integer getRepaymentStatus() {
		return repaymentStatus;
	}

	public void setRepaymentStatus(Integer repaymentStatus) {
		this.repaymentStatus = repaymentStatus;
	}

	/**
	 * @return the investRepaymentStatus
	 */
	public Integer getInvestRepaymentStatus() {
		return investRepaymentStatus;
	}

	/**
	 * @param investRepaymentStatus the investRepaymentStatus to set
	 */
	public void setInvestRepaymentStatus(Integer investRepaymentStatus) {
		this.investRepaymentStatus = investRepaymentStatus;
	}

}