package com.yourong.core.fin.model;

import java.math.BigDecimal;
import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;

public class WithdrawFee extends AbstractBaseObject  {
	private static final long serialVersionUID = -6788851483909460435L;
	
	private List<BigDecimal>withDrawAmount;
	
	private BigDecimal popularValue;
	
	private Integer second;

	public List<BigDecimal> getWithDrawAmount() {
		return withDrawAmount;
	}

	public void setWithDrawAmount(List<BigDecimal> withDrawAmount) {
		this.withDrawAmount = withDrawAmount;
	}

	public BigDecimal getPopularValue() {
		return popularValue;
	}

	public void setPopularValue(BigDecimal popularValue) {
		this.popularValue = popularValue;
	}

	public Integer getSecond() {
		return second;
	}

	public void setSecond(Integer second) {
		this.second = second;
	}
	
	
	
}