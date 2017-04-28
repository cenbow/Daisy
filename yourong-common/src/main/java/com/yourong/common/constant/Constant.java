package com.yourong.common.constant;

import java.math.BigDecimal;
import java.nio.charset.Charset;

/**
 * 常量类
 * 
 * @author py
 *
 */
public final class Constant {

	public static final String DEFAULT_CODE = "UTF-8";
	// "startRow"
	public static final String STARTROW = "startRow";
	// pageSize
	public static final String PAGESIZE = "pageSize";

	public static final Integer SMSVOICECOUNT = 6;

	// 手机发送短信验证内容
	public static final String MOBILE_CODE = "mobile_code";
	// 图形验证码
	public static final String CAPTCHA_TOKEN = "captchaToken";

	// session获取当前用户
	public static final String CURRENT_USER = "currentUser";
	// session用户登录次数
	public static final String CURRENT_USER_LOGIN_COUNT = "currentUser_login_count";
	// session标识从M站跳转电脑版
	public static final String PC_INDEX_FROM_MOBIE = "PCIndexFromMobile";
	// 跟踪码
	public static final String REGISTERTRACESOURCE = "register_TraceSource";
	// 跟踪码
	public static final String REGISTERTRACENO = "register_TraceNo";
	// 状态：启用
	public static final int ENABLE = 1;
	// 状态：禁用
	public static final int DISABLE = -1;
	// 默认
	public static final int ISDEFAULT = 1;
	// 非默认
	public static final int ISNOTDEFAULT = 0;

	// 第三方支付回调通知结果：失败
	public static final String NOTIFY_RETURN_RESULT_FAIL_CODE = "fail";

	// 第三方支付回调通知结果：成功
	public static final String NOTIFY_RETURN_RESULT_SUCCESS_CODE = "success";
	
	//网银充值
	public static final int SINA_PAY  = 1;
	//绑卡支付
	public static final int CARD_PAY  = 2;
	
	// 第三方支付回调通知结果：订单关闭错误码
	public static final String NOTIFY_RETURN_RESULT_CLOSE_ERROR_CODE = "F0012";
	
	// 发送短信验证码长度
	public static final int SEND_SMS_LENGTH = 4;
	// 短信验证码
	public static final String SMS_CONTENT_CODE = "sms_content_code";
	// 语音验证码
	public static final String VOICE_CONTENT_CODE = "voice_content_code";
	// 记录发送短信次数
	public static final String MOBILE_SMS_COUNT = "mobile_count";
	// 记录发送语音次数
	public static final String MOBILE_VOICE_COUNT = "mobile_count";
	// 上午要小于的时间点
	public static final int MORNING_TIME = 8;
	// 晚上大于的时间带你
	public static final int NIGHT_TIME = 20;
	// 短信发送 的时间点
	public static final int SEND_SMS_TIME = 9;

	/**
	 * 用户投资代收
	 */
	public static final String SINA_PAY_MEMBER_HOSTING_COLLECT = "MEMBER_HOSTING_COLLECT";
	/**
	 * 平台基本户收益权垫付代收
	 */
	public static final String SINA_PAY_BASIC_COUPON_HOSTING_COLLECT = "SINA_PAY_BASIC_COUPON_HOSTING_COLLECT";

	/**
	 * 新浪存钱罐 x天收益
	 */
	public static final int FIN_SEVEN = 7;

	/**
	 * 附件合同模块标识
	 */
	public static final String ATTACHMENT_CONTRACT_IDENTITY = "contract";

	/**
	 * 首页默认数量
	 */
	public static final int INDEX_DEFAULT_NUMBER = 8;
	/**
	 * 首页默认数量7
	 */
	public static final int INDEX_DEFAULT_NUMBER_NEW = 7;
	/**
	 * 托管还本付息 (一天中还本付息时间)
	 */
	public static final int PAY_PRINCIPAL_INTEREST_TIME = 14;

	public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

	/** 新客投资1000以上，允许使用优惠券/收益券 **/
	public static final int NOVICE_INVEST_AMOUNT = 1000;

	/** 短信 **/
	public static final int MSG_TEMPLATE_TYPE_PHONE = 1;

	/** 邮件 **/
	public static final int MSG_TEMPLATE_TYPE_EMAIL = 2;

	/** 站内信 **/
	public static final int MSG_TEMPLATE_TYPE_STAND = 3;
	
	/**百度推送**/
	public static final int MSG_TEMPLATE_TYPE_BAIDU = 4;
	
	/**app消息**/
	public static final int MSG_TEMPLATE_TYPE_APP = 5;
	
	/**系统通知**/
	public static final int MSG_NOTIFY_TYPE_SYSTEM = 1;

	/** 操作通知 **/
	public static final int MSG_NOTIFY_TYPE_OPERATE = 2;

	/** 收益通知 **/
	public static final int MSG_NOTIFY_TYPE_INCOME = 3;

	/** 活动通知 **/
	public static final int MSG_NOTIFY_TYPE_ACTIVITY = 4;

	/** 其它通知 **/
	public static final int MSG_NOTIFY_TYPE_OTHER = 0;

