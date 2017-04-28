package com.yourong.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 状态枚举类
 * 
 */
public enum StatusEnum {

	/**
	 * 待支付
	 */
	ORDER_WAIT_PAY(0, "wait_pay", "待支付"),
	/**
	 * 处理中
	 */
	ORDER_WAIT_PROCESS(1, "ORDER_WAIT_PROCESS", "支付中"),
	/**
	 * 已支付，投资失败
	 */
	ORDER_PAYED_INVEST_FAILED(2, "payed_invest_failed", "投资失败"),
	/**
	 * 已支付，已投资
	 */
	ORDER_PAYED_INVESTED(3, "payed_invested", "已支付"),
	/**
	 * 支付失败
	 */
	ORDER_PAYED_FAILED(4, "payed_failed", "支付失败"),
	/**
	 * 新浪确认中
	 */
	ORDER_SINA_CASHDESK_CONFIRM(8, "ORDER_SINA_CASHDESK_CONFIRM", "待确认"),
	/**
	 * 订单关闭
	 */
	ORDER_CLOSED(9, "ORDER_CLOSED", "已取消"),

	/**
	 * 充值失败
	 */
	RECHARGE_STATUS_FAIL(-2, "recharge_status_fail", "充值失败"),
	/**
	 * 充值拒绝
	 */
	RECHARGE_STATUS_REFUSED(-1, "recharge_status_refused", "充值拒绝"),
	/**
	 * 充值冻结
	 */
	RECHARGE_STATUS_FREEZE(0, "recharge_status_freeze", "充值冻结"),
	/**
	 * 充值处理中
	 */
	RECHARGE_STATUS_PROESS(1, "recharge_status_proess", "充值处理中"),
	/**
	 * 充值成功
	 */
	RECHARGE_STATUS_SUCESS(5, "recharge_status_sucess", "充值成功"),

	/**
	 * 提现失败
	 */
	WITHDRAW_STATUS_FAIL(-2, "withdraw_status_fail", "提现失败"),
	/**
	 * 提现拒绝
	 */
	WITHDRAW_STATUS_REFUSED(-1, "withdraw_status_refused", "提现拒绝"),
	/**
	 * 提现冻结
	 */
	WITHDRAW_STATUS_FREEZE(0, "withdraw_status_freeze", "提现冻结"),
	/**
	 * 提现处理中
	 */
	WITHDRAW_STATUS_PROESS(1, "withdraw_status_proess", "提现处理中"),
	/**
	 * 提现待支付
	 */
	WITHDRAW_STATUS_WAIT_PAY(2, "withdraw_status_wait_pay", "提现待支付"),
	/**
	 * 提现支付中
	 */
	WITHDRAW_STATUS_PAYING(3, "withdraw_status_paying", "提现支付中"),
	/**
	 * 提现成功
	 */
	WITHDRAW_STATUS_SUCESS(5, "withdraw_status_sucess", "提现成功"),

	/**
	 * 提现取消
	 */
	WITHDRAW_STATUS_CANCEL(6, "WITHDRAW_STATUS_CANCEL", "取消"),

	/**
	 * 募集中的
	 */
	TRANSACTION_INVESTMENTING(0, "transaction_repayment", "募集中"),
	/**
	 * 投资回款中
	 */
	TRANSACTION_REPAYMENT(1, "transaction_repayment", "履约中"),
	/**
	 * 投资已完结
	 */
	TRANSACTION_COMPLETE(2, "transaction_complete", "已还款"),
	/**
	 * 投资流标
	 */
	TRANSACTION_LOSE(3, "transaction_lose", "流标"),
	
	
	/**
	 * 未转让
	 */
	TRANSACTION_TRANSFER_STATUS_NO(0, "TRANSACTION_TRANSFER_STATUS_NO", "未转让"),
	/**
	 * 转让中
	 */
	TRANSACTION_TRANSFER_STATUS_TRANSFERING(1, "TRANSACTION_TRANSFER_STATUS_TRANSFERING", "转让中"),
	
	/**
	 * 部分转让
	 */
	TRANSACTION_TRANSFER_STATUS_PART_TRANSFERED(2, "TRANSACTION_TRANSFER_STATUS_PART_TRANSFERED", "部分转让"),
	/**
	 * 全部转让
	 */
	TRANSACTION_TRANSFER_STATUS_ALL_TRANSFERED(3, "TRANSACTION_TRANSFER_STATUS_ALL_TRANSFERED", "全部转让"),

