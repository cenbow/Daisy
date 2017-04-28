/**
 * 
 */
package com.yourong.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年7月6日下午2:24:35
 */
public enum BestSignCode {

	/**
	 * 上上签外部业务码
	 */
	BUSINESS_100000(100000, "成功"),
	BUSINESS_100001(100001, "系统忙，请稍候再试"),
	BUSINESS_120122(120122, "签名已完成"),
			;
	
	
	private static Map<String, BestSignCode> enumMap;


	private int code;

	private String desc;

	BestSignCode(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}


	public String getCode() {
		return String.valueOf(this.code);
	}

	public String getDesc() {
		return this.desc;
	}

	public static BestSignCode getResultCodeByCode(String code) {
		for (BestSignCode result : BestSignCode.values()) {
			if (result.getCode().equals(code)) {
				return result;
			}
		}
		return null;
	}
}
