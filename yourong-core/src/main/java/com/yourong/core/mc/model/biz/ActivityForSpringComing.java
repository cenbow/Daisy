package com.yourong.core.mc.model.biz;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.core.mc.model.CouponTemplate;

public class ActivityForSpringComing extends AbstractBaseObject{

	//优惠券
	private List<CouponTemplate> couponList;
	
	//累计投资额
	private BigDecimal totalAmount;
	
	//模板id
	private Long template;
	
	private Integer status;
	
	private Integer pacNum;
	/**
     * 开始日期
     */
    private Date startDate;
    /**
     * 结束日期
     */
    private Date endDate;
	

	public List<CouponTemplate> getCouponList() {
		return couponList;
	}

	public void setCouponList(List<CouponTemplate> couponList) {
		this.couponList = couponList;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Long getTemplate() {
		return template;
	}

	public void setTemplate(Long template) {
		this.template = template;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getPacNum() {
		return pacNum;
	}

	public void setPacNum(Integer pacNum) {
		this.pacNum = pacNum;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
