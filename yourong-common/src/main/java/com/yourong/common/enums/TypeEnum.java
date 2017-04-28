package com.yourong.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 状态枚举类
 * 
 */
public enum TypeEnum {

	
	
	/***
	 * 资金记录表 
	 * 1:充值 2:提现 3:充值手续费 4：提现手续费 5:用户投资 6：现金券支出 7：收益券支出 8:利息收支 9：本金收支
	 */
	/**
	 * 1:充值
	 */
	FINCAPITALINOUT_TYPE_RECHARGE(1,"FINCAPITALINOUT_TYPE_RECHARGE","充值"),
	/**
	 * 2:提现
	 */
	FINCAPITALINOUT_TYPE_WITHDRAW(2,"FINCAPITALINOUT_TYPE_WITHDRAW","提现"),
	/**
	 * 3:充值手续费
	 */
	FINCAPITALINOUT_TYPE_RECHARGE_POUNDAGE(3,"FINCAPITALINOUT_TYPE_RECHARGE_POUNDAGE","充值手续费"),
	/**
	 * 4：提现手续费
	 */
	FINCAPITALINOUT_TYPE_WITHDRAW_POUNDAGE(4,"FINCAPITALINOUT_TYPE_WITHDRAW_POUNDAGE","提现手续费"),
	/**
	 * 5:用户投资
	 */
	FINCAPITALINOUT_TYPE_INVEST(5,"FINCAPITALINOUT_TYPE_INVEST","用户投资"),
	/**
	 * 6：现金券支出
	 */
	FINCAPITALINOUT_TYPE_CASH(6,"FINCAPITALINOUT_TYPE_CASH","现金券支出 "),
	/**
	 * 7：收益券支出
	 */
	FINCAPITALINOUT_TYPE_EARNINGS(7,"FINCAPITALINOUT_TYPE_EARNINGS","收益券支出"),
	/**
	 * 8:利息收支
	 */
	FINCAPITALINOUT_TYPE_INTEREST(8,"FINCAPITALINOUT_TYPE_INTEREST","利息收支"),
	/**
	 * 9：本金收支
	 */
	FINCAPITALINOUT_TYPE_PRINCIPAL(9,"FINCAPITALINOUT_TYPE_PRINCIPAL","本金收支"),
	/**
	 * 10:新浪存钱罐收益
	 */
	FINCAPITALINOUT_TYPE_THIRDPAY(10,"FINCAPITALINOUT_TYPE_PRINCIPAL","新浪存钱罐收益"),
	/**
	 * 11:满标放款
	 */
	FINCAPITALINOUT_TYPE_LOAN(11,"FINCAPITALINOUT_TYPE_LOAN","满标放款"),
	/**
	 * 12:原始债权人还款
	 */
	FINCAPITALINOUT_TYPE_RETURN(12,"FINCAPITALINOUT_TYPE_RETURN","原始债权人还款"),
	/**
	 * 13:直接代付
	 */
	FINCAPITALINOUT_TYPE_DIRECT_PAY(13,"FINCAPITALINOUT_TYPE_DIRECT_PAY","直接代付"),
	/**
	 * 14:直接代收
	 */
	FINCAPITALINOUT_TYPE_DIRECT_COLLECT(14,"FINCAPITALINOUT_TYPE_DIRECT_COLLECT","直接代收"),
	/**
	 * 16:平台代付租赁分红代收
	 */
	FINCAPITALINOUT_TYPE_LEASE_BONUS(16,"FINCAPITALINOUT_TYPE_LEASE_BONUS","平台代付租赁分红代收"),
	/**
	 * 17:租赁分红代付资金流水
	 */
	FINCAPITALINOUT_TYPE_PAY_LEASE_BONUS(17,"FINCAPITALINOUT_TYPE_PAY_LEASE_BONUS","租赁分红代付资金流水"),
	/**
	 * 18:资金回退
	 */
	FINCAPITALINOUT_TYPE_CAPITAL_FALLBACK(18,"FINCAPITALINOUT_TYPE_CAPITAL_FALLBACK","资金回退"),
	/**
	 * 19:垫资还款
	 */
	FINCAPITALINOUT_TYPE_UNDERWRITE_TYPE(19,"FINCAPITALINOUT_TYPE_UNDERWRITE_TYPE","垫资还款"),
	/**
	 * 20：直投项目管理费
	 */
	FINCAPITALINOUT_TYPE_PAY_MANAGER_FEE(20,"FINCAPITALINOUT_TYPE_PAY_OVERDUE_FINE","直投项目管理费"),
	/**
	 * 21：保证金
	 */
	FINCAPITALINOUT_TYPE_PAY_GUARANTEE_FEE(21, "FINCAPITALINOUT_TYPE_PAY_GUARANTEE_FEE", "保证金"),
	/**
	 * 22：风险金
	 */
	FINCAPITALINOUT_TYPE_PAY_RISK_FEE(22, "FINCAPITALINOUT_TYPE_PAY_RISK_FEE", "风险金"),
	/**
	 * 23：滞纳金
	 */
	FINCAPITALINOUT_TYPE_PAY_OVERDUE(23,"FINCAPITALINOUT_TYPE_PAY_OVERDUE","滞纳金"),
	/**
	 * 24：项目介绍费
	 */
	FINCAPITALINOUT_TYPE_PAY_INTRODUCE_FEE(24, "FINCAPITALINOUT_TYPE_PAY_INTRODUCE_FEE", "项目介绍费"),
	/**
	 * 25：转让收款
	 */
	FINCAPITALINOUT_TYPE_PAY_TRANSFER_PAYMENT(25, "FINCAPITALINOUT_TYPE_PAY_TRANSFER_PAYMENT", "转让项目收款"),
	/**
	 * 26：转让项目手续费
	 */
	FINCAPITALINOUT_TYPE_PAY_TRANSFER_FEE(26, "FINCAPITALINOUT_TYPE_PAY_TRANSFER_FEE", "转让项目手续费"),
	/**
	 * 27：快投发放奖励
	 */
	FINCAPITALINOUT_TYPE_PAY_QUICK_REWARD(27, "FINCAPITALINOUT_TYPE_PAY_QUICK_REWARD", "快投发放奖励"),

