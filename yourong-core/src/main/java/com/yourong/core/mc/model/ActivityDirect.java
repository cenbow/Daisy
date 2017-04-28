package com.yourong.core.mc.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.yourong.core.ic.model.LotteryRuleAmountNumber;

public class ActivityDirect implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 8439409269377260592L;

	private BigDecimal lotteryAmount;
	private Integer lotteryNumber;
	
	private List<LotteryRuleAmountNumber> lottery;
	
	
	
	
	public List<LotteryRuleAmountNumber> getLottery() {
		return lottery;
	}
	public void setLottery(List<LotteryRuleAmountNumber> lottery) {
		this.lottery = lottery;
	}
	public BigDecimal getLotteryAmount() {
		return lotteryAmount;
	}
	public void setLotteryAmount(BigDecimal lotteryAmount) {
		this.lotteryAmount = lotteryAmount;
	}
	public Integer getLotteryNumber() {
		return lotteryNumber;
	}
	public void setLotteryNumber(Integer lotteryNumber) {
		this.lotteryNumber = lotteryNumber;
	}
	
	
}