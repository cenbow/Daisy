package com.yourong.core.ic.model;

import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;

public class DirectLotteryRuleForBackend extends AbstractBaseObject{

	
	private List<PrizePool> prizePool;
	
	//奖励时间：自项目上线多少小时内可抽奖
	private String rewardHour;
	
	//未能中奖时赠送的人气值
	private String popularity;
	
	private List<PrizeInPool> prizeInPool;
	
	
	private List<LotteryRuleAmountNumber> lottery;


	public List<PrizePool> getPrizePool() {
		return prizePool;
	}


	public void setPrizePool(List<PrizePool> prizePool) {
		this.prizePool = prizePool;
	}


	public List<PrizeInPool> getPrizeInPool() {
		return prizeInPool;
	}


	public void setPrizeInPool(List<PrizeInPool> prizeInPool) {
		this.prizeInPool = prizeInPool;
	}


	public List<LotteryRuleAmountNumber> getLottery() {
		return lottery;
	}


	public void setLottery(List<LotteryRuleAmountNumber> lottery) {
		this.lottery = lottery;
	}


	public String getRewardHour() {
		return rewardHour;
	}


	public void setRewardHour(String rewardHour) {
		this.rewardHour = rewardHour;
	}


	public String getPopularity() {
		return popularity;
	}


	public void setPopularity(String popularity) {
		this.popularity = popularity;
	}
	
}
