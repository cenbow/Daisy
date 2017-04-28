package com.yourong.core.mc.model.biz;

import java.util.Date;

/**
 * 红色星期五活动rulebiz
 * @author Leon Ray
 * 2015年6月8日-下午4:47:46
 */
public class RedFridayRuleBiz  {
	private Long couponTemplateId;
	private Date startTime;
	private Date endTime;
	public Long getCouponTemplateId() {
		return couponTemplateId;
	}
	public void setCouponTemplateId(Long couponTemplateId) {
		this.couponTemplateId = couponTemplateId;
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
	
	
}