	/**
	 * 人气值流水类型 1-推荐好友；2-平台活动；3-平台派送；4-兑换优惠券；5-补发人气值
	 */
	/**
	 * 1-推荐好友
	 */
	FIN_POPULARITY_TYPE_RECOMMEND(1,"FIN_POPULARITY_TYPE_RECOMMEND","推荐好友"),
	/**
	 * 2-平台活动
	 */
	FIN_POPULARITY_TYPE_ACTIVITY(2,"FIN_POPULARITY_TYPE_ACTIVITY","平台活动"),
	/**
	 * 3-平台派送
	 */
	FIN_POPULARITY_TYPE_SEND(3,"FIN_POPULARITY_TYPE_SEND","平台派送"),
	/**
	 * 4-兑换现金券
	 */
	FIN_POPULARITY_TYPE_EXCHANGE(4,"FIN_POPULARITY_TYPE_EXCHANGE","兑换现金券"),
	/**
	 * 5-补发人气值
	 */
	FIN_POPULARITY_TYPE_REISSUE(5,"FIN_POPULARITY_TYPE_REISSUE","补发人气值"),
	/**
	 * 6-签到送人气值
	 */
	FIN_POPULARITY_TYPE_CHECK(6,"FIN_POPULARITY_TYPE_CHECK","每日签到"),
	
	/**
	 * 7-提现手续费
	 */
	FIN_POPULARITY_TYPE_WITHDRAW(7,"FIN_POPULARITY_TYPE_WITHDRAW","提现手续费"),
	
	/**
	 * 8-兑换收益券
	 */
	FIN_POPULARITY_TYPE_PROFIT_EXCHANGE(8,"FIN_POPULARITY_TYPE_PROFIT_EXCHANGE","兑换收益券"),
	/**
	 * 9-人气值商城消费
	 */
	FIN_POPULARITY_TYPE_SHOP_CONSUME(9,"FIN_POPULARITY_TYPE_WITHDRAW","商城消费"),
	/**
	 * 10-人气值过期
	 */
	FIN_POPULARITY_TYPE_OVERDUE_CONSUME(10,"FIN_POPULARITY_TYPE_OVERDUE_CONSUME","人气值过期"),
	
	/**存钱罐账户**/
	FINCAPITALINOUT_PAYACCOUNTTYPE_MAIN(1,"FINCAPITALINOUT_TYPE_PRINCIPAL","存钱罐账户"),
	/**保证金账户**/
	FINCAPITALINOUT_PAYACCOUNTTYPE_ENSURE(2,"FINCAPITALINOUT_TYPE_PRINCIPAL","保证金账户"),
	/**基本户账户**/
	FINCAPITALINOUT_PAYACCOUNTTYPE_BASE(3,"FINCAPITALINOUT_TYPE_PRINCIPAL","基本户账户"),
	
		
	

	/**
	 * 余额类型
	 * 1-存钱罐余额；2-企业基本户余额 3-风险保证金余额 4-项目资金余额
	 */
	/**
	 * 存钱罐余额
	 */
	BALANCE_TYPE_PIGGY(1, "BALANCE_TYPE_PIGGY", "存钱罐余额"),
	/**
	 * 企业基本户余额
	 */
	BALANCE_TYPE_BASIC(2, "BALANCE_TYPE_BASIC", "企业基本户余额"),
	/**
	 * 风险保证金余额 
	 */
	BALANCE_TYPE_ENSURE(3, "BALANCE_TYPE_ENSURE", "风险保证金余额 "),
	/**
	 * 项目资金余额
	 */
	BALANCE_TYPE_PROJECT(4, "BALANCE_TYPE_PROJECT", "项目资金余额"),
	/**
	 * 平台累计投资总额
	 */
	BALANCE_TYPE_PLATFORM_TOTAL_INVEST(5, "BALANCE_TYPE_PLATFORM_TOTAL_INVEST", "平台累计投资总额"),
	/**
	 * 平台累计为投资人赚取利息
	 */
	BALANCE_TYPE_PLATFORM_TOTAL_INTEREST(6, "BALANCE_TYPE_PLATFORM_TOTAL_INTEREST", "平台累计为投资人赚取利息"),
	/**
	 * 用户累计存钱罐收益
	 */
	BALANCE_TYPE_MEMBER_TOTAL_PIGGY(7, "BALANCE_TYPE_MEMBER_TOTAL_PIGGY", "用户累计存钱罐收益"),
	/**
	 * 人气值（可用于兑换优惠券）
	 */
	BALANCE_TYPE_MEMBER_POPULARITY(8, "BALANCE_TYPE_MEMBER_POPULARITY", "人气值"),
	/**
	 * 平台累计送出人气值
	 */
	BALANCE_TYPE_PLATFORM_TOTAL_GIVE(9,"BALANCE_TYPE_PLATFORM_TOTAL_GIVE","平台累计送出人气值"),
	/**
	 * 平台累计兑换人气值
	 */
	BALANCE_TYPE_PLATFORM_TOTAL_RECHARGE(10,"BALANCE_TYPE_PLATFORM_TOTAL_RECHARGE","平台累计兑换人气值"),
	/**
	 * 企业信用额度
	 */
	BALANCE_TYPE_COMPANY_CREDIT_AMOUNT(11,"BALANCE_TYPE_COMPANY_CREDIT_AMOUNT","企业信用额度"),
	/**
	 * 转让认购本金
	 */
	BALANCE_TYPE_TRANSFER_AMOUNT(12, "BALANCE_TYPE_TRANSFER_AMOUNT", "转让认购本金"),
	
		
	
