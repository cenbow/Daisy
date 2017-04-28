package com.yourong.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 订单状态枚举类
 * 
 */
public enum SinaPayEnum {

	/**
	 * 5.1	外部业务码
	 */
	/** 外部业务码-1001-代收投资金 **/
	BUSINESS_1001("1001", "代收投资金"),
	/** 外部业务码-1002-代收还款金 **/
	BUSINESS_1002("1002", "代收还款金"),
	/** 外部业务码-2001-代付借款金  **/
	BUSINESS_2001("2001", "代付借款金"),
	/** 外部业务码-2002-代付（本金/收益）金  **/
	BUSINESS_2002("2002", "代付（本金/收益）金"),
	
	/**
	 * 5.2	交易状态
	 */
	/** 交易状态-等待付款 **/
	TRANSACTION_WAIT_PAY("WAIT_PAY", "等待付款"),
	/** 交易状态-已付款 **/
	TRANSACTION_PAY_FINISHED("PAY_FINISHED", "已付款"),
	/** 交易状态-交易成功 **/
	TRANSACTION_TRADE_SUCCESS("TRADE_SUCCESS", "交易成功"),
	/** 交易状态-交易失败 **/
	TRANSACTION_TRADE_FAILED("TRADE_FAILED", "交易失败"),
	/** 交易状态-交易结束 **/
	TRANSACTION_TRADE_FINISHED("TRADE_FINISHED", "交易结束"),
	/** 交易状态-交易关闭 **/
	TRANSACTION_TRADE_CLOSED("TRADE_CLOSED", "交易关闭（合作方通过调用交易取消接口来关闭）"),
	
	/**
	 * 5.3	退款状态
	 */
	/** 退款状态-等待退款 **/
	REFUND_WAIT_REFUND("WAIT_REFUND", "等待退款（处理中）"),
	/** 退款状态-已扣款**/
	REFUND_PAY_FINISHED("PAY_FINISHED", "已扣款（处理中）"),
	/** 退款状态-退款成功 **/
	REFUND_SUCCESS("SUCCESS", "退款成功"),
	/** 退款状态-退款失败 **/
	REFUND_FAILED("FAILED", "退款失败"),
	
	/**
	 * 5.4	支付状态
	 */
	/** 支付状态-成功 **/
	PAY_SUCCESS("SUCCESS", "成功"),
	/** 支付状态-失败 **/
	PAY_FAILED("FAILED", "失败"),
	/** 支付状态-处理中 **/
	PAY_PROCESSING("PROCESSING", "处理中"),
	
	
	/**
	 * 5.5	冻结、解冻状态
	 */
	/** -成功 **/
	FORZEN_SUCCESS("SUCCESS", "成功"),
	/** 失败 **/
	FORZEN_FAILED("FAILED", "失败"),
	/** 处理中 **/
	FORZEN_PROCESSING("PROCESSING", "处理中"),
	
	/**
	 * 提现状态
	 */
	/** 提现状态-成功 **/
	WITHDRAW_SUCCESS("SUCCESS", "成功"),
	/** 提现状态-失败 **/
	WITHDRAW_FAILED("FAILED", "失败"),
	/** 提现状态-退票 **/
	WITHDRAW_RETURNT_TICKET("RETURNT_TICKET", "退票"),
	/** 提现状态-处理中 **/
	WITHDRAW_PROCESSING("PROCESSING", "处理中"),
	
	/**
	 * 会员类型
	 */
	/** 会员类型-个人会员 **/
	MEMBER_1("1", "个人会员"),
	/** 会员类型-企业会员 **/
	MEMBER_2("2", "企业会员"),
	
	/**
	 * 支付方式
	 */
	PAY_TYPE_ONLINE_BANK("online_bank", "网银"),
	PAY_TYPE_BALANCE("balance", "余额"),
	PAY_TYPE_BINDING_PAY("binding_pay", "绑定支付"),
	PAY_TYPE_QUICK_PAY("quick_pay", "快捷"),
	PAY_TYPE_OFFLINE_PAY("offline_pay", "线下"),
	
	/**
	 * 证件类型
	 */
	CERTIFICATE_IC("IC", "身份证"),
	CERTIFICATE_PP("PP", "护照"),
	CERTIFICATE_HMP("HMP", "港澳通行证"),
	
	/**
	 * 账户类型
	 */
	ACCOUNT_BASIC("BASIC", "基本户"),
	ACCOUNT_ENSURE("ENSURE", "保证金户"),
	ACCOUNT_SAVING_POT("SAVING_POT", "存钱罐"),
	
