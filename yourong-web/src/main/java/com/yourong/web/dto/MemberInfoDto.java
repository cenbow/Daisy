package com.yourong.web.dto;

import com.yourong.common.util.StringUtil;

public class MemberInfoDto {
	/****/
    private Long id;

    /**用户id**/
    private Long memberId;

    /**所在身份**/
    private String province;

    /**所在城市**/
    private String city;

    /**详细地址**/
    private String address;

    /**邮编**/
    private Integer postalCode;

    /**qq号**/
    private Long qq;

    /**职业**/
    private String occupation;

    /**婚姻状况**/
    private String marriage;

    /**月收入**/
    private String income;

    /**最高学历**/
    private String highEdu;

    /**毕业学校**/
    private String school;
    
    /**0:男 1:女**/   
    private Integer sex;
    
    /**生日**/
    private String birthday;
    
    /**省市区名称**/
    private String areaFullName;
    
    /**户籍地**/
    private String censusRegisterName;
    /**户籍地 ID**/
    private String censusRegisterId;

    private Integer evaluationScore;
    
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

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(Integer postalCode) {
		this.postalCode = postalCode;
	}

	public Long getQq() {
		return qq;
	}

	public void setQq(Long qq) {
		this.qq = qq;
	}

	public String getOccupation() {
		return occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	public String getMarriage() {
		return marriage;
	}

	public void setMarriage(String marriage) {
		this.marriage = marriage;
	}

	public String getIncome() {
		return income;
	}

	public void setIncome(String income) {
		this.income = income;
	}

	public String getHighEdu() {
		return highEdu;
	}

	public void setHighEdu(String highEdu) {
		this.highEdu = highEdu;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
    
	public String getAreaFullName() {
		return areaFullName;
	}

	public void setAreaFullName(String areaFullName) {
		this.areaFullName = areaFullName;
	}
	
	public String getCensusRegisterName() {
		return censusRegisterName;
	}

	public void setCensusRegisterName(String censusRegisterName) {
		this.censusRegisterName = censusRegisterName;
	}

	public String getCensusRegisterId() {
		return censusRegisterId;
	}

	public void setCensusRegisterId(String censusRegisterId) {
		this.censusRegisterId = censusRegisterId;
	}

	public Integer getEvaluationScore() {
		return evaluationScore;
	}

	public void setEvaluationScore(Integer evaluationScore) {
		this.evaluationScore = evaluationScore;
	}

	/**
	 * 获得用户完整信息
	 * @return
	 */
	public String getFullAddress(){
		StringBuffer sb = new StringBuffer();
		if(StringUtil.isNotBlank(getAreaFullName())){
			sb.append(getAreaFullName());
		}
		if(StringUtil.isNotBlank(getAddress())){
			sb.append(getAddress());
		}
		return sb.toString();
	}
}
