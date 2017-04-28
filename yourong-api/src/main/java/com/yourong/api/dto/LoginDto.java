package com.yourong.api.dto;

import com.yourong.common.annotation.EncryptionAnnotation;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class LoginDto {

    @EncryptionAnnotation
    @NotBlank(message="10008")
    @Size(min = 2, max = 16, message = "10014")

    private String username;

    @EncryptionAnnotation
    @NotBlank(message="10008")
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

    /** 登录来源 0-pc 1-android 2-ios 3-wap **/
    @NotNull(message = "10008")
    private Integer loginSource;

    /** 设备序列号 **/
    @NotBlank(message="10008")
    private String device;

    /****设备型号**，比较三星，小米***/
    @NotBlank(message="10008")
    private String equipment;

    /****地理位置***/
    private String position ;

        /**通道ID**/
    private String channelId;

    public String getEquipment() {
        return equipment;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
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

}
