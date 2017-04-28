package com.yourong.web.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import com.yourong.common.cache.RedisPlatformClient;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.thirdparty.sinapay.common.util.DateUtil;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.common.util.SpringContextHolder;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.mc.model.biz.ActivityBiz;
import com.yourong.web.service.ActivityLotteryService;
import com.yourong.web.service.BalanceService;

/**
 * 平台资金类util
 * @author Leon Ray
 * 2014年10月8日-下午2:48:17
 */
public class PaltformCapitalUtils {
	
	private static BalanceService balanceService = SpringContextHolder.getBean(BalanceService.class);
	private static ActivityLotteryService activityLotteryService = SpringContextHolder.getBean(ActivityLotteryService.class);
	private  final static  BigDecimal YI_YI = new BigDecimal("100000000");
	
	
	/**
	 * 获得平台总投资额
	 * @return
	 */
	public static BigDecimal getPaltformTotalInvest(){
		return balanceService.getBalanceByType(TypeEnum.BALANCE_TYPE_PLATFORM_TOTAL_INVEST);
	}
	
	/**
	 * 获得平台总收益
	 * @return
	 */
	public static BigDecimal getPaltformTotalInterest(){
		return balanceService.getBalanceByType(TypeEnum.BALANCE_TYPE_PLATFORM_TOTAL_INTEREST);
	}
	
	/**
	 * 获得平台总投资额前缀
	 * @return
	 */
	public String getPrefixPaltformTotalInvest() {
		BigDecimal totalInvest = getPaltformTotalInvest();
		return FormulaUtil.getIntegerDefaultZero(totalInvest);
	}

	/**
	 * 获得平台总投资额后缀
	 * @return
	 */
	public String getSuffixPaltformTotalInvest() {
		BigDecimal totalInvest = getPaltformTotalInvest();
		return FormulaUtil.getDecimalDefaultZero(totalInvest);
	}
	
	/**
	 * 获得平台总投资额前缀
	 * @return
	 */
	public String getPrefixPaltformTotalInterest() {
		BigDecimal totalInterest = getPaltformTotalInterest();
		return FormulaUtil.getIntegerDefaultZero(totalInterest);
	}
	
	/**
	 * 获得平台总投资额后缀
	 * @return
	 */
	public String getSuffixPaltformTotalInterest() {
		BigDecimal totalInterest = getPaltformTotalInterest();
		return FormulaUtil.getDecimalDefaultZero(totalInterest);
	}
	
	/**
	 * 获取平台总注册人数
	 * @return
	 */
	public Long getPaltformMembers() {
		return RedisPlatformClient.getMemberCount();
	}

	/**
	 * 破亿活动 剩余金额
	 * @return
	 */
	public String getYiRoadformTotalInvest() {
		BigDecimal totalInvest = getPaltformTotalInvest();
		BigDecimal subtract = YI_YI.subtract(totalInvest);
		if (subtract.compareTo(BigDecimal.ZERO)==1){
			return  FormulaUtil.getInteger(subtract);
		}
		return null;
	}

	/**
	 * 破亿活动 第几天
	 * @return
	 */
	public String getYiRoadformDays() {
		Long activityId = Long.parseLong(PropertiesUtil.getProperties("activity.yiLotteryId"));
		Activity activity = activityLotteryService.getActivityId(activityId);
		if (activity!=null){
			int i = DateUtils.daysOfTwo( activity.getStartTime(),DateUtils.getCurrentDate()) + 1;
			return String.valueOf(i);
		}
		return null;
	}


	
	/**
	 * 获得平台累计赠送人气值
	 * @return
	 */
	public static BigDecimal getPaltformTotalGivePopularity(){
		return balanceService.getBalanceByType(TypeEnum.BALANCE_TYPE_PLATFORM_TOTAL_GIVE);
	}

	/**
	 * 获得平台累计兑换出总人气值
	 * @return
	 */
	public static BigDecimal getPaltformTotalRechargePopularity(){
		return balanceService.getBalanceByType(TypeEnum.BALANCE_TYPE_PLATFORM_TOTAL_RECHARGE);
	}

}