	/**
	 * 代收交易类型
	 * 1-用户投资 2-借款人还款 3-基本户现金券投资垫付代收 4-基本户收益权付息垫付代收
	 */
	/**
	 * 1-用户投资
	 */
	HOSTING_COLLECT_TRADE_INVEST(1, "HOSTING_COLLECT_TRADE_INVEST", "用户投资"),
	/**
	 * 2-借款人还款
	 */
	HOSTING_COLLECT_TRADE_RETURN(2, "HOSTING_COLLECT_TRADE_RETURN", "借款人还款"),
	/**
	 * 3-基本户现金券投资垫付代收
	 */
	HOSTING_COLLECT_TRADE_COUPON_A(3, "HOSTING_COLLECT_TRADE_COUPON_A", "基本户现金券投资垫付代收"),
	/**
	 * 4-基本户收益券付息垫付代收
	 */
	HOSTING_COLLECT_TRADE_COUPON_B(4, "HOSTING_COLLECT_TRADE_COUPON_B", "基本户收益券付息垫付代收"),
	/**
	 *  5-直接代收
	 */
	HOSTING_COLLECT_DIRECT_PAY(5, "HOSTING_COLLECT_DIRECT_PAY", "直接代收"),
	/**
	 * 6-基本户垫付租赁分红
	 */
	HOSTING_COLLECT_TRADE_LEASE_BONUS(6, "HOSTING_COLLECT_TRADE_LEASE_BONUS", "基本户垫付租赁分红"),
	/**
	 * 7-垫资代收
	 */
	HOSTING_COLLECT_TRADE_OVERDUE_REPAY(7, "HOSTING_COLLECT_TRADE_OVERDUE_REPAY", "垫资还款代收"),
	/**
	 * 8-项目保证金归还代收
	 */
	HOSTING_COLLECT_TRADE_PROJECT_GUARNANTEEFEE(8, "HOSTING_COLLECT_TRADE_PROJECT_GUARNANTEEFEE", "项目保证金归还代收"),
	
	/**
	 * 代付交易类型
	 * 1-放款给借款人 2-还款给投资人
	 */
	/**
	 * 1-放款给出借人
	 */
	HOSTING_PAY_TRADE_LOAN(1, "HOSTING_PAY_TRADE_LOAN", "放款给出借人"),
	/**
	 *  2-还款给投资人
	 */
	HOSTING_PAY_TRADE_RETURN(2, "HOSTING_PAY_TRADE_RETURN", "还款给投资人"),
	/**
	 *  3-直接代付
	 */
	HOSTING_PAY_DIRECT_PAY(3, "HOSTING_PAY_DIRECT_PAY", "直接代付"),
	/**
	 *  4-收益分红代付
	 */
	HOSTING_PAY_LEASE_BONUS(4, "HOSTING_PAY_LEASE_BONUS", "收益分红代付"),
	/**
	 *  5-放款给借款人
	 */
	HOSTING_PAY_TRADE_LOAN_BORROWER(5, "HOSTING_PAY_TRADE_LOAN_BORROWER", "放款给借款人"),
	/**
	 *  6-直投项目管理费
	 */
	HOSTING_PAY_TRADE_MANAGER_FEE(6,"HOSTING_PAY_TRADE_MANAGER_FEE","直投项目管理费"),
	/**
	 *  7-垫资还款
	 */
	HOSTING_PAY_TRADE_OVERDUE_REPAY(7,"HOSTING_PAY_TRADE_OVERDUE_REPAY","垫资还款"),
	/**
	 *  8-项目保证金
	 */
	HOSTING_PAY_TRADE_GUARANTEEFEE(8,"HOSTING_PAY_TRADE_GUARANTEEFEE","项目保证金还款"),
	/**
	 * 9-项目风险金
	 */
	HOSTING_PAY_TRADE_RISK_FEE(9, "HOSTING_PAY_TRADE_RISK_FEE", "项目风险金还款"),
	/**
	 * 10-项目保证金归还给借款人
	 */
	HOSTING_PAY_TRADE_GUARANTEEFEE_RETURN(10, "HOSTING_PAY_TRADE_GUARANTEEFEE_RETURN", "项目保证金还款"),
	/**
	 * 11-项目介绍费
	 */
	HOSTING_PAY_TRADE_INTRODUCE_FEE(11, "HOSTING_PAY_TRADE_INTRODUCE_FEE", "项目介绍费"),
	/**
	 * 12-转让项目转让转让款
	 */
	HOSTING_PAY_TRADE_TRANSFER_AMOUNT(12, "HOSTING_PAY_TRADE_TRANSFER_AMOUNT", "转让项目转让款"),
	/**
	 * 13-转让项目手续费
	 */
	HOSTING_PAY_TRADE_TRANSFER_FEE(13, "HOSTING_PAY_TRADE_TRANSFER_FEE", "转让项目手续费"),
	
	/**
	 * 14-快投奖励发放
	 */
	HOSTING_PAY_TRADE_QUICK_REWARD(14, "HOSTING_PAY_TRADE_QUICK_REWARD", "快投奖励发放"),
	
	
	/**
	 * 优惠券类型
	 */
	/** 现金券 **/
	COUPON_TYPE_CASH(1, "COUPON_TYPE_CASH", "现金券"),
	/** 收益券 **/
	COUPON_TYPE_INCOME(2, "COUPON_TYPE_INCOME", "收益券"),
	
