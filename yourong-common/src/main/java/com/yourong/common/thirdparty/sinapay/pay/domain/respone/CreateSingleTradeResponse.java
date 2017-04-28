package com.yourong.common.thirdparty.sinapay.pay.domain.respone;

/**
 * <p>单笔代付(响应JSON结果进行转换)</p>
 * @author Wallis Wang
 * @version $Id: PayTradeResponse.java, v 0.1 2014年5月15日 下午7:28:56 wangqiang Exp $
 */
public class CreateSingleTradeResponse extends PayResponse {

    /**
     * 交易订单号
     */
    private String outTradeNo;

    /**
     * 交易状态
     */
    private String tradeStatus;

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

}
