package com.yourong.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 活动枚举类
 * 
 */
public enum ActivityEnum {

	/***
	 * 周年庆-奖励大放送10000
	 */
	ANNIVERSARY_PRIZE_1117(10000, "周年庆-奖励大放送1117","30点人气值，限时抢","点击抢人气值"),
	
	ANNIVERSARY_PRIZE_1118(10001, "周年庆-奖励大放送1118","1%收益券，限时抢","点击抢收益券"),
	
	ANNIVERSARY_PRIZE_1119(10002, "周年庆-奖励大放送1119","50元现金券，限时抢","点击抢现金券"),
	
	ANNIVERSARY_PRIZE_1120(10003, "周年庆-奖励大放送1120","30点人气值，限时抢","点击抢人气值"),
	
	ANNIVERSARY_PRIZE_1121(10004, "周年庆-奖励大放送1121","1%收益券，限时抢","点击抢收益券"),
	
	ANNIVERSARY_PRIZE_1122(10005, "周年庆-奖励大放送1122","50元现金券，限时抢","点击抢现金券"),
	
	ANNIVERSARY_PRIZE_1123(10006, "周年庆-奖励大放送1123","30点人气值，限时抢","点击抢人气值"),
	
	JULYTEAM_GROUPTYPE_JYSH(1,"五仁月饼","五仁月饼","五仁月饼"),
	
	JULYTEAM_GROUPTYPE_QLYX(2,"冰皮月饼","冰皮月饼","冰皮月饼");
	
	private static Map<Integer, ActivityEnum> enumMap;

	private Integer code;
	
	private String activityName;

	private String document;

	private String btnStr;

	ActivityEnum(int code, String activityName, String document, String btnStr) {
		this.code = code;
		this.activityName = activityName;
		this.document = document;
		this.btnStr = btnStr;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public String getBtnStr() {
		return btnStr;
	}

	public void setBtnStr(String btnStr) {
		this.btnStr = btnStr;
	}

	public static Map<Integer, ActivityEnum> getMap() {
		if (enumMap == null) {
			enumMap = new HashMap<Integer, ActivityEnum>();
			for (ActivityEnum value : ActivityEnum.values()) {
				enumMap.put(value.getCode(), value);
			}
		}
		return enumMap;
	}
}