	/**
	 * 发送消息类型
	 */
	/**充值成功**/
	SEND_MSG_NOTIFY_TYPE_RECHARGE_SUCCESS(1, "SEND_MSG_NOTIFY_TYPE_RECHARGE_SUCCESS", "充值成功"),
	/**奖励活动**/
	SEND_MSG_NOTIFY_TYPE_AWARD_ACTIVITY(3, "SEND_MSG_NOTIFY_TYPE_AWARD_ACTIVITY", "奖励活动"),
	/**投资成功**/
	SEND_MSG_NOTIFY_TYPE_TRANSACTION_SUCCESS(4, "SEND_MSG_NOTIFY_TYPE_TRANSACTION_SUCCESS", "投资成功"),
	/**利息支付**/
	SEND_MSG_NOTIFY_TYPE_PAY_INTEREST(5, "SEND_MSG_NOTIFY_TYPE_PAY_INTEREST", "利息支付"),
	/**本金归还**/
	SEND_MSG_NOTIFY_TYPE_PAY_PRINCIPAL(16, "SEND_MSG_NOTIFY_TYPE_PAY_PRINCIPAL", "本金归还"),
	/**新项目上线**/
	SEND_MSG_NOTIFY_TYPE_PROJECT_ONLINE(7, "SEND_MSG_NOTIFY_TYPE_PROJECT_ONLINE", "新项目上线"),
	/**项目预告**/
	SEND_MSG_NOTIFY_TYPE_PROJECT_NOTICE(8, "SEND_MSG_NOTIFY_TYPE_PROJECT_NOTICE", "项目预告"),
	/**提现成功**/
	SEND_MSG_NOTIFY_TYPE_WITHDRAW_SUCCESS(15, "SEND_MSG_NOTIFY_TYPE_WITHDRAW_SUCCESS", "提现成功"),
	/**提现失败**/
	SEND_MSG_NOTIFY_TYPE_WITHDRAW_FAILED(10, "SEND_MSG_NOTIFY_TYPE_WITHDRAW_FAILED", "提现失败"),
	/**提现拒绝**/
	SEND_MSG_NOTIFY_TYPE_WITHDRAW_REFUSE(11, "SEND_MSG_NOTIFY_TYPE_WITHDRAW_REFUSE", "提现拒绝"),
	/**订单失效提醒**/
	SEND_MSG_NOTIFY_TYPE_ORDER_INVALID(12, "SEND_MSG_NOTIFY_TYPE_ORDER_INVALID", "订单失效提醒"),
	/**提现状态**/
	SEND_MSG_NOTIFY_TYPE_WITHDRAW_STATUS(9, "SEND_MSG_NOTIFY_TYPE_WITHDRAW_STATUS", "提现状态"),
	/**资金到账**/
	SEND_MSG_NOTIFY_TYPE_PAY_STATUS(6, "SEND_MSG_NOTIFY_TYPE_PAY_STATUS", "资金到账（本金、利息）"),
	
	/**
	 * 存储方式：1-本地 2-OSS 3-又拍云
	 */
	/**1-本地**/
	ATTACHMENT_STORAGE_WAY_LOCAL(1, "ATTACHMENT_STORAGE_WAY_LOCAL", "1-本地"),
	/**2-OSS**/
	ATTACHMENT_STORAGE_WAY_OSS(2, "ATTACHMENT_STORAGE_WAY_OSS", "2-OSS"),
	/**3-又拍云**/
	ATTACHMENT_STORAGE_WAY_UPYUN(3, "ATTACHMENT_STORAGE_WAY_UPYUN", "3-又拍云"),
	
	/**
	 * 创建代收交易来源类型
	 */
	/**1-充值notify**/
	TRADE_SOURCE_TYPE_RECHARGE_NOTIFY(1, "TRADE_SOURCE_TYPE_RECHARGE_NOTIFY", "充值notify"),
	/**2-交易notify**/
	TRADE_SOURCE_TYPE_TRADE_NOTIFY(2, "TRADE_SOURCE_TYPE_TRADE_NOTIFY", "2-交易notify"),
	/**3-交易时调用（无需充值）**/
	TRADE_SOURCE_TYPE_TRANSACTION(3, "NOTIFY_TYPE_WITHDRAW", "3-交易时调用（无需充值）"),
	/**
	 * 充值状态
	 */
	RECHARGELOG_TYPE_DIRECTLY(1,"RECHARGELOG_TYPE_DIRECTLY","直接充值"),
	/**交易充值**/
	RECHARGELOG_TYPE_TRADING(2,"RECHARGELOG_TYPE_TRADING","交易充值"),
	/**
	 * 网银直连
	 */
	RECHARGELOG_PAY_METHOD_EBANK(1,"RECHARGELOG_TYPE_DIRECTLY","网银"),
	/**
	 * 绑卡支付
	 */
	RECHARGELOG_PAY_METHOD_QUICK(2,"RECHARGELOG_PAY_METHOD_QUICK","快捷"),
	
