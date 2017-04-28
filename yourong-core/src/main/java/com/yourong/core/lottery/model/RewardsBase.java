package com.yourong.core.lottery.model;

import com.yourong.common.domain.AbstractBaseObject;

public class RewardsBase extends AbstractBaseObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8395893519608067917L;

	/**
	 * 奖品code
	 */
	private String rewardCode;

	/**
	 * 奖品名称
	 */
	private String rewardName;

	/**
	 * 奖品类型
	 */
	private Integer rewardType;

	/**
	 * 奖品数值
	 */
	private Integer rewardValue;

	/**
	 * 优惠券模板
	 */
	private Long templateId;

	/**
	 * 领取时间要求
	 */
	private String requireTime;

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

	public Integer getRewardValue() {
		return rewardValue;
	}

	public void setRewardValue(Integer rewardValue) {
		this.rewardValue = rewardValue;
	}

	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	public String getRequireTime() {
		return requireTime;
	}

	public void setRequireTime(String requireTime) {
		this.requireTime = requireTime;
	}

}
