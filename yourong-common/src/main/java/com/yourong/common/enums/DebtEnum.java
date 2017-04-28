package com.yourong.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 债权枚举类
 * 
 */
public enum DebtEnum {

	/**
	 * 债权附件类型
	 */
	/**
	 * 债权人附件
	 */
	ATTACHMENT_MODULE_DEBT_LENDER("debt_lender", "债权人附件"),
	/**
	 * 借款人附件
	 */
	ATTACHMENT_MODULE_DEBT_BORROWER("debt_borrower", "借款人附件"),
	/**
	 * 担保物附件
	 */
	ATTACHMENT_MODULE_DEBT_COLLATERAL("debt_collateral", "担保物附件"),
	/**
	 * 合同图片
	 */
	ATTACHMENT_MODULE_DEBT_CONTRACT("debt_contract", "合同图片"),
	/**
	 * 法律意见书
	 */
	ATTACHMENT_MODULE_DEBT_LEGAL("debt_legal", "法律意见书"),
	/**
	 * 其他资料图片
	 */
	ATTACHMENT_MODULE_DEBT_BASE("debt_base", "其他资料图片"),
	/**
	 * 合同附件
	 */
	ATTACHMENT_MODULE_CONTRACT("contract", "合同附件"),
	
	
	ATTACHMENT_MODULE_DEBT_SIGN("debt_sign", "项目形象图"),
	
	
	
	/**
	 * 担保物类型
	 */
	/**
	 * 担保物类型-抵押
	 */
	DEBT_TYPE_COLLATERAL("collateral", "抵押"),
	/**
	 * 担保物类型- 质押
	 */
	DEBT_TYPE_PLEDGE("pledge", "质押"),
	/**
	 * 担保物类型- 信用
	 */
	DEBT_TYPE_CREDIT("credit","信用"),
	
	/**
	 * 质押或抵押的具体类型
	 */
	DEBT_PLEDGE_COLLATERAL_TYPE_CAR("car", "车"),
	DEBT_PLEDGE_COLLATERAL_TYPE_HOUSE("house", "房"),
	DEBT_PLEDGE_COLLATERAL_TYPE_NEWCAR("newCar", "车辆合格证"),
	DEBT_PLEDGE_COLLATERAL_TYPE_EQUITY("equity", "股权融"),
	DEBT_PLEDGE_COLLATERAL_TYPE_HOUSERECORD("houseRecord", "房屋备注"),
	DEBT_PLEDGE_COLLATERAL_TYPE_CARPAYIN("carPayIn", "车贷垫资"),
	DEBT_PLEDGE_COLLATERAL_TYPE_CARBUSINESS("carBusiness","车商融"),
	DEBT_PLEDGE_COLLATERAL_TYPE_RUNCOMPANY("runCompany","经营融"),
	
	/**
	 * 微信相关图片
	 */
	ATTACHMENT_MODULE_WEIXIN_LENDER("weixin_lender", "微信图片"),
	/**
	 * 计息方式
	 */
	/**
	 * 按日计息，按月付息，到期还本
	 */
	RETURN_TYPE_DAY("monthly_paid","按日计息，按月付息，到期还本"),
	/**
	 * 一次性还本付息
	 */
	RETURN_TYPE_ONCE("once_paid","一次性还本付息"),
	RETURN_TYPE_AVG_PRINCIPAL("principal_average","等额本金"),
	RETURN_TYPE_AVG_INTEREST("all_average","等额本息"),
	/**
	 * 等本等息按月还款
	 */
	RETURN_TYPE_AVG_PRINCIPAL_INTEREST("avg_principal","等本等息按月还款"),
	/**
	 * 按日计息，按季付息，到期还本
	 */
	RETURN_TYPE_DAY_SEASON("season_paid","按日计息，按季付息，到期还本"),
	/**
	 * 等本等息按周还款
	 */
	RETURN_TYPE_AVG_PRINCIPAL_INTEREST_WEEK("avg_principal_week","等本等息按周还款");
	
	
	private static Map<String, DebtEnum> enumMap;


	private String code;

	private String desc;

	DebtEnum(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}


	public String getCode() {
		return this.code;
	}

	public String getDesc() {
		return this.desc;
	}

	public static DebtEnum getEnumByCode(String code) {
		return getMap().get(code);
	}
	
	public static Map<String, DebtEnum> getMap() {
		if (enumMap == null) {
			enumMap = new HashMap<String, DebtEnum>();
			for (DebtEnum value : DebtEnum.values()) {
				enumMap.put(value.getCode(), value);
			}
		}
		return enumMap;
	}
}
