package com.yourong.common.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.apache.http.annotation.NotThreadSafe;

import com.yourong.common.constant.Config;
import com.yourong.common.thirdparty.pay.sina.SinaPayConfig;

@NotThreadSafe
public class SerialNumberUtil {

	/**
	 * 新浪用户id前缀
	 */
	public static String PREFIX_UID = "YRUC";
	/**
	 * 订单编号前缀
	 */
	public static String PREFIX_TRADE_NO = "YRTC";
	/**
	 * 充值流水编号前缀
	 */
	public static String PREFIX_RECHARGE_NO = "YRRC";
	/**
	 * 提现流水号前缀
	 */
	public static String PREFIX_WITHDRAW_NO = "YRWD";

	/**
	 * 新浪提现流水号前缀
	 */
	public static String PREFIX_UC_WITHDRAW_NO = "UCWD";

	/**
	 * 现金券编号前缀
	 */
	public static String PREFIX_COUPON_A = "YRCA";
	/**
	 * 收益权编号前缀
	 */
	public static String PREFIX_COUPON_B = "YRCB";
	/**
	 * 债权编号前缀
	 */
	public static String PREFIX_DEBT = "YRDE";

	/**
	 * 冻结订单号前缀
	 */
	public static String PREFIX_FREEZE_NO = "YRFZ";

	/**
	 * 解冻订单号前缀
	 */
	public static String PREFIX_UNFREEZE_NO = "YRUF";
	/**
	 * 代收交易号前缀
	 */
	public static String PREFIX_COLLECT_TRADE_NO = "YRCT";
	/**
	 * 代收完成批次号前缀
	 */
	public static String PREFIX_COLLECT_BATCH_FINISH_NO = "YRBF";
	/**
	 * 单笔代收完成号前缀
	 */
	public static String PREFIX_COLLECT_FINISH_NO = "YRCF";
	/**
	 * 代收撤销批次号前缀
	 */
	public static String PREFIX_COLLECT_BATCH_CANCEL_NO = "YRBC";
	/**
	 * 代收撤销批次号前缀
	 */
	public static String PREFIX_COLLECT_CANCEL_NO = "YRCC";
	/**
	 * 代付交易号前缀
	 */
	public static String PREFIX_PAY_TRADE_NO = "YRPT";

	/**
	 * 批量代付交易号前缀
	 */
	public static String PREFIX_BATCH_PAY_TRADE_NO = "YRBP";

	/**
	 * 银行绑卡 ，流水号
	 */
	public static String PREFIX_PAY_BING_CAR_NO = "YRBK";
	
	/**
	 * 退款交易前缀
	 */
	public static String PREFIX_REFUND_TRADE_NO="YRRT";
	
	/**
	 * 冻结订单号前缀
	 */
	public static String PREFIX_OUT_FREEZE_NO="YRDJ";
	
	/**
	 * 解冻订单号前缀
	 */
	public static String PREFIX_OUT_UNFREEZE_NO="YRJD";

	/**
	 * 托管交易支付号前缀
	 */
	public static String PREFIX_OUT_PAY_NO = "YRPC";
	
	/**
	 * 生成绑卡银行流水号
	 * 
	 * @return
	 */
	public static String generatePayBingCar(Long memberId) {
		if (memberId == null) {
			return null;
		}
		String memberIdStr = memberId.toString();
		// 截取用户ID后六位
		String dateline = DateUtils.formatDatetoString(
				DateUtils.getCurrentDate(), DateUtils.TIME_PATTERN_MILLISECOND);
		String number = PREFIX_PAY_BING_CAR_NO
				+ dateline
				+ memberIdStr.substring(memberIdStr.length() - 6,
						memberIdStr.length());
		return number;
	}

	/**
	 * 生成订单编号
	 * 
	 * @param memberId
	 * @return
	 */
	public static String generateOrderNo(Long memberId) {

		if (memberId == null) {
			return null;
		}
		String memberIdStr = memberId.toString();
		// 截取用户ID后六位
		String dateline = DateUtils.formatDatetoString(
				DateUtils.getCurrentDate(), DateUtils.TIME_PATTERN_MILLISECOND);
		String tradeNo = PREFIX_TRADE_NO
				+ dateline
				+ memberIdStr.substring(memberIdStr.length() - 6,
						memberIdStr.length());
		return tradeNo;
	}

	/**
	 * 生成充值流水号
	 * 
	 * @param memberId
	 * @return
	 */
	public static String generateRechargeNo(Long memberId) {
		if (memberId == null) {
			return null;
		}
		String memberIdStr = memberId.toString();
		// 截取用户ID后六位
		String dateline = DateUtils.formatDatetoString(
				DateUtils.getCurrentDate(), DateUtils.TIME_PATTERN_MILLISECOND);
		String rechargeNo = PREFIX_RECHARGE_NO
				+ dateline
				+ memberIdStr.substring(memberIdStr.length() - 6,
						memberIdStr.length());
		return rechargeNo;
	}
	/**
	 * 生成提现流水号
	 * 
	 * @param memberId
	 * @return
	 */
	public static String generateWithdrawNo(Long memberId) {
		if (memberId == null) {
			return null;
		}
		String memberIdStr = memberId.toString();
		// 截取用户ID后六位
		String dateline = DateUtils.formatDatetoString(
				DateUtils.getCurrentDate(), DateUtils.TIME_PATTERN_MILLISECOND);
		String withdrawNo = PREFIX_WITHDRAW_NO
				+ dateline
				+ memberIdStr.substring(memberIdStr.length() - 6,
						memberIdStr.length());
		return withdrawNo;
	}

