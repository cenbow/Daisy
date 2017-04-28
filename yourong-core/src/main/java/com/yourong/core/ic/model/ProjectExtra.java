package com.yourong.core.ic.model;

import java.math.BigDecimal;
import java.util.Date;

public class ProjectExtra {
    /**主键**/
    private Long id;

    /**项目id**/
    private Long projectId;

    /**特殊业务类型: 1-活动 2-项目加息**/
    private Integer extraType;
    
    /**活动标识(参考数据字典project_activity_sign)**/
    private Integer activitySign;

    /**外部金额（快投有奖时为奖金池,项目加息时为加息利率）**/
    private BigDecimal extraAmount;
    
    private String extraInformation;
    
    private String extraInformationTwo;
    
    /**创建时间**/
    private Date createTime;

    /**更新时间**/
    private Date updateTime;

    /**备注**/
    private String remark;

    /**删除标识**/
    private Integer delFlag;
    
    private Long activityId;

    public Long getActivityId() {
		return activityId;
	}

	public void setActivityId(Long activityId) {
		this.activityId = activityId;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Integer getActivitySign() {
        return activitySign;
    }

    public void setActivitySign(Integer activitySign) {
        this.activitySign = activitySign;
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

	public BigDecimal getExtraAmount() {
		return extraAmount;
	}

	public void setExtraAmount(BigDecimal extraAmount) {
		this.extraAmount = extraAmount;
	}

	public String getExtraInformation() {
		return extraInformation;
	}

	public void setExtraInformation(String extraInformation) {
		this.extraInformation = extraInformation;
	}

	public Integer getExtraType() {
		return extraType;
	}

	public void setExtraType(Integer extraType) {
		this.extraType = extraType;
	}

	public String getExtraInformationTwo() {
		return extraInformationTwo;
	}

	public void setExtraInformationTwo(String extraInformationTwo) {
		this.extraInformationTwo = extraInformationTwo;
	}
	
	
}