package com.yourong.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 备注枚举类
 * 
 */
public enum RemarksEnum {

	/**
	 * 订单类备注
	 */
	/** 用户中心取消订单备注 **/
	ORDER_CANCEL_ORDER("ORDER_CANCEL_ORDER", "用户取消"),
	/** 用户超时未支付 **/
	ORDER_NOT_PAY_ORDER("ORDER_NOT_PAY_ORDER", "用户超时未支付"),
	/** 用户超时未支付 **/
	ORDER_UNWITHHOLD_SINA_PAY_ORDER("ORDER_UNWITHHOLD_SINA_PAY_ORDER", "15分钟内未支付成功，订单将自动取消"),
	/**
	 * 投资送人气值
	 */
	TRANSACTION_ADD_POPULARITY_BALANCE("TRANSACTION_ADD_POPULARITY_BALANCE", "投资送人气值"),
	/**
	 * 签到送人气值
	 */
	MEMBER_CHECK_ADD_POPULARITY_BALANCE("MEMBER_CHECK_ADD_POPULARITY_BALANCE", "签到送人气值"),
	/**
	 * 一马当先送人气值
	 */
	TRANSACTION_ADD_POPULARITY_BALANCE_BY_FIRST_INVEST("TRANSACTION_ADD_POPULARITY_BALANCE_BY_FIRST_INVEST", "一羊领头送人气值"),
	/**
	 * 一鸣惊人送人气值
	 */
	TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_INVEST("TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_INVEST", "一鸣惊人送人气值"),
	/**
	 * 一锤定音送人气值
	 */
	TRANSACTION_ADD_POPULARITY_BALANCE_BY_LAST_INVEST("TRANSACTION_ADD_POPULARITY_BALANCE_BY_LAST_INVEST", "一锤定音送人气值"),
	/**
	 * 幸运女神送人气值
	 */
	TRANSACTION_ADD_POPULARITY_BALANCE_BY_LUCK_INVEST("TRANSACTION_ADD_POPULARITY_BALANCE_BY_LUCK_INVEST", "幸运女神送人气值"),
	/**
	 * 一掷千金送人气值
	 */
	TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_AND_LAST_INVEST("TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_AND_LAST_INVEST", "一掷千金送人气值"),
	/**
	 * 单笔投资活动送人气值
	 */
	TRANSACTION_ADD_POPULARITY_BALANCE_BY_SINGLE_INVEST("TRANSACTION_ADD_POPULARITY_BALANCE_BY_SINGLE_INVEST", "单笔投资活动送人气值"),
	/**
	 * 人气值兑换现金券
	 */
	COUPON_EXCHANGE_POPULARITY_BALANCE("COUPON_EXCHANGE_POPULARITY_BALANCE", "兑换{0}元现金券"),
	/**
	 * 人气值兑换收益券
	 */
	COUPON_PROFIT_EXCHANGE_POPULARITY_BALANCE("COUPON_PROFIT_EXCHANGE_POPULARITY_BALANCE", "兑换{0}%收益券"),
	
	
	/**
	 * 问卷调查送人气值
	 */
	QUESTION_ADD_POPULARITY_BALANCE("QUESTION_ADD_POPULARITY_BALANCE","问卷调查送人气值"),
	
	/**
	 * 代收失败解锁优惠券
	 */
	COUPON_UNLOCK_BY_COLLECT_TRADE_FAILED("COUPON_UNLOCK_BY_COLLECT_TRADE_FAILED", "代收失败解锁优惠券"),
	
	/**
	 * 代收成功锁定优惠券
	 */
	COUPON_LOCK_BY_COLLECT_TRADE_SUCCESS("COUPON_LOCK_BY_COLLECT_TRADE_SUCCESS", " 代收成功锁定优惠券"),
	
	/**
	 * 充值失败解锁优惠券
	 */
	RECHARGE_UNLOCK_BY_NOTIFY_FAILED("RECHARGE_UNLOCK_BY_NOTIFY_FAILED", "充值失败解锁优惠券"),
	
