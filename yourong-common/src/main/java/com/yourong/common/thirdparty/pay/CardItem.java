package com.yourong.common.thirdparty.pay;

import com.yourong.common.domain.AbstractBaseObject;

public class CardItem extends AbstractBaseObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 309390325644493257L;
	/**
	 * Card信息id
	 */
	private String card_id;
	/**
	 * 银行编号
	 */
	private String bank_code;
	/**
	 * 银行卡号
	 */
	private String bank_account_no;
	/**
	 * 户名
	 */
	private String account_name;
	/**
	 * 卡类型
	 */
	private String card_type;
	/**
	 * 卡属性
	 */
	private String card_attribute;
	
	
	/**
	 * 是否已认证
	 */
	private String need_confirm;
	/**
	 *创建时间
	 */
	private String create_time;
	
	
	public String getCard_id() {
		return card_id;
	}
	public void setCard_id(String card_id) {
		this.card_id = card_id;
	}
	public String getBank_code() {
		return bank_code;
	}
	public void setBank_code(String bank_code) {
		this.bank_code = bank_code;
	}
	public String getBank_account_no() {
		return bank_account_no;
	}
	public void setBank_account_no(String bank_account_no) {
		this.bank_account_no = bank_account_no;
	}
	public String getAccount_name() {
		return account_name;
	}
	public void setAccount_name(String account_name) {
		this.account_name = account_name;
	}
	public String getCard_type() {
		return card_type;
	}
	public void setCard_type(String card_type) {
		this.card_type = card_type;
	}
	public String getCard_attribute() {
		return card_attribute;
	}
	public void setCard_attribute(String card_attribute) {
		this.card_attribute = card_attribute;
	}
	public String getNeed_confirm() {
		return need_confirm;
	}
	public void setNeed_confirm(String need_confirm) {
		this.need_confirm = need_confirm;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	
	
}
