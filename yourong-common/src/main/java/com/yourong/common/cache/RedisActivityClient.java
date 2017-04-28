package com.yourong.common.cache;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yourong.common.constant.ActivityConstant;
import com.yourong.common.constant.Constant;
import com.yourong.common.constant.RedisConstant;
import com.yourong.common.thirdparty.sinapay.common.util.StringUtil;
import com.yourong.common.util.DateUtils;

public class RedisActivityClient {

	private static final Logger logger = LoggerFactory.getLogger(RedisActivityClient.class);

	/**
	 * 标记用户参与的活动
	 * 
	 * @param activityId
	 * @param memberId
	 */
	public static void setActivitiesMember(Long activityId, Long memberId) {
		String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR + memberId;
		RedisManager.hset(key, RedisConstant.REDIS_FIELD_ACTIVITY_IS_PARTICIPATE + activityId, activityId.toString());
	}

	/**
	 * 判断用户是否有参与活动
	 * 
	 * @param activityId
	 * @param memberId
	 * @return
	 */
	public static boolean isParticipateInActivity(Long activityId, Long memberId) {
		String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR + memberId;
		String value = RedisManager.hget(key, RedisConstant.REDIS_FIELD_ACTIVITY_IS_PARTICIPATE + activityId);
		if (StringUtil.isNotBlank(value) && value.equals(activityId.toString())) {
			return true;
		}
		return false;
	}

	/**
	 * 清空进行中活动的缓存
	 */
	public static void removeProgressActivity() {
		RedisManager.removeObject(RedisConstant.REDIS_ACTIVITY_LIST);
	}

	/**
	 * 记录非会员活动中奖缓存
	 * 
	 * @param activityId
	 * @param mobile
	 * @param uniqueCode
	 * @param rewardCode
	 */
	public static void setActivityOutsiderMobile(Long activityId, Long mobile, String uniqueCode, String rewardCode) {
		String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR + RedisConstant.REDIS_KEY_ACTIVITY_OUTSIDER_MOBILE
				+ RedisConstant.REDIS_SEPERATOR + mobile;
		RedisManager.hset(key, activityId + RedisConstant.REDIS_SEPERATOR + uniqueCode, rewardCode);
	}

	/**
	 * 获取记录非会员活动中奖缓存
	 * 
	 * @param activityId
	 * @param mobile
	 */
	public static String getActivityOutsiderMobile(Long activityId, Long mobile, String uniqueCode) {
		String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR + RedisConstant.REDIS_KEY_ACTIVITY_OUTSIDER_MOBILE
				+ RedisConstant.REDIS_SEPERATOR + mobile;
		return RedisManager.hget(key, activityId + RedisConstant.REDIS_SEPERATOR + uniqueCode);
	}

	/**
	 * 获取站外人员的所有待发放奖励
	 * 
	 * @param activityId
	 * @param memberMobile
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> getOutsiderAllRewards(Long memberMobile) {
		String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR + RedisConstant.REDIS_KEY_ACTIVITY_OUTSIDER_MOBILE
				+ RedisConstant.REDIS_SEPERATOR + memberMobile;
		return RedisManager.hgetAll(key);
	}

	/**
	 * 清除缓存
	 * 
	 * @param activityId
	 * @param mobile
	 * @param uniqueCode
	 */
	public static void delOutsiderAllRewards(Long activityId, Long mobile, String uniqueCode) {
		String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR + RedisConstant.REDIS_KEY_ACTIVITY_OUTSIDER_MOBILE
				+ RedisConstant.REDIS_SEPERATOR + mobile;
		RedisManager.hdel(key, activityId + RedisConstant.REDIS_SEPERATOR + uniqueCode);
		if (MapUtils.isEmpty(RedisManager.hgetAll(key))) {
			RedisManager.removeObject(key);
		}
	}

