package com.yourong.core.mc.model;

import java.util.Date;

public class ActivityLottery {
    /**主键**/
    private Long id;

    /**活动ID**/
    private Long activityId;

    /**会员ID**/
    private Long memberId;

    /**总计获得兑换次数**/
    private Integer totalCount;

    /**实际剩余可兑换次数**/
    private Integer realCount;

    /**备注**/
    private String remark;

    /**创建时间**/
    private Date createTime;

    /**更新时间**/
    private Date updateTime;

    /**周期约束**/
    private String cycleConstraint;
    
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

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getRealCount() {
        return realCount;
    }

    public void setRealCount(Integer realCount) {
        this.realCount = realCount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
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

	public String getCycleConstraint() {
		return cycleConstraint;
	}

	public void setCycleConstraint(String cycleConstraint) {
		this.cycleConstraint = cycleConstraint;
	}
}