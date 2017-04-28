package com.yourong.core.ic.model;

public class ProjectType {
	/** 主键 **/
	private Long id;

	/** 项目类型名称 **/
	private String projectTypeName;

	/** 项目类型编码(自定义) **/
	private String projectTypeCode;

	/** 担保方式(guarantee：担保；credit：信用) **/
	private String guarantyType;

	/**
	 * 担保类型（inclusive_financial：普惠消费；personal_credit：个人信用；sm_lending：小微贷款）
	 * （pledge_direct：抵质押；personal_guarantee：个人担保；escrow：第三方担保）
	 **/
	private String guarantyThingType;

	/** 是否分期（0-非分期；1-分期） **/
	private String instalment;

	/** 担保物信息 **/
	private String guarantyInfo;

	private Integer delFlag;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProjectTypeName() {
		return projectTypeName;
	}

	public void setProjectTypeName(String projectTypeName) {
		this.projectTypeName = projectTypeName == null ? null : projectTypeName.trim();
	}

	public String getProjectTypeCode() {
		return projectTypeCode;
	}

	public void setProjectTypeCode(String projectTypeCode) {
		this.projectTypeCode = projectTypeCode == null ? null : projectTypeCode.trim();
	}

	public String getGuarantyType() {
		return guarantyType;
	}

	public void setGuarantyType(String guarantyType) {
		this.guarantyType = guarantyType == null ? null : guarantyType.trim();
	}

	public String getGuarantyThingType() {
		return guarantyThingType;
	}

	public void setGuarantyThingType(String guarantyThingType) {
		this.guarantyThingType = guarantyThingType == null ? null : guarantyThingType.trim();
	}

	public String getInstalment() {
		return instalment;
	}

	public void setInstalment(String instalment) {
		this.instalment = instalment == null ? null : instalment.trim();
	}

	public String getGuarantyInfo() {
		return guarantyInfo;
	}

	public void setGuarantyInfo(String guarantyInfo) {
		this.guarantyInfo = guarantyInfo == null ? null : guarantyInfo.trim();
	}

	public Integer getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}
}