package com.yourong.core.mc.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Activity implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 306312214390115336L;

	/****/
    private Long id;

    /**活动名称**/
    private String activityName;

    /**开始时间**/
    private Date startTime;

    /**结束时间**/
    private Date endTime;

    /**活动原因**/
    private String releaseReason;

    /**活动描述**/
    private String activityDesc;

    /**广告描述**/
    private String adDesc;

    /**总预算**/
    private BigDecimal totalBudget;

    /**活动状态**/
    private Integer activityStatus;

    /**审核状态**/
    private Integer auditStatus;

    /**发放类型**/
    private Integer grantType;

    /**发放数量**/
    private Integer grantNumber;

    /**发放用户数量**/
    private Integer userNumber;

    /**是否立即启用**/
    private Integer isRelease;

    /**活动创建人**/
    private Long createId;

    /**活动审核人**/
    private Long auditId;

    /**审核时间**/
    private Date auditTime;

    /**审核信息**/
    private String auditMessage;

    /**创建时间**/
    private Date createTime;

    /**更新时间**/
    private Date updateTime;
    
    /**活动参与条件**/
    private String obtainConditionsJson;
    
    /**活动类型**/
    private int type;
    
    private Long projectId;
    
    

    public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName == null ? null : activityName.trim();
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

    public String getReleaseReason() {
        return releaseReason;
    }

    public void setReleaseReason(String releaseReason) {
        this.releaseReason = releaseReason == null ? null : releaseReason.trim();
    }

    public String getActivityDesc() {
        return activityDesc;
    }

    public void setActivityDesc(String activityDesc) {
        this.activityDesc = activityDesc == null ? null : activityDesc.trim();
    }

    public String getAdDesc() {
        return adDesc;
    }

    public void setAdDesc(String adDesc) {
        this.adDesc = adDesc == null ? null : adDesc.trim();
    }

    public BigDecimal getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(BigDecimal totalBudget) {
        this.totalBudget = totalBudget;
    }

    public Integer getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(Integer activityStatus) {
        this.activityStatus = activityStatus;
    }

    public Integer getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(Integer auditStatus) {
        this.auditStatus = auditStatus;
    }

    public Integer getGrantType() {
        return grantType;
    }

    public void setGrantType(Integer grantType) {
        this.grantType = grantType;
    }

    public Integer getGrantNumber() {
        return grantNumber;
    }

    public void setGrantNumber(Integer grantNumber) {
        this.grantNumber = grantNumber;
    }

    public Integer getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(Integer userNumber) {
        this.userNumber = userNumber;
    }

    public Integer getIsRelease() {
        return isRelease;
    }

    public void setIsRelease(Integer isRelease) {
        this.isRelease = isRelease;
    }

    public Long getCreateId() {
        return createId;
    }

    public void setCreateId(Long createId) {
        this.createId = createId;
    }

    public Long getAuditId() {
        return auditId;
    }

    public void setAuditId(Long auditId) {
        this.auditId = auditId;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    public String getAuditMessage() {
        return auditMessage;
    }

    public void setAuditMessage(String auditMessage) {
        this.auditMessage = auditMessage == null ? null : auditMessage.trim();
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
    
	public String getObtainConditionsJson() {
		return obtainConditionsJson;
	}

	public void setObtainConditionsJson(String obtainConditionsJson) {
		this.obtainConditionsJson = obtainConditionsJson;
	}
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Activity [id=");
		builder.append(id);
		builder.append(", activityName=");
		builder.append(activityName);
		builder.append(", startTime=");
		builder.append(startTime);
		builder.append(", endTime=");
		builder.append(endTime);
		builder.append(", releaseReason=");
		builder.append(releaseReason);
		builder.append(", activityDesc=");
		builder.append(activityDesc);
		builder.append(", adDesc=");
		builder.append(adDesc);
		builder.append(", totalBudget=");
		builder.append(totalBudget);
		builder.append(", activityStatus=");
		builder.append(activityStatus);
		builder.append(", auditStatus=");
		builder.append(auditStatus);
		builder.append(", grantType=");
		builder.append(grantType);
		builder.append(", grantNumber=");
		builder.append(grantNumber);
		builder.append(", userNumber=");
		builder.append(userNumber);
		builder.append(", isRelease=");
		builder.append(isRelease);
		builder.append(", createId=");
		builder.append(createId);
		builder.append(", auditId=");
		builder.append(auditId);
		builder.append(", auditTime=");
		builder.append(auditTime);
		builder.append(", auditMessage=");
		builder.append(auditMessage);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", updateTime=");
		builder.append(updateTime);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((activityDesc == null) ? 0 : activityDesc.hashCode());
		result = prime * result
				+ ((activityName == null) ? 0 : activityName.hashCode());
		result = prime * result
				+ ((activityStatus == null) ? 0 : activityStatus.hashCode());
		result = prime * result + ((adDesc == null) ? 0 : adDesc.hashCode());
		result = prime * result + ((auditId == null) ? 0 : auditId.hashCode());
		result = prime * result
				+ ((auditMessage == null) ? 0 : auditMessage.hashCode());
		result = prime * result
				+ ((auditStatus == null) ? 0 : auditStatus.hashCode());
		result = prime * result
				+ ((auditTime == null) ? 0 : auditTime.hashCode());
		result = prime * result
				+ ((createId == null) ? 0 : createId.hashCode());
		result = prime * result
				+ ((createTime == null) ? 0 : createTime.hashCode());
		result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
		result = prime * result
				+ ((grantNumber == null) ? 0 : grantNumber.hashCode());
		result = prime * result
				+ ((grantType == null) ? 0 : grantType.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((isRelease == null) ? 0 : isRelease.hashCode());
		result = prime * result
				+ ((releaseReason == null) ? 0 : releaseReason.hashCode());
		result = prime * result
				+ ((startTime == null) ? 0 : startTime.hashCode());
		result = prime * result
				+ ((totalBudget == null) ? 0 : totalBudget.hashCode());
		result = prime * result
				+ ((updateTime == null) ? 0 : updateTime.hashCode());
		result = prime * result
				+ ((userNumber == null) ? 0 : userNumber.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Activity other = (Activity) obj;
		if (activityDesc == null) {
			if (other.activityDesc != null)
				return false;
		} else if (!activityDesc.equals(other.activityDesc))
			return false;
		if (activityName == null) {
			if (other.activityName != null)
				return false;
		} else if (!activityName.equals(other.activityName))
			return false;
		if (activityStatus == null) {
			if (other.activityStatus != null)
				return false;
		} else if (!activityStatus.equals(other.activityStatus))
			return false;
		if (adDesc == null) {
			if (other.adDesc != null)
				return false;
		} else if (!adDesc.equals(other.adDesc))
			return false;
		if (auditId == null) {
			if (other.auditId != null)
				return false;
		} else if (!auditId.equals(other.auditId))
			return false;
		if (auditMessage == null) {
			if (other.auditMessage != null)
				return false;
		} else if (!auditMessage.equals(other.auditMessage))
			return false;
		if (auditStatus == null) {
			if (other.auditStatus != null)
				return false;
		} else if (!auditStatus.equals(other.auditStatus))
			return false;
		if (auditTime == null) {
			if (other.auditTime != null)
				return false;
		} else if (!auditTime.equals(other.auditTime))
			return false;
		if (createId == null) {
			if (other.createId != null)
				return false;
		} else if (!createId.equals(other.createId))
			return false;
		if (createTime == null) {
			if (other.createTime != null)
				return false;
		} else if (!createTime.equals(other.createTime))
			return false;
		if (endTime == null) {
			if (other.endTime != null)
				return false;
		} else if (!endTime.equals(other.endTime))
			return false;
		if (grantNumber == null) {
			if (other.grantNumber != null)
				return false;
		} else if (!grantNumber.equals(other.grantNumber))
			return false;
		if (grantType == null) {
			if (other.grantType != null)
				return false;
		} else if (!grantType.equals(other.grantType))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isRelease == null) {
			if (other.isRelease != null)
				return false;
		} else if (!isRelease.equals(other.isRelease))
			return false;
		if (releaseReason == null) {
			if (other.releaseReason != null)
				return false;
		} else if (!releaseReason.equals(other.releaseReason))
			return false;
		if (startTime == null) {
			if (other.startTime != null)
				return false;
		} else if (!startTime.equals(other.startTime))
			return false;
		if (totalBudget == null) {
			if (other.totalBudget != null)
				return false;
		} else if (!totalBudget.equals(other.totalBudget))
			return false;
		if (updateTime == null) {
			if (other.updateTime != null)
				return false;
		} else if (!updateTime.equals(other.updateTime))
			return false;
		if (userNumber == null) {
			if (other.userNumber != null)
				return false;
		} else if (!userNumber.equals(other.userNumber))
			return false;
		return true;
	}
}