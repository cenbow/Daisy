package com.yourong.core.ic.model;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.enums.StatusEnum;
import com.yourong.common.util.FormulaUtil;

public class LeaseBonus {
	/** 主键 **/
	private Long id;

	/** 项目id **/
	private Long projectId;

	/** 项目名称 **/
	private String projectName;

	/** 租赁状态(0-待租 1-已租) **/
	private Integer leaseStatus;

	/** 租赁总收益 **/
	private BigDecimal totalIncome;

	/** 分红期数 **/
	private Integer periods;

	/** 创建时间 **/
	private Date createTime;

	/** 更新时间 **/
	private Date updateTime;

	/** 删除标记 **/
	private Integer delFlag;

	/** 项目缩略图 **/
	private String thumbnail;

	/** 项目总额 **/
	private BigDecimal totalAmount;

	/** 项目状态 **/
	private Integer projectStatus;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public Integer getLeaseStatus(){
		return leaseStatus;
	}
	public String getLeaseStatusStr() {
		if(leaseStatus == StatusEnum.LEASE_BONUS_WAIT_LEASE.getStatus()){
			return StatusEnum.LEASE_BONUS_WAIT_LEASE.getDesc();
		}
		if(leaseStatus == StatusEnum.LEASE_BONUS_BEEN_LEASED.getStatus()){
			return StatusEnum.LEASE_BONUS_DID_BONUS.getDesc();
		}
		return null;
	}

	public void setLeaseStatus(Integer leaseStatus) {
		this.leaseStatus = leaseStatus;
	}

	public BigDecimal getTotalIncome() {
		return totalIncome;
	}

	public void setTotalIncome(BigDecimal totalIncome) {
		this.totalIncome = totalIncome;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getPrefixProjectName() {
		if (projectName.contains("期")) {
			return projectName.substring(0, projectName.indexOf("期") + 1);
		} else {
			return "";
		}
	}

	public String getSuffixProjectName() {
		if (projectName.contains("期")) {
			return projectName.substring(projectName.indexOf("期") + 1);
		} else {
			return projectName;
		}
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Integer getProjectStatus() {
		return projectStatus;
	}

	public void setProjectStatus(Integer projectStatus) {
		this.projectStatus = projectStatus;
	}

	public Integer getPeriods() {
		return periods;
	}

	public void setPeriods(Integer periods) {
		this.periods = periods;
	}

	public String getTotalIncomeStr(){
		if(totalIncome!=null){
			return FormulaUtil.formatCurrencyNoUnit(totalIncome);
		}
		return "";
	}
}