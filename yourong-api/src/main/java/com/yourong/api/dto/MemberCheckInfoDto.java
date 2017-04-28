package com.yourong.api.dto;

import java.math.BigDecimal;

public class MemberCheckInfoDto {
	/** 是否签到 **/
	private boolean isSignIn;
	/** 累计签到人气值 **/
	private int totalGainPopularity;
	/** 今天签到人值气 **/
	private int todayGainPopularity;
	/** 是否生日 **/
	private boolean isBirthday = false;
	/** 生日提示 **/
	private String birthdayTips;
	/** 生日专题URL **/
	private String url;
	/** 生日领收益券投资限额 **/
	private String profitCouponAmountScope;
	/** 生日领收益券投资收益天数限制 **/
	private String profitCouponDaysScope;
	/** 生日领收益券客户端使用权限 **/
	private String profitCouponPrivileges;
	/** 生日领收益券面额 **/
	private BigDecimal profitAmount;
	/** 生日领现金券投资限额 **/
	private String cashCouponAmountScope;
	/** 生日领现金券投资收益天数限制 **/
	private String cashCouponDaysScope;
	/** 生日领现金券客户端使用权限 **/
	private String cashCouponPrivileges;
	/** 生日领现金券面额 **/
	private BigDecimal cashAmount;
	/**是否领用现金券**/
	private boolean isReceiveCashCoupon;
	/**是否领用收益券**/
	private boolean isReceiveProfitCoupon;
	/**收益券兑换有效期**/
	private String profitCouponValidity;
	/**现金券兑换有效期**/
	private String cashCouponValidity;
	
	private Integer popularity;
	
	public boolean isSignIn() {
		return isSignIn;
	}

	public void setSignIn(boolean isSignIn) {
		this.isSignIn = isSignIn;
	}

	public int getTotalGainPopularity() {
		return totalGainPopularity;
	}

	public void setTotalGainPopularity(int totalGainPopularity) {
		this.totalGainPopularity = totalGainPopularity;
	}

	public int getTodayGainPopularity() {
		return todayGainPopularity;
	}

	public void setTodayGainPopularity(int todayGainPopularity) {
		this.todayGainPopularity = todayGainPopularity;
	}

	public boolean isBirthday() {
		return isBirthday;
	}

	public void setBirthday(boolean isBirthday) {
		this.isBirthday = isBirthday;
	}

	public String getBirthdayTips() {
		return birthdayTips;
	}

	public void setBirthdayTips(String birthdayTips) {
		this.birthdayTips = birthdayTips;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getProfitCouponAmountScope() {
		return profitCouponAmountScope;
	}

	public void setProfitCouponAmountScope(String profitCouponAmountScope) {
		this.profitCouponAmountScope = profitCouponAmountScope;
	}

	public String getProfitCouponDaysScope() {
		return profitCouponDaysScope;
	}

	public void setProfitCouponDaysScope(String profitCouponDaysScope) {
		this.profitCouponDaysScope = profitCouponDaysScope;
	}

	public String getProfitCouponPrivileges() {
		return profitCouponPrivileges;
	}

	public void setProfitCouponPrivileges(String profitCouponPrivileges) {
		this.profitCouponPrivileges = profitCouponPrivileges;
	}

	public String getCashCouponAmountScope() {
		return cashCouponAmountScope;
	}

	public void setCashCouponAmountScope(String cashCouponAmountScope) {
		this.cashCouponAmountScope = cashCouponAmountScope;
	}

	public String getCashCouponDaysScope() {
		return cashCouponDaysScope;
	}

	public void setCashCouponDaysScope(String cashCouponDaysScope) {
		this.cashCouponDaysScope = cashCouponDaysScope;
	}

	public String getCashCouponPrivileges() {
		return cashCouponPrivileges;
	}

	public void setCashCouponPrivileges(String cashCouponPrivileges) {
		this.cashCouponPrivileges = cashCouponPrivileges;
	}

	public BigDecimal getProfitAmount() {
		return profitAmount;
	}

	public void setProfitAmount(BigDecimal profitAmount) {
		this.profitAmount = profitAmount;
	}

	public BigDecimal getCashAmount() {
		return cashAmount;
	}

	public void setCashAmount(BigDecimal cashAmount) {
		this.cashAmount = cashAmount;
	}

	public boolean isReceiveCashCoupon() {
		return isReceiveCashCoupon;
	}

	public void setReceiveCashCoupon(boolean isReceiveCashCoupon) {
		this.isReceiveCashCoupon = isReceiveCashCoupon;
	}

	public boolean isReceiveProfitCoupon() {
		return isReceiveProfitCoupon;
	}

	public void setReceiveProfitCoupon(boolean isReceiveProfitCoupon) {
		this.isReceiveProfitCoupon = isReceiveProfitCoupon;
	}

	public String getProfitCouponValidity() {
		return profitCouponValidity;
	}

	public void setProfitCouponValidity(String profitCouponValidity) {
		this.profitCouponValidity = profitCouponValidity;
	}

	public String getCashCouponValidity() {
		return cashCouponValidity;
	}

	public void setCashCouponValidity(String cashCouponValidity) {
		this.cashCouponValidity = cashCouponValidity;
	}

	public Integer getPopularity() {
		return popularity;
	}

	public void setPopularity(Integer popularity) {
		this.popularity = popularity;
	}
	
}
