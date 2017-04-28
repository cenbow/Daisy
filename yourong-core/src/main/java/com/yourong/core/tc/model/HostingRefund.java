package com.yourong.core.tc.model;

import java.math.BigDecimal;
import java.util.Date;

public class HostingRefund {
    /**主键**/
    private Long id;

    /**退款交易号**/
    private String tradeNo;

    /**外部交易号**/
    private String outTradeNo;

    /**代付交易号**/
    private String collectTradeNo;

    /**项目id**/
    private Long projectId;

    /**退款金额**/
    private BigDecimal amount;

    /**收款人id**/
    private Long receiverId;

    /**退款类型（1-交易退款；)**/
    private Integer type;

    /**退款状态(WAIT_REFUND,SUCCESS,FAILED)**/
    private String refundStatus;

    /**摘要**/
    private String summary;
    
    
    private String userIp;

    /**备注**/
    private String remarks;

    /**创建时间**/
    private Date createTime;

    /**更新时间**/
    private Date updateTime;

    /**删除标记（-1：删除；1-正常）**/
    private Integer delFlag;

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

    public String getCollectTradeNo() {
        return collectTradeNo;
    }

    public void setCollectTradeNo(String collectTradeNo) {
        this.collectTradeNo = collectTradeNo == null ? null : collectTradeNo.trim();
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary == null ? null : summary.trim();
    }

    public String getUserIp() {
		return userIp;
	}

	public void setUserIp(String userIp) {
		this.userIp = userIp;
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

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }
}