package com.yourong.core.mc.model.biz;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * 
 * @desc 七月活动规则
 * @author chaisen
 * 2016年6月30日上午10:16:12
 */
public class ActivityForJulyDate extends AbstractBaseObject {
		
	private Integer joinStartTime;
	
	private Integer joinEndTime;
	
	private Integer betStartTime;
	
	private Integer betEndTime;
	
	private Integer receiveStartTime;
	
	private Integer receiveEndTime;
	
	private Integer countStartTime;
	
	private Integer countEndTime;
	
	private BigDecimal investAmount;
	
	private BigDecimal thousandCase;
	
	private List<Long> templateId;
	
	private List<Integer> couponAmount;
	
	private List<Integer> totalCoupon;

	public Integer getJoinStartTime() {
		return joinStartTime;
	}

	public void setJoinStartTime(Integer joinStartTime) {
		this.joinStartTime = joinStartTime;
	}

	public Integer getJoinEndTime() {
		return joinEndTime;
	}

	public void setJoinEndTime(Integer joinEndTime) {
		this.joinEndTime = joinEndTime;
	}

	public Integer getBetStartTime() {
		return betStartTime;
	}

	public void setBetStartTime(Integer betStartTime) {
		this.betStartTime = betStartTime;
	}

	public Integer getBetEndTime() {
		return betEndTime;
	}

	public void setBetEndTime(Integer betEndTime) {
		this.betEndTime = betEndTime;
	}

	public Integer getReceiveStartTime() {
		return receiveStartTime;
	}

	public void setReceiveStartTime(Integer receiveStartTime) {
		this.receiveStartTime = receiveStartTime;
	}

	public Integer getReceiveEndTime() {
		return receiveEndTime;
	}

	public void setReceiveEndTime(Integer receiveEndTime) {
		this.receiveEndTime = receiveEndTime;
	}

	public BigDecimal getInvestAmount() {
		return investAmount;
	}

	public void setInvestAmount(BigDecimal investAmount) {
		this.investAmount = investAmount;
	}

	public List<Long> getTemplateId() {
		return templateId;
	}

	public void setTemplateId(List<Long> templateId) {
		this.templateId = templateId;
	}

	public List<Integer> getCouponAmount() {
		return couponAmount;
	}

	public void setCouponAmount(List<Integer> couponAmount) {
		this.couponAmount = couponAmount;
	}

	public List<Integer> getTotalCoupon() {
		return totalCoupon;
	}

	public void setTotalCoupon(List<Integer> totalCoupon) {
		this.totalCoupon = totalCoupon;
	}

	public Integer getCountStartTime() {
		return countStartTime;
	}

	public void setCountStartTime(Integer countStartTime) {
		this.countStartTime = countStartTime;
	}

	public Integer getCountEndTime() {
		return countEndTime;
	}

	public void setCountEndTime(Integer countEndTime) {
		this.countEndTime = countEndTime;
	}

	public BigDecimal getThousandCase() {
		return thousandCase;
	}

	public void setThousandCase(BigDecimal thousandCase) {
		this.thousandCase = thousandCase;
	}

	
	
	
	
	
}
