package com.yourong.core.ic.model;


/**
 * 直投满标抽奖规则
 * @author ga
 *
 */
public class LotteryRuleAmountNumber {

	private Integer startAmount;
	
	private Integer endAmount=0;
	
	private Integer number;
	
	private Integer code;
	
	private Integer prize;
	
	

	public Integer getPrize() {
		return prize;
	}

	public void setPrize(Integer prize) {
		this.prize = prize;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public Integer getStartAmount() {
		return startAmount;
	}

	public void setStartAmount(Integer startAmount) {
		this.startAmount = startAmount;
	}

	public Integer getEndAmount() {
		return endAmount;
	}

	public void setEndAmount(Integer endAmount) {
		this.endAmount = endAmount;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}
	
	
	
}
