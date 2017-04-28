package com.yourong.core.ic.model.biz;

import java.math.BigDecimal;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.util.FormulaUtil;

public class LeaseBonusForFront extends AbstractBaseObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 主键 **/
	private Long id;

	/** 项目id **/
	private Long projectId;

	/** 项目名称 **/
	private String projectName;

	/** 租赁状态(0-待租 1-已租) **/
	private Integer leaseStatus;

	/** 分红状态 **/
	private Integer bonusStatus;

	/** 租赁总收益 **/
	private BigDecimal totalIncome;

	/** 分红期数 **/
	private Integer periods;

	/** 项目缩略图 **/
	private String thumbnail;

	/** 项目状态 **/
	private Integer projectStatus;
	
	/**已租期数**/
	private Integer leasePeriods;
	
	/**已分红期数**/
	private Integer bonusPeriods;
	

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

	public Integer getLeaseStatus() {
		return leaseStatus;
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

	public String getTotalIncomeStr() {
		if (totalIncome == null) {
			totalIncome = BigDecimal.ZERO;
		}
		return FormulaUtil.formatCurrencyNoUnit(totalIncome);
	}

	public Integer getBonusStatus() {
		return bonusStatus;
	}

	public void setBonusStatus(Integer bonusStatus) {
		this.bonusStatus = bonusStatus;
	}

	public Integer getLeasePeriods() {
		return leasePeriods;
	}

	public void setLeasePeriods(Integer leasePeriods) {
		this.leasePeriods = leasePeriods;
	}

	public Integer getBonusPeriods() {
		return bonusPeriods;
	}

	public void setBonusPeriods(Integer bonusPeriods) {
		this.bonusPeriods = bonusPeriods;
	}
	
}
