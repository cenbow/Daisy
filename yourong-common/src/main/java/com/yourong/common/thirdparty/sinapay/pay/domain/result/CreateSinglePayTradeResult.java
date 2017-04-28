package com.yourong.common.thirdparty.sinapay.pay.domain.result;

import java.io.Serializable;

import com.yourong.common.thirdparty.sinapay.common.enums.TradeStatus;

/**
 * <p>单笔代付结果对象</p>
 * @author Wallis Wang
 * @version $Id: SingleHostPayTradeResult.java, v 0.1 2014年5月21日 上午10:22:59 wangqiang Exp $
 */
public class CreateSinglePayTradeResult implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 8469692672430358841L;

    /**
     * 交易订单号
     */
    private String            tradeNo;

    /**
     * 交易状态
     */
    private TradeStatus       tradeStatus;

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

}
