package com.yourong.core.mc.model.biz;

import java.math.BigDecimal;
import java.util.List;
import java.util.Date;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * 
 * @desc 百万现金券活动model
 * @author wangyanji 2016年1月5日下午4:46:53
 */
public class ActivityForMillionCoupon extends AbstractBaseObject {

	private BigDecimal minInvestAmount;
	private BigDecimal maxInvestAmount;
	private String rewardsGroup;
	private List<ActivityLotteryResultBiz> investList;
	private Integer fund;
	private Integer fund1;
	private Integer fund2;
	private Integer activityStatus;
	private String startDate;
	private Date startTime;
	private BigDecimal investAmount;
	private Long templateId88;
	private Long templateId188;
	private BigDecimal totalAmount2000;
	private BigDecimal totalAmount4000;
	private String receiveStartTime;
	private String receiveEndTime;
	
	
	
	
	
	


	public String getReceiveStartTime() {
		return receiveStartTime;
	}

	public void setReceiveStartTime(String receiveStartTime) {
		this.receiveStartTime = receiveStartTime;
	}

	public String getReceiveEndTime() {
		return receiveEndTime;
	}

	public void setReceiveEndTime(String receiveEndTime) {
		this.receiveEndTime = receiveEndTime;
	}

	public Long getTemplateId88() {
		return templateId88;
	}

	public void setTemplateId88(Long templateId88) {
		this.templateId88 = templateId88;
	}

	public Long getTemplateId188() {
		return templateId188;
	}

	public void setTemplateId188(Long templateId188) {
		this.templateId188 = templateId188;
	}

	public BigDecimal getTotalAmount2000() {
		return totalAmount2000;
	}

	public void setTotalAmount2000(BigDecimal totalAmount2000) {
		this.totalAmount2000 = totalAmount2000;
	}

	public BigDecimal getTotalAmount4000() {
		return totalAmount4000;
	}

	public void setTotalAmount4000(BigDecimal totalAmount4000) {
		this.totalAmount4000 = totalAmount4000;
	}

	public BigDecimal getInvestAmount() {
		return investAmount;
	}

	public void setInvestAmount(BigDecimal investAmount) {
		this.investAmount = investAmount;
	}

	public BigDecimal getMinInvestAmount() {
		return minInvestAmount;
	}

	public void setMinInvestAmount(BigDecimal minInvestAmount) {
		this.minInvestAmount = minInvestAmount;
	}

	public BigDecimal getMaxInvestAmount() {
		return maxInvestAmount;
	}

	public void setMaxInvestAmount(BigDecimal maxInvestAmount) {
		this.maxInvestAmount = maxInvestAmount;
	}

	public String getRewardsGroup() {
		return rewardsGroup;
	}

	public void setRewardsGroup(String rewardsGroup) {
		this.rewardsGroup = rewardsGroup;
	}

	public List<ActivityLotteryResultBiz> getInvestList() {
		return investList;
	}

	public void setInvestList(List<ActivityLotteryResultBiz> investList) {
		this.investList = investList;
	}

	public Integer getActivityStatus() {
		return activityStatus;
	}

	public void setActivityStatus(Integer activityStatus) {
		this.activityStatus = activityStatus;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public Integer getFund1() {
		return fund1;
	}

	public void setFund1(Integer fund1) {
		this.fund1 = fund1;
	}

	public Integer getFund2() {
		return fund2;
	}

	public void setFund2(Integer fund2) {
		this.fund2 = fund2;
	}

	public Integer getFund() {
		return fund;
	}

	public void setFund(Integer fund) {
		this.fund = fund;
	}
	
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
}