	/**
	 * 认证类型
	 */
	AUTH_MOBILE("MOBILE", "手机"),
	AUTH_EMAIL("EMAIL", "邮箱"),
	
	/**
	 * 卡类型
	 */
	BANKCARD_DEBIT("DEBIT", "借记"),
	BANKCARD_CREDIT("CREDIT", "贷记（信用卡）"),
	
	/**
	 * 卡属性
	 */
	BANKCARD_PROPERTY_C("C", "对私"),
	BANKCARD_PROPERTY_B("B", "对公"),
	
	/**
	 * 卡认证方式
	 */
	BANKCARD_AUTH_DEPOSIT("DEPOSIT", "充值认证"),
	BANKCARD_AUTH_QUERY("QUERY", "查询认证"),
	
	/**
	 * 标识类型
	 */
	IDENTITY_UID ("UID", "商户用户id"),
	IDENTITY_MOBILE("MOBILE", "钱包绑定手机号"),
	IDENTITY_EMAIL("EMAIL", "钱包绑定邮箱"),
	
	/**
	 * 响应码
	 */
	RESPONSE_APPLY_SUCCESS("APPLY_SUCCESS", "提交成功，存在业务响应的以业务响应状态为准"),
	RESPONSE_AUTHORIZE_FAIL("AUTHORIZE_FAIL", "授权失败"),
	RESPONSE_AUTH_INVALID_DATE("AUTH_INVALID_DATE", "商户该接口授权已过期"),
	RESPONSE_ADD_VERIFY_FAIL("ADD_VERIFY_FAIL", "添加认证信息失败"),
	RESPONSE_BANK_ACCOUNT_NOT_EXISTS("BANK_ACCOUNT_NOT_EXISTS", "银行卡信息不存在"),
	RESPONSE_BANK_ACCOUNT_TOO_MANY("BANK_ACCOUNT_TOO_MANY", "绑定银行卡数量超限"),
	RESPONSE_BANK_CODE_NOT_SUPPORT("BANK_CODE_NOT_SUPPORT", "暂不支持该银行"),
	RESPONSE_BANK_INFO_VERIFY_FAILED("BANK_INFO_VERIFY_FAILED", "银行卡信息校验失败"),
	RESPONSE_BIZ_PENDING("BIZ_PENDING", "业务处理中，等待通知或查询"),
	RESPONSE_BLANCE_NOT_ENOUGH("BLANCE_NOT_ENOUGH", "余额不足"),
	RESPONSE_CARD_TYPE_NOT_SUPPORT("CARD_TYPE_NOT_SUPPORT", "卡类型暂不支持"),
	RESPONSE_CERT_NOT_EXIST("CERT_NOT_EXIST", "证件号不存在，请提前实名认证"),
	RESPONSE_CERTNO_NOT_MATCHING("CERTNO_NOT_MATCHING", "证件号不匹配"),
	RESPONSE_DUPLICATE_IDENTITY_ID("DUPLICATE_IDENTITY_ID", "用户标识信息重复"),
	RESPONSE_DUPLICATE_OUT_FREEZE_NO("DUPLICATE_OUT_FREEZE_NO", "冻结订单号重复"),
	RESPONSE_DUPLICATE_OUT_UNFREEZE_NO("DUPLICATE_OUT_UNFREEZE_NO", "解冻订单号重复"),
	RESPONSE_DUPLICATE_REQUEST_NO("DUPLICATE_REQUEST_NO", "重复的请求号"),
	RESPONSE_DUPLICATE_VERIFY("DUPLICATE_VERIFY", "会员认证信息重复"),
	RESPONSE_FREEZE_FUND_FAIL("FREEZE_FUND_FAIL", "冻结余额失败"),
	RESPONSE_FREEZE_FUND_PROCESSING("FREEZE_FUND_PROCESSING", "冻结余额处理中，请联系管理员"),
	RESPONSE_GET_VERIFY_FAIL("GET_VERIFY_FAIL", "查询认证信息失败"),
	RESPONSE_HOST_PAY_NOT_SUPPORT_REFUND("HOST_PAY_NOT_SUPPORT_REFUND", "代付交易不允许退款"),
	RESPONSE_ILLEGAL_ACCESS_SWITCH_SYSTEM("ILLEGAL_ACCESS_SWITCH_SYSTEM", "不允许访问该类型的接口"),
	RESPONSE_ILLEGAL_ARGUMENT("ILLEGAL_ARGUMENT", "参数校验未通过"),
	RESPONSE_ILLEGAL_DECRYPT("ILLEGAL_DECRYPT", "解密失败，请检查加密字段"),
	RESPONSE_ILLEGAL_INDETITY_PALTFORMTYPE("ILLEGAL_INDETITY_PALTFORMTYPE", "用户标识信息中不存在该平台标志"),
	RESPONSE_ILLEGAL_IP_OR_DOMAIN("ILLEGAL_IP_OR_DOMAIN", "非法的商户IP或域名"),
	RESPONSE_ILLEGAL_OUTER_TRADE_NO("ILLEGAL_OUTER_TRADE_NO", "交易订单号不存在"),
	RESPONSE_ILLEGAL_SERVICE("ILLEGAL_SERVICE", "服务接口不存在"),
	RESPONSE_ILLEGAL_SIGN("ILLEGAL_SIGN", "验签未通过"),
	RESPONSE_ILLEGAL_SIGN_TYPE("ILLEGAL_SIGN_TYPE", "签名类型不正确"),
	RESPONSE_INCORRECT_CARD_INFORMATION("INCORRECT_CARD_INFORMATION", "用户卡信息有误"),
	RESPONSE_INSUFFICIENT_FREEZE_BALANCE("INSUFFICIENT_FREEZE_BALANCE", "超过可冻结金额"),
	RESPONSE_INSUFFICIENT_UNFREEZE_BALANCE("INSUFFICIENT_UNFREEZE_BALANCE", "超过可解冻金额"),
	RESPONSE_MEMBER_ID_NOT_EXIST("MEMBER_ID_NOT_EXIST", "用户不存在"),
	RESPONSE_MEMBER_NOT_EXIST("MEMBER_NOT_EXIST", "用户标识信息不存在"),
	RESPONSE_NO_BANK_CARD_INFO("NO_BANK_CARD_INFO", "无相关银行卡信息"),
	RESPONSE_NO_BASIC_ACCOUNT("NO_BASIC_ACCOUNT", "用户无基本账户信息或没有激活"),
	RESPONSE_NO_FUND_ORIG_FREEEZE_TRADE("NO_FUND_ORIG_FREEEZE_TRADE", "原冻结交易不存在"),
	RESPONSE_NO_SUCH_MERCHANT("NO_SUCH_MERCHANT", "该商户信息不存在"),
	RESPONSE_ORDER_NOT_EXIST("ORDER_NOT_EXIST", "订单不存在"),
	RESPONSE_OTHER_ERROR("OTHER_ERROR", "其它错误"),
	RESPONSE_PARAMETER_INVALID("PARAMETER_INVALID", "请求参数不合法"),
	RESPONSE_PARTNER_ID_NOT_EXIST("PARTNER_ID_NOT_EXIST", "合作方Id不存在"),
	RESPONSE_PAY_METHOD_NOT_SUPPORT("PAY_METHOD_NOT_SUPPORT", "支付方式不支持"),
	RESPONSE_PAYER_INCONSISTENT("PAYER_INCONSISTENT", "订单批量支付付款人信息不一致"),
	RESPONSE_PAYMENT_DUPLIDATE("PAYMENT_DUPLIDATE", "重复支付"),
	RESPONSE_PAY_FAILED("PAY_FAILED", "支付失败"),
	RESPONSE_REALNAME_CONFIRM_FAILED("REALNAME_CONFIRM_FAILED", "实名认证不通过"),
	RESPONSE_REALNAME_NOT_MATCHING("REALNAME_NOT_MATCHING", "实名不匹配"),
	RESPONSE_REQUEST_METHOD_NOT_VALIDATE("REQUEST_METHOD_NOT_VALIDATE", "请求方式不合法"),
	RESPONSE_REQUEST_EXPIRED("REQUEST_EXPIRED", "请求过期"),
	RESPONSE_SYSTEM_ERROR("SYSTEM_ERROR", "系统内部错误"),
	RESPONSE_TRADE_AMOUNT_MODIFIED("TRADE_AMOUNT_MODIFIED", "交易金额修改不合法"),
	RESPONSE_TRADE_CLOSED("TRADE_CLOSED", "交易关闭"),
	RESPONSE_TRADE_FAILED("TRADE_FAILED", "交易调用失败"),
	RESPONSE_TRADE_NO_MATCH_ERROR("TRADE_NO_MATCH_ERROR", "交易号信息有误"),
	RESPONSE_USER_BANK_ACCOUNT_NOT_MATCH("USER_BANK_ACCOUNT_NOT_MATCH", "用户银行卡信息不匹配"),
	RESPONSE_VERIFY_NOT_EXIST("VERIFY_NOT_EXIST", "认证信息不存在"),
	RESPONSE_VERIFY_BINDED_OVERRUN("VERIFY_BINDED_OVERRUN", "认证信息绑定超限"),
	RESPONSE_AUTH_EMPTY("AUTH_EMPTY", "鉴权失败，未找到对应的授权信息"),
	
