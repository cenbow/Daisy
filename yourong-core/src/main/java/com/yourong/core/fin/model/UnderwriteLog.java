package com.yourong.core.fin.model;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.domain.AbstractBaseObject;

public class UnderwriteLog extends AbstractBaseObject{
	 private static final long serialVersionUID = -6788851483909460435L;
    /****/
    private Long id;

    /**项目本息ID**/
    private Long projectInterestId;

    /**垫付会员ID**/
    private Long underwriteMemberId;

    /**垫付利息**/
    private BigDecimal payableInterest;

    /**垫付本金**/
    private BigDecimal payablePrincipal;

    /**垫付开始时间**/
    private Date underwriteDate;
    
    /**垫付结束时间**/
    private Date underwriteEndDate;

    /**垫付状态[1:未垫付，2：垫付中，3：已垫付，4:已结束]**/
    private Integer underwriteStatus;

    /**备注**/
    private String remarks;

    /****/
    private Date updateTime;

    /****/
    private Date createTime;

    /**-1:删除，1：正常**/
    private Integer delFlag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectInterestId() {
        return projectInterestId;
    }

    public void setProjectInterestId(Long projectInterestId) {
        this.projectInterestId = projectInterestId;
    }

    public Long getUnderwriteMemberId() {
        return underwriteMemberId;
    }

    public void setUnderwriteMemberId(Long underwriteMemberId) {
        this.underwriteMemberId = underwriteMemberId;
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


    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks == null ? null : remarks.trim();
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

	public Date getUnderwriteDate() {
		return underwriteDate;
	}

	public void setUnderwriteDate(Date underwriteDate) {
		this.underwriteDate = underwriteDate;
	}

	public Date getUnderwriteEndDate() {
		return underwriteEndDate;
	}

	public void setUnderwriteEndDate(Date underwriteEndDate) {
		this.underwriteEndDate = underwriteEndDate;
	}

	public Integer getUnderwriteStatus() {
		return underwriteStatus;
	}

	public void setUnderwriteStatus(Integer underwriteStatus) {
		this.underwriteStatus = underwriteStatus;
	}
    
}