package com.yourong.common.thirdparty.sinapay.pay.domain.notify;

import com.yourong.common.thirdparty.sinapay.common.annotations.NotNull;

/**
 * <p>退款异步响应结果</p>
 * @author Wallis Wang
 * @version $Id: NotifyRefundResponse.java, v 0.1 2014年6月20日 上午11:03:20 wangqiang Exp $
 */
public class NotifyRefundResponse extends NotifyResponse {

    /**
     * 原交易商户网站唯一订单号 or 退款原交易原始凭证号
     */
    @NotNull
    private String origOuterTradeNo;

    /**
     * 商户网站退款唯一订单号 or 退款交易原始凭证号
     */
    @NotNull
    private String outerTradeNo;

    /**
     * 退款交易凭证号
     */
    @NotNull
    private String innerTradeNo;

    /**
     * 退款金额 
     */
    @NotNull
    private String refundAmount;

    /**
     * 退款状态 
     */
    @NotNull
    private String refundStatus;

    /**
     * 交易退款时间
     */
    @NotNull
    private String gmtRefund;

    public String getOrigOuterTradeNo() {
        return origOuterTradeNo;
    }

    public void setOrigOuterTradeNo(String origOuterTradeNo) {
        this.origOuterTradeNo = origOuterTradeNo;
    }

    public String getOuterTradeNo() {
        return outerTradeNo;
    }

    public void setOuterTradeNo(String outerTradeNo) {
        this.outerTradeNo = outerTradeNo;
    }

    public String getInnerTradeNo() {
        return innerTradeNo;
    }

    public void setInnerTradeNo(String innerTradeNo) {
        this.innerTradeNo = innerTradeNo;
    }

    public String getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(String refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }

    public String getGmtRefund() {
        return gmtRefund;
    }

    public void setGmtRefund(String gmtRefund) {
        this.gmtRefund = gmtRefund;
    }
}
