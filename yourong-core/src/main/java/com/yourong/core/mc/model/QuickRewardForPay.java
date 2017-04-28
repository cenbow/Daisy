package com.yourong.core.mc.model;

import java.math.BigDecimal;

import com.yourong.common.domain.AbstractBaseObject;

public class QuickRewardForPay extends AbstractBaseObject{
	
	//用户ID
	private Long memberId;
	
	private BigDecimal amount;
	//中奖备注
	private String remarks;
	public Long getMemberId() {
		return memberId;
	}
	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}
