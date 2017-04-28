package com.yourong.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

import com.yourong.common.enums.DebtEnum;

/**
 * 公式计算工具类
 * 
 * @author Administrator
 *
 */
public class FormulaUtil {

	private static final BigDecimal YEAR_DAYS = new BigDecimal(36000);
	private static final BigDecimal MONTH_LEN = new BigDecimal(1200);
	private static final BigDecimal HUNDRED = new BigDecimal(100);
	private static final BigDecimal THOUSAND = new BigDecimal(1000);



	/**
	 *  按日计息
	 * @return
	 */
	public static  BigDecimal  calculateInterest(BigDecimal amount, BigDecimal annualizedRate,int date){
		BigDecimal divide = annualizedRate.divide(YEAR_DAYS,10, BigDecimal.ROUND_HALF_UP);
		BigDecimal pay = amount.multiply(divide).multiply(new BigDecimal(date)).setScale(2, BigDecimal.ROUND_HALF_UP);
		return pay;
	}

	/**
	 *  等本等息 ，计算本金
	 * @param amount
	 * @param month
	 * @return
	 */
	public static  BigDecimal calculatePrincipalByAvg(BigDecimal amount, int month){
		BigDecimal pay = amount.divide(new BigDecimal(month),10, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
		return  pay;
	}

	/**
	 *  等本等息按月还款 ，计算利息
	 * @param amount
	 * @return
	 */
	public static  BigDecimal calculateInterestByAvg(BigDecimal amount, BigDecimal annualizedRate){
		BigDecimal pay  = amount	.multiply(annualizedRate.divide(MONTH_LEN, 10, BigDecimal.ROUND_HALF_UP)).setScale(2, BigDecimal.ROUND_HALF_UP);
		return  pay;
	}
	
	/**
	 *  等本等息按周还款 ，计算本金
	 * @param amount
	 * @param weeks
	 * @return
	 */
	public static  BigDecimal calculatePrincipalByAvgWeek(BigDecimal amount, int weeks){
		BigDecimal pay = amount.divide(new BigDecimal(weeks),10, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
		return pay;
	}
	
	/**
	 * 
	 * @Description 等本等息按周还款 ，计算利息
	 * @param amount
	 * @param annualizedRate
	 * @param days
	 * @return
	 * @author luwenshan
	 * @time 2016年10月31日 下午2:10:35
	 */
	public static BigDecimal calculateInterestByAvgWeek(BigDecimal amount, BigDecimal annualizedRate, int days){
		BigDecimal pay = amount.multiply(annualizedRate.divide(YEAR_DAYS, 10, BigDecimal.ROUND_HALF_UP))
				         .multiply(new BigDecimal(days))
				         .setScale(2, BigDecimal.ROUND_HALF_UP);
		return pay;
	}
	
	/**
	 * 
	 * @Description 等本等息 ，计算几个周的预期总利息
	 * @param amount
	 * @param annualizedRate
	 * @param weeks
	 * @return
	 * @author luwenshan
	 * @time 2016年10月31日 下午2:10:27
	 */
	public static BigDecimal calculateTotalInterestByWeek(BigDecimal amount, BigDecimal annualizedRate, int weeks){
		BigDecimal oneWeekInterest = amount.multiply(annualizedRate.divide(YEAR_DAYS, 10, BigDecimal.ROUND_HALF_UP)).multiply(new BigDecimal(7));
		BigDecimal pay = oneWeekInterest.multiply(new BigDecimal(weeks)).setScale(2, BigDecimal.ROUND_HALF_UP);
		return pay;
	}
	
	/**
	 * 
	 * @Description 按日计息，按季付息，到期还本，计算利息
	 * @param amount
	 * @param annualizedRate
	 * @param days
	 * @return
	 * @author luwenshan
	 * @time 2016年10月31日 下午2:10:43
	 */
	public static BigDecimal calculateInterestByAvgSeason(BigDecimal amount, BigDecimal annualizedRate, int days){
		BigDecimal divide = annualizedRate.divide(YEAR_DAYS,10, BigDecimal.ROUND_HALF_UP);
		BigDecimal pay = amount.multiply(divide).multiply(new BigDecimal(days)).setScale(2, BigDecimal.ROUND_HALF_UP);
		return pay;
	}
	
	/**
	 *  等本等息 ，计算几个月的总利息,
	 * @param amount
	 * @return
	 */
	public static  BigDecimal calculateInterestAndMonthByAvg(BigDecimal amount, BigDecimal annualizedRate,int month){
		BigDecimal pay  = amount	.multiply(annualizedRate.divide(MONTH_LEN, 10, BigDecimal.ROUND_HALF_UP))
				.multiply(new BigDecimal(month))
				.setScale(2, BigDecimal.ROUND_HALF_UP);
		return  pay;
	}


	/**
	 * 单位利息计算方法
	 * 
	 * @param returnType
	 * @param amount
	 * @param annualizedRate
	 * @return
	 */
	public static BigDecimal getUnitInterest(String returnType, BigDecimal amount, BigDecimal annualizedRate) {
		if (DebtEnum.RETURN_TYPE_DAY.getCode().equals(returnType)||DebtEnum.RETURN_TYPE_ONCE.getCode().equals(returnType)) {
			return amount.multiply(annualizedRate.divide(YEAR_DAYS, 10, BigDecimal.ROUND_HALF_UP))
					.setScale(2, BigDecimal.ROUND_HALF_UP);
		}
		return BigDecimal.ZERO;
	}

	/**
	 * 债权本息表的应付利息
	 * @param returnType 还款方式
	 * @param amount 项目总额
	 * @param annualizedRate 年化率
	 * @return
	 */
	public static BigDecimal getInterest(String returnType, BigDecimal amount, BigDecimal annualizedRate){
		BigDecimal interest = BigDecimal.ZERO;
		if (DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getCode().equals(returnType)) {
				interest = amount
						.multiply(annualizedRate.divide(MONTH_LEN, 10, BigDecimal.ROUND_HALF_UP)).setScale(2, BigDecimal.ROUND_HALF_UP);
		}
		return interest;
		
	}
	
	/**
	 * 交易本息表的应付利息
	 * @param returnType
	 * @param amount
	 * @param annualizedRate
	 * @param period
	 * @param debtStartDate
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static BigDecimal getTransactionInterest(String returnType, BigDecimal amount, BigDecimal annualizedRate,int period,Date debtStartDate,Date startDate,Date endDate){
		BigDecimal interest = BigDecimal.ZERO;
		if (DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getCode().equals(returnType)) {
			if (period == 0) {// 第一期
				int days = DateUtils.getIntervalDays(startDate, endDate) + 1;
				int naturlMonthDays = DateUtils.getIntervalDays(debtStartDate, endDate) + 1;
				interest = amount
						.multiply(annualizedRate.divide(MONTH_LEN, 10, BigDecimal.ROUND_HALF_UP))
						.multiply(
								new BigDecimal(days).divide(new BigDecimal(naturlMonthDays), 10,
										BigDecimal.ROUND_HALF_UP)).setScale(2, BigDecimal.ROUND_HALF_UP);
			}else {
				interest = amount
						.multiply(annualizedRate.divide(MONTH_LEN, 10, BigDecimal.ROUND_HALF_UP)).setScale(2, BigDecimal.ROUND_HALF_UP);
			}
		}
		return interest;
		
	}
	
	
	/**
	 * 应付本金计算方式
	 * @param returnType  还款方式
	 * @param amount  投资总额
	 * @param totalPeriod   总期数
	 * @param period    当前期数
	 * @return
	 */
	public static BigDecimal getPrincipal(String returnType,BigDecimal amount,int totalPeriod,int period){
		BigDecimal principal = BigDecimal.ZERO;
		if(totalPeriod<=0){//必须大于等于1期
			return principal;
		}
		if (DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getCode().equals(returnType)) {
			if(period==totalPeriod-1){
				principal = amount.subtract(amount.divide(new BigDecimal(totalPeriod), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(totalPeriod-1)));
			}else{
				principal = amount.divide(new BigDecimal(totalPeriod), 2, BigDecimal.ROUND_HALF_UP);
			}
		}
		return principal;
	}
	/**
	 * 线下利息计算方法
	 * 
	 * @param investAmount
	 * @param days
	 * @param offlineRate
	 * @return
	 */
	public static BigDecimal getDayInterestSettlement(BigDecimal investAmount, int days, BigDecimal offlineRate) {
		if (offlineRate != null) {
			return investAmount.multiply(new BigDecimal(days)).multiply(
					offlineRate.divide(YEAR_DAYS, 10, BigDecimal.ROUND_HALF_UP));
		}
		return BigDecimal.ZERO;
	}

	/**
	 * 根据分红百分比获取分红总额
	 * 
	 * @param bonusPercent
	 * @return
	 */
	public static BigDecimal getTotalBonusByPercent(BigDecimal bonusPercent, BigDecimal totalIncome) {
		BigDecimal totalUserBonus = totalIncome.multiply(
				bonusPercent.divide(new BigDecimal(100), 10, BigDecimal.ROUND_HALF_UP)).setScale(2,
				BigDecimal.ROUND_HALF_UP);
		return totalUserBonus;
	}

	public static BigDecimal getUnitRental(BigDecimal totalRental, Integer leaseDays) {
		BigDecimal unitRental = totalRental.divide(new BigDecimal(leaseDays), 10, BigDecimal.ROUND_HALF_UP).setScale(2,
				BigDecimal.ROUND_HALF_UP);
		return unitRental;
	}
	
	public static BigDecimal getBonusAnnualizedRate(BigDecimal bonusAmount, BigDecimal totalInterest, BigDecimal annualizedRate, BigDecimal ExtraAnnualizedRate) {
		if(ExtraAnnualizedRate!=null) {
			annualizedRate = annualizedRate.add(ExtraAnnualizedRate);
		}
		return BigDecimal.valueOf(bonusAmount.doubleValue()/totalInterest.doubleValue()*annualizedRate.doubleValue()).setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 格式化金额
	 * 
	 * @param price
	 * @return
	 */
	public static String getFormatPrice(BigDecimal price) {
		if (price == null) {
			return "";
		}
		return new DecimalFormat("###,###.##").format(price);
	}

	/**
	 * 格式化金额
	 * @Deprecated 此方法内#占位符，当匹配为0时，返回空，例如0.01格式化为.01
	 * @param price
	 * @return
	 */
	@Deprecated 
	public static String getFormatPriceDefaultZero(BigDecimal price) {
		if (price == null) {
			return "";
		}
		return new DecimalFormat("###,###.00").format(price);
	}

	/**
	 * 格式化金额，中间无分隔符“,”
	 */
	public static String getFormatPriceNoSep(BigDecimal price){
		if(price ==null){
			return "";
		}
		return new DecimalFormat("######.##").format(price);
	}
	
	/**
	 * 格式化金额，中间无分隔符“,”,结尾默认为00
	 */
	public static String getFormatPriceNoSepDefaultZero(BigDecimal price){
		if(price ==null){
			return "";
		}
		return new DecimalFormat("######.00").format(price);
	}
	
	/**
	 * 格式化金额
	 * @param price
	 * @return
	 */
	public static String getFormatPriceRound(BigDecimal price){
		if(price == null){
			return "";
		}
		String formatPrice = new DecimalFormat("###.00").format(price);
		if(formatPrice.startsWith(".")){
			return "0"+formatPrice;
		}
		return formatPrice;
	}
	
	 /**
	  * 格式化金额
	  * @param price   5000
	  * @return  ¥5000.00
	  */	
	 public static String formatCurrency(BigDecimal price) {
		 if (price == null) {
			 return "¥0.0";
		 }
		 NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.CHINA);
		 String format = currency.format(price);
		 String result = "";
		 if (price.compareTo(BigDecimal.ZERO) >= 0) {
			 result = "¥" + format.substring(1);
		 } else {
			 result = "¥-" + format.substring(2);
		 }
		 return result;

	 }

	/**
	 * 格式化金额 ,不带 人民币符号
	 * 
	 * @param price
	 *            5000
	 * @return 5000.00 author: pengyong 下午4:26:42
	 */
	public static String formatCurrencyNoUnit(BigDecimal price) {
		String formatCurrency = formatCurrency(price);
		String format = formatCurrency.substring(1);
		return format;

	}

	/**
	 * 获得金额整数部分，默认为零
	 * 
	 * @param price
	 * @return
	 */
	public static String getIntegerDefaultZero(BigDecimal price) {
		String _price = getInteger(price);
		if (StringUtil.isNotBlank(_price)) {
			return _price;
		}
		return "0";
	}

	/**
	 * 获得金额小数部分，默认为零
	 * 
	 * @param price
	 * @return
	 */
	public static String getDecimalDefaultZero(BigDecimal price) {
		String _price = getDecimal(price);
		if (StringUtil.isNotBlank(_price)) {
			return _price;
		}
		return ".00";
	}

	/**
	 * 获得整数部分
	 * 
	 * @param price
	 * @return
	 */
	public static String getInteger(BigDecimal price) {
		if (price == null) {
			return "";
		}
		//String p = getFormatPriceDefaultZero(price);
		String p = formatCurrencyNoUnit(price);
		if (p.lastIndexOf(".") > 0) {
			return p.substring(0, p.lastIndexOf("."));
		}
		return "";
	}

	/**
	 * 获得小数部分
	 * 
	 * @param price
	 * @return
	 */
	public static String getDecimal(BigDecimal price) {
		if (price == null) {
			return "";
		}
		//String decimal = getFormatPriceDefaultZero(price);
		String decimal = formatCurrencyNoUnit(price);
		if (decimal.lastIndexOf(".") >= 0) {
			return decimal.substring(decimal.lastIndexOf("."), decimal.length());
		}
		return "";
	}

	/**
	 * 格式化百分比
	 * 
	 * @param percent
	 *            0.12
	 * @return 12 author: pengyong 下午4:29:09
	 */
	public static String getFormartPercentage(BigDecimal percent) {
		if (percent == null) {
			return "";
		}
		NumberFormat formart = NumberFormat.getPercentInstance();
		String format = formart.format(percent);
		return format.substring(0, format.length() - 1);
	}

	public static double doubleAdd(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.add(b2).doubleValue();
	}
	
	/**
	 * 格式化金额
	 * 
	 * @param price
	 * @return
	 */
	public static String getTempFormatPrice(Integer price) {
		if (price == null) {
			return "";
		}
		return new DecimalFormat("###,###.##").format(price);
	}
	
	/**
	 * 
	 * @Description:金额乘以百分比
	 * @param amount
	 * @param annualizedRate 
	 * @return
	 * @author: chaisen
	 * @time:2016年1月7日 上午10:18:40
	 */
	public static BigDecimal getManagerAmount(BigDecimal amount, BigDecimal manageFeeRate){
		BigDecimal interest = BigDecimal.ZERO;
		if(amount==null){
			amount = BigDecimal.ZERO;
		}
		if(manageFeeRate==null){
			manageFeeRate = BigDecimal.ZERO;
		}
		interest = amount.multiply(manageFeeRate.divide(HUNDRED, 10, BigDecimal.ROUND_HALF_UP)).setScale(2, BigDecimal.ROUND_HALF_UP);
		return interest;
		
	}
	//滞纳金  优化 days 
	public static BigDecimal getLateFeeAmount(BigDecimal amount, BigDecimal manageFeeRate,int days){
		BigDecimal interest = BigDecimal.ZERO;
		if(amount==null){
			amount = BigDecimal.ZERO;
		}
		if(manageFeeRate==null){
			manageFeeRate = BigDecimal.ZERO;
		}
		interest = amount.multiply(manageFeeRate.divide(THOUSAND, 10, BigDecimal.ROUND_HALF_UP)).multiply(new BigDecimal(days)).setScale(2, BigDecimal.ROUND_HALF_UP);
		return interest;
		
	}
	
	/**
	 * @Description:交易本息的滞纳金
	 * @param amount
	 * @param manageFeeRate
	 * @param days
	 * @return
	 * @author: fuyili
	 * @time:2016年6月5日 下午2:44:36
	 */
	public static BigDecimal getTransactionInterestLateFeeAmount(BigDecimal amount, BigDecimal manageFeeRate,int days,int periods){
		BigDecimal interest = BigDecimal.ZERO;
		if(amount==null){
			amount = BigDecimal.ZERO;
		}
		if(manageFeeRate==null){
			manageFeeRate = BigDecimal.ZERO;
		}
		interest = amount.multiply(manageFeeRate.divide(THOUSAND, 10, BigDecimal.ROUND_HALF_UP)).multiply(new BigDecimal(days)).divide(new BigDecimal(periods)).setScale(2, BigDecimal.ROUND_HALF_UP);
		return interest;
		
	}
	
	/**
	 * 格式化金额,取亿
	 * 
	 * @param price
	 * @return   author: zhanghao 
	 */
	public static String getFormatPriceHundredMillion(String price) {
		String priceStrNoSeq = price.replace(",", "");
		
		if (StringUtil.isBlank(priceStrNoSeq) ) {
			return "0";
		}
		
		BigDecimal priceBig =  new BigDecimal(priceStrNoSeq);
		
		
		String priceStr = getFormatPriceNoSepDefaultZero(priceBig);
		
		if(priceStr.indexOf(".")<8){
			return "0";
		}else{
			return priceStr.substring(0, priceStr.indexOf(".")-8);
		}
		
	}
	
	/**
	 * 格式化金额,取万
	 * 
	 * @param price
	 * @return   author: zhanghao 
	 */
	public static String getFormatPriceTenThousand(String price) {
		String priceStrNoSeq = price.replace(",", "");
		
		if (StringUtil.isBlank(priceStrNoSeq) ) {
			return "0";
		}
		BigDecimal priceBig =  new BigDecimal(priceStrNoSeq);
		
		
		String priceStr = getFormatPriceNoSepDefaultZero(priceBig);
		
		if(priceStr.indexOf(".")<4){
			return "0";
		}else{
			return priceStr.substring(0, priceStr.indexOf(".")-4);
		}
		
	}

	/**
	 * 
	 * @Description:TODO
	 * @param bigDecimal
	 *            被运算的数
	 * @param str
	 *            运算表达式：例如 *=0.005 返回 bigDecimal * 0.005
	 * @return
	 * @author: wangyanji
	 * @time:2016年3月17日 上午11:47:01
	 */
	public static BigDecimal arithmeticByString(BigDecimal big1, String str) {
		String[] array = str.split("=");
		BigDecimal big2 = new BigDecimal(array[1]);
		if ("*".equals(array[0])) {
			return big1.multiply(big2);
		}
		return null;
	}
	
	/**
	 * 金额相加
	 */
	public static BigDecimal addDecimal(BigDecimal... addEle){
		BigDecimal result = BigDecimal.ZERO;
		for(int i=0;i<addEle.length;i++){
			if(addEle[i]==null){
				addEle[i] = BigDecimal.ZERO;
			}
			result = result.add(addEle[i]);
		}
		return result;
	}
	
	public static BigDecimal subtractDecimal(BigDecimal firstEle,BigDecimal secondEle){
		if(firstEle==null){
			firstEle = BigDecimal.ZERO;
		}
		if(secondEle==null){
			secondEle = BigDecimal.ZERO;
		}
		return firstEle.subtract(secondEle);
		
	}
	
	/**
	 * 计算转让项目年化
	 * 
	 * @param residualInterest
	 * @param discount
	 * @param transferAmount
	 * @param days
	 * @return
	 */
	public static BigDecimal getTransferAnnualizedRate(BigDecimal residualInterest,BigDecimal discount,BigDecimal transferAmount, int days) {
		BigDecimal transferAnnualizedRate  = BigDecimal.ZERO;
		// 转让项目收益率 = （项目剩余利息（总）+折价） / 转让价格   * （360/剩余收益天数）（计算得到的收益率 直接舍弃）
		transferAnnualizedRate = ((residualInterest.add(discount)).divide(transferAmount,10, BigDecimal.ROUND_HALF_UP))
				.multiply((YEAR_DAYS.divide(new BigDecimal(days),10, BigDecimal.ROUND_HALF_UP))).setScale(2, BigDecimal.ROUND_HALF_UP );
		return transferAnnualizedRate;
	}

	/**
	 * @Description:获取转让交易的交易本息的本金
	 * 				计算公式：
	 * 					转让后单笔交易本金/转让项目的交易本金*转让前项目的交易本息的本金
	 * @param beforeTransationAmount
	 * @param afterTransactionAmount
	 * @param transactionPayablePrincipal
	 * @return
	 * @author: fuyili
	 * @time:2016年9月22日 下午7:27:37
	 */
	public static BigDecimal calculateTransferTransactionPrincipal(BigDecimal beforeTransationAmount,
			BigDecimal afterTransactionAmount, BigDecimal beforeTransactionInterestPayablePrincipal) {
		if (beforeTransationAmount.compareTo(BigDecimal.ZERO) <= 0
				|| afterTransactionAmount.compareTo(BigDecimal.ZERO) <= 0
				|| beforeTransactionInterestPayablePrincipal.compareTo(BigDecimal.ZERO) <= 0) {
			return BigDecimal.ZERO;
		}
		return afterTransactionAmount.divide(beforeTransationAmount, 10, BigDecimal.ROUND_HALF_UP)
				.multiply(beforeTransactionInterestPayablePrincipal).setScale(2, BigDecimal.ROUND_HALF_UP);

	}
	
	
	/**
	 * @Description:获取转让交易的交易本息的利息
	 * 				计算公式：
	 * 					转让后单笔交易本金/转让项目的交易本金*转让前项目的交易本息的利息
	 * @param beforeTransationAmount
	 * @param afterTransactionAmount
	 * @param transactionPayablePrincipal
	 * @return
	 * @author: fuyili
	 * @time:2016年9月22日 下午7:27:37
	 */
	public static BigDecimal calculateTransferTransactionInterest(BigDecimal beforeTransationAmount,
			BigDecimal afterTransactionAmount, BigDecimal beforeTransactionInterestPayableInterest) {
		if (beforeTransationAmount.compareTo(BigDecimal.ZERO) <= 0
				|| afterTransactionAmount.compareTo(BigDecimal.ZERO) <= 0
				|| beforeTransactionInterestPayableInterest.compareTo(BigDecimal.ZERO) <= 0) {
			return BigDecimal.ZERO;
		}
		return afterTransactionAmount.divide(beforeTransationAmount, 10, BigDecimal.ROUND_HALF_UP)
				.multiply(beforeTransactionInterestPayableInterest).setScale(2, BigDecimal.ROUND_HALF_UP);

	}
	
	
	/**
	 * 获取转让项目进度
	 */
	public static String getTransferNumberProgress(BigDecimal totalAmount, BigDecimal availableBalance) {
		String progress = "0";
		if (availableBalance != null) {
			if (availableBalance.compareTo(BigDecimal.ZERO) <= 0) {
				progress = "100";
			} else if (availableBalance.compareTo(totalAmount) == 0) {
				progress = "0";
			} else {
				progress = new DecimalFormat("###.##").format((totalAmount.subtract(availableBalance)).divide(totalAmount, 4,
						RoundingMode.HALF_UP).multiply(new BigDecimal("100")));
			}
		}
		return progress;
	}
	
}
