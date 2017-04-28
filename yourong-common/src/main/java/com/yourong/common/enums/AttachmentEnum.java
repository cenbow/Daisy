package com.yourong.common.enums;

import java.util.HashMap;
import java.util.Map;

public enum AttachmentEnum {
	
	/**
	 * 项目附件module
	 */
	/**
	 * 项目形象图
	 */
	ATTACHMENT_MODULE_DIRECT_PROJECT_SIGN("direct_project_sign", "项目形象图"),
	/**
	 * 借款人附件
	 */
	ATTACHMENT_MODULE_DIRECT_PROJECT_BORROWER("direct_project_borrower", "项目借款人附件"),
	/**
	 * 借款人附件加码赛克
	 */
	ATTACHMENT_MODULE_DIRECT_PROJECT_BORROWER_MOSAIC("direct_project_borrower_mosaic", "项目借款人附件"),
	/**
	 * 担保物附件
	 */
	ATTACHMENT_MODULE_DIRECT_PROJECT_COLLATERAL("direct_project_collateral", "担保物附件"),
	/**
	 * 担保物附件加码赛克
	 */
	ATTACHMENT_MODULE_DIRECT_PROJECT_COLLATERAL_MOSAIC("direct_project_collateral_mosaic", "担保物附件"),
	/**
	 * 合同图片
	 */
	ATTACHMENT_MODULE_DIRECT_PROJECT_CONTRACT("direct_project_contract", "合同图片"),
	/**
	 * 合同图片加码赛克
	 */
	ATTACHMENT_MODULE_DIRECT_PROJECT_CONTRACT_MOSAIC("direct_project_contract_mosaic", "合同图片"),
	/**
	 * 法律意见书
	 */
	ATTACHMENT_MODULE_DIRECT_PROJECT_LEGAL("direct_project_legal", "法律意见书"),
	/**
	 * 法律意见书加码赛克
	 */
	ATTACHMENT_MODULE_DIRECT_PROJECT_LEGAL_MOSAIC("direct_project_legal_mosaic", "法律意见书"),
	/**
	 * 征信报告
	 */
	ATTACHMENT_MODULE_DIRECT_PROJECT_CREDIT("direct_project_credit", "征信报告"),
	/**
	 * 征信报告加码赛克
	 */
	ATTACHMENT_MODULE_DIRECT_PROJECT_CREDIT_MOSAIC("direct_project_credit_mosaic", "征信报告"),
	/**
	 * 其他资料图片
	 */
	ATTACHMENT_MODULE_DIRECT_PROJECT_BASE("direct_project_base", "其他资料图片"),
	/**
	 * 其他资料图片加码赛克
	 */
	ATTACHMENT_MODULE_DIRECT_PROJECT_BASE_MOSAIC("direct_project_base_mosaic", "其他资料图片"),
	/**
	 * 人气值商品图片
	 */
	ATTACHMENT_MODULE_SHOP_GOODS("shop_goods", "人气值商品图片"),

	;
	private static Map<String, AttachmentEnum> enumMap;


	private String code;

	private String desc;

	AttachmentEnum(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}


	public String getCode() {
		return this.code;
	}

	public String getDesc() {
		return this.desc;
	}

	public static AttachmentEnum getEnumByCode(String code) {
		return getMap().get(code);
	}
	
	public static Map<String, AttachmentEnum> getMap() {
		if (enumMap == null) {
			enumMap = new HashMap<String, AttachmentEnum>();
			for (AttachmentEnum value : AttachmentEnum.values()) {
				enumMap.put(value.getCode(), value);
			}
		}
		return enumMap;
	}
}
