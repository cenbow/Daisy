package com.yourong.common.util;

import java.math.BigDecimal;
import java.text.NumberFormat;


/**
 * 数字格式化工具类
 * @author Administrator
 *
 */
public class NumberFormatUtil {

	
	/**
	 * 获得小数部分
	 * @param price
	 * @return
	 */
	public static String formatNumberToChinese(int number){
		switch (number) {
		case 1:
			return "一";
		case 2:
			return "二";
		case 3:
			return "三";
		case 4:
			return "四";
		case 5:
			return "五";
		case 6:
			return "六";
		case 7:
			return "七";
		case 8:
			return "八";
		case 9:
			return "九";
			
		case 10:
			return "十";
		default:
			break;
		}
		return "";
	}
	
	public static String formatNumberFromBigDecimalToString(BigDecimal bigdecimal){
		NumberFormat nf = NumberFormat.getInstance();
        return nf.format(bigdecimal)+"";
	}
	
	public static void main(String[] args) {		
		
		System.out.println(formatNumberToChinese(1));
		
	}
	
}
