package com.yourong.common.thirdparty.pay;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * 充值明细
 * 
 * @author Administrator
 *
 */
public class WithdrawItem extends AbstractBaseObject {

    /**
     * 
     */
    private static final long serialVersionUID = -2889097864658638512L;
    /**
     * 交易订单号
     */
    private String out_trade_no;
    /**
     * 交易金额
     */
    private String amount;
    /**
     * 状态
     */
    private String withdraw_status;
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

    public String getAmount() {
	return amount;
    }

    public void setAmount(String amount) {
	this.amount = amount;
    }

    public String getWithdraw_status() {
	return withdraw_status;
    }

    public void setWithdraw_status(String withdraw_status) {
	this.withdraw_status = withdraw_status;
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
