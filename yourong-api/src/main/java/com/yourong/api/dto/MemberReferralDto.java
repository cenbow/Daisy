package com.yourong.api.dto;

import java.util.Date;

import org.codehaus.jackson.map.annotate.JsonFilter;

import com.alibaba.fastjson.annotation.JSONField;
import com.yourong.common.util.StringUtil;

public class MemberReferralDto {
	
	/**
	 * 用户名
	 */
	@JSONField(serialize=false)
	private String username;
	
	/**
	 * 手机号
	 */
	@JSONField(serialize=false)
	private Long mobile;
	
	/**
	 * 注册时间
	 */
	private Date referralRegisterTime;
	
	/**
	 * 是否开通新浪存钱罐
	 */
	@JSONField(serialize=false)
	private boolean isSavingPotOpen = false;
	
	/**
	 * 是否邮箱绑定
	 */
	@JSONField(serialize=false)
	private boolean isEmailBind = false;
	
	/**
	 * 是否投资
	 */
	@JSONField(serialize=false)
	private boolean isInvestment = false;
	
	/**
	 * 贡献人气值
	 */
	private int contributePopularityValue = 0;

	public Date getReferralRegisterTime() {
		return referralRegisterTime;
	}

	public void setReferralRegisterTime(Date referralRegisterTime) {
		this.referralRegisterTime = referralRegisterTime;
	}

	public boolean isSavingPotOpen() {
		return isSavingPotOpen;
	}

	public void setSavingPotOpen(boolean isSavingPotOpen) {
		this.isSavingPotOpen = isSavingPotOpen;
	}

	public boolean isEmailBind() {
		return isEmailBind;
	}

	public void setEmailBind(boolean isEmailBind) {
		this.isEmailBind = isEmailBind;
	}

	public boolean isInvestment() {
		return isInvestment;
	}

	public void setInvestment(boolean isInvestment) {
		this.isInvestment = isInvestment;
	}

	public int getContributePopularityValue() {
		return contributePopularityValue;
	}

	public void setContributePopularityValue(int contributePopularityValue) {
		this.contributePopularityValue = contributePopularityValue;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Long getMobile() {
		return mobile;
	}

	public void setMobile(Long mobile) {
		this.mobile = mobile;
	}
	
	public String getMaskUsername() {
//		if(StringUtil.isNotBlank(username)) {
//			return StringUtil.maskString(username, StringUtil.ASTERISK, 1, 1, 4);
//		} else {
			return StringUtil.maskString(mobile.toString(), StringUtil.ASTERISK, 3, 4, 4);
//		}
	}
}
