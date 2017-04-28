package com.yourong.core.mc.model.biz;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.yourong.core.tc.model.biz.TransactionForFirstInvestAct;

public class ActivityForOlympic implements Serializable  {


	private Date startTime;
	private Date endTime;
	private Integer activityStatus;
	//当天累计投资前8用户 
	private List<TransactionForFirstInvestAct> everydayTotalAmountList;
	//活动期间总的累计投资总额前10
	private List<TransactionForFirstInvestAct> totalAmountList;
	
	private BigDecimal todayMyInvestAmount;
	private BigDecimal totalMyInvestAmount;
	//亮奥运
	private String brightOlympic;
	//亮奥运奖励领取
	private String brightOlympicReceive;
	private boolean ifGuessMedal=false;
	private boolean ifGuessGold=false;
	private Integer guessTotalNumber;
	private List<ActivityForOlympicGuess> guessMedalRecord;
	private List<ActivityForOlympicGuess> guessGoldRecord;
	private String puzzleRemind;
	//当前剩余人气值
	private Integer remindPopularityVaule;
	
	private Integer guessMedalNumber;
	
	private boolean couponAmount50=false;
	private boolean couponAmount100=false;
	private boolean couponAmount200=false;
	
	private Integer puzzle1;
	private Integer puzzle2;
	private Integer puzzle3;
	private Integer puzzle4;
	
	private List<ActivityForOlympicGuess> luckyList;
	
	private BigDecimal investAmount;
	
	private String guessMedalStartTime;
	
	private String guessMedalEndTime;
	
	private Integer matchStartTime;
	
	private Integer matchEndTime;
	
	private Integer publishTime;
	
	
	public String getBrightOlympic() {
		return brightOlympic;
	}
	public void setBrightOlympic(String brightOlympic) {
		this.brightOlympic = brightOlympic;
	}
	private String guessGoldStartTime;
	
	private String guessGoldEndTime;
	
	private String guessGoldPublishTime;
	
	private String raceStartTime;
	private String raceEndTime;
	
	private Integer guessStartTime;
	private Integer guessEndTime;
	
	private String sixEndTime;
	private String sevenStartTime;
	private String eightEndTime;
	private String nineStartTime;
	
