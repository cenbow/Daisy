package com.yourong.core.lottery.model;

import java.math.BigDecimal;
import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;

public class RewardsBodyForProbility extends AbstractBaseObject {

	public RewardsBodyForProbility() {

	}

	public RewardsBodyForProbility(String rewardCode, String rewardName, Integer rewardType) {
		this.rewardCode = rewardCode;
		this.rewardName = rewardName;
		this.rewardType = rewardType;
	};

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
	 * 概率
	 */
	private Float probability;

	/**
	 * 操作符
	 */
	private String operator;

	/**
	 * 优惠券模板
	 */
	private Long templateId;

	private List<RewardsBodyForProbility> rewardsList;

	private List<BigDecimal> probilityList;

	/**
	 * 分组编号
	 */
	private String rewardsGroup;

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

	public Float getProbability() {
		return probability;
	}

	public void setProbability(Float probability) {
		this.probability = probability;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	public List<RewardsBodyForProbility> getRewardsList() {
		return rewardsList;
	}

	public void setRewardsList(List<RewardsBodyForProbility> rewardsList) {
		this.rewardsList = rewardsList;
	}

	public List<BigDecimal> getProbilityList() {
		return probilityList;
	}

	public void setProbilityList(List<BigDecimal> probilityList) {
		this.probilityList = probilityList;
	}

	public String getRewardsGroup() {
		return rewardsGroup;
	}

	public void setRewardsGroup(String rewardsGroup) {
		this.rewardsGroup = rewardsGroup;
	}

}