	/**
	 * 接口服务
	 */
	SERVICE_CREATE_ACTIVATE_MEMBER("create_activate_member", "创建激活会员"),
	SERVICE_SET_REAL_NAME("set_real_name", "设置实名信息"),
	SERVICE_BINDING_VERIFY("binding_verify", "绑定认证信息"),
	SERVICE_UNBINDING_VERIFY("unbinding_verify", "解绑认证信息"),
	SERVICE_QUERY_VERIFY("query_verify", "查询认证信息"),
	SERVICE_BINDING_BANK_CARD("binding_bank_card", "绑定银行卡"),
	SERVICE_UNBINDING_BANK_CARD("unbinding_bank_card", "解绑银行卡"),
	SERVICE_QUERY_BANK_CARD("query_bank_card", "查询银行卡"),
	SERVICE_QUERY_BALANCE("query_balance", "查询余额"),
	SERVICE_QUERY_ACCOUNT_DETAILS("query_account_details", "查询收支明细"),
	SERVICE_BALANCE_FREEZE("balance_freeze", "冻结余额"),
	SERVICE_BALANCE_UNFREEZE("balance_unfreeze", "解冻余额"),
	SERVICE_CREATE_HOSTING_COLLECT_TRADE("create_hosting_collect_trade", "创建托管代收交易"),
	SERVICE_CREATE_SINGLE_HOSTING_PAY_TRADE("create_single_hosting_pay_trade", "创建单笔托管代付交易"),
	SERVICE_CREATE_BATCH_HOSTING_PAY_TRADE("create_batch_hosting_pay_trade", "创建批量托管代付交易"),
	SERVICE_PAY_HOSTING_TRADE("pay_hosting_trade", "托管交易支付"),
	SERVICE_QUERY_PAY_RESULT("query_pay_result", "支付结果查询"),
	SERVICE_QUERY_HOSTING_TRADE("query_hosting_trade", "托管交易查询"),
	SERVICE_CREATE_HOSTING_REFUND("create_hosting_refund", "托管退款"),
	SERVICE_QUERY_HOSTING_REFUND("query_hosting_refund", "托管退款查询"),
	SERVICE_CREATE_HOSTING_DEPOSIT("create_hosting_deposit", "托管充值"),
	SERVICE_QUERY_HOSTING_DEPOSIT("query_hosting_deposit", "托管充值查询"),
	SERVICE_CREATE_HOSTING_WITHDRAW("create_hosting_withdraw", "托管提现"),
	SERVICE_QUERY_HOSTING_WITHDRAW("query_hosting_withdraw", "托管提现查询"),
	
