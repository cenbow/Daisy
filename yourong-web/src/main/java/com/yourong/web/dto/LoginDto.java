package com.yourong.web.dto;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

public class LoginDto {

    @NotBlank
    @Size(min = 2, max = 16, message = "10014")
    private String username;

 
    @NotBlank
    @Size(min = 6, max = 16,message="10011")
    @Pattern(regexp = "(?!^\\d+$)(?!^[a-zA-Z]+$)(?!^[_#@]+$).{6,16}",message="10014")
    private String password;

    /** 用户id **/
    private Long memberId;
    
    private String  pngCode;
    /** 登录ip **/
    private String loginIp;

    /** 登录类型 0-用户名登陆 ；1-手机登陆 2-邮箱登陆 **/
    private Integer loginType;

    /** 登录来源 0-pc 1-android 2-ios 3-mobile **/
    private Integer loginSource;
    
    /**用户代理信息**/
    private String userAgent;

    /** 设备序列号 **/
    private String device;

    //微信ID
    private String weixinId;

    public String getWeixinId() {
        return weixinId;
    }

    public void setWeixinId(String weixinId) {
        this.weixinId = weixinId;
    }

    public String getPngCode() {
        return pngCode;
    }

    public void setPngCode(String pngCode) {
        this.pngCode = pngCode;
    }

    public String getUsername() {
	return username;
    }

    public void setUsername(String username) {
	this.username = username;
    }

    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = password;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public Integer getLoginType() {
        return loginType;
    }

    public void setLoginType(Integer loginType) {
        this.loginType = loginType;
    }

    public Integer getLoginSource() {
        return loginSource;
    }

    public void setLoginSource(Integer loginSource) {
        this.loginSource = loginSource;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

}
