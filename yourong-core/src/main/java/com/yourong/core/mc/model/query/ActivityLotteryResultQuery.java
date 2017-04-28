package com.yourong.core.mc.model.query;

import com.yourong.common.domain.BaseQueryParam;

public class ActivityLotteryResultQuery extends BaseQueryParam {

	 /**活动ID**/
    private Long activityId;

    /**会员ID**/
    private Long memberId;
    

    /**奖品类型:1现金券2收益券3礼品4其他**/
    private Integer rewardType;


    /**状态（0未领取、1已领取）**/
    private Integer status;

    /**备注**/
    private String remark;

	/**
	 * @return the activityId
	 */
	public Long getActivityId() {
		return activityId;
	}

	/**
	 * @param activityId the activityId to set
	 */
	public void setActivityId(Long activityId) {
		this.activityId = activityId;
	}

	/**
	 * @return the memberId
	 */
	public Long getMemberId() {
		return memberId;
	}

	/**
	 * @param memberId the memberId to set
	 */
	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	/**
	 * @return the rewardType
	 */
	public Integer getRewardType() {
		return rewardType;
	}

	/**
	 * @param rewardType the rewardType to set
	 */
	public void setRewardType(Integer rewardType) {
		this.rewardType = rewardType;
	}

	/**
	 * @return the status
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * @param remark the remark to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}
 
    
}
