package com.yourong.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 日志枚举类
 * 
 */
public enum MemberLogEnum {

	//行为类型
	/**
	 * 1:合同下载
	 */
	MEMBER_BEHAVIOR_TYPE_DOWN_CONTRACT(1,"MEMBER_BEHAVIOR_TYPE_DOWN_CONTRACT","合同下载"),
	
	MEMBER_BEHAVIOR_TYPE_BIRTH_SIGN(2,"MEMBER_BEHAVIOR_TYPE_BIRTH_SIGN","生日签到"),
	
	MEMBER_BEHAVIOR_TYPE_APPH5_OPERATION(3,"MEMBER_BEHAVIOR_TYPE_APPH5_OPERATION","移动端H5操作"),
	
	MEMBER_BEHAVIOR_TYPE_WEB_HELPCENTER(4,"MEMBER_BEHAVIOR_TYPE_WEB_HELPCENTER","WEB端帮助中心访问"),
	
	
	
	//行为渠道
	MEMBER_BEHAVIOR_WEB(0,"MEMBER_BEHAVIOR_WEB","web"),
	MEMBER_BEHAVIOR_BACKEND(1,"MEMBER_BEHAVIOR_WEB","backend"),
	MEMBER_BEHAVIOR_APP(2,"MEMBER_BEHAVIOR_WEB","app"),
	MEMBER_BEHAVIOR_M(3,"MEMBER_BEHAVIOR_WEB","m"),
	MEMBER_BEHAVIOR_APPH5(4,"MEMBER_BEHAVIOR_APPH5","apph5"),
	
	
	;
	
	
	private static Map<String, MemberLogEnum> enumMap;

	private int type;

	private String code;

	private String desc;

	MemberLogEnum(int type, String code, String desc) {
		this.type = type;
		this.code = code;
		this.desc = desc;
	}

	public static Map<String, MemberLogEnum> getMap() {
		if (enumMap == null) {
			enumMap = new HashMap<String, MemberLogEnum>();
			for (MemberLogEnum type : MemberLogEnum.values()) {
				enumMap.put(type.getCode(), type);
			}
		}
		return enumMap;
	}

	public static boolean checkType(int type) {
		if (getMap().get(type) == null) {
			return false;
		}
		return true;
	}

	public static MemberLogEnum getByCode(String code) {
		return getMap().get(code);
	}

	public int getType() {
		return this.type;
	}

	public String getCode() {
		return this.code;
	}

	public String getDesc() {
		return this.desc;
	}
}
