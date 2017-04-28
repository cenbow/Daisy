package com.yourong.common.thirdparty.pay;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * 收支明细
 * @author Administrator
 *
 */
public class DetailItem extends AbstractBaseObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8567890515776833993L;
	/**
	 * 摘要
	 */
	private String summary;
	/**
	 * 入账时间
	 */
	private String create_time;
	/**
	 * 加减方向
	 */
	private String detail_type;
	/**
	 * 发生额
	 */
	private String amount;
	/**
	 * 交易后余额
	 */
	private String balance;
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public String getDetail_type() {
		return detail_type;
	}
	public void setDetail_type(String detail_type) {
		this.detail_type = detail_type;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}
	
	
}
