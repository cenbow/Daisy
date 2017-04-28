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
public class ActivityForFiveBillionInit extends AbstractBaseObject {
	
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
	
	
	private Integer reward=0;
	
	private String rewardInfo;
	
	private BigDecimal myInvestAmount;
	
	private int countLuckBoth;
	
	private int countLuckMonkey;
	
	private List<ActivityForFiveBillionRetrun> myListLuckBoth;
	
	private List<ActivityForFiveBillionRetrun> myListLuckMonkey;
	
	
	private List<ActivityForMemberInfo> luckBothRecord;
	
	private List<ActivityForMemberInfo> luckMonkeyRecord;
	
	private int number;
	

	/**
	 * @return the number
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * @param number the number to set
	 */
	public void setNumber(int number) {
		this.number = number;
	}

	/**
	 * @return the luckBothRecord
	 */
	public List<ActivityForMemberInfo> getLuckBothRecord() {
		return luckBothRecord;
	}

	/**
	 * @param luckBothRecord the luckBothRecord to set
	 */
	public void setLuckBothRecord(List<ActivityForMemberInfo> luckBothRecord) {
		this.luckBothRecord = luckBothRecord;
	}

	/**
	 * @return the luckMonkeyRecord
	 */
	public List<ActivityForMemberInfo> getLuckMonkeyRecord() {
		return luckMonkeyRecord;
	}

	/**
	 * @param luckMonkeyRecord the luckMonkeyRecord to set
	 */
	public void setLuckMonkeyRecord(List<ActivityForMemberInfo> luckMonkeyRecord) {
		this.luckMonkeyRecord = luckMonkeyRecord;
	}

	

	/**
	 * @return the myListLuckBoth
	 */
	public List<ActivityForFiveBillionRetrun> getMyListLuckBoth() {
		return myListLuckBoth;
	}

	/**
	 * @param myListLuckBoth the myListLuckBoth to set
	 */
	public void setMyListLuckBoth(List<ActivityForFiveBillionRetrun> myListLuckBoth) {
		this.myListLuckBoth = myListLuckBoth;
	}

	/**
	 * @return the myListLuckMonkey
	 */
	public List<ActivityForFiveBillionRetrun> getMyListLuckMonkey() {
		return myListLuckMonkey;
	}

	/**
	 * @param myListLuckMonkey the myListLuckMonkey to set
	 */
	public void setMyListLuckMonkey(
			List<ActivityForFiveBillionRetrun> myListLuckMonkey) {
		this.myListLuckMonkey = myListLuckMonkey;
	}

	/**
	 * @return the countLuckBoth
	 */
	public int getCountLuckBoth() {
		return countLuckBoth;
	}

	/**
	 * @param countLuckBoth the countLuckBoth to set
	 */
	public void setCountLuckBoth(int countLuckBoth) {
		this.countLuckBoth = countLuckBoth;
	}

	/**
	 * @return the countLuckMonkey
	 */
	public int getCountLuckMonkey() {
		return countLuckMonkey;
	}

	/**
	 * @param countLuckMonkey the countLuckMonkey to set
	 */
	public void setCountLuckMonkey(int countLuckMonkey) {
		this.countLuckMonkey = countLuckMonkey;
	}

	/**
	 * @return the myInvestAmount
	 */
	public BigDecimal getMyInvestAmount() {
		return myInvestAmount;
	}

	/**
	 * @param myInvestAmount the myInvestAmount to set
	 */
	public void setMyInvestAmount(BigDecimal myInvestAmount) {
		this.myInvestAmount = myInvestAmount;
	}

	/**
	 * @return the startTime
	 */
	public Date getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the endTime
	 */
	public Date getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	public String getRewardInfo() {
		return rewardInfo;
	}

	public void setRewardInfo(String rewardInfo) {
		this.rewardInfo = rewardInfo;
	}

	public Integer getReward() {
		return reward;
	}

	public void setReward(Integer reward) {
		this.reward = reward;
	}
	
	
}
