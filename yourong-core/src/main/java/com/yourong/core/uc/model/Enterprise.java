package com.yourong.core.uc.model;

import antlr.StringUtils;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.util.StringUtil;
import com.yourong.core.bsc.model.BscAttachment;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Past;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class Enterprise extends AbstractBaseObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**id**/
	private Long id;

	/**借款企业前台显示名称**/
	private String name;
	
	/**借款企业全称**/
	private String fullName;
	

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**工商营业执照注册号**/
	private String license;

	/**社会统一代码**/
	private String organizationCode;

	/**联系电话**/
	private String telephone;

	/**企业经营年限**/
	private Integer period;

	/**注册资本**/
	private Integer income;

	/**法人身份证号码**/
	private String identity;

	/**法人id**/
	private Long legalId;

	/**法人姓名**/
	private String legalName;

	/**创建时间**/
	private Date createTime;

	/**修改时间**/
	private Date updateTime;

	/**删除标记**/
	private Integer delFlage;

	/**地区**/
	private String region;

	/**详细地址**/
	private String address;

	/**注册日期**/
	@Past
	@DateTimeFormat( pattern = "yyyy-MM")
	private Date regeditDate;

	/** 信用额度**/
	private BigDecimal creditAmount;
	
	/**可用信用额度**/
	private BigDecimal availableCreditAmount;
	
	/**年销售额**/
	private BigDecimal yearSales;
	
	/**是否CA认证（0否，1是）**/
	private Integer isAuth;
	
	/**是否上传图章（0否，1是）**/
	private Integer isStamp;
	
	/**CA认证编号**/
	private String caNo;
	
	/**企业注册号**/
	private String regisNo;
	
	/**组织机构代码**/
	private String organizNo;
	
	/**税务登记证号**/
	private String taxNo;
	
	/**省份**/
	private String province;
	
	/**城市**/
	private String city;
	
	/**
	 * 企业图章
	 */
	private BscAttachment bscAttachment;
	
	private Integer type;
	
	
	 

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license == null ? null : license.trim();
	}

	public String getOrganizationCode() {
		return organizationCode;
	}

	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode == null ? null : organizationCode.trim();
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone == null ? null : telephone.trim();
	}

	public Integer getPeriod() {
		return period;
	}

	public void setPeriod(Integer period) {
		this.period = period;
	}

	public Integer getIncome() {
		return income;
	}

	public void setIncome(Integer income) {
		this.income = income;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity == null ? null : identity.trim();
	}

	public Long getLegalId() {
		return legalId;
	}

	public void setLegalId(Long legalId) {
		this.legalId = legalId;
	}

	public String getLegalName() {
		return legalName;
	}

	public void setLegalName(String legalName) {
		this.legalName = legalName == null ? null : legalName.trim();
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getDelFlage() {
		return delFlage;
	}

	public void setDelFlage(Integer delFlage) {
		this.delFlage = delFlage;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region == null ? null : region.trim();
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address == null ? null : address.trim();
	}

	public Date getRegeditDate() {
		return regeditDate;
	}

	public void setRegeditDate(Date regeditDate) {
		this.regeditDate = regeditDate;
	}

	public BigDecimal getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(BigDecimal creditAmount) {
		this.creditAmount = creditAmount;
	}

	public BigDecimal getAvailableCreditAmount() {
		return availableCreditAmount;
	}

	public void setAvailableCreditAmount(BigDecimal availableCreditAmount) {
		this.availableCreditAmount = availableCreditAmount;
	}

	public BigDecimal getYearSales() {
		return yearSales;
	}

	public void setYearSales(BigDecimal yearSales) {
		this.yearSales = yearSales;
	}

	public String getMaskOrganizationCode(){
		if(StringUtil.isNotBlank(organizationCode)){
			return StringUtil.maskString(organizationCode, StringUtil.ASTERISK, 3, 3);
		}
		return "";
	}
	
	public String getMaskLegalName(){
		if(StringUtil.isNotBlank(legalName)){
			return StringUtil.maskTrueName(legalName);
		}
		return "";
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
	 * @return the isStamp
	 */
	public Integer getIsStamp() {
		return isStamp;
	}

	/**
	 * @param isStamp the isStamp to set
	 */
	public void setIsStamp(Integer isStamp) {
		this.isStamp = isStamp;
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

	/**
	 * @return the regisNo
	 */
	public String getRegisNo() {
		return regisNo;
	}

	/**
	 * @param regisNo the regisNo to set
	 */
	public void setRegisNo(String regisNo) {
		this.regisNo = regisNo;
	}

	/**
	 * @return the organizNo
	 */
	public String getOrganizNo() {
		return organizNo;
	}

	/**
	 * @param organizNo the organizNo to set
	 */
	public void setOrganizNo(String organizNo) {
		this.organizNo = organizNo;
	}

	/**
	 * @return the taxNo
	 */
	public String getTaxNo() {
		return taxNo;
	}

	/**
	 * @param taxNo the taxNo to set
	 */
	public void setTaxNo(String taxNo) {
		this.taxNo = taxNo;
	}

	/**
	 * @return the province
	 */
	public String getProvince() {
		return province;
	}

	/**
	 * @param province the province to set
	 */
	public void setProvince(String province) {
		this.province = province;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	public BscAttachment getBscAttachment() {
		return bscAttachment;
	}

	public void setBscAttachment(BscAttachment bscAttachment) {
		this.bscAttachment = bscAttachment;
	}
	
}