	/**
	 * 送红包的来源类型
	 */
	/**实名认证送红包**/
	COUPON_SEND_SOURCE_TYPE_IDENTITY_BIND(1,"COUPON_SEND_SOURCE_TYPE_IDENTITY_BIND","实名认证送红包"),
	/**绑定邮箱送红包**/
	COUPON_SEND_SOURCE_TYPE_EMAIL_BIND(2,"COUPON_SEND_SOURCE_TYPE_EMAIL_BIND","绑定邮箱送红包"),
	/**给推荐人注册返利送红包**/
	COUPON_SEND_SOURCE_TYPE_RECOMMEND_REGISTER(3,"COUPON_SEND_SOURCE_TYPE_RECOMMEND_REGISTER","给推荐人注册返利送红包"),
	/**给推荐人投资送红包**/
	COUPON_SEND_SOURCE_TYPE_RECOMMEND_INVESTMENT(4,"COUPON_SEND_SOURCE_TYPE_RECOMMEND_INVESTMENT","给推荐人投资送红包"),
	/**完善个人信息送红包**/
	COUPON_SEND_SOURCE_TYPE_COMPLETE_MEMBER_INFO(5,"COUPON_SEND_SOURCE_TYPE_COMPLETE_MEMBER_INFO","完善个人信息送红包"),
	
	
	// 优惠券发送渠道
	// 0-web，1-backend，2-app，3-m
	COUPON_WAY_WEB(0, "coupon_way_web", "发送渠道为web"), COUPON_WAY_BACKEND(1, "coupon_way_backend", "发送渠道为backend"), COUPON_WAY_APP(2,
			"coupon_way_app", "发送渠道为app"), COUPON_WAY_M(3, "coupon_way_m", "发送渠道为m"),COUPON_WAY_SHOP(4, "COUPON_WAY_SHOP", "发送渠道为人气值商城"),
	// 优惠券获取方式
	// 0-人气值兑换，1-注册，2-活动，3-后台补偿"
	COUPON_ACCESS_SOURCE_POPULARITY(0, "coupon_access_source_popularity", "获取方式:人气值兑换"), 
	COUPON_ACCESS_SOURCE_REGISTER(1, "coupon_access_source_register", "获取方式:注册"), 
	COUPON_ACCESS_SOURCE_ACTIVITY(2,"coupon_access_source_app", "获取方式:活动"), 
	COUPON_ACCESS_SOURCE_BACKEND_COMPENSATE(3, "coupon_access_source_backend_compensate", "获取方式:后台补偿"),
	COUPON_ACCESS_SOURCE_BACKEND_SHOP(4, "COUPON_ACCESS_SOURCE_BACKEND_SHOP", "获取方式:人气值商城兑换"),

	
	/***
	 * 服务费类型 1：项目管理费 2：风险金 3：保证金
	 */
	FEE_TYPE_MANAGE(1,"fee_type_manage","管理费"),
	FEE_TYPE_RISK(2,"fee_type_risk","风险金 "),
	FEE_TYPE_GUARANTEE(3,"fee_type_guarantee","保证金"),
	FEE_TYPE_INTRODUCER(4,"fee_type_introducer","介绍费"),
	
	/**
	 * 逾期结算类型 1-垫资逾期结算；2-普通逾期结算
	 */
	/**
	 * 1-垫资逾期结算
	 */
	OVERDUE_SETTLEMENT_TYPE_UNDERWRITE(1,"overdue_settlement_type_underwrite","垫资逾期结算 "),
	/**
	 * 2-普通逾期结算
	 */
	OVERDUE_SETTLEMENT_TYPE_COMMON(2,"overdue_settlement_type_common","普通逾期结算"),
	/**
	 * 会员认证类型
	 */
	/**手机认证**/
	MEMBER_VERIFY_TYPE_MOBILE(1,"mobile","手机认证"),
	/**身份证认证**/
	MEMBER_VERIFY_TYPE_IDENTITY(2,"identity","身份证认证"),
	/**邮箱认证**/
	MEMBER_VERIFY_TYPE_EMAIL(3,"email","邮箱认证"),
	
	
	/**用户名登录 **/	
	MEMBER_LOGIN_TYPE_USERNAME(0,"username","用户名登录"),
	/**手机登录 **/
	MEMBER_LOGIN_TYPE_MOBILE(1,"mobile","手机登录"),
	/**email登录 **/
	MEMBER_LOGIN_TYPE_EMAIL(2,"email","email登录"),
	
	/**登录来源 pc**/
	MEMBER_LOGIN_SOURCE_PC(0,"pc","pc"),
	/**登录来源 android**/
	MEMBER_LOGIN_SOURCE_ANDROID(1,"android","android"),
	/**登录来源 ios**/
	MEMBER_LOGIN_SOURCE_IOS(2,"ios","ios"),
	/**登陆来源 mobile**/
	MEMBER_LOGIN_SOURCE_MOBILE(3,"mobile","手机浏览器登陆"),
	/**登陆来源 微信**/
	MEMBER_LOGIN_SOURCE_WEIXIN(4,"微信","微信登陆"),
	
	/** 支付来源 **/
	ORDER_SOURCE_PC(0,"pc","pc"),
	/** 支付来源 wap **/
	ORDER_SOURCE_WAP(3,"wap","手机浏览器"),
	/**
	 * banner投放渠道
	 * 0-pc端 1-app端 2-M站
	 */
	/**
	 * 0-pc端
	 */
	BANNER_CHANNEL_TYPE_PC(0,"BANNER_CHANNEL_TYPE_PC","pc端"),

	/**
	 * 1-app端
	 */
	BANNER_CHANNEL_TYPE_APP(1,"BANNER_CHANNEL_TYPE_APP","app端"),
	
	/**
	 * 2-M站
	 */
	BANNER_CHANNEL_TYPE_M(2,"BANNER_CHANNEL_TYPE_M","m站"),
	/**
	 * 3-app活动页
	 */
	BANNER_CHANNEL_TYPE_APP_ACTIVITY(3,"BANNER_CHANNEL_TYPE_APP_ACTIVITY","app活动页"),
	
	/**
	 * 4-app发现页
	 */
	BANNER_CHANNEL_TYPE_APP_FIND(4,"BANNER_CHANNEL_TYPE_APP_FIND","app发现页"),
	
	
	/**
	 * 6-app首页广告
	 */
	BANNER_CHANNEL_TYPE_APP_INDEX_AD(6,"BANNER_CHANNEL_TYPE_APP_INDEX_AD","app首页广告位"),
	
