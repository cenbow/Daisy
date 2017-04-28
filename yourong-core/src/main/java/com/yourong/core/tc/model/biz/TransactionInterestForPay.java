package com.yourong.core.tc.model.biz;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * 用户支付本息的DO
 * @author Leon Ray
 * 2014年9月26日-下午5:53:14
 */
public class TransactionInterestForPay  extends AbstractBaseObject{
    /**
	 * 
	 */
	private static final long serialVersionUID = 4941487938805576752L;

	/**项目id**/
    private Long projectId;

    /** 支付用户id**/
    private Long payerId;
    
    /**交易本息id**/
    private Long transactionInterestId;

    private Date startDate;
    private Date endDate;
    private Integer status;
    private BigDecimal payableInterest;//实付利息
    private BigDecimal payablePrincipal;//实付本金
    private BigDecimal extraInterest;//活动、收益券利息
    /**滞纳金**/
    private BigDecimal overdueFine;
    /**项目加息**/
    private BigDecimal extraProjectInterest;
    
	/**
	 * 是否垫付
	 */
	private  Boolean isAdvances = false ;



	/**
     * 原始债权人需要支付的本息
     */
    private BigDecimal totalPayInterestAndPrincipalForLender;
    /**
     * 平台需要支付的本息
     */
    private BigDecimal totalPayInterestAndPrincipalForPlatform;
    
    /**插入代收的本息id**/
    private String transactionInterestIds;
    
    /**交易本息id*/
    private Long interestId;

	/**
	 * 对外类型
	 */
	private String openPlatformKey;

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Long getPayerId() {
		return payerId;
	}

	public void setPayerId(Long payerId) {
		this.payerId = payerId;
	}

	
	public Long getTransactionInterestId() {
		return transactionInterestId;
	}

	public void setTransactionInterestId(Long transactionInterestId) {
		this.transactionInterestId = transactionInterestId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
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

	public BigDecimal getExtraInterest() {
		return extraInterest;
	}

	public void setExtraInterest(BigDecimal extraInterest) {
		this.extraInterest = extraInterest;
	}

	public BigDecimal getTotalPayInterestAndPrincipalForLender() {
		return totalPayInterestAndPrincipalForLender;
	}

	public void setTotalPayInterestAndPrincipalForLender(
			BigDecimal totalPayInterestAndPrincipalForLender) {
		this.totalPayInterestAndPrincipalForLender = totalPayInterestAndPrincipalForLender;
	}

	public BigDecimal getTotalPayInterestAndPrincipalForPlatform() {
		return totalPayInterestAndPrincipalForPlatform;
	}

	public void setTotalPayInterestAndPrincipalForPlatform(
			BigDecimal totalPayInterestAndPrincipalForPlatform) {
		this.totalPayInterestAndPrincipalForPlatform = totalPayInterestAndPrincipalForPlatform;
	}

	public String getTransactionInterestIds() {
		return transactionInterestIds;
	}

	public void setTransactionInterestIds(String transactionInterestIds) {
		this.transactionInterestIds = transactionInterestIds;
	}

	public Boolean getIsAdvances() {
		return isAdvances;
	}

	public void setIsAdvances(Boolean isAdvances) {
		this.isAdvances = isAdvances;
	}

	public BigDecimal getOverdueFine() {
		return overdueFine;
	}

	public void setOverdueFine(BigDecimal overdueFine) {
		this.overdueFine = overdueFine;
	}

	public Long getInterestId() {
		return interestId;
	}

	public void setInterestId(Long interestId) {
		this.interestId = interestId;
	}

	public String getOpenPlatformKey() {
		return openPlatformKey;
	}

	public void setOpenPlatformKey(String openPlatformKey) {
		this.openPlatformKey = openPlatformKey;
	}

	public BigDecimal getExtraProjectInterest() {
		return extraProjectInterest;
	}

	public void setExtraProjectInterest(BigDecimal extraProjectInterest) {
		this.extraProjectInterest = extraProjectInterest;
	}
	
}