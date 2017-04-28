package com.yourong.core.fin.model;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.domain.AbstractBaseObject;

public class OverdueLog extends AbstractBaseObject{
	
	 private static final long serialVersionUID = -6788851483909460435L;
    /****/
    private Long id;
    
    /**逾期结算id**/
    private Long overdueRepayId;
    
    /**项目ID**/
    private Long projectId;

    /**本息ID**/
    private Long interestId;
    
    /**垫资记录id**/
    private Long underwriteId;
    
    /**垫资人id**/
    private Long underwriteMemberId;
    
    /**逾期本金**/
    private BigDecimal overduePrincipal;
    
    /**逾期利息**/
    private BigDecimal overdueInterest;
    
    /**滞纳金**/
    private BigDecimal overdueFine;
    
    /**逾期开始时间**/
    private Date startDate;
    	
    /**逾期结束时间**/
    private Date endDate;
    
    /**还款状态[1:未还款，2 已还款]**/
    private Integer status;


    /**备注**/
    private String remarks;

    /****/
    private Date updateTime;

    /****/
    private Date createTime;

    /**-1:删除，1：正常**/
    private Integer delFlag;
    
    private Integer type;
    
    /**所属期数**/
    private String periods;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Long getInterestId() {
		return interestId;
	}

	public void setInterestId(Long interestId) {
		this.interestId = interestId;
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

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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

	public Long getUnderwriteId() {
		return underwriteId;
	}

	public void setUnderwriteId(Long underwriteId) {
		this.underwriteId = underwriteId;
	}

	public Long getUnderwriteMemberId() {
		return underwriteMemberId;
	}

	public void setUnderwriteMemberId(Long underwriteMemberId) {
		this.underwriteMemberId = underwriteMemberId;
	}

	public BigDecimal getOverduePrincipal() {
		return overduePrincipal;
	}

	public void setOverduePrincipal(BigDecimal overduePrincipal) {
		this.overduePrincipal = overduePrincipal;
	}

	public BigDecimal getOverdueInterest() {
		return overdueInterest;
	}

	public BigDecimal getOverdueFine() {
		return overdueFine;
	}

	public void setOverdueFine(BigDecimal overdueFine) {
		this.overdueFine = overdueFine;
	}

	public void setOverdueInterest(BigDecimal overdueInterest) {
		this.overdueInterest = overdueInterest;
	}

	public String getPeriods() {
		return periods;
	}

	public void setPeriods(String periods) {
		this.periods = periods;
	}

	public Long getOverdueRepayId() {
		return overdueRepayId;
	}

	public void setOverdueRepayId(Long overdueRepayId) {
		this.overdueRepayId = overdueRepayId;
	}

   
}