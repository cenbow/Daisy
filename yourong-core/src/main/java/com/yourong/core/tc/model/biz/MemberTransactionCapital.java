package com.yourong.core.tc.model.biz;

import java.io.Serializable;
import java.math.BigDecimal;

public class MemberTransactionCapital implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 4941487938805576752L;


    /**用户ID**/
    private Long memberId;

    /**已完结投资笔数**/
    private int finishedInvestNum = 0;

    /**存续期投资笔数**/
    private int subsistingInvestNum = 0;

    /**已完结投资总额**/
    private BigDecimal finishedInvestTotal = BigDecimal.ZERO;

    /**存续期投资总额**/
    private BigDecimal subsistingInvestTotal = BigDecimal.ZERO;

    /**待收利息**/
    private BigDecimal receivableInterest = BigDecimal.ZERO;

    /**已收利息**/
    private BigDecimal receivedInterest = BigDecimal.ZERO;
    
    /**代收本金**/
    private BigDecimal receivablePrincipal = BigDecimal.ZERO;
    
    /**已收本金**/
    private BigDecimal receivedPrincipal = BigDecimal.ZERO;
    
    /**滞纳金**/
    private BigDecimal overdueFine =BigDecimal.ZERO;
    
    /**待收利息笔数**/
    private int receivableInterestNum = 0;

    /**已收利息笔数**/
    private int receivedInterestNum = 0;
    
    /**累计投资笔数**/
    private int totalInvestNum = 0;

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public int getFinishedInvestNum() {
		return finishedInvestNum;
	}

	public void setFinishedInvestNum(int finishedInvestNum) {
		this.finishedInvestNum = finishedInvestNum;
	}

	public int getSubsistingInvestNum() {
		return subsistingInvestNum;
	}

	public void setSubsistingInvestNum(int subsistingInvestNum) {
		this.subsistingInvestNum = subsistingInvestNum;
	}

	public BigDecimal getFinishedInvestTotal() {
		return finishedInvestTotal;
	}

	public void setFinishedInvestTotal(BigDecimal finishedInvestTotal) {
		this.finishedInvestTotal = finishedInvestTotal;
	}

	public BigDecimal getSubsistingInvestTotal() {
		return subsistingInvestTotal;
	}

	public void setSubsistingInvestTotal(BigDecimal subsistingInvestTotal) {
		this.subsistingInvestTotal = subsistingInvestTotal;
	}

	public BigDecimal getReceivableInterest() {
		return receivableInterest;
	}

	public void setReceivableInterest(BigDecimal receivableInterest) {
		this.receivableInterest = receivableInterest;
	}

	public BigDecimal getReceivedInterest() {
		return receivedInterest;
	}

	public void setReceivedInterest(BigDecimal receivedInterest) {
		this.receivedInterest = receivedInterest;
	}

	public BigDecimal getReceivablePrincipal() {
		return receivablePrincipal;
	}

	public void setReceivablePrincipal(BigDecimal receivablePrincipal) {
		this.receivablePrincipal = receivablePrincipal;
	}

	public BigDecimal getReceivedPrincipal() {
		return receivedPrincipal;
	}

	public void setReceivedPrincipal(BigDecimal receivedPrincipal) {
		this.receivedPrincipal = receivedPrincipal;
	}

	public int getReceivableInterestNum() {
		return receivableInterestNum;
	}

	public void setReceivableInterestNum(int receivableInterestNum) {
		this.receivableInterestNum = receivableInterestNum;
	}

	public int getReceivedInterestNum() {
		return receivedInterestNum;
	}

	public void setReceivedInterestNum(int receivedInterestNum) {
		this.receivedInterestNum = receivedInterestNum;
	}

	public int getTotalInvestNum() {
		return totalInvestNum;
	}

	public void setTotalInvestNum(int totalInvestNum) {
		this.totalInvestNum = totalInvestNum;
	}

	public BigDecimal getOverdueFine() {
		return overdueFine;
	}

	public void setOverdueFine(BigDecimal overdueFine) {
		this.overdueFine = overdueFine;
	}

}