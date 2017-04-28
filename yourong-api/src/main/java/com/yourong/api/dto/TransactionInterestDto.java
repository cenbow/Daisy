package com.yourong.api.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.util.DateUtils;

public class TransactionInterestDto {
	
	 /** 交易id**/
    private Long transactionId;

	/**应付利息**/
    private BigDecimal payableInterest;

    /**应付本金**/
    private BigDecimal payablePrincipal;
    
    /**计息结束时间**/
    private Date endDate;
    
    /**状态：0：待支付 1：已全部支付 2:部分支付 3:未支付**/
    private Integer status;

    /**还款类型（0-正常 1-提前还款 2-逾期还款 3-垫资还款 ）**/
    private Integer payType;
    
    /**支付日期**/
    private Date payTime;
    
    /**还款日期**/
    private Date topayDate;
    
    /**滞纳金**/
    private BigDecimal overdueFine = BigDecimal.ZERO;
    
    /**实付本金**/
    private BigDecimal realPayPrincipal = BigDecimal.ZERO;

    /**实付利息**/
    private BigDecimal realPayInterest = BigDecimal.ZERO;
    
    /**逾期 天数**/
    private Integer overDays;
    
	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
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

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}
	
	/**
	 * 结束时间格式化
	 * @return
	 */
	public String getEndDateStr() {
		return DateUtils.formatDatetoString(endDate, DateUtils.DATE_FMT_3);
	}
	
	/**
	 * 支付时间格式化
	 * @return
	 */
	public String getPayTimeStr() {
		if(payTime!=null){
			return DateUtils.formatDatetoString(payTime, DateUtils.DATE_FMT_3);
		}
		return "——";
	}

	public BigDecimal getOverdueFine() {
		return overdueFine;
	}

	public void setOverdueFine(BigDecimal overdueFine) {
		this.overdueFine = overdueFine;
	}

	public BigDecimal getRealPayPrincipal() {
		return realPayPrincipal;
	}

	public void setRealPayPrincipal(BigDecimal realPayPrincipal) {
		this.realPayPrincipal = realPayPrincipal;
	}

	public BigDecimal getRealPayInterest() {
		return realPayInterest;
	}

	public void setRealPayInterest(BigDecimal realPayInterest) {
		this.realPayInterest = realPayInterest;
	}

	public Integer getOverDays() {
		return overDays;
	}

	public void setOverDays(Integer overDays) {
		this.overDays = overDays;
	}

	public Date getTopayDate() {
		return topayDate;
	}

	public void setTopayDate(Date topayDate) {
		this.topayDate = topayDate;
	}
    
}
