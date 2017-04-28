package com.yourong.core.ic.model;

import java.io.Serializable;
import java.util.Date;

public class DebtPledge implements Serializable {
	/**
	 * 质押物
	 */
	private static final long serialVersionUID = 2790692916522706885L;

	/****/
	private Long id;

	/** 债权id **/
	private Long debtId;

	/** 质押物类型 **/
	private String pledgeType;

	/** 质押物详情 **/
	private String pledgeDetails;

	/** 质押物认证明细 **/
	private String pledgeVerify;

	/** 备注 **/
	private String remarks;

	/****/
	private Date createTime;

	/****/
	private Date updateTime;
	
	/**市场估值**/
	
	private Double pledgeValuation;

	/** 删除标记 **/
	private Integer delFlag;

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

	public String getPledgeType() {
		return pledgeType;
	}

	public void setPledgeType(String pledgeType) {
		this.pledgeType = pledgeType == null ? null : pledgeType.trim();
	}

	public String getPledgeDetails() {
		return pledgeDetails;
	}

	public void setPledgeDetails(String pledgeDetails) {
		this.pledgeDetails = pledgeDetails == null ? null : pledgeDetails
				.trim();
	}

	public String getPledgeVerify() {
		return pledgeVerify;
	}

	public void setPledgeVerify(String pledgeVerify) {
		this.pledgeVerify = pledgeVerify == null ? null : pledgeVerify.trim();
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
		builder.append("DebtPledge [id=");
		builder.append(id);
		builder.append(", debtId=");
		builder.append(debtId);
		builder.append(", pledgeType=");
		builder.append(pledgeType);
		builder.append(", pledgeDetails=");
		builder.append(pledgeDetails);
		builder.append(", pledgeVerify=");
		builder.append(pledgeVerify);
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
		result = prime * result
				+ ((createTime == null) ? 0 : createTime.hashCode());
		result = prime * result + ((debtId == null) ? 0 : debtId.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((pledgeDetails == null) ? 0 : pledgeDetails.hashCode());
		result = prime * result
				+ ((pledgeType == null) ? 0 : pledgeType.hashCode());
		result = prime * result
				+ ((pledgeVerify == null) ? 0 : pledgeVerify.hashCode());
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
		DebtPledge other = (DebtPledge) obj;
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
		if (pledgeDetails == null) {
			if (other.pledgeDetails != null)
				return false;
		} else if (!pledgeDetails.equals(other.pledgeDetails))
			return false;
		if (pledgeType == null) {
			if (other.pledgeType != null)
				return false;
		} else if (!pledgeType.equals(other.pledgeType))
			return false;
		if (pledgeVerify == null) {
			if (other.pledgeVerify != null)
				return false;
		} else if (!pledgeVerify.equals(other.pledgeVerify))
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

	public Double getPledgeValuation() {
		return pledgeValuation;
	}

	public void setPledgeValuation(Double pledgeValuation) {
		this.pledgeValuation = pledgeValuation;
	}
}