	private Date eightEndTimeM;
	private Date guessGoldEndTimeM;
	private Date guessGoldPublishTimeM;
	private Date guessGoldStartTimeM;
	private Date guessMedalStartTimeM;
	private Date guessMedalEndTimeM;
	private Date nineStartTimeM;
	private Date sevenStartTimeM;
	private Date sixEndTimeM;
	
	
	
	
	public Date getEightEndTimeM() {
		return eightEndTimeM;
	}
	public void setEightEndTimeM(Date eightEndTimeM) {
		this.eightEndTimeM = eightEndTimeM;
	}
	public Date getGuessGoldEndTimeM() {
		return guessGoldEndTimeM;
	}
	public void setGuessGoldEndTimeM(Date guessGoldEndTimeM) {
		this.guessGoldEndTimeM = guessGoldEndTimeM;
	}
	public Date getGuessGoldPublishTimeM() {
		return guessGoldPublishTimeM;
	}
	public void setGuessGoldPublishTimeM(Date guessGoldPublishTimeM) {
		this.guessGoldPublishTimeM = guessGoldPublishTimeM;
	}
	public Date getGuessGoldStartTimeM() {
		return guessGoldStartTimeM;
	}
	public void setGuessGoldStartTimeM(Date guessGoldStartTimeM) {
		this.guessGoldStartTimeM = guessGoldStartTimeM;
	}
	public Date getGuessMedalStartTimeM() {
		return guessMedalStartTimeM;
	}
	public void setGuessMedalStartTimeM(Date guessMedalStartTimeM) {
		this.guessMedalStartTimeM = guessMedalStartTimeM;
	}
	public Date getGuessMedalEndTimeM() {
		return guessMedalEndTimeM;
	}
	public void setGuessMedalEndTimeM(Date guessMedalEndTimeM) {
		this.guessMedalEndTimeM = guessMedalEndTimeM;
	}
	public Date getNineStartTimeM() {
		return nineStartTimeM;
	}
	public void setNineStartTimeM(Date nineStartTimeM) {
		this.nineStartTimeM = nineStartTimeM;
	}
	public Date getSevenStartTimeM() {
		return sevenStartTimeM;
	}
	public void setSevenStartTimeM(Date sevenStartTimeM) {
		this.sevenStartTimeM = sevenStartTimeM;
	}
	public Date getSixEndTimeM() {
		return sixEndTimeM;
	}
	public void setSixEndTimeM(Date sixEndTimeM) {
		this.sixEndTimeM = sixEndTimeM;
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
	public String getGuessGoldEndTime() {
		return guessGoldEndTime;
	}
	public void setGuessGoldEndTime(String guessGoldEndTime) {
		this.guessGoldEndTime = guessGoldEndTime;
	}
	public String getGuessGoldPublishTime() {
		return guessGoldPublishTime;
	}
	public void setGuessGoldPublishTime(String guessGoldPublishTime) {
		this.guessGoldPublishTime = guessGoldPublishTime;
	}
	public BigDecimal getInvestAmount() {
		return investAmount;
	}
	public void setInvestAmount(BigDecimal investAmount) {
		this.investAmount = investAmount;
	}
	public List<ActivityForOlympicGuess> getLuckyList() {
		return luckyList;
	}
	public void setLuckyList(List<ActivityForOlympicGuess> luckyList) {
		this.luckyList = luckyList;
	}
	public Integer getGuessMedalNumber() {
		return guessMedalNumber;
	}
	public void setGuessMedalNumber(Integer guessMedalNumber) {
		this.guessMedalNumber = guessMedalNumber;
	}
	public Integer getPuzzle1() {
		return puzzle1;
	}
	public void setPuzzle1(Integer puzzle1) {
		this.puzzle1 = puzzle1;
	}
	public Integer getPuzzle2() {
		return puzzle2;
	}
	public void setPuzzle2(Integer puzzle2) {
		this.puzzle2 = puzzle2;
	}
	public Integer getPuzzle3() {
		return puzzle3;
	}
	public void setPuzzle3(Integer puzzle3) {
		this.puzzle3 = puzzle3;
	}
	public Integer getPuzzle4() {
		return puzzle4;
	}
	public void setPuzzle4(Integer puzzle4) {
		this.puzzle4 = puzzle4;
	}
	public boolean isCouponAmount50() {
		return couponAmount50;
	}
	public void setCouponAmount50(boolean couponAmount50) {
		this.couponAmount50 = couponAmount50;
	}
	public boolean isCouponAmount100() {
		return couponAmount100;
	}
	public void setCouponAmount100(boolean couponAmount100) {
		this.couponAmount100 = couponAmount100;
	}
	public boolean isCouponAmount200() {
		return couponAmount200;
	}
	public void setCouponAmount200(boolean couponAmount200) {
		this.couponAmount200 = couponAmount200;
	}
	public Integer getRemindPopularityVaule() {
		return remindPopularityVaule;
	}
	public void setRemindPopularityVaule(Integer remindPopularityVaule) {
		this.remindPopularityVaule = remindPopularityVaule;
	}
	public String getBrightOlympicReceive() {
		return brightOlympicReceive;
	}
	public void setBrightOlympicReceive(String brightOlympicReceive) {
		this.brightOlympicReceive = brightOlympicReceive;
	}
	public String getPuzzleRemind() {
		return puzzleRemind;
	}
	public void setPuzzleRemind(String puzzleRemind) {
		this.puzzleRemind = puzzleRemind;
	}
	public List<ActivityForOlympicGuess> getGuessGoldRecord() {
		return guessGoldRecord;
	}
	public void setGuessGoldRecord(List<ActivityForOlympicGuess> guessGoldRecord) {
		this.guessGoldRecord = guessGoldRecord;
	}
	public boolean isIfGuessGold() {
		return ifGuessGold;
	}
	public void setIfGuessGold(boolean ifGuessGold) {
		this.ifGuessGold = ifGuessGold;
	}
	public BigDecimal getTodayMyInvestAmount() {
		return todayMyInvestAmount;
	}
	public void setTodayMyInvestAmount(BigDecimal todayMyInvestAmount) {
		this.todayMyInvestAmount = todayMyInvestAmount;
	}
	public BigDecimal getTotalMyInvestAmount() {
		return totalMyInvestAmount;
	}
	public void setTotalMyInvestAmount(BigDecimal totalMyInvestAmount) {
		this.totalMyInvestAmount = totalMyInvestAmount;
	}
	public List<TransactionForFirstInvestAct> getTotalAmountList() {
		return totalAmountList;
	}
	public void setTotalAmountList(List<TransactionForFirstInvestAct> totalAmountList) {
		this.totalAmountList = totalAmountList;
	}
	public List<TransactionForFirstInvestAct> getEverydayTotalAmountList() {
		return everydayTotalAmountList;
	}
	public void setEverydayTotalAmountList(List<TransactionForFirstInvestAct> everydayTotalAmountList) {
		this.everydayTotalAmountList = everydayTotalAmountList;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public Integer getActivityStatus() {
		return activityStatus;
	}
	public void setActivityStatus(Integer activityStatus) {
		this.activityStatus = activityStatus;
	}
	
	public boolean isIfGuessMedal() {
		return ifGuessMedal;
	}
	public void setIfGuessMedal(boolean ifGuessMedal) {
		this.ifGuessMedal = ifGuessMedal;
	}
	
	public Integer getGuessTotalNumber() {
		return guessTotalNumber;
	}
	public void setGuessTotalNumber(Integer guessTotalNumber) {
		this.guessTotalNumber = guessTotalNumber;
	}
	public List<ActivityForOlympicGuess> getGuessMedalRecord() {
		return guessMedalRecord;
	}
	public void setGuessMedalRecord(List<ActivityForOlympicGuess> guessMedalRecord) {
		this.guessMedalRecord = guessMedalRecord;
	}
	
	
	
	

}
