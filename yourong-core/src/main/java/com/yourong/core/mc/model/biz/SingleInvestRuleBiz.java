package com.yourong.core.mc.model.biz;

import java.math.BigDecimal;

public class SingleInvestRuleBiz  {
	private BigDecimal minInvestAmount;
	private BigDecimal maxInvestAmount;
	private BigDecimal baseSendPopularity;
	private boolean ExtraCalc;
	
	public BigDecimal getMinInvestAmount() {
		return minInvestAmount;
	}
	public void setMinInvestAmount(BigDecimal minInvestAmount) {
		this.minInvestAmount = minInvestAmount;
	}
	public BigDecimal getMaxInvestAmount() {
		return maxInvestAmount;
	}
	public void setMaxInvestAmount(BigDecimal maxInvestAmount) {
		this.maxInvestAmount = maxInvestAmount;
	}
	public BigDecimal getBaseSendPopularity() {
		return baseSendPopularity;
	}
	public void setBaseSendPopularity(BigDecimal baseSendPopularity) {
		this.baseSendPopularity = baseSendPopularity;
	}
	public boolean isExtraCalc() {
		return ExtraCalc;
	}
	public void setExtraCalc(boolean extraCalc) {
		ExtraCalc = extraCalc;
	}
	
	
}
