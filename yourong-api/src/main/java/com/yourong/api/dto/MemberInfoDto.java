package com.yourong.api.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.yourong.common.util.StringUtil;

public class MemberInfoDto {
	/****/
	@JSONField(serialize=false)
    private Long id;

    @JSONField(serialize=false)
    /**用户id**/
    private Long memberId;

    /**所在身份**/
    @JSONField(serialize=false)
    private String province;

    /**所在城市**/
    private String city;

    /**详细地址**/
    private String address;

    /**邮编**/
    @JSONField(serialize=false)
    private Integer postalCode;

    /**qq号**/
    @JSONField(serialize=false)
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
    @JSONField(serialize=false)
    private String school;
    
    /**0:男 1:女**/   
    private Integer sex;
    
    /**生日**/
    private String birthday;
    
    /**省市区名称**/
    @JSONField(serialize=false)
    private String areaFullName;
    
    /**户籍地**/
    @JSONField(serialize=false)
    private String censusRegisterName;
    
    /**户籍地 ID**/
    private String censusRegisterId;

    private Long infoId;
    
    private Integer evaluationScore;
    
   	public Long getInfoId() {
   		if(getId() != null){
   			infoId = id;
   		}
   		return infoId;
   	}

   	public void setInfoId(Long infoId) {
   		this.infoId = infoId;
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

	private String getFormatArea(String area){
		if(StringUtil.isNotBlank(area)){
			StringBuilder str = new StringBuilder(area);
			int j = 0;
			for(int i = 0; i < area.length(); i++){
				String targetWord = String.valueOf(area.charAt(i));
				if(targetWord.equals("省") || targetWord.equals("市") || targetWord.equals("区")){
					j = j+1;
					str = str.insert(i+j, "|");
				}
			}
			String s = str.toString();
			if(s.endsWith("|")){
				return s.substring(0, s.length()-1);
			}
			return s;
		}
		return area;
	}
	
	public String getFormatAreaFullName(){
		return getFormatArea(getAreaFullName());
	}

	public String getFormatCensusRegisterName(){
		return getFormatArea(getCensusRegisterName());
	}
}
