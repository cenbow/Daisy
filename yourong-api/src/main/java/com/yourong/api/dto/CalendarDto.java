package com.yourong.api.dto;

import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.core.tc.model.biz.TransactionInterestDetail;
import com.yourong.core.tc.model.biz.TransactionInterestForDate;
import com.yourong.core.tc.model.biz.TransactionInterestForMonth;

/**
 * Created by Administrator on 2015/4/3.
 */
public class CalendarDto extends  AbstractBaseObject {
	/**
	 * 月收金额
	 */
	private List<TransactionInterestForMonth> transactionInterestByMonth;
	/**
	 * 日历还款数据
	 */
	private List<TransactionInterestForDate> transactionInterestByCalendar;
	/**
	 * 当日还款数据
	 */
	private TransactionInterestDetail transactionInterestByDate;
	
	/**
	 * 当日还款数据 1.5.0 重构
	 */
	private CalendarTransactionInterestDetailAPPDto calendarTransactionInterestDetailAPPDto;
	
	/**
	 * 日历单日项目
	 */
	private List<RecommendProjectDto> recommendProjectList;
	/**
	 * 今日有回款，推荐精选项目：1 ；今日无回款，有今日投资，当日可回款项目：2; 都没有：0
	 */
	private Integer flag ;

	public List<TransactionInterestForMonth> getTransactionInterestByMonth() {
		return transactionInterestByMonth;
	}

	public void setTransactionInterestByMonth(List<TransactionInterestForMonth> transactionInterestByMonth) {
		this.transactionInterestByMonth = transactionInterestByMonth;
	}

	public List<TransactionInterestForDate> getTransactionInterestByCalendar() {
		return transactionInterestByCalendar;
	}

	public void setTransactionInterestByCalendar(List<TransactionInterestForDate> transactionInterestByCalendar) {
		this.transactionInterestByCalendar = transactionInterestByCalendar;
	}

	public TransactionInterestDetail getTransactionInterestByDate() {
		return transactionInterestByDate;
	}

	public void setTransactionInterestByDate(TransactionInterestDetail transactionInterestByDate) {
		this.transactionInterestByDate = transactionInterestByDate;
	}

	/**
	 * @return the recommendProjectList
	 */
	public List<RecommendProjectDto> getRecommendProjectList() {
		return recommendProjectList;
	}

	/**
	 * @param recommendProjectList the recommendProjectList to set
	 */
	public void setRecommendProjectList(
			List<RecommendProjectDto> recommendProjectList) {
		this.recommendProjectList = recommendProjectList;
	}

	/**
	 * @return the flag
	 */
	public Integer getFlag() {
		return flag;
	}

	/**
	 * @param flag the flag to set
	 */
	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	public CalendarTransactionInterestDetailAPPDto getCalendarTransactionInterestDetailAPPDto() {
		return calendarTransactionInterestDetailAPPDto;
	}

	public void setCalendarTransactionInterestDetailAPPDto(
			CalendarTransactionInterestDetailAPPDto calendarTransactionInterestDetailAPPDto) {
		this.calendarTransactionInterestDetailAPPDto = calendarTransactionInterestDetailAPPDto;
	}
	
}
