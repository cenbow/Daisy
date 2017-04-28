package com.yourong.web.dto;

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

/**
 * 存放在session里的member
 * @author Administrator
 *
 */
public class MemberSessionDto extends AbstractBaseObject {
	
    private static final long serialVersionUID = 2580230280254710583L;

    private Long id;

    /**用户名**/
    @NotBlank
    @Size(min = 6, max = 16,message="10004")
    private String username;

    /**手机号**/    
    private Long mobile;    

    /**0:男 1:女**/   
    private Integer sex;

    /**生日**/
    @Past
    @DateTimeFormat( pattern = "yyyy-MM-dd")
    private Date birthday;

    /**真实姓名**/
    @NotBlank
    @Size(min = 8, max = 32)
    private String trueName;

    /**身份证号码**/
    @NotBlank
    @Size(min = 8, max = 32)
    private String identityNumber;

    /**会员类型**/
    private String memberType;

    /**邮箱**/
    @Email
    private String email;

    /**唯一标识用户的短链接url**/
    private String shortUrl;

    /**个人头像url**/
    private String avatars;

    /**推荐人用户id**/
    private Long referral;

    /**0, "注册来源为站点内注册";1, "注册来源为微博";2, "注册来源为QQ";3, "注册来源为后台管理员注册"**/
    private Integer registerMethod;

    /**注册时间**/    
    private Date registerTime;

    /**更新时间**/
    private Date updateTime;


    /****/
    @Length(max = 255)
    private String remarks;
	//注册来源码
	private String registerTraceSource;
	//注册来源码编号
	private String registerTraceNo;
	//注册IP
	private String ip;
	
	/** 登录来源 0-pc 1-android 2-ios 3-mobile **/
    private Integer loginSource;
    
    /**用户代理信息**/
    private String userAgent;

	/**
	 * 用户是否投资过， DSP 统计用
	 */
	private Boolean isMemberInvested;
	
	/**是否生日**/
	private boolean isBirthday;
	
	/**是否债权**/
	private boolean isDisplayDebt;
	
	/**是否显示项目收放款**/
	private boolean isDisplayProjectMoney;
	
	/**是否显示垫资还款**/
	private boolean isDisplayLoanRepayment;
	
	/**委托扣款授权（0否，1是）**/
	private Integer withholdAuthorityFlag;
	
	/**
	 * 设置支付密码标记（0-未设置（默认）；1-已设置）
	 */
	private Integer payPasswordFlag;

	public Boolean getIsMemberInvested() {
		return isMemberInvested;
	}

	public void setIsMemberInvested(Boolean isMemberInvested) {
		this.isMemberInvested = isMemberInvested;
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

	public Integer getSex() {
		return sex;
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

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
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

	
	public String getTrueName() {
		return trueName;
	}

	public void setTrueName(String trueName) {
		this.trueName = trueName;
	}

	public String getIdentityNumber() {
		return identityNumber;
	}
	
	public String getMaskIdentityNumber() {
		return StringUtil.maskString(identityNumber, StringUtil.ASTERISK, 6, 4, 8);
	}

	public void setIdentityNumber(String identityNumber) {
		this.identityNumber = identityNumber;
	}

	public String getMemberType() {
		return memberType;
	}

	public void setMemberType(String memberType) {
		this.memberType = memberType;
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
		this.remarks = remarks;
	}    
	
	public boolean isEmailBind() {
		if(StringUtil.isNotBlank(email)) {
			return true;
		} else {
			return false;
		}
	}

	public Integer getLoginSource() {
		return loginSource;
	}

	public void setLoginSource(Integer loginSource) {
		this.loginSource = loginSource;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	
	public boolean getIsBirthday() {
		if(birthday == null){
			return false;
		}
		Date date = DateUtils.getCurrentDate();
		int c_month = DateUtils.getMonth(date);
		int c_day = DateUtils.getDate(date);
		int b_month = DateUtils.getMonth(birthday);
		int b_day = DateUtils.getDate(birthday);
		if(c_month == b_month && c_day == b_day){
			return true;
		}
		return false;
	}

	/**
	 * @return the isDisplayDebt
	 */
	public boolean isDisplayDebt() {
		return isDisplayDebt;
	}

	/**
	 * @param isDisplayDebt the isDisplayDebt to set
	 */
	public void setDisplayDebt(boolean isDisplayDebt) {
		this.isDisplayDebt = isDisplayDebt;
	}

	/**
	 * @return the isDisplayProjectMoney
	 */
	public boolean isDisplayProjectMoney() {
		return isDisplayProjectMoney;
	}

	/**
	 * @param isDisplayProjectMoney the isDisplayProjectMoney to set
	 */
	public void setDisplayProjectMoney(boolean isDisplayProjectMoney) {
		this.isDisplayProjectMoney = isDisplayProjectMoney;
	}

	/**
	 * @return the isDisplayLoanRepayment
	 */
	public boolean isDisplayLoanRepayment() {
		return isDisplayLoanRepayment;
	}

	/**
	 * @param isDisplayLoanRepayment the isDisplayLoanRepayment to set
	 */
	public void setDisplayLoanRepayment(boolean isDisplayLoanRepayment) {
		this.isDisplayLoanRepayment = isDisplayLoanRepayment;
	}

	public Integer getWithholdAuthorityFlag() {
		return withholdAuthorityFlag;
	}

	public void setWithholdAuthorityFlag(Integer withholdAuthorityFlag) {
		this.withholdAuthorityFlag = withholdAuthorityFlag;
	}

	public Integer getPayPasswordFlag() {
		return payPasswordFlag;
	}

	public void setPayPasswordFlag(Integer payPasswordFlag) {
		this.payPasswordFlag = payPasswordFlag;
	}
	
}
