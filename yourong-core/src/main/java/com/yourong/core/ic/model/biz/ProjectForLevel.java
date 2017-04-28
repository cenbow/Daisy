package com.yourong.core.ic.model.biz;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * 各等级奖项信息
 */
public class ProjectForLevel extends AbstractBaseObject{

	
	private static final long serialVersionUID = -9193339820515134532L;

	//几等奖
	private Integer level;
	
	//奖项数量
	private Integer number;
	
	//奖金
	private String reward;
	
	private String radio;
	
	
	public String getRadio() {
		return radio;
	}
	public void setRadio(String radio) {
		this.radio = radio;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	public String getReward() {
		return reward;
	}
	public void setReward(String reward) {
		this.reward = reward;
	}
	
	
	
	
	
	
}