	/**
	 * 7-app首页弹层
	 */
	BANNER_CHANNEL_TYPE_APP_INDEX_POPUP(7,"BANNER_CHANNEL_TYPE_APP_INDEX_POPUP","app首页弹层"),
	
	/**奖励类型：谢谢惠顾**/
	ACTIVITY_LOTTERY_TYPE_NOREWARD(0,"NOREWARD","奖励类型：谢谢惠顾"),
	/**奖励类型：人气值**/
	ACTIVITY_LOTTERY_TYPE_POPULARITY(1,"POPULARITY","奖励类型：人气值"),
	/**奖励类型：收益券**/
	ACTIVITY_LOTTERY_TYPE_ANNUALIZED(2,"ANNUALIZED","奖励类型：收益券"),
	/**奖励类型：现金券**/
	ACTIVITY_LOTTERY_TYPE_COUPON(3,"COUPON","奖励类型：现金券"),
	/**奖励类型：礼品**/
	ACTIVITY_LOTTERY_TYPE_GIFT(4,"GIFT","奖励类型：礼品"),
	/**奖励类型：其他**/
	ACTIVITY_LOTTERY_TYPE_OTHER(5,"OTHER","奖励类型：其他"),
	
	/**抽奖校验人气值**/
	ACTIVITY_LOTTERY_VALIDATE_POPULARITY(0, "popularity", "抽奖校验人气值"),
	/**抽奖校验是否参加**/
	ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE(1, "participate", "抽奖校验是否参加"),
	/**人气值红包校验**/
	ACTIVITY_LOTTERY_VALIDATE_POPULARITYREDBAG(1, "popularityRedBag", "人气值红包校验"),
	
	/**子活动编组1**/
	ACTIVITY_CHILD_GROUP_1(1, "1", "子活动1"),
	/**子活动编组2**/
	ACTIVITY_CHILD_GROUP_2(2, "2", "子活动2"),
	/**子活动编组3**/
	ACTIVITY_CHILD_GROUP_3(3, "3", "子活动3"),
	/**子活动编组4**/
	ACTIVITY_CHILD_GROUP_4(4, "4", "子活动4"),
	
	/** 人气值红包随机算法之随机数相减算法 **/
	REDPACKAGE_POPULARITY_RANDOM_SUBTRACT(1, "redPackageRandomSubtract", "人气值红包随机算法之随机数相减算法"),

	/** 人气值红包随机算法之支持单次算法 **/
	REDPACKAGE_POPULARITY_RANDOM_ONCE(2, "redPackageRandomOnce", "人气值红包随机算法之支持单次算法"),
	
	/** 定制活动类型-交易 **/
	ACTIVITY_BIZ_TYPE_TRANSACTION(1, "ACTIVITY_BIZ_TYPE_TRANSACTION", "定制活动类型-交易"),

	/** 活动校验来源 **/
	ACTIVITY_SOURCE_TYPE_CREATE(1, "ACTIVITY_SOURCE_TYPE_CREATE", "活动校验来源-来自创建"),

	/** 活动校验来源 **/
	ACTIVITY_SOURCE_TYPE_JOIN(2, "ACTIVITY_SOURCE_TYPE_CREATE", "活动校验来源-参与"),
	
	/**
	 * 生日礼包优惠券类型
	 */
	/** 50现金券 **/
	BIRTHDAY_COUPON_TYPE_50CASH(1, "BIRTHDAY_COUPON_TYPE_50CASH", "50现金券"),
	/** 1%收益券 **/
	BIRTHDAY_COUPON_TYPE_001INCOME(2, "BIRTHDAY_COUPON_TYPE_001INCOME", "1%收益券"),
	


	/**
	 * 用户类型
	 */
	/**
	 * 个人用户
	 */
	MEMBER_TYPE_PERSONAL(1,"MEMBER_TYPE_PERSONAL","个人用户"),
	/**
	 * 企业用户
	 */
	MEMBER_TYPE_COMPANY(2,"MEMBER_TYPE_COMPANY","企业用户"),
	
	
	MEMBER_TYPE_ORG(4,"MEMBER_TYPE_ORG","其他组织"),
	
	/**
	 * 审核结果
	 */
	/**
	 * 审核通过
	 */
	AUDIT_RESULT_SUCCESS(1,"AUDIT_RESULT_SUCCESS","审核通过"),
	/**
	 * 审核不通过
	 */
	AUDIT_RESULT_FAILD(2,"AUDIT_RESULT_FAILD","审核不通过"),
	
	/**
	 * 审核内容类型
	 */
	/**
	 * 项目表
	 */
	AUDIT_TYPE_PROJECT(1,"AUDIT_TYPE_PROJECT","项目表"),
	
	/**
	 * 退款类型
	 */
	/**
	 * 交易退款
	 */
	REFUND_TYPE_TRANSACTION(1,"REFUND_TYPE_TRANSACTION","交易退款"),
	
	/**
	 * 直投项目收益周期类型
	 * （1-日；2-月；3-年；4-周）
	 */
	/**
	 * 1-日
	 */
	DIRECT_PROJECT_BORROW_PERIOD_TYPE_DAY(1,"DIRECT_PROJECT_BORROW_PERIOD_TYPE_DAY","日"),
	/**
	 * 2-月
	 */
	DIRECT_PROJECT_BORROW_PERIOD_TYPE_MONTH(2,"DIRECT_PROJECT_BORROW_PERIOD_TYPE_MONTH","月"),
	/**
	 * 3-年
	 */
	DIRECT_PROJECT_BORROW_PERIOD_TYPE_YEAR(3,"DIRECT_PROJECT_BORROW_PERIOD_TYPE_YEAR","年"),
	/**
	 * 4-周
	 */
	DIRECT_PROJECT_BORROW_PERIOD_TYPE_WEEK(4,"DIRECT_PROJECT_BORROW_PERIOD_TYPE_WEEK","周"),
	
