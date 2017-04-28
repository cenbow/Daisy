package com.yourong.api.dto;

import com.yourong.common.util.DateUtils;

import java.math.BigDecimal;

public class FinEaringDto {
	
	private String date;
	
	/**七天收益率*/
	private BigDecimal servenEaring;
	
	/**年化收益率*/
	private BigDecimal yearEaring;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public BigDecimal getServenEaring() {
		return servenEaring;
	}

	public void setServenEaring(BigDecimal servenEaring) {
		this.servenEaring = servenEaring;
	}

	public BigDecimal getYearEaring() {
		return yearEaring;
	}

	public void setYearEaring(BigDecimal yearEaring) {
		this.yearEaring = yearEaring;
	}

	
}
