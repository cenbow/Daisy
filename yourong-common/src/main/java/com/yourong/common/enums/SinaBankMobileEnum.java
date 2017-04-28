/**
 * 
 */
package com.yourong.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年8月9日下午7:50:21
 */
public enum SinaBankMobileEnum {
		
	PAY_PASSWORD_M(1, "PAY_PASSWORD_M", "M站设置支付密码"),
	WITHHOLD_AUTHORITY_M(2, "WITHHOLD_AUTHORITY_M", "M站开关委托扣款"),
	RECHARGE_M(3, "PAY_PASSWORD_M", "M站充值"),
	INVESTMENT_RECHARGE_M(4, "PAY_PASSWORD_M", "M站交易充值"),
	WITHDRAWALS_M(5, "WITHDRAWALS_M", "M站提现"),
	INVESTMENT_M(6, "INVESTMENT_M", "M站投资"),
	RECHARGE_INVESTMENT_M(7, "RECHARGE_INVESTMENT_M", "M站充值接口，交易充值类型"),
	INVEST_WITHHOLD_AUTHORITY_M(8, "INVEST_WITHHOLD_AUTHORITY_M", "投资页面M站开关委托扣款"),
	SAFETY_CERTIFICATION(9, "SAFETY_CERTIFICATION", "M站安全认证"),
	
	COMMON_APP(11,"COMMON_APP","APP一般关闭"),
	;

	private static Map<String, SinaBankMobileEnum> enumMap;

	private int status;

	private String code;

	private String desc;

	SinaBankMobileEnum(int status, String code, String desc) {
		this.status = status;
		this.code = code;
		this.desc = desc;
	}

	public static Map<String, SinaBankMobileEnum> getMap() {
		if (enumMap == null) {
			enumMap = new HashMap<String, SinaBankMobileEnum>();
			for (SinaBankMobileEnum status : SinaBankMobileEnum.values()) {
				enumMap.put(status.getCode(), status);
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

	public static SinaBankMobileEnum getByCode(String code) {
		return getMap().get(code);
	}

	public int getStatus() {
		return this.status;
	}

	public String getCode() {
		return this.code;
	}

	public String getDesc() {
		return this.desc;
	}
}
