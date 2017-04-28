package com.yourong.common.enums;

/**
 * 
 * @desc 摘要枚举类
 * @author wangyanji 2016年8月1日下午2:12:00
 */
public enum SummaryEnum {

	/**
	 * 退款类摘要:项目余额不足
	 */
	REFUND_PROJECT_BALANCE_NOT_ENOUGN("REFUND_PROJECT_BALANCE_NOT_ENOUGN", "项目余额不足投资失败"),

	/**
	 * 转让项目已撤销投资失败
	 */
	TRANSFER_PROJECT_STSTUS_ERROR("TRANSFER_PROJECT_STSTUS_ERROR", "转让项目已结束,投资失败");

	private String code;

	private String desc;

	SummaryEnum(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}
