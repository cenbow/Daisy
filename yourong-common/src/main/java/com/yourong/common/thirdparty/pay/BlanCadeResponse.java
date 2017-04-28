package com.yourong.common.thirdparty.pay;

public class BlanCadeResponse extends PayResponse{
	
	private static final long serialVersionUID = 5961812468641253382L;
	
	//钱包系统卡ID
	private String card_id;
	
	//银行卡是否已认证，Y：已认证；N：未认证
	private String is_verified;
	
	
	public String getCard_id() {
		return card_id;
	}
	public void setCard_id(String card_id) {
		this.card_id = card_id;
	}
	public String getIs_verified() {
		return is_verified;
	}
	public void setIs_verified(String is_verified) {
		this.is_verified = is_verified;
	}


}
