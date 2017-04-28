package com.yourong.core.tc.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;

public class TransactionInterest implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 4941487938805576752L;

	/****/
    private Long id;
    
    /**项目本息id**/
    private Long interestId;
    
    /**项目id**/
    private Long projectId;

    /** 交易id**/
    private Long transactionId;

    /**计息开始时间**/
    private Date startDate;

    /**计息结束时间**/
    private Date endDate;

    /**用户ID**/
    private Long memberId;

    /**状态：0：待支付 1：已全部支付 2:部分支付 3:未支付**/
    private Integer status;

    /**单位本金**/
    private BigDecimal unitPrincipal;

    /**单位利息**/
    private BigDecimal unitInterest;

    /**应付利息**/
    private BigDecimal payableInterest;

    /**应付本金**/
    private BigDecimal payablePrincipal;
    
    /**使用收益券产生的利息**/
    private BigDecimal extraInterest = BigDecimal.ZERO;
    
    /**项目加息产生的利息**/
    private BigDecimal extraProjectInterest = BigDecimal.ZERO;

    /**实付本金**/
    private BigDecimal realPayPrincipal = BigDecimal.ZERO;

    /**实付利息**/
    private BigDecimal realPayInterest = BigDecimal.ZERO;
    
    /**实际支付使用收益券收益*/
    private BigDecimal realPayExtraInterest = BigDecimal.ZERO;
    
    /***实际支付项目加息产生的利息**/
    private BigDecimal realPayExtraProjectInterest = BigDecimal.ZERO;
    
    /**滞纳金**/
    private BigDecimal overdueFine = BigDecimal.ZERO;

    /**还款类型（0-正常 1-提前还款 2-逾期还款 3-垫资还款 ）**/
    private Integer payType;
    
    /**还款日期**/
    private Date topayDate;
    
    /**支付日期**/
    private Date payTime;
    
    /**代收交易号**/
    private String tradeNo;
    
    /**备注**/
    private String remarks;

    /**创建时间**/
    private Date createTime;

    /**修改时间**/
    private Date updateTime;
    
    /**支付日期**/
    private String payTimeStr;
    
    /**所属期数**/
    private String periods;
    
    private Integer extraInterestDay;
    
    private Date extraEndDay;
    
    

    public Date getExtraEndDay() {
		return extraEndDay;
	}

	public void setExtraEndDay(Date extraEndDay) {
		this.extraEndDay = extraEndDay;
	}

	public Integer getExtraInterestDay() {
		return extraInterestDay;
	}

	public void setExtraInterestDay(Integer extraInterestDay) {
		this.extraInterestDay = extraInterestDay;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
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

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public BigDecimal getUnitPrincipal() {
        return unitPrincipal;
    }

    public void setUnitPrincipal(BigDecimal unitPrincipal) {
        this.unitPrincipal = unitPrincipal;
    }

    public BigDecimal getUnitInterest() {
        return unitInterest;
    }

    public void setUnitInterest(BigDecimal unitInterest) {
        this.unitInterest = unitInterest;
    }

    public BigDecimal getPayableInterest() {
        return payableInterest;
    }

    public void setPayableInterest(BigDecimal payableInterest) {
        this.payableInterest = payableInterest;
    }

    public BigDecimal getPayablePrincipal() {
        return payablePrincipal;
    }

    public void setPayablePrincipal(BigDecimal payablePrincipal) {
        this.payablePrincipal = payablePrincipal;
    }

    public BigDecimal getExtraInterest() {
		return extraInterest;
	}

	public void setExtraInterest(BigDecimal extraInterest) {
		this.extraInterest = extraInterest;
	}

	public BigDecimal getRealPayPrincipal() {
        return realPayPrincipal;
    }

    public void setRealPayPrincipal(BigDecimal realPayPrincipal) {
        this.realPayPrincipal = realPayPrincipal;
    }

    public BigDecimal getRealPayInterest() {
        return realPayInterest;
    }

    public void setRealPayInterest(BigDecimal realPayInterest) {
        this.realPayInterest = realPayInterest;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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
    
    public int getDays() {
    	return DateUtils.daysOfTwo(this.startDate, this.endDate) + 1;
    }
    
    public int getRealDays() {
    	int days = DateUtils.daysOfTwo(this.startDate, this.payTime) + 1;
    	if(days > 0 ){
    		return days;
    	}
    	return 0;
    }

	

	public void setPayTimeStr(String payTimeStr) {
		this.payTimeStr = payTimeStr;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((createTime == null) ? 0 : createTime.hashCode());
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((memberId == null) ? 0 : memberId.hashCode());
		result = prime * result + ((payTime == null) ? 0 : payTime.hashCode());
		result = prime * result
				+ ((payableInterest == null) ? 0 : payableInterest.hashCode());
		result = prime
				* result
				+ ((payablePrincipal == null) ? 0 : payablePrincipal.hashCode());
		result = prime * result
				+ ((realPayInterest == null) ? 0 : realPayInterest.hashCode());
		result = prime
				* result
				+ ((realPayPrincipal == null) ? 0 : realPayPrincipal.hashCode());
		result = prime * result
				+ ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result
				+ ((transactionId == null) ? 0 : transactionId.hashCode());
		result = prime * result
				+ ((unitInterest == null) ? 0 : unitInterest.hashCode());
		result = prime * result
				+ ((unitPrincipal == null) ? 0 : unitPrincipal.hashCode());
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
		TransactionInterest other = (TransactionInterest) obj;
		if (createTime == null) {
			if (other.createTime != null)
				return false;
		} else if (!createTime.equals(other.createTime))
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
		if (memberId == null) {
			if (other.memberId != null)
				return false;
		} else if (!memberId.equals(other.memberId))
			return false;
		if (payTime == null) {
			if (other.payTime != null)
				return false;
		} else if (!payTime.equals(other.payTime))
			return false;
		if (payableInterest == null) {
			if (other.payableInterest != null)
				return false;
		} else if (!payableInterest.equals(other.payableInterest))
			return false;
		if (payablePrincipal == null) {
			if (other.payablePrincipal != null)
				return false;
		} else if (!payablePrincipal.equals(other.payablePrincipal))
			return false;
		if (realPayInterest == null) {
			if (other.realPayInterest != null)
				return false;
		} else if (!realPayInterest.equals(other.realPayInterest))
			return false;
		if (realPayPrincipal == null) {
			if (other.realPayPrincipal != null)
				return false;
		} else if (!realPayPrincipal.equals(other.realPayPrincipal))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (transactionId == null) {
			if (other.transactionId != null)
				return false;
		} else if (!transactionId.equals(other.transactionId))
			return false;
		if (unitInterest == null) {
			if (other.unitInterest != null)
				return false;
		} else if (!unitInterest.equals(other.unitInterest))
			return false;
		if (unitPrincipal == null) {
			if (other.unitPrincipal != null)
				return false;
		} else if (!unitPrincipal.equals(other.unitPrincipal))
			return false;
		if (updateTime == null) {
			if (other.updateTime != null)
				return false;
		} else if (!updateTime.equals(other.updateTime))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TransactionInterest [id=");
		builder.append(id);
		builder.append(", transactionId=");
		builder.append(transactionId);
		builder.append(", startDate=");
		builder.append(startDate);
		builder.append(", endDate=");
		builder.append(endDate);
		builder.append(", memberId=");
		builder.append(memberId);
		builder.append(", status=");
		builder.append(status);
		builder.append(", unitPrincipal=");
		builder.append(unitPrincipal);
		builder.append(", unitInterest=");
		builder.append(unitInterest);
		builder.append(", payableInterest=");
		builder.append(payableInterest);
		builder.append(", payablePrincipal=");
		builder.append(payablePrincipal);
		builder.append(", realPayPrincipal=");
		builder.append(realPayPrincipal);
		builder.append(", realPayInterest=");
		builder.append(realPayInterest);
		builder.append(", payTime=");
		builder.append(payTime);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", updateTime=");
		builder.append(updateTime);
		builder.append("]");
		return builder.toString();
	}
	
	/**
	 * 交易本息表状态   
	 */
	public String getInterestStatusName(){
		if(status==null){
			return null;
		}
		if(payType!=null){
			if(StatusEnum.TRANSACTION_INTEREST_WAIT_PAY.getStatus()==status.intValue()&&TypeEnum.REPAYMENT_TYPE_OVERDUE.getType() ==payType){
				return "逾期";
			}
		}
		if(StatusEnum.TRANSACTION_INTEREST_WAIT_PAY.getStatus()==status.intValue()){
			return StatusEnum.TRANSACTION_INTEREST_WAIT_PAY.getDesc();
		}
		if(StatusEnum.TRANSACTION_INTEREST_PAYING.getStatus()==status.intValue()){
			return StatusEnum.TRANSACTION_INTEREST_PAYING.getDesc();
		}
		if(StatusEnum.TRANSACTION_INTEREST_PART_PAYED.getStatus()==status.intValue()){
			return StatusEnum.TRANSACTION_INTEREST_PART_PAYED.getDesc();
		}
		if(StatusEnum.TRANSACTION_INTEREST_NOT_PAY.getStatus()==status.intValue()){
			return StatusEnum.TRANSACTION_INTEREST_NOT_PAY.getDesc();
		}
		if(StatusEnum.TRANSACTION_INTEREST_ALL_PAYED.getStatus()==status.intValue()){
			return StatusEnum.TRANSACTION_INTEREST_ALL_PAYED.getDesc();
		}
		return null;
	}
	
	/**
	 * 获取格式化应付利息
	 * @return
	 */
	public String getFormatPayableInterest() {
		if(payableInterest!=null){
			return FormulaUtil.formatCurrencyNoUnit(payableInterest);
		}
		return null;
	}
	
	/**
	 * 获取格式化实付利息
	 * @return
	 */
	public String getFormatRealPayInterest() {
		if(payableInterest!=null){
			return FormulaUtil.formatCurrencyNoUnit(realPayInterest);
		}
		return null;
	}
	
	/**
	 * 获取格式化应付本金
	 * @return
	 */
	public String getFormatPayablePrincipal() {
		if(payablePrincipal!=null){
			return FormulaUtil.formatCurrencyNoUnit(payablePrincipal);
		}
		return null;
	}
	
	/**
	 * 获取格式化实付本金
	 * @return
	 */
	public String getFormatRealPayPrincipal() {
		if(payablePrincipal!=null){
			return FormulaUtil.formatCurrencyNoUnit(realPayPrincipal);
		}
		return null;
	}
	
	/**
	 * 获取格式化滞纳金
	 * @return
	 */
	public String getFormatOverdueFine() {
		if(overdueFine!=null){
			return FormulaUtil.formatCurrencyNoUnit(overdueFine);
		}
		return null;
	}
	
	
	
	/**
	 * 获取格式化应付本金+应付利息
	 * @return
	 */
	public String getFormatSum() {
		if(payablePrincipal!=null && payableInterest!=null){
			return FormulaUtil.formatCurrencyNoUnit(payablePrincipal.add(payableInterest));
		}
		if(payableInterest!=null && payablePrincipal ==null){
			return FormulaUtil.formatCurrencyNoUnit(payableInterest);
		}
		if(payablePrincipal!=null && payableInterest ==null){
			return FormulaUtil.formatCurrencyNoUnit(payablePrincipal);
		}
		return null;
	}
	
	/**
	 * 结束时间格式化
	 * @return
	 */
	public String getEndDateStr() {
		return DateUtils.formatDatetoString(endDate, DateUtils.DATE_FMT_3);
	}
	
	/**
	 * 支付时间格式化
	 * @return
	 */
	public String getPayTimeStr() {
		if(payTime!=null){
			return DateUtils.formatDatetoString(payTime, DateUtils.DATE_FMT_3);
		}
		return "——";
	}
	
	public String getPeriods() {
		return periods;
	}

	public void setPeriods(String periods) {
		this.periods = periods;
	}

	public BigDecimal getRealPayExtraInterest() {
		return realPayExtraInterest;
	}

	public void setRealPayExtraInterest(BigDecimal realPayExtraInterest) {
		this.realPayExtraInterest = realPayExtraInterest;
	}

	public BigDecimal getOverdueFine() {
		return overdueFine;
	}

	public void setOverdueFine(BigDecimal overdueFine) {
		this.overdueFine = overdueFine;
	}

	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public Date getTopayDate() {
		return topayDate;
	}

	public void setTopayDate(Date topayDate) {
		this.topayDate = topayDate;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Long getInterestId() {
		return interestId;
	}

	public void setInterestId(Long interestId) {
		this.interestId = interestId;
	}

	public BigDecimal getExtraProjectInterest() {
		return extraProjectInterest;
	}

	public void setExtraProjectInterest(BigDecimal extraProjectInterest) {
		this.extraProjectInterest = extraProjectInterest;
	}

	public BigDecimal getRealPayExtraProjectInterest() {
		return realPayExtraProjectInterest;
	}

	public void setRealPayExtraProjectInterest(
			BigDecimal realPayExtraProjectInterest) {
		this.realPayExtraProjectInterest = realPayExtraProjectInterest;
	}
	
}