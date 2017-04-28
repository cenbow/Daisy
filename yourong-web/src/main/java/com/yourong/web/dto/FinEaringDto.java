package com.yourong.web.dto;

import java.math.BigDecimal;

import com.yourong.common.util.DateUtils;

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
	
	public String getFormatDate() {
		if(date!=null){
			return DateUtils.convertToString(date, DateUtils.DATE_FMT_0, "MM-dd");
		}
		return "";
	}
	
}
