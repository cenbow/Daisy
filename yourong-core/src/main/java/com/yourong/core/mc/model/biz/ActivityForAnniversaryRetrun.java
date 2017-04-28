package com.yourong.core.mc.model.biz;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * 
 * @desc 周年庆
 * @author chaisen
 * 2016年10月20日上午11:17:24
 */
public class ActivityForAnniversaryRetrun extends AbstractBaseObject {
	private Integer position;
	private String rewardResult;
	private Integer coupon88Remind;
	
	
	public Integer getCoupon88Remind() {
		return coupon88Remind;
	}
	public void setCoupon88Remind(Integer coupon88Remind) {
		this.coupon88Remind = coupon88Remind;
	}
	public Integer getPosition() {
		return position;
	}
	public void setPosition(Integer position) {
		this.position = position;
	}
	public String getRewardResult() {
		return rewardResult;
	}
	public void setRewardResult(String rewardResult) {
		this.rewardResult = rewardResult;
	}
	
	
}
