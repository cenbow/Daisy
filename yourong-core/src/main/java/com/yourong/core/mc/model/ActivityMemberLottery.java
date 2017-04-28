package com.yourong.core.mc.model;

import java.io.Serializable;

public class ActivityMemberLottery implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 8439409269377260592L;

	private String[] lotteryId;
	private String[] resultId;
	
	private Integer unLottery;
	
	public String[] getLotteryId() {
		return lotteryId;
	}
	public void setLotteryId(String[] lotteryId) {
		this.lotteryId = lotteryId;
	}
	public String[] getResultId() {
		return resultId;
	}
	public void setResultId(String[] resultId) {
		this.resultId = resultId;
	}
	public Integer getUnLottery() {
		return unLottery;
	}
	public void setUnLottery(Integer unLottery) {
		this.unLottery = unLottery;
	}
	
}