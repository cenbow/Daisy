package com.yourong.common.thirdparty.sinapay.pay.domain.respone;

/**
 * <p>提现查询(响应JSON结果进行转换)</p>
 * @author Wallis Wang
 * @version $Id: QueryWithDrawResponse.java, v 0.1 2014年5月15日 下午7:41:29 wangqiang Exp $
 */
public class QueryWithDrawResponse extends PageRespone {

    /**
     * 提现明细列表
     */
    private String withdrawList;

    public String getWithdrawList() {
        return withdrawList;
    }

    public void setWithdrawList(String withdrawList) {
        this.withdrawList = withdrawList;
    }
}
