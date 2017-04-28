package com.yourong.common.thirdparty.sinapay.pay.domain.respone;


/**
 *<p>交易查询(响应JSON结果进行转换)</p>
 * @author Wallis Wang
 * @version $Id: QueryTradeResponse.java, v 0.1 2014年5月15日 下午7:33:36 wangqiang Exp $
 */
public class QueryTradeResponse extends PageRespone {

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
