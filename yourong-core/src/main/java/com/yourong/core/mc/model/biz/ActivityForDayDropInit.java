package com.yourong.core.mc.model.biz;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.core.mc.model.ActivityLotteryResult;
import com.yourong.core.tc.model.biz.TransactionForFirstInvestAct;

/**
 * 领取福袋
 * @author chaisen
 *
 */
public class ActivityForDayDropInit extends AbstractBaseObject {
	
	/**
	 * 开始时间
	 */
	private Date startTime;
	
	/**
	 * 结束时间
	 */
	private Date endTime;
	/**
	 * 活动状态
	 */
	private int status;
	
	
	private int totalRed;
	
	
	private BigDecimal myInvestAmount;
	
	
	private BigDecimal myTotalInvestAmount;
	
	
	private List<ActivityForMemberInfo> investFirstTen;


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


	public int getStatus() {
		return status;
	}


	public void setStatus(int status) {
		this.status = status;
	}


	

	public int getTotalRed() {
		return totalRed;
	}


	public void setTotalRed(int totalRed) {
		this.totalRed = totalRed;
	}


	public BigDecimal getMyInvestAmount() {
		return myInvestAmount;
	}


	public void setMyInvestAmount(BigDecimal myInvestAmount) {
		this.myInvestAmount = myInvestAmount;
	}


	public BigDecimal getMyTotalInvestAmount() {
		return myTotalInvestAmount;
	}


	public void setMyTotalInvestAmount(BigDecimal myTotalInvestAmount) {
		this.myTotalInvestAmount = myTotalInvestAmount;
	}


	public List<ActivityForMemberInfo> getInvestFirstTen() {
		return investFirstTen;
	}


	public void setInvestFirstTen(List<ActivityForMemberInfo> investFirstTen) {
		this.investFirstTen = investFirstTen;
	}
	
	

	
	
}
