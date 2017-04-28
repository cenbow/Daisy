package com.yourong.web.dto;

import java.math.BigDecimal;
import java.util.List;

import com.yourong.core.tc.model.biz.TransactionInterestDetailForCalendar;

public class RepayTransactionInterestDetailDto {

	/**个数**/
	private int num;
	/** 提前还款期数信息 **/
	private List<TransactionInterestDetailForCalendar> transactionInterestList;
	/** 还款总计 **/
	private BigDecimal totalAmount;
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public List<TransactionInterestDetailForCalendar> getTransactionInterestList() {
		return transactionInterestList;
	}
	public void setTransactionInterestList(
			List<TransactionInterestDetailForCalendar> transactionInterestList) {
		this.transactionInterestList = transactionInterestList;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	
}
