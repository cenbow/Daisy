package com.yourong.common.thirdparty.sinapay.pay.domain.respone;

/**
 * <p>提现查询(响应JSON结果进行转换)</p>
 * @author Wallis Wang
 * @version $Id: CreateWithDrawResponse.java, v 0.1 2014年5月15日 下午7:39:30 wangqiang Exp $
 */
public class CreateWithDrawResponse extends PayResponse {

    /**
     * 提现订单号
     */
    private String outTradeNo;

    /**
     * 提现状态
     */
    private String withdrawStatus;

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getWithdrawStatus() {
        return withdrawStatus;
    }

    public void setWithdrawStatus(String withdrawStatus) {
        this.withdrawStatus = withdrawStatus;
    }

}
