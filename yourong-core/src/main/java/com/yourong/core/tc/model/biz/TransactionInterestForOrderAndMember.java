package com.yourong.core.tc.model.biz;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 用于前台下订单的本息信息和用户中心交易本息信息
 * @author Leon Ray
 * 2014年10月8日-下午6:35:28
 */
public class TransactionInterestForOrderAndMember implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 4941487938805576752L;

    /**计息开始时间**/
    private String startDate;

    /**计息结束时间**/
    private String endDate;

    /**支付天数**/
    private Integer days;

    /**应付利息**/
    private BigDecimal payableInterest;

    /**应付本金**/
    private BigDecimal payablePrincipal;
    
    /**状态**/
    private int status;

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public Integer getDays() {
		return days;
	}

	public void setDays(Integer days) {
		this.days = days;
	}

	public BigDecimal getPayableInterest() {
		return payableInterest;
	}

	public void setPayableInterest(BigDecimal payableInterest) {
		this.payableInterest = payableInterest;
	}

	public BigDecimal getPayablePrincipal() {
		return payablePrincipal;
	}

	public void setPayablePrincipal(BigDecimal payablePrincipal) {
		this.payablePrincipal = payablePrincipal;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}


}