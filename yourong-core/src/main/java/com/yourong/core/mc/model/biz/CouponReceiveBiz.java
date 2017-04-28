package com.yourong.core.mc.model.biz;

import com.yourong.common.domain.AbstractBaseObject;

public class CouponReceiveBiz extends AbstractBaseObject {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 模板Id
	 */
	private Long couponTemplateId;
	/**
	 * 用户Id
	 */
	private Long memberId;
	/**
	 * 活动Id
	 */
	private Long activityId;
	/**
	 * 发送Id
	 */
	private Long senderId;
	/**
	 * 是否重复  1--可以重复   0--不能重复
	 */
	private int isRepeat;

	public Long getCouponTemplateId() {
		return couponTemplateId;
	}

	public void setCouponTemplateId(Long couponTemplateId) {
		this.couponTemplateId = couponTemplateId;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public Long getActivityId() {
		return activityId;
	}

	public void setActivityId(Long activityId) {
		this.activityId = activityId;
	}

	public Long getSenderId() {
		return senderId;
	}

	public void setSenderId(Long senderId) {
		this.senderId = senderId;
	}

	public int getIsRepeat() {
		return isRepeat;
	}

	public void setIsRepeat(int isRepeat) {
		this.isRepeat = isRepeat;
	}

}
