package com.yourong.core.mc.model;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.domain.AbstractBaseObject;

public class CouponTemplatePrint extends AbstractBaseObject{
	/**
	 * 优惠券印刷记录表
	 */
	private static final long serialVersionUID = 4415579382386640649L;

	/****/
	private Long id;

	/** 优惠券ID **/
	private Long couponTemplateId;

	/** 印刷时间 **/
	private Date printTime;

	/** 印刷数量 **/
	private Integer printNum;

	/** 领用数量 **/
	private Integer receivedNum;

	/** 使用数量 **/
	private Integer usedNum;

	/** 总金额(只针对于现金券) **/
	private BigDecimal totalAmount;

	/****/
	private Long createBy;

	/** 备注 **/
	private String remarks;

	/****/
	private Date updateTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCouponTemplateId() {
		return couponTemplateId;
	}

	public void setCouponTemplateId(Long couponTemplateId) {
		this.couponTemplateId = couponTemplateId;
	}

	public Date getPrintTime() {
		return printTime;
	}

	public void setPrintTime(Date printTime) {
		this.printTime = printTime;
	}

	public Integer getPrintNum() {
		return printNum;
	}

	public void setPrintNum(Integer printNum) {
		this.printNum = printNum;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Long getCreateBy() {
		return createBy;
	}

	public void setCreateBy(Long createBy) {
		this.createBy = createBy;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks == null ? null : remarks.trim();
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getReceivedNum() {
		return receivedNum;
	}

	public void setReceivedNum(Integer receivedNum) {
		this.receivedNum = receivedNum;
	}

	public Integer getUsedNum() {
		return usedNum;
	}

	public void setUsedNum(Integer usedNum) {
		this.usedNum = usedNum;
	}

}