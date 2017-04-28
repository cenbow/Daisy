package com.yourong.common.thirdparty.pay;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * 充值明细
 * @author Administrator
 *
 */
public class DepositItem extends AbstractBaseObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2846929301101808013L;
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
	private String deposit_status;
	/**
	 * 创建时间
	 */
	private String create_time;
	
	/**
	 *最后修改时间
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

	public String getDeposit_status() {
		return deposit_status;
	}

	public void setDeposit_status(String deposit_status) {
		this.deposit_status = deposit_status;
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
