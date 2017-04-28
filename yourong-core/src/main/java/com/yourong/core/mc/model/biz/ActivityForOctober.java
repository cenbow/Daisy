package com.yourong.core.mc.model.biz;

import java.math.BigDecimal;
import java.util.List;
import java.util.Date;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.core.tc.model.biz.TransactionForFirstInvestAct;

/**
 * 
 * @desc 国庆活动活动model
 * 
 */
public class ActivityForOctober extends AbstractBaseObject {

	private BigDecimal investAmount;
	private List<Integer> projectDay;
	private Date startTime;
	private Long templateId;
	private Integer totalDays;
	private BigDecimal directInvestAmount;
	private List<Long> templateIds;
	private Integer projectDays;
	private Date endTime;
	private Integer activityStatus;
	private List<Integer> days;
	private List<BigDecimal> popularityValue;
	
	private BigDecimal myInvestAmount;
	
	private BigDecimal fourInvestAmount;
	
	
	
	
	public BigDecimal getFourInvestAmount() {
		return fourInvestAmount;
	}

	public void setFourInvestAmount(BigDecimal fourInvestAmount) {
		this.fourInvestAmount = fourInvestAmount;
	}

	public BigDecimal getMyInvestAmount() {
		return myInvestAmount;
	}

	public void setMyInvestAmount(BigDecimal myInvestAmount) {
		this.myInvestAmount = myInvestAmount;
	}

	public List<BigDecimal> getPopularityValue() {
		return popularityValue;
	}

	public void setPopularityValue(List<BigDecimal> popularityValue) {
		this.popularityValue = popularityValue;
	}

	public List<Long> getTemplateIds() {
		return templateIds;
	}

	public void setTemplateIds(List<Long> templateIds) {
		this.templateIds = templateIds;
	}

	public List<Integer> getDays() {
		return days;
	}


	public void setDays(List<Integer> days) {
		this.days = days;
	}


	public Date getEndTime() {
		return endTime;
	}


	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}


	public Integer getActivityStatus() {
		return activityStatus;
	}


	public void setActivityStatus(Integer activityStatus) {
		this.activityStatus = activityStatus;
	}

	private List<TransactionForFirstInvestAct> firstTenInvestAmount;
	

	
	public List<Integer> getProjectDay() {
		return projectDay;
	}


	public void setProjectDay(List<Integer> projectDay) {
		this.projectDay = projectDay;
	}


	public Integer getTotalDays() {
		return totalDays;
	}


	public void setTotalDays(Integer totalDays) {
		this.totalDays = totalDays;
	}


	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	public BigDecimal getInvestAmount() {
		return investAmount;
	}

	public void setInvestAmount(BigDecimal investAmount) {
		this.investAmount = investAmount;
	}

	
	
	public Integer getProjectDays() {
		return projectDays;
	}


	public void setProjectDays(Integer projectDays) {
		this.projectDays = projectDays;
	}


	public List<TransactionForFirstInvestAct> getFirstTenInvestAmount() {
		return firstTenInvestAmount;
	}


	public void setFirstTenInvestAmount(List<TransactionForFirstInvestAct> firstTenInvestAmount) {
		this.firstTenInvestAmount = firstTenInvestAmount;
	}


	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}


	public BigDecimal getDirectInvestAmount() {
		return directInvestAmount;
	}


	public void setDirectInvestAmount(BigDecimal directInvestAmount) {
		this.directInvestAmount = directInvestAmount;
	}
	
	
}
