package com.yourong.core.ic.model;

import java.math.BigDecimal;

/**
 * 记录各级奖项信息  
 *
 */
public class PrizeInPool {

	//几等奖
	private Integer level;
	
	//奖项占奖池比例
	private String proportion;
	
	//奖项个数
	private Integer num;
	
	//几等奖金额
	private BigDecimal rewardAmount;
	
	

	public BigDecimal getRewardAmount() {
		return rewardAmount;
	}

	public void setRewardAmount(BigDecimal rewardAmount) {
		this.rewardAmount = rewardAmount;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getProportion() {
		return proportion;
	}

	public void setProportion(String proportion) {
		this.proportion = proportion;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}
	
	
	
}
