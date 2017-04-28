package com.yourong.core.mc.model.biz;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.core.tc.model.biz.TransactionForFirstInvestAct;
/**
 * 
 * @desc TODO
 * @author chaisen
 * 2016年12月6日上午10:05:53
 */
public class ActivityForDouble extends AbstractBaseObject {
	
	
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
	
	private Integer ten;
	
	private Integer fifTeen;
	
	private Integer totalRed;
	
	private BigDecimal myInvestAmount;
	
	private List<Integer> singleInvestAmount;
	
	private List<Double> probabilityListTen;
	
	private List<Double> probabilityListFifTeen;
	
	private List<Long> couponTemplate;
	
	private List<BigDecimal> investAmounts;
	
	private List<Long> templates;
	
	private List<TransactionForFirstInvestAct> firstInvestAmount;

	private List<TransactionForFirstInvestAct> everyDayFirstInvestAmount;
	
	private List<TransactionForFirstInvestAct> countFirstAmountNumber;
	
	private BigDecimal couponAmount;
	
	private Integer redRemind;
	
	
	
	

	public Integer getRedRemind() {
		return redRemind;
	}

	public void setRedRemind(Integer redRemind) {
		this.redRemind = redRemind;
	}

	public BigDecimal getCouponAmount() {
		return couponAmount;
	}

	public void setCouponAmount(BigDecimal couponAmount) {
		this.couponAmount = couponAmount;
	}

	public List<TransactionForFirstInvestAct> getCountFirstAmountNumber() {
		return countFirstAmountNumber;
	}

	public void setCountFirstAmountNumber(List<TransactionForFirstInvestAct> countFirstAmountNumber) {
		this.countFirstAmountNumber = countFirstAmountNumber;
	}

	public List<TransactionForFirstInvestAct> getEveryDayFirstInvestAmount() {
		return everyDayFirstInvestAmount;
	}

	public void setEveryDayFirstInvestAmount(List<TransactionForFirstInvestAct> everyDayFirstInvestAmount) {
		this.everyDayFirstInvestAmount = everyDayFirstInvestAmount;
	}

	public List<TransactionForFirstInvestAct> getFirstInvestAmount() {
		return firstInvestAmount;
	}

	public void setFirstInvestAmount(List<TransactionForFirstInvestAct> firstInvestAmount) {
		this.firstInvestAmount = firstInvestAmount;
	}

	public List<Long> getTemplates() {
		return templates;
	}

	public void setTemplates(List<Long> templates) {
		this.templates = templates;
	}

	public List<BigDecimal> getInvestAmounts() {
		return investAmounts;
	}

	public void setInvestAmounts(List<BigDecimal> investAmounts) {
		this.investAmounts = investAmounts;
	}

	public List<Long> getCouponTemplate() {
		return couponTemplate;
	}

	public void setCouponTemplate(List<Long> couponTemplate) {
		this.couponTemplate = couponTemplate;
	}

	public List<Double> getProbabilityListTen() {
		return probabilityListTen;
	}

	public void setProbabilityListTen(List<Double> probabilityListTen) {
		this.probabilityListTen = probabilityListTen;
	}

	

	public List<Double> getProbabilityListFifTeen() {
		return probabilityListFifTeen;
	}

	public void setProbabilityListFifTeen(List<Double> probabilityListFifTeen) {
		this.probabilityListFifTeen = probabilityListFifTeen;
	}

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

	public Integer getTen() {
		return ten;
	}

	public void setTen(Integer ten) {
		this.ten = ten;
	}

	

	public Integer getFifTeen() {
		return fifTeen;
	}

	public void setFifTeen(Integer fifTeen) {
		this.fifTeen = fifTeen;
	}

	public Integer getTotalRed() {
		return totalRed;
	}

	public void setTotalRed(Integer totalRed) {
		this.totalRed = totalRed;
	}

	public List<Integer> getSingleInvestAmount() {
		return singleInvestAmount;
	}

	public void setSingleInvestAmount(List<Integer> singleInvestAmount) {
		this.singleInvestAmount = singleInvestAmount;
	}

	public BigDecimal getMyInvestAmount() {
		return myInvestAmount;
	}

	public void setMyInvestAmount(BigDecimal myInvestAmount) {
		this.myInvestAmount = myInvestAmount;
	}
	
	
	

	
}
