package com.yourong.common.thirdparty.sinapay.pay.domain.respone;


/**
 * <p>退款(响应JSON结果进行转换)</p>
 * @author Wallis Wang
 * @version $Id: RefundTradeResponse.java, v 0.1 2014年5月15日 下午7:31:41 wangqiang Exp $
 */
public class RefundTradeResponse extends PayResponse {

    /**
     * 交易订单号
     */
    private String outTradeNo;

    /**
     * 退款状态
     */
    private String refundStatus;

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }
}
