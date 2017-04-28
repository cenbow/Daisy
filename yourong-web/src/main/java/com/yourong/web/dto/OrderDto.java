package com.yourong.web.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.Digits;

public class OrderDto implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 690063366925237829L;

	/****/
    private Long id;

    /**内部交易号，由前台生成**/
    private String orderNo;

    /**外部交易号，由第三方返回**/
    private String outOrderNo;

    /**用户id**/
    private Long memberId;

    /****/
    private Long projectId;

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
    /**
     * 是否第一次创建订单
     */
    private Boolean isFirstCreaterOrder ;
    /**
     * 银行卡ID
     */
    private Long cardID;

    
	/**
	 * 项目类型
	 */
    private Integer projectCategory;

	/**
	 * 转让项目id
	 */
	private Long transferId;

	/**
	 * 转让本金
	 */
	@Digits(integer = 10, fraction = 2)
	private BigDecimal transferPrincipal;

    public Long getCardID() {
        return cardID;
    }

    public void setCardID(Long cardID) {
        this.cardID = cardID;
    }

    public Boolean getIsFirstCreaterOrder() {
        return isFirstCreaterOrder;
    }

    public void setIsFirstCreaterOrder(Boolean isFirstCreaterOrder) {
        this.isFirstCreaterOrder = isFirstCreaterOrder;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

	public BigDecimal getExtraAnnualizedRate() {
		return extraAnnualizedRate;
	}

	public void setExtraAnnualizedRate(BigDecimal extraAnnualizedRate) {
		this.extraAnnualizedRate = extraAnnualizedRate;
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

}