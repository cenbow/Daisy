package com.yourong.core.tc.model.biz;

import com.yourong.common.domain.AbstractBaseObject;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 借款人还本付息
 * Created by XR on 2016/9/10.
 */
public class BorrowRepayInterestBiz extends AbstractBaseObject {
    /**
     * 借款人ID
     */
    private Long borrowerId;
    /**
     * 借款人姓名
     */
    private String borrowerName;
    /**
     * 借款人手机
     */
    private String borrowerMobile;
    /**
     * 还款日期
     */
    private Date endDate;
    /**
     * 项目id集合
     */
    private String projectIds;
    /**
     * 待还项目数
     */
    private Integer waitRepayNum=0;
    /**
     * 已还项目数
     */
    private Integer endRepayNum=0;
    /**
     * 需还利息
     */
    private BigDecimal payableInterest;
    /**
     * 需还本金
     */
    private BigDecimal payablePrincipal;
    /**
     * 平台贴息
     */
    private BigDecimal extraInterest;

    private BigDecimal payabletotal;
    
    private BigDecimal payExtraProjectInterest;
    
    /**
     * 借款人渠道
     */
    private String borrowerPlatformKey;

    public Long getBorrowerId() {
        return borrowerId;
    }

    public void setBorrowerId(Long borrowerId) {
        this.borrowerId = borrowerId;
    }

    public String getBorrowerName() {
        return borrowerName;
    }

    public void setBorrowerName(String borrowerName) {
        this.borrowerName = borrowerName;
    }

    public String getBorrowerMobile() {
        return borrowerMobile;
    }

    public void setBorrowerMobile(String borrowerMobile) {
        this.borrowerMobile = borrowerMobile;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getProjectIds() {
        return projectIds;
    }

    public void setProjectIds(String projectIds) {
        this.projectIds = projectIds;
    }

    public Integer getWaitRepayNum() {
        return waitRepayNum;
    }

    public void setWaitRepayNum(Integer waitRepayNum) {
        this.waitRepayNum = waitRepayNum;
    }

    public Integer getEndRepayNum() {
        return endRepayNum;
    }

    public void setEndRepayNum(Integer endRepayNum) {
        this.endRepayNum = endRepayNum;
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

    public BigDecimal getPayabletotal() {
        return this.payableInterest.add(this.payablePrincipal);
    }

    public void setPayabletotal(BigDecimal payabletotal) {
        this.payabletotal = payabletotal;
    }
    
    public BigDecimal getPayExtraProjectInterest() {
		return payExtraProjectInterest;
	}

	public void setPayExtraProjectInterest(BigDecimal payExtraProjectInterest) {
		this.payExtraProjectInterest = payExtraProjectInterest;
	}

	public BigDecimal getRealPayableInterest(){
    	if(payableInterest==null){
    		return BigDecimal.ZERO;
    	}
    	if(extraInterest==null){
    		extraInterest=BigDecimal.ZERO;
    	}
    	if(payExtraProjectInterest==null){
    		payExtraProjectInterest=BigDecimal.ZERO;
    	}
    	return payableInterest.subtract(extraInterest).subtract(payExtraProjectInterest);
    }
	
	public BigDecimal getRealPayableExtraInterest(){
    	if(extraInterest==null){
    		extraInterest= BigDecimal.ZERO;
    	}
    	if(payExtraProjectInterest==null){
    		payExtraProjectInterest= BigDecimal.ZERO;
    	}
    	return extraInterest.add(payExtraProjectInterest);
    }

	public String getBorrowerPlatformKey() {
		return borrowerPlatformKey;
	}

	public void setBorrowerPlatformKey(String borrowerPlatformKey) {
		this.borrowerPlatformKey = borrowerPlatformKey;
	}
	
}
