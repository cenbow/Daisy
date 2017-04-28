package com.yourong.core.fin.model;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.domain.AbstractBaseObject;

public class RechargeLog extends  AbstractBaseObject {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1516716823940463311L;

	/****/
    private Long id;

    /**充值金额**/
    private BigDecimal amount;
    /**手续费**/
    private BigDecimal fee;

    /**充值时间**/
    private Date rechargeTime;

    /**会员id**/
    private Long memberId;
    
    /**会员姓名**/
    private String trueName;
    
    /**手机号**/
    private Long mobile;

    /**1-网银直连，2-绑卡支付*/
    private Integer payMethod;

    /**备注**/
    private String remarks;

    /**银行编码**/
    private String bankCode;

    /**交易号**/
    private String rechargeNo;

    /**外部交易号**/
    private String outerRechargeNo;

    /**-2-失败 -1-拒绝  0-冻结  1-处理中  5-操作成功  **/
    private Integer status;

    /****/
    private Date updateTime;
    
    /**充值类型：1-直接充值 2-交易充值  **/
    private Integer type;
    
    /**订单号  **/
    private String orderNo;

    /**
     * 来源 0-pc 1-android 2-ios,默认null，是pc
     */
    private Integer rechargeSource;

	/**
	 * 支付者IP
	 */
	private String payerIp;

    public Long getBankCardId() {
        return bankCardId;
    }

    public void setBankCardId(Long bankCardId) {
        this.bankCardId = bankCardId;
    }

    /** 银行卡id**/
    private Long  bankCardId;

    public Long getId() {
        return id;
    }

    public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getRechargeTime() {
        return rechargeTime;
    }

    public void setRechargeTime(Date rechargeTime) {
        this.rechargeTime = rechargeTime;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
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

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode == null ? null : bankCode.trim();
    }


    public String getRechargeNo() {
		return rechargeNo;
	}

	public void setRechargeNo(String rechargeNo) {
		this.rechargeNo = rechargeNo;
	}

	public String getOuterRechargeNo() {
		return outerRechargeNo;
	}

	public void setOuterRechargeNo(String outerRechargeNo) {
		this.outerRechargeNo = outerRechargeNo;
	}

	public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}


    public Integer getRechargeSource() {
        return rechargeSource;
    }

    public void setRechargeSource(Integer rechargeSource) {
        this.rechargeSource = rechargeSource;
    }

	public String getTrueName() {
		return trueName;
	}

	public void setTrueName(String trueName) {
		this.trueName = trueName;
	}

	public Long getMobile() {
		return mobile;
	}

	public void setMobile(Long mobile) {
		this.mobile = mobile;
	}

	public String getPayerIp() {
		return payerIp;
	}

	public void setPayerIp(String payerIp) {
		this.payerIp = payerIp;
	}

}