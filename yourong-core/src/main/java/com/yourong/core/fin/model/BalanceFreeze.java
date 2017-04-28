package com.yourong.core.fin.model;

import java.math.BigDecimal;
import java.util.Date;

public class BalanceFreeze {
    /****/
    private Long id;

    /**冻结订单号**/
    private String freezeNo;

    /**解冻订单号**/
    private String unfreezeNo;

    /**用户id**/
    private Long memberId;

    /**1-冻结 2-解冻**/
    private Integer type;

    /**冻结金额**/
    private BigDecimal amount;

    /**余额**/
    private BigDecimal balance;

    /**摘要**/
    private String summary;

    /**备注**/
    private String remarks;

    /**冻结时间**/
    private Date freezeTime;

    /**解冻时间**/
    private Date unfreezeTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFreezeNo() {
        return freezeNo;
    }

    public void setFreezeNo(String freezeNo) {
        this.freezeNo = freezeNo == null ? null : freezeNo.trim();
    }

    public String getUnfreezeNo() {
        return unfreezeNo;
    }

    public void setUnfreezeNo(String unfreezeNo) {
        this.unfreezeNo = unfreezeNo == null ? null : unfreezeNo.trim();
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary == null ? null : summary.trim();
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks == null ? null : remarks.trim();
    }

    public Date getFreezeTime() {
        return freezeTime;
    }

    public void setFreezeTime(Date freezeTime) {
        this.freezeTime = freezeTime;
    }

    public Date getUnfreezeTime() {
        return unfreezeTime;
    }

    public void setUnfreezeTime(Date unfreezeTime) {
        this.unfreezeTime = unfreezeTime;
    }
}