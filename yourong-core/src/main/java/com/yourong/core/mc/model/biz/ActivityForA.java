package com.yourong.core.mc.model.biz;

import java.util.Date;
import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * 
 * @desc 融光焕发model	
 * @author 
 */
public class ActivityForA extends AbstractBaseObject {

	private Date startTime;
	private Date endTime;
	private Integer activityStatus;
	private List<Integer> totalAmount;
	
	private List<Long> templateId;
	
	private Integer day;
	
	private List<Integer> couponAmount;
	
	private boolean isReceived=false;
	
	private boolean isLogined=false;

	public Date getStartTime() {
		return startTime;
	}

	
	public boolean isReceived() {
		return isReceived;
	}


	public void setReceived(boolean isReceived) {
		this.isReceived = isReceived;
	}


	public boolean isLogined() {
		return isLogined;
	}


	public void setLogined(boolean isLogined) {
		this.isLogined = isLogined;
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

	public Integer getActivityStatus() {
		return activityStatus;
	}

	public void setActivityStatus(Integer activityStatus) {
		this.activityStatus = activityStatus;
	}

	public List<Integer> getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(List<Integer> totalAmount) {
		this.totalAmount = totalAmount;
	}

	public List<Long> getTemplateId() {
		return templateId;
	}

	public void setTemplateId(List<Long> templateId) {
		this.templateId = templateId;
	}

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	public List<Integer> getCouponAmount() {
		return couponAmount;
	}

	public void setCouponAmount(List<Integer> couponAmount) {
		this.couponAmount = couponAmount;
	}
	
	
	

}