	PROJECT_ACTIVITY_SIGN(1, "wishes", "新春专享项目标识"),
	
	
	PROJECT_ACTIVITY_CATALYZER(2, "CATALYZER", "满标快投活动标识"),
	
	PROJECT_EXTRA_PROJECT_ACTIVITY(1,"PROJECT_EXTRA_PROJECT_ACTIVITY","活动"),
	
	PROJECT_EXTRA_PROJECT_ADD_RATE(2, "PROJECT_EXTRA_PROJECT_ADD_RATE", "项目加息"),
	
	/**
	 * 功能类短信
	 */
	SMS_REMIND_FUNCTION(1, "SMS_REMIND_FUNCTION", "8SDK-EMY-6699-RIWON"),
	/**
	 * 营销类短信
	 */
	SMS_REMIND_SALE(2, "SMS_REMIND_SALE", "8SDK-EMY-6699-RIWPK"),
	/**
	 * 验证码短信
	 */
	SMS_REMIND_CODE(3, "SMS_REMIND_CODE", "8SDK-EMY-6699-RIWNM"),
	/**
	 * 短信账号类别
	 */
	SMS_TYPE(1, "sms_type", "短信账号类别"),
	
	SMS_REMIND(2, "sms_remind", "短信预警设置"),
	
	/**
	 * 操作类型
	 */
	/**
	 * 1，项目表
	 */
	OPERATE_TYPE_PROJECT(1,"OPERATE_TYPE_PROJECT","项目表"),
	/**
	 * 2，逾期还款记录表
	 */
	OPERATE_TYPE_OVERDUE(2,"OPERATE_TYPE_OVERDUE","逾期还款记录表"),
	/**
	 * 3,提现流水表
	 */
	OPERATE_TYPE_WITHDRAW(3,"OPERATE_TYPE_WITHDRAW","提现流水表"),
	
	/**
	 *  按钮名称
	 */
	OPERATE_DIRECT_LOAN(1,"OPERATE_DIRECT_LOAN","直投项目-放款"),
	
	OPERATE_OVERDUE_REPAYMENT(2,"OPERATE_OVERDUE_REPAYMENT","直投项目-逾期还款标记"),
	
	OPERATE_WITHDRAW_AGREE(3,"OPERATE_WITHDRAW_AGREE","提现管理-同意"),
	
	OPERATE_WITHDRAW_REFUSE(4,"OPERATE_WITHDRAW_REFUSE","提现管理-拒绝"),
	
	OPERATE_PROJECT_SET(5,"OPERATE_PROJECT_SET","债权项目-置为存盘"),
	
	OPERATE_PROJECT_PREREPAYMENT(6,"OPERATE_PROJECT_PREREPAYMENT","直投项目提前还款管理-提前还款"),
	/**
	 * 性别
	 */
	SEX_WOMAN(0, "woman", "女"),

	SEX_MAN(0, "man", "男"),

	/**
	 * APP 我的交易列表查询类型
	 */
	MY_TRANSACTION_UNRASING(0, "MY_TRANSACTION_UNRASING", "APP我的交易列表"),

	MY_TRANSACTION_RASING(1, "MY_TRANSACTION_RASING", "APP我的募集中列表"),
	
	/**
	 * 还款类型
	 */
	REPAYMENT_TYPE_NORMAL(0, "REPAYMENT_TYPE_NORMAL", "正常"),

	REPAYMENT_TYPE_PREREPAYMENT(1, "REPAYMENT_TYPE_PREREPAYMENT", "提前还款"),
	
	REPAYMENT_TYPE_OVERDUE(2, "REPAYMENT_TYPE_OVERDUE", "逾期还款"),
	
	REPAYMENT_TYPE_LOANREPAYMENT(3, "REPAYMENT_TYPE_LOANREPAYMENT", "垫资还款"),
	
	/**
	 * 代收交易类型
	 */
	COLLECT_TRADE_TYPE_NORMAL(0, null, "代收交易类型-正常"),

	COLLECT_TRADE_TYPE_FREEZE(1, "pre_auth", "代收交易类型-冻结"),

	/**
	 * 代收完成/撤销类型
	 */
	COLLECT_TRADE_FINISH_TYPE(1, "COLLECT_TRADE_FINISH_TYPE", "代收完成"),

	COLLECT_TRADE_CANCEL_TYPE(2, "COLLECT_TRADE_CANCEL_TYPE", "代收撤销"),
	
	
	/**
	 * 逾期记录类型（1-垫资逾期 2-普通逾期）
	 */
	/**
	 * 1-垫资逾期
	 */
	OVERDUE_LOG_TYPE_UNDERWRITE(1,"OVERDUE_LOG_TYPE_UNDERWRITE","垫资逾期"),
	/**
	 * 2-普通逾期
	 */
	OVERDUE_LOG_TYPE_GENERAL(2,"OVERDUE_LOG_TYPE_GENERAL","普通逾期"),
	
	/**
	 * 交易本息还款类型
	 */
	/**
	 * 0-正常
	 */
	TRANSACTION_INTEREST_PAY_TYPE_NORMAL(0,"TRANSACTION_INTEREST_PAY_TYPE_NORMAL","正常"),
	/**
	 * 1-提前还款
	 */
	TRANSACTION_INTEREST_PAY_TYPE_PREPAY(1,"TRANSACTION_INTEREST_PAY_TYPE_PREPAY","提前还款"),
	/**
	 * 2-逾期还款
	 */
	TRANSACTION_INTEREST_PAY_TYPE_OVERDUE(2,"TRANSACTION_INTEREST_PAY_TYPE_OVERDUE","逾期还款"),
	
