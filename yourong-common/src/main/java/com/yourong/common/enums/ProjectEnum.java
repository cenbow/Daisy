package com.yourong.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 状态枚举类
 * 
 */
public enum ProjectEnum {
	
	/**
	 * 项目类型
	 */
	/** 债权项目 **/
	PROJECT_TYPE_DEBT(1, "PROJECT_TYPE_DEBT", "债权项目"),
	/** 直投项目 **/
	PROJECT_TYPE_DIRECT(2, "PROJECT_TYPE_DIRECT", "直投项目"),
	
	
	/**
	 * 项目收益类型
	 */
	/** 阶梯收益**/
	PROJECT_ANNUALIZED_RATE_TYPE_LADDER(1, "PROJECT_ANNUALIZED_RATE_TYPE_LADDER", "阶梯收益"),
	/** 递增收益 **/
	PROJECT_ANNUALIZED_RATE_TYPE_INCREASE(2, "PROJECT_ANNUALIZED_RATE_TYPE_INCREASE", "递增收益"),
	
	/**
	 * 直投项目担保类型
	 */
	/** 担保**/
	PROJECT_DIRECT_GUARANTEE_TYPE_GUARANTEE(1, "guarantee", "担保"),
	/** 信用 **/
	PROJECT_DIRECT_GUARANTEE_TYPE_CREDIT(2, "credit", "信用"),
	
	
	
	/**借款人类型（1：个人）**/
	PROJECT_BORROWER_PERSON_TYPE_DIRECT(1, "PROJECT_BORROWER_PERSON_TYPE_DIRECT", "借款人为个人"),
	/**借款人类型（2-企业）**/
	PROJECT_BORROWER_ENTERPRISE_TYPE_DIRECT(2, "PROJECT_BORROWER_ENTERPRISE_TYPE_DIRECT", "借款人为企业"),
	;
	
	
	private static Map<String, ProjectEnum> enumMap;

	private int type;

	private String code;

	private String desc;

	ProjectEnum(int type, String code, String desc) {
		this.type = type;
		this.code = code;
		this.desc = desc;
	}

	public static Map<String, ProjectEnum> getMap() {
		if (enumMap == null) {
			enumMap = new HashMap<String, ProjectEnum>();
			for (ProjectEnum type : ProjectEnum.values()) {
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

	public static ProjectEnum getByCode(String code) {
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