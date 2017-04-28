package com.yourong.core.mc.model.biz;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * 
 * @desc 百万现金券活动model
 * @author wangyanji 2016年1月5日下午4:46:53
 */
public class ActivityForBreak2Billion extends AbstractBaseObject {

	private Date startTime;
	private Date endTime;
	private Integer activityStatus;
	private Integer fund;
	private List<Integer> fundLevel;
	private List<Long> fundPrizeLevel;
	private Integer lotteryMinInvest;
	private String lotteryRewardName;
	private String lotteryRewardCode;
	private List<ActivityLotteryResultBiz> lotteryList;
	private List<Integer> giftLevel;
	private List<String> giftPrizeLevel;
	private Integer giftOutTime;
	private Integer giftIndex;
	private boolean hasBreak = false;
	private Date break2BillionTime;
	private String amountUnit = "2000000000";
	private boolean hasLottery = false;
	private boolean hasReceiveGift = false;
	private List<ActivityForRankListBiz> rankList;
	private BigDecimal memberTotalAmount;
	private BigDecimal memberDayAmount;
	private Integer activityPart;

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

	public Integer getFund() {
		return fund;
	}

	public void setFund(Integer fund) {
		this.fund = fund;
	}

	public List<Integer> getFundLevel() {
		return fundLevel;
	}

	public void setFundLevel(List<Integer> fundLevel) {
		this.fundLevel = fundLevel;
	}

	public List<Long> getFundPrizeLevel() {
		return fundPrizeLevel;
	}

	public void setFundPrizeLevel(List<Long> fundPrizeLevel) {
		this.fundPrizeLevel = fundPrizeLevel;
	}

	public Integer getLotteryMinInvest() {
		return lotteryMinInvest;
	}

	public void setLotteryMinInvest(Integer lotteryMinInvest) {
		this.lotteryMinInvest = lotteryMinInvest;
	}

	public List<Integer> getGiftLevel() {
		return giftLevel;
	}

	public void setGiftLevel(List<Integer> giftLevel) {
		this.giftLevel = giftLevel;
	}

	public List<String> getGiftPrizeLevel() {
		return giftPrizeLevel;
	}

	public void setGiftPrizeLevel(List<String> giftPrizeLevel) {
		this.giftPrizeLevel = giftPrizeLevel;
	}

	public boolean isHasBreak() {
		return hasBreak;
	}

	public void setHasBreak(boolean hasBreak) {
		this.hasBreak = hasBreak;
	}

	public Date getBreak2BillionTime() {
		return break2BillionTime;
	}

	public void setBreak2BillionTime(Date break2BillionTime) {
		this.break2BillionTime = break2BillionTime;
	}

	public Integer getGiftOutTime() {
		return giftOutTime;
	}

	public void setGiftOutTime(Integer giftOutTime) {
		this.giftOutTime = giftOutTime;
	}

	public String getAmountUnit() {
		return amountUnit;
	}

	public void setAmountUnit(String amountUnit) {
		this.amountUnit = amountUnit;
	}

	public boolean isHasReceiveGift() {
		return hasReceiveGift;
	}

	public void setHasReceiveGift(boolean hasReceiveGift) {
		this.hasReceiveGift = hasReceiveGift;
	}

	public List<ActivityForRankListBiz> getRankList() {
		return rankList;
	}

	public void setRankList(List<ActivityForRankListBiz> rankList) {
		this.rankList = rankList;
	}

	public boolean isHasLottery() {
		return hasLottery;
	}

	public void setHasLottery(boolean hasLottery) {
		this.hasLottery = hasLottery;
	}

	public BigDecimal getMemberTotalAmount() {
		return memberTotalAmount;
	}

	public void setMemberTotalAmount(BigDecimal memberTotalAmount) {
		this.memberTotalAmount = memberTotalAmount;
	}

	public String getLotteryRewardName() {
		return lotteryRewardName;
	}

	public void setLotteryRewardName(String lotteryRewardName) {
		this.lotteryRewardName = lotteryRewardName;
	}

	public String getLotteryRewardCode() {
		return lotteryRewardCode;
	}

	public void setLotteryRewardCode(String lotteryRewardCode) {
		this.lotteryRewardCode = lotteryRewardCode;
	}

	public Integer getGiftIndex() {
		return giftIndex;
	}

	public void setGiftIndex(Integer giftIndex) {
		this.giftIndex = giftIndex;
	}

	public List<ActivityLotteryResultBiz> getLotteryList() {
		return lotteryList;
	}

	public void setLotteryList(List<ActivityLotteryResultBiz> lotteryList) {
		this.lotteryList = lotteryList;
	}

	public BigDecimal getMemberDayAmount() {
		return memberDayAmount;
	}

	public void setMemberDayAmount(BigDecimal memberDayAmount) {
		this.memberDayAmount = memberDayAmount;
	}

	public Integer getActivityPart() {
		return activityPart;
	}

	public void setActivityPart(Integer activityPart) {
		this.activityPart = activityPart;
	}

}
