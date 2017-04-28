package com.yourong.core.mc.model.biz;

import java.util.Date;
import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * 周年庆
 * 
 * @author wangyanji
 */
public class ActivityForAnniversary extends AbstractBaseObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2062381053365771155L;

	private Long activityId;
	/**
	 * 简短文案
	 */
	private String document;
	
	/**
	 * 按钮文案
	 */
	private String btnStr;
	
	/**
	 * 活动Index
	 */
	private Integer activityIndex;
	
	/**
	 * 开始时间
	 */
	private Date startTime;
	
	/**
	 * 结束时间
	 */
	private Date endTime;
	
	/**
	 * 是否已经领取奖励
	 */
	private boolean isParticipate;
	
	/**
	 * 中奖结果
	 */
	private String rewardCode;
	
	/**
	 * 中奖结果描述
	 */
	private String rewardName;
	
	/**
	 * 25宫格幸运格坐标
	 */
	private List<Integer> gridIndexList;
	
	/**
	 * 活动状态
	 */
	private int status;

	/**
	 * 加密链接
	 */
	private String encryptUrl;
	
	/**
	 * 人气值
	 */
	private Integer popularityVaule;
	
	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public String getBtnStr() {
		return btnStr;
	}

	public void setBtnStr(String btnStr) {
		this.btnStr = btnStr;
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

	public boolean isParticipate() {
		return isParticipate;
	}

	public void setParticipate(boolean isParticipate) {
		this.isParticipate = isParticipate;
	}

	public String getRewardCode() {
		return rewardCode;
	}

	public void setRewardCode(String rewardCode) {
		this.rewardCode = rewardCode;
	}

	public List<Integer> getGridIndexList() {
		return gridIndexList;
	}

	public void setGridIndexList(List<Integer> gridIndexList) {
		this.gridIndexList = gridIndexList;
	}

	public String getEncryptUrl() {
		return encryptUrl;
	}

	public void setEncryptUrl(String encryptUrl) {
		this.encryptUrl = encryptUrl;
	}

	public Long getActivityId() {
		return activityId;
	}

	public void setActivityId(Long activityId) {
		this.activityId = activityId;
	}

	public String getRewardName() {
		return rewardName;
	}

	public void setRewardName(String rewardName) {
		this.rewardName = rewardName;
	}

	public Integer getPopularityVaule() {
		return popularityVaule;
	}

	public void setPopularityVaule(Integer popularityVaule) {
		this.popularityVaule = popularityVaule;
	}

	public Integer getActivityIndex() {
		return activityIndex;
	}

	public void setActivityIndex(Integer activityIndex) {
		this.activityIndex = activityIndex;
	}
	
	
}
