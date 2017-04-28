package com.yourong.core.ic.model.biz;

import java.math.BigDecimal;
import java.sql.Date;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.util.FormulaUtil;

public class ProjectInvestingDto extends AbstractBaseObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String name;
	private Date endDate;
	private BigDecimal totalAmount;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getFormatTotalAmount(){
		return FormulaUtil.getFormatPrice(totalAmount);
	}

}
