package com.yourong.core.tc.model.biz;

import java.math.BigDecimal;

/**
 * 交易本息表
 * @author leonray
 *
 */
public class ContractTransactionInterest {


    /**
     * 计息结束时间
     */
    private String payTime;

    /**
     * 应付利息和本金
     */
    private BigDecimal payableInterestAndPrincipal;

	public String getPayTime() {
		return payTime;
	}

	public void setPayTime(String payTime) {
		this.payTime = payTime;
	}

	public BigDecimal getPayableInterestAndPrincipal() {
		return payableInterestAndPrincipal;
	}

	public void setPayableInterestAndPrincipal(
			BigDecimal payableInterestAndPrincipal) {
		this.payableInterestAndPrincipal = payableInterestAndPrincipal;
	}
    

}