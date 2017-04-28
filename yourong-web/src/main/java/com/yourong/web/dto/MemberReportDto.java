package com.yourong.web.dto;
import java.util.Date;

import org.apache.ibatis.type.Alias;

import com.yourong.common.domain.AbstractBaseObject;
public class MemberReportDto {
	/**
	 * 报名信息
	 */
	private static final long serialVersionUID = -6263092266781392061L;

	/** 主键id **/
	private Long id;

	/** 用户id **/
	private Long memberId;

	/** 真实姓名**/
	private String trueName;

	/** 手机号码 **/
	private Long mobile;

	/** 所在省份**/
	private String province;

	/** 详细地址 **/
	private String address;

	/** 出行方式 **/
	private Integer travelMode;

	/****/
	private Date createTime;

	/****/
	private Date updateTime;

	private Integer delFlag;

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

	public String getTrueName() {
		return trueName;
	}

	public void setTrueName(String trueName) {
		this.trueName = trueName;
	}

	public Long getMobile() {
		return mobile;
	}

	public void setMobile(Long mobile) {
		this.mobile = mobile;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getTravelMode() {
		return travelMode;
	}

	public void setTravelMode(Integer travelMode) {
		this.travelMode = travelMode;
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

	public Integer getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}
	
	
	
}