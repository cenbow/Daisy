package com.yourong.api.dto;


import java.util.Date;
import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.core.ic.model.PrizeInPool;

public class PrizeInPoolForProject extends  AbstractBaseObject{

	//天数
	private Integer day;
	
	private Date dayTime;
	
	//系数
	private String ratio;

	
	private Integer rewardAmount;
	
	private Integer orignRewardAmount;
	
	//每天的奖项情况
	private List<PrizeInPool> prizeInPoolList;

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	public List<PrizeInPool> getPrizeInPoolList() {
		return prizeInPoolList;
	}

	public void setPrizeInPoolList(List<PrizeInPool> prizeInPoolList) {
		this.prizeInPoolList = prizeInPoolList;
	}

	public Date getDayTime() {
		return dayTime;
	}

	public void setDayTime(Date dayTime) {
		this.dayTime = dayTime;
	}

	public Integer getRewardAmount() {
		return rewardAmount;
	}

	public void setRewardAmount(Integer rewardAmount) {
		this.rewardAmount = rewardAmount;
	}

	public Integer getOrignRewardAmount() {
		return orignRewardAmount;
	}

	public void setOrignRewardAmount(Integer orignRewardAmount) {
		this.orignRewardAmount = orignRewardAmount;
	}

	public String getRatio() {
		return ratio;
	}

	public void setRatio(String ratio) {
		this.ratio = ratio;
	}
	
}
