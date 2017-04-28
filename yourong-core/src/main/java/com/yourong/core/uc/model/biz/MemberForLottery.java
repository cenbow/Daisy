package com.yourong.core.uc.model.biz;

import java.util.Date;

import com.yourong.common.util.StringUtil;

/**
 * 用户注册送彩票biz类
 * @author Leon Ray
 * 2014年12月16日-上午10:45:17
 */
public class MemberForLottery{
	
    private Long memberId;

    /**用户名**/
    private String username;

    /**手机号**/    
    private Long mobile;

    /**个人头像url**/
    private String avatars;
    
    private Date registerTime;
    
    private String maskUsername;

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public String getUsername() {
		if(StringUtil.isNotBlank(username)
				||mobile!=null) {
			return StringUtil.maskUserNameOrMobile(username, mobile);
		} else {
			return null;
		}
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

	public String getAvatars() {
		return avatars;
	}

	public void setAvatars(String avatars) {
		this.avatars = avatars;
	}

	public Date getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(Date registerTime) {
		this.registerTime = registerTime;
	}

	public String getMaskUsername() {
		return maskUsername;
	}

	public void setMaskUsername(String maskUsername) {
		this.maskUsername = maskUsername;
	}
    
    

}