package com.yourong.api.dto;

import com.yourong.common.util.StringUtil;

public class MemberNonSecurityBankCardDto {
	private Long id;
	
	/**银行卡号**/
    private String cardNumber;
    
    /**银行唯一code**/
    private String bankCode;
    
    /******卡类型，1一般卡，2快捷支付卡**/
	private int cardType;
	
	private Long bankMobile;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public int getCardType() {
		return cardType;
	}

	public void setCardType(int cardType) {
		this.cardType = cardType;
	}
	
	public Long getBankMobile() {
		return bankMobile;
	}

	public void setBankMobile(Long bankMobile) {
		this.bankMobile = bankMobile;
	}
	
//	public String getMobile(){
//		return StringUtil.maskMobileCanNull(getBankMobile());
//	}
	public String getCardNumberMask() {
		if(StringUtil.isNotBlank(this.cardNumber)) {
    		return StringUtil.maskBankCodeNumberEnd(this.cardNumber);
    	}
    	return "";
	}
    
}
