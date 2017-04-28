package com.yourong.core.ic.model;

import com.yourong.common.domain.AbstractBaseObject;

public class WinNumber extends AbstractBaseObject{
	
	//中奖号码
	private Integer number;
		
	//几等奖
	private Integer prize;

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Integer getPrize() {
		return prize;
	}

	public void setPrize(Integer prize) {
		this.prize = prize;
	}
	
	

}
