package com.yourong.core.tc.model.biz;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 收益日历交易本息的DO
 * 
 * @author Leon Ray 2014年9月26日-下午5:53:14
 */
public class TransactionInterestForCalendar implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4941487938805576752L;

	/** 用户id **/
	private Long memberId;
	/** 支付日期 **/
	private String payDate;
	/** 当前年份 **/
	private int year;
	/** 当前月份 **/
	private int month;
	/** 当前日期 **/
	private int day;
	/** 还款项目总数 **/
	private int num;
	/** 总计支付利息 **/
	private BigDecimal totalPayableInterest;
	/** 总计支付本金 **/
	private BigDecimal totalPayablePrincipal;
	
	 /**还款类型（0-正常 1-提前还款 2-逾期还款 3-垫资还款 ）**/
	private Integer payType;
	
	/** 正常未还款项目总数 **/
	private int noNum;
	
	/** 提前还款项目总数 **/
	private int preNum;
	
	/** 逾期还款项目总数 **/
	private int overNum;
	
	/** 逾期未还款项目总数 **/
	private int overNoPayNum;
	
	public Long getMemberId() {
		return memberId;
	}
	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}
	public String getPayDate() {
		return payDate;
	}
	public void setPayDate(String payDate) {
		this.payDate = payDate;
	}
	
	public int getYear() {
		return Integer.parseInt(payDate.substring(0, 4));
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getMonth() {
		return Integer.parseInt(payDate.substring(5, 7));
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getDay() {
		return Integer.parseInt(payDate.substring(8));
	}
	public void setDay(int day) {
		this.day = day;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public BigDecimal getTotalPayableInterest() {
		return totalPayableInterest;
	}
	public void setTotalPayableInterest(BigDecimal totalPayableInterest) {
		this.totalPayableInterest = totalPayableInterest;
	}
	public BigDecimal getTotalPayablePrincipal() {
		return totalPayablePrincipal;
	}
	public void setTotalPayablePrincipal(BigDecimal totalPayablePrincipal) {
		this.totalPayablePrincipal = totalPayablePrincipal;
	}
	public Integer getPayType() {
		return payType;
	}
	public void setPayType(Integer payType) {
		this.payType = payType;
	}
	public int getPreNum() {
		return preNum;
	}
	public void setPreNum(int preNum) {
		this.preNum = preNum;
	}
	public int getOverNum() {
		return overNum;
	}
	public void setOverNum(int overNum) {
		this.overNum = overNum;
	}
	public int getOverNoPayNum() {
		return overNoPayNum;
	}
	public void setOverNoPayNum(int overNoPayNum) {
		this.overNoPayNum = overNoPayNum;
	}
	public int getNoNum() {
		return noNum;
	}
	public void setNoNum(int noNum) {
		this.noNum = noNum;
	}
	
}