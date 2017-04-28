package com.yourong.core.fin.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Balance implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2827208227226428074L;

	/****/
    private Long id;

    /**余额类型：1、第三方账户余额；2、红包余额；4、项目资金余额；5、免提现费额度；6、每天提现限定次数**/
    private Integer balanceType;

    /**总余额**/
    private BigDecimal balance;

    /**可用余额**/
    private BigDecimal availableBalance;
    
//    
//    /**收益余额**/
//    private BigDecimal earnings;
//    
//    
//    /**
//     * 昨日收益
//     */
//    private BigDecimal yestDayBonus;
//    /**
//     * 最近一月收益
//     */
//    private BigDecimal lastMonthBonus;
//    

//    public BigDecimal getYestDayBonus() {
//		return yestDayBonus;
//	}
//
//	public void setYestDayBonus(BigDecimal yestDayBonus) {
//		this.yestDayBonus = yestDayBonus;
//	}
//
//	public BigDecimal getLastMonthBonus() {
//		return lastMonthBonus;
//	}
//
//	public void setLastMonthBonus(BigDecimal lastMonthBonus) {
//		this.lastMonthBonus = lastMonthBonus;
//	}

	/**来源id(用户id、项目id)**/
    private Long sourceId;

    /****/
    private Date createTime;

    /****/
    private Date updateTime;
    
    
    

//    public BigDecimal getEarnings() {
//		return earnings;
//	}
//
//	public void setEarnings(BigDecimal earnings) {
//		this.earnings = earnings;
//	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(Integer balanceType) {
        this.balanceType = balanceType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(BigDecimal availableBalance) {
        this.availableBalance = availableBalance;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Balance [id=");
		builder.append(id);
		builder.append(", balanceType=");
		builder.append(balanceType);
		builder.append(", balance=");
		builder.append(balance);
		builder.append(", availableBalance=");
		builder.append(availableBalance);
		builder.append(", sourceId=");
		builder.append(sourceId);
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
				+ ((availableBalance == null) ? 0 : availableBalance.hashCode());
		result = prime * result + ((balance == null) ? 0 : balance.hashCode());
		result = prime * result
				+ ((balanceType == null) ? 0 : balanceType.hashCode());
		result = prime * result
				+ ((createTime == null) ? 0 : createTime.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((sourceId == null) ? 0 : sourceId.hashCode());
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
		Balance other = (Balance) obj;
		if (availableBalance == null) {
			if (other.availableBalance != null)
				return false;
		} else if (!availableBalance.equals(other.availableBalance))
			return false;
		if (balance == null) {
			if (other.balance != null)
				return false;
		} else if (!balance.equals(other.balance))
			return false;
		if (balanceType == null) {
			if (other.balanceType != null)
				return false;
		} else if (!balanceType.equals(other.balanceType))
			return false;
		if (createTime == null) {
			if (other.createTime != null)
				return false;
		} else if (!createTime.equals(other.createTime))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (sourceId == null) {
			if (other.sourceId != null)
				return false;
		} else if (!sourceId.equals(other.sourceId))
			return false;
		if (updateTime == null) {
			if (other.updateTime != null)
				return false;
		} else if (!updateTime.equals(other.updateTime))
			return false;
		return true;
	}
}