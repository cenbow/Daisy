package com.yourong.core.mc.model.biz;

import java.util.Date;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * 领取福袋
 * @author chaisen
 *
 */
public class ActivityForFiveBillionRetrun extends AbstractBaseObject {
	private Integer reward;
	
	private String rewardInfo;
	
	private Date happenTime;
	
	
	public Date getHappenTime() {
		return happenTime;
	}

	public void setHappenTime(Date happenTime) {
		this.happenTime = happenTime;
	}

	public String getRewardInfo() {
		return rewardInfo;
	}

	public void setRewardInfo(String rewardInfo) {
		this.rewardInfo = rewardInfo;
	}

	public Integer getReward() {
		return reward;
	}

	public void setReward(Integer reward) {
		this.reward = reward;
	}
	
	
}
