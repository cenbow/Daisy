package com.yourong.common.util;

import java.util.Date;
import java.util.Locale;

import org.ocpsoft.prettytime.PrettyTime;

/**
 * PrettyTime工具类
 * @author wangyanji
 *
 */
public class PrettyTimeUtils {

	private static final PrettyTime pt = new PrettyTime(Locale.CHINESE);

	/**
	 * 获取目标时间到当前时间的表达
	 * @param targetDate
	 * @return
	 */
	public static String getTimeIntervalString(Date targetDate) {
		return pt.format(targetDate);
	}
	
	public static void main(String[] args) {
		System.out.println(PrettyTimeUtils.getTimeIntervalString(new Date()));
	}
}
