package com.yourong.core.bsc.model;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.util.DateUtils;

public class PaymentPlatform extends AbstractBaseObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7672180270444497383L;

	/**维护号**/
    private Long id;

    /**支付平台: 1,新浪**/
    private Integer platformType;

    /**银行卡主键**/
    private Long bankId;

    /**限制类型: 1,网银; 2,快捷**/
    private Integer typeLimit;

    /**单笔限额**/
    private BigDecimal singleLimit;

    /**每日限额**/
    private BigDecimal dailyLimit;

    /**最低支付额度**/
    private BigDecimal minLimit;

    /**服务状态: 0,不可用; 1,可用;**/
    private Integer serviceStatus;

    /**维护开始时间**/
    private Date startTime;

    /**维护结束时间**/
    private Date endTime;
    
    /**维护开始时间**/
    private String startTimeStr;

    /**维护结束时间**/
    private String endTimeStr;

    /**备注**/
    private String remark;

    /**删除标记**/
    private Integer delFlag;

    /**创建时间**/
    private Date createTime;

    /**更新时间**/
    private Date updateTime;

    /**维护公告**/
    private String maintenanceContent;

    /**银行简称**/
    private String simpleName;
    
    /**银行编码**/
    private String code;
    
    private Long maintenanceId;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPlatformType() {
        return platformType;
    }

    public void setPlatformType(Integer platformType) {
        this.platformType = platformType;
    }

    public Long getBankId() {
        return bankId;
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }

    public Integer getTypeLimit() {
        return typeLimit;
    }

    public void setTypeLimit(Integer typeLimit) {
        this.typeLimit = typeLimit;
    }

    public BigDecimal getSingleLimit() {
        return singleLimit;
    }

    public void setSingleLimit(BigDecimal singleLimit) {
        this.singleLimit = singleLimit;
    }

    public BigDecimal getDailyLimit() {
        return dailyLimit;
    }

    public void setDailyLimit(BigDecimal dailyLimit) {
        this.dailyLimit = dailyLimit;
    }

    public BigDecimal getMinLimit() {
        return minLimit;
    }

    public void setMinLimit(BigDecimal minLimit) {
        this.minLimit = minLimit;
    }

    public Integer getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(Integer serviceStatus) {
        this.serviceStatus = serviceStatus;
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

    public String getMaintenanceContent() {
        return maintenanceContent;
    }

    public void setMaintenanceContent(String maintenanceContent) {
        this.maintenanceContent = maintenanceContent == null ? null : maintenanceContent.trim();
    }

	public String getSimpleName() {
		return simpleName;
	}

	public void setSimpleName(String simpleName) {
		this.simpleName = simpleName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getStartTimeStr() {
		if(this.startTime != null) {
			return DateUtils.getLongStrFromDate(startTime);
		}
		return startTimeStr;
	}

	public void setStartTimeStr(String startTimeStr) {
		this.startTimeStr = startTimeStr;
	}

	public String getEndTimeStr() {
		if(this.endTime != null) {
			return DateUtils.getLongStrFromDate(endTime);
		}
		return endTimeStr;
	}

	public void setEndTimeStr(String endTimeStr) {
		this.endTimeStr = endTimeStr;
	}

	public Long getMaintenanceId() {
		return maintenanceId;
	}

	public void setMaintenanceId(Long maintenanceId) {
		this.maintenanceId = maintenanceId;
	}
}