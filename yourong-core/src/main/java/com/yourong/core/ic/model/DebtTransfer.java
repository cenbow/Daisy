package com.yourong.core.ic.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class DebtTransfer implements Serializable {
    /**
	 * 债权转让表
	 */
	private static final long serialVersionUID = 2310124699169986306L;

	/****/
    private Long id;

    /**债权id**/
    private Long debtId;

    /**被转让人id，关联用户id**/
    private Long ownerId;

    /**转让金额**/
    private BigDecimal amount;

    /**转让债权开始日期**/
    private Date startDate;

    /**转让债权结束日期**/
    private Date endDate;

    /**转让时间**/
    private Date transferTime;

    /**年化收益**/
    private BigDecimal annualizedRate;

    /****/
    private Date createTime;
    
    /**删除标记**/
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

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getTransferTime() {
        return transferTime;
    }

    public void setTransferTime(Date transferTime) {
        this.transferTime = transferTime;
    }

    public BigDecimal getAnnualizedRate() {
        return annualizedRate;
    }

    public void setAnnualizedRate(BigDecimal annualizedRate) {
        this.annualizedRate = annualizedRate;
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
		builder.append("DebtTransfer [id=");
		builder.append(id);
		builder.append(", debtId=");
		builder.append(debtId);
		builder.append(", ownerId=");
		builder.append(ownerId);
		builder.append(", amount=");
		builder.append(amount);
		builder.append(", startDate=");
		builder.append(startDate);
		builder.append(", endDate=");
		builder.append(endDate);
		builder.append(", transferTime=");
		builder.append(transferTime);
		builder.append(", annualizedRate=");
		builder.append(annualizedRate);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result
				+ ((annualizedRate == null) ? 0 : annualizedRate.hashCode());
		result = prime * result
				+ ((createTime == null) ? 0 : createTime.hashCode());
		result = prime * result + ((debtId == null) ? 0 : debtId.hashCode());
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((ownerId == null) ? 0 : ownerId.hashCode());
		result = prime * result
				+ ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result
				+ ((transferTime == null) ? 0 : transferTime.hashCode());
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
		DebtTransfer other = (DebtTransfer) obj;
		if (amount == null) {
			if (other.amount != null)
				return false;
		} else if (!amount.equals(other.amount))
			return false;
		if (annualizedRate == null) {
			if (other.annualizedRate != null)
				return false;
		} else if (!annualizedRate.equals(other.annualizedRate))
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
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (ownerId == null) {
			if (other.ownerId != null)
				return false;
		} else if (!ownerId.equals(other.ownerId))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		if (transferTime == null) {
			if (other.transferTime != null)
				return false;
		} else if (!transferTime.equals(other.transferTime))
			return false;
		return true;
	}

	public Integer getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}
}