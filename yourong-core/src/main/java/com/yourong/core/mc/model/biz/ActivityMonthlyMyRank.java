package com.yourong.core.mc.model.biz;

import java.math.BigDecimal;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.util.FormulaUtil;

public class ActivityMonthlyMyRank extends AbstractBaseObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 用户id
	 */
	private Long memberId;

	/**
	 * 投资总额
	 */
	private BigDecimal sums;

	/**
	 * 排名
	 */
	private Integer no;

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public BigDecimal getSums() {
		return sums;
	}

	public void setSums(BigDecimal sums) {
		this.sums = sums;
	}

	public Integer getNo() {
		return no;
	}

	public void setNo(Integer no) {
		this.no = no;
	}
	
	public String getFormatSums(){
		return FormulaUtil.getFormatPrice(sums);
	}

}
