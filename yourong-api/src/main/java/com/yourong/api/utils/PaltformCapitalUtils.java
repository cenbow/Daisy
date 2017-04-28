package com.yourong.api.utils;

import com.yourong.api.service.BalanceService;
import com.yourong.common.cache.RedisPlatformClient;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.common.util.SpringContextHolder;
import com.yourong.core.mc.model.Activity;
import java.math.BigDecimal;

/**
 * 平台资金类util
 * @author Leon Ray
 * 2014年10月8日-下午2:48:17
 */
public class PaltformCapitalUtils {
	
	private static BalanceService balanceService = SpringContextHolder.getBean(BalanceService.class);
	
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







}