	/** 放款状态（0-未放款 1-放款中 2-已放款） **/
	/**
	 * 未放款
	 */
	TRANSACTION_LOAN_STATUS_WAIT_LOAN(0, "TRANSACTION_LOAN_STATUS_WAIT_LOAN", "未放款"),
	/**
	 * 放款中
	 */
	TRANSACTION_LOAN_STATUS_LOANING(1, "TRANSACTION_LOAN_STATUS_LOANING", "放款中"),
	/**
	 * 已放款
	 */
	TRANSACTION_LOAN_STATUS_LOANED(2, "TRANSACTION_LOAN_STATUS_LOANED", "已放款"),

	/**
	 * 交易本息
	 */
	/**
	 * 交易本息待支付
	 */
	TRANSACTION_INTEREST_WAIT_PAY(0, "transaction_interest_wait_pay", "待支付"),
	/**
	 * 交易本息全部支付
	 */
	TRANSACTION_INTEREST_ALL_PAYED(1, "transaction_interest_all_payed", "全部支付"),
	/**
	 * 交易本息部分支付
	 */
	TRANSACTION_INTEREST_PART_PAYED(2, "transaction_interest_part_payed", "部分支付"),
	/**
	 * 交易本息未支付
	 */
	TRANSACTION_INTEREST_NOT_PAY(3, "transaction_interest_not_pay", "未支付"),
	/**
	 * 交易本息支付中
	 */
	TRANSACTION_INTEREST_PAYING(4, "TRANSACTION_INTEREST_PAYING", "支付中"),

	/**
	 * 交易本息已转让
	 */
	TRANSACTION_INTEREST_TRANSFER(5, "TRANSACTION_INTEREST_TRANSFER", "已转让"),

	/**
	 * 项目模块
	 */
	/**
	 * 存盘
	 */
	PROJECT_STATUS_SAVE(1, "project_status_save", "存盘"),

	/**
	 * 待审核
	 */
	PROJECT_STATUS_WAIT_REVIEW(10, "project_status_wait_review", "待审核"),

	/**
	 * 待发布
	 */
	PROJECT_STATUS_WAIT_RELEASE(20, "project_status_wait_release", "待发布"),

	/**
	 * 投资中
	 */
	PROJECT_STATUS_INVESTING(30, "project_status_investing", "投资中"),

	/**
	 * 已暂停
	 */
	PROJECT_STATUS_STOP(40, "project_status_stop", "已暂停"),

	/**
	 * 已满额
	 */
	PROJECT_STATUS_FULL(50, "project_status_full", "已满额"),
	
	/**
	 * 待放款
	 */
	PROJECT_STATUS_WAIT_LOAN(51, "project_status_wait_loan", "待放款"),
	
	/**
	 * 已放款
	 */
	PROJECT_STATUS_HAD_LOAN(52, "project_status_had_loan", "已放款"),

	/**
	 * 已截止
	 */
	PROJECT_STATUS_END(60, "project_status_end", "已截止"),

	/**
	 * 已还款
	 */
	PROJECT_STATUS_REPAYMENT(70, "project_status_repayment", "已还款"),
	/**
	 * 流标中
	 */
	PROJECT_STATUS_LOSING(80, "project_status_losing", "流标中"),
	/**
	 * 审核通过
	 */
	PROJECT_STATUS_AUTH_PASS(81, "project_status_auth_pass", "审核通过"),
	/**
	 * 流标
	 */
	PROJECT_STATUS_LOSE(90, "project_status_lose", "流标"),
	
	
	/**
	 * 转让项目-30-投资中
	 */
	TRANSFER_PROJECT_STATUS_INVESTING(30, "TRANSFER_PROJECT_STATUS_INVESTING", "投资中"),
	
	/**
	 * 转让项目-52-已转让
	 */
	TRANSFER_PROJECT_STATUS_LOAN(52, "TRANSFER_PROJECT_STATUS_LOAN", "已转让"),
	
	/**
	 * 转让项目-70-已还款
	 */
	TRANSFER_PROJECT_STATUS_REPAYMENT(70, "TRANSFER_PROJECT_STATUS_REPAYMENT", "已还款"),
	
	
	/**
	 * 转让项目-90-转让失败
	 */
	TRANSFER_PROJECT_STATUS_LOSE(90, "TRANSFER_PROJECT_STATUS_LOSE", "转让失败"),


	

	/**
	 * 阶梯收益
	 */
	PROJECT_ANNUALIZED_RATE_TYPE_LADDER(1, "annualized_rate_type_ladder", "阶梯收益"),

	/**
	 * 递增收益
	 */
	PROJECT_ANNUALIZED_RATE_TYPE_INCREMENT(2, "annualized_rate_type_increment", "利率随递增额递增"),

	/**
	 * 新手项目
	 */
	PROJECT_IS_NOVICE(0, "project_is_novice", "新手项目"),
	
	/**
	 * 非新手项目
	 */
	PROJECT_IS_NOT_NOVICE(1, "project_is_not_novice", "非新手项目"),

	/**
	 * 项目预告
	 */

