package com.yourong.core.ic.model;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * 记录奖池系数和天数中间的对应关系
 *
 */
public class PrizePool extends AbstractBaseObject{

	//天数
	private Integer day;
	
	//系数
	private String ratio;

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	public String getRatio() {
		return ratio;
	}

	public void setRatio(String ratio) {
		this.ratio = ratio;
	}
	
	
	
}