	/**
	 * 还款类型
	 */
	INTEREST_TYPE_PRININTER(1,"INTEREST_TYPE_PRININTER","本金+利息"),
	INTEREST_TYPE_INTEREST(2,"INTEREST_TYPE_INTEREST","利息"),
	
	/**
	 * 操作支付密码类型
	 */
	SET_PAY_PASSWORD(1, "SET_PAY_PASSWORD", "设置支付密码"),
	MODIFY_PAY_PASSWORD(2, "MODIFY_PAY_PASSWORD", "修改支付密码"),
	FIND_PAY_PASSWORD(3, "FIND_PAY_PASSWORD", "找回支付密码"),
	QUERY_IS_SET_PAY_PASSWORD(4, "QUERY_IS_SET_PAY_PASSWORD", "查询是否设置支付密码"),
	
	
	/**
	 * 委托扣款授权操作
	 */
	HANDLE_WITHHOLD_AUTHORITY(1, "HANDLE_WITHHOLD_AUTHORITY", "开通委托扣款"),
	
	RELIEVE_WITHHOLD_AUTHORITY(2, "RELIEVE_WITHHOLD_AUTHORITY", "关闭委托扣款"),	

	/**
	 * 收银台回调方式
	 */
	SINA_RETURN_TYPE_NULL(0, "SINA_RETURN_TYPE_NULL", "不回调"),

	SINA_RETURN_TYPE_CLOSE(1, "SINA_RETURN_TYPE_CLOSE", "回调后关闭"),

	SINA_RETURN_TYPE_REDIRECT(2, "SINA_RETURN_TYPE_REDIRECT", "回调后跳转"),
	
	CARD_TYPE_ORDINARY(1, "CARD_TYPE_ORDINARY", "一般卡"),
	CARD_TYPE_QUICK(2, "CARD_TYPE_QUICK", "快捷支付卡"),
	
	/**
	 * 转让项目标识
	 */
	PROJECT_CATEGORY_NORMAL(1, "PROJECT_CATEGORY_NORMAL", "普通项目"),
	PROJECT_CATEGORY_TRANSFER(2, "PROJECT_CATEGORY_TRANSFER", "转让项目"),
	PROJECT_CATEGORY_INVEST(3, "PROJECT_CATEGORY_INVEST", "投资专区"),
	
	/**
	 * 转让项目失败标识
	 */
	TRANSFER_PROJECT_FAIL_LOSE(1, "TRANSFER_PROJECT_FAIL_LOSE", "失败"),
	TRANSFER_PROJECT_FAIL_CANCLE(2, "TRANSFER_PROJECT_FAIL_CANCLE", "撤销"),
	
	/**
	 * 交易操作类型
	 */
	TRANSACTION_OPERA_TYPE_NORMAL(1,"TRANSACTION_OPERA_TYPE_NORMAL","普通，无"),
	TRANSACTION_OPERA_TYPE_CANNOT_TRANSFER(2,"TRANSACTION_OPERA_TYPE_CANNOT_TRANSFER","不可转让，也不能跳转详情页"),
	TRANSACTION_OPERA_TYPE_TRANSFER(3,"TRANSACTION_OPERA_TYPE_TRANSFER","可转让，可跳转详情页"),
	
	/**
	 * 自动投标优惠券使用的类型
	 */
	AUTO_INVEST_COUPON_USE_TYPE_NOUSE(0,"AUTO_INVEST_COUPON_USE_TYPE_NOUSE","不使用优惠券"),
	AUTO_INVEST_COUPON_USE_TYPE_INCOME(1,"AUTO_INVEST_COUPON_USE_TYPE_INCOME","优先使用收益最高优惠券"),
	AUTO_INVEST_COUPON_USE_TYPE_EXPIRY_DATE(2,"AUTO_INVEST_COUPON_USE_TYPE_EXPIRY_DATE","优先使用有效期最短优惠券"),
	
	
	
	/**
	 * 商品类型
	 */
	GOODS_TYPE_FOR_INVEST(1,"GOODS_TYPE_FOR_INVEST","投资专享"),
	
	GOODS_TYPE_VIRTUAL_CARD(2,"GOODS_TYPE_VIRTUAL_CARD","虚拟卡券"),

	GOODS_TYPE_PHYSICAL(3,"GOODS_TYPE_PHYSICAL","超值实物"),

	GOODS_TYPE_DOUBLE(4,"GOODS_TYPE_DOUBLE","新品特惠"),

	/**
	 * 商品充值类型
	 */
	GOODS_RECHARGE_TYPE_RECHARGE(1,"GOODS_RECHARGE_TYPE_RECHARGE","直冲"),
	
	GOODS_RECHARGE_TYPE_CAMI(2,"GOODS_RECHARGE_TYPE_CAMI","卡密"),
	
	/**
	 * 借款人授信额度上线标识
	 */
	BORROWER_CREDIT_NORMAL(1, "BORROWER_CREDIT_NORMAL", "正常上线"),
	BORROWER_CREDIT_PAUSE(0, "BORROWER_CREDIT_PAUSE", "暂停上线")
	
	;
	private static Map<String, TypeEnum> enumMap;

	private int type;

	private String code;

	private String desc;

	TypeEnum(int type, String code, String desc) {
		this.type = type;
		this.code = code;
		this.desc = desc;
	}

	public static Map<String, TypeEnum> getMap() {
		if (enumMap == null) {
			enumMap = new HashMap<String, TypeEnum>();
			for (TypeEnum type : TypeEnum.values()) {
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

	public static TypeEnum getByCode(String code) {
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
