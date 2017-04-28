package com.yourong.api.dto;

import java.util.Date;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import com.alibaba.fastjson.annotation.JSONField;
import com.yourong.common.annotation.EncryptionAnnotation;
import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.thirdparty.sinapay.common.enums.BankCode;
import com.yourong.common.util.StringUtil;
import com.yourong.core.uc.model.Member;

public class MemberBankCardDto  extends AbstractBaseObject{
	 /****/
    private Long id;

    /**银行卡号**/
   @EncryptionAnnotation
    @Size(min = 15, max = 20,message="90027")
    @NotBlank(message="10008")
    private String cardNumber;


    /**持卡人**/
    //@NotBlank(message="10008")
	@JSONField(serialize = false)
    private String cardHolder;

    /**银行唯一code**/
    @NotBlank(message="10008")
    private String bankCode;

    /**银行所属省份**/
    @NotBlank(message="10008")
	@JSONField(serialize = false)
    private String bankProvince;

    /**银行所属城市**/
    @NotBlank(message="10008")
	@JSONField(serialize = false)
    private String bankCity;

    /**银行所属所在区**/
	@JSONField(serialize = false)
    private String bankDistrict;

    /**开卡行所属开户行(分行)的名称**/
	@JSONField(serialize = false)
    private String branchName;

    /**开卡行所属支行的名称**/
	@JSONField(serialize = false)
    private String subBranchName;

	/**是否为安全卡(0-不是 1-是)**/
	private Integer isSecurity;

	/****银行预留手机号码***/
	//@JSONField(serialize = false)
	private Long  bankMobile;
	/******卡类型，1一般卡，2快捷支付卡**/
	private int cardType;
	/**外部sourceId**/
	@JSONField(serialize = false)
	private String outerSourceId;

	/**新浪反馈ticket**/
	@JSONField(serialize = false)
	private String ticket;
	/**短信验证码**/
	@JSONField(serialize = false)
	private String validCode;
	/****/
	@JSONField(serialize = false)
    private Date createTime;

    /****/
	@JSONField(serialize = false)
    private Date updateTime;
    
    /**备注**/
	@JSONField(serialize = false)
    private String remarks;

	@JSONField(serialize = false)
    private Member member;
	

	
	public Integer getIsSecurity() {
		return isSecurity;
	}

	public void setIsSecurity(Integer isSecurity) {
		this.isSecurity = isSecurity;
	}


	public String getOuterSourceId() {
		return outerSourceId;
	}

	public void setOuterSourceId(String outerSourceId) {
		this.outerSourceId = outerSourceId;
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

	public String getCardHolder() {
		return cardHolder;
	}

	public void setCardHolder(String cardHolder) {
		this.cardHolder = cardHolder;
	}

	public String getBankCode() {
		return bankCode;
	}
	public String getBankCodeRemaks(){
		return BankCode.getBankCode(bankCode).getRemarks();
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

	public String getBankDistrict() {
		return bankDistrict;
	}

	public void setBankDistrict(String bankDistrict) {
		this.bankDistrict = bankDistrict;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getSubBranchName() {
		return subBranchName;
	}

	public void setSubBranchName(String subBranchName) {
		this.subBranchName = subBranchName;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}


	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public String getValidCode() {
		return validCode;
	}

	public void setValidCode(String validCode) {
		this.validCode = validCode;
	}
	
	public String getCardNumberMask() {
		if(StringUtil.isNotBlank(this.cardNumber)) {
    		return StringUtil.maskFormatBankCodeNumberWithBlank(this.cardNumber,4);
    	}
    	return "";
	}


}
