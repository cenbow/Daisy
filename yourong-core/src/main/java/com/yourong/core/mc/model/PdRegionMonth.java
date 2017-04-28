package com.yourong.core.mc.model;

import java.io.Serializable;
import java.math.BigDecimal;
	
public class PdRegionMonth implements Serializable {
	/**
	 * 数据频道注册人数和投资金额地区分布月更表
	 */
	private static final long serialVersionUID = -1556656446043628674L;

	/** 月份 **/
	private String month;

	/**省份 **/
	private String province;

	/** 注册人数地域占比 **/
	private BigDecimal registerMemberRate;

	/** 投资金额地域占比 **/
	private BigDecimal investAmountRate;

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public BigDecimal getRegisterMemberRate() {
		return registerMemberRate;
	}

	public void setRegisterMemberRate(BigDecimal registerMemberRate) {
		this.registerMemberRate = registerMemberRate;
	}

	public BigDecimal getInvestAmountRate() {
		return investAmountRate;
	}

	public void setInvestAmountRate(BigDecimal investAmountRate) {
		this.investAmountRate = investAmountRate;
	}

	
}