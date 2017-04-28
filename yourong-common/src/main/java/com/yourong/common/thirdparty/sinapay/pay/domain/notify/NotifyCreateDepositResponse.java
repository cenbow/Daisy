package com.yourong.common.thirdparty.sinapay.pay.domain.notify;

import com.yourong.common.thirdparty.sinapay.common.annotations.NotNull;

/**
 * <p>充值异步通知响应</p>
 * @author Wallis Wang
 * @version $Id: NotifyCreateDepositResponse.java, v 0.1 2014年6月20日 上午11:07:17 wangqiang Exp $
 */
public class NotifyCreateDepositResponse extends NotifyResponse {

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
     * 充值状态
     */
    @NotNull
    private String depositStatus;

    /**
     * 充值金额 
     */
    @NotNull
    private String depositAmount;

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

    public String getDepositStatus() {
        return depositStatus;
    }

    public void setDepositStatus(String depositStatus) {
        this.depositStatus = depositStatus;
    }

    public String getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(String depositAmount) {
        this.depositAmount = depositAmount;
    }

}
