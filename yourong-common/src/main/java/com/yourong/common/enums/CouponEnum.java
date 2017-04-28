package com.yourong.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 优惠券枚举类
 * 
 */
public enum CouponEnum {

	/**
	 * 有效期类型
	 */
	/** 永久 **/
	COUPONTEMPLATE_VAILD_CALC_TYPE_FOREVER("0", "永久"),
	/** 按时间区间 **/
	COUPONTEMPLATE_VAILD_CALC_TYPE_TIMEZONE("1", "按时间区间"),
	/** 按领取后天数计算 **/
	COUPONTEMPLATE_VAILD_CALC_TYPE_DAYS("2", "按领取后天数计算"),
	/** 首次投资后可用 **/
	COUPONTEMPLATE_VAILD_CALC_TYPE_INVEST("3", "首次投资后可用"),

	/**
	 * 优惠券模板类型
	 */
	/** 现金券 **/
	COUPONTEMPLATE_COUPON_TYPE_CASH("1", "现金券"),
	/** 收益券 **/
	COUPONTEMPLATE_COUPON_TYPE_INCOME("2", "收益券"),
	
	/***
	 * 使用优惠券的客户端
	 */
	/** web **/
	COUPON_CLIENT_WEB("1","web"),
	/** wap **/
	COUPON_CLIENT_WAP("2","wap"),
	/** app **/
	COUPON_CLIENT_APP("3","app");
	

	private static Map<String, CouponEnum> enumMap;

	private String code;

	private String desc;

	CouponEnum(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public String getCode() {
		return this.code;
	}

	public String getDesc() {
		return this.desc;
	}

	public static CouponEnum getEnumByCode(String code) {
		return getMap().get(code);
	}

	public static Map<String, CouponEnum> getMap() {
		if (enumMap == null) {
			enumMap = new HashMap<String, CouponEnum>();
			for (CouponEnum value : CouponEnum.values()) {
				enumMap.put(value.getCode(), value);
			}
		}
		return enumMap;
	}
}
