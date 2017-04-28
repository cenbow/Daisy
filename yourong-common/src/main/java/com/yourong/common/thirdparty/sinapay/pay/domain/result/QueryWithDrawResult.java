package com.yourong.common.thirdparty.sinapay.pay.domain.result;

import java.util.List;

/**
 * <p>提现查询结果对象</p>
 * @author Wallis Wang
 * @version $Id: QueryWithDrawResultDto.java, v 0.1 2014年5月7日 下午6:56:03 wangqiang Exp $
 */
public class QueryWithDrawResult extends PageResult {

    /**
     * 
     */
    private static final long serialVersionUID = 2434748967818679385L;
    /**
     * 提现条目
     */
    private List<TradeItem> withDrawList;

    public List<TradeItem> getWithDrawList() {
        return withDrawList;
    }

    public void setWithDrawList(List<TradeItem> withDrawList) {
        this.withDrawList = withDrawList;
    }
}