	/** 后台通过会员号发送消息 **/
	public static final int MSG_SEND_BY_MEMBERID = 1;

	/** 后台直接发送消息 **/
	public static final int MSG_SEND_BY_FORWARDOBJECT = 2;

	/** 拦截第三方接口 ***/
	public static final String IS_HANDLE_SINA_PAY = "is_handle_sina_pay";
	/** 拦截第三方接口 ***/
	public static final String IS_REDIRECT = "is_redirect";

	/** 保存请求Key **/
	public static final String SAVED_REQUEST_KEY = "SAVED_REQUEST_KEY";

	/** 保存请求的url是否需要过滤 **/
	public static final String SAVED_REQUEST_KEY_IS_IGNORE = "SAVED_REQUEST_KEY_IS_IGNORE";

	public static final int USER_MEMBER_COUNTS = 20;

	/** 周年庆投资分享最小投资额 */
	public static final BigDecimal AnniversaryShareInvest = new BigDecimal(500);

	/** 周年庆投资获得人气值百分比 */
	public static final BigDecimal AnniversarySharePercent = new BigDecimal(0.002f);

	/** 黑名单**/
	public static  final  String  BLACK_MEMBERIDS_GROUP =  "black_memberids_group";
	public static  final  String  BLACK_MEMBERIDS_KEY =  "black_memberids_key";

	
	/**百万现金券总额*/
	public static final String ACTIVITY_MILLIONCOUPON_FUND = "1000000";

	/** 春节活动名称 */
	public static final String SPRING_FESTIVAL_NAME = "春节2016";

	/** 春节活动:辞旧迎新兑现返人气 */
	public static final String SPRING_REBATE = "【红运当头 过年狂欢 一路惊喜】辞旧迎新";

	/** 春节活动:压岁钱 */
	public static final String SPRING_COUPON = "【红运当头 过年狂欢 一路惊喜】守岁跨年";

	/** 春节活动:如意项目 */
	public static final String SPRING_WISHES = "【红运当头 过年狂欢 一路惊喜】万事如意";

	/** 春节活动:抢红包 */
	public static final String SPRING_RICH = "【红运当头 过年狂欢 一路惊喜】恭喜发财";

	/** 春节活动:压岁钱面额 */
	public static final String SPRING_COUPON_VALUE = "118元现金券";

	/** 红包活动 */
	public static final String TRANSACTION_REDBAG_DESC = "交易分享红包活动";
	
	public static final int BALANCE_SIZE = 3000;

	/**
	 * APP移动端日历默认显示数
	 */
	public static final int CALENDAR_MONTHS_NUM = 6;

	/**
	 * 债权合同名称
	 */
	public static final String CONTRACT_DEBT_TILE = "债权收益权转让协议";

	/**
	 * 直投合同名称
	 */
	public static final String CONTRACT_P2P_TILE = "借款协议";
	
	/**
	 * 转让债权合同名称
	 */
	public static final String CONTRACT_TRANSFER_DEBT_TILE = "债权收益权再转让协议";

	/**
	 * 转让直投合同名称
	 */
	public static final String CONTRACT_TRANSFER_P2P_TILE = "债权转让协议";
	
	/**
	 *  用户修改新手机号：新手机号和验证码加密放入session的key
	 */
	public static final String MEMBER_MODIFY_MOBILE_CAPTCHA_AES = "member_modify_mobile_captcha_aes";
	
	/**
	 * 用户修改新手机号：手机号、身份证、姓名组合加密放入session的key
	 */
	public static final String MEMBER_MODIFY_MOBILE_IDENTITY_NAME = "member_modify_mobile_identity_name";

	/**
	 * 代收交易商品名称修饰
	 */
	public static final String TRADE_GOODS_SUMMARY = "订单支付";

	/**
	 * 注册来源限制 dict_group
	 */
	public static final String DICT_GROUP_REGISTER_TRACE_SOURCE_LIMIT = "register_trace_source_limit";

	/**
	 * 使用现金券限制 dict_key
	 */
	public static final String DICT_KEY_USE_COUPON_LIMIT = "use_coupon_limit";

	/**
	 * 字段分隔符
	 */
	public static final String DICT_VALUE_SPLIT = "^";
	
	/**
	 * 自动投标标识：代表已自动投标过
	 */
	public static final String AUTO_INVEST_FLAG = "Y";

	/**
	 * 特殊用户 dict_group
	 */
	public static final String SPECIAL_MEMBER = "special_member";

	/**
	 * 新客项目校验过滤用户 dict_key
	 */
	public static final String NO_CHECK_NOVICE = "no_check_novice";
	
	/** 直投项目默认授信额度 */
	public static final BigDecimal DEFALUT_ZT_BORROWER_CREDIT = new BigDecimal(10000000);
	
	/** 资产包默认年后收益**/
	public static final BigDecimal DEFAULT_ANNUALIZEDRATE =new BigDecimal(10.08);
	
	/**资产包的默认周期 **/
	public static final Integer  DEFAULT_BORROWPERIOD = 60;
}
