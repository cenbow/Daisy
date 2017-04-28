package com.yourong.api.dto;

import org.codehaus.jackson.map.annotate.JsonFilter;

import com.alibaba.fastjson.annotation.JSONField;
import com.yourong.common.util.StringUtil;

public class SimpleMemberBankCardDto {
	
	private Long id;
	
	 /**银行卡号**/
	@JSONField(serialize=false)
    private String cardNumber;
    
    /**银行唯一code**/
    private String bankCode;

    /**银行所属省份**/
    private String bankProvince;

    /**银行所属城市**/
    private String bankCity;
    
    /******卡类型，1一般卡，2快捷支付卡**/
	private int cardType;

	/**是否为安全卡(0-不是 1-是)**/
	private Integer isSecurity;

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

	public String getBankProvince() {
		return bankProvince;
	}

	public void setBankProvince(String bankProvince) {
		this.bankProvince = bankProvince;
	}

	public String getBankCity() {
		return bankCity;
	}

	public void setBankCity(String bankCity) {
		this.bankCity = bankCity;
	}

	public int getCardType() {
		return cardType;
	}

	public void setCardType(int cardType) {
		this.cardType = cardType;
	}

	public Integer getIsSecurity() {
		return isSecurity;
	}

	public void setIsSecurity(Integer isSecurity) {
		this.isSecurity = isSecurity;
	}
	
	/**
	 * 需要掩码
	 * @return
	 */
	public String getBankCardNumber(){
		return StringUtil.maskString(getCardNumber(),StringUtil.ASTERISK, 5, 4, 6);
	}
	
	
}
