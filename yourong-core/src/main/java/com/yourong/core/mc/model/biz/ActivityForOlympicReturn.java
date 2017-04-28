package com.yourong.core.mc.model.biz;

import java.io.Serializable;
import java.util.List;

public class ActivityForOlympicReturn implements Serializable  {
	
	private Integer guessTotalNumber;

	public Integer getGuessTotalNumber() {
		return guessTotalNumber;
	}
	private List<ActivityForOlympicGuess> guessMedalRecord;
	
	private List<ActivityForOlympicGuess> guessGoldRecord;
	
	private List<ActivityForOlympicGuess> luckyList;
	
	
	private String puzzleRemind;
	
	private Integer puzzle1;
	private Integer puzzle2;
	private Integer puzzle3;
	private Integer puzzle4;
	 
	
	
	
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

	public String getPuzzleRemind() {
		return puzzleRemind;
	}

	public void setPuzzleRemind(String puzzleRemind) {
		this.puzzleRemind = puzzleRemind;
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

	public List<ActivityForOlympicGuess> getGuessGoldRecord() {
		return guessGoldRecord;
	}

	public void setGuessGoldRecord(List<ActivityForOlympicGuess> guessGoldRecord) {
		this.guessGoldRecord = guessGoldRecord;
	}

	public List<ActivityForOlympicGuess> getLuckyList() {
		return luckyList;
	}

	public void setLuckyList(List<ActivityForOlympicGuess> luckyList) {
		this.luckyList = luckyList;
	}
	
	
}
