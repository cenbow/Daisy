package com.yourong.common.thirdparty.sinapay.pay.domain.result;

import com.yourong.common.thirdparty.sinapay.common.enums.PayStatus;

/**
 * <p>支付查询结果</p>
 * @author Wallis Wang
 * @version $Id: QueryPayTradeResult.java, v 0.1 2014年6月4日 下午9:33:02 wangqiang Exp $
 */
public class QueryPayTradeResult extends PageResult {

    /**
     * 
     */
    private static final long serialVersionUID = 7665227375250830877L;

    /**
     * 支付订单号
     */
    private String    outPayNo;

    /**
     * 支付状态
     */
    private PayStatus payStatus;

    public String getOutPayNo() {
        return outPayNo;
    }

    public void setOutPayNo(String outPayNo) {
        this.outPayNo = outPayNo;
    }

    public PayStatus getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(PayStatus payStatus) {
        this.payStatus = payStatus;
    }
}