	/**
	 * 生成新浪提现流水号
	 * 
	 * @param memberId
	 * @return
	 */
	public static String generateSinaWithdrawNo(Long memberId) {
		if (memberId == null) {
			return null;
		}
		String memberIdStr = memberId.toString();
		// 截取用户ID后六位
		String dateline = DateUtils.formatDatetoString(
				DateUtils.getCurrentDate(), DateUtils.TIME_PATTERN_MILLISECOND);
		String withdrawNo = PREFIX_UC_WITHDRAW_NO
				+ dateline
				+ memberIdStr.substring(memberIdStr.length() - 6,
						memberIdStr.length());
		return withdrawNo;
	}

	/**
	 * 生成现金券编号
	 * 
	 * @return
	 */
	public static String generateCouponANo(Integer increaseNum) {
		String dateline = DateUtils.formatDatetoString(
				DateUtils.getCurrentDate(), DateUtils.TIME_PATTERN_MILLISECOND);
		NumberFormat formatter = new DecimalFormat("000000");
		String num = formatter.format(increaseNum);
		String couponNo = PREFIX_COUPON_A + dateline + num;
		return couponNo;
	}

	/**
	 * 生成收益券编号
	 * 
	 * @return
	 */
	public static String generateCouponBNo(Integer increaseNum) {
		String dateline = DateUtils.formatDatetoString(
				DateUtils.getCurrentDate(), DateUtils.TIME_PATTERN_MILLISECOND);
		NumberFormat formatter = new DecimalFormat("000000");
		String num = formatter.format(increaseNum);
		String couponNo = PREFIX_COUPON_B + dateline + num;
		return couponNo;
	}

	/**
	 * 生成代收交易号
	 * 
	 * @return
	 */
	public static String generateCollectTradeaNo() {
		String dateline = DateUtils.formatDatetoString(
				DateUtils.getCurrentDate(), DateUtils.TIME_PATTERN_MILLISECOND);
		String couponNo = PREFIX_COLLECT_TRADE_NO + dateline;
		return couponNo;
	}

	/**
	 * 生成代付交易号,传入交易本息id防止单号重复，规则：小于等于6位直接传入，大于6位截取后六位
	 * 
	 * @return
	 */
	public static String generatePayTradeaNo(Long transactionInterestId) {
		String dateline = DateUtils.formatDatetoString(
				DateUtils.getCurrentDate(), DateUtils.TIME_PATTERN_MILLISECOND);
		String memberIdStr = transactionInterestId.toString();
		if(memberIdStr.length()<=6) {
			return PREFIX_PAY_TRADE_NO + dateline + memberIdStr;
		} else {
			return PREFIX_PAY_TRADE_NO + dateline + memberIdStr.substring(memberIdStr.length() - 6,
					memberIdStr.length());
		}
	}

	/**
	 * 生成批量代付交易号
	 * 
	 * @return
	 */
	public static String generateBatchPayTradeaNo() {
		String dateline = DateUtils.formatDatetoString(
				DateUtils.getCurrentDate(), DateUtils.TIME_PATTERN_MILLISECOND);
		String couponNo = PREFIX_BATCH_PAY_TRADE_NO + dateline;
		return couponNo;
	}
	
	/**
	 * 生成批量代付交易号,带标识
	 * 
	 * @return
	 */
	public static String generateBatchPayTradeaNo(String identify) {
		String dateline = DateUtils.formatDatetoString(
				DateUtils.getCurrentDate(), DateUtils.TIME_PATTERN_MILLISECOND);
		String couponNo = PREFIX_BATCH_PAY_TRADE_NO + dateline + identify.substring(identify.length() - 6,
				identify.length());
		return couponNo;
	}

	/**
	 * 生成债权编号
	 * 
	 * @param debtId
	 * @return
	 */
	public static String generateDebtNo(Long debtId) {
		if (debtId == null) {
			return null;
		}
		String debtNo = PREFIX_DEBT + debtId;
		return debtNo;
	}

	/**
	 * 生成新浪用户id
	 * 
	 * @param debtId
	 * @return
	 */
	public static String generateIdentityId(Long memberId) {
		if (memberId == null) {
			return null;
		}
		// 企业基本户
		if (memberId == Long.parseLong(Config.internalMemberId)) {
			return SinaPayConfig.indentityEmail;
		}
		return PREFIX_UID + memberId;
	}