	/**
	 * 正常
	 */
	PROJECT_NOTICE_STATUS_DEFAULT(1, "project_notice_status_default", "正常"),

	/**
	 * 预告中
	 */
	PROJECT_NOTICE_STATUS_RUNING(10, "project_notice_status_runing", "预告中"),

	/**
	 * 暂停
	 */
	PROJECT_NOTICE_STATUS_STOP(20, "project_notice_status_stop", "暂停"),

	/**
	 * 结束
	 */
	PROJECT_NOTICE_STATUS_END(30, "project_notice_status_end", "结束"),

	/**
	 * 上线是否通知（0-不通知 1-通知）
	 */
	/**
	 * 0-不通知
	 */
	PROJECT_NOT_ONLINE_NOTICE(0, "PROJECT_NOT_ONLINE_NOTICE", "不通知"),
	/**
	 * 1-通知
	 */
	PROJECT_ONLINE_NOTICE(1, "PROJECT_ONLINE_NOTICE", "通知"),

	/**
	 * 预告是否通知（0-不通知 1-通知）
	 */
	/**
	 * 0-不通知
	 */
	PROJECT_NOT_NOTICE_NOTICE(0, "PROJECT_NOT_NOTICE_NOTICE", "不通知"),
	/**
	 * 1-通知
	 */
	PROJECT_NOTICE_NOTICE(1, "PROJECT_NOTICE_NOTICE", "通知"),

	/**
	 * 债权模块
	 */

	/**
	 * 存盘
	 */
	DEBT_STATUS_SAVE(0, "debt_status_save", "存盘"),
	/**
	 * 已使用
	 */
	DEBT_STATUS_USED(10, "debt_status_used", "已使用"),
	/**
	 * 已还款
	 */
	DEBT_STATUS_PAID(20, "debt_status_paid", "已还款"),

	/**
	 * 债权本息
	 */
	/**
	 * 债权本息待支付
	 */
	DEBT_INTEREST_WAIT_PAY(0, "DEBT_INTEREST_WAIT_PAY", "待支付"),
	/**
	 * 债权本息全部支付
	 */
	DEBT_INTEREST_ALL_PAYED(1, "DEBT_INTEREST_ALL_PAYED", "全部支付"),
	/**
	 * 债权本息部分支付
	 */
	DEBT_INTEREST_PART_PAYED(2, "DEBT_INTEREST_PART_PAYED", "部分支付"),
	/**
	 * 债权本息逾期
	 */
	DEBT_INTEREST_OVERDUE_PAY(3, "DEBT_INTEREST_OVERDUE_PAY", "逾期"),

	/**
	 * 优惠券模板模块
	 */
	/**
	 * 未印刷
	 */
	COUPONTEMPLATE_STATUS_SAVE(0, "coupon_status_save", "未印刷"),
	/**
	 * 已印刷
	 */
	COUPONTEMPLATE_STATUS_USED(1, "coupon_status_used", "已印刷"),

	/****
	 * 会员模块
	 * 
	 */
	MEMBER_SEX_UN(-1, "member_sex_un", "未知"), MEMBER_SEX_MAN(0, "member_sex_man", "男"), MEMBER_SEX_WOMAEN(1, "member_sex_womaen", "nv"),

	MEMBER_STATUS_ACTIVE(2, "member_status_active", "激活"), MEMBER_STATUS_UN_ACTIVE(1, "member_status_un_active", "未激活"), MEMBER_STATUS_FREEZE(
			-1, "member_status_freeze", "冻结"),

	// 会员来源
	// 0, "注册来源为站点内注册";1, "注册来源为微博";2, "注册来源为QQ";3, "注册来源为后台管理员注册"
	MEMBER_SOURCE_WEB(0, "member_source_web", "注册来源为站点内注册"), MEMBER_SOURCE_WEIBO(1, "member_source_weibo", "注册来源为微博"), MEMBER_SOURCE_QQ(2,
			"member_source_qq", "注册来源为QQ"), MEMBER_SOURCE_BACKEND(3, "member_source_backend", "注册来源为后台管理员注册"), MEMBER_SOURCE_ANROID(4,
			"member_source_android", "注册来源为android客户单"), MEMBER_SOURCE_IOS(5, "member_source_ios", "注册来源为ios客户单"), MEMBER_SOURCE_WEIXIN(6,
			"member_source_weixin", "注册来源为微信"), MEMBER_SOURCE_OPEN_PLATFORM(7, "member_source_open_platform", "注册来源为外部渠道接口"),

	// 推荐方式：1：推荐注册 2：推荐投资 3：其他
	MEMBER_REFER_TYPE_REGIST(1, "member_refer_type_regist", "推荐注册 "), MEMBER_REFER_TYPE_INVESTMENT(2, "member_refer_type_investment",
			"推荐投资"), MEMBER_REFER_TYPE_OTHER(3, "member_refer_type_other", "其他"),

