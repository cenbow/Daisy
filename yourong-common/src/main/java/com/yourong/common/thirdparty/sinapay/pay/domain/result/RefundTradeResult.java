package com.yourong.common.thirdparty.sinapay.pay.domain.result;

import com.yourong.common.thirdparty.sinapay.common.enums.RefundStatus;

/**
 * <p>退款结果</p>
 * @author Wallis Wang
 * @version $Id: RefundTradeResult.java, v 0.1 2014年6月24日 下午4:08:15 wangqiang Exp $
 */
public class RefundTradeResult {

    /**
     * 交易订单号
     */
    private String       outTradeNo;

    /**
     * 退款状态
     */
    private RefundStatus refundStatus;

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public RefundStatus getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(RefundStatus refundStatus) {
        this.refundStatus = refundStatus;
    }
}
