package com.yourong.core.mc.model.biz;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class ActivityForOlympicDate implements Serializable  {
		
	private String raceStartTime;
	private String raceEndTime;
	
	private Integer guessStartTime;
	private Integer guessEndTime;
	
	private Integer deductPopularvalue;
	
	private Integer fiveHundred;
	
	private List<Integer> puzzleNumber;
	
	private String setFourStartTime;
	
	private String setFourEndTime;
	
	private String sixEndTime;
	private String sevenStartTime;
	private String eightEndTime;
	private String nineStartTime;
	
	/**
	 * 亮奥运投资满2000
	 */
	private BigDecimal investAmount;
	
	private List<Integer> badgeNumber;
	
	private List<Long> templateId;
	
	private List<BigDecimal>totalAmount;
	
	private List<Integer> popularityValue;
	
	private List<Integer> couponAmount;
	
	private List<Integer> puzzle;
	
	private List<BigDecimal>multiple;
	
	private List<Integer> probability;
	
	private String guessMedalStartTime;
	
	private String guessMedalEndTime;
	
	private Integer matchStartTime;
	
	private Integer matchEndTime;
	
	private Integer publishTime;
	
	
	private String guessGoldStartTime;
	
	private String guessGoldEndTime;
	
	private String guessGoldPublishTime;
	
	
	
	public List<Integer> getPuzzleNumber() {
		return puzzleNumber;
	}
	public void setPuzzleNumber(List<Integer> puzzleNumber) {
		this.puzzleNumber = puzzleNumber;
	}
	public String getGuessMedalStartTime() {
		return guessMedalStartTime;
	}
	public void setGuessMedalStartTime(String guessMedalStartTime) {
		this.guessMedalStartTime = guessMedalStartTime;
	}
	public String getGuessMedalEndTime() {
		return guessMedalEndTime;
	}
	public void setGuessMedalEndTime(String guessMedalEndTime) {
		this.guessMedalEndTime = guessMedalEndTime;
	}
	public Integer getMatchStartTime() {
		return matchStartTime;
	}
	public void setMatchStartTime(Integer matchStartTime) {
		this.matchStartTime = matchStartTime;
	}
	public Integer getMatchEndTime() {
		return matchEndTime;
	}
	public void setMatchEndTime(Integer matchEndTime) {
		this.matchEndTime = matchEndTime;
	}
	public Integer getPublishTime() {
		return publishTime;
	}
	public void setPublishTime(Integer publishTime) {
		this.publishTime = publishTime;
	}
	public String getGuessGoldStartTime() {
		return guessGoldStartTime;
	}
	public void setGuessGoldStartTime(String guessGoldStartTime) {
		this.guessGoldStartTime = guessGoldStartTime;
	}
	public String getGuessGoldPublishTime() {
		return guessGoldPublishTime;
	}
	public void setGuessGoldPublishTime(String guessGoldPublishTime) {
		this.guessGoldPublishTime = guessGoldPublishTime;
	}
	public List<Integer> getProbability() {
		return probability;
	}
	public void setProbability(List<Integer> probability) {
		this.probability = probability;
	}
	public String getSetFourStartTime() {
		return setFourStartTime;
	}
	public void setSetFourStartTime(String setFourStartTime) {
		this.setFourStartTime = setFourStartTime;
	}
	public String getSetFourEndTime() {
		return setFourEndTime;
	}
	public void setSetFourEndTime(String setFourEndTime) {
		this.setFourEndTime = setFourEndTime;
	}
	public List<Integer> getBadgeNumber() {
		return badgeNumber;
	}
	public void setBadgeNumber(List<Integer> badgeNumber) {
		this.badgeNumber = badgeNumber;
	}
	public BigDecimal getInvestAmount() {
		return investAmount;
	}
	public void setInvestAmount(BigDecimal investAmount) {
		this.investAmount = investAmount;
	}
	public Integer getFiveHundred() {
		return fiveHundred;
	}
	public void setFiveHundred(Integer fiveHundred) {
		this.fiveHundred = fiveHundred;
	}
	public List<Integer> getPuzzle() {
		return puzzle;
	}
	public void setPuzzle(List<Integer> puzzle) {
		this.puzzle = puzzle;
	}
	public List<BigDecimal> getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(List<BigDecimal> totalAmount) {
		this.totalAmount = totalAmount;
	}
	public List<Long> getTemplateId() {
		return templateId;
	}
	public void setTemplateId(List<Long> templateId) {
		this.templateId = templateId;
	}
	public String getSixEndTime() {
		return sixEndTime;
	}
	public void setSixEndTime(String sixEndTime) {
		this.sixEndTime = sixEndTime;
	}
	public String getSevenStartTime() {
		return sevenStartTime;
	}
	public void setSevenStartTime(String sevenStartTime) {
		this.sevenStartTime = sevenStartTime;
	}
	public String getEightEndTime() {
		return eightEndTime;
	}
	public void setEightEndTime(String eightEndTime) {
		this.eightEndTime = eightEndTime;
	}
	public String getNineStartTime() {
		return nineStartTime;
	}
	public void setNineStartTime(String nineStartTime) {
		this.nineStartTime = nineStartTime;
	}
	public String getRaceStartTime() {
		return raceStartTime;
	}
	public void setRaceStartTime(String raceStartTime) {
		this.raceStartTime = raceStartTime;
	}
	public String getRaceEndTime() {
		return raceEndTime;
	}
	public void setRaceEndTime(String raceEndTime) {
		this.raceEndTime = raceEndTime;
	}
	public Integer getGuessStartTime() {
		return guessStartTime;
	}
	public void setGuessStartTime(Integer guessStartTime) {
		this.guessStartTime = guessStartTime;
	}
	public Integer getGuessEndTime() {
		return guessEndTime;
	}
	public void setGuessEndTime(Integer guessEndTime) {
		this.guessEndTime = guessEndTime;
	}
	public Integer getDeductPopularvalue() {
		return deductPopularvalue;
	}
	public void setDeductPopularvalue(Integer deductPopularvalue) {
		this.deductPopularvalue = deductPopularvalue;
	}
	public String getGuessGoldEndTime() {
		return guessGoldEndTime;
	}
	public void setGuessGoldEndTime(String guessGoldEndTime) {
		this.guessGoldEndTime = guessGoldEndTime;
	}
	public List<Integer> getPopularityValue() {
		return popularityValue;
	}
	public void setPopularityValue(List<Integer> popularityValue) {
		this.popularityValue = popularityValue;
	}
	public List<Integer> getCouponAmount() {
		return couponAmount;
	}
	public void setCouponAmount(List<Integer> couponAmount) {
		this.couponAmount = couponAmount;
	}
	public List<BigDecimal> getMultiple() {
		return multiple;
	}
	public void setMultiple(List<BigDecimal> multiple) {
		this.multiple = multiple;
	}
	
	
}
