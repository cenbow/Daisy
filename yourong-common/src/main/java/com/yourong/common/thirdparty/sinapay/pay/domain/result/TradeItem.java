package com.yourong.common.thirdparty.sinapay.pay.domain.result;

import com.yourong.common.thirdparty.sinapay.common.domain.Money;

/**
 * <p>充值条目参数</p>
 * @author Walis Wang
 * @version $Id: PayItemArgs.java, v 0.1 2014年5月7日 下午5:16:43 wangqiang Exp $
 */
public class TradeItem {

    /**
     * 充值订单号
     */
    private String tradeNo;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态
     */
    private Money  amount;

    /**
     * 处理状态 
     */
    private String processStatus;
    /**
     * 创建时间
     */
    private String gmtCreate;

    /**
     * 最后修改时间
     */
    private String gmtModified;

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public Money getAmount() {
        return amount;
    }

    public void setAmount(Money amount) {
        this.amount = amount;
    }

    public String getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }

    public String getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(String gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public String getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(String gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
