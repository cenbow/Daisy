package com.yourong.web.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.yourong.web.utils.ServletUtil;

public class ActivityForRedFridayDto {
	/**
	 * 会员id
	 */
	@JSONField(serialize=false)
	private Long memberId;
	/**
	 * 用户名
	 */
	private String username;
	
	/**
	 * 手机号
	 */
	@JSONField(serialize=false)
	private Long mobile;
	
	/**头像**/
    private String avatars;
	
	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setMobile(Long mobile) {
		this.mobile = mobile;
	}
	
	public String getAvatars() {
		return ServletUtil.getMemberAvatarById(memberId);
	}

}
