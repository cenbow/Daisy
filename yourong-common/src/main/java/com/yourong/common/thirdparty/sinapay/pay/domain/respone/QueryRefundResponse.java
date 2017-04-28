package com.yourong.common.thirdparty.sinapay.pay.domain.respone;

/**
 * <p>退款查询(响应JSON结果进行转换)</p>
 * @author Wallis Wang
 * @version $Id: QueryRefundResponse.java, v 0.1 2014年6月5日 下午1:12:40 wangqiang Exp $
 */
public class QueryRefundResponse extends PageRespone {

    /**
     * 交易明细
     */
    private String tradeList;

    public String getTradeList() {
        return tradeList;
    }

    public void setTradeList(String tradeList) {
        this.tradeList = tradeList;
    }
}
