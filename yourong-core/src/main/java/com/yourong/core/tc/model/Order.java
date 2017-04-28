package com.yourong.core.tc.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import com.yourong.common.domain.AbstractBaseObject;

@SuppressWarnings("serial")
public class Order extends AbstractBaseObject {

	/****/
    private Long id;

    /**内部交易号，由前台生成**/
    private String orderNo;

    /**外部交易号，由第三方返回**/
    private String outOrderNo;

    /**用户id**/
    @NotNull(message="90016")
    private Long memberId;

    /****/
    private Long projectId;
    
    /**项目名称**/
    private String projectName;

    /**银行编号**/
    private String bankCode;

    /**投资额**/
    @Digits (integer=10, fraction=2)
    private BigDecimal investAmount;

    /**用户使用资金账户金额**/
    private BigDecimal usedCapital;

    /**用户使用奖励金额**/
    private BigDecimal usedCouponAmount;

    /**年化收益率**/
    private BigDecimal annualizedRate;
    
    /**使用收益券增加的年化收益**/
    private BigDecimal extraAnnualizedRate;
    
    /**项目加息的年化收益**/
    private BigDecimal extraProjectAnnualizedRate;

    /**支付金额**/
    private BigDecimal payAmount;

    /**1:待支付 2：已支付，投资失败 3：已支付，投资成功 4：支付失败**/
    private Integer status;

    /**1-新浪支付，2-盛付通**/
    private Integer payMethod;
    
    /**备注**/
    private String remarks;

    /**使用的现金券编号**/
    private String cashCouponNo;
    /**
     * 使用的收益权编号
     */
    private String profitCouponNo;
    
    /**订单来源 0:PC,1：android, 2:IOS**/
    private Integer orderSource;

    /****/
    private Date orderTime;

    /****/
    private Date updateTime;

	/** 用于余额支付以后判断该订单是否符合参加活动的标识 */
	private boolean isActivity = false;
	
	/** 投标标识（0-手动投资（默认null也为手动投资）；1-自动投资） **/
	private Integer investFlag;

	/** 项目类型 **/
    private Integer projectCategory;

	/**
	 * 转让项目id
	 */
	private Long transferId;

	/**
	 * 转让本金
	 */
	private BigDecimal transferPrincipal;
	
	private Integer extraInterestDay;
	
	

    public Integer getExtraInterestDay() {
		return extraInterestDay;
	}

	public void setExtraInterestDay(Integer extraInterestDay) {
		this.extraInterestDay = extraInterestDay;
	}

	public void setId(Long id) {
        this.id = id;
    }

	public Long getId() {
		return id;
	}

    public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getOutOrderNo() {
		return outOrderNo;
	}

	public void setOutOrderNo(String outOrderNo) {
		this.outOrderNo = outOrderNo;
	}

	public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode == null ? null : bankCode.trim();
    }

    public BigDecimal getInvestAmount() {
        return investAmount;
    }

    public void setInvestAmount(BigDecimal investAmount) {
        this.investAmount = investAmount;
    }

    public BigDecimal getUsedCapital() {
        return usedCapital;
    }

    public void setUsedCapital(BigDecimal usedCapital) {
        this.usedCapital = usedCapital;
    }

    public BigDecimal getUsedCouponAmount() {
        return usedCouponAmount;
    }

    public void setUsedCouponAmount(BigDecimal usedCouponAmount) {
        this.usedCouponAmount = usedCouponAmount;
    }

    public BigDecimal getAnnualizedRate() {
        return annualizedRate;
    }

    public void setAnnualizedRate(BigDecimal annualizedRate) {
        this.annualizedRate = annualizedRate;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(Integer payMethod) {
        this.payMethod = payMethod;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks == null ? null : remarks.trim();
    }


    public String getCashCouponNo() {
		return cashCouponNo;
	}

	public void setCashCouponNo(String cashCouponNo) {
		this.cashCouponNo = cashCouponNo;
	}

	public String getProfitCouponNo() {
		return profitCouponNo;
	}

	public void setProfitCouponNo(String profitCouponNo) {
		this.profitCouponNo = profitCouponNo;
	}

	public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

	public BigDecimal getExtraAnnualizedRate() {
		return extraAnnualizedRate;
	}

	public void setExtraAnnualizedRate(BigDecimal extraAnnualizedRate) {
		this.extraAnnualizedRate = extraAnnualizedRate;
	}

	public Integer getOrderSource() {
		return orderSource;
	}

	public void setOrderSource(Integer orderSource) {
		this.orderSource = orderSource;
	}

	public boolean isActivity() {
		return isActivity;
	}

	public void setActivity(boolean isActivity) {
		this.isActivity = isActivity;
	}

	public Integer getInvestFlag() {
		return investFlag;
	}

	public void setInvestFlag(Integer investFlag) {
		this.investFlag = investFlag;
	}

	

	public Long getTransferId() {
		return transferId;
	}

	public void setTransferId(Long transferId) {
		this.transferId = transferId;
	}

	public BigDecimal getTransferPrincipal() {
		return transferPrincipal;
	}

	public void setTransferPrincipal(BigDecimal transferPrincipal) {
		this.transferPrincipal = transferPrincipal;
	}

	/**
	 * @return the projectCategory
	 */
	public Integer getProjectCategory() {
		return projectCategory;
	}

	/**
	 * @param projectCategory the projectCategory to set
	 */
	public void setProjectCategory(Integer projectCategory) {
		this.projectCategory = projectCategory;
	}

	public BigDecimal getExtraProjectAnnualizedRate() {
		return extraProjectAnnualizedRate;
	}

	public void setExtraProjectAnnualizedRate(BigDecimal extraProjectAnnualizedRate) {
		this.extraProjectAnnualizedRate = extraProjectAnnualizedRate;
	}
	

}