	/**
	 * 基本参数
	 */
	PARAM_BASE_SERVICE("service", "接口名称"),
	PARAM_BASE_VERSION("version", "接口版本"),
	PARAM_BASE_REQUEST_TIME("request_time", "请求时间"),
	PARAM_BASE_PARTNER_ID("partner_id", "合作者身份ID"),
	PARAM_BASE_INPUT_CHARSET("_input_charset", "参数编码字符集"),
	PARAM_BASE_SIGN("sign", "签名"),
	PARAM_BASE_SIGN_TYPE("sign_type", "签名方式"),
	PARAM_BASE_SIGN_VERSION("sign_version", "签名版本号"),
	PARAM_BASE_ENCRYPT_VERSION("encrypt_version", "加密版本号"),
	PARAM_BASE_NOTIFY_URL("notify_url", "系统异步回调通知地址"),
	PARAM_BASE_RETURN_URL("return_url", "页面跳转同步返回页面路径"),
	PARAM_BASE_MEMO("memo", "说明信息，原文返回"),
	
	/**
	 * 核心参数
	 */
	PARAM_CORE_IDENTITY_ID("identity_id", "用户标识信息"),
	PARAM_CORE_IDENTITY_TYPE("identity_type", "用户标识类型"),
	PARAM_CORE_MEMBER_TYPE("member_type", "会员类型"),
	PARAM_CORE_EXTEND_PARAM("extend_param", "扩展信息"),
	PARAM_CORE_REAL_NAME("real_name", "真实姓名"),
	PARAM_CORE_CERT_TYPE("cert_type", "证件类型"),
	PARAM_CORE_CERT_NO("cert_no", "证件号码"),
	PARAM_CORE_NEED_CONFIRM("need_confirm", "是否认证"),
	PARAM_CORE_VERIFY_TYPE("verify_type", "认证类型"),
	PARAM_CORE_VERIFY_ENTITY("verify_entity", "认证内容"),
	PARAM_CORE_IS_MASK("is_mask", "是否掩码"),
	PARAM_CORE_BANK_CODE("bank_code", "银行编号"),
	PARAM_CORE_BANK_ACCOUNT_NO("bank_account_no", "银行卡号"),
	PARAM_CORE_ACCOUNT_NAME("account_name", "户名"),
	PARAM_CORE_CARD_TYPE("card_type", "卡类型"),
	PARAM_CORE_CARD_ATTRIBUTE("card_attribute", "卡属性"),
	PARAM_CORE_PHONE_NO("phone_no", "银行预留手机号"),
	PARAM_CORE_VALIDITY_PERIOD("validity_period", "有效期"),
	PARAM_CORE_VERIFICATION_VALUE("verification_value", "CVV2"),
	PARAM_CORE_PROVINCE("province", "认证内容"),
	PARAM_CORE_CITY("city", "城市"),
	PARAM_CORE_BANK_BRANCH("bank_branch", "支行名称"),
	PARAM_CORE_VERIFY_MODE("verify_mode", "认证方式"),
	PARAM_CORE_CARD_ID("card_id", "卡ID"),
	PARAM_CORE_ACCOUNT_TYPE("account_type", "账户类型"),
	PARAM_CORE_START_TIME("start_time", "开始时间"),
	PARAM_CORE_END_TIME("end_time", "结束时间"),
	PARAM_CORE_PAGE_NO("page_no", "页号"),
	PARAM_CORE_PAGE_SIZE("page_size", "每页大小"),
	PARAM_CORE_OUT_FREEZE_NO("out_freeze_no", "冻结订单号"),
	PARAM_CORE_AMOUNT("amount", "金额"),
	PARAM_CORE_SUMMARY("summary", "摘要"),
	PARAM_CORE_OUT_UNFREEZE_NO("out_unfreeze_no", "解冻订单号"),
	PARAM_CORE_OUT_TRADE_NO("out_trade_no", "交易订单号"),
	PARAM_CORE_OUT_TRADE_CODE("out_trade_code", "交易码"),
	PARAM_CORE_TRADE_CLOSE_TIME("trade_close_time", "交易关闭时间"),
	PARAM_CORE_PAYER_ID("payer_id", "付款用户ID"),
	PARAM_CORE_PAYER_IDENTITY_TYPE("payer_identity_type", "标识类型"),
	PARAM_CORE_PAYER_IP("payer_ip", "付款用户ip地址"),
	PARAM_CORE_PAY_METHOD("pay_method", "支付方式"),
	PARAM_CORE_PAYEE_IDENTITY_ID("payee_identity_id", "收款人标识"),
	PARAM_CORE_PAYEE_IDENTITY_TYPE("payee_identity_type", "收款人标识类型"),
	PARAM_CORE_SPLIT_LIST("split_list", "分账信息列表"),
	PARAM_CORE_TRADE_LIST("trade_list", "交易列表"),
	PARAM_CORE_OUT_PAY_NO("out_pay_no", "支付请求号"),
	PARAM_CORE_OUTER_TRADE_NO_LIST("outer_trade_no_list", "商户网站唯一交易订单号集合"),
	PARAM_CORE_ORIG_OUTER_TRADE_NO("orig_outer_trade_no", "原始的商户网站唯一交易订单号"),
	PARAM_CORE_REFUND_AMOUNT("refund_amount", "退款金额");

	private static Map<String, SinaPayEnum> enumMap;


	private String code;

	private String desc;

	SinaPayEnum(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}


	public String getCode() {
		return this.code;
	}

	public String getDesc() {
		return this.desc;
	}

	public static SinaPayEnum getEnumByCode(String code) {
		return getMap().get(code);
	}
	
	public static Map<String, SinaPayEnum> getMap() {
		if (enumMap == null) {
			enumMap = new HashMap<String, SinaPayEnum>();
			for (SinaPayEnum value : SinaPayEnum.values()) {
				enumMap.put(value.getCode(), value);
			}
		}
		return enumMap;
	}
}
