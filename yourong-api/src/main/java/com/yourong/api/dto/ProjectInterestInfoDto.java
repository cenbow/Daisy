package com.yourong.api.dto;

import java.math.BigDecimal;

public class ProjectInterestInfoDto {
	 /**投资下线**/
    private BigDecimal minInvest;

    /**投资上限**/
    private BigDecimal maxInvest;

    /**年化收益**/
    private BigDecimal annualizedRate;

	public BigDecimal getMinInvest() {
		return minInvest;
	}

	public void setMinInvest(BigDecimal minInvest) {
		this.minInvest = minInvest;
	}

	public BigDecimal getMaxInvest() {
		return maxInvest;
	}

	public void setMaxInvest(BigDecimal maxInvest) {
		this.maxInvest = maxInvest;
	}

	public BigDecimal getAnnualizedRate() {
		return annualizedRate;
	}

	public void setAnnualizedRate(BigDecimal annualizedRate) {
		this.annualizedRate = annualizedRate;
	}
    
    
}
