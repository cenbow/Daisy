package com.yourong.common.thirdparty.sinapay.pay.domain.respone;

/**
 * <p>充值(响应JSON结果进行转换</p>
 * @author Wallis Wang
 * @version $Id: CreateDepositResponse.java, v 0.1 2014年5月15日 下午7:35:10 wangqiang Exp $
 */
public class CreateDepositResponse extends PayResponse {

    /**
     * 充值订单号
     */
    private String outTradeNo;

    /**
     * 充值状态
     */
    private String depositStatus;
    //后续推进需要的参数
    private String  ticket;

    public String getTicket() {
        return ticket;
    }
    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getDepositStatus() {
        return depositStatus;
    }

    public void setDepositStatus(String depositStatus) {
        this.depositStatus = depositStatus;
    }

}
