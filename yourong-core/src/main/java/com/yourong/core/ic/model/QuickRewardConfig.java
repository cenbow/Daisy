package com.yourong.core.ic.model;

import java.util.Date;

import com.yourong.common.domain.AbstractBaseObject;

public class QuickRewardConfig extends AbstractBaseObject{

	//开始时间
	private Date startDate;
	
	//结束时间
	private Date endDate;
	
	//人气值
	private String popularity;
	
	//人气值开关
	private boolean flag = false;

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getPopularity() {
		return popularity;
	}

	public void setPopularity(String popularity) {
		this.popularity = popularity;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	
}
