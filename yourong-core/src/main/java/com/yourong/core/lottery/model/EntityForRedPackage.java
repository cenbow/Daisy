package com.yourong.core.lottery.model;

import java.math.BigDecimal;

import com.yourong.common.domain.AbstractBaseObject;

@SuppressWarnings("serial")
public class EntityForRedPackage extends AbstractBaseObject {

	/** 来源ID **/
	private Long sourceId;

	/** 红包总点数 **/
	private BigDecimal totalAmount;

	/** 红包总个数 **/
	private Integer number;
	
	/** 红包最小点数 **/
	private Integer minPopularityUnit;
	
	/** 过期分钟数 **/
	private Integer timeOutSeconds;

	/** 活动ID **/
	private Long activityId;

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Integer getMinPopularityUnit() {
		return minPopularityUnit;
	}

	public void setMinPopularityUnit(Integer minPopularityUnit) {
		this.minPopularityUnit = minPopularityUnit;
	}

	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

	public Integer getTimeOutSeconds() {
		return timeOutSeconds;
	}

	public void setTimeOutSeconds(Integer timeOutSeconds) {
		this.timeOutSeconds = timeOutSeconds;
	}

	public Long getActivityId() {
		return activityId;
	}

	public void setActivityId(Long activityId) {
		this.activityId = activityId;
	}

}