	// refer_sourceint(2) NOT NULL推荐来源：1：普通url 2：站内注册 3：微博 4：微信
	MEMBER_REFER_SOURCE_URL(1, "member_refer_source_url", "普通url"), MEMBER_REFER_SOURCE_WEB(2, "member_refer_source_web", "站内注册"), MEMBER_REFER_SOURCE_WEIBO(
			3, "member_refer_source_weibo", "微博"), MEMBER_REFER_SOURCE_WEIXIN(4, "member_refer_source_weixin", "微信"),

	/**
	 * 优惠券模块 0-未领取 1-已领取，未使用 2-已使用 3-未领取，已过期 4-已领取，已过期
	 */

	/**
	 * 未领取
	 */
	COUPON_STATUS_NOT_RECEIVED(0, "COUPON_STATUS_NOT_RECEIVED", "未领取"),
	/**
	 * 已领取，未使用
	 */
	COUPON_STATUS_RECEIVED_NOT_USED(1, "COUPON_STATUS_RECEIVED_NOT_USED", "已领取，未使用"),
	/**
	 * 已使用
	 */
	COUPON_STATUS_USED(2, "COUPON_STATUS_USED", "已使用"),
	/**
	 * 未领取，已过期
	 */
	COUPON_STATUS_NOT_RECEIVED_EXPIRE(3, "COUPON_STATUS_RECEIVED_EXPIRE", "未领取，已过期 "),
	/**
	 * 已领取，已过期
	 */
	COUPON_STATUS_RECEIVED_EXPIRE(4, "COUPON_STATUS_RECEIVED_EXPIRE", "已领取，已过期"),

	/**
	 * 使用中
	 */
	COUPON_STATUS_USING(5, "COUPON_STATUS_USING", "使用中"),

	/**
	 * 资金模块-放款管理 0-未放款 1-已放款
	 */
	/**
	 * 0-未放款
	 */
	FIN_LOAN_NOT_LOAN(0, "FIN_LOAN_NOT_LOAN", "未放款"),
	/**
	 * 1-已放款
	 */
	FIN_LOAN_LOANED(1, "FIN_LOAN_LOANED", "已放款"),

	/**
	 * 放款状态 0-未放款 1-部分放款 2-全部放款
	 */
	/**
	 * 0-待放款
	 */
	FIN_LOAN_WAIT_LOAN(0, "FIN_LOAN_WAIT_LOAN", "待放款"),
	/**
	 * 1-部分放款
	 */
	FIN_LOAN_PART_LOAN(1, "FIN_LOAN_PART_LOAN", "部分放款"),
	/**
	 * 2-全部放款
	 */
	FIN_LOAN_ALL_LOAN(2, "FIN_LOAN_ALL_LOAN", "全部放款"),

	/**
	 * 托管还本付息状态 0-待还款 1-当期已付 2-还本付息
	 */
	/**
	 * 0-待付款
	 */
	FIN_PAY_PRINCIPAL_INTEREST_WAIT_PAY(0, "fin_pay_principal_interest_wait_pay", "待付款"),
	/**
	 * 1-当期已付
	 */
	FIN_PAY_PRINCIPAL_INTEREST_CURRENT_PAID(1, "fin_pay_principal_interest_current_paid", "当期已付"),

	/**
	 * 租赁分红模块
	 */
	/**
	 * 项目是否参与分红 0-否 1-是
	 */
	/**
	 * 0-否
	 */
	LEASE_BONUS_NOT_JOIN_LEASE(0, "LEASE_BONUS_NOT_JOIN_LEASE", "否"),
	/**
	 * 1-是
	 */
	LEASE_BONUS_JOIN_LEASE(1, "LEASE_BONUS_JOIN_LEASE", "是"),

	/**
	 * 租赁状态
	 */
	/**
	 * 租赁分红-分红状态 1-分红中 2-已分红
	 */
	/**
	 * 1-分红中
	 */
	LEASE_BONUS_DOING_BONUS(1, "LEASE_BONUS_DOING_BONUS", "分红中"),
	/**
	 * 2-已分红
	 */
	LEASE_BONUS_DID_BONUS(2, "LEASE_BONUS_DID_BONUS", "已分红"),

	/**
	 * 租赁分红-租赁状态 0-待租 1-已租
	 */
	/**
	 * 0-待租
	 */
	LEASE_BONUS_WAIT_LEASE(0, "LEASE_BONUS_WAIT_LEASE", "待租"),
	/**
	 * 1-已租
	 */
	LEASE_BONUS_BEEN_LEASED(1, "LEASE_BONUS_BEEN_LEASED", "已租"),

