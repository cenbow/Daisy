package com.yourong.common.thirdparty.sinapay.pay.domain.notify;

import com.yourong.common.thirdparty.sinapay.common.annotations.NotNull;

/**
 * <p>交易异步通知响应</p>
 * <pre>
 * 1、代收
 * 2、代付
 * 3、批量代付
 * 4、托管支付
 * </pre>
 * @author Wallis Wang
 * @version $Id: NotifyTradeResponse.java, v 0.1 2014年6月20日 上午11:16:42 wangqiang Exp $
 */
public class NotifyTradeResponse extends NotifyResponse {
    /**
     * 商户网站唯一订单号 or 交易原始凭证号 
     */
    @NotNull
    private String outerTradeNo;

    /**
     * 交易凭证号
     */
    @NotNull
    private String innerTradeNo;

    /**
     *  交易状态 
     */
    @NotNull
    private String tradeStatus;

    /**
     * 交易金额
     */
    @NotNull
    private String tradeAmount;

    /**
     * 交易创建时间 
     */
    private String gmtCreate;

    /**
     * 交易支付时间
     */
    private String gmtPayment;

    /**
     * 交易关闭时间
     */
    private String gmtClose;

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

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public String getTradeAmount() {
        return tradeAmount;
    }

    public void setTradeAmount(String tradeAmount) {
        this.tradeAmount = tradeAmount;
    }

    public String getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(String gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public String getGmtPayment() {
        return gmtPayment;
    }

    public void setGmtPayment(String gmtPayment) {
        this.gmtPayment = gmtPayment;
    }

    public String getGmtClose() {
        return gmtClose;
    }

    public void setGmtClose(String gmtClose) {
        this.gmtClose = gmtClose;
    }

}
