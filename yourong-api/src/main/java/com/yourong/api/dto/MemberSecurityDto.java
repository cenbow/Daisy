package com.yourong.api.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.yourong.common.util.SerialNumberUtil;
import com.yourong.common.util.StringUtil;

public class MemberSecurityDto {
	
	@JSONField(serialize=false)
	public Long memberId;

	/**手机号**/    
    private String mobile;
    
    /**姓名**/
    @JSONField(name="name")
    private String trueName;
    
    /**身份证号**/
    @JSONField(serialize=false)
    private String identityNumber;
    
    /**银行卡号**/
    @JSONField(serialize=false)
    private String bankNumber;
    
    /**邮箱**/
    private String email;
    
    /**是否开通安全卡**/
    private boolean isSecurity;
    
    /**是否绑定邮箱**/
    private boolean isBindEmail;
    
    /**是否完善信息**/
    private boolean isCompletedInfo;
    
    /**是否委托授权**/
    private boolean isWithholdAuthority;
    
    /**是否设置支付密码**/
    private boolean isPayPassword;

	public String getMobile() {
		/*if(StringUtil.isNotBlank(mobile)){
			return StringUtil.maskMobileCanNull(mobile);
		}*/
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getTrueName() {
		return trueName;
	}

	public void setTrueName(String trueName) {
		this.trueName = trueName;
	}

	public String getIdentityNumber() {
		return StringUtil.maskIdentityNumber(identityNumber);
	}

	public void setIdentityNumber(String identityNumber) {
		this.identityNumber = identityNumber;
	}

	public String getBankNumber() {
		return StringUtil.maskBankCodeNumber(bankNumber);
	}

	public void setBankNumber(String bankNumber) {
		this.bankNumber = bankNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	
    
	public boolean getIsSecurity() {
		return isSecurity;
	}

	public void setSecurity(boolean isSecurity) {
		this.isSecurity = isSecurity;
	}

	/**
	 * 是否实名验证
	 * @return
	 */
	public boolean getIsVerifyTrueName(){
		if(StringUtil.isNotBlank(trueName) && StringUtil.isNotBlank(identityNumber)){
			return true;
		}
		return false;
	}
	
	/**
	 * 是否绑定银行卡
	 * @return
	 */
	@JSONField(serialize=false)
	public boolean isBindBank(){
		if(StringUtil.isNotBlank(bankNumber)){
			return true;
		}
		return false;
	}
	
	/**
	 * 是否绑定邮箱
	 * @return
	 */
	public boolean getIsBindEmail(){
		return isBindEmail;
	}
	
	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	/**
	 * 新浪ID
	 * @return
	 */
	public String getMemberSinaId(){
		if(getMemberId() != null){
			return SerialNumberUtil.PREFIX_UID+getMemberId();
		}
		return null;
	}

	public boolean getIsCompletedInfo() {
		return isCompletedInfo;
	}

	public void setCompletedInfo(boolean isCompletedInfo) {
		this.isCompletedInfo = isCompletedInfo;
	}

	public void setBindEmail(boolean isBindEmail) {
		this.isBindEmail = isBindEmail;
	}

	/**
	 * @return the isWithholdAuthority
	 */
	public boolean getIsWithholdAuthority() {
		return isWithholdAuthority;
	}

	/**
	 * @param isWithholdAuthority the isWithholdAuthority to set
	 */
	public void setWithholdAuthority(boolean isWithholdAuthority) {
		this.isWithholdAuthority = isWithholdAuthority;
	}

	/**
	 * @return the isPayPassword
	 */
	public boolean getIsPayPassword() {
		return isPayPassword;
	}

	/**
	 * @param isPayPassword the isPayPassword to set
	 */
	public void setPayPassword(boolean isPayPassword) {
		this.isPayPassword = isPayPassword;
	}
    
	
}
