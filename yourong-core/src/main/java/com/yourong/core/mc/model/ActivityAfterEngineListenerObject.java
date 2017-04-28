package com.yourong.core.mc.model;

import com.yourong.common.domain.AbstractBaseObject;

public class ActivityAfterEngineListenerObject extends AbstractBaseObject {

	public ActivityAfterEngineListenerObject(Long memberId, Long activityId, String activityName) {
		this.memberId = memberId;
		this.activityId = activityId;
		this.activityName = activityName;
	}

	private Long memberId;

	private Long activityId;

	private String activityName;

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public Long getActivityId() {
		return activityId;
	}

	public void setActivityId(Long activityId) {
		this.activityId = activityId;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

}
