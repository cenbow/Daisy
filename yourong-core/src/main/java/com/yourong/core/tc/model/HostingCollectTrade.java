package com.yourong.core.tc.model;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.enums.StatusEnum;


public class HostingCollectTrade  extends AbstractBaseObject{


    /****/
    private Long id;

    /**代收交易号**/
    private String tradeNo;
    
    /**外部交易号**/
    private String outTradeNo;

    /**1-用户投资 2-借款人还款**/
    private Integer type;

    /**来源id（订单id、充值流水id）**/
    private Long sourceId;

    /**金额**/
    private BigDecimal amount;
    
    /**平台垫付收益券金额**/
    private BigDecimal platformAmount;

    /**付款用户id**/
    private Long payerId;

    /**付款ip**/
    private String payerIp;

    /**支付方式 网银:online_bank 余额支付：balance**/
    private String payMethod;

    /**银行编码**/
    private String bankCode;

    /**摘要**/
    private String summary;

    /**设置未付款交易的超时时间，一旦超时，该笔交易就会自动被关闭。**/
    private String tradeCloseTime;

    /**交易状态**/
    private String tradeStatus;

    /**支付状态**/
    private String payStatus;

    /**备注**/
    private String remarks;

    /**交易时间**/
    private Date tradeTime;

    /**支付时间**/
    private Date payTime;

    /****/
    private Date updateTime;
    
    private String transactionInterestIds;
    
    private Long projectId;

	/**
	 * 代收交易类型
	 */
	private Integer isPreAuth;

	/**
	 * 是否委托支付代收
	 */
	private Integer isWithholdAuthority;

	/** 项目类型 **/
	private Integer projectCategory;

	/**
	 * 转让项目id
	 */
	private Long transferId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo == null ? null : tradeNo.trim();
    }

    public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    
    public BigDecimal getPlatformAmount() {
		return platformAmount;
	}

	public void setPlatformAmount(BigDecimal platformAmount) {
		this.platformAmount = platformAmount;
	}

	public Long getPayerId() {
        return payerId;
    }

    public void setPayerId(Long payerId) {
        this.payerId = payerId;
    }

    public String getPayerIp() {
        return payerIp;
    }

    public void setPayerIp(String payerIp) {
        this.payerIp = payerIp == null ? null : payerIp.trim();
    }

    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod == null ? null : payMethod.trim();
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode == null ? null : bankCode.trim();
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary == null ? null : summary.trim();
    }

    public String getTradeCloseTime() {
        return tradeCloseTime;
    }

    public void setTradeCloseTime(String tradeCloseTime) {
        this.tradeCloseTime = tradeCloseTime == null ? null : tradeCloseTime.trim();
    }

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus == null ? null : tradeStatus.trim();
    }

    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus == null ? null : payStatus.trim();
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks == null ? null : remarks.trim();
    }

    public Date getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(Date tradeTime) {
        this.tradeTime = tradeTime;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

	public String getTransactionInterestIds() {
		return transactionInterestIds;
	}

	public void setTransactionInterestIds(String transactionInterestIds) {
		this.transactionInterestIds = transactionInterestIds;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Integer getIsPreAuth() {
		return isPreAuth;
	}

	public void setIsPreAuth(Integer isPreAuth) {
		this.isPreAuth = isPreAuth;
	}

	public Integer getIsWithholdAuthority() {
		return isWithholdAuthority;
	}

	public void setIsWithholdAuthority(Integer isWithholdAuthority) {
		this.isWithholdAuthority = isWithholdAuthority;
	}

	public void setIsWithholdAuthority(boolean isWithholdAuthority) {
		if (isWithholdAuthority) {
			this.isWithholdAuthority = StatusEnum.WITHHOLD_AUTHORITY_FLAG_Y.getStatus();
		} else {
			this.isWithholdAuthority = StatusEnum.WITHHOLD_AUTHORITY_FLAG_N.getStatus();
		}
	}

	public Integer getProjectCategory() {
		return projectCategory;
	}

	public void setProjectCategory(Integer projectCategory) {
		this.projectCategory = projectCategory;
	}

	public Long getTransferId() {
		return transferId;
	}

	public void setTransferId(Long transferId) {
		this.transferId = transferId;
	}

}