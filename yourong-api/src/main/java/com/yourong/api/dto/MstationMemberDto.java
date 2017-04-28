package com.yourong.api.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.yourong.common.annotation.EncryptionAnnotation;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.util.Date;

/**
 * Created by Administrator on 2015/7/9.
 */
public class MstationMemberDto {
    private Long id;

    @NotBlank(message = "10002")
    @JSONField(serialize = false)
    private String pngCode;

     @NotBlank(message = "10002")
    @JSONField(serialize = false)
    private String phonecode;


    public void setMobile(long mobile) {
        this.mobile = mobile;
    }

    public String getPngCode() {
        return pngCode;
    }

    public void setPngCode(String pngCode) {
        this.pngCode = pngCode;
    }

    public String getPhonecode() {
        return phonecode;
    }

    public void setPhonecode(String phonecode) {
        this.phonecode = phonecode;
    }

    /**用户名**/
    //@NotBlank(message="10011")
    @Size(min = 6, max = 16,message="{system.error.message}")
    private String username;

    /**手机号**/
//    @Min(value = 10000000000L,message="10014")
//    @Max(value = 90000000000L,message="10014")
    private long mobile;

    /**密码**/
    @NotBlank(message="10011")
    @Size(min = 6, max = 16,message="10011")
    @Pattern(regexp = "(?!^\\d+$)(?!^[a-zA-Z]+$)(?!^[_#@]+$).{6,16}",message="10014")
    @JSONField(serialize = false)
    private String password;




    /**生日**/
    @Past(message="10015")
    @DateTimeFormat( pattern = "yyyy-MM-dd")
    private Date birthday;

    /**0-冻结,1-未激活,2-已激活**/
    // private Integer status;

    /**真实姓名**/
    //@NotBlank
    @Size(min = 2, max = 32,message="10011")
    //@JSONField(serialize = false)
    private String trueName;

    /**身份证号码**/
    //@NotBlank
    @Size(min = 8, max = 32,message="10011")
    //@JSONField(serialize = false)
    private String identityNumber;

    /**会员类型**/
    // private String memberType;

    /**邮箱**/
    @Email
    private String email;

    /**唯一标识用户的短链接url**/
    private String shortUrl;

    /**个人头像url**/
    private String avatars;

    /**推荐人用户id**/
    @JSONField(serialize = false)
    private Long referral;

    /**推荐人用户手机号码**/
    @JSONField(serialize = false)
    @Min(value = 10000000000L,message="10014")
    @Max(value = 90000000000L,message="10014")
    private Long referralMobile;

    /**0, "注册来源为站点内注册";1, "注册来源为微博";2, "注册来源为QQ";3, "注册来源为后台管理员注册"**/
    // private Integer registerMethod;
    @JSONField(serialize = false)
    private Integer referSource;

    /**注册时间**/
    @JSONField(serialize = false)
    private Date registerTime;

    /**更新时间**/
    @JSONField(serialize = false)
    private Date updateTime;

    /****/
    @JSONField(serialize = false)
    @Length(max = 255)
    private String remarks;

    /**用户认证**/
    @JSONField(serialize = false)
    private MemberVerifyDto memberVerifyDto;

    /**推荐码**/
    private String referCode;

    /***设备ID***/
    @JSONField(serialize = false)
    private String device;

    /****设备型号**，比如三星，小米***/
    @JSONField(serialize = false)
    private  String equipment;

    /**登陆平台 1-android 2-ios*/
    @JSONField(serialize = false)
    private Integer loginSource;

    //注册来源码
    @JSONField(serialize = false)
    private String registerTraceSource;
    //注册来源码编号
    @JSONField(serialize = false)
    private String registerTraceNo;
    //注册IP
    @JSONField(serialize = false)
    private String ip;
    //都玩ID
    @JSONField(serialize = false)
    private String tid;

    /****地理位置***/
    @JSONField(serialize = false)
    private String position ;

    private  String channelId;

    @Min(value = 1,message="10002")
    private int checkType;

    public int getCheckType() {
        return checkType;
    }

    public void setCheckType(int checkType) {
        this.checkType = checkType;
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

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRegisterTraceSource() {
        return registerTraceSource;
    }

    public void setRegisterTraceSource(String registerTraceSource) {
        this.registerTraceSource = registerTraceSource;
    }

    public String getRegisterTraceNo() {
        return registerTraceNo;
    }

    public Integer getLoginSource() {
        return loginSource;
    }

    public void setLoginSource(Integer loginSource) {
        this.loginSource = loginSource;
    }

    public void setRegisterTraceNo(String registerTraceNo) {
        this.registerTraceNo = registerTraceNo;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }




    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
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



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getAvatars() {
        return avatars;
    }

    public void setAvatars(String avatars) {
        this.avatars = avatars;
    }

    public Long getReferral() {
        return referral;
    }

    public void setReferral(Long referral) {
        this.referral = referral;
    }



    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public MemberVerifyDto getMemberVerifyDto() {
        return memberVerifyDto;
    }

    public void setMemberVerifyDto(MemberVerifyDto memberVerifyDto) {
        this.memberVerifyDto = memberVerifyDto;
    }

    public Integer getReferSource() {
        return referSource;
    }

    public void setReferSource(Integer referSource) {
        this.referSource = referSource;
    }

    public String getReferCode() {
        return referCode;
    }

    public void setReferCode(String referCode) {
        this.referCode = referCode;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public Long getReferralMobile() {
        return referralMobile;
    }

    public void setReferralMobile(Long referralMobile) {
        this.referralMobile = referralMobile;
    }
}
