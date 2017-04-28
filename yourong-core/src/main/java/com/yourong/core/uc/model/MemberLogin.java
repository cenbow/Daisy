package com.yourong.core.uc.model;

import java.util.Date;

public class MemberLogin {
    /****/
    private Long id;

    /**用户id**/
    private Long memberId;

    /**登录时间**/
    private Date loginTime;

    /**登录ip**/
    private String loginIp;

    /**登录类型 0-用户名登陆 ；1-手机登陆 2-邮箱登陆**/
    private Integer loginType;

    /**登录来源 0-pc 1-android  2-ios 3-mobile**/
    private Integer loginSource;
    
    /**用户代理信息**/
    private String userAgent;

    /**设备序列号**/
    private String device;
    /**经纬度**/
    private String position;

    /****/
    private Date createTime;

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
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

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp == null ? null : loginIp.trim();
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
        this.device = device == null ? null : device.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
}