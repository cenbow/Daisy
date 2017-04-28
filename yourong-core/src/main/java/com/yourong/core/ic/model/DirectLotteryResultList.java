package com.yourong.core.ic.model;

import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;

public class DirectLotteryResultList extends AbstractBaseObject{
	
	//奖励等级
	private String prize;
	
	//奖励金额
	private String rewardAmount;

	public String getPrize() {
		return prize;
	}

	public void setPrize(String prize) {
		this.prize = prize;
	}

	public String getRewardAmount() {
		return rewardAmount;
	}

	public void setRewardAmount(String rewardAmount) {
		this.rewardAmount = rewardAmount;
	}
	
}
