package com.yourong.core.tc.model;

import java.math.BigDecimal;
import java.util.Date;

public class HostingPayTrade {
    /****/
    private Long id;

    /**代付交易号**/
    private String tradeNo;

    /**外部交易号**/
    private String outTradeNo;
    
    /**批量代付交易号**/
    private String batchPayNo;

    /**1-放款给借款人 2-还款给投资人**/
    private Integer type;

    /**来源id（本息表id、充值流水id）**/
    private Long sourceId;

    /**收款人用户id**/
    private Long payeeId;

    /**金额**/
    private BigDecimal amount;

    /**摘要**/
    private String summary;
    
    /**代付交易ip**/
    private String userIp;

    /**交易状态(WAIT_PAY:等待付款 PAY_FINISHED:已付款  TRADE_SUCCESS:交易成功 TRADE_FAILED:交易失败 TRADE_FINISHED:交易结束 TRADE_CLOSED:交易关闭（合作方通过调用交易取消接口来关闭） **/
    private String tradeStatus;

    /**备注**/
    private String remarks;

    /****/
    private Date createTime;

    /****/
    private Date updateTime;
    
    private Long projectId;
    
    private Long transactionId;

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
        this.outTradeNo = outTradeNo == null ? null : outTradeNo.trim();
    }

    public String getBatchPayNo() {
		return batchPayNo;
	}

	public void setBatchPayNo(String batchPayNo) {
		this.batchPayNo = batchPayNo;
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

    public Long getPayeeId() {
        return payeeId;
    }

    public void setPayeeId(Long payeeId) {
        this.payeeId = payeeId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary == null ? null : summary.trim();
    }

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus == null ? null : tradeStatus.trim();
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
    
    public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public String getUserIp() {
		return userIp;
	}

	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}
	
}