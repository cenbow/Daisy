package com.yourong.common.thirdparty.sinapay.pay.domain.notify;

import com.yourong.common.thirdparty.sinapay.common.annotations.NotNull;

/**
 * <p>提现异步状态结果</p>
 * @author Wallis Wang
 * @version $Id: NotifyWithdrawResponse.java, v 0.1 2014年6月20日 下午6:58:36 wangqiang Exp $
 */
public class NotifyWithdrawResponse extends NotifyResponse {

    /**
     * 商户网站唯一订单号 或者交易原始凭证号
     */
    @NotNull
    private String outerTradeNo;

    /**
     * 商户网站交易订单号，商户内部保证唯一
     */
    @NotNull
    private String innerTradeNo;

    /**
     * 提现状态
     */
    @NotNull
    private String withdrawStatus;

    /**
     * 提现金额
     */
    @NotNull
    private String withdrawAmount;

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

    public String getWithdrawStatus() {
        return withdrawStatus;
    }

    public void setWithdrawStatus(String withdrawStatus) {
        this.withdrawStatus = withdrawStatus;
    }

    public String getWithdrawAmount() {
        return withdrawAmount;
    }

    public void setWithdrawAmount(String withdrawAmount) {
        this.withdrawAmount = withdrawAmount;
    }
}
