package com.yourong.web.dto;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

public class MemberForm {
	

	
	@NotBlank
	@Size(min = 6, max = 16, message = "10011")
	@Pattern(regexp = "(?!^\\d+$)(?!^[a-zA-Z]+$)(?!^[_#@]+$).{6,16}", message = "10014")
	private String oldPassword;

	@NotBlank
	@Size(min = 6, max = 16, message = "10011")
	@Pattern(regexp = "(?!^\\d+$)(?!^[a-zA-Z]+$)(?!^[_#@]+$).{6,16}", message = "10014")
	private String newPassword;

	@NotBlank
	@Size(min = 6, max = 16, message = "10011")
	@Pattern(regexp = "(?!^\\d+$)(?!^[a-zA-Z]+$)(?!^[_#@]+$).{6,16}", message = "10014")
	private String checkNewPassword;

	// 验证码
//	private String code;
//
//	public String getCode() {
//		return code;
//	}
//
//	public void setCode(String code) {
//		this.code = code;
//	}

//	public Long getMobile() {
//		return mobile;
//	}
//
//	public void setMobile(Long mobile) {
//		this.mobile = mobile;
//	}
//
//	public Long getMemberID() {
//		return memberID;
//	}
//
//	public void setMemberID(Long memberID) {
//		this.memberID = memberID;
//	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getCheckNewPassword() {
		return checkNewPassword;
	}

	public void setCheckNewPassword(String checkNewPassword) {
		this.checkNewPassword = checkNewPassword;
	}

}
