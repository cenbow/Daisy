package com.yourong.core.tc.model;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.core.tc.model.HostingPayTrade;
import com.yourong.core.tc.model.TransactionInterest;
import com.yourong.core.uc.model.Member;
import org.apache.ibatis.type.Alias;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户投资记录和代付表关联结果
 * Created by py on 2015/7/30.
 */
@Alias("HostingPayProjectMember")
public class HostingPayProjectMember extends AbstractBaseObject {
    private Long id;
    private Integer  status;
    private BigDecimal payableInterest;
    private BigDecimal payablePrincipal;
    private BigDecimal realPayInterest;
    private BigDecimal realPayPrincipal;
    private String remarks;
    private String trueName;
    private Long mobile;
    private String tradeNo;
    private String outTradeNo;
    private String batchPayNo;
    private BigDecimal amount;
    private String tradeStatus;
    private String hostRepayRemarks;
    private Date createTime;
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public BigDecimal getRealPayInterest() {
        return realPayInterest;
    }

    public void setRealPayInterest(BigDecimal realPayInterest) {
        this.realPayInterest = realPayInterest;
    }

    public BigDecimal getRealPayPrincipal() {
        return realPayPrincipal;
    }

    public void setRealPayPrincipal(BigDecimal realPayPrincipal) {
        this.realPayPrincipal = realPayPrincipal;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getBatchPayNo() {
        return batchPayNo;
    }

    public void setBatchPayNo(String batchPayNo) {
        this.batchPayNo = batchPayNo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public String getHostRepayRemarks() {
        return hostRepayRemarks;
    }

    public void setHostRepayRemarks(String hostRepayRemarks) {
        this.hostRepayRemarks = hostRepayRemarks;
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
}
