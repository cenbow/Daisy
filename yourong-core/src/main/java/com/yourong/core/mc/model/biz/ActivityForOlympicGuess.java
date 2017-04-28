package com.yourong.core.mc.model.biz;

import java.io.Serializable;
import java.util.Date;

public class ActivityForOlympicGuess implements Serializable  {
	private Date guessTime;
	private Integer guessResult;
	private Integer realResult;
	private Integer goldNumber;
	private Integer popularityValue;
	private Integer type;
	
	private String avatars;
	private String username;
	
	public Date getGuessTime() {
		return guessTime;
	}
	public void setGuessTime(Date guessTime) {
		this.guessTime = guessTime;
	}
	public Integer getGuessResult() {
		return guessResult;
	}
	public void setGuessResult(Integer guessResult) {
		this.guessResult = guessResult;
	}
	public Integer getRealResult() {
		return realResult;
	}
	public void setRealResult(Integer realResult) {
		this.realResult = realResult;
	}
	public Integer getGoldNumber() {
		return goldNumber;
	}
	public void setGoldNumber(Integer goldNumber) {
		this.goldNumber = goldNumber;
	}
	public Integer getPopularityValue() {
		return popularityValue;
	}
	public void setPopularityValue(Integer popularityValue) {
		this.popularityValue = popularityValue;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getAvatars() {
		return avatars;
	}
	public void setAvatars(String avatars) {
		this.avatars = avatars;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	
}
