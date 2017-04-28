package com.yourong.core.mc.model.biz;

import java.math.BigDecimal;

/**
 * 我是王活动
 * @author Leon Ray
 * 2014年12月12日-下午2:35:02
 */
public class ActivityForKing {

	/**
	 * 会员id
	 */
	private Long memberId;
	
	/**
	 * 用户名
	 */
	private String username;
	
	/**
	 * 推荐好友数
	 */
	private int refferCount;
	
	/**
	 * 推荐好友投资额
	 */
	private BigDecimal refferalInvestAmount;

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getRefferCount() {
		return refferCount;
	}

	public void setRefferCount(int refferCount) {
		this.refferCount = refferCount;
	}

	public BigDecimal getRefferalInvestAmount() {
		return refferalInvestAmount;
	}

	public void setRefferalInvestAmount(BigDecimal refferalInvestAmount) {
		this.refferalInvestAmount = refferalInvestAmount;
	}
	
	
}
