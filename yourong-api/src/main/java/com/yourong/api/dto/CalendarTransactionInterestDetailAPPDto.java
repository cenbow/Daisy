package com.yourong.api.dto;

import java.math.BigDecimal;
import java.util.List;

import com.yourong.core.tc.model.biz.CalenderTransactionInterestDetail;

public class CalendarTransactionInterestDetailAPPDto {

	/**个数**/
	private int num;
	/** 期数信息 **/
	private List<CalenderTransactionInterestDetail> calenderTransactionInterestDetail;
	/** 还款总计 **/
	private BigDecimal totalAmount;
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	
	public List<CalenderTransactionInterestDetail> getCalenderTransactionInterestDetail() {
		return calenderTransactionInterestDetail;
	}
	public void setCalenderTransactionInterestDetail(
			List<CalenderTransactionInterestDetail> calenderTransactionInterestDetail) {
		this.calenderTransactionInterestDetail = calenderTransactionInterestDetail;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	
}