	/***
	 * 租赁分红租赁状态-前台显示
	 */
	/***
	 * 0-待租中
	 */
	LEASE_BONUS_WAIT_LEASE_FOR_FRONT(0, "LEASE_BONUS_WAIT_LEASE_FOR_FRONT", "待租中"),
	/**
	 * 1-已租赁,待租中
	 */
	LEASE_BONUS_BEEN_LEASE_FOR_FRONT(1, "LEASE_BONUS_BEEN_LEASE_FOR_FRONT", "已租赁,待租中"),
	/**
	 * 2-未出租
	 */
	LEASE_BONUS_NOT_LEASE_FOR_FRONT(2, "LEASE_BONUS_NOT_LEASE_FOR_FRONT", "未出租"),
	/**
	 * 3-已租赁，结束租赁
	 */
	LEASE_BONUS_BEEN_END_LEASE_FOR_FRONT(3, "LEASE_BONUS_BEEN_END_LEASE_FOR_FRONT", "已租赁，结束租赁"),

	/***
	 * 租赁分红分红状态-前台显示
	 */
	/**
	 * 0-待分红
	 */
	LEASE_BONUS_WAIT_BONUS_FOR_FRONT(0, "LEASE_BONUS_WAIT_BONUS_FOR_FRONT", "待分红"),
	/**
	 * 1-已分红
	 */
	LEASE_BONUS_BEEN_BONUS_FOR_FRONT(1, "LEASE_BONUS_BEEN_BONUS_FOR_FRONT", "已分红"),
	/**
	 * 2-未分红
	 */
	LEASE_BONUS_NOT_BONUS_FOR_FRONT(2, "LEASE_BONUS_NOT_BONUS_FOR_FRONT", "未分红"),

	/**
	 * 1-正常
	 */
	DEL_STATUS_NORMAL(1, "DEL_STATUS_NORMAL", "正常"),
	/**
	 * -1-删除
	 */
	DEL_STATUS_DEL(-1, "DEL_STATUS_DEL", "删除"),

	/** 优惠券客户端支持 **/
	/**
	 * 0-不支持
	 */
	COUPON_CLIENT_NOT_SUPPORT(0, "COUPON_CLIENT_NOT_SUPPORT", "不支持"),
	/**
	 * 1-支持
	 */
	COUPON_CLIENT_SUPPORT(1, "COUPON_CLIENT_SUPPORT", "支持"),

	/***
	 * 优惠券使用是否受限
	 */
	/**
	 * 不受限
	 */
	COUPON_USE_NO_LIMITED(0, "COUPON_USE_NO_LIMITED", "不受限"),

	/**
	 * 受限
	 */
	COUPON_USE_LIMITED(1, "COUPON_USE_LIMITED", "受限"),
	/**
	 * 出行方式（汽车，火车，飞机）
	 */
	TRAVEL_MODE_ONE(1, "ONE", "汽车"),

	TRAVEL_MODE_TWO(2, "TWO", "火车"),

	TRAVEL_MODE_THREE(3, "THREE", "飞机"),

	/**
	 * 抽奖扣除筹码方式
	 */
	LOTTERY_DEDUCT_MODE_FREE(0, "LOTTERY_DEDUCT_MODE_FREE", "不扣除"),

	LOTTERY_DEDUCT_MODE_COIN(1, "LOTTERY_DEDUCT_MODE_COIN", "mc_activity_lottery表扣除兑换次数"),

	LOTTERY_DEDUCT_MODE_POPULAR(2, "LOTTERY_DEDUCT_MODE_POPULAR", "扣除人气值"),

	/**
	 * 活动状态
	 */
	ACTIVITY_STATUS_IS_AUDIT(2, "ACTIVITY_STATUS_IS_AUDITING", "审核中"),

	ACTIVITY_STATUS_IS_START(4, "ACTIVITY_STATUS_IS_START", "进行中"),

	ACTIVITY_STATUS_IS_END(6, "ACTIVITY_STATUS_IS_END", "已结束"),
	
	
	/**
	 * 逾期还本付息状态 0-逾期已垫资 1-逾期垫资已还
	 */
	/**
	 * 0-逾期已垫资
	 */
	OVERDUE_INTEREST_WAIT_PAY(0, "overude_interest_wait_pay", "逾期已垫资"),
	/**
	 * 1-逾期垫资已还
	 */
	OVERDUE_INTEREST_HAD_PAY(1, "overude_interest_had_pay", "逾期垫资已还"),


	/**
	 * 逾期记录表 还款状态  1:未还款，2 已还款 ，3还款中
	 */
	/**
	 * 1-未还款
	 */
	OVERDUE_LOG_WAIT_PAY(1, "OVERDUE_LOG_WAIT_PAY", "未还款"),
	/**
	 * 2-已还款
	 */
	OVERDUE_LOG_HAD_PAY(2, "OVERDUE_LOG_HAD_PAY", "已还款"),
	/**
	 * 3-还款中
	 */
	OVERDUE_LOG_PAYING(3, "OVERDUE_LOG_PAYING", "还款中"),


