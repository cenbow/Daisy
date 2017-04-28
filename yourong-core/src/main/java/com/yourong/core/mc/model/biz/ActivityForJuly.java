package com.yourong.core.mc.model.biz;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.core.tc.model.biz.TransactionForFirstInvestAct;

/**
 * 
 * @desc 七月战队
 * @author chaisen
 * 2016年6月28日下午4:01:35
 */
public class ActivityForJuly extends AbstractBaseObject {

	private Date startTime;
	private Date endTime;
	private Integer activityStatus;
	private boolean isJoined=false;
	private boolean isBeted=false;
	private BigDecimal todayInvestAmountA;
	private BigDecimal todayInvestAmountB;
	private BigDecimal todayInvestAmountMy;
	private String avatars;
	private Integer currentGroupType;
	private Integer successFlag=0;
	private boolean canJoinTeam=false;
	private boolean canBet=false;
	private boolean canReceive=false;
	private List<ActivityForJulyBet> betList;
	private List<ActivityForJulyHistory> pkHistoryList;
	private List<TransactionForFirstInvestAct> julyTeamContribution;
	private ActivityForJulyBet remindCoupon;
	private boolean ifFirstBet=false;
	private Integer currentBetMember;
	private Integer lastBetMember;
	
	private Integer joinStartTime;
	private Integer joinEndTime;
	private Integer betStartTime;
	private Integer betEndTime;
	private Integer receiveStartTime;
	private Integer receiveEndTime;
	private Integer countStartTime;
	private Integer countEndTime;
	
	
	
	
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
	public boolean isJoined() {
		return isJoined;
	}
	public void setJoined(boolean isJoined) {
		this.isJoined = isJoined;
	}
	public boolean isBeted() {
		return isBeted;
	}
	public void setBeted(boolean isBeted) {
		this.isBeted = isBeted;
	}
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
	public BigDecimal getTodayInvestAmountMy() {
		return todayInvestAmountMy;
	}
	public void setTodayInvestAmountMy(BigDecimal todayInvestAmountMy) {
		this.todayInvestAmountMy = todayInvestAmountMy;
	}
	public String getAvatars() {
		return avatars;
	}
	public void setAvatars(String avatars) {
		this.avatars = avatars;
	}
	public Integer getCurrentGroupType() {
		return currentGroupType;
	}
	public void setCurrentGroupType(Integer currentGroupType) {
		this.currentGroupType = currentGroupType;
	}
	public Integer getSuccessFlag() {
		return successFlag;
	}
	public void setSuccessFlag(Integer successFlag) {
		this.successFlag = successFlag;
	}
	public boolean isCanJoinTeam() {
		return canJoinTeam;
	}
	public void setCanJoinTeam(boolean canJoinTeam) {
		this.canJoinTeam = canJoinTeam;
	}
	public boolean isCanBet() {
		return canBet;
	}
	public void setCanBet(boolean canBet) {
		this.canBet = canBet;
	}
	public List<ActivityForJulyBet> getBetList() {
		return betList;
	}
	public void setBetList(List<ActivityForJulyBet> betList) {
		this.betList = betList;
	}
	public boolean isIfFirstBet() {
		return ifFirstBet;
	}
	public void setIfFirstBet(boolean ifFirstBet) {
		this.ifFirstBet = ifFirstBet;
	}
	public Integer getCurrentBetMember() {
		return currentBetMember;
	}
	public void setCurrentBetMember(Integer currentBetMember) {
		this.currentBetMember = currentBetMember;
	}
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
	public List<TransactionForFirstInvestAct> getJulyTeamContribution() {
		return julyTeamContribution;
	}
	public void setJulyTeamContribution(List<TransactionForFirstInvestAct> julyTeamContribution) {
		this.julyTeamContribution = julyTeamContribution;
	}
	public ActivityForJulyBet getRemindCoupon() {
		return remindCoupon;
	}
	public void setRemindCoupon(ActivityForJulyBet remindCoupon) {
		this.remindCoupon = remindCoupon;
	}
	public Integer getLastBetMember() {
		return lastBetMember;
	}
	public void setLastBetMember(Integer lastBetMember) {
		this.lastBetMember = lastBetMember;
	}
	public List<ActivityForJulyHistory> getPkHistoryList() {
		return pkHistoryList;
	}
	public void setPkHistoryList(List<ActivityForJulyHistory> pkHistoryList) {
		this.pkHistoryList = pkHistoryList;
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
	public boolean isCanReceive() {
		return canReceive;
	}
	public void setCanReceive(boolean canReceive) {
		this.canReceive = canReceive;
	}
	
	
	
	
}
