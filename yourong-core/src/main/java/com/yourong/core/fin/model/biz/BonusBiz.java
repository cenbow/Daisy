package com.yourong.core.fin.model.biz;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 存钱罐收益
 * @author Leon Ray
 * 2014年10月28日-下午6:24:42
 */
public class BonusBiz {

	private Long memberId;
	
	private Date bonusDate;
	
	//七日收益的百分比（以七日的最大值为100%）
	private BigDecimal percent;
	
	private BigDecimal bonusAmount;

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public Date getBonusDate() {
		return bonusDate;
	}

	public void setBonusDate(Date bonusDate) {
		this.bonusDate = bonusDate;
	}

	public BigDecimal getBonusAmount() {
		return bonusAmount;
	}

	public void setBonusAmount(BigDecimal bonusAmount) {
		this.bonusAmount = bonusAmount;
	}

	public BigDecimal getPercent() {
		return percent;
	}

	public void setPercent(BigDecimal percent) {
		this.percent = percent;
	}
	
	
}
