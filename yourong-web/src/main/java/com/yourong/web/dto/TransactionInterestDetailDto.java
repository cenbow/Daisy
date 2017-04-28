package com.yourong.web.dto;

import java.math.BigDecimal;
import java.util.List;

import com.yourong.common.util.FormulaUtil;
import com.yourong.core.tc.model.biz.TransactionInterestDetailForCalendar;

public class TransactionInterestDetailDto {
	private int num;
	List<TransactionInterestDetailForCalendar> detailList;
	
	private BigDecimal totalAmount = BigDecimal.ZERO;
	
	private CalendarTransactionInterestDetailDto CalendarTransactionInterestDetailDto;
	
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public List<TransactionInterestDetailForCalendar> getDetailList() {
		return detailList;
	}
	public void setDetailList(List<TransactionInterestDetailForCalendar> detailList) {
		this.detailList = detailList;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	
	public String getFormatTotalAmount(){
		return FormulaUtil.getFormatPrice(getTotalAmount());
	}
	public CalendarTransactionInterestDetailDto getCalendarTransactionInterestDetailDto() {
		return CalendarTransactionInterestDetailDto;
	}
	public void setCalendarTransactionInterestDetailDto(
			CalendarTransactionInterestDetailDto calendarTransactionInterestDetailDto) {
		CalendarTransactionInterestDetailDto = calendarTransactionInterestDetailDto;
	}
	
}
