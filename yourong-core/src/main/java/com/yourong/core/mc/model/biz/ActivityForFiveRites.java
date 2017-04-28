package com.yourong.core.mc.model.biz;

import java.math.BigDecimal;

import com.yourong.common.domain.AbstractBaseObject;

public class ActivityForFiveRites extends AbstractBaseObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8498764095852065272L;
	int[] ymjrPopularityValue=new int[4];
	int[] ycdyPopularityValue=new int[4];
	int[] yzqjPopularityValue=new int[4]; 
	int[] days=new int[4];
	String[] totalAmount=new String[4];
	int[] totalDays=new int[3];
	
	public int day;
	
	public BigDecimal investRecommendScale;
	
	public int[] getYmjrPopularityValue() {
		return ymjrPopularityValue;
	}
	public void setYmjrPopularityValue(int[] ymjrPopularityValue) {
		this.ymjrPopularityValue = ymjrPopularityValue;
	}
	public int[] getYcdyPopularityValue() {
		return ycdyPopularityValue;
	}
	public void setYcdyPopularityValue(int[] ycdyPopularityValue) {
		this.ycdyPopularityValue = ycdyPopularityValue;
	}
	public int[] getYzqjPopularityValue() {
		return yzqjPopularityValue;
	}
	public void setYzqjPopularityValue(int[] yzqjPopularityValue) {
		this.yzqjPopularityValue = yzqjPopularityValue;
	}
	public int[] getDays() {
		return days;
	}
	public void setDays(int[] days) {
		this.days = days;
	}
	
	public String[] getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(String[] totalAmount) {
		this.totalAmount = totalAmount;
	}
	public int[] getTotalDays() {
		return totalDays;
	}
	public void setTotalDays(int[] totalDays) {
		this.totalDays = totalDays;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public BigDecimal getInvestRecommendScale() {
		return investRecommendScale;
	}
	public void setInvestRecommendScale(BigDecimal investRecommendScale) {
		this.investRecommendScale = investRecommendScale;
	}
	
	
	
	
	
}
