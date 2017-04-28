package com.yourong.core.mc.model.biz;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * 
 * @desc 七月战队
 * @author chaisen	
 * 2016年6月28日下午4:01:35
 */
public class ActivityForJulyHistory extends AbstractBaseObject {
	private BigDecimal todayInvestAmountA;
	private BigDecimal todayInvestAmountB;
	private String pkTime;
	private Date happenTime;
	public BigDecimal getTodayInvestAmountA() {
		return todayInvestAmountA;
	}
	public void setTodayInvestAmountA(BigDecimal todayInvestAmountA) {
		this.todayInvestAmountA = todayInvestAmountA;
	}
	public BigDecimal getTodayInvestAmountB() {
		return todayInvestAmountB;
	}
	public void setTodayInvestAmountB(BigDecimal todayInvestAmountB) {
		this.todayInvestAmountB = todayInvestAmountB;
	}
	public String getPkTime() {
		return pkTime;
	}
	public void setPkTime(String pkTime) {
		this.pkTime = pkTime;
	}
	public Date getHappenTime() {
		return happenTime;
	}
	public void setHappenTime(Date happenTime) {
		this.happenTime = happenTime;
	}
	
	
}
