package com.yourong.core.mc.model.biz;

import java.math.BigDecimal;
import java.util.List;
import java.util.Date;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * 
 * @desc 十月活动活动model
 * 
 */
public class ActivityForOct extends AbstractBaseObject {

	private BigDecimal investAmount;
	private Integer projectDays;
	private Date startTime;
	private Long templateId;
	

	public Integer getProjectDays() {
		return projectDays;
	}

	public void setProjectDays(Integer projectDays) {
		this.projectDays = projectDays;
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

	
	
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
}
