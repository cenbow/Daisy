package com.yourong.core.tc.model.biz;

import java.math.BigDecimal;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * 收益日历交易本息的DO
 * 
 * @author Leon Ray 2014年9月26日-下午5:53:14
 */
public class TransactionInterestForMonth extends AbstractBaseObject {
	
	/** 月份 **/
	private String countYM;
	/** 总计支付利息 **/
	private BigDecimal totalPayableInterest;
	/** 总计支付本金 **/
	private BigDecimal totalPayablePrincipal;
	
	public String getCountYM() {
		return countYM;
	}
	public void setCountYM(String countYM) {
		this.countYM = countYM;
	}
	public BigDecimal getTotalPayableInterest() {
		return totalPayableInterest;
	}
	public void setTotalPayableInterest(BigDecimal totalPayableInterest) {
		this.totalPayableInterest = totalPayableInterest;
	}
	public BigDecimal getTotalPayablePrincipal() {
		return totalPayablePrincipal;
	}
	public void setTotalPayablePrincipal(BigDecimal totalPayablePrincipal) {
		this.totalPayablePrincipal = totalPayablePrincipal;
	}

}