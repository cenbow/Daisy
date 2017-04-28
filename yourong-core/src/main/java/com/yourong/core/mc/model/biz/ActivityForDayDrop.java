package com.yourong.core.mc.model.biz;

import java.math.BigDecimal;
import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.core.tc.model.biz.TransactionForFirstInvestAct;

/**
 * 
 * @author chaisen
 *
 */
public class ActivityForDayDrop extends AbstractBaseObject {
	
	private Long templateId;
	
	private int totalDays;
	
	private BigDecimal singleInvestAmount;
	
	private BigDecimal totalInvestAmount;
	
	private int totalRed;
	
	private List<Long> templateIds;
	
	
	private List<Integer> couponAmount;

	/**
	 * 红包奖励金额
	 */
	private Integer templateIdName;
	
	

	public List<Integer> getCouponAmount() {
		return couponAmount;
	}

	public void setCouponAmount(List<Integer> couponAmount) {
		this.couponAmount = couponAmount;
	}

	public List<Long> getTemplateIds() {
		return templateIds;
	}

	public void setTemplateIds(List<Long> templateIds) {
		this.templateIds = templateIds;
	}

	public int getTotalRed() {
		return totalRed;
	}

	public void setTotalRed(int totalRed) {
		this.totalRed = totalRed;
	}

	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	public int getTotalDays() {
		return totalDays;
	}

	public void setTotalDays(int totalDays) {
		this.totalDays = totalDays;
	}

	public BigDecimal getSingleInvestAmount() {
		return singleInvestAmount;
	}

	public void setSingleInvestAmount(BigDecimal singleInvestAmount) {
		this.singleInvestAmount = singleInvestAmount;
	}

	public BigDecimal getTotalInvestAmount() {
		return totalInvestAmount;
	}

	public void setTotalInvestAmount(BigDecimal totalInvestAmount) {
		this.totalInvestAmount = totalInvestAmount;
	}

	public Integer getTemplateIdName() {
		return templateIdName;
	}

	public void setTemplateIdName(Integer templateIdName) {
		this.templateIdName = templateIdName;
	}
}
