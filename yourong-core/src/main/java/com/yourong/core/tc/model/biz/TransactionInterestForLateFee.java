package com.yourong.core.tc.model.biz;

import java.math.BigDecimal;

import com.yourong.common.domain.AbstractBaseObject;

public class TransactionInterestForLateFee extends AbstractBaseObject {

	/** */
	private static final long serialVersionUID = 1L;
	private Long id;

	/** 项目id **/
	private Long projectId;

	/** 交易id **/
	private Long transactionId;

	 /**应付利息**/
    private BigDecimal payableInterest;

    /**应付本金**/
    private BigDecimal payablePrincipal;
    
    /**使用收益券产生的利息**/
    private BigDecimal extraInterest;

	/** 总利息收益 **/
	private BigDecimal totalInterest;

	/** 已收取利息 **/
	private BigDecimal receivedInterest;

	/** 总本金 **/
	private BigDecimal totalPrincipal;

	/** 已收取本金 **/
	private BigDecimal receivedPrincipal;

	/** 收益券增加的年化收益 **/
	private BigDecimal totalExtraInterest;

	/*** 已收取的收益券收益 **/
	private BigDecimal receivedExtraInterest;
	
	/**滞纳金**/
	private BigDecimal overdueFine;

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public BigDecimal getTotalInterest() {
		return totalInterest;
	}

	public void setTotalInterest(BigDecimal totalInterest) {
		this.totalInterest = totalInterest;
	}

	public BigDecimal getReceivedInterest() {
		return receivedInterest;
	}

	public void setReceivedInterest(BigDecimal receivedInterest) {
		this.receivedInterest = receivedInterest;
	}

	public BigDecimal getTotalPrincipal() {
		return totalPrincipal;
	}

	public void setTotalPrincipal(BigDecimal totalPrincipal) {
		this.totalPrincipal = totalPrincipal;
	}

	public BigDecimal getReceivedPrincipal() {
		return receivedPrincipal;
	}

	public void setReceivedPrincipal(BigDecimal receivedPrincipal) {
		this.receivedPrincipal = receivedPrincipal;
	}

	public BigDecimal getTotalExtraInterest() {
		return totalExtraInterest;
	}

	public void setTotalExtraInterest(BigDecimal totalExtraInterest) {
		this.totalExtraInterest = totalExtraInterest;
	}

	public BigDecimal getReceivedExtraInterest() {
		return receivedExtraInterest;
	}

	public void setReceivedExtraInterest(BigDecimal receivedExtraInterest) {
		this.receivedExtraInterest = receivedExtraInterest;
	}

	public BigDecimal getOverdueFine() {
		return overdueFine;
	}

	public void setOverdueFine(BigDecimal overdueFine) {
		this.overdueFine = overdueFine;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getPayableInterest() {
		return payableInterest;
	}

	public BigDecimal getPayablePrincipal() {
		return payablePrincipal;
	}

	public BigDecimal getExtraInterest() {
		return extraInterest;
	}

	public void setPayableInterest(BigDecimal payableInterest) {
		this.payableInterest = payableInterest;
	}

	public void setPayablePrincipal(BigDecimal payablePrincipal) {
		this.payablePrincipal = payablePrincipal;
	}

	public void setExtraInterest(BigDecimal extraInterest) {
		this.extraInterest = extraInterest;
	}

}
