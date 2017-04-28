package com.yourong.common.thirdparty.sinapay.pay.domain.result;

import java.io.Serializable;

import com.yourong.common.thirdparty.sinapay.common.enums.TradeStatus;

/**
 * <p>交易结果</p>
 * @author Wallis Wang
 * @version $Id: TradeResult.java, v 0.1 2014年5月21日 下午1:25:23 wangqiang Exp $
 */
public class TradeResult implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -1699830317682902459L;

    /**
     * 充值订单号
     */
    private String      outTradeNo;

    /**
     * 交易状态
     */
    private TradeStatus tradeStatus;

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public TradeStatus getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(TradeStatus tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

}
