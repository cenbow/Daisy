package com.yourong.web.dto;

import java.math.BigDecimal;
import java.util.Date;

public class LeaseBonusDetailDto {
	
	/** 分红值 **/
	private BigDecimal bonusAmount;
	
	/** 创建时间 **/
	private Date createTime;
	
	public BigDecimal getBonusAmount() {
		return bonusAmount;
	}
	public void setBonusAmount(BigDecimal bonusAmount) {
		this.bonusAmount = bonusAmount;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
}
