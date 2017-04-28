package com.yourong.core.mc.model.biz;

import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * 
 * @desc 双旦活动
 * @author wangyanji 2015年11月20日下午1:35:46
 */
public class ActivityForDoubleDan extends AbstractBaseObject {

	/**
	 * 圣诞节项目状态
	 */
	private Integer christmasStatus;

	/**
	 * 元旦项目状态
	 */
	private Integer newYearStatus;

	/**
	 * 神秘新年礼活动状态
	 */
	private Integer secretStatus;

	/**
	 * 本次领取的圣诞礼
	 */
	private String thisChristmasGift;

	/**
	 * 本次领取的元旦礼
	 */
	private String thisNewYearGift;

	/**
	 * 本次领取的神秘新年礼
	 */
	private String thisSecretGift;

	/**
	 * 圣诞礼品集合
	 */
	private List<String> christmasGiftList;

	/**
	 * 元旦闯关状态集合
	 */
	private List<Integer> newYearMissionList;

	/**
	 * 神秘新年礼状态
	 */
	private Integer secretMission;

	/**
	 * 神秘新年礼实际领取等级:-1->0, 0->60, 1->120, 2->180, 3->240, 4->300
	 */
	private Integer secretRealIndex;

	public List<String> getChristmasGiftList() {
		return christmasGiftList;
	}

	public void setChristmasGiftList(List<String> christmasGiftList) {
		this.christmasGiftList = christmasGiftList;
	}

	public Integer getChristmasStatus() {
		return christmasStatus;
	}

	public void setChristmasStatus(Integer christmasStatus) {
		this.christmasStatus = christmasStatus;
	}

	public Integer getNewYearStatus() {
		return newYearStatus;
	}

	public void setNewYearStatus(Integer newYearStatus) {
		this.newYearStatus = newYearStatus;
	}

	public List<Integer> getNewYearMissionList() {
		return this.newYearMissionList;
	}

	public Integer getSecretMission() {
		return secretMission;
	}

	public void setSecretMission(Integer secretMission) {
		this.secretMission = secretMission;
	}

	public Integer getSecretRealIndex() {
		return secretRealIndex;
	}

	public void setSecretRealIndex(Integer secretRealIndex) {
		this.secretRealIndex = secretRealIndex;
	}

	public void setNewYearMissionList(List<Integer> newYearMissionList) {
		this.newYearMissionList = newYearMissionList;
	}

	public String getThisChristmasGift() {
		return thisChristmasGift;
	}

	public void setThisChristmasGift(String thisChristmasGift) {
		this.thisChristmasGift = thisChristmasGift;
	}

	public Integer getSecretStatus() {
		return secretStatus;
	}

	public void setSecretStatus(Integer secretStatus) {
		this.secretStatus = secretStatus;
	}

	public String getThisNewYearGift() {
		return thisNewYearGift;
	}

	public void setThisNewYearGift(String thisNewYearGift) {
		this.thisNewYearGift = thisNewYearGift;
	}

	public String getThisSecretGift() {
		return thisSecretGift;
	}

	public void setThisSecretGift(String thisSecretGift) {
		this.thisSecretGift = thisSecretGift;
	}

}