	/**
	 * 平台垫付现金券
	 */
	PALTFORM_COUPON_FOR_LOAN("PALTFORM_COUPON_FOR_LOAN", "平台垫付现金券"),
	
	/**
	 * 直接代收平台金额
	 */
	HOSTING_COLLECT_TRADE_FOR_DIRECT_PAY("HOSTING_COLLECT_TRADE_FOR_DIRECT_PAY", "直接代收平台金额"),
	/**
	 * 直接代收会员存钱罐金额到还本付息专用中间账户
	 */
	HOSTING_COLLECT_TRADE_FOR_DIRECT_PAY_FROM_MEMBER("HOSTING_COLLECT_TRADE_FOR_DIRECT_PAY_FROM_MEMBER", " 直接代收会员存钱罐金额到还本付息专用中间账户"),
	/**
	 * 平台垫付租赁分红
	 */
	PALTFORM_COUPON_FOR_LEASE_BONUS("PALTFORM_COUPON_FOR_LEASE_BONUS", "平台垫付租赁分红"),
	/**
	 * 原始债权人还本付息代收
	 */
	HOSTING_COLLECT_TRADE_FOR_LENDER("HOSTING_COLLECT_TRADE_FOR_LENDER", "原始债权人还本付息代收"),
	/**
	 *还本付息代付交易
	 */
	HOSTING_PAY_TRADE_FOR_INTEREST_AND_PRINCIPAL("HOSTING_PAY_TRADE_FOR_INTEREST_AND_PRINCIPAL", "还本付息代付交易"),
	/**
	 *租赁分红代付交易
	 */
	HOSTING_PAY_TRADE_FOR_LEASE_BONUS("HOSTING_PAY_TRADE_FOR_LEASE_BONUS", "租赁分红代付交易,sourceId为lease_detail_id"),
	/**
	 *垫资还款代付交易
	 */
	HOSTING_PAY_TRADE_FOR_OVERDUE_REPAY("HOSTING_PAY_TRADE_FOR_OVERDUE_REPAY", "垫资还款代付交易"),
	/**
	 *抽奖活动赠送人气值
	 */
	ACTIVITY_LOTTERY_FOR_POPULARITY("ACTIVITY_LOTTERY_FOR_POPULARITY", "获得人气值"),
	
	/**
	 *破亿活动分享
	 */
	ACTIVITY_LOTTERY_FOR_YIROADSHARE("ACTIVITY_LOTTERY_FOR_YIROADSHARE", "破亿活动分享"),
	/**
	 * 人气值提现手续费
	 */
	WITHDRAW_POPULARITY_BALANCE("WITHDRAW_POPULARITY_BALANCE", "人气值提现手续费"),
	
	/**
	 * 人气值商城消费
	 */
	WITHDRAW_POPULARITY_SHOP_CONSUME("WITHDRAW_POPULARITY_SHOP_CONSUME", "人气值商城消费"),
	
	/**
	 * 提现失败，退回提现手续费
	 */
	REFUND_WITHDRAW_POPULARITY_BALANCE("REFUND_WITHDRAW_POPULARITY_BALANCE", "提现失败，退回提现手续费"),
	
	/**
	 * 用户取消提现，退回提现手续费
	 */
	WITHDRAW_CANCEL_POPULARITY_BALANCE("WITHDRAW_CANCEL_POPULARITY_BALANCE","用户提现取消，退回提现手续费"),
	
	/**
	 * 用户取消提现备注
	 */
	WITHDRAW_USER_CANCEL("WITHDRAW_USER_CANCEL","用户取消"),
	
