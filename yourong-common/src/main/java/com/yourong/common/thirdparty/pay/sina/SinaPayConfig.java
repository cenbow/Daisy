package com.yourong.common.thirdparty.pay.sina;

import com.yourong.common.enums.SinaPayEnum;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.common.util.StringUtil;

public class SinaPayConfig {

	/**
	 * 接口版本(非空)
	 */
	public static String version;
	/**
	 * 合作者身份ID(非空)
	 * 签约合作方的钱包唯一用户号。	非空	2000001159940003 
	 */
	public static String partnerId;
	/**
	 * 参数编码字符集(非空)
	 * 商户网站使用的编码格式，如utf-8、gbk、gb2312等。	非空	UTF-8
	 */
	public static String inputCharset;
	/**
	 * 签名方式(非空)
	 * 签名方式支持RSA、MD5。建议使用MD5
	 */
	public static String signType;
	/**
	 * 签名版本号(可空)
	 * 签名密钥版本，默认1.0
	 */
	public static String signVersion;
	/**
	 * 系统异步回调通知地址(可空)
	 * 钱包处理发生状态变迁后异步通知结果，响应结果为“success”，全部小写
	 */
	public static String notifyUrl;
	/**
	 * 页面跳转同步返回页面路径(可空)
	 * 钱包处理完请求后，当前页面自动跳转到商户网站里指定页面的http路径
	 */
	public static String returnUrl;
	
	/**
	 * 会员网关
	 */
	public static String memberGate;
	/**
	 * 订单网关
	 */
	public static String orderGate;
	
	/**
	 * 安全码
	 */
	public static String md5Key;
	
	/**
	 * 新浪账号企业邮箱
	 */
	public static String indentityEmail;
	/**
	 * 批付数量
	 */
	public static int batchNum;
	
	/**
	 * 交易回调url
	 */
	public static  String tradeNotifyUrl;
	
	/**
	 * 充值回调url
	 */
	public static  String depositNotifyUrl;
	
	/**
	 * 提现回调url
	 */
	public static  String withdrawNotifyUrl;
	/**
	 * 批量代付url
	 */
	public static  String batchHostingPayNotifyUrl  = PropertiesUtil.getProperties("");

	/**
	 * 退款回调url
	 */
	public static String refundNotifyUrl;
	/**
	 * 批次回调url
	 */
	public static String batchNotifyUrl;
	

	public static String getTradeNotifyUrl() {
		return tradeNotifyUrl;
	}

	public static void setTradeNotifyUrl(String tradeNotifyUrl) {
		SinaPayConfig.tradeNotifyUrl = tradeNotifyUrl;
	}

	public static String getDepositNotifyUrl() {
		return depositNotifyUrl;
	}

	public static void setDepositNotifyUrl(String depositNotifyUrl) {
		SinaPayConfig.depositNotifyUrl = depositNotifyUrl;
	}

	public static String getWithdrawNotifyUrl() {
		return withdrawNotifyUrl;
	}

	public static void setWithdrawNotifyUrl(String withdrawNotifyUrl) {
		SinaPayConfig.withdrawNotifyUrl = withdrawNotifyUrl;
	}

	public static String getVersion() {
		return version;
	}

	public static void setVersion(String version) {
		SinaPayConfig.version = version;
	}

	public static String getPartnerId() {
		return partnerId;
	}

	public static void setPartnerId(String partnerId) {
		SinaPayConfig.partnerId = partnerId;
	}

	public static String getInputCharset() {
		return inputCharset;
	}

	public static void setInputCharset(String inputCharset) {
		SinaPayConfig.inputCharset = inputCharset;
	}

	public static String getSignType() {
		return signType;
	}

	public static void setSignType(String signType) {
		SinaPayConfig.signType = signType;
	}

	public static String getSignVersion() {
		return signVersion;
	}

	public static void setSignVersion(String signVersion) {
		SinaPayConfig.signVersion = signVersion;
	}

	public static String getNotifyUrl() {
		return notifyUrl;
	}

	public static void setNotifyUrl(String notifyUrl) {
		SinaPayConfig.notifyUrl = notifyUrl;
	}

	public static String getReturnUrl() {
		return returnUrl;
	}

	public static void setReturnUrl(String returnUrl) {
		SinaPayConfig.returnUrl = returnUrl;
	}

	public static String getMemberGate() {
		return memberGate;
	}

	public static void setMemberGate(String memberGate) {
		SinaPayConfig.memberGate = memberGate;
	}

	public static String getOrderGate() {
		return orderGate;
	}

	public static void setOrderGate(String orderGate) {
		SinaPayConfig.orderGate = orderGate;
	}

	public static String getMd5Key() {
		return md5Key;
	}

	public static void setMd5Key(String md5Key) {
		SinaPayConfig.md5Key = md5Key;
	}
	

	public static String getIndentityEmail() {
		return indentityEmail;
	}

	public static void setIndentityEmail(String indentityEmail) {
		SinaPayConfig.indentityEmail = indentityEmail;
	}

	
	public static int getBatchNum() {
		return batchNum;
	}

	public static void setBatchNum(int batchNum) {
		SinaPayConfig.batchNum = batchNum;
	}

	public static String getRefundNotifyUrl() {
		return refundNotifyUrl;
	}
	
	public static void setRefundNotifyUrl(String refundNotifyUrl) {
		SinaPayConfig.refundNotifyUrl = refundNotifyUrl;
	}
	
	
	public static String getBatchNotifyUrl() {
		return batchNotifyUrl;
	}

	public static void setBatchNotifyUrl(String batchNotifyUrl) {
		SinaPayConfig.batchNotifyUrl = batchNotifyUrl;
	}

	public static String getGate(String service) {
		if(StringUtil.isNotBlank(service)) {
			if(SinaPayEnum.SERVICE_CREATE_ACTIVATE_MEMBER.getCode().equals(service)
					||SinaPayEnum.SERVICE_SET_REAL_NAME.getCode().equals(service)
					||SinaPayEnum.SERVICE_BINDING_VERIFY.getCode().equals(service)
					||SinaPayEnum.SERVICE_UNBINDING_VERIFY.getCode().equals(service)
					||SinaPayEnum.SERVICE_QUERY_VERIFY.getCode().equals(service)
					||SinaPayEnum.SERVICE_BINDING_BANK_CARD.getCode().equals(service)
					||SinaPayEnum.SERVICE_UNBINDING_BANK_CARD.getCode().equals(service)
					||SinaPayEnum.SERVICE_QUERY_BANK_CARD.getCode().equals(service)
					||SinaPayEnum.SERVICE_QUERY_BALANCE.getCode().equals(service)
					||SinaPayEnum.SERVICE_QUERY_ACCOUNT_DETAILS.getCode().equals(service)
					||SinaPayEnum.SERVICE_BALANCE_FREEZE.getCode().equals(service)
					||SinaPayEnum.SERVICE_BALANCE_UNFREEZE.getCode().equals(service)
					) {
				return SinaPayConfig.memberGate;
			} else {
				return SinaPayConfig.orderGate;
			}
		}
		return null;
	}
	
}