	/**
	 * 双旦迎薪-按钮状态
	 */
	ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_WAITED(1, "ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_WAITED", "等待领取"),

	ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_RECEIVED_EFFECT(2, "ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_RECEIVED", "已领取，未过期"),

	ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_RECEIVED_EXPIRE(3, "ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_RECEIVED", "已领取，已过期"),

	ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_USED(4, "ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_USED", "已使用"),

	ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_RECEIVENOW(5, "ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_RECEIVENOW", "点击领取"),

	ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_WAITING(6, "ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_WAITING", "热情等待中"),

	ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_UNLOGIN(7, "ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_UNLOGIN", "登录查看"),

	ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_END(8, "ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_END", "活动结束"),
	
	/**
	 * 红包状态
	 */
	REDPACKAGE_STATUS_NULL(0, "REDPACKAGE_STATUS_NULL", ""),
	REDPACKAGE_STATUS_SEND(1, "REDPACKAGE_STATUS_SEND", "发红包"),
	REDPACKAGE_STATUS_EMPTY(2, "REDPACKAGE_STATUS_EMPTY", "红包已抢完"),
	REDPACKAGE_STATUS_TIMEOUT(3, "REDPACKAGE_STATUS_TIMEOUT", "红包已过期"),

	/**
	 * 垫付状态[1:未垫付，2：垫付中，3：已垫付，4:已结束]
	 */
	UNDERWRITE_STATUS_WAIT_UNDERWRITE(1, "UNDERWRITE_STATUS_WAIT_UNDERWRITE", "未垫付"),
	UNDERWRITE_STATUS_UNDERWRITEING(2, "UNDERWRITE_STATUS_UNDERWRITEING", "垫付中"),
	UNDERWRITE_STATUS_HAD_UNDERWRITE(3, "UNDERWRITE_STATUS_HAD_UNDERWRITE", "已垫付"),
	UNDERWRITE_STATUS_OVER_UNDERWRITE(4, "UNDERWRITE_STATUS_OVER_UNDERWRITE", "已结束"),
	
	/**
	 * 逾期未还本息 1：有，2：无
	 */
	OVERDUE_STATUS_YES(1, "OVERDUE_STATUS_YES", "有"),
	OVERDUE_STATUS_NO(2, "OVERDUE_STATUS_NO", "无"),
	
	/**
	 * 还款方式 1：线上；2：线下
	 */
	OVERDUE_REPAYTYPE_ONLINE(1, "OVERDUE_REPAYTYPE_ONLINE", "线上"),
	OVERDUE_REPAYTYPE_UNDERLINE(2, "OVERDUE_REPAYTYPE_UNDERLINE", "线下"),
	
	/**
	 * 逾期还款记录
	 * 还款状态（1-未还款；2-还款中；3-已还款；4-还款失败）
	 */
	OVERDUE_REPAYSTATUS_NOPAY(1, "OVERDUE_REPAYSTATUS_NOPAY", "未还款"),
	OVERDUE_REPAYSTATUS_PAYING(2, "OVERDUE_REPAYSTATUS_PAYING", "还款中"),
	OVERDUE_REPAYSTATUS_HADPAY(3, "OVERDUE_REPAYSTATUS_HADPAY", "已还款"),
	OVERDUE_REPAYSTATUS_PAYFAIL(4, "OVERDUE_REPAYSTATUS_PAYFAIL", "还款失败"),
	
	/**
	 * 管理费收取状态[1:未收取，2：收取中，3 已收取]
	 */
	/**
	 * 1:未收取
	 */
	MANAGEMENT_FEE_GATHER_STATE_WAIT_GATHER(1,"MANAGEMENT_FEE_GATHER_STATE_WAIT_GATHER","未收取"),
	/**
	 * 2：收取中
	 */
	MANAGEMENT_FEE_GATHER_STATE_GATHERING(2,"MANAGEMENT_FEE_GATHER_STATE_GATHERING","收取中"),
	/**
	 * 3 已收取
	 */
	MANAGEMENT_FEE_GATHER_STATE_GATHERED(3,"MANAGEMENT_FEE_GATHER_STATE_GATHERED","已收取"),
	/**
	 * 5：已归还
	 */
	MANAGEMENT_FEE_GATHER_STATE_RETURNED(5,"MANAGEMENT_FEE_GATHER_STATE_RETURNED","已归还"),
	
