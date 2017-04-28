package com.yourong.web.dto;

import java.util.Date;

import javax.validation.constraints.Size;

import com.yourong.common.thirdparty.sinapay.common.enums.BankCode;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.validator.constraints.NotBlank;

import com.yourong.core.uc.model.Member;

public class MemberBankCardDto {	
	 /****/
    private Long id;

    /**会员id**/
    private Long memberId;

    /**银行卡号**/
    @Size(min = 15, max = 20,message="90027")
    @NotBlank(message="10008")
    private String cardNumber;   
    /***确认银行卡****/
    @Size(min = 15, max = 20,message="90027")
    @NotBlank(message="10008")
    private String checkCardNumber;

    /**持卡人**/
    @NotBlank(message="10008")
    private String cardHolder;

    /**银行唯一code**/
    @NotBlank(message="10008")
    private String bankCode;

    /**银行所属省份**/
    @NotBlank(message="10008")
    private String bankProvince;

    /**银行所属城市**/
    @NotBlank(message="10008")
    private String bankCity;

    /**银行所属所在区**/
    private String bankDistrict;

    /**开卡行所属开户行(分行)的名称**/
    private String branchName;

    /**开卡行所属支行的名称**/
    private String subBranchName;

    /**是否为默认银行卡(0-不是 1-是)**/
    private Integer isDefault;
	/**是否为安全卡(0-不是 1-是)**/
	private Integer isSecurity;
	/****银行预留手机号码***/
	private Long  bankMobile;
	/******卡类型，1一般卡，2快捷支付卡**/
	private int cardType;
	/**外部sourceId**/
	private String outerSourceId;

    /****/
    private Date createTime;

    /****/
    private Date updateTime;
    
    /**备注**/
    private String remarks;

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

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
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

	public Integer getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Integer isDefault) {
		this.isDefault = isDefault;
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
    
	public String getCheckCardNumber() {
		return checkCardNumber;
	}

	public void setCheckCardNumber(String checkCardNumber) {
		this.checkCardNumber = checkCardNumber;
	}

	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.DEFAULT_STYLE, false);
	}

	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

}
