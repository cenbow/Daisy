package com.yourong.common.thirdparty.pay;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * 交易明细
 * 
 * @author Administrator
 *
 */
public class TradeItem extends AbstractBaseObject {

    /**
     * 
     */
    private static final long serialVersionUID = 9131964651993338024L;
    /**
     * 交易订单号
     */
    private String out_trade_no;
    /**
     * 摘要
     */
    private String summary;
    /**
     * 交易金额
     */
    private String amount;
    /**
     * 状态
     */
    private String trade_status;
    /**
     * 创建时间
     */
    private String create_time;

    /**
     * 最后修改时间
     */
    private String last_update_time;

    public String getOut_trade_no() {
	return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
	this.out_trade_no = out_trade_no;
    }

    public String getSummary() {
	return summary;
    }

    public void setSummary(String summary) {
	this.summary = summary;
    }

    public String getAmount() {
	return amount;
    }

    public void setAmount(String amount) {
	this.amount = amount;
    }

    public String getTrade_status() {
	return trade_status;
    }

    public void setTrade_status(String trade_status) {
	this.trade_status = trade_status;
    }

    public String getCreate_time() {
	return create_time;
    }

    public void setCreate_time(String create_time) {
	this.create_time = create_time;
    }

    public String getLast_update_time() {
	return last_update_time;
    }

    public void setLast_update_time(String last_update_time) {
	this.last_update_time = last_update_time;
    }

}
