package com.yourong.core.uc.model;

import java.util.Date;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import com.yourong.common.domain.AbstractBaseObject;

public class MemberBehaviorLog extends AbstractBaseObject{

	private static final long serialVersionUID = 1L;
	
	 private Long id;

	 /**会员号**/
	 private Long memberId;

	 /**操作类型，1-合同下载，2-用户生日签到，3-移动端H5操作**/    
	 private Integer type;

	 /**操作来源：0-web，1-backend，2-app，3-m，4-移动端H5**/    
	 private Integer operatWay;
	 
	 /**业务主键**/
	 private String sourceId;
	 
	 /**操作时间**/
	 private Date operatTime;  
	 
	 /**设备**/
	 private String device;
	 
	 /**设备参数**/
	 private String deviceParam;
	 
	 /**锚点**/
	 private String anchor;
	 
	 /**备注**/
	 private String remarks;

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

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getOperatWay() {
		return operatWay;
	}

	public void setOperatWay(Integer operatWay) {
		this.operatWay = operatWay;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public Date getOperatTime() {
		return operatTime;
	}

	public void setOperatTime(Date operatTime) {
		this.operatTime = operatTime;
	}
	
	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getDeviceParam() {
		return deviceParam;
	}

	public void setDeviceParam(String deviceParam) {
		this.deviceParam = deviceParam;
	}

	public String getAnchor() {
		return anchor;
	}

	public void setAnchor(String anchor) {
		this.anchor = anchor;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
}
