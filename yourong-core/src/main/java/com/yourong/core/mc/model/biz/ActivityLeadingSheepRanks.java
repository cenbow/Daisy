package com.yourong.core.mc.model.biz;

import java.math.BigDecimal;

import com.yourong.common.domain.AbstractBaseObject;

public class ActivityLeadingSheepRanks extends AbstractBaseObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 用户id
	 */
	private Long memberId;

	/**
	 * 获取次数
	 */
	private int gainTimes;

	/**
	 * 获取人气值总数
	 */
	private BigDecimal income;

	/**
	 * 用户名
	 */
	private String username;

	/**
	 * 用户头像
	 */
	private String avatars;

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public int getGainTimes() {
		return gainTimes;
	}

	public void setGainTimes(int gainTimes) {
		this.gainTimes = gainTimes;
	}

	public BigDecimal getIncome() {
		return income;
	}

	public void setIncome(BigDecimal income) {
		this.income = income;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAvatars() {
		return avatars;
	}

	public void setAvatars(String avatars) {
		this.avatars = avatars;
	}

}
