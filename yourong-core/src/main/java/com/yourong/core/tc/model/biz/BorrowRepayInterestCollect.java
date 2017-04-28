package com.yourong.core.tc.model.biz;

import com.yourong.common.domain.AbstractBaseObject;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by XR on 2016/9/11.
 */
public class BorrowRepayInterestCollect extends AbstractBaseObject {
    /**
     * 还款日期
     */
    private Date endDate;
    /**
     * 带还款项目
     */
    private Integer waitRepayNum;
    /**
     * 需还本金
     */
    private BigDecimal payablePrincipal;
    /**
     * 需还利息
     */
    private BigDecimal payableInterest;
    /**
     * 平台贴息
     */
    private BigDecimal extraInterest;
    /**
     * 需还金额
     */
    private BigDecimal payabletotal;

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getWaitRepayNum() {
        return waitRepayNum;
    }

    public void setWaitRepayNum(Integer waitRepayNum) {
        this.waitRepayNum = waitRepayNum;
    }

    public BigDecimal getPayablePrincipal() {
        return payablePrincipal;
    }

    public void setPayablePrincipal(BigDecimal payablePrincipal) {
        this.payablePrincipal = payablePrincipal;
    }

    public BigDecimal getPayableInterest() {
        return payableInterest;
    }

    public void setPayableInterest(BigDecimal payableInterest) {
        this.payableInterest = payableInterest;
    }

    public BigDecimal getExtraInterest() {
        return extraInterest;
    }

    public void setExtraInterest(BigDecimal extraInterest) {
        this.extraInterest = extraInterest;
    }

    public BigDecimal getPayabletotal() {
        return this.payableInterest.add(this.payablePrincipal);
    }

    public void setPayabletotal(BigDecimal payabletotal) {
        this.payabletotal = payabletotal;
    }
}
