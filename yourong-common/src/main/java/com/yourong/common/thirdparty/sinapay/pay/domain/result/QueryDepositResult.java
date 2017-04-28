package com.yourong.common.thirdparty.sinapay.pay.domain.result;

import java.util.List;

/**
 * <p>充值查询结果对象</p>
 * @author Wallis Wang
 * @version $Id: QueryDepositResult.java, v 0.1 2014年5月21日 下午1:27:25 wangqiang Exp $
 */
public class QueryDepositResult extends PageResult {

    /**
     * 
     */
    private static final long serialVersionUID = 1953231705091886285L;
    /**
     * 充值条目参数
     */
    private List<TradeItem>   tradeItemList;

    public List<TradeItem> getTradeItemList() {
        return tradeItemList;
    }

    public void setTradeItemList(List<TradeItem> tradeItemList) {
        this.tradeItemList = tradeItemList;
    }
}
