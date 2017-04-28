package com.yourong.core.mc.model.biz;


public class ActivityLotteryBiz {
	
	/**
	 * 活动ID
	 */
	private Long activityId;
	
	/**
	 * 用户ID
	 */
	private Long memberId;
	
	/**
	 * 奖励CODE
	 */
	private String rewardCode;
	/**
	 * 奖励NAME
	 */
	private String rewardName;
	/**
	 * 奖励类型:1现金券2收益券3礼品4其他
	 */
	private Integer rewardType;
	/**
	 * 奖励标识ID
	 */
	private String rewardId;
	/**
	 * 人气值活现金券的数额
	 */
	private Integer rewardValue;
	/**
	 * 中奖概率
	 */
	private Double probability;
	/**
	 * 剩余抽奖次数
	 */
	private Integer realCount;
	/**
	 * 排名
	 */
	private Integer rankIndex;
	/**
	 * 中奖信息
	 */
	private String rewardInfo;
	/**
	 * 交易信息
	 */
	private String transInfo;
	/**
	 * 用户名
	 */
	private String username;
	/**
	 * 活动周期
	 */
	private String cycle;
	
	private String thisUsername;
	
	private String lastUsername;
	
	private String lastTotalInvest;
	
	private String thisTotalInvest;
	
	public String getRewardCode() {
		return rewardCode;
	}
	public void setRewardCode(String rewardCode) {
		this.rewardCode = rewardCode;
	}
	public String getRewardName() {
		return rewardName;
	}
	public void setRewardName(String rewardName) {
		this.rewardName = rewardName;
	}
	public Integer getRewardType() {
		return rewardType;
	}
	public void setRewardType(Integer rewardType) {
		this.rewardType = rewardType;
	}
	public String getRewardId() {
		return rewardId;
	}
	public void setRewardId(String rewardId) {
		this.rewardId = rewardId;
	}
	public Double getProbability() {
		return probability;
	}
	public void setProbability(Double probability) {
		this.probability = probability;
	}
	public Integer getRewardValue() {
		return rewardValue;
	}
	public void setRewardValue(Integer rewardValue) {
		this.rewardValue = rewardValue;
	}
	public Long getActivityId() {
		return activityId;
	}
	public void setActivityId(Long activityId) {
		this.activityId = activityId;
	}
	public Long getMemberId() {
		return memberId;
	}
	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}
	public Integer getRealCount() {
		return realCount;
	}
	public void setRealCount(Integer realCount) {
		this.realCount = realCount;
	}
	public Integer getRankIndex() {
		return rankIndex;
	}
	public void setRankIndex(Integer rankIndex) {
		this.rankIndex = rankIndex;
	}
	public String getRewardInfo() {
		return rewardInfo;
	}
	public void setRewardInfo(String rewardInfo) {
		this.rewardInfo = rewardInfo;
	}
	public String getTransInfo() {
		return transInfo;
	}
	public void setTransInfo(String transInfo) {
		this.transInfo = transInfo;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getCycle() {
		return cycle;
	}
	public void setCycle(String cycle) {
		this.cycle = cycle;
	}
	public String getThisUsername() {
		return thisUsername;
	}
	public void setThisUsername(String thisUsername) {
		this.thisUsername = thisUsername;
	}
	public String getLastUsername() {
		return lastUsername;
	}
	public void setLastUsername(String lastUsername) {
		this.lastUsername = lastUsername;
	}
	public String getLastTotalInvest() {
		return lastTotalInvest;
	}
	public void setLastTotalInvest(String lastTotalInvest) {
		this.lastTotalInvest = lastTotalInvest;
	}
	public String getThisTotalInvest() {
		return thisTotalInvest;
	}
	public void setThisTotalInvest(String thisTotalInvest) {
		this.thisTotalInvest = thisTotalInvest;
	}
	
}