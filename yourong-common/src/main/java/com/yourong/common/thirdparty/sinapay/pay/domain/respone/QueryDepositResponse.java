package com.yourong.common.thirdparty.sinapay.pay.domain.respone;

/**
 * <p>充值查询(响应JSON结果进行转换)</p>
 * @author Wallis Wang
 * @version $Id: QueryDepositResponse.java, v 0.1 2014年5月15日 下午7:37:34 wangqiang Exp $
 */
public class QueryDepositResponse extends PageRespone {

    /**
     * 充值明细列表
     */
    private String depositList;

    public String getDepositList() {
        return depositList;
    }

    public void setDepositList(String depositList) {
        this.depositList = depositList;
    }
}
