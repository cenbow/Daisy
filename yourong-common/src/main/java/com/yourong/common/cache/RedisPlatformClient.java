package com.yourong.common.cache;

import java.math.BigDecimal;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yourong.common.constant.RedisConstant;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.StringUtil;

/**
 * 平台redis相关
 */
public class RedisPlatformClient {

	private static final Logger log = LoggerFactory.getLogger(RedisPlatformClient.class);

	/**
	 * 设置新浪存钱罐7日收益
	 * 
	 * @return
	 */
	public static boolean setSinapaySevenDaysBonus(String bonus) {
		return RedisManager.hset(RedisConstant.REDIS_KEY_PLATFORM, RedisConstant.REDIS_FIELD_PLATFORM_SINAPAY_SEVEN_DAYS_BONUS, bonus);

	}

	/**
	 * 获取新浪存钱罐7日收益
	 * 
	 * @return
	 */
	public static String getSinapaySevenDaysBonus() {
		return RedisManager.hget(RedisConstant.REDIS_KEY_PLATFORM, RedisConstant.REDIS_FIELD_PLATFORM_SINAPAY_SEVEN_DAYS_BONUS);
	}

	/**
	 * 设置平台注册会员数
	 * 
	 * @return
	 */
	public static boolean setMemberCount(int count) {
		return RedisManager.hset(RedisConstant.REDIS_KEY_PLATFORM, RedisConstant.REDIS_FIELD_PLATFORM_MEMBER_COUNT, count + "");

	}

	/**
	 * 获取平台注册会员数
	 * 
	 * @return
	 */
	public static Long getMemberCount() {
		String count = RedisManager.get(RedisConstant.REDIS_KEY_PLATFORM + RedisConstant.REDIS_SEPERATOR
				+ RedisConstant.REDIS_FIELD_PLATFORM_MEMBER_INCR_COUNT);
		if (StringUtil.isNotBlank(count)) {
			return Long.parseLong(count);
		} else {
			return 0L;
		}
	}

	/**
	 * 增加平台注册会员数
	 * 
	 * @param
	 * @param count
	 * @return
	 */
	public static boolean addMemberCount(int count) {
		// Integer totalCount = getMemberCount()+count;
		// Long value = NumberUtils.toLong(Integer.valueOf(count).toString());
		RedisManager.incr(RedisConstant.REDIS_KEY_PLATFORM + RedisConstant.REDIS_SEPERATOR
				+ RedisConstant.REDIS_FIELD_PLATFORM_MEMBER_INCR_COUNT);
		return true;

	}

	public static boolean addReceiveGiftMoneyMember(Long memberId, String username, String avatar) {
		String memberStr = getReceiveGiftMoneyMembers();
		if (StringUtil.isBlank(avatar)) {
			// 设置为默认头像
			avatar = "http://www.yrw.com/static/img/member/avatar_35x35.png";
		} else {
			avatar = "https://oss-cn-hangzhou.aliyuncs.com" + avatar;
		}
		if (StringUtil.isBlank(memberStr)) {
			RedisManager.set(RedisConstant.REDIS_KEY_PLATFORM_RECEIVE_GIFT_MONEY, memberId + "," + username + "," + avatar);
		} else {
			RedisManager.set(RedisConstant.REDIS_KEY_PLATFORM_RECEIVE_GIFT_MONEY, memberId + "," + username + "," + avatar + "|"
					+ memberStr);
		}
		return true;
	}

	public static String getReceiveGiftMoneyMembers() {
		return RedisManager.get(RedisConstant.REDIS_KEY_PLATFORM_RECEIVE_GIFT_MONEY);
	}

	/**
	 * 统计app下载次数
	 * 
	 * @param userAgent
	 * @param count
	 * @return
	 */
	public static boolean addAppDownLoadCount(String userAgent, int count) {
		Long value = NumberUtils.toLong(Integer.valueOf(count).toString());
		// RedisManager.hincrBy(RedisConstant.REDIS_KEY_YOURONGAPKDOWNLOAD,
		// userAgent, value);
		RedisManager.hincrBy(RedisConstant.REDIS_KEY_YOURONGAPKDOWNLOAD + RedisConstant.REDIS_SEPERATOR
				+ RedisConstant.REDIS_KEY_YOURONGAPKDOWNLOAD_COUNT, RedisConstant.REDIS_FIELD_YOURONGAPKDOWNLOAD_COUNT, value);
		return true;
	}

	/**
	 * 
	 * @param count
	 * @return
	 */
	public static boolean addRedFridayCouponNumber() {
		RedisManager.hincrBy(RedisConstant.REDIS_KEY_PLATFORM, RedisConstant.REDIS_FIELD_PLATFORM_RED_FRIDAY_COUNT, 1L);
		return true;
	}

	public static int getRedFridayCouponCount() {
		String count = RedisManager.hget(RedisConstant.REDIS_KEY_PLATFORM, RedisConstant.REDIS_FIELD_PLATFORM_RED_FRIDAY_COUNT);
		if (StringUtil.isNotBlank(count)) {
			return Integer.parseInt(count);
		} else {
			return 0;
		}
	}

	/**
	 * 获取app下载次数
	 * 
	 * @param userAgent
	 * @param count
	 * @return
	 */
	public static int getAppDownLoadCount() {
		String platDownLoad = RedisManager.hget(RedisConstant.REDIS_KEY_YOURONGAPKDOWNLOAD + RedisConstant.REDIS_SEPERATOR
				+ RedisConstant.REDIS_KEY_YOURONGAPKDOWNLOAD_COUNT, RedisConstant.REDIS_FIELD_YOURONGAPKDOWNLOAD_COUNT);
		String otherDownLoad = RedisManager.hget(RedisConstant.REDIS_KEY_YOURONGAPKDOWNLOAD + RedisConstant.REDIS_SEPERATOR
				+ RedisConstant.REDIS_KEY_YOURONGAPKDOWNLOAD_COUNT, RedisConstant.REDIS_FIELD_YOURONGAPKDOWNLOAD_OTHERCOUNT);
		return Integer.parseInt(platDownLoad) + Integer.parseInt(otherDownLoad);
	}

	/**
	 * 破亿记录时间点
	 */
	public static void breakHundredMillion(BigDecimal platformTotalInvest) {
		String key = RedisConstant.REDIS_PLATFORM_TOTAL_INVEST_TIME;
		long unit = 100000000;
		Long level = platformTotalInvest.intValue() / unit;
		String field = level + "00000000";
		String time = RedisManager.hget(key, field);
		if (StringUtil.isNotBlank(time)) {
			return;
		}
		RedisManager.hset(key, field, DateUtils.getStrFromDate(DateUtils.getCurrentDate(), DateUtils.TIME_PATTERN_SESSION));
	}
}
