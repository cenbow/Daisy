package com.yourong.core.mc.model;

import java.io.Serializable;
import java.util.Date;

public class ActivityRule implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 8439409269377260592L;

	/****/
    private Long id;

    /**活动id**/
    private Long activityId;

    /**活动规则描述**/
    private String description;

    /**规则类型**/
    private String ruleType;

    /**规则参数（json格式）**/
    private String ruleParameter;

    /**规则版本**/
    private String version;

    /**备注**/
    private String remarks;

    /****/
    private Date createTime;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType == null ? null : ruleType.trim();
    }

    public String getRuleParameter() {
        return ruleParameter;
    }

    public void setRuleParameter(String ruleParameter) {
        this.ruleParameter = ruleParameter == null ? null : ruleParameter.trim();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version == null ? null : version.trim();
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks == null ? null : remarks.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ActivityRule [id=");
		builder.append(id);
		builder.append(", activityId=");
		builder.append(activityId);
		builder.append(", description=");
		builder.append(description);
		builder.append(", ruleType=");
		builder.append(ruleType);
		builder.append(", ruleParameter=");
		builder.append(ruleParameter);
		builder.append(", version=");
		builder.append(version);
		builder.append(", remarks=");
		builder.append(remarks);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((activityId == null) ? 0 : activityId.hashCode());
		result = prime * result
				+ ((createTime == null) ? 0 : createTime.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((remarks == null) ? 0 : remarks.hashCode());
		result = prime * result
				+ ((ruleParameter == null) ? 0 : ruleParameter.hashCode());
		result = prime * result
				+ ((ruleType == null) ? 0 : ruleType.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
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
		ActivityRule other = (ActivityRule) obj;
		if (activityId == null) {
			if (other.activityId != null)
				return false;
		} else if (!activityId.equals(other.activityId))
			return false;
		if (createTime == null) {
			if (other.createTime != null)
				return false;
		} else if (!createTime.equals(other.createTime))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (remarks == null) {
			if (other.remarks != null)
				return false;
		} else if (!remarks.equals(other.remarks))
			return false;
		if (ruleParameter == null) {
			if (other.ruleParameter != null)
				return false;
		} else if (!ruleParameter.equals(other.ruleParameter))
			return false;
		if (ruleType == null) {
			if (other.ruleType != null)
				return false;
		} else if (!ruleType.equals(other.ruleType))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}
}