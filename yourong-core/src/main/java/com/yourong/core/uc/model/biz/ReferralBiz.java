package com.yourong.core.uc.model.biz;

import java.util.Date;

import com.yourong.common.util.StringUtil;

/**
 * 前台被推荐用户biz
 * @author Leon Ray
 * 2014年10月28日-上午10:48:23
 */
public class ReferralBiz {

	/**
	 * 会员id
	 */
	private Long memberId;
	
	/**
	 * 被推荐用户
	 */
	private Long referralId;
	
	/**
	 * 用户名
	 */
	private String username;
	
	/**
	 * 手机号
	 */
	private Long mobile;
	
	/**
	 * 注册时间
	 */
	private String referralRegisterTime;
	
	/**
	 * 是否开通新浪存钱罐
	 */
	private boolean isSavingPotOpen = false;
	
	/**
	 * 是否邮箱绑定
	 */
	private boolean isEmailBind = false;
	
	/**
	 * 是否投资
	 */
	private boolean isInvestment = false;
	
	/**
	 * 贡献人气值
	 */
	private int contributePopularityValue = 0;

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public Long getReferralId() {
		return referralId;
	}

	public void setReferralId(Long referralId) {
		this.referralId = referralId;
	}

	public String getReferralRegisterTime() {
		return referralRegisterTime;
	}

	public void setReferralRegisterTime(String referralRegisterTime) {
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
		if(StringUtil.isNotBlank(username)) {
			return StringUtil.maskString(username, StringUtil.ASTERISK, 1, 1, 4);
		} else {
			return StringUtil.maskString(mobile.toString(), StringUtil.ASTERISK, 3, 2, 4);
		}
	}
	
}
