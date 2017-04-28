package com.yourong.api.dto;

import com.yourong.common.annotation.EncryptionAnnotation;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

/**
 * Created by Administrator on 2015/7/9.
 */
public class MstationMemberAuthDto {


    private Long memberID;

    private long mobile;

    /**
     * 真实姓名*
     */
    @NotBlank(message = "10008")
    @Size(min = 2, max = 32, message = "10011")
    private String trueName;

    /**
     * 身份证号码*
     */
    @NotBlank(message = "10008")
    @Size(min = 8, max = 32, message = "10011")
    private String identityNumber;

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
}
