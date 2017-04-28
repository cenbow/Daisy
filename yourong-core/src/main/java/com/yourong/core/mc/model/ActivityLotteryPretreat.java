package com.yourong.core.mc.model;

import java.util.Date;

public class ActivityLotteryPretreat {
	/** 编号 **/
	private Long id;

	/** 活动编号 **/
	private Long activityId;

	/** 手机号 **/
	private Long mobile;

	/** 奖品类型:1人气值2收益券3现金券4礼品5其他 **/
	private Integer rewardType;

	/** 奖品值 **/
	private Integer rewardValue;

	/** 奖品描述 **/
	private String rewardInfo;

	/** 同一组奖品里的排序序列，source_id相同视为一组 **/
	private Integer rewardSort;

	/** 优惠券模板ID **/
	private Long couponTemplateId;

	/** 是否认领:0 未认领, 1 已认领 **/
	private Integer claimFlag;

	/** 是否已经领取:0 未领取; 1 已领取 **/
	private Integer receiveFlag;

	/** 活动中参与的角色 **/
	private String activityRole;

	/** 来源业务标识 **/
	private Long sourceId;

	/** 源标识对应的会员ID **/
	private Long sourceHolder;

	/** 创建时间 **/
	private Date createTime;

	/** 更新时间 **/
	private Date updateTime;

	/** 备注 **/
	private String remark;

	/** 删除标记 **/
	private Integer delFlag;

	/** 唯一码 **/
	private Long uniqueCode;

	/** 认领时间 */
	private Date claimTime;

	/** 是否会员 */
	private Integer isMember;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getActivityId() {
		return activityId;
	}

	public void setActivityId(Long activityId) {
		this.activityId = activityId;
	}

	public Long getMobile() {
		return mobile;
	}

	public void setMobile(Long mobile) {
		this.mobile = mobile;
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

	public String getRewardInfo() {
		return rewardInfo;
	}

	public void setRewardInfo(String rewardInfo) {
		this.rewardInfo = rewardInfo == null ? null : rewardInfo.trim();
	}

	public Integer getRewardSort() {
		return rewardSort;
	}

	public void setRewardSort(Integer rewardSort) {
		this.rewardSort = rewardSort;
	}

	public Long getCouponTemplateId() {
		return couponTemplateId;
	}

	public void setCouponTemplateId(Long couponTemplateId) {
		this.couponTemplateId = couponTemplateId;
	}

	public Integer getClaimFlag() {
		return claimFlag;
	}

	public void setClaimFlag(Integer claimFlag) {
		this.claimFlag = claimFlag;
	}

	public Integer getReceiveFlag() {
		return receiveFlag;
	}

	public void setReceiveFlag(Integer receiveFlag) {
		this.receiveFlag = receiveFlag;
	}

	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark == null ? null : remark.trim();
	}

	public Integer getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}

	public Long getUniqueCode() {
		return uniqueCode;
	}

	public void setUniqueCode(Long uniqueCode) {
		this.uniqueCode = uniqueCode;
	}

	public Date getClaimTime() {
		return claimTime;
	}

	public void setClaimTime(Date claimTime) {
		this.claimTime = claimTime;
	}

	public Integer getIsMember() {
		return isMember;
	}

	public void setIsMember(Integer isMember) {
		this.isMember = isMember;
	}

	public Long getSourceHolder() {
		return sourceHolder;
	}

	public void setSourceHolder(Long sourceHolder) {
		this.sourceHolder = sourceHolder;
	}

	public String getActivityRole() {
		return activityRole;
	}

	public void setActivityRole(String activityRole) {
		this.activityRole = activityRole;
	}
}