	/**
	 * 本息记录状态 0：待还款  1：已还款 3：逾期
	 */
	INTEREST_RECORD_WAITPAY(0, "INTEREST_RECORD_WAITPAY", "待还款"),
	INTEREST_RECORD_UNDERWRITE(2, "INTEREST_RECORD_UNDERWRITE", "垫资还款"),
	INTEREST_RECORD_HADPAY(1, "INTEREST_RECORD_HADPAY", "已还款"),
	INTEREST_RECORD_OVERDUE(3, "INTEREST_RECORD_OVERDUE", "逾期"),
	
	/**
	 * 本息记录-备注 0：正常还款，1：需补还垫资，2：垫资已补还，3：-
	 */
	INTEREST_OVERDUE_REMARK_NORMALPAY(0, "INTEREST_OVERDUE_REMARK_NORMALPAY", "正常还款"),
	INTEREST_OVERDUE_REMARK_UNDERPPAY(1, "INTEREST_OVERDUE_REMARK_UNDERPPAY", "需补还垫资"),
	INTEREST_OVERDUE_REMARK_UNDERHADPAY(2, "INTEREST_OVERDUE_REMARK_UNDERHADPAY", "垫资已补还"),
	INTEREST_OVERDUE_REMARK_NO(3, "INTEREST_OVERDUE_REMARK_NO", "-"),
	INTEREST_OVERDUE_WAIT_PAY(4, "INTEREST_OVERDUE_WAIT_PAY", "待支付"),
	
	/**
	 * 借款周期类型 1-日；2-月；3-年
	 */
	BORROW_PERIOD_TYPE_DAY(1, "BORROW_PERIOD_TYPE_DAY", "天"),
	BORROW_PERIOD_TYPE_MONTH(2, "BORROW_PERIOD_TYPE_MONTH", "个月"),
	BORROW_PERIOD_TYPE_YEAR(3, "BORROW_PERIOD_TYPE_YEAR", "年"),
	
	/**
	 * 流标原因 1， 募集未完成  2，项目审核未通过
	 */
	LABEL_REASON_RAISE_NOCOMP(1, "LABEL_REASON_RAISE_NOCOMP", "募集未完成"),
	LABEL_REASON_RAISE_NOPASS(2, "LABEL_REASON_RAISE_NOPASS", "项目审核未通过"),
	
	/**
	 * 合同签署
	 * 甲方 -1  ；  乙方 -2 ； 第三方 -3；
	 */
	CONTRACT_PARTY_FIRST(1, "CONTRACT_PARTY_FIRST", "甲方"),
	CONTRACT_PARTY_SECOND(2, "CONTRACT_PARTY_SECOND", "乙方"),
	CONTRACT_PARTY_THIRD(3, "CONTRACT_PARTY_THIRD", "第三方，即小融"),
	/**
	 * 合同签署
	 * 未签署 -0  ；  签署中 -1 ； 签署成功 -2； 签署失败 -3；
	 */
	CONTRACT_SIGN_STATUS_UNSIGN(0, "CONTRACT_SIGN_STATUS_UNSIGN", "初始化状态，未签署"),
	CONTRACT_SIGN_STATUS_SIGNING(1, "CONTRACT_SIGN_STATUS_SIGNING", "签署中"),
	CONTRACT_SIGN_STATUS_SUCCESS(2, "CONTRACT_SIGN_STATUS_UNSIGN", "签署成功"),
	CONTRACT_SIGN_STATUS_FAIL(3, "CONTRACT_SIGN_STATUS_UNSIGN", "签署失败"),
	/**
	 * 合同签署
	 * 甲方类别 1-个人 2-企业；
	 */
	CONTRACT_SIGN_FIRST_PERSON(1, "CONTRACT_SIGN_FIRST_PERSON", "个人"),
	CONTRACT_SIGN_FIRST_ENTERPRISE(2, "CONTRACT_SIGN_FIRST_COMPANY", "企业"),
	/**
	 * 合同签署
	 * 签署途径  1-PC 2-mobile；
	 */
	CONTRACT_SIGN_WAY_PC(1, "CONTRACT_SIGN_WAY_PC", "PC"),
	CONTRACT_SIGN_WAY_MOBILE(2, "CONTRACT_SIGN_WAY_MOBILE", "MOBILE"),
	/**
	 * 合同状态
	 * 0-初始化，1-未签署，2-已签署，3-已过期
	 */
	CONTRACT_STATUS_INIT(0, "CONTRACT_STATUS_INIT", "初始化状态"),
	CONTRACT_STATUS_UNSIGN(1, "CONTRACT_STATUS_UNSIGN", "未签署"),
	CONTRACT_STATUS_ALREADY_SIGN(2, "CONTRACT_STATUS_ALREADY_SIGN", "已签署"),
	CONTRACT_STATUS_EXPIRED(3, "CONTRACT_STATUS_EXPIRED", "已过期"),
	/**
	 * 手签合同发起方
	 * 1-安卓,2-ios,3-M站,4-PC
	 *
	 */
	CONTRACT_MANUAL_WAY_ANDROID(1, "CONTRACT_MANUAL_WAY_ANDROID", "安卓"),
	CONTRACT_MANUAL_WAY_IOS(2, "CONTRACT_MANUAL_WAY_IOS", "苹果"),
	CONTRACT_MANUAL_WAY_M(3, "CONTRACT_MANUAL_WAY_M", "M站"),
	CONTRACT_MANUAL_WAY_PC(4, "CONTRACT_MANUAL_WAY_PC", "PC"),
	
