package com.yourong.common.thirdparty.sinapay.pay.domain.result;

import java.io.Serializable;

import com.yourong.common.thirdparty.sinapay.common.enums.PayStatus;

/**
 * <p>支付结果</p>
 * @author Wallis Wang
 * @version $Id: PayResult.java, v 0.1 2014年5月15日 下午6:56:19 wangqiang Exp $
 */
public class PayResult implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 3707979745007186761L;
    /**
     * 房金所系统交易订单号，对于外部必须唯一。
     */
    private String    tradeNo;


    //后续推进需要的参数
    private String  ticket;


    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }


    /**
     * 交易状态
     */
    private PayStatus payStatus;

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public PayStatus getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(PayStatus payStatus) {
        this.payStatus = payStatus;
    }

}
