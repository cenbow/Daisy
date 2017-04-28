package com.yourong.common.thirdparty.sinapay.pay.domain.result;

import java.io.Serializable;

import com.yourong.common.thirdparty.sinapay.common.enums.PayStatus;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeStatus;

/**
 * <p>代收</p>
 * @author Wallis Wang
 * @version $Id: CollectTradeResult.java, v 0.1 2014年5月21日 下午1:27:06 wangqiang Exp $
 */
public class CreateCollectTradeResult implements Serializable {

    private static final long serialVersionUID = 6688785846589833272L;
    /**
     * 房金所系统交易订单号，对于外部必须唯一。
     */
    private String            tradeNo;
    /**
     * 交易状态
     */
    private TradeStatus       tradeStatus;

    /**
     * 支付状态
     */
    private PayStatus         payStatus;

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public TradeStatus getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(TradeStatus tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public PayStatus getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(PayStatus payStatus) {
        this.payStatus = payStatus;
    }

}