	/**
	 * 校验参加次数
	 * 
	 * @param activity
	 *            活动ID
	 * @param mobile
	 *            参与者手机号
	 * @param checkNum
	 *            参与次数
	 * @return 达到校验数 返回 false，else 返回 true
	 */
	public static boolean checkActivityIsParticipate(Long activity, Long mobile, int checkNum) {
		String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR + RedisConstant.REDIS_KEY_ACTIVITY_PARTICIPATE
				+ RedisConstant.REDIS_SEPERATOR + activity + RedisConstant.REDIS_SEPERATOR + mobile;
		String participateNum = RedisManager.get(key);
		if (StringUtil.isNotEmpty(participateNum)) {
			if (Integer.parseInt(participateNum) >= checkNum) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 记录参加活动的手机号参加次数,保存到23点59分59秒999
	 * 
	 * @param activity
	 * @param mobile
	 * @param addNum
	 * @return
	 */
	public static Long saveActivityParticipateNum(Long activity, Long mobile, int addNum) {
		String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR + RedisConstant.REDIS_KEY_ACTIVITY_PARTICIPATE
				+ RedisConstant.REDIS_SEPERATOR + activity + RedisConstant.REDIS_SEPERATOR + mobile;
		Long retValue = RedisManager.incr(key);
		int timeIntervalSencond = DateUtils.getTimeIntervalSencond(DateUtils.getCurrentDate(),
				DateUtils.getEndTime(DateUtils.getCurrentDate()));
		RedisManager.expire(key, timeIntervalSencond);
		return retValue;
	}

	/**
	 * 
	 * @Description:百万现金券
	 * @param investAmount
	 * @return
	 * @author: wangyanji
	 * @time:2016年1月5日 下午2:28:44
	 */
	public static int millionCoupon(BigDecimal investAmount) {
		String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR + RedisConstant.REDIS_KEY_ACTIVITY_MILLIONCOUPON_NAME
				+ RedisConstant.REDIS_SEPERATOR + RedisConstant.REDIS_KEY_ACTIVITY_MILLIONCOUPON_FUND;
		String fund = RedisManager.get(key);
		if (StringUtil.isBlank(fund)) {
			fund = Constant.ACTIVITY_MILLIONCOUPON_FUND;
			RedisManager.set(key, fund);
			// 一个月后过期
			RedisManager.expire(key, 60 * 60 * 24 * 30);
		}
		BigDecimal number = new BigDecimal(fund);
		if (number.compareTo(BigDecimal.ZERO) == 1) {
			if (investAmount != null) {
				RedisManager.decrBy(key, investAmount.longValue());
			}
			return number.intValue();
		}
		return 0;
	}


	/**
	 * 
	 * @Description:更新红包总数
	 * @param sourceId
	 * @param splitNum
	 * @param seconds
	 * @author: wangyanji
	 * @time:2016年1月9日 下午10:07:53
	 */
	public static void incrPopularityRedBag(Long activityId, Long splitNum) {
		String totalKey = RedisConstant.REDIS_KEY_ACTIVITY_REDBAG_TOTALNUM + RedisConstant.REDIS_SEPERATOR + activityId;
		RedisManager.incrby(totalKey, splitNum);
	}

	/**
	 * 
	 * @Description:更新已领红包总数
	 * @param sourceId
	 * @param splitNum
	 * @author: wangyanji
	 * @time:2016年1月8日 下午3:40:55
	 */
	public static void decrPopularityRedBag(Long activityId) {
		String claimKey = RedisConstant.REDIS_KEY_ACTIVITY_REDBAG_CLAIMNUM + RedisConstant.REDIS_SEPERATOR + activityId;
		RedisManager.incr(claimKey);
	}

	/**
	 * 
	 * @Description:用户领取红包 + 1, 返回结果
	 * @param sourceId
	 * @param mobile
	 * @return
	 * @author: wangyanji
	 * @time:2016年1月8日 下午5:28:43
	 */
	public static Long hincrRedBagMobile(Long sourceId, Long mobile, Long operValue, Integer expireTime) {
		String key = RedisConstant.REDIS_KEY_ACTIVITY_REDBAG_MOBILE + RedisConstant.REDIS_SEPERATOR + mobile;
		boolean exists = RedisManager.isExit(key);
		Long val = RedisManager.hincrBy(key, RedisConstant.REDIS_KEY_ACTIVITY_REDBAG + sourceId, operValue);
		if (!exists) {
			int expire = expireTime == null ? ActivityConstant.activityKeyExpire : expireTime;
			RedisManager.expire(key, expire);
		}
		return val;
	}

	/**
	 * 
	 * @Description:push一组红包到redis
	 * @param sourceId
	 * @param randomList
	 * @return
	 * @author: wangyanji
	 * @time:2016年1月20日 下午3:33:04
	 */
	public static Long rpushRedBagValue(Long sourceId, List<Integer> randomList, int expireTime) {
		String infoKey = RedisConstant.REDIS_KEY_ACTIVITY_REDBAG_INFO + RedisConstant.REDIS_SEPERATOR + sourceId;
		RedisManager.set(infoKey, randomList.size() + RedisConstant.REDIS_SEPERATOR + RedisConstant.REDIS_VALUE_ACTIVITY_REDBAG_WAITING);
		RedisManager.expire(infoKey, expireTime);
		String valuesKey = RedisConstant.REDIS_KEY_ACTIVITY_REDBAG_VALUES + RedisConstant.REDIS_SEPERATOR + sourceId;
		String[] values = new String[randomList.size()];
		for (int i = 0, size = randomList.size(); i < size; i++) {
			values[i] = randomList.get(i).toString();
		}
		Long num = RedisManager.rpush(valuesKey, values);
		RedisManager.expire(valuesKey, expireTime);
		return num;
	}

	/**
	 * 
	 * @Description:rpop一组红包中的一个
	 * @param sourceId
	 * @return
	 * @author: wangyanji
	 * @time:2016年1月20日 下午5:40:05
	 */
	public static String getRedBagInfo(Long sourceId) {
		String infoKey = RedisConstant.REDIS_KEY_ACTIVITY_REDBAG_INFO + RedisConstant.REDIS_SEPERATOR + sourceId;
		return RedisManager.get(infoKey);
	}

	/**
	 * 
	 * @Description:获取红包信息
	 * @param sourceId
	 * @return
	 * @author: wangyanji
	 * @time:2016年1月20日 下午5:40:05
	 */
	public static Integer rpopRedBagValue(Long sourceId) {
		int retInt = -1;
		String key = RedisConstant.REDIS_KEY_ACTIVITY_REDBAG_VALUES + RedisConstant.REDIS_SEPERATOR + sourceId;
		String retStr = RedisManager.rpop(key);
		if (StringUtil.isNotBlank(retStr)) {
			retInt = Integer.valueOf(retStr);
		}
		return retInt;
	}

	/**
	 * 
	 * @Description:变更红包状态
	 * @param sourceId
	 * @param expireTime
	 * @author: wangyanji
	 * @time:2016年3月23日 下午3:44:36
	 */
	public static void updateRedPackageInfo(Long sourceId, int expireTime) {
		String infoKey = RedisConstant.REDIS_KEY_ACTIVITY_REDBAG_INFO + RedisConstant.REDIS_SEPERATOR + sourceId;
		if (expireTime < 1) {
			RedisManager.removeString(infoKey);
		} else {
			String info = RedisManager.get(infoKey);
			if (StringUtil.isNotBlank(info)) {
				info = info.replace(RedisConstant.REDIS_VALUE_ACTIVITY_REDBAG_WAITING, RedisConstant.REDIS_VALUE_ACTIVITY_REDBAG_EMPTY);
				RedisManager.set(infoKey, info);
				RedisManager.expire(infoKey, expireTime);
			}
		}
	}

	/**
	 * 
	 * @Description:获取红包缓存
	 * @param sourceId
	 * @return
	 * @author: wangyanji
	 * @time:2016年3月18日 下午2:55:37
	 */
	public static List<String> getRedBag(Long sourceId) {
		String key = RedisConstant.REDIS_KEY_ACTIVITY_REDBAG_VALUES + RedisConstant.REDIS_SEPERATOR + sourceId;
		return RedisManager.lrangeAll(key);
	}

	/**
	 * 
	 * @Description:获取活动现金券总额限制
	 * @param activityId
	 * @param defaultFund
	 * @return
	 * @author: wangyanji
	 * @time:2016年2月19日 上午10:15:09
	 */
	public static Long getActivityCouponAmountLimit(Long activityId, String defaultFund) {
		String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR + RedisConstant.REDIS_KEY_ACTIVITY_MILLIONCOUPON_FUND
				+ RedisConstant.REDIS_SEPERATOR + activityId;
		String fund = RedisManager.get(key);
		if (StringUtil.isBlank(fund)) {
			fund = defaultFund;
			RedisManager.set(key, fund);
		}
		BigDecimal number = new BigDecimal(fund);
		return number.longValue();
	}

	/**
	 * 
	 * @Description:更新活动现金券总额限制
	 * @param activityId
	 * @param value
	 * @return
	 * @author: wangyanji
	 * @time:2016年2月19日 上午10:15:18
	 */
	public static Long decrActivityCouponAmountLimit(Long activityId, BigDecimal value) {
		String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR + RedisConstant.REDIS_KEY_ACTIVITY_MILLIONCOUPON_FUND
				+ RedisConstant.REDIS_SEPERATOR + activityId;
		return RedisManager.decrBy(key, value.longValue());
	}

	/**
	 * 
	 * @Description:一羊领头分布式锁
	 * @param memberId
	 * @param projectId
	 * @return
	 * @author: wangyanji
	 * @time:2016年3月31日 下午4:54:54
	 */
	public static boolean setFirstInvestInProject(Long projectId, Long orderId) {
		try {
			String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR + RedisConstant.REDIS_KEY_ACTIVITY_LEADING_SHEEP
					+ RedisConstant.REDIS_SEPERATOR + projectId;
			if (RedisManager.isExit(key)) {
				return false;
			}
			String value = String.valueOf(orderId) + RedisConstant.REDIS_SEPERATOR + String.valueOf(DateUtils.getCurrentDate().getTime());
			Long retValue = RedisManager.setnx(key, value);
			if (retValue == 1l) {
				RedisManager.expire(key, ActivityConstant.activityKeyWeekExpire);
				logger.info("一羊领头锁定成功： key={}, value={}", key, value);
				return true;
			}
			return false;
		} catch (Exception e) {
			logger.error("一羊领头锁定失败：  projectId={}", projectId, e);
			return false;
		}
	}

	/**
	 * 
	 * @Description:记录当天投资笔数
	 * @param splitNum
	 * @author: chaisen
	 * @time:2016年7月23日 下午1:30:02
	 */
	public static void incrTransactionCount( Long splitNum) {
		String totalKey = RedisConstant.ACTIVITY_PLAY_OLYMPIC_CURRENT_TRANSACTION_TOTALINVEST + RedisConstant.REDIS_SEPERATOR + DateUtils.getDateStrFromDate(DateUtils.getCurrentDate());
		RedisManager.incrby(totalKey, splitNum);
	}
	/**
	 * 
	 * @Description 记录第四张拼图数量
	 * @param splitNum
	 * @author: chaisen
	 * @time:2016年7月23日 下午1:30:44
	 */
	public static void incrFourPuzzle( Long number) {
		String totalKey = RedisConstant.REDIS_KEY_ACTIVITY+ RedisConstant.REDIS_SEPERATOR+RedisConstant.ACTIVITY_PLAY_OLYMPIC_CURRENT_FOURPUZZLE;
		RedisManager.incrby(totalKey, number);
	}
	
	
	public static boolean setFourOlympicPuzzle(Long transactionId) {
		try {
			String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR + RedisConstant.ACTIVITY_PLAY_OLYMPIC_KEY
					+ RedisConstant.REDIS_SEPERATOR +RedisConstant.ACTIVITY_PLAY_OLYMPIC_FOUR
					+ RedisConstant.REDIS_SEPERATOR + DateUtils.getDateStrFromDate(DateUtils.getCurrentDate());
			if (RedisManager.isExit(key)) {
				return false;
			}
			String value = String.valueOf(transactionId) + RedisConstant.REDIS_SEPERATOR + String.valueOf(DateUtils.getCurrentDate().getTime());
			Long retValue = RedisManager.setnx(key, value);
			if (retValue == 1l) {
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	/**
	 * 五重礼是否领取过
	 * @param projectId
	 * @param fiveRitesType
	 * @param fiveRiteName
	 * @return
	 */
	public static boolean fiveRitesInvestInProject(Long projectId, String  fiveRitesType,String fiveRiteName) {
		try {
			String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR + fiveRitesType
					+ RedisConstant.REDIS_SEPERATOR + projectId;
			if (RedisManager.isExit(key)) {
				return false;
			}
			String value = String.valueOf(projectId) + RedisConstant.REDIS_SEPERATOR + String.valueOf(DateUtils.getCurrentDate().getTime());
			Long retValue = RedisManager.setnx(key, value);
			if (retValue == 1l) {
				RedisManager.expire(key, ActivityConstant.activityKeyWeekExpire);
				logger.info(fiveRiteName+"锁定成功： key={}, value={}", key, value);
				return true;
			}
			return false;
		} catch (Exception e) {
			logger.error(fiveRiteName+"锁定失败：  projectId={}", projectId, e);
			return false;
		}
	}
	
	public static int getReceivedCouponEightNum() {
		int retInt = 0;
		String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR+ RedisConstant.ACTIVITY_ANNERCELEBRATE_CURRENT_COUPONAMOUNT+RedisConstant.REDIS_SEPERATOR+88;
		String retStr = RedisManager.get(key);
		if (StringUtil.isNotBlank(retStr)) {
			retInt = Integer.valueOf(retStr);
		}
		return retInt;
		
	}
	
	public static void incrEightAnnCelebCoupon() {
		String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR+ RedisConstant.ACTIVITY_ANNERCELEBRATE_CURRENT_COUPONAMOUNT+RedisConstant.REDIS_SEPERATOR+88;
		RedisManager.incrby(key, 1L);
	}

	public static int getReceivedCouponNum(String activityCoupon, String date) {
		int retInt = 0;
		String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR+ RedisConstant.ACTIVITY_COUPONAMOUNT+ RedisConstant.REDIS_SEPERATOR+activityCoupon+RedisConstant.REDIS_SEPERATOR+date;
		String retStr = RedisManager.get(key);
		if (StringUtil.isNotBlank(retStr)) {
			retInt = Integer.valueOf(retStr);
		}
		return retInt;
		
	}
	
	public static void incrFourChangeCoupon(String activityCoupon, String date) {
		String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR+ RedisConstant.ACTIVITY_COUPONAMOUNT+ RedisConstant.REDIS_SEPERATOR+activityCoupon+RedisConstant.REDIS_SEPERATOR+date;
		RedisManager.incrby(key, 1L);
	}
	
	
	public static int getSendCouponNum(String date) {
		int retInt = 0;
		String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR+ RedisConstant.ACTIVITY_SEND_COUPON_TOTAL+ RedisConstant.REDIS_SEPERATOR+date;
		String retStr = RedisManager.get(key);
		if (StringUtil.isNotBlank(retStr)) {
			retInt = Integer.valueOf(retStr);
		}
		return retInt;
		
	}
	
	public static void incrSendCoupon(String date) {
		String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR+ RedisConstant.ACTIVITY_SEND_COUPON_TOTAL+ RedisConstant.REDIS_SEPERATOR+date;
		RedisManager.incrby(key, 1L);
	}
	
	
	public static boolean directLotteryIsSend(Long projectId) {
		try {
			String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR + RedisConstant.DIRECT_LOTTERY_KEY_ACTIVITY+ RedisConstant.REDIS_SEPERATOR+ projectId;
			if (RedisManager.isExit(key)) {
				return false;
			}
			String value = String.valueOf(projectId) + RedisConstant.REDIS_SEPERATOR + String.valueOf(DateUtils.getCurrentDate().getTime());
			Long retValue = RedisManager.setnx(key, value);
			if (retValue == 1l) {
				RedisManager.expire(key, ActivityConstant.activityKeyWeekExpire);
				logger.info(projectId+"锁定成功： key={}, value={}", key, value);
				return true;
			}
			return false;
		} catch (Exception e) {
			logger.error(projectId+"锁定失败：  projectId={}", projectId, e);
			return false;
		}
	}
	
	
	public static boolean directLotteryFailIsSend(Long projectId) {
		try {
			String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR + RedisConstant.DIRECT_LOTTERY_KEY_ACTIVITY_FAIL+ RedisConstant.REDIS_SEPERATOR+ projectId;
			if (RedisManager.isExit(key)) {
				return false;
			}
			String value = String.valueOf(projectId) + RedisConstant.REDIS_SEPERATOR + String.valueOf(DateUtils.getCurrentDate().getTime());
			Long retValue = RedisManager.setnx(key, value);
			if (retValue == 1l) {
				RedisManager.expire(key, ActivityConstant.activityKeyWeekExpire);
				logger.info(projectId+"锁定成功： key={}, value={}", key, value);
				return true;
			}
			return false;
		} catch (Exception e) {
			logger.error(projectId+"锁定失败：  projectId={}", projectId, e);
			return false;
		}
	}
	
	
	public static int getReceivedCouponTotal(Integer param) {
		int retInt = 0;
		String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR+ RedisConstant.ACTIVITY_DOUBLE_DAN_TOTAL_RED
				+RedisConstant.REDIS_SEPERATOR+DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+RedisConstant.REDIS_SEPERATOR+param;
		String retStr = RedisManager.get(key);
		if (StringUtil.isNotBlank(retStr)) {
			retInt = Integer.valueOf(retStr);
		}
		return retInt;
		
	}
	
	public static void incrTotalRedForDouble(Integer param) {
		String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR+ RedisConstant.ACTIVITY_DOUBLE_DAN_TOTAL_RED
				+RedisConstant.REDIS_SEPERATOR+DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+RedisConstant.REDIS_SEPERATOR+param;
		RedisManager.incrby(key, 1L);
	}
	
	

	public static int getReceivedCouponTotalForDayDrop() {
		int retInt = 0;
		String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR+ RedisConstant.ACTIVITY_DAY_DROPGOLD_TOTAL_RED
				+RedisConstant.REDIS_SEPERATOR+DateUtils.getDateStrFromDate(DateUtils.getCurrentDate());
		String retStr = RedisManager.get(key);
		if (StringUtil.isNotBlank(retStr)) {
			retInt = Integer.valueOf(retStr);
		}
		return retInt;
		
	}
	
	public static void incrTotalRedForDayDrop() {
		String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR+ RedisConstant.ACTIVITY_DAY_DROPGOLD_TOTAL_RED
				+RedisConstant.REDIS_SEPERATOR+DateUtils.getDateStrFromDate(DateUtils.getCurrentDate());
		RedisManager.incrby(key, 1L);
	}

}
