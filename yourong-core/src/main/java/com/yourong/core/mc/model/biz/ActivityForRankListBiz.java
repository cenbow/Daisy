package com.yourong.core.mc.model.biz;

import java.io.Serializable;
import java.util.Date;
import java.util.List;



public class ActivityForRankListBiz implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2510202916694873640L;

	/**
	 * 活动ID
	 */
	private Long activityId;
	
	/**
	 * 用户ID
	 */
	private Long memberId;
	/**
	 * 用户头像
	 */
	private String avatar;
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
	/**
	 * 有效开始日期
	 */
	private Date startDay;
	/**
	 * 有效结束日期
	 */
	private Date endDay;
	/**
	 * 有效开始时间
	 */
	private String startTime;
	/**
	 * 有效开始时间
	 */
	private String endTime;
	/**
	 * 本轮用户名
	 */
	private String thisUsername;
	/**
	 * 上轮用户名
	 */
	private String lastUsername;
	/**
	 * 本轮用户id
	 */
	private Long thisUserId;
	/**
	 * 上轮用户id
	 */
	private Long lastUserId;
	/**
	 * 上轮总投资
	 */
	private String lastTotalInvest;
	/**
	 * 本轮总投资
	 */
	private String thisTotalInvest;
	/**
	 * 本轮开始
	 */
	private String lastTimeStart;
	/**
	 * 上轮结束
	 */
	private String lastTimeEnd;
	/**
	 * 上轮开始
	 */
	private String beforeLastStartTime;
	/**
	 * 本轮结束
	 */
	private String thisTimeEnd;
	/**
	 * redis失效时间
	 */
	private Date redisEndTime;
	/**
	 * 当前属于第几轮
	 */
	private int weekIndex;
	/**
	 * 本轮列表
	 */
	private List<ActivityForRankListBiz> thisList;
	/**
	 * 上轮列表
	 */
	private List<ActivityForRankListBiz> lastList;
	
	/**
	 * 上轮列表
	 */
	private List<ActivityForRankListBiz> topperList;
	
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
	public Date getStartDay() {
		return startDay;
	}
	public void setStartDay(Date startDay) {
		this.startDay = startDay;
	}
	public Date getEndDay() {
		return endDay;
	}
	public void setEndDay(Date endDay) {
		this.endDay = endDay;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public Long getThisUserId() {
		return thisUserId;
	}
	public void setThisUserId(Long thisUserId) {
		this.thisUserId = thisUserId;
	}
	public Long getLastUserId() {
		return lastUserId;
	}
	public String getLastTimeStart() {
		return lastTimeStart;
	}
	public void setLastTimeStart(String lastTimeStart) {
		this.lastTimeStart = lastTimeStart;
	}
	public String getLastTimeEnd() {
		return lastTimeEnd;
	}
	public void setLastTimeEnd(String lastTimeEnd) {
		this.lastTimeEnd = lastTimeEnd;
	}
	public String getBeforeLastStartTime() {
		return beforeLastStartTime;
	}
	public void setBeforeLastStartTime(String beforeLastStartTime) {
		this.beforeLastStartTime = beforeLastStartTime;
	}
	public String getThisTimeEnd() {
		return thisTimeEnd;
	}
	public void setThisTimeEnd(String thisTimeEnd) {
		this.thisTimeEnd = thisTimeEnd;
	}
	public void setLastUserId(Long lastUserId) {
		this.lastUserId = lastUserId;
	}
	public Date getRedisEndTime() {
		return redisEndTime;
	}
	public void setRedisEndTime(Date redisEndTime) {
		this.redisEndTime = redisEndTime;
	}
	public int getWeekIndex() {
		return weekIndex;
	}
	public void setWeekIndex(int weekIndex) {
		this.weekIndex = weekIndex;
	}
	public List<ActivityForRankListBiz> getThisList() {
		return thisList;
	}
	public void setThisList(List<ActivityForRankListBiz> thisList) {
		this.thisList = thisList;
	}
	public List<ActivityForRankListBiz> getLastList() {
		return lastList;
	}
	public void setLastList(List<ActivityForRankListBiz> lastList) {
		this.lastList = lastList;
	}
	public List<ActivityForRankListBiz> getTopperList() {
		return topperList;
	}
	public void setTopperList(List<ActivityForRankListBiz> topperList) {
		this.topperList = topperList;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
}