package com.yourong.core.mc.model.biz;

import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.core.tc.model.biz.TransactionForFirstInvestAct;

/**
 * 
 * @desc 七月战队
 * @author chaisen	
 * 2016年6月28日下午4:01:35
 */
public class ActivityForJulyRetrun extends AbstractBaseObject {
	private Integer currentGroupType;
	
	private Integer popularityValue;
	
	private Integer couponValue;
	
	private ActivityForJulyBet couponRemind;
	
	private List<ActivityForJulyBet> betList;
	
	private Integer betTotals;
	
	private List<TransactionForFirstInvestAct> julyTeamContribution;

	public Integer getCurrentGroupType() {
		return currentGroupType;
	}

	public void setCurrentGroupType(Integer currentGroupType) {
		this.currentGroupType = currentGroupType;
	}

	public Integer getPopularityValue() {
		return popularityValue;
	}

	public void setPopularityValue(Integer popularityValue) {
		this.popularityValue = popularityValue;
	}

	public Integer getCouponValue() {
		return couponValue;
	}

	public void setCouponValue(Integer couponValue) {
		this.couponValue = couponValue;
	}

	public List<ActivityForJulyBet> getBetList() {
		return betList;
	}

	public void setBetList(List<ActivityForJulyBet> betList) {
		this.betList = betList;
	}

	public ActivityForJulyBet getCouponRemind() {
		return couponRemind;
	}

	public void setCouponRemind(ActivityForJulyBet couponRemind) {
		this.couponRemind = couponRemind;
	}

	public Integer getBetTotals() {
		return betTotals;
	}

	public void setBetTotals(Integer betTotals) {
		this.betTotals = betTotals;
	}

	public List<TransactionForFirstInvestAct> getJulyTeamContribution() {
		return julyTeamContribution;
	}

	public void setJulyTeamContribution(List<TransactionForFirstInvestAct> julyTeamContribution) {
		this.julyTeamContribution = julyTeamContribution;
	}
	
	
}
