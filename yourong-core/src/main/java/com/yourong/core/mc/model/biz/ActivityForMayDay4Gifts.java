package com.yourong.core.mc.model.biz;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * 
 * @desc 五一四重礼活动model
 * @author wangyanji 2016年1月5日下午4:46:53
 */
public class ActivityForMayDay4Gifts extends AbstractBaseObject {

	private Integer activityStatus;
	private Date startTime;
	private Date endTime;
	private BigDecimal InvestAmount;
	private String receiveStartTime;
	private String receiveEndTime;
	private BigDecimal totalInvestLv1;
	private BigDecimal totalInvestLv2;
	private BigDecimal totalInvestLv3;
	private Long templateIdForDay;
	private Long templateIdForLv1;
	private Long templateIdForLv2;
	private Long templateIdForLv3;
	private boolean couponForDayFlag = false;
	private boolean couponForLv1 = false;
	private boolean couponForLv2 = false;
	private boolean couponForLv3 = false;
	private Date lastReceiveEndTime;

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

	public BigDecimal getTotalInvestLv1() {
		return totalInvestLv1;
	}

	public void setTotalInvestLv1(BigDecimal totalInvestLv1) {
		this.totalInvestLv1 = totalInvestLv1;
	}

	public BigDecimal getTotalInvestLv2() {
		return totalInvestLv2;
	}

	public void setTotalInvestLv2(BigDecimal totalInvestLv2) {
		this.totalInvestLv2 = totalInvestLv2;
	}

	public BigDecimal getTotalInvestLv3() {
		return totalInvestLv3;
	}

	public void setTotalInvestLv3(BigDecimal totalInvestLv3) {
		this.totalInvestLv3 = totalInvestLv3;
	}

	public Long getTemplateIdForDay() {
		return templateIdForDay;
	}

	public void setTemplateIdForDay(Long templateIdForDay) {
		this.templateIdForDay = templateIdForDay;
	}

	public Long getTemplateIdForLv1() {
		return templateIdForLv1;
	}

	public void setTemplateIdForLv1(Long templateIdForLv1) {
		this.templateIdForLv1 = templateIdForLv1;
	}

	public Long getTemplateIdForLv2() {
		return templateIdForLv2;
	}

	public void setTemplateIdForLv2(Long templateIdForLv2) {
		this.templateIdForLv2 = templateIdForLv2;
	}

	public Long getTemplateIdForLv3() {
		return templateIdForLv3;
	}

	public void setTemplateIdForLv3(Long templateIdForLv3) {
		this.templateIdForLv3 = templateIdForLv3;
	}

	public Integer getActivityStatus() {
		return activityStatus;
	}

	public void setActivityStatus(Integer activityStatus) {
		this.activityStatus = activityStatus;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public BigDecimal getInvestAmount() {
		return InvestAmount;
	}

	public void setInvestAmount(BigDecimal investAmount) {
		InvestAmount = investAmount;
	}

	public boolean isCouponForDayFlag() {
		return couponForDayFlag;
	}

	public void setCouponForDayFlag(boolean couponForDayFlag) {
		this.couponForDayFlag = couponForDayFlag;
	}

	public boolean isCouponForLv1() {
		return couponForLv1;
	}

	public void setCouponForLv1(boolean couponForLv1) {
		this.couponForLv1 = couponForLv1;
	}

	public boolean isCouponForLv2() {
		return couponForLv2;
	}

	public void setCouponForLv2(boolean couponForLv2) {
		this.couponForLv2 = couponForLv2;
	}

	public boolean isCouponForLv3() {
		return couponForLv3;
	}

	public void setCouponForLv3(boolean couponForLv3) {
		this.couponForLv3 = couponForLv3;
	}

	public Date getLastReceiveEndTime() {
		return lastReceiveEndTime;
	}

	public void setLastReceiveEndTime(Date lastReceiveEndTime) {
		this.lastReceiveEndTime = lastReceiveEndTime;
	}
	
}
