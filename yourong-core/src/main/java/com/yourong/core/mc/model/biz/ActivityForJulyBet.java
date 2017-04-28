package com.yourong.core.mc.model.biz;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * 
 * @desc 押注记录
 * @author chaisen
 * 2016年6月30日上午10:16:12
 */
public class ActivityForJulyBet extends AbstractBaseObject {
		
	private Date betTime;
	private Integer popularityValue;
	private Integer rewardPopularityValue;
	
	private Integer coupon30;
	private Integer coupon50;
	private Integer coupon100;
	private Integer coupon200;
	
	private Long templateId;
	
	
	
	public Long getTemplateId() {
		return templateId;
	}
	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}
	public Date getBetTime() {
		return betTime;
	}
	public void setBetTime(Date betTime) {
		this.betTime = betTime;
	}
	public Integer getPopularityValue() {
		return popularityValue;
	}
	public void setPopularityValue(Integer popularityValue) {
		this.popularityValue = popularityValue;
	}
	public Integer getRewardPopularityValue() {
		return rewardPopularityValue;
	}
	public void setRewardPopularityValue(Integer rewardPopularityValue) {
		this.rewardPopularityValue = rewardPopularityValue;
	}
	public Integer getCoupon50() {
		return coupon50;
	}
	public void setCoupon50(Integer coupon50) {
		this.coupon50 = coupon50;
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
	public Integer getCoupon30() {
		return coupon30;
	}
	public void setCoupon30(Integer coupon30) {
		this.coupon30 = coupon30;
	}
	
	
	
	
}
