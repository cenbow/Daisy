package com.yourong.core.uc.model;

import java.util.Date;

import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.StringUtil;

public class Member extends AbstractBaseObject{
    /**
	 * 
	 */
   private static final long serialVersionUID = 5746379109990570174L;

	/**user table**/
    private Long id;

    /**用户名**/
    @NotBlank
    @Size(min = 6, max = 16)
    private String username;

    /**手机号**/    
    private Long mobile;

    /**密码**/
    @NotBlank
    @Size(min = 8, max = 32)
    private String password;

    /**0:男 1:女**/   
    private Integer sex;

    /**生日**/
    @Past
    @DateTimeFormat( pattern = "yyyy-MM-dd")
    private Date birthday;

    /**0-冻结,1-未激活,2-已激活**/
    private Integer status;

    /**真实姓名**/
    @NotBlank
    @Size(min = 8, max = 32)
    private String trueName;

    /**身份证号码**/
    @NotBlank
    @Size(min = 8, max = 32)
    private String identityNumber;

    /**会员类型**/
    private Integer memberType;

    /**邮箱**/
    @Email
    private String email;

    /**唯一标识用户的短链接url**/
    private String shortUrl;

    /**个人头像url**/
    private String avatars;

    /**推荐人用户id**/
    private Long referral;
    
    /**推荐方**/
	private String recommend;

    /**0, "注册来源为站点内注册";1, "注册来源为微博";2, "注册来源为QQ";3, "注册来源为后台管理员注册"**/
    private Integer registerMethod;

    /**注册时间**/    
    private Date registerTime;

    /**更新时间**/
    private Date updateTime;

    /****/
    @Length(max = 255)
    private String remarks;

    /**删除标记**/
    private Integer delFlag;
    //注册来源码
    private String registerTraceSource;
    //注册来源码编号
    private String registerTraceNo;
    

	/**
	 * 设置支付密码标记（0-未设置（默认）；1-已设置）
	 */
	private Integer payPasswordFlag;
	
	/**
	 * 委托扣款授权（0否，1是）
	 */
	private Integer withholdAuthorityFlag;

    /**签署方式  0手动，1自动**/
    private Integer signWay;
    /**是否CA认证（0否，1是）**/
    private Integer isAuth;
    //CA认证编号
    private String caNo;
    
    /**
     * 注册IP
     */
    private String registerIp;
    

    /**会员详细信息*/
    private MemberInfo memberInfo;
    
    
    private String channelBusiness;

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
        this.username = username == null ? null : username.trim();
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
        this.password = password == null ? null : password.trim();
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTrueName() {
        return trueName;
    }

    public void setTrueName(String trueName) {
        this.trueName = trueName == null ? null : trueName.trim();
    }

    public String getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(String identityNumber) {
        this.identityNumber = identityNumber == null ? null : identityNumber.trim();
    }

    public Integer getMemberType() {
        return memberType;
    }

    public void setMemberType(Integer memberType) {
        this.memberType = memberType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl == null ? null : shortUrl.trim();
    }

    public String getAvatars() {
        return avatars;
    }

    public void setAvatars(String avatars) {
        this.avatars = avatars == null ? null : avatars.trim();
    }

    public Long getReferral() {
        return referral;
    }

    public void setReferral(Long referral) {
        this.referral = referral;
    }

    public String getRecommend() {
		return recommend;
	}

	public void setRecommend(String recommend) {
		this.recommend = recommend;
	}

	public Integer getRegisterMethod() {
        return registerMethod;
    }

    public void setRegisterMethod(Integer registerMethod) {
        this.registerMethod = registerMethod;
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
        this.remarks = remarks == null ? null : remarks.trim();
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
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

	public void setRegisterTraceNo(String registerTraceNo) {
		this.registerTraceNo = registerTraceNo;
	}

	public MemberInfo getMemberInfo() {
		return memberInfo;
	}

	public void setMemberInfo(MemberInfo memberInfo) {
		this.memberInfo = memberInfo;
	}
	
	public boolean isEmailBind() {
		if(StringUtil.isNotBlank(email)) {
			return true;
		} else 
			return false;
	}
	
	public boolean isSavingPotOpen() {
		if(StringUtil.isNotBlank(trueName)
				&& StringUtil.isNotBlank(identityNumber)) {
			return true;
		} else
			return false;
	}
	
	public String maskIdentityNumber(){
		if(StringUtil.isNotBlank(getIdentityNumber())){
			return StringUtil.maskIdentityNumber(getIdentityNumber());
		}
		return getIdentityNumber();
	}
	
	public int getAge(){
		if(getBirthday() != null){
			int age = DateUtils.getYear(DateUtils.getCurrentDate()) - DateUtils.getYear(getBirthday());
			return age;
		}
		return 0;
	}
	
	public Integer getPayPasswordFlag() {
		return payPasswordFlag;
	}

	public void setPayPasswordFlag(Integer payPasswordFlag) {
		this.payPasswordFlag = payPasswordFlag;
	}

	public Integer getWithholdAuthorityFlag() {
		return withholdAuthorityFlag;
	}

	public void setWithholdAuthorityFlag(Integer withholdAuthorityFlag) {
		this.withholdAuthorityFlag = withholdAuthorityFlag;
	}

	public String getMaskTrueName(){
		if(StringUtil.isNotBlank(trueName)){
			return StringUtil.maskTrueName(trueName);
		}
		return "";
	}

	/**
	 * @return the signWay
	 */
	public Integer getSignWay() {
		return signWay;
	}

	/**
	 * @param signWay the signWay to set
	 */
	public void setSignWay(Integer signWay) {
		this.signWay = signWay;
	}

	/**
	 * @return the isAuth
	 */
	public Integer getIsAuth() {
		return isAuth;
	}

	/**
	 * @param isAuth the isAuth to set
	 */
	public void setIsAuth(Integer isAuth) {
		this.isAuth = isAuth;
	}

	/**
	 * @return the caNo
	 */
	public String getCaNo() {
		return caNo;
	}

	/**
	 * @param caNo the caNo to set
	 */
	public void setCaNo(String caNo) {
		this.caNo = caNo;
	}

	public String getRegisterIp() {
		return registerIp;
	}

	public void setRegisterIp(String registerIp) {
		this.registerIp = registerIp;
	}

	public String getChannelBusiness() {
		return channelBusiness;
	}

	public void setChannelBusiness(String channelBusiness) {
		this.channelBusiness = channelBusiness;
	}
	
}