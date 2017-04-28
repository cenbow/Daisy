package com.yourong.common.thirdparty.sinapay.pay.domain.result;

import com.yourong.common.thirdparty.sinapay.common.enums.NotifyWithdrawStatus;

/**
 * <p>提现结果</p>
 * @author Wallis Wang
 * @version $Id: WithdrawResult.java, v 0.1 2014年6月24日 下午12:55:58 wangqiang Exp $
 */
public class CreateWithdrawResult {

    /**
     * 提现订单号
     */
    private String outerTradeNo;

    /**
     * 提现状态
     */
    private NotifyWithdrawStatus withdrawStatus;

    public String getOuterTradeNo() {
        return outerTradeNo;
    }

    public void setOuterTradeNo(String outerTradeNo) {
        this.outerTradeNo = outerTradeNo;
    }

    public NotifyWithdrawStatus getWithdrawStatus() {
        return withdrawStatus;
    }

    public void setWithdrawStatus(NotifyWithdrawStatus withdrawStatus) {
        this.withdrawStatus = withdrawStatus;
    }
}