	/**
	 * 获取企业基本户账号，返回的是邮箱
	 * 
	 * @return
	 */
	public static String getBasicAccount() {
		return SinaPayConfig.indentityEmail;
	}

	/**
	 * 获取企业基本户内部id账号
	 * 
	 * @return
	 */
	public static Long getInternalMemberId() {
		return Long.parseLong(Config.internalMemberId);
	}

	/**
	 * 生成退款交易号
	 * 
	 * @return
	 */
	public static String generateRefundTradeaNo() {
		String dateline = DateUtils.formatDatetoString(
				DateUtils.getCurrentDate(), DateUtils.TIME_PATTERN_MILLISECOND);
		String refundNo = PREFIX_REFUND_TRADE_NO + dateline;
		return refundNo;
	}
	
	/**
	 * 
	 * @Description:生成代收完成批次号
	 * @param index
	 * @return
	 * @author: wangyanji
	 * @time:2016年6月7日 下午1:45:36
	 */
	public static String generateFinishCollecTradeBatchNo(int index) {
		StringBuffer no = new StringBuffer(PREFIX_COLLECT_BATCH_FINISH_NO);
		return no.append(DateUtils.formatDatetoString(DateUtils.getCurrentDate(), DateUtils.TIME_PATTERN_MILLISECOND)).append(index)
				.toString();
	}

	/**
	 * 生成单笔代收完成请求号
	 * 
	 * @return
	 */
	public static String generateFinishCollecTradeRequestNo(Long orderId) {
		String dateline = DateUtils.formatDatetoString(
				DateUtils.getCurrentDate(), DateUtils.TIME_PATTERN_MILLISECOND);
		String refundNo = PREFIX_COLLECT_FINISH_NO + dateline + orderId;
		return refundNo;
	}
	
	/**
	 * 
	 * @Description:生成代收撤销批次号
	 * @param index
	 * @return
	 * @author: wangyanji
	 * @time:2016年6月7日 下午1:46:07
	 */
	public static String generateCancelCollecTradeBatchNo(int index) {
		StringBuffer no = new StringBuffer(PREFIX_COLLECT_BATCH_CANCEL_NO);
		return no.append(DateUtils.formatDatetoString(DateUtils.getCurrentDate(), DateUtils.TIME_PATTERN_MILLISECOND)).append(index)
				.toString();
	}
	
	/**
	 * 生成单笔代收撤销请求号
	 * 
	 * @return
	 */
	public static String generateCancelCollecTradeRequestNo(Long orderId) {
		String dateline = DateUtils.formatDatetoString(
				DateUtils.getCurrentDate(), DateUtils.TIME_PATTERN_MILLISECOND);
		String refundNo = PREFIX_COLLECT_CANCEL_NO + dateline + orderId;
		return refundNo;
	}
	
	/**
	 * 
	 * @Description:生成冻结订单号
	 * @return
	 * @author: chaisen
	 * @time:2016年7月28日 上午9:54:07
	 */
	public static String generateFreezeOutCtrlNo() {
		String dateline = DateUtils.formatDatetoString(
				DateUtils.getCurrentDate(), DateUtils.TIME_PATTERN_MILLISECOND);
		String couponNo = PREFIX_OUT_FREEZE_NO + dateline;
		return couponNo;
	}
	
	/**
	 * 
	 * @Description:生成解冻订单号
	 * @return
	 * @author: chaisen
	 * @time:2016年7月28日 上午10:29:07
	 */
	public static String generateUnFreezeOutCtrlNo() {
		String dateline = DateUtils.formatDatetoString(
				DateUtils.getCurrentDate(), DateUtils.TIME_PATTERN_MILLISECOND);
		String couponNo = PREFIX_OUT_FREEZE_NO + dateline;
		return couponNo;
	}
	
	/**
	 * 
	 * @Description:生成托管交易支付号
	 * @return
	 * @author: wangyanji
	 * @time:2016年8月5日 下午9:41:40
	 */
	public static String generatePayHostingCollectTradeNo(Long payerId) {
		String payerIdStr = payerId.toString();
		payerIdStr = payerIdStr.substring(payerIdStr.length() - 6, payerIdStr.length());
		String dateline = DateUtils.formatDatetoString(DateUtils.getCurrentDate(), DateUtils.TIME_PATTERN_MILLISECOND);
		String couponNo = PREFIX_OUT_PAY_NO + dateline + payerIdStr;
		return couponNo;
	}
	
	/**
	 * 
	 * @Description:生成会员外部编号
	 * @param memberId
	 * @param prefix
	 * @return
	 * @author: wangyanji
	 * @time:2016年11月15日 上午10:37:14
	 */
	public static String generateOpenNo(Long memberId, String prefix) {
		String payerIdStr = memberId.toString();
		payerIdStr = payerIdStr.substring(payerIdStr.length() - 2, payerIdStr.length());
		String dateline = DateUtils.formatDatetoString(DateUtils.getCurrentDate(), DateUtils.TIME_PATTERN_MILLISECOND);
		return prefix + dateline + payerIdStr;
	}

	public static void main(String[] args) {

	}

}
