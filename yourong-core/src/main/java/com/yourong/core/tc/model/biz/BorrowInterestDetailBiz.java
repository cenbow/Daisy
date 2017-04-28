package com.yourong.core.tc.model.biz;

import com.yourong.common.domain.AbstractBaseObject;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by XR on 2016/9/11.
 */
public class BorrowInterestDetailBiz extends AbstractBaseObject {
    /**
     * 项目名称
     */
    private String projectName;
    /**
     * 本息id
     */
    private String interestId;
    /**
     * 期数
     */
    private String periods;
    /**
     * 还款日期
     */
    private Date endDate;
    /**
     * 需还本金
     */
    private BigDecimal payablePrincipal;
    /**
     * 需还利息
     */
    private BigDecimal payableInterest;
    /**
     * 平台贴息
     */
    private BigDecimal extraInterest;
    
    private BigDecimal payExtraProjectInterest;
    
    

    public BigDecimal getPayExtraProjectInterest() {
		return payExtraProjectInterest;
	}

	public void setPayExtraProjectInterest(BigDecimal payExtraProjectInterest) {
		this.payExtraProjectInterest = payExtraProjectInterest;
	}

	/**
     * 状态：0：待支付 1：已全部支付 2:部分支付 3:未支付
     */
    private Integer status;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getInterestId() {
        return interestId;
    }

    public void setInterestId(String interestId) {
        this.interestId = interestId;
    }

    public String getPeriods() {
        return periods;
    }

    public void setPeriods(String periods) {
        this.periods = periods;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getPayablePrincipal() {
        return payablePrincipal;
    }

    public void setPayablePrincipal(BigDecimal payablePrincipal) {
        this.payablePrincipal = payablePrincipal;
    }

    public BigDecimal getPayableInterest() {
        return payableInterest;
    }

    public void setPayableInterest(BigDecimal payableInterest) {
        this.payableInterest = payableInterest;
    }

    public BigDecimal getExtraInterest() {
        return extraInterest;
    }

    public void setExtraInterest(BigDecimal extraInterest) {
        this.extraInterest = extraInterest;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
}
