package com.yourong.common.thirdparty.sinapay.pay.domain.result;

import java.util.List;

/**
 * <p>托管充值查询结果对象</p>
 * @author Wallis Wang
 * @version $Id: QueryTradeResult.java, v 0.1 2014年5月21日 下午1:27:42 wangqiang Exp $
 */
public class QueryTradeResult extends PageResult {

    /**
     * 
     */
    private static final long serialVersionUID = -4920046316895233494L;
    /**
     * 交易条目
     */
    private List<TradeItem> payItemList;

    public List<TradeItem> getPayItemList() {
        return payItemList;
    }

    public void setPayItemList(List<TradeItem> payItemList) {
        this.payItemList = payItemList;
    }

}
