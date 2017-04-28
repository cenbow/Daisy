package com.yourong.core.mc.model.biz;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * 520活动
 * 
 * @author wangyanji
 */
public class ActivityFor520 extends AbstractBaseObject{

	private Date startTime;

	private Date endTime;

	private Integer activityStatus;

	private Long couponNumber;
	
	private String receiveStartTimeStr;
	
	private BigDecimal receiveGiftPacksAmount;

	private BigDecimal memberTotalAmount;

	private Integer giftPacksForPopularity;

	private List<Long> giftPacksForCoupons;

	private String shortMsg;

	private String webMsg;

	private BigDecimal minInvestAmount;

	private List<ActivityForRankListBiz> rankList;

	public Long getCouponNumber() {
		return couponNumber;
	}

	public String getCouponNumberStr() {
		if (getCouponNumber() != null)
			return getCouponNumber().toString();
		else
			return "";
	}

	public void setCouponNumber(Long couponNumber) {
		this.couponNumber = couponNumber;
	}

	public String getReceiveStartTimeStr() {
		return receiveStartTimeStr;
	}

	public void setReceiveStartTimeStr(String receiveStartTimeStr) {
		this.receiveStartTimeStr = receiveStartTimeStr;
	}

	public List<ActivityForRankListBiz> getRankList() {
		return rankList;
	}

	public void setRankList(List<ActivityForRankListBiz> rankList) {
		this.rankList = rankList;
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

	public BigDecimal getReceiveGiftPacksAmount() {
		return receiveGiftPacksAmount;
	}

	public void setReceiveGiftPacksAmount(BigDecimal receiveGiftPacksAmount) {
		this.receiveGiftPacksAmount = receiveGiftPacksAmount;
	}

	public BigDecimal getMemberTotalAmount() {
		return memberTotalAmount;
	}

	public void setMemberTotalAmount(BigDecimal memberTotalAmount) {
		this.memberTotalAmount = memberTotalAmount;
	}

	public Integer getGiftPacksForPopularity() {
		return giftPacksForPopularity;
	}

	public void setGiftPacksForPopularity(Integer giftPacksForPopularity) {
		this.giftPacksForPopularity = giftPacksForPopularity;
	}

	public List<Long> getGiftPacksForCoupons() {
		return giftPacksForCoupons;
	}

	public void setGiftPacksForCoupons(List<Long> giftPacksForCoupons) {
		this.giftPacksForCoupons = giftPacksForCoupons;
	}

	public String getShortMsg() {
		return shortMsg;
	}

	public void setShortMsg(String shortMsg) {
		this.shortMsg = shortMsg;
	}

	public String getWebMsg() {
		return webMsg;
	}

	public void setWebMsg(String webMsg) {
		this.webMsg = webMsg;
	}

	public BigDecimal getMinInvestAmount() {
		return minInvestAmount;
	}

	public void setMinInvestAmount(BigDecimal minInvestAmount) {
		this.minInvestAmount = minInvestAmount;
	}

}
