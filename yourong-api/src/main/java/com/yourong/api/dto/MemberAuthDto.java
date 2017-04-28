package com.yourong.api.dto;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import com.yourong.common.annotation.EncryptionAnnotation;

/**
 * 会员认证dto
 * Created by py on 2015/3/22.
 */
public class MemberAuthDto {


    private Long memberID;

    private long mobile;

    /**真实姓名**/
    @EncryptionAnnotation
    @NotBlank(message="10008")
    @Size(min = 2, max = 32,message="10011")
    private String trueName;

    /**身份证号码**/
    @EncryptionAnnotation
    @NotBlank(message="10008")
    @Size(min = 8, max = 32,message="10011")
    private String identityNumber;
    
    /** 请求IP **/
    private String clientIp;

    public Long getMemberID() {
        return memberID;
    }

    public void setMemberID(Long memberID) {
        this.memberID = memberID;
    }

    public long getMobile() {
        return mobile;
    }

    public void setMobile(long mobile) {
        this.mobile = mobile;
    }

    public String getTrueName() {
        return trueName;
    }

    public void setTrueName(String trueName) {
        this.trueName = trueName;
    }

    public String getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(String identityNumber) {
        this.identityNumber = identityNumber;
    }

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}
	
}
