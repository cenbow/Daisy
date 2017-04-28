package com.yourong.common.thirdparty.sinapay.pay.domain.respone;


/**
 * <p>代收(响应JSON结果进行转换)</p>
 * @author Wallis Wang
 * @version $Id: CollectTradeResponse.java, v 0.1 2014年5月15日 下午7:16:53 wangqiang Exp $
 */
public class CollectTradeResponse extends PayResponse {

    /**
     * 交易订单号
     */
    private String outTradeNo;

    /**
     * 交易状态
     */
    private String tradeStatus;

    /**
     * 支付状态
     */
    private String payStatus;

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

}
