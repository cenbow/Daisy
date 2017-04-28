package com.yourong.core.ic.model;

import java.io.Serializable;
import java.util.Date;

public class DebtCollateral implements Serializable {
    /**
	 * 抵押物表
	 */
	private static final long serialVersionUID = 3947579849349939292L;

	/****/
    private Long id;

    /**债权id**/
    private Long debtId;

    /**抵押物类型**/
    private String collateralType;

    /**抵押物详情**/
    private String collateralDetails;

    /**抵押物认证明细**/
    private String collateralVerify;

    /**备注**/
    private String remarks;

    /****/
    private Date createTime;

    /****/
    private Date updateTime;
    
    /**市场估值**/
    private Double collateralValuation;
    
    /**删除标记**/
    private Integer delFlag; 
    
    /**
     * 担保类型 
     */
    private String debtType;

    /**
     * 项目id
     */
    private Long projectId;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDebtId() {
        return debtId;
    }

    public void setDebtId(Long debtId) {
        this.debtId = debtId;
    }

    public String getCollateralType() {
        return collateralType;
    }

    public void setCollateralType(String collateralType) {
        this.collateralType = collateralType == null ? null : collateralType.trim();
    }

    public String getCollateralDetails() {
        return collateralDetails;
    }

    public void setCollateralDetails(String collateralDetails) {
        this.collateralDetails = collateralDetails == null ? null : collateralDetails.trim();
    }

    public String getCollateralVerify() {
        return collateralVerify;
    }

    public void setCollateralVerify(String collateralVerify) {
        this.collateralVerify = collateralVerify == null ? null : collateralVerify.trim();
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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DebtCollateral [id=");
		builder.append(id);
		builder.append(", debtId=");
		builder.append(debtId);
		builder.append(", collateralType=");
		builder.append(collateralType);
		builder.append(", collateralDetails=");
		builder.append(collateralDetails);
		builder.append(", collateralVerify=");
		builder.append(collateralVerify);
		builder.append(", remarks=");
		builder.append(remarks);
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
		result = prime
				* result
				+ ((collateralDetails == null) ? 0 : collateralDetails
						.hashCode());
		result = prime * result
				+ ((collateralType == null) ? 0 : collateralType.hashCode());
		result = prime
				* result
				+ ((collateralVerify == null) ? 0 : collateralVerify.hashCode());
		result = prime * result
				+ ((createTime == null) ? 0 : createTime.hashCode());
		result = prime * result + ((debtId == null) ? 0 : debtId.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((remarks == null) ? 0 : remarks.hashCode());
		result = prime * result
				+ ((updateTime == null) ? 0 : updateTime.hashCode());
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
		DebtCollateral other = (DebtCollateral) obj;
		if (collateralDetails == null) {
			if (other.collateralDetails != null)
				return false;
		} else if (!collateralDetails.equals(other.collateralDetails))
			return false;
		if (collateralType == null) {
			if (other.collateralType != null)
				return false;
		} else if (!collateralType.equals(other.collateralType))
			return false;
		if (collateralVerify == null) {
			if (other.collateralVerify != null)
				return false;
		} else if (!collateralVerify.equals(other.collateralVerify))
			return false;
		if (createTime == null) {
			if (other.createTime != null)
				return false;
		} else if (!createTime.equals(other.createTime))
			return false;
		if (debtId == null) {
			if (other.debtId != null)
				return false;
		} else if (!debtId.equals(other.debtId))
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
		if (updateTime == null) {
			if (other.updateTime != null)
				return false;
		} else if (!updateTime.equals(other.updateTime))
			return false;
		return true;
	}

	public Integer getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}

	public Double getCollateralValuation() {
		return collateralValuation;	
	}

	public void setCollateralValuation(Double collateralValuation) {
		this.collateralValuation = collateralValuation;
	}

	public String getDebtType() {
		return debtType;
	}

	public void setDebtType(String debtType) {
		this.debtType = debtType;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
}