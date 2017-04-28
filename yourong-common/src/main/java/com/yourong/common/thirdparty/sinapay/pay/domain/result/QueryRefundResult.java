package com.yourong.common.thirdparty.sinapay.pay.domain.result;

import java.util.List;

/**
 * <p>查询退款结果</p>
 * @author Wallis Wang
 * @version $Id: QueryRefundResult.java, v 0.1 2014年6月5日 下午1:10:23 wangqiang Exp $
 */
public class QueryRefundResult extends PageResult {

    private static final long serialVersionUID = 807686855603521488L;

    /**
     * 交易条目
     */
    private List<TradeItem>   tradeList;

    public List<TradeItem> getTradeList() {
        return tradeList;
    }

    public void setTradeList(List<TradeItem> tradeList) {
        this.tradeList = tradeList;
    }

}
