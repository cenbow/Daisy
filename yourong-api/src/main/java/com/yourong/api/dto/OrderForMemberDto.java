package com.yourong.api.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

public class OrderForMemberDto {
	private Long orderId;
	private String orderNo;
	@JSONField(name="name")
	private String projectName;
	private Date   orderTime;
	private BigDecimal investAmount;
	/** 1:待支付 2：已支付，投资失败 3：已支付，投资成功 4：支付失败 **/
	private Integer status;
	/** 备注 **/
	private String remarks;
	
	/** 项目类型 **/
	private Integer projectCategory;
	
	/**
	 * 转让本金
	 */
	private BigDecimal transferPrincipal;

	
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public Date getOrderTime() {
		return orderTime;
	}
	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}
	public BigDecimal getInvestAmount() {
		return investAmount;
	}
	public void setInvestAmount(BigDecimal investAmount) {
		this.investAmount = investAmount;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	/**
	 * @return the projectCategory
	 */
	public Integer getProjectCategory() {
		return projectCategory;
	}
	/**
	 * @param projectCategory the projectCategory to set
	 */
	public void setProjectCategory(Integer projectCategory) {
		this.projectCategory = projectCategory;
	}
	/**
	 * @return the transferPrincipal
	 */
	public BigDecimal getTransferPrincipal() {
		return transferPrincipal;
	}
	/**
	 * @param transferPrincipal the transferPrincipal to set
	 */
	public void setTransferPrincipal(BigDecimal transferPrincipal) {
		this.transferPrincipal = transferPrincipal;
	}
	
	
	
}
