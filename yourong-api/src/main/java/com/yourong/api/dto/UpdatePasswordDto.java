package com.yourong.api.dto;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import com.yourong.common.annotation.EncryptionAnnotation;

public class UpdatePasswordDto {
	
	@EncryptionAnnotation
	@NotBlank
	@Size(min = 6, max = 16, message = "10011")
	@Pattern(regexp = "(?!^\\d+$)(?!^[a-zA-Z]+$)(?!^[_#@]+$).{6,16}", message = "10014")
	private String oldPassword;

	@EncryptionAnnotation
	@NotBlank
	@Size(min = 6, max = 16, message = "10011")
	@Pattern(regexp = "^(?=.*?[a-zA-Z])(?=.*?[0-9])[a-zA-Z0-9\\S]{6,16}$", message = "10014")
	private String password;

	@EncryptionAnnotation
	@NotBlank
	@Size(min = 6, max = 16, message = "10011")
	@Pattern(regexp = "^(?=.*?[a-zA-Z])(?=.*?[0-9])[a-zA-Z0-9\\S]{6,16}$", message = "10014")
	private String checkNewPassword;

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCheckNewPassword() {
		return checkNewPassword;
	}

	public void setCheckNewPassword(String checkNewPassword) {
		this.checkNewPassword = checkNewPassword;
	}
}
