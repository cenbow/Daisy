package com.yourong.web.dto;

import java.math.BigDecimal;
import java.util.List;

import com.yourong.core.tc.model.biz.CalenderTransactionInterestDetail;


public class CalendarTransactionInterestDetailDto {

	/**个数**/
	private int num;
	/** 期数信息 **/
	private List<CalenderTransactionInterestDetail> transactionInterestList;
	/** 还款总计 **/
	private BigDecimal totalAmount;
	
	/** 待还款总计 **/
	private BigDecimal topayTotalAmount;
	
	/** 已还款总计 **/
	private BigDecimal repaidTotalAmount;
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public List<CalenderTransactionInterestDetail> getTransactionInterestList() {
		return transactionInterestList;
	}
	public void setTransactionInterestList(
			List<CalenderTransactionInterestDetail> transactionInterestList) {
		this.transactionInterestList = transactionInterestList;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	public BigDecimal getTopayTotalAmount() {
		return topayTotalAmount;
	}
	public void setTopayTotalAmount(BigDecimal topayTotalAmount) {
		this.topayTotalAmount = topayTotalAmount;
	}
	public BigDecimal getRepaidTotalAmount() {
		return repaidTotalAmount;
	}
	public void setRepaidTotalAmount(BigDecimal repaidTotalAmount) {
		this.repaidTotalAmount = repaidTotalAmount;
	}
	
}
