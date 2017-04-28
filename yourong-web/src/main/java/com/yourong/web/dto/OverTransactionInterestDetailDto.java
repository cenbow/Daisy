package com.yourong.web.dto;

import java.math.BigDecimal;
import java.util.List;

import com.yourong.core.tc.model.biz.OverTransactionInterestForCalendar;

public class OverTransactionInterestDetailDto {

	/** 个数 **/
	private int num;
	/** 还款总计 **/
	private BigDecimal totalAmount;
	/** 还款总计 **/
	private List<OverTransactionInterestForCalendar> transactionInterestList;
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	public List<OverTransactionInterestForCalendar> getTransactionInterestList() {
		return transactionInterestList;
	}
	public void setTransactionInterestList(
			List<OverTransactionInterestForCalendar> transactionInterestList) {
		this.transactionInterestList = transactionInterestList;
	}
	
	
	
}