	/***
	 * 资金流水的备注
	 */
	/**
	 * 充值
	 * {0}银行名称{1}方式：网银、快捷
	 */
	FINCAPITALINOUT_TYPE_RECHARGE("FINCAPITALINOUT_TYPE_RECHARGE","{0}{1}"),
	/**
	 * 提现
	 * {0}银行名称{1}银行卡号
	 */
	FINCAPITALINOUT_TYPE_WITHDRAW("FINCAPITALINOUT_TYPE_WITHDRAW","到账银行：{0} {1}"),
	
	/**
	 * 用户投资
	 * {0}项目名称，例如"车有融181期"
	 */
	FINCAPITALINOUT_TYPE_INVEST("FINCAPITALINOUT_TYPE_INVEST","{0}"),
	
	/**
	 * 利息收支
	 * {0}项目名称{1}还本付息期数
	 */
	FINCAPITALINOUT_TYPE_INTEREST("FINCAPITALINOUT_TYPE_INTEREST","{0}（{1}）利息"),
	
	/**
	 * 本金收支
	 * {0}项目名称
	 */
	FINCAPITALINOUT_TYPE_PRINCIPAL("FINCAPITALINOUT_TYPE_PRINCIPAL","{0}"),
	
	/**
	 * 新浪存钱罐收益
	 * {0}时间
	 */
	FINCAPITALINOUT_TYPE_THIRDPAY("FINCAPITALINOUT_TYPE_THIRDPAY","{0} 存钱罐收益"),
	
	/**
	 * 项目放款
	 * {0}项目名称
	 */
	FINCAPITALINOUT_TYPE_LOAN("FINCAPITALINOUT_TYPE_LOAN","{0}"),
	
	/**
	 * 项目还款
	 * {0}项目名称{1}还本付息期数{2}
	 */
	FINCAPITALINOUT_TYPE_RETURN("FINCAPITALINOUT_TYPE_RETURN","{0}{1}"),
	
	/**
	 * 租赁分红代付资金流水
	 * {0}项目名称
	 */
	FINCAPITALINOUT_TYPE_PAY_LEASE_BONUS("FINCAPITALINOUT_TYPE_PAY_LEASE_BONUS","{0},车辆租赁分红"),
	
	/**
	 * 滞纳金资金流水
	 * {0}项目名称
	 */
	FINCAPITALINOUT_TYPE_OVERFINE("FINCAPITALINOUT_TYPE_OVERFINE", "{0}滞纳金"),
	
	/**
	 * 保证金归还资金流水
	 * {0}项目名称
	 */
	FINCAPITALINOUT_TYPE_GUARANTEEFEE("FINCAPITALINOUT_TYPE_GUARANTEEFEE", "{0}保证金"),
	
	/**
	 * 项目流标，资金已返回至您的存钱罐余额
	 */
	TRANSACTION_LOSE_AND_RETURN_CAPITAL("TRANSACTION_LOSE_AND_RETURN_CAPITAL", "项目流标，资金已解冻"),
	
	/**
	 * 项目审核通过
	 */
	PROJECT_AUTH_PASS("PROJECT_AUTH_PASS", "项目审核通过"),
	
	/**
	 * 项目流标
	 */
	PROJECT_LOSE("PROJECT_LOSE", "项目流标"),
	
	/**
	 * 快投有奖
	 */
	QUICK_REWARD("QUICK_REWARD", "快投有奖");
	
	private static Map<String, RemarksEnum> enumMap;


	private String code;

	private String remarks;

	RemarksEnum(String code, String remarks) {
		this.code = code;
		this.remarks = remarks;
	}


	public String getCode() {
		return this.code;
	}

	public String getRemarks() {
		return this.remarks;
	}

	public static RemarksEnum getEnumByCode(String code) {
		return getMap().get(code);
	}
	
	public static Map<String, RemarksEnum> getMap() {
		if (enumMap == null) {
			enumMap = new HashMap<String, RemarksEnum>();
			for (RemarksEnum value : RemarksEnum.values()) {
				enumMap.put(value.getCode(), value);
			}
		}
		return enumMap;
	}
}
