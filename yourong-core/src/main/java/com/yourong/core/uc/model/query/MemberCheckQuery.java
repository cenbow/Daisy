package com.yourong.core.uc.model.query;

import java.util.Date;

import com.yourong.common.domain.BaseQueryParam;

public class MemberCheckQuery extends BaseQueryParam {

	private Date checkDate;
	private Integer checkSource;
	
	
	public Date getCheckDate() {
		return checkDate;
	}
	public void setCheckDate(Date checkDate) {
		this.checkDate = checkDate;
	}
	public Integer getCheckSource() {
		return checkSource;
	}
	public void setCheckSource(Integer checkSource) {
		this.checkSource = checkSource;
	}

	
}
