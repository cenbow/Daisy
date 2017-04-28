package com.yourong.core.mc.model.biz;

import java.util.Date;
import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * 
 * @desc TODO
 * @author chaisen
 * 2016年10月12日上午11:15:42
 */
public class ActivityForFourChange extends AbstractBaseObject {

	private Date startTime;
	private List<Long> templateId;
	private Integer activityStatus;
	private Date endTime;
	private String receiveStartTime;
	private String receiveEndTime;
	private Integer coupon10;
	private Integer coupon30;
	private Integer coupon100;
	private Integer coupon200;
	private List<Integer> totalCoupon;
	private List<Integer> couponAmount;
	private List<String> couponAmountKey;
	private boolean isreceived=false;
	private Date weakStartTime;
	private Integer days;
	private Integer couponRemind;
	


	public Integer getCouponRemind() {
		return couponRemind;
	}

	public void setCouponRemind(Integer couponRemind) {
		this.couponRemind = couponRemind;
	}

	public Integer getDays() {
		return days;
	}

	public void setDays(Integer days) {
		this.days = days;
	}

	public Date getWeakStartTime() {
		return weakStartTime;
	}

	public void setWeakStartTime(Date weakStartTime) {
		this.weakStartTime = weakStartTime;
	}

	public boolean isIsreceived() {
		return isreceived;
	}

	public void setIsreceived(boolean isreceived) {
		this.isreceived = isreceived;
	}

	public List<Integer> getCouponAmount() {
		return couponAmount;
	}

	public void setCouponAmount(List<Integer> couponAmount) {
		this.couponAmount = couponAmount;
	}

	public List<String> getCouponAmountKey() {
		return couponAmountKey;
	}

	public void setCouponAmountKey(List<String> couponAmountKey) {
		this.couponAmountKey = couponAmountKey;
	}

	public List<Integer> getTotalCoupon() {
		return totalCoupon;
	}

	public void setTotalCoupon(List<Integer> totalCoupon) {
		this.totalCoupon = totalCoupon;
	}

	public Integer getCoupon10() {
		return coupon10;
	}

	public void setCoupon10(Integer coupon10) {
		this.coupon10 = coupon10;
	}

	public Integer getCoupon30() {
		return coupon30;
	}

	public void setCoupon30(Integer coupon30) {
		this.coupon30 = coupon30;
	}

	public Integer getCoupon100() {
		return coupon100;
	}

	public void setCoupon100(Integer coupon100) {
		this.coupon100 = coupon100;
	}

	public Integer getCoupon200() {
		return coupon200;
	}

	public void setCoupon200(Integer coupon200) {
		this.coupon200 = coupon200;
	}


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

	public Integer getActivityStatus() {
		return activityStatus;
	}

	public void setActivityStatus(Integer activityStatus) {
		this.activityStatus = activityStatus;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public List<Long> getTemplateId() {
		return templateId;
	}

	public void setTemplateId(List<Long> templateId) {
		this.templateId = templateId;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
}
