package com.yourong.core.mc.model.biz;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class ActivityForAnniversaryCelebrate implements Serializable  {


	private Date startTime;
	private Date endTime;
	private Date coupon88StartTime;
	private Integer activityStatus;	
	private BigDecimal totalMyInvestAmount;
	//当前剩余人气值
	private Integer remindPopularityVaule;
	private List<BigDecimal> firstInvestAmount;
	private List<Long> firstTemplateId;
	private Date eightCouponStartTime;
	private List<Integer> couponAmount;
	private List<Long> templateId;
	private List<Long> ptemplateId; 
	private Integer totalDays;
	private List<BigDecimal> totalAmount;
	private Integer totalCoupon88;
	private Integer coupon88Detault;
	private Integer coupon88Remind;
	private BigDecimal availableBalance;
	private List<Integer> popularValueRealData;
	private Long templateId88;
	
	private boolean popularity19=false;
	private boolean popularity199=false;
	private boolean popularity659=false;
	private boolean popularity1119=false;
	private boolean popularity2016=false;
	private boolean iphone7=false;
	
	private Integer position;
	private String rewardResult;
	private BigDecimal investAmount;
	
	
	private Integer myPopularity;
	private Integer myNumber;
	
	
	
	public boolean isPopularity19() {
		return popularity19;
	}
	public void setPopularity19(boolean popularity19) {
		this.popularity19 = popularity19;
	}
	public Integer getMyPopularity() {
		return myPopularity;
	}
	public void setMyPopularity(Integer myPopularity) {
		this.myPopularity = myPopularity;
	}
	public Integer getMyNumber() {
		return myNumber;
	}
	public void setMyNumber(Integer myNumber) {
		this.myNumber = myNumber;
	}
	public List<Long> getPtemplateId() {
		return ptemplateId;
	}
	public void setPtemplateId(List<Long> ptemplateId) {
		this.ptemplateId = ptemplateId;
	}
	public Long getTemplateId88() {
		return templateId88;
	}
	public void setTemplateId88(Long templateId88) {
		this.templateId88 = templateId88;
	}
	public List<Integer> getPopularValueRealData() {
		return popularValueRealData;
	}
	public void setPopularValueRealData(List<Integer> popularValueRealData) {
		this.popularValueRealData = popularValueRealData;
	}
	public BigDecimal getAvailableBalance() {
		return availableBalance;
	}
	public void setAvailableBalance(BigDecimal availableBalance) {
		this.availableBalance = availableBalance;
	}
	public BigDecimal getInvestAmount() {
		return investAmount;
	}
	public void setInvestAmount(BigDecimal investAmount) {
		this.investAmount = investAmount;
	}
	public Integer getPosition() {
		return position;
	}
	public void setPosition(Integer position) {
		this.position = position;
	}
	public String getRewardResult() {
		return rewardResult;
	}
	public void setRewardResult(String rewardResult) {
		this.rewardResult = rewardResult;
	}
	
	public boolean isPopularity199() {
		return popularity199;
	}
	public void setPopularity199(boolean popularity199) {
		this.popularity199 = popularity199;
	}
	public boolean isPopularity659() {
		return popularity659;
	}
	public void setPopularity659(boolean popularity659) {
		this.popularity659 = popularity659;
	}
	public boolean isPopularity2016() {
		return popularity2016;
	}
	public void setPopularity2016(boolean popularity2016) {
		this.popularity2016 = popularity2016;
	}
	public boolean isPopularity1119() {
		return popularity1119;
	}
	public void setPopularity1119(boolean popularity1119) {
		this.popularity1119 = popularity1119;
	}
	public boolean isIphone7() {
		return iphone7;
	}
	public void setIphone7(boolean iphone7) {
		this.iphone7 = iphone7;
	}
	public Integer getCoupon88Remind() {
		return coupon88Remind;
	}
	public void setCoupon88Remind(Integer coupon88Remind) {
		this.coupon88Remind = coupon88Remind;
	}
	public Integer getCoupon88Detault() {
		return coupon88Detault;
	}
	public void setCoupon88Detault(Integer coupon88Detault) {
		this.coupon88Detault = coupon88Detault;
	}
	public Integer getTotalCoupon88() {
		return totalCoupon88;
	}
	public void setTotalCoupon88(Integer totalCoupon88) {
		this.totalCoupon88 = totalCoupon88;
	}
	public Date getCoupon88StartTime() {
		return coupon88StartTime;
	}
	public void setCoupon88StartTime(Date coupon88StartTime) {
		this.coupon88StartTime = coupon88StartTime;
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
	public Integer getActivityStatus() {
		return activityStatus;
	}
	public void setActivityStatus(Integer activityStatus) {
		this.activityStatus = activityStatus;
	}
	public BigDecimal getTotalMyInvestAmount() {
		return totalMyInvestAmount;
	}
	public void setTotalMyInvestAmount(BigDecimal totalMyInvestAmount) {
		this.totalMyInvestAmount = totalMyInvestAmount;
	}
	public Integer getRemindPopularityVaule() {
		return remindPopularityVaule;
	}
	public void setRemindPopularityVaule(Integer remindPopularityVaule) {
		this.remindPopularityVaule = remindPopularityVaule;
	}
	public List<BigDecimal> getFirstInvestAmount() {
		return firstInvestAmount;
	}
	public void setFirstInvestAmount(List<BigDecimal> firstInvestAmount) {
		this.firstInvestAmount = firstInvestAmount;
	}
	public List<Long> getFirstTemplateId() {
		return firstTemplateId;
	}
	public void setFirstTemplateId(List<Long> firstTemplateId) {
		this.firstTemplateId = firstTemplateId;
	}
	public Date getEightCouponStartTime() {
		return eightCouponStartTime;
	}
	public void setEightCouponStartTime(Date eightCouponStartTime) {
		this.eightCouponStartTime = eightCouponStartTime;
	}
	public List<Integer> getCouponAmount() {
		return couponAmount;
	}
	public void setCouponAmount(List<Integer> couponAmount) {
		this.couponAmount = couponAmount;
	}
	public List<Long> getTemplateId() {
		return templateId;
	}
	public void setTemplateId(List<Long> templateId) {
		this.templateId = templateId;
	}
	public Integer getTotalDays() {
		return totalDays;
	}
	public void setTotalDays(Integer totalDays) {
		this.totalDays = totalDays;
	}
	public List<BigDecimal> getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(List<BigDecimal> totalAmount) {
		this.totalAmount = totalAmount;
	}
	
	
}