	/**
	 * 是否委托扣款授权
	 */
	WITHHOLD_AUTHORITY_FLAG_Y(1, "WITHHOLD_AUTHORITY_FLAG_Y", "已开通委托扣款"),
	WITHHOLD_AUTHORITY_FLAG_N(0, "WITHHOLD_AUTHORITY_FLAG_N", "未开通委托扣款"),
	
	/**
	 * 是否设置支付密码
	 */
	SET_PAY_SUCCESS_FLAG_N(0, "SET_PAY_SUCCESS_FLAG_N", "未设置支付密码"),
	SET_PAY_SUCCESS_FLAG_Y(1, "SET_PAY_SUCCESS_FLAG_Y", "已设置支付密码"),
	
	/**
	 * 1-开启，2-关闭
	 */
	INVEST_FLAG_OPEN(1, "INVEST_FLAG_OPEN", "自动投标开启"),
	INVEST_FLAG_CLOSE(2, "INVEST_FLAG_CLOSE", "自动投标关闭"),
	
	/**
	 * 投标频率：1 每天投标一次  2 每个项目投标一次
	 */
	INVEST_FREQUENCY_EVERYDAY(1, "INVEST_FREQUENCY_EVERYDAY", "每天投标一次"),
	INVEST_FREQUENCY_EVERYPROJECT(2, "INVEST_FREQUENCY_EVERYPROJECT", "每个项目投标一次"),
	
	/**
	 * 投标频率：1 每天投标一次  2 每个项目投标一次
	 */
	MEMBER_INVEST_FLAG_HAND(0, "MEMBER_INVEST_FLAG_HAND", "手动投标"),
	MEMBER_INVEST_FLAG_AUTO(1, "MEMBER_INVEST_FLAG_AUTO", "自动投标"),
	
	
	/**
	 * 抽奖状态 抽奖or未抽奖
	 */
	LOTTERY_STATUS_NO(0, "LOTTERY_STATUS_NO", "未抽"),
	LOTTERY_STATUS_YES(1, "LOTTERY_STATUS_YES", "已抽"),
	
	/**
	 * 1-待发放 2-已发放 3-已取消
	 */
	SHOP_ORDER_TO_SENT(1, "SHOP_ORDER_TO_SENT", "待发送"),
	
	SHOP_ORDER_SENTED(2, "SHOP_ORDER_SENTED", "已发放"),
	
	SHOP_ORDER_CANCLE(3, "SHOP_ORDER_CANCLE", "已取消"),
	
	/**
	 * 直投项目授信额度状态， 1-正常，0-超出授信额
	 */
	DIRECT_BORROWER_CREDIT_STATUS_NORMAL(1, "DIRECT_BORROWER_CREDIT_STATUS_NORMAL", "正常"),
	
	DIRECT_BORROWER_CREDIT_STATUS_OVER(0, "DIRECT_BORROWER_CREDIT_STATUS_OVER", "超出授信额"),
	/**
	 * 项目包状态  0-未审核 1-募集中 2-售罄
	 */
	PROJECT_PACKAGE_STATUS_NO_AUDIT(0, "PROJECT_PACKAGE_STATUS_NO_AUDIT", "未审核"),
	
	PROJECT_PACKAGE_STATUS_INSALES(1, "PROJECT_PACKAGE_STATUS_INSALES", "募集中"),
	
	PROJECT_PACKAGE_STATUS_SALESCOMPLETED(2, "PROJECT_PACKAGE_STATUS_SALESCOMPLETED", "售罄"),
	
	
	IS_PROJECT_PACKAGE_FLAG(1,"PROJECT_PACKAGE_FLAG","资产包项目"),
	
	NO_PROJECT_PACKAGE_FLAG(0,"PROJECT_PACKAGE_FLAG","非资产包项目")
	;

	private static Map<String, StatusEnum> enumMap;

	private int status;

	private String code;

	private String desc;

	StatusEnum(int status, String code, String desc) {
		this.status = status;
		this.code = code;
		this.desc = desc;
	}

	public static Map<String, StatusEnum> getMap() {
		if (enumMap == null) {
			enumMap = new HashMap<String, StatusEnum>();
			for (StatusEnum status : StatusEnum.values()) {
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

	public static StatusEnum getByCode(String code) {
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
