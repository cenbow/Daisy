package com.yourong.web.service.impl;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yourong.core.mc.model.biz.*;
import com.yourong.core.mc.model.query.ActivityLotteryResultQuery;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rop.thirdparty.com.google.common.collect.Maps;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.yourong.common.cache.RedisActivityClient;
import com.yourong.common.cache.RedisManager;
import com.yourong.common.cache.RedisPlatformClient;
import com.yourong.common.constant.ActivityConstant;
import com.yourong.common.constant.Constant;
import com.yourong.common.constant.RedisConstant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ActivityEnum;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.AES;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.LotteryUtil;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.common.util.RandomUtils;
import com.yourong.common.util.StringUtil;
import com.yourong.core.common.MessageClient;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.PopularityInOutLogManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.model.Project;
import com.yourong.core.ic.model.biz.ProjectForFront;
import com.yourong.core.lottery.container.LotteryContainer;
import com.yourong.core.lottery.draw.DrawByPrizeDirectly;
import com.yourong.core.lottery.draw.DrawByProbability;
import com.yourong.core.lottery.model.PopularityRedBag;
import com.yourong.core.lottery.model.RewardsBase;
import com.yourong.core.lottery.model.RewardsBodyForProbility;
import com.yourong.core.lottery.model.RuleBody;
import com.yourong.core.lottery.validation.impl.VerificationByParticipate;
import com.yourong.core.mc.dao.ActivityGroupMapper;
import com.yourong.core.mc.dao.ActivityLotteryResultNewMapper;
import com.yourong.core.mc.dao.ActivityMessageMapper;
import com.yourong.core.mc.manager.ActivityAfterTransactionManager;
import com.yourong.core.mc.manager.ActivityGroupManager;
import com.yourong.core.mc.manager.ActivityHistoryManager;
import com.yourong.core.mc.manager.ActivityLotteryManager;
import com.yourong.core.mc.manager.ActivityLotteryResultManager;
import com.yourong.core.mc.manager.ActivityManager;
import com.yourong.core.mc.manager.ActivityMessageManager;
import com.yourong.core.mc.manager.ActivityRuleManager;
import com.yourong.core.mc.manager.ChannelDataManager;
import com.yourong.core.mc.manager.CouponManager;
import com.yourong.core.mc.manager.CouponTemplateManager;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.mc.model.ActivityData;
import com.yourong.core.mc.model.ActivityGroup;
import com.yourong.core.mc.model.ActivityLottery;
import com.yourong.core.mc.model.ActivityLotteryResult;
import com.yourong.core.mc.model.ActivityLotteryResultNew;
import com.yourong.core.mc.model.ActivityMessage;
import com.yourong.core.mc.model.Coupon;
import com.yourong.core.mc.model.CouponTemplate;
import com.yourong.core.mc.model.PdGeneralMonth;
import com.yourong.core.mc.model.PdRegionMonth;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.tc.model.biz.TransactionForFirstInvestAct;
import com.yourong.core.tc.model.query.TransactionQuery;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.Member;
import com.yourong.web.dto.MemberSessionDto;
import com.yourong.web.service.ActivityLotteryService;
import com.yourong.web.service.BalanceService;
import com.yourong.web.service.TransactionService;
import com.yourong.web.utils.PaltformCapitalUtils;
import com.yourong.web.utils.ServletUtil;
import com.yourong.web.utils.SysServiceUtils;
import com.yourong.web.utils.WebPropertiesUtil;

@Service
public class ActivityLotteryServiceImpl implements ActivityLotteryService {

	private static final Logger logger = LoggerFactory.getLogger(ActivityLotteryServiceImpl.class);

	@Autowired
	private ActivityLotteryManager activityLotteryManager;
	
	@Autowired
	private ActivityLotteryResultNewMapper activityLotteryResultNewMapper;

	@Autowired
	private PopularityInOutLogManager popularityInOutLogManager;

	@Autowired
	private TransactionManager transactionManager;

	@Autowired
	private ProjectManager projectManager;

	@Autowired
	private ActivityManager activityManager;

	@Autowired
	private ActivityHistoryManager activityHistoryManager;

	@Autowired
	private DrawByPrizeDirectly drawByPrizeDirectly;

	@Autowired
	private DrawByProbability drawByProbability;

	@Autowired
	private ActivityRuleManager activityRuleManager;

	@Autowired
	private VerificationByParticipate verificationByParticipate;

	@Autowired
	private BalanceService balanceService;
	
	@Autowired
	private ActivityLotteryResultManager activityLotteryResultManager;

	@Autowired
	private CouponManager couponManager;

	@Autowired
	private ActivityMessageManager activityMessageManager;

	@Autowired
	private ActivityAfterTransactionManager activityAfterTransactionManager;

	@Autowired
	private BalanceManager balanceManager;

	@Autowired
	private MemberManager memberManager;

	@Autowired
	private CouponTemplateManager couponTemplateManager;
	
	@Autowired
	private ActivityGroupManager activityGroupManager;
	
	@Autowired
	private ActivityLotteryManager activityLotterManager;
	
	@Autowired
	private ActivityGroupMapper activityGroupMapper;
	
	@Autowired
	private ActivityMessageMapper activityMessageMapper;
	 
	@Autowired
	private ChannelDataManager channelDataManager;
	
	@Autowired
	private TransactionService transactionService;


	@Override
	public Long getActivityIdByPropertiesName(String activityName) {
		return Long.parseLong(PropertiesUtil.getProperties(activityName));
	}

	@Override
	public ActivityLotteryBiz yiRoadDrawLottery(Long memberId) {
		Long activityId = null;
		try {
			activityId = getActivityIdByPropertiesName("activity.yiLotteryId");
			ActivityLotteryBiz biz = activityLotteryManager.drawLotteryByProbability(activityId, memberId);
			// 清空缓存
			String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR + RedisConstant.REDIS_KEY_ACTIVITY_YIROAD
					+ RedisConstant.REDIS_SEPERATOR + RedisConstant.REDIS_KEY_ACTIVITY_YIROAD_LOTTERYLIST;
			RedisManager.removeObject(key);
			return biz;
		} catch (Exception e) {
			logger.error("亿路上有你活动增加用户抽奖次数失败, activityId={}, memberId={}", activityId, memberId, e);
			ActivityLotteryBiz biz = new ActivityLotteryBiz();
			biz.setRewardCode("error");
			return biz;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ActivityLotteryResultBiz> yiRoadNewLotteryResult() {
		Long activityId = null;
		try {
			String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR + RedisConstant.REDIS_KEY_ACTIVITY_YIROAD
					+ RedisConstant.REDIS_SEPERATOR + RedisConstant.REDIS_KEY_ACTIVITY_YIROAD_LOTTERYLIST;
			boolean isExit = RedisManager.isExitByObjectKey(key);
			List<ActivityLotteryResultBiz> list = null;
			if (isExit) {
				list = (List<ActivityLotteryResultBiz>) RedisManager.getObject(key);
			} else {
				activityId = getActivityIdByPropertiesName("activity.yiLotteryId");
				List<ActivityLotteryResult> modelList = activityLotteryManager.queryNewLotteryResult(activityId, null, 30);
				list = BeanCopyUtil.mapList(modelList, ActivityLotteryResultBiz.class);
				if (CollectionUtils.isNotEmpty(list)) {
					for (ActivityLotteryResultBiz model : list) {
						model.setMemberName(ServletUtil.getMemberUserName(model.getMemberId()));
						model.setAvatar(ServletUtil.getMemberAvatarById(model.getMemberId()));
						model.setMemberId(null);
						// 计算抽奖时间差
						int time = DateUtils.getTimeIntervalMins(model.getCreateTime(), DateUtils.getCurrentDate());
						if (time <= 1) {
							model.setDrawInterval("1分钟");
						} else if (time <= 60) {
							model.setDrawInterval(time + "分钟");
						} else {
							model.setDrawInterval((int) (time / 60) + "小时");
						}
					}
					RedisManager.putObject(key, list);
					RedisManager.expireObject(key, 120);
				}
			}
			return list;
		} catch (Exception e) {
			logger.error("查询亿路上有你活动最新中奖结果失败, activityId={}", activityId, e);
			return null;
		}
	}

	@Override
	public int yiRoadNewLotteryCount(Long memberId) {
		Long activityId = null;
		try {
			activityId = getActivityIdByPropertiesName("activity.yiLotteryId");
			return activityLotteryManager.getMemberRealLotteryNumber(activityId, memberId, null);
		} catch (Exception e) {
			logger.error("查询亿路上有你活动剩余抽奖次数失败, memberId={}", memberId, e);
			return 0;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public ActivityForRankListBiz yiRoadRankList() {
		try {
			Date startTime = DateUtils.getDateFromString(PropertiesUtil.getProperties("activity.yiRankList.startTime"),
					DateUtils.TIME_PATTERN);
			Date endTime = DateUtils.getDateFromString(PropertiesUtil.getProperties("activity.yiRankList.endTime"), DateUtils.TIME_PATTERN);
			String now = DateUtils.getDateStrFromDate(DateUtils.getCurrentDate());
			String nowTime = now + " 16:59:59";
			String yesterday = null;
			String yesterdayTimeStart = null;
			String yesterdayTimeEnd = null;
			String beforeYesterday = null;
			String beforeYesterdayTime = null;
			if (DateUtils.getCurrentDate().getTime() <= DateUtils.getDateFromString(nowTime, DateUtils.TIME_PATTERN).getTime()) {
				yesterday = DateUtils.getDateStrFromDate(DateUtils.getYesterDay());
				yesterdayTimeStart = yesterday + " 17:00:00";
				yesterdayTimeEnd = yesterday + " 16:59:59";
				beforeYesterday = DateUtils.getDateStrFromDate(DateUtils.addDate(DateUtils.getDateFromString(yesterdayTimeStart), -1));
				beforeYesterdayTime = beforeYesterday + " 17:00:00";
			} else {
				yesterday = DateUtils.getDateStrFromDate(DateUtils.getCurrentDate());
				yesterdayTimeStart = yesterday + " 17:00:00";
				yesterdayTimeEnd = yesterday + " 16:59:59";
				nowTime = DateUtils.getDateStrFromDate(DateUtils.addDate(DateUtils.getCurrentDate(), 1)) + " 16:59:59";
				beforeYesterday = DateUtils.getDateStrFromDate(DateUtils.addDate(DateUtils.getDateFromString(yesterdayTimeStart), -1));
				beforeYesterdayTime = beforeYesterday + " 17:00:00";
			}
			logger.debug("yesterdayTimeStart={}, nowTime={}, beforeYesterdayTime={}, yesterdayTimeEnd={} ", yesterdayTimeStart, nowTime,
					beforeYesterdayTime, yesterdayTimeEnd);
			List<ActivityForRankListBiz> yesterdayList = null;
			String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR + RedisConstant.REDIS_KEY_ACTIVITY_YIROAD
					+ RedisConstant.REDIS_SEPERATOR + RedisConstant.REDIS_KEY_ACTIVITY_YIROAD_RANKING;
			boolean isExit = RedisManager.isExitByObjectKey(key);
			if (isExit) {
				yesterdayList = (List<ActivityForRankListBiz>) RedisManager.getObject(key);
			} else {
				Date beforeYester = DateUtils.getDateFromString(beforeYesterdayTime, DateUtils.TIME_PATTERN);
				if (DateUtils.isDateBetween(beforeYester, startTime, endTime)) {
					yesterdayList = getMemberMeetTotalInvestRange(beforeYesterdayTime, yesterdayTimeEnd, 10, "asc");
					// 计算秒数
					int timeIntervalSencond = DateUtils.getTimeIntervalSencond(DateUtils.getCurrentDate(),
							DateUtils.getDateFromString(nowTime, DateUtils.TIME_PATTERN));
					RedisManager.putObject(key, yesterdayList);
					RedisManager.expireObject(key, timeIntervalSencond);
				}
			}
			List<ActivityForRankListBiz> todayList = null;
			if (DateUtils.isDateBetween(DateUtils.getCurrentDate(), startTime, endTime)) {
				todayList = getMemberMeetTotalInvestRange(yesterdayTimeStart, nowTime, 10, "asc");
			}
			ActivityForRankListBiz activityLotteryBiz = new ActivityForRankListBiz();
			if (CollectionUtils.isNotEmpty(yesterdayList)) {
				activityLotteryBiz.setLastList(yesterdayList);
			}
			if (CollectionUtils.isNotEmpty(todayList)) {
				activityLotteryBiz.setThisList(todayList);
			}
			return activityLotteryBiz;
		} catch (Exception e) {
			logger.error("查询亿路上有你亿举夺魁排行榜失败!", e);
			return null;
		}
	}

	@Override
	public int getDownTotalCount() {
		try {
			return RedisPlatformClient.getAppDownLoadCount();
		} catch (Exception e) {
			logger.error("获取下载次数失败!", e);
			return 0;
		}
	}

	@Override
	public ActivityLotteryBiz yiRoadShare(Long memberId) {
		Long activityId = null;
		try {
			activityId = getActivityIdByPropertiesName("activity.yiShareId");
			activityLotteryManager.yiRoadAddShareNumber(activityId, memberId);
			return activityLotteryManager.drawLotteryByProbability(activityId, memberId);
		} catch (Exception e) {
			logger.error("获取下载次数失败!", e);
			ActivityLotteryBiz biz = new ActivityLotteryBiz();
			biz.setRewardCode("error");
			return biz;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ActivityLotteryResultBiz> yiRoadShareList() {
		Long activityId = null;
		try {
			List<ActivityLotteryResultBiz> list = null;
			String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR + RedisConstant.REDIS_KEY_ACTIVITY_YIROAD
					+ RedisConstant.REDIS_SEPERATOR + RedisConstant.REDIS_KEY_ACTIVITY_YIROAD_SHARELIST;
			boolean isExit = RedisManager.isExitByObjectKey(key);
			if (isExit) {
				list = (List<ActivityLotteryResultBiz>) RedisManager.getObject(key);
			} else {
				activityId = getActivityIdByPropertiesName("activity.yiShareId");
				List<ActivityLotteryResult> modelList = activityLotteryManager.queryNewLotteryResult(activityId, null, 30);
				list = BeanCopyUtil.mapList(modelList, ActivityLotteryResultBiz.class);
				if (CollectionUtils.isNotEmpty(list)) {
					for (ActivityLotteryResultBiz model : list) {
						model.setMemberName(ServletUtil.getMemberUserName(model.getMemberId()));
						model.setAvatar(ServletUtil.getMemberAvatarById(model.getMemberId()));
						model.setMemberId(null);
						// 计算抽奖时间差
						int time = DateUtils.getTimeIntervalMins(model.getCreateTime(), DateUtils.getCurrentDate());
						if (time <= 1) {
							model.setDrawInterval("1分钟");
						} else if (time <= 60) {
							model.setDrawInterval(time + "分钟");
						} else {
							model.setDrawInterval((int) (time / 60) + "小时");
						}
					}
					RedisManager.putObject(key, list);
					RedisManager.expireObject(key, 120);
				}
			}
			return list;
		} catch (Exception e) {
			logger.error("获取亿路上有你分享滚屏失败!", e);
			return null;
		}
	}

	@Override
	public int showYiRoadShareFlag(Long memberId) {
		Long activityId = null;
		try {
			activityId = getActivityIdByPropertiesName("activity.yiShareId");
			return activityLotteryManager.showYiRoadShareFlag(activityId, memberId);
		} catch (Exception e) {
			logger.error("查询亿路上有你活动剩余抽奖次数失败, memberId={}", memberId, e);
			return 0;
		}
	}

	@Override
	public Activity getActivityId(Long id) {
		try {
			Activity activity = activityManager.selectByPrimaryKey(id);
			return activity;
		} catch (Exception e) {
			logger.error("查询活动失败, ID={}", id, e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public ActivityForRankListBiz weeklyRank() {
		try {
			ActivityForRankListBiz aBiz = new ActivityForRankListBiz();
			if (DateUtils.isDateBetween(DateUtils.getCurrentDate(),
					DateUtils.getDateFromString(WebPropertiesUtil.getProperties("activity.monthlyStartTime"), DateUtils.TIME_PATTERN),
					DateUtils.getDateFromString(WebPropertiesUtil.getProperties("activity.monthlyEndTime"), DateUtils.TIME_PATTERN))) {
				String[] rankRange1 = new String[] { WebPropertiesUtil.getProperties("activity.monthly.weeklyRank.round1.startTime"),
						WebPropertiesUtil.getProperties("activity.monthly.weeklyRank.round1.endTime") };
				String[] rankRange2 = new String[] { WebPropertiesUtil.getProperties("activity.monthly.weeklyRank.round2.startTime"),
						WebPropertiesUtil.getProperties("activity.monthly.weeklyRank.round2.endTime") };
				String[] rankRange3 = new String[] { WebPropertiesUtil.getProperties("activity.monthly.weeklyRank.round3.startTime"),
						WebPropertiesUtil.getProperties("activity.monthly.weeklyRank.round3.endTime") };
				String[] rankRange4 = new String[] { WebPropertiesUtil.getProperties("activity.monthly.weeklyRank.round4.startTime"),
						WebPropertiesUtil.getProperties("activity.monthly.weeklyRank.round4.endTime") };
				List<String[]> timeList = new ArrayList<String[]>();
				timeList.add(rankRange1);
				timeList.add(rankRange2);
				timeList.add(rankRange3);
				timeList.add(rankRange4);
				int historyTopperIndex = 0;
				Date nowTime = DateUtils.getCurrentDate();
				List<ActivityForRankListBiz> thisList = null;
				List<ActivityForRankListBiz> lastList = null;
				List<ActivityForRankListBiz> topperList = null;
				String lastListKey = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR
						+ RedisConstant.REDIS_KEY_ACTIVITY_MONTHLYRANK + RedisConstant.REDIS_SEPERATOR
						+ RedisConstant.REDIS_KEY_ACTIVITY_MONTHLYRANK_WEEKLYLIST;
				String topperKey = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR
						+ RedisConstant.REDIS_KEY_ACTIVITY_MONTHLYRANK + RedisConstant.REDIS_SEPERATOR
						+ RedisConstant.REDIS_KEY_ACTIVITY_MONTHLYRANK_WEEKLYLIST + RedisConstant.REDIS_SEPERATOR
						+ RedisConstant.REDIS_KEY_ACTIVITY_MONTHLYRANK_WEEKLYLIST_TOPPER;
				if (nowTime.before(DateUtils.getDateFromString(rankRange1[0], DateUtils.TIME_PATTERN))) {
					// 第一轮之前
					logger.debug("奇偶排行，当前查询位于第一轮之前");
					aBiz.setStartDay(DateUtils.getDateFromString(rankRange1[0], DateUtils.TIME_PATTERN));
				} else if (DateUtils.isDateBetween(nowTime, DateUtils.getDateFromString(rankRange1[0], DateUtils.TIME_PATTERN),
						DateUtils.getDateFromString(rankRange1[1], DateUtils.TIME_PATTERN))) {
					thisList = getMemberMeetTotalInvestRange(rankRange1[0], rankRange1[1], 10, "asc");
					aBiz.setStartDay(DateUtils.getDateFromString(rankRange1[0], DateUtils.TIME_PATTERN));
					aBiz.setEndDay(DateUtils.getDateFromString(rankRange1[1], DateUtils.TIME_PATTERN));
				} else if (nowTime.before(DateUtils.getDateFromString(rankRange2[0], DateUtils.TIME_PATTERN))) {
					// 在第二轮之前，生成上轮战况
					boolean isExitLastList = RedisManager.isExitByObjectKey(lastListKey);
					if (isExitLastList) {
						logger.debug("奇偶排行，当前查询位于第二轮之前，生成上轮战况（缓存）");
						lastList = (List<ActivityForRankListBiz>) RedisManager.getObject(lastListKey);
					} else {
						logger.debug("奇偶排行，当前查询位于第二轮之前，生成上轮战况（非缓存）");
						lastList = getMemberMeetTotalInvestRange(rankRange1[0], rankRange1[1], 10, "asc");
						if (CollectionUtils.isNotEmpty(lastList)) {
							putRedisObj(lastListKey, lastList, DateUtils.getDateFromString(rankRange2[1], DateUtils.TIME_PATTERN));
						}
					}
					historyTopperIndex = 1;
					aBiz.setStartDay(DateUtils.getDateFromString(rankRange2[0], DateUtils.TIME_PATTERN));
				} else if (DateUtils.isDateBetween(nowTime, DateUtils.getDateFromString(rankRange2[0], DateUtils.TIME_PATTERN),
						DateUtils.getDateFromString(rankRange2[1], DateUtils.TIME_PATTERN))) {
					// 在第二轮之间，实时查询本轮战况,生成上轮战况
					thisList = getMemberMeetTotalInvestRange(rankRange2[0], rankRange2[1], 10, "asc");
					boolean isExitLastList = RedisManager.isExitByObjectKey(lastListKey);
					if (isExitLastList) {
						logger.debug("奇偶排行，在第二轮之间，实时查询本轮战况,生成上轮战况（缓存）");
						lastList = (List<ActivityForRankListBiz>) RedisManager.getObject(lastListKey);
					} else {
						logger.debug("奇偶排行，在第二轮之间，实时查询本轮战况,生成上轮战况（非缓存）");
						lastList = getMemberMeetTotalInvestRange(rankRange1[0], rankRange1[1], 10, "asc");
						if (CollectionUtils.isNotEmpty(lastList)) {
							putRedisObj(lastListKey, lastList, DateUtils.getDateFromString(rankRange2[1], DateUtils.TIME_PATTERN));
						}
					}
					historyTopperIndex = 1;
					aBiz.setStartDay(DateUtils.getDateFromString(rankRange2[0], DateUtils.TIME_PATTERN));
					aBiz.setEndDay(DateUtils.getDateFromString(rankRange2[1], DateUtils.TIME_PATTERN));
				} else if (nowTime.before(DateUtils.getDateFromString(rankRange3[0], DateUtils.TIME_PATTERN))) {
					// 在第三轮之前，生成上轮战况
					boolean isExitLastList = RedisManager.isExitByObjectKey(lastListKey);
					if (isExitLastList) {
						logger.debug("奇偶排行，在第三轮之前，生成上轮战况（缓存）");
						lastList = (List<ActivityForRankListBiz>) RedisManager.getObject(lastListKey);
					} else {
						logger.debug("奇偶排行，在第三轮之前，生成上轮战况（非缓存）");
						lastList = getMemberMeetTotalInvestRange(rankRange2[0], rankRange2[1], 10, "asc");
						if (CollectionUtils.isNotEmpty(lastList)) {
							putRedisObj(lastListKey, lastList, DateUtils.getDateFromString(rankRange3[1], DateUtils.TIME_PATTERN));
						}
					}
					historyTopperIndex = 2;
					aBiz.setStartDay(DateUtils.getDateFromString(rankRange3[0], DateUtils.TIME_PATTERN));
				} else if (DateUtils.isDateBetween(nowTime, DateUtils.getDateFromString(rankRange3[0], DateUtils.TIME_PATTERN),
						DateUtils.getDateFromString(rankRange3[1], DateUtils.TIME_PATTERN))) {
					// 在第三轮之间，实时查询本轮战况,生成上轮战况
					thisList = getMemberMeetTotalInvestRange(rankRange3[0], rankRange3[1], 10, "asc");
					boolean isExitLastList = RedisManager.isExitByObjectKey(lastListKey);
					if (isExitLastList) {
						logger.debug("奇偶排行，在第三轮之间，实时查询本轮战况,生成上轮战况（缓存）");
						lastList = (List<ActivityForRankListBiz>) RedisManager.getObject(lastListKey);
					} else {
						logger.debug("奇偶排行，在第三轮之间，实时查询本轮战况,生成上轮战况（非缓存）");
						lastList = getMemberMeetTotalInvestRange(rankRange2[0], rankRange2[1], 10, "asc");
						if (CollectionUtils.isNotEmpty(lastList)) {
							putRedisObj(lastListKey, lastList, DateUtils.getDateFromString(rankRange3[1], DateUtils.TIME_PATTERN));
						}
					}
					historyTopperIndex = 2;
					aBiz.setStartDay(DateUtils.getDateFromString(rankRange3[0], DateUtils.TIME_PATTERN));
					aBiz.setEndDay(DateUtils.getDateFromString(rankRange3[1], DateUtils.TIME_PATTERN));
				} else if (nowTime.before(DateUtils.getDateFromString(rankRange4[0], DateUtils.TIME_PATTERN))) {
					// 在第四轮之前，生成上轮战况
					boolean isExitLastList = RedisManager.isExitByObjectKey(lastListKey);
					if (isExitLastList) {
						logger.debug("奇偶排行，在第四轮之前，生成上轮战况（缓存）");
						lastList = (List<ActivityForRankListBiz>) RedisManager.getObject(lastListKey);
					} else {
						logger.debug("奇偶排行，在第四轮之前，生成上轮战况（非缓存）");
						lastList = getMemberMeetTotalInvestRange(rankRange3[0], rankRange3[1], 10, "asc");
						if (CollectionUtils.isNotEmpty(lastList)) {
							putRedisObj(lastListKey, lastList, DateUtils.getDateFromString(rankRange4[1], DateUtils.TIME_PATTERN));
						}
					}
					historyTopperIndex = 3;
					aBiz.setStartDay(DateUtils.getDateFromString(rankRange4[0], DateUtils.TIME_PATTERN));
				} else if (DateUtils.isDateBetween(nowTime, DateUtils.getDateFromString(rankRange4[0], DateUtils.TIME_PATTERN),
						DateUtils.getDateFromString(rankRange4[1], DateUtils.TIME_PATTERN))) {
					// 在第四轮之间，实时查询本轮战况,生成上轮战况
					thisList = getMemberMeetTotalInvestRange(rankRange4[0], rankRange4[1], 10, "asc");
					boolean isExitLastList = RedisManager.isExitByObjectKey(lastListKey);
					if (isExitLastList) {
						logger.debug("奇偶排行，在第四轮之间，实时查询本轮战况,生成上轮战况（缓存）");
						lastList = (List<ActivityForRankListBiz>) RedisManager.getObject(lastListKey);
					} else {
						logger.debug("奇偶排行，在第四轮之间，实时查询本轮战况,生成上轮战况（非缓存）");
						lastList = getMemberMeetTotalInvestRange(rankRange3[0], rankRange3[1], 10, "asc");
						if (CollectionUtils.isNotEmpty(lastList)) {
							// 计算秒数
							putRedisObj(lastListKey, lastList, DateUtils.getDateFromString(rankRange4[1], DateUtils.TIME_PATTERN));
						}
					}
					historyTopperIndex = 3;
					aBiz.setStartDay(DateUtils.getDateFromString(rankRange4[0], DateUtils.TIME_PATTERN));
					aBiz.setEndDay(DateUtils.getDateFromString(rankRange4[1], DateUtils.TIME_PATTERN));
				} else {
					// 在第四轮之后
					boolean isExitLastList = RedisManager.isExitByObjectKey(lastListKey);
					if (isExitLastList) {
						logger.debug("奇偶排行，在第四轮之后（缓存）");
						lastList = (List<ActivityForRankListBiz>) RedisManager.getObject(lastListKey);
					} else {
						logger.debug("奇偶排行，在第四轮之后（非缓存）");
						lastList = getMemberMeetTotalInvestRange(rankRange4[0], rankRange4[1], 10, "asc");
						if (CollectionUtils.isNotEmpty(lastList)) {
							putRedisObj(lastListKey, lastList, DateUtils.getDateFromString(
									WebPropertiesUtil.getProperties("activity.monthlyEndTime"), DateUtils.TIME_PATTERN));
						}
					}
					historyTopperIndex = 4;
				}
				if (CollectionUtils.isNotEmpty(thisList))
					aBiz.setThisList(thisList);
				if (CollectionUtils.isNotEmpty(lastList))
					aBiz.setLastList(lastList);
				// 查询四轮top1用户及投资额
				boolean isExitTopper = RedisManager.isExitByObjectKey(topperKey);
				if (isExitTopper) {
					topperList = (List<ActivityForRankListBiz>) RedisManager.getObject(topperKey);
				}
				if (historyTopperIndex > 0 && (CollectionUtils.isEmpty(topperList) || topperList.size() != historyTopperIndex)) {
					topperList = new ArrayList<ActivityForRankListBiz>();
					for (int i = 0; i < historyTopperIndex; i++) {
						List<ActivityForRankListBiz> list = getMemberMeetTotalInvestRange(timeList.get(i)[0], timeList.get(i)[1], 1, "asc");
						if (CollectionUtils.isNotEmpty(list)) {
							topperList.add(list.get(0));
						} else {
							topperList.add(null);
						}
					}
					if (CollectionUtils.isNotEmpty(topperList))
						putRedisObj(topperKey, topperList, DateUtils.getDateFromString(
								WebPropertiesUtil.getProperties("activity.monthlyEndTime"), DateUtils.TIME_PATTERN));
				}
				if (CollectionUtils.isNotEmpty(topperList))
					aBiz.setTopperList(topperList);
			}
			return aBiz;
		} catch (Exception e) {
			logger.error("查询月度活动奇偶排行失败！", e);
			return null;
		}
	}

	/**
	 * 获得某段时间内的投资排行榜
	 * 
	 * @param startTime
	 * @param endTime
	 * @param rowLimit
	 * @param transactionOrder
	 * @return
	 * @throws ManagerException
	 */
	private List<ActivityForRankListBiz> getMemberMeetTotalInvestRange(String startTime, String endTime, int rowLimit,
			String transactionOrder) throws ManagerException {
		try {
			Map<String, Object> pareMap = new HashMap<String, Object>();
			pareMap.put("limitNum", rowLimit);
			pareMap.put("startTime", startTime);
			pareMap.put("endTime", endTime);
			pareMap.put("transactionOrder", transactionOrder);
			List<TransactionForFirstInvestAct> list = transactionManager.getMemberMeetTotalInvestRange(pareMap);
			if (CollectionUtils.isNotEmpty(list)) {
				List<ActivityForRankListBiz> thisList = new ArrayList<ActivityForRankListBiz>();
				ActivityForRankListBiz innerBiz = new ActivityForRankListBiz();
				for (TransactionForFirstInvestAct act : list) {
					innerBiz = new ActivityForRankListBiz();
					innerBiz.setLastUsername(ServletUtil.getMemberUserName(act.getMemberId()));
					innerBiz.setAvatar(ServletUtil.getMemberAvatarById(act.getMemberId()));
					innerBiz.setLastTotalInvest(act.getTotalInvestFormat());
					thisList.add(innerBiz);
				}
				return thisList;
			}
			return null;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 查询结果加入缓存
	 * 
	 * @param key
	 * @param redisObj
	 * @param invalidDateTime
	 */
	private void putRedisObj(String key, Object redisObj, Date invalidDateTime) {
		// 计算秒数
		int timeIntervalSencond = DateUtils.getTimeIntervalSencond(DateUtils.getCurrentDate(), invalidDateTime);
		RedisManager.putObject(key, redisObj);
		RedisManager.expireObject(key, timeIntervalSencond);
	}

	@Override
	public ResultDO<ActivityForAnniversary> anniversaryPrizeInit(Long memberId) {
		StopWatch watch = new StopWatch();
		watch.start();
		ResultDO<ActivityForAnniversary> rDO = new ResultDO<ActivityForAnniversary>();
		try {
			String activityIdStr = PropertiesUtil.getProperties("activity.anniversary.id");
			Long actId = Long.parseLong(activityIdStr);
			ActivityForAnniversary afa = new ActivityForAnniversary();
			ImmutableList<Activity> actList = activityManager.selectByParentId(actId, TypeEnum.ACTIVITY_CHILD_GROUP_1.getType());
			// 活动未开始
			if (actList.get(0).getStartTime().after(DateUtils.getCurrentDate())) {
				afa.setStartTime(actList.get(0).getStartTime());
				afa.setEndTime(actList.get(0).getEndTime());
				afa.setDocument("敬请期待");
				afa.setStatus(2);
				afa.setActivityIndex(0);
				rDO.setResult(afa);
			} else if (actList.get(actList.size() - 1).getEndTime().before(DateUtils.getCurrentDate())) {
				// 活动已结束
				afa.setDocument("奖励大放送已结束");
				afa.setBtnStr("已结束");
				afa.setStatus(6);
				rDO.setResult(afa);
			}
			if (rDO.getResult() != null) {
				return rDO;
			}
			// 活动中
			String todayMD = DateUtils.getStrFromDate(DateUtils.getCurrentDate(), "MMdd");
			// 当天大放送活动名称
			String activeActName = "【干杯！我们的纪念日】奖励大放送" + todayMD;
			Activity currentAct = null;
			Integer currentIndex = null;
			for (Activity act : actList) {
				if (activeActName.equals(act.getActivityName())) {
					currentAct = act;
					currentIndex = actList.indexOf(act);
					break;
				}
			}
			if (currentAct != null && currentIndex != null) {
				int statusInt = currentAct.getActivityStatus().intValue();
				// 比较系统时间和领券时间
				if (statusInt == 2) {
					// 未到领券时间
					afa.setDocument("敬请期待");
					afa.setStatus(2);
					afa.setActivityIndex(currentIndex);
					rDO.setResult(afa);
				} else if (statusInt == 4) {
					// 活动状态已经切换到进行中
					ActivityEnum actEnum = ActivityEnum.getMap().get(10000 + currentIndex);
					if (memberId != null) {
						afa.setParticipate(isParticipateInActivity(memberId, currentAct.getId()));
					}
					afa.setDocument(actEnum.getDocument());
					afa.setBtnStr(actEnum.getBtnStr());
					afa.setStatus(4);
					afa.setActivityIndex(currentIndex);
					rDO.setResult(afa);
				} else if (statusInt == 6) {
					// 当天领券已过
					try {
						currentAct = actList.get(currentIndex + 1);
					} catch (Exception e) {
						logger.error("获取次日活动失败!", e);
						throw e;
					}
					afa.setDocument("敬请期待");
					afa.setStatus(2);
					afa.setActivityIndex(currentIndex + 1);
					rDO.setResult(afa);
				}
				afa.setActivityId(currentAct.getId());
				afa.setStartTime(currentAct.getStartTime());
				afa.setEndTime(currentAct.getEndTime());
				rDO.setResult(afa);
				return rDO;
			} else {
				throw new Exception("未找到进行中的【周年庆-奖励大放送】活动记录");
			}
		} catch (Exception e) {
			logger.error("周年庆-奖励大放送初始化失败!", e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
			return rDO;
		} finally {
			watch.stop();
			logger.info("周年庆-奖励大放送初始化耗时= {} 毫秒", watch.getTime());
		}
	}

	@Override
	public ResultDO<ActivityForAnniversary> anniversaryReceivePrize(Long memberId, Long activityId) {
		ResultDO<ActivityForAnniversary> rDO = new ResultDO<ActivityForAnniversary>();
		try {
			if (activityId.equals(0L)) {
				rDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
				return rDO;
			}
			ActivityForAnniversary afa = new ActivityForAnniversary();
			Optional<Activity> retOpt = LotteryContainer.getInstance().getActivity(activityId);
			if (retOpt.isPresent()) {
				Activity act = retOpt.get();
				if (DateUtils.isDateBetween(DateUtils.getCurrentDate(), act.getStartTime(), act.getEndTime())) {
					RuleBody ruleBody = new RuleBody();
					ruleBody.setActivityId(activityId);
					ruleBody.setMemberId(memberId);
					ruleBody.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
					ruleBody.setCycleStr(activityId.toString());
					ruleBody.setActivityName("【干杯！我们的纪念日】奖励大放送");
					if (drawByPrizeDirectly.validate(ruleBody, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
						Optional<List<Object>> retList = LotteryContainer.getInstance().getRewardsListMap(activityId, RewardsBase.class);
						if (retList.isPresent()) {
							RewardsBase rewardsBase = (RewardsBase) (retList.get().get(0));
							drawByPrizeDirectly
									.drawLottery(ruleBody, rewardsBase, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode());
							// 放入缓存
							RedisActivityClient.setActivitiesMember(activityId, memberId);
							// 发送站内信
							MessageClient.sendMsgForSPEngin(memberId, ruleBody.getActivityName(), rewardsBase.getRewardName());
							afa.setRewardCode(rewardsBase.getRewardCode());
							afa.setRewardName(rewardsBase.getRewardName());
						} else {
							rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
							return rDO;
						}
					} else {
						afa.setParticipate(true);
					}
				} else {
					rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
					return rDO;
				}
			} else {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			rDO.setResult(afa);
			return rDO;
		} catch (Exception e) {
			logger.error("周年庆-奖励大放送领取失败!", e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
			if (e.getClass().equals(ManagerException.class)) {
				String errorMsg = e.getMessage();
				if (errorMsg.startsWith("重复参加活动")) {
					rDO.getResultCode().setMsg(errorMsg);
				}
			}
			return rDO;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultDO<ActivityForRankListBiz> anniversaryOneHourList() {
		ResultDO<ActivityForRankListBiz> rDO = new ResultDO<ActivityForRankListBiz>();
		try {
			ActivityForRankListBiz actBiz = new ActivityForRankListBiz();
			String activityIdStr = PropertiesUtil.getProperties("activity.anniversary.id");
			Long actId = Long.parseLong(activityIdStr);
			ImmutableList<Activity> actList = activityManager.selectByParentId(actId, 2);
			if (Collections3.isNotEmpty(actList)) {
				Activity act = actList.get(0);
				actBiz.setStartDay(act.getStartTime());
				actBiz.setEndDay(act.getEndTime());
				rDO.setResult(actBiz);
				if (DateUtils.isDateBetween(DateUtils.getCurrentDate(), act.getStartTime(), act.getEndTime())) {
					String startTime = DateUtils.getStrFromDate(act.getStartTime(), DateUtils.TIME_PATTERN);
					String endTime = DateUtils.getStrFromDate(act.getEndTime(), DateUtils.TIME_PATTERN);
					List<ActivityForRankListBiz> rankList = getMemberMeetTotalInvestRange(startTime, endTime, 10, "asc");
					rDO.setResultList(rankList);
					return rDO;
				} else if (DateUtils.getCurrentDate().after(act.getStartTime())) {
					String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR
							+ RedisConstant.REDIS_KEY_ACTIVITY_ANNIVERSARY + RedisConstant.REDIS_SEPERATOR
							+ RedisConstant.REDIS_KEY_ACTIVITY_ANNIVERSARY_1HOUR_LIST;
					boolean isExit = RedisManager.isExitByObjectKey(key);
					if (isExit) {
						rDO.setResultList((List<ActivityForRankListBiz>) RedisManager.getObject(key));
						return rDO;
					} else {
						String startTime = DateUtils.getStrFromDate(act.getStartTime(), DateUtils.TIME_PATTERN);
						String endTime = DateUtils.getStrFromDate(act.getEndTime(), DateUtils.TIME_PATTERN);
						List<ActivityForRankListBiz> rankList = getMemberMeetTotalInvestRange(startTime, endTime, 10, "asc");
						RedisManager.putObject(key, rankList);
						rDO.setResultList(rankList);
						return rDO;
					}
				}
			}
			return rDO;
		} catch (Exception e) {
			logger.error("周年庆-奖励大放送巅峰1小时获取列表失败!", e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
			return rDO;
		}
	}

	@Override
	public ResultDO<ActivityForAnniversary> anniversaryTwentyFiveGrid(Long memberId, Integer chip) {
		ResultDO<ActivityForAnniversary> rDO = new ResultDO<ActivityForAnniversary>();
		rDO.setSuccess(false);
		try {
			String activityIdStr = PropertiesUtil.getProperties("activity.anniversary.twentyFive.id");
			Long actId = Long.parseLong(activityIdStr);
			// 判断是否在活动期间内
			if (drawByProbability.isInActivityTime(actId)) {
				RuleBody rb = new RuleBody();
				rb.setActivityId(actId);
				rb.setMemberId(memberId);
				rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_POPULAR.getStatus());
				rb.setCycleStr(actId.toString());
				rb.setDeductValue(chip);
				rb.setDeductRemark("【干杯！我们的纪念日】幸运25宫格人气值下注");
				rb.setRewardsAvailableNum(3);
				rb.setRewardsPoolMaxNum(25);
				// 校验
				if (drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_POPULARITY.getCode())) {
					// 抽奖
					RewardsBodyForProbility rfp = (RewardsBodyForProbility) drawByProbability.drawLottery(rb, chip,
							TypeEnum.ACTIVITY_LOTTERY_VALIDATE_POPULARITY.getCode());
					ActivityForAnniversary afa = new ActivityForAnniversary();
					// 刷新人气值
					Balance balance = balanceService.queryBalance(memberId, TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY);
					if (balance != null) {
						afa.setPopularityVaule(balance.getAvailableBalance().intValue());
					}
					afa.setRewardCode(rfp.getRewardCode());
					rDO.setResult(afa);
					rDO.setSuccess(true);
					if (!"noReward".equals(rfp.getRewardCode())) {
						// 清空缓存
						String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR
								+ RedisConstant.REDIS_KEY_ACTIVITY_ANNIVERSARY + RedisConstant.REDIS_SEPERATOR
								+ RedisConstant.REDIS_KEY_ACTIVITY_ANNIVERSARY_25GRID_LIST;
						RedisManager.removeObject(key);
						// 发送站内信
						String value = null;
						if ("PopularityFor10times".equals(rfp.getRewardCode())) {
							value = new BigDecimal(chip).multiply(new BigDecimal(10)).toString();
						} else if ("PopularityFor5times".equals(rfp.getRewardCode())) {
							value = new BigDecimal(chip).multiply(new BigDecimal(5)).toString();
						} else if ("PopularityFor2times".equals(rfp.getRewardCode())) {
							value = new BigDecimal(chip).multiply(new BigDecimal(2)).toString();
						}
						MessageClient.sendMsgForSPEngin(memberId, "【干杯！我们的纪念日】幸运25宫格", value + "点人气值");
					}
					return rDO;
				} else {
					rDO.setResultCode(ResultCode.ACTIVITY_POPULARITY__ERROR);
					return rDO;
				}

			} else {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
				return rDO;
			}
		} catch (Exception e) {
			logger.error("周年庆-幸运25宫格错误, memberId={}, chip={}", memberId, chip, e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
			if (e.getClass().equals(ManagerException.class)) {
				String errorMsg = e.getMessage();
				if (errorMsg.startsWith("可用余额不够")) {
					rDO.getResultCode().setMsg(errorMsg);
				}
			}
		}
		return rDO;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultDO<ActivityLotteryResultBiz> anniversaryTwentyFiveGridResult() {
		ResultDO<ActivityLotteryResultBiz> rDO = new ResultDO<ActivityLotteryResultBiz>();
		rDO.setSuccess(false);
		Long activityId = null;
		try {
			String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR + RedisConstant.REDIS_KEY_ACTIVITY_ANNIVERSARY
					+ RedisConstant.REDIS_SEPERATOR + RedisConstant.REDIS_KEY_ACTIVITY_ANNIVERSARY_25GRID_LIST;
			boolean isExit = RedisManager.isExitByObjectKey(key);
			List<ActivityLotteryResultBiz> list = null;
			if (isExit) {
				list = (List<ActivityLotteryResultBiz>) RedisManager.getObject(key);
			} else {
				String activityIdStr = PropertiesUtil.getProperties("activity.anniversary.twentyFive.id");
				activityId = Long.parseLong(activityIdStr);
				List<ActivityLotteryResult> modelList = activityLotteryManager.queryNewLotteryResult(activityId, null, 30);
				list = BeanCopyUtil.mapList(modelList, ActivityLotteryResultBiz.class);
				if (CollectionUtils.isNotEmpty(list)) {
					for (ActivityLotteryResultBiz model : list) {
						model.setMemberName(ServletUtil.getMemberUserName(model.getMemberId()));
						model.setAvatar(ServletUtil.getMemberAvatarById(model.getMemberId()));
						model.setMemberId(null);
					}
					RedisManager.putObject(key, list);
					RedisManager.expireObject(key, 120);
				}
			}
			rDO.setResultList(list);
			rDO.setSuccess(true);
		} catch (Exception e) {
			logger.error("周年庆-幸运二十五宫格中奖结果查询失败, activityId={}", activityId, e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}

	private boolean isParticipateInActivity(Long memberId, Long activityId) throws Exception {
		boolean flag = RedisActivityClient.isParticipateInActivity(activityId, memberId);
		if (!flag) {
			RuleBody rb = new RuleBody();
			rb.setActivityId(activityId);
			rb.setMemberId(memberId);
			rb.setCycleStr(activityId.toString());
			if (!verificationByParticipate.validate(rb)) {
				RedisActivityClient.setActivitiesMember(activityId, memberId);
				flag = true;
			}
		}
		return flag;
	}

	@Override
	public ResultDO<ActivityForAnniversary> anniversaryShareUrl(Long transactionId, Long memberId) {
		ResultDO<ActivityForAnniversary> rDO = new ResultDO<ActivityForAnniversary>();
		rDO.setSuccess(false);
		if (transactionId == null || transactionId.longValue() == 0L) {
			rDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
			return rDO;
		}
		try {
			String activityIdStr = PropertiesUtil.getProperties("activity.anniversary.red.id");
			Long actId = Long.parseLong(activityIdStr);
			Optional<Activity> act = getActivityByCache(actId);
			if (act.isPresent() && DateUtils.isDateBetween(DateUtils.getCurrentDate(), act.get().getStartTime(), act.get().getEndTime())) {
				Transaction t = transactionManager.selectTransactionById(transactionId);
				if (!t.getMemberId().equals(memberId)) {
					rDO.setResultCode(ResultCode.ANNIVERSARY_SHARE_INVESTSELF_ERROR);
					return rDO;
				}
				if (!DateUtils.isDateBetween(t.getTransactionTime(), act.get().getStartTime(), act.get().getEndTime())) {
					rDO.setResultCode(ResultCode.ANNIVERSARY_SHARE_TIMEOUT_ERROR);
					return rDO;
				}
				if (t.getInvestAmount().compareTo(Constant.AnniversaryShareInvest) == -1) {
					rDO.setResultCode(ResultCode.ANNIVERSARY_SHARE_INVEST_ERROR);
					return rDO;
				}
				;
				// 新客项目除外
				Project p = projectManager.selectByPrimaryKey(t.getProjectId());
				if (p.getIsNovice().equals(0)) {
					rDO.setResultCode(ResultCode.ANNIVERSARY_REDURL_ISNOVICE_ERROR);
					return rDO;
				}
				AES aes = AES.getInstance();
				String encryptUrl = aes.encryptAnniversary(transactionId, memberId, act.get().getStartTime().getTime(), act.get()
						.getEndTime().getTime());
				if (StringUtil.isNotBlank(encryptUrl)) {
					ActivityForAnniversary afa = new ActivityForAnniversary();
					afa.setEncryptUrl(URLEncoder.encode(encryptUrl, Constant.DEFAULT_CODE));
					rDO.setResult(afa);
					rDO.setSuccess(true);
				}
			} else {
				logger.error("红包不存在actId={}", actId);
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
				return rDO;
			}
		} catch (Exception e) {
			logger.error("红包加密失败, transactionId={}, memberId={}", transactionId, memberId, e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}

	@Override
	public Optional<Activity> getActivityByCache(Long activityId) {
		try {
			return LotteryContainer.getInstance().getActivity(activityId);
		} catch (Exception e) {
			logger.error("获取活动信息（缓存）失败, activityId={}", activityId, e);
		}
		return Optional.absent();
	}

	/**
	 * 
	 * @Description:根据活动时间返回状态，适用于读取活动缓存无法精确得到活动状态的场景
	 * @param startTime
	 * @param endTime
	 * @return
	 * @author: wangyanji
	 * @time:2015年11月20日 下午4:23:29
	 */
	private Integer getActivityStatusFromTime(Date startTime, Date endTime) {
		if (DateUtils.getCurrentDate().before(startTime)) {
			return StatusEnum.ACTIVITY_STATUS_IS_AUDIT.getStatus();
		} else if (DateUtils.getCurrentDate().after(endTime)) {
			return StatusEnum.ACTIVITY_STATUS_IS_END.getStatus();
		} else {
			return StatusEnum.ACTIVITY_STATUS_IS_START.getStatus();
		}
	}

	@Override
	public Object doubleDanInit(Optional<MemberSessionDto> optOfMember) {
		ResultDO<ActivityForDoubleDan> rDO = new ResultDO<ActivityForDoubleDan>();
		ActivityForDoubleDan acivityModel = new ActivityForDoubleDan();
		long christmasId = 0L;
		long newYearId = 0L;
		long secretId = 0L;
		try {
			christmasId = Long.parseLong(WebPropertiesUtil.getProperties("activity.doubleDan.christmas.id"));
			newYearId = Long.parseLong(WebPropertiesUtil.getProperties("activity.doubleDan.newYear.id"));
			secretId = Long.parseLong(WebPropertiesUtil.getProperties("activity.doubleDan.secret.id"));
			Optional<Activity> optChristmas = this.getActivityByCache(christmasId);
			Optional<Activity> optNewYear = this.getActivityByCache(newYearId);
			Optional<Activity> optSecret = this.getActivityByCache(secretId);
			if (!optChristmas.isPresent() || !optNewYear.isPresent() || !optSecret.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			// 加载圣诞节已抽记录
			christmasInit(optChristmas.get(), optOfMember, acivityModel);
			// 加载元旦已抽记录
			newYearInit(optNewYear.get(), optOfMember, acivityModel);
			// 加载神秘新年礼
			secretGift(optSecret.get(), optOfMember, acivityModel);
			rDO.setResult(acivityModel);
		} catch (Exception e) {
			logger.error("圣诞节&元旦初始化失败, christmasId={}, newYearId={}, secretId={}", christmasId, newYearId, secretId, e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}

	/**
	 * 
	 * @Description:加载圣诞节已抽记录
	 * @param act
	 * @param memberId
	 * @param activityForDoubleDan
	 * @return
	 * @throws Exception
	 * @author: wangyanji
	 * @time:2015年11月20日 下午4:14:29
	 */
	private ActivityForDoubleDan christmasInit(Activity act, Optional<MemberSessionDto> optOfMember,
			ActivityForDoubleDan activityForDoubleDan) throws Exception {
		activityForDoubleDan.setChristmasStatus(getActivityStatusFromTime(act.getStartTime(), act.getEndTime()));
		if (activityForDoubleDan.getChristmasStatus() == StatusEnum.ACTIVITY_STATUS_IS_AUDIT.getStatus() || !optOfMember.isPresent()) {
			return activityForDoubleDan;
		}
		ActivityLotteryResult alr = new ActivityLotteryResult();
		alr.setActivityId(act.getId());
		alr.setMemberId(optOfMember.get().getId());
		// 查出已经领取的圣诞礼物
		List<ActivityLotteryResult> christmasResList = activityLotteryResultManager.getLotteryResultBySelective(alr);
		if (Collections3.isEmpty(christmasResList)) {
			return activityForDoubleDan;
		}
		List<String> list = Lists.newArrayList();
		for (ActivityLotteryResult gift : christmasResList) {
			list.add(gift.getRewardInfo());
		}
		activityForDoubleDan.setChristmasGiftList(list);
		return activityForDoubleDan;
	}

	/**
	 * 
	 * @Description:加载元旦已抽记录&新年礼
	 * @param act
	 * @param memberId
	 * @param activityForDoubleDan
	 * @return
	 * @throws Exception
	 * @author: wangyanji
	 * @time:2015年11月20日 下午4:29:48
	 */
	private ActivityForDoubleDan newYearInit(Activity act, Optional<MemberSessionDto> optOfMember, ActivityForDoubleDan activityForDoubleDan)
			throws Exception {
		activityForDoubleDan.setNewYearStatus(getActivityStatusFromTime(act.getStartTime(), act.getEndTime()));
		if (activityForDoubleDan.getNewYearStatus().intValue() == StatusEnum.ACTIVITY_STATUS_IS_AUDIT.getStatus()
				|| activityForDoubleDan.getNewYearStatus().intValue() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
			// 活动未开始或者进行中，默认“热情等待中”
			activityForDoubleDan.setNewYearMissionList(Lists.newArrayList(
					StatusEnum.ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_WAITING.getStatus(),
					StatusEnum.ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_WAITING.getStatus(),
					StatusEnum.ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_WAITING.getStatus(),
					StatusEnum.ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_WAITING.getStatus(),
					StatusEnum.ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_WAITING.getStatus()));
		} else {
			// 活动已结束
			activityForDoubleDan.setNewYearMissionList(Lists.newArrayList(StatusEnum.ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_END.getStatus(),
					StatusEnum.ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_END.getStatus(),
					StatusEnum.ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_END.getStatus(),
					StatusEnum.ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_END.getStatus(),
					StatusEnum.ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_END.getStatus()));
		}
		if (activityForDoubleDan.getNewYearStatus().intValue() == StatusEnum.ACTIVITY_STATUS_IS_AUDIT.getStatus()
				|| activityForDoubleDan.getNewYearStatus().intValue() == StatusEnum.ACTIVITY_STATUS_IS_END.getStatus()) {
			// 活动未开始返回
			return activityForDoubleDan;
		}
		Date nowDay = DateUtils.zerolizedTime(DateUtils.getCurrentDate());
		Date startDate = DateUtils.zerolizedTime(act.getStartTime());
		Date endDate = DateUtils.zerolizedTime(act.getEndTime());
		// 当前任务格(0开始)
		int nowIndex = DateUtils.getIntervalDays(startDate, nowDay);
		int activityIndex = DateUtils.getIntervalDays(startDate, endDate) - 1;
		if (nowIndex > activityIndex) {
			nowIndex = activityIndex;
		}
		if (!optOfMember.isPresent()) {
			// 未登录，初始化领取按钮文案
			for (int i = 0; i <= nowIndex; i++) {
				activityForDoubleDan.getNewYearMissionList().set(i, StatusEnum.ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_UNLOGIN.getStatus());
			}
			return activityForDoubleDan;
		}
		Optional<List<Object>> optReward = LotteryContainer.getInstance().getRewardsListMap(act.getId(), RewardsBase.class);
		List<Coupon> couponList = couponManager.selectNewYearCoupon(optReward.get(), optOfMember.get().getId(), act.getId());
		if (Collections3.isEmpty(couponList)) {
			// 一张未领
			activityForDoubleDan.getNewYearMissionList().set(0, StatusEnum.ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_RECEIVENOW.getStatus());
			for (int i = 1; i <= nowIndex; i++) {
				activityForDoubleDan.getNewYearMissionList().set(i, StatusEnum.ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_WAITED.getStatus());
			}
			activityForDoubleDan.setSecretRealIndex(0);
			return activityForDoubleDan;
		} else {
			if (couponList.size() != optReward.get().size()) {
				activityForDoubleDan.setSecretRealIndex(0);
			} else {
				// 返回神秘礼分子
				BigDecimal start = new BigDecimal(0);
				for (Coupon c : couponList) {
					if (c.getStatus() == StatusEnum.COUPON_STATUS_USED.getStatus() && c.getAmount().intValue() == 1) {
						start = start.add(c.getAmount());
					}
				}
				activityForDoubleDan.setSecretRealIndex(start.intValue());
			}
		}
		for (int i = 0; i < couponList.size(); i++) {
			// 遍历已经拿了的优惠券
			if (couponList.get(i).getStatus().intValue() == StatusEnum.COUPON_STATUS_USED.getStatus()) {
				activityForDoubleDan.getNewYearMissionList().set(i, StatusEnum.ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_USED.getStatus());
			} else if (couponList.get(i).getStatus().intValue() == StatusEnum.COUPON_STATUS_RECEIVED_EXPIRE.getStatus()) {
				activityForDoubleDan.getNewYearMissionList().set(i,
						StatusEnum.ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_RECEIVED_EXPIRE.getStatus());
			} else {
				activityForDoubleDan.getNewYearMissionList().set(i,
						StatusEnum.ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_RECEIVED_EFFECT.getStatus());
			}
		}
		if (activityForDoubleDan.getNewYearMissionList().get(nowIndex).intValue() == StatusEnum.ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_WAITING
				.getStatus()) {
			int startIndex = couponList.size();
			if (startIndex == 0
					|| activityForDoubleDan.getNewYearMissionList().get(startIndex - 1) == StatusEnum.ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_USED
							.getStatus()) {
				activityForDoubleDan.getNewYearMissionList().set(startIndex,
						StatusEnum.ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_RECEIVENOW.getStatus());
			} else {
				activityForDoubleDan.getNewYearMissionList().set(startIndex,
						StatusEnum.ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_WAITED.getStatus());
			}
			startIndex++;
			// 当天未领取
			for (int i = startIndex; i <= nowIndex; i++) {
				activityForDoubleDan.getNewYearMissionList().set(i, StatusEnum.ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_WAITED.getStatus());
			}
		}
		return activityForDoubleDan;
	}

	/**
	 * 
	 * @Description:加载神秘新年礼
	 * @param act
	 * @param memberId
	 * @param activityForDoubleDan
	 * @return
	 * @throws Exception
	 * @author: wangyanji
	 * @time:2015年11月24日 上午10:00:58
	 */
	private ActivityForDoubleDan secretGift(Activity act, Optional<MemberSessionDto> optOfMember, ActivityForDoubleDan activityForDoubleDan)
			throws Exception {
		activityForDoubleDan.setSecretStatus(getActivityStatusFromTime(act.getStartTime(), act.getEndTime()));
		if (activityForDoubleDan.getSecretStatus().intValue() == StatusEnum.ACTIVITY_STATUS_IS_AUDIT.getStatus()) {
			activityForDoubleDan.setSecretRealIndex(null);
			// 活动未开始或者进行中，默认“热情等待中”
			activityForDoubleDan.setSecretMission(StatusEnum.ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_WAITING.getStatus());
		} else if (activityForDoubleDan.getSecretStatus().intValue() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
			secretGiftStart(act, optOfMember, activityForDoubleDan);
		} else {
			activityForDoubleDan.setSecretRealIndex(null);
			// 活动已结束，默认显示“活动结束”
			activityForDoubleDan.setSecretMission(StatusEnum.ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_END.getStatus());
		}
		return activityForDoubleDan;
	}

	/**
	 * 加载神秘大礼状态
	 * 
	 * @Description:TODO
	 * @param optOfMember
	 * @param activityForDoubleDan
	 * @return
	 * @author: wangyanji
	 * @throws ManagerException
	 * @time:2015年12月16日 下午7:35:15
	 */
	private ActivityForDoubleDan secretGiftStart(Activity act, Optional<MemberSessionDto> optOfMember,
			ActivityForDoubleDan activityForDoubleDan) throws ManagerException {
		if (optOfMember.isPresent()) {
			List<Coupon> couponList = couponManager.getCouponByMemberIdAndActivityId(optOfMember.get().getId(), act.getId());
			if (Collections3.isEmpty(couponList)
					&& activityForDoubleDan.getNewYearMissionList().get(4) == StatusEnum.ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_USED
							.getStatus()) {
				// 未领取,立即领取
				activityForDoubleDan.setSecretMission(StatusEnum.ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_RECEIVENOW.getStatus());
				return activityForDoubleDan;
			} else if (Collections3.isEmpty(couponList)) {
				// 未领取，等待领取
				activityForDoubleDan.setSecretMission(StatusEnum.ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_WAITED.getStatus());
				return activityForDoubleDan;
			}
			if (couponList.get(0).getStatus().intValue() == StatusEnum.COUPON_STATUS_USED.getStatus()) {
				// 已使用
				activityForDoubleDan.setSecretMission(StatusEnum.ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_USED.getStatus());
			} else if (couponList.get(0).getStatus().intValue() == StatusEnum.COUPON_STATUS_RECEIVED_EXPIRE.getStatus()) {
				// 已领取
				activityForDoubleDan.setSecretMission(StatusEnum.ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_RECEIVED_EXPIRE.getStatus());
			} else {
				activityForDoubleDan.setSecretMission(StatusEnum.ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_RECEIVED_EFFECT.getStatus());
			}
		} else {
			activityForDoubleDan.setSecretMission(StatusEnum.ACTIVITY_DOUBLEDAN_NEWYEAR_SATATUS_UNLOGIN.getStatus());
		}
		return activityForDoubleDan;
	}

	@Override
	public Object doubleDanReceiveChristmas(Long memberId) {
		ResultDO<ActivityForDoubleDan> rDO = new ResultDO<ActivityForDoubleDan>();
		ActivityForDoubleDan acivityModel = new ActivityForDoubleDan();
		Long christmasId = 0L;
		try {
			christmasId = Long.parseLong(WebPropertiesUtil.getProperties("activity.doubleDan.christmas.id"));
			Optional<Activity> optChristmas = this.getActivityByCache(christmasId);
			if (!optChristmas.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			int status = getActivityStatusFromTime(optChristmas.get().getStartTime(), optChristmas.get().getEndTime());
			acivityModel.setChristmasStatus(status);
			if (status != StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
				acivityModel.setSecretRealIndex(null);
				rDO.setResult(acivityModel);
				return rDO;
			}
			// 查询领券记录
			Map<String, Object> map = ImmutableMap.of("activityId", (Object) christmasId, "memberId", (Object) memberId);
			List<ActivityLottery> lotteriedList = activityLotteryManager.queryLotteryByMemberAndActivity(map);
			if (Collections3.isNotEmpty(lotteriedList) && lotteriedList.size() >= 2) {
				rDO.setResultCode(ResultCode.ACTIVITY_YET_JOIN_ACTIVITY_ERROR);
				return rDO;
			}
			if (Collections3.isEmpty(lotteriedList)) {
				lotteriedList = Lists.newArrayList();
			}
			// 领券
			RuleBody rb = new RuleBody();
			rb.setActivityId(optChristmas.get().getId());
			rb.setMemberId(memberId);
			rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
			if (Collections3.isEmpty(lotteriedList) || "2".equals(lotteriedList.get(0).getCycleConstraint())) {
				rb.setCycleStr("1");
			} else {
				rb.setCycleStr("2");
			}
			rb.setActivityName("【双旦迎薪,提钱跨年】之神秘圣诞礼活动");
			rb.setExceptDrawedRewards(true);
			rb.setProbabilityByAverage(true);
			// 校验
			if (drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
				// 抽奖
				RewardsBodyForProbility rfp = (RewardsBodyForProbility) drawByProbability.drawLottery(rb, null,
						TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode());
				if (!"noReward".equals(rfp.getRewardCode())) {
					// 发送站内信
					MessageClient.sendMsgForSPEngin(memberId, rb.getActivityName(), rfp.getRewardName());
				}
				acivityModel.setThisChristmasGift(rfp.getRewardName());
				// 获取圣诞结果集
				ActivityLotteryResult alr = new ActivityLotteryResult();
				alr.setActivityId(christmasId);
				alr.setMemberId(memberId);
				// 查出已经领取的圣诞礼物
				List<ActivityLotteryResult> christmasResList = activityLotteryResultManager.getLotteryResultBySelective(alr);
				if (Collections3.isNotEmpty(christmasResList)) {
					List<String> list = Lists.newArrayList();
					for (ActivityLotteryResult gift : christmasResList) {
						list.add(gift.getRewardInfo());
					}
					acivityModel.setChristmasGiftList(list);
				}
			} else {
				rDO.setResultCode(ResultCode.ACTIVITY_YET_JOIN_ACTIVITY_ERROR);
				return rDO;
			}
			rDO.setResult(acivityModel);
		} catch (Exception e) {
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
			String errorMsg = e.getMessage();
			if (e.getClass().equals(ManagerException.class) && errorMsg.startsWith("重复参加活动")) {
				logger.error("圣诞节领券失败,重复参加活动！christmasId={}", christmasId);
				rDO.getResultCode().setMsg(errorMsg);
			} else {
				logger.error("圣诞节领券失败, christmasId={}", christmasId, e);
			}
		}
		return rDO;
	}

	@Override
	public Object doubleDanReceiveNewYear(Long memberId, int giftIndex) {
		ResultDO<ActivityForDoubleDan> rDO = new ResultDO<ActivityForDoubleDan>();
		ActivityForDoubleDan acivityModel = new ActivityForDoubleDan();
		Long newYearId = 0L;
		try {
			if (giftIndex < 0 || giftIndex > 4) {
				rDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
				return rDO;
			}
			newYearId = Long.parseLong(WebPropertiesUtil.getProperties("activity.doubleDan.newYear.id"));
			Optional<Activity> optNewYear = this.getActivityByCache(newYearId);
			if (!optNewYear.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			int status = getActivityStatusFromTime(optNewYear.get().getStartTime(), optNewYear.get().getEndTime());
			acivityModel.setNewYearStatus(status);
			if (status != StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
				rDO.setResult(acivityModel);
				return rDO;
			}
			// 领取前判断上一张券是否已经使用
			Optional<List<Object>> opt = LotteryContainer.getInstance().getRewardsListMap(newYearId, RewardsBase.class);
			if (!opt.isPresent()) {
				logger.error("元旦规则读取失败, newYearId={}", newYearId);
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			RewardsBase rBase = (RewardsBase) (opt.get().get(giftIndex));
			Date requireDate = DateUtils.getDateFromString(rBase.getRequireTime(), DateUtils.DATE_FMT_3);
			if (requireDate.after(DateUtils.getCurrentDate())) {
				// 未到当天那一关
				rDO.setResultCode(ResultCode.ACTIVITY_DOUBLEDAN_BEFOREREQUIRETIME_ERROR);
				return rDO;
			}
			if (giftIndex > 0) {
				// 判断前一天的券是否已经使用
				RewardsBase rb = (RewardsBase) opt.get().get(giftIndex - 1);
				List<Coupon> couponList = couponManager.getCouponByMemberIdAndCouponTemplateId(memberId, rb.getTemplateId());
				if (Collections3.isEmpty(couponList)) {
					// 前一天关卡现金券未领取
					rDO.setResultCode(ResultCode.ACTIVITY_DOUBLEDAN_LASTDAY_NOTRECEIVE_ERROR);
					return rDO;
				} else if (couponList.get(0).getStatus().intValue() == StatusEnum.COUPON_STATUS_RECEIVED_NOT_USED.getStatus()) {
					// 未使用
					rDO.setResultCode(ResultCode.ACTIVITY_DOUBLEDAN_LASTDAY_NOTUSED_ERROR);
					return rDO;
				} else if (couponList.get(0).getStatus().intValue() == StatusEnum.COUPON_STATUS_RECEIVED_EXPIRE.getStatus()) {
					// 已过期
					rDO.setResultCode(ResultCode.ACTIVITY_DOUBLEDAN_LASTDAY_OUTTIME_ERROR);
					return rDO;
				}
			}
			// 领取
			RuleBody ruleBody = new RuleBody();
			ruleBody.setActivityId(newYearId);
			ruleBody.setMemberId(memberId);
			ruleBody.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
			ruleBody.setCycleStr(rBase.getTemplateId().toString());
			ruleBody.setActivityName("【双旦迎薪，提钱跨年】之跨年现金周活动");
			if (drawByPrizeDirectly.validate(ruleBody, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
				drawByPrizeDirectly.drawLottery(ruleBody, rBase, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode());
				// 发送站内信
				MessageClient.sendMsgForSPEngin(memberId, ruleBody.getActivityName(), rBase.getRewardName());
				acivityModel.setThisNewYearGift(rBase.getRewardName());
			} else {
				rDO.setResultCode(ResultCode.ACTIVITY_YET_JOIN_ACTIVITY_ERROR);
				return rDO;
			}
			rDO.setResult(acivityModel);
		} catch (Exception e) {
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
			String errorMsg = e.getMessage();
			if (e.getClass().equals(ManagerException.class) && errorMsg.startsWith("重复参加活动")) {
				logger.error("元旦领券失败,重复参加活动！ newYearId={}", newYearId);
				rDO.getResultCode().setMsg(errorMsg);
			} else {
				logger.error("元旦领券失败, newYearId={}", newYearId, e);
			}
		}
		return rDO;
	}

	@Override
	public Object doubleDanReceiveScretGift(Long memberId) {
		ResultDO<ActivityForDoubleDan> rDO = new ResultDO<ActivityForDoubleDan>();
		ActivityForDoubleDan acivityModel = new ActivityForDoubleDan();
		Long secretId = 0L;
		long newYearId = 0L;
		try {
			newYearId = Long.parseLong(WebPropertiesUtil.getProperties("activity.doubleDan.newYear.id"));
			secretId = Long.parseLong(WebPropertiesUtil.getProperties("activity.doubleDan.secret.id"));
			Optional<Activity> optSecret = this.getActivityByCache(secretId);
			if (!optSecret.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			int status = getActivityStatusFromTime(optSecret.get().getStartTime(), optSecret.get().getEndTime());
			acivityModel.setSecretStatus(status);
			if (status != StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
				rDO.setResult(acivityModel);
				return rDO;
			}
			Optional<List<Object>> optReward = LotteryContainer.getInstance().getRewardsListMap(newYearId, RewardsBase.class);
			List<Coupon> couponList = couponManager.selectNewYearCoupon(optReward.get(), memberId, newYearId);
			if (Collections3.isEmpty(couponList) || couponList.size() != optReward.get().size()) {
				// 未参加元旦活动或者没有全部领取
				acivityModel.setSecretRealIndex(0);
				rDO.setResult(acivityModel);
				return rDO;
			}
			BigDecimal start = new BigDecimal(0);
			for (Coupon c : couponList) {
				if (c.getStatus() == StatusEnum.COUPON_STATUS_USED.getStatus() && c.getAmount().intValue() == 1) {
					start = start.add(c.getAmount());
				} else if (c.getStatus() != StatusEnum.COUPON_STATUS_USED.getStatus()) {
					// 存在未使用
					acivityModel.setSecretRealIndex(0);
					rDO.setResult(acivityModel);
					return rDO;
				}
			}
			if (start.intValue() == 0) {
				// 神秘礼擦肩而过
				acivityModel.setSecretRealIndex(0);
				rDO.setResult(acivityModel);
				return rDO;
			}
			acivityModel.setSecretRealIndex(start.intValue());
			// 领取对应现金券
			Optional<List<Object>> opt = LotteryContainer.getInstance().getRewardsListMap(secretId, RewardsBase.class);
			if (!opt.isPresent()) {
				logger.error("神秘大奖规则读取失败, newYearId={}", newYearId);
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			int index = start.intValue() - 1;
			RewardsBase rBase = (RewardsBase) (opt.get().get(index));
			// 领取
			RuleBody ruleBody = new RuleBody();
			ruleBody.setActivityId(secretId);
			ruleBody.setMemberId(memberId);
			ruleBody.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
			ruleBody.setCycleStr(secretId.toString());
			ruleBody.setActivityName("【双旦迎薪，提钱跨年】之神秘新年礼活动");
			if (drawByPrizeDirectly.validate(ruleBody, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
				drawByPrizeDirectly.drawLottery(ruleBody, rBase, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode());
				// 发送站内信
				MessageClient.sendMsgForSPEngin(memberId, ruleBody.getActivityName(), rBase.getRewardName());
				acivityModel.setThisSecretGift(rBase.getRewardName());
			} else {
				rDO.setResultCode(ResultCode.ACTIVITY_YET_JOIN_ACTIVITY_ERROR);
				return rDO;
			}
			rDO.setResult(acivityModel);
		} catch (Exception e) {
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
			String errorMsg = e.getMessage();
			if (e.getClass().equals(ManagerException.class) && errorMsg.startsWith("重复参加活动")) {
				logger.error("神秘新年礼领取失败, 重复参加活动！secretId={}", secretId);
				rDO.getResultCode().setMsg(errorMsg);
			} else {
				logger.error("神秘新年礼领取失败, secretId={}", secretId, e);
			}
		}
		return rDO;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object millionFundInit() {
		ResultDO<ActivityForMillionCoupon> rDO = new ResultDO<ActivityForMillionCoupon>();
		ActivityForMillionCoupon model = new ActivityForMillionCoupon();
		try {
			String activityIdStr = PropertiesUtil.getProperties("activity.millionCoupon.id");
			Long activityId = Long.parseLong(activityIdStr);
			Optional<Activity> optActivity = this.getActivityByCache(activityId);
			if (!optActivity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			model.setActivityStatus(optActivity.get().getActivityStatus());
			int fund = 0;
			if (model.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_AUDIT.getStatus()) {
				fund = new Integer(Constant.ACTIVITY_MILLIONCOUPON_FUND);
			} else if (model.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
				fund = RedisActivityClient.millionCoupon(null);
			}
			model.setFund(fund);
			String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR
					+ RedisConstant.REDIS_KEY_ACTIVITY_MILLIONCOUPON_NAME;
			boolean isExit = RedisManager.isExitByObjectKey(key);
			List<ActivityLotteryResultBiz> list = null;
			if (isExit) {
				list = (List<ActivityLotteryResultBiz>) RedisManager.getObject(key);
				model.setInvestList(list);
			} else {
				getInvestList(activityId, model, key);
			}
			rDO.setResult(model);
		} catch (Exception e) {
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("百万现金券活动初始化失败", e);
		}
		return rDO;
	}

	/**
	 * 
	 * @Description:获取百万活动投资记录
	 * @param activityId
	 * @param model
	 * @param redisKey
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年1月14日 下午6:24:29
	 */
	private void getInvestList(Long activityId, ActivityForMillionCoupon model, String redisKey) throws ManagerException {
		List<ActivityLotteryResultBiz> list = Lists.newArrayList();
		List<ActivityLotteryResult> modelList = activityLotteryManager.queryNewLotteryResult(activityId, null, 40);
		if (CollectionUtils.isNotEmpty(modelList)) {
			list = BeanCopyUtil.mapList(modelList, ActivityLotteryResultBiz.class);
			for (ActivityLotteryResultBiz biz : list) {
				biz.setMemberName(ServletUtil.getMemberUserName(biz.getMemberId()));
				biz.setAvatar(ServletUtil.getMemberAvatarById(biz.getMemberId()));
				biz.setMemberId(null);
			}
			RedisManager.putObject(redisKey, list);
			RedisManager.expireObject(redisKey, 120);
			model.setInvestList(list);
		}

	}

	@Override
	public Object springFestivalInit(Optional<MemberSessionDto> optOfMember) {
		ResultDO<ActivityForSpringFestival> rDO = new ResultDO<ActivityForSpringFestival>();
		try {
			// 春节活动
			Optional<Activity> optOfSpring = LotteryContainer.getInstance().getActivityByName(Constant.SPRING_FESTIVAL_NAME);
			if (!optOfSpring.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			Activity activity = optOfSpring.get();
			// 匹配除夕领券时间
			ActivityForSpringFestival springFestival = JSON
					.parseObject(activity.getObtainConditionsJson(), ActivityForSpringFestival.class);
			springFestival.setCouponTemplateId(null);
			springFestival.setActivityStatus(activity.getActivityStatus());
			springFestival.setStartTime(activity.getStartTime());
			springFestival.setEndTime(activity.getEndTime());
			if (activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_AUDIT.getStatus()) {
				// 活动未开始，返回活动状态
				rDO.setResult(springFestival);
				return rDO;
			}
			// 加载辞旧迎新兑现返人气
			springLoadRebate(activity, 40, springFestival);
			// 加载许愿列表
			springLoadWish(activity.getId(), 40, springFestival);
			// 加载如意项目列表
			springLoadWishesPro(activity.getId(), 40, springFestival);
			// 加载红包领用情况
			springLoadRedBag(activity.getId(), springFestival);
			// 加载会员活动状态
			if (optOfMember.isPresent()) {
				// 判断是否许愿
				boolean messageFlag = activityMessageManager.checkMessageByActivityIdAndMemberId(optOfSpring.get().getId(), optOfMember
						.get().getId());
				springFestival.setHasMakeWish(messageFlag);
				// 判断是否领取现金券
				List<Coupon> list = couponManager.getCouponByMemberIdAndActivityId(optOfMember.get().getId(), activity.getId());
				if (Collections3.isNotEmpty(list)) {
					springFestival.setHasReceiveCoupon(true);
				}
			}
			rDO.setResult(springFestival);
		} catch (Exception e) {
			logger.error("春节初始化失败", e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}

	/**
	 * 
	 * @Description:加载春节兑换返利
	 * @author: wangyanji
	 * @time:2015年12月29日 下午5:48:13
	 */
	@SuppressWarnings("unchecked")
	private void springLoadRebate(Activity activity, int rowNum, ActivityForSpringFestival springFestival) throws Exception {
		String rebateKey = RedisConstant.REDIS_KEY_ACTIVITY_SPRING_REBATELIST;
		boolean isExit = RedisManager.isExitByObjectKey(rebateKey);
		List<ActivityLotteryResultBiz> list = null;
		if (isExit) {
			list = (List<ActivityLotteryResultBiz>) RedisManager.getObject(rebateKey);
		} else {
			List<ActivityLotteryResult> modelList = activityLotteryManager.queryNewLotteryResult(activity.getId(), Constant.SPRING_REBATE,
					rowNum);
			if (CollectionUtils.isNotEmpty(modelList)) {
				list = BeanCopyUtil.mapList(modelList, ActivityLotteryResultBiz.class);
				for (ActivityLotteryResultBiz model : list) {
					model.setMemberName(ServletUtil.getMemberUserName(model.getMemberId()));
					model.setAvatar(ServletUtil.getMemberAvatarById(model.getMemberId()));
					model.setMemberId(null);
				}
				RedisManager.putObject(rebateKey, list);
				RedisManager.expireObject(rebateKey, 1200);
			}
		}
		springFestival.setRechargePopularityList(list);
	}

	/**
	 * 
	 * @Description:加载许愿列表
	 * @author: wangyanji
	 * @time:2015年12月29日 下午5:48:13
	 */
	@SuppressWarnings("unchecked")
	private void springLoadWish(Long activityId, int rowNum, ActivityForSpringFestival springFestival) throws Exception {
		String wishKey = RedisConstant.REDIS_KEY_ACTIVITY_SPRING_WISHLIST;
		boolean isExit = RedisManager.isExitByObjectKey(wishKey);
		List<ActivityLotteryResultBiz> list = null;
		if (isExit) {
			list = (List<ActivityLotteryResultBiz>) RedisManager.getObject(wishKey);
		} else {
			List<ActivityMessage> modelList = activityMessageManager.selectRankByActivityId(activityId, rowNum);
			if (CollectionUtils.isNotEmpty(modelList)) {
				list = BeanCopyUtil.mapList(modelList, ActivityLotteryResultBiz.class);
				for (ActivityLotteryResultBiz model : list) {
					model.setMemberName(ServletUtil.getMemberUserName(model.getMemberId()));
					model.setAvatar(ServletUtil.getMemberAvatarById(model.getMemberId()));
					model.setMemberId(null);
				}
				RedisManager.putObject(wishKey, list);
				RedisManager.expireObject(wishKey, 1200);
			}
		}
		springFestival.setWishList(list);
		// 判断领取压岁钱的个数
		ActivityLotteryResult model = new ActivityLotteryResult();
		model.setActivityId(activityId);
		model.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
		int participateNum = activityLotteryManager.countNewLotteryResult(model);
		springFestival.setReceiveCouponNum(participateNum);

	}

	/**
	 * 
	 * @Description:加载如意项目列表
	 * @author: wangyanji
	 * @time:2015年12月29日 下午5:48:13
	 */
	@SuppressWarnings("unchecked")
	private void springLoadWishesPro(Long activityId, int rowNum, ActivityForSpringFestival springFestival) throws Exception {
		String wishesProKey = RedisConstant.REDIS_KEY_ACTIVITY_SPRING_WISHESPROLIST;
		boolean isExit = RedisManager.isExitByObjectKey(wishesProKey);
		List<ActivityLotteryResultBiz> list = null;
		if (isExit) {
			list = (List<ActivityLotteryResultBiz>) RedisManager.getObject(wishesProKey);
		} else {
			List<ActivityLotteryResult> modelList = activityLotteryManager.queryNewLotteryResultAndProject(activityId,
					Constant.SPRING_WISHES, rowNum);
			if (CollectionUtils.isNotEmpty(modelList)) {
				list = BeanCopyUtil.mapList(modelList, ActivityLotteryResultBiz.class);
				for (ActivityLotteryResultBiz model : list) {
					model.setMemberName(ServletUtil.getMemberUserName(model.getMemberId()));
					model.setAvatar(ServletUtil.getMemberAvatarById(model.getMemberId()));
					model.setMemberId(null);
				}
				RedisManager.putObject(wishesProKey, list);
				RedisManager.expireObject(wishesProKey, 1200);
			}
		}
		springFestival.setSprProInvestList(list);
		// 加载推荐如意标
		Map<String, Object> selectMap = ImmutableMap.of("activitySign", (Object) 1);
		List<ProjectForFront> returnList = projectManager.selectExtraProject(selectMap,
				new int[] { StatusEnum.PROJECT_STATUS_WAIT_RELEASE.getStatus(), StatusEnum.PROJECT_STATUS_INVESTING.getStatus(),
						StatusEnum.PROJECT_STATUS_FULL.getStatus() });
		if (Collections3.isNotEmpty(returnList)) {
			ProjectForFront pro = returnList.get(0);
			// 获取进度
			pro.setProcess(SysServiceUtils.getProjectProgress(pro.getTotalAmount(), pro.getId()));
			// 获取剩余可投金额
			pro.setAvailableBalance(SysServiceUtils.getProjectBalance(pro.getId()));
			// 获取进度条
			pro.setRound(SysServiceUtils.getProgressCeil(pro.getTotalAmount(), pro.getId()));
			springFestival.setRecommendProject(pro);
		}
	}

	/**
	 * 
	 * @Description:红包统计
	 * @param activityId
	 * @param rowNum
	 * @param springFestival
	 * @throws Exception
	 * @author: wangyanji
	 * @time:2016年1月13日 下午4:43:01
	 */
	private void springLoadRedBag(Long activityId, ActivityForSpringFestival springFestival) throws Exception {
		String totalKey = RedisConstant.REDIS_KEY_ACTIVITY_REDBAG_TOTALNUM + RedisConstant.REDIS_SEPERATOR
				+ RedisConstant.REDIS_KEY_ACTIVITY_SPRING;
		String claimKey = RedisConstant.REDIS_KEY_ACTIVITY_REDBAG_CLAIMNUM + RedisConstant.REDIS_SEPERATOR
				+ RedisConstant.REDIS_KEY_ACTIVITY_SPRING;
		String totalNum = RedisManager.get(totalKey);
		String claimNum = RedisManager.get(claimKey);
		springFestival.setTotalNum(StringUtil.isBlank(totalNum) ? "0" : totalNum);
		springFestival.setClaimNum(StringUtil.isBlank(claimNum) ? "0" : claimNum);
	}

	@Override
	public Object springFestivalMakeWish(Long memberId, Long messageTemplateId) {
		ResultDO<ActivityForSpringFestival> rDO = new ResultDO<ActivityForSpringFestival>();
		try {
			Optional<Activity> optOfActivity = LotteryContainer.getInstance().getActivityByName(Constant.SPRING_FESTIVAL_NAME);
			if (!optOfActivity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			if (messageTemplateId < 1l) {
				rDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
				return rDO;
			}
			if (optOfActivity.get().getActivityStatus() != StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
				return rDO;
			}
			int updateNum = activityMessageManager.insert(Constant.SPRING_FESTIVAL_NAME, memberId, messageTemplateId);
			if (updateNum == 1) {
				String wishKey = RedisConstant.REDIS_KEY_ACTIVITY_SPRING_WISHLIST;
				RedisManager.removeObject(wishKey);
				rDO.setSuccess(true);
			} else {
				rDO.setResultCode(ResultCode.ERROR_SYSTEM);
				return rDO;
			}
		} catch (Exception e) {
			logger.error("春节许愿失败, memberId={}, messageTemplateId={}", memberId, messageTemplateId, e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}

	@Override
	public Object springFestivalReceiveCoupon(Long memberId) {
		ResultDO<Object> rDO = new ResultDO<Object>();
		try {
			rDO = activityLotteryManager.springFestivalReceiveCoupon(memberId);
		} catch (Exception e) {
			logger.error("春节领券失败 memberId={}", memberId, e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}

	@Override
	public Object getTransactionRedBagUrl(Long transactionId, Long memberId) {
		ResultDO<Object> rDO = new ResultDO<Object>();
		try {
			if (transactionId == null || transactionId.longValue() == 0L) {
				rDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
				return rDO;
			}
			Transaction t = transactionManager.selectTransactionById(transactionId);
			if (!memberId.equals(t.getMemberId())) {
				rDO.setResultCode(ResultCode.ANNIVERSARY_SHARE_INVESTSELF_ERROR);
				return rDO;
			}
			Activity act = new Activity();
			act.setActivityStatus(StatusEnum.ACTIVITY_STATUS_IS_START.getStatus());
			act.setActivityDesc(Constant.TRANSACTION_REDBAG_DESC);
			List<Activity> list = activityManager.getActivityBySelective(act);
			if (Collections3.isNotEmpty(list)) {
				ResultDO<Object> returnDO = activityLotteryManager.createRedBagUrlFromTransaction(list.get(0), transactionId);
				if (returnDO.isError()) {
					rDO.setResultCode(returnDO.getResultCode());
					return rDO;
				}
				PopularityRedBag redBag = JSON.parseObject(list.get(0).getObtainConditionsJson(), PopularityRedBag.class);
				String redBagCode = PropertiesUtil.getProperties("springFestival.share.redBag.url") + returnDO.getResult();
				PopularityRedBagBiz biz = new PopularityRedBagBiz();
				biz.setRedBagCode(redBagCode);
				biz.setWechatShareFriends(redBag.getWechatShareFriends());
				biz.setWechatShareCircle(redBag.getWechatShareCircle());
				rDO.setResult(biz);
				return rDO;
			} else {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
			}
		} catch (Exception e) {
			logger.error("生成红包链接失败 transactionId={}", transactionId, e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}

	@Override
	public Object getRedBagRule() {
		ResultDO<Object> rDO = new ResultDO<Object>();
		Long activityId = null;
		try {
			Activity act = new Activity();
			act.setActivityStatus(StatusEnum.ACTIVITY_STATUS_IS_START.getStatus());
			act.setActivityDesc(Constant.TRANSACTION_REDBAG_DESC);
			List<Activity> list = activityManager.getActivityBySelective(act);
			if (Collections3.isNotEmpty(list)) {
				activityId = list.get(0).getId();
				RedBagRuleBiz biz = JSON.parseObject(list.get(0).getObtainConditionsJson(), RedBagRuleBiz.class);
				biz.setStartTime(list.get(0).getStartTime());
				biz.setEndTime(list.get(0).getEndTime());
				rDO.setResult(biz);
				return rDO;
			}
		} catch (Exception e) {
			logger.error("获取红包规则失败 activityId={}", activityId, e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}

	@Override
	public void activityAfterTransaction(Transaction t) {
		try {
			activityAfterTransactionManager.afterTransactionEntry(t);
		} catch (Exception e) {
			Long transactionId = 0l;
			if (t != null)
				transactionId = t.getId();
			logger.error("处理交易后的活动业务失败 transactionId={}", transactionId, e);
		}
	}

	@Override
	public Object breakBillionInit() {
		ResultDO<ActivityForMillionCoupon> rDO = new ResultDO<ActivityForMillionCoupon>();
		ActivityForMillionCoupon model = new ActivityForMillionCoupon();
		try {
			// 破十亿活动
			Optional<Activity> optOfBreakBillion = LotteryContainer.getInstance().getActivityByName(
					ActivityConstant.ACTIVITY_BREAK_BILLION_NAME);
			if (!optOfBreakBillion.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			model.setStartDate(DateUtils.getStrFromDate(optOfBreakBillion.get().getStartTime(), DateUtils.DATE_FMT_4));
			model.setActivityStatus(optOfBreakBillion.get().getActivityStatus());
			int fund = 0;
			if (model.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_AUDIT.getStatus()) {
				fund = Integer.valueOf(ActivityConstant.ACTIVITY_BREAK_BILLION_COUPONAMOUNT_LIMIT);
			} else if (model.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
				fund = RedisActivityClient.getActivityCouponAmountLimit(optOfBreakBillion.get().getId(),
						ActivityConstant.ACTIVITY_BREAK_BILLION_COUPONAMOUNT_LIMIT).intValue();
				// 负数不显示
				if (fund < 0)
					fund = 0;
				if (fund == 0)
					model.setActivityStatus(StatusEnum.ACTIVITY_STATUS_IS_END.getStatus());
			}
			model.setFund(fund);
			String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR + ActivityConstant.ACTIVITY_BREAK_BILLION;
			boolean isExit = RedisManager.isExitByObjectKey(key);
			List<ActivityLotteryResultBiz> list = null;
			if (isExit) {
				list = (List<ActivityLotteryResultBiz>) RedisManager.getObject(key);
				model.setInvestList(list);
			} else {
				getInvestList(optOfBreakBillion.get().getId(), model, key);
			}
			rDO.setResult(model);
		} catch (Exception e) {
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("百万现金券活动初始化失败", e);
		}
		return rDO;
	}

	@Override
	public ResultDO<ActivityHistoryBiz> getNewerMissionList() {
		ResultDO<ActivityHistoryBiz> rDO = new ResultDO<ActivityHistoryBiz>();
		try {
			// 获取新手任务列表
			String listKey = RedisConstant.REDIS_KEY_NEWER_MISSION_LIST;
			boolean isExit = RedisManager.isExitByObjectKey(listKey);
			List<ActivityHistoryBiz> list = null;
			if (isExit) {
				list = (List<ActivityHistoryBiz>) RedisManager.getObject(listKey);
			} else {
				list = activityHistoryManager.getNewerPrizeList(40);
				if (Collections3.isNotEmpty(list)) {
					for (ActivityHistoryBiz biz : list) {
						biz.setMemberName(ServletUtil.getMemberUserName(biz.getMemberId()));
						biz.setAvatar(ServletUtil.getMemberAvatarById(biz.getMemberId()));
						biz.setMemberId(null);
					}
					RedisManager.putObject(listKey, list);
					RedisManager.expireObject(listKey, 120);
				} else {
					rDO.setResultCode(ResultCode.ERROR_SYSTEM_DATABASE_QUERY_NULL);
					return rDO;
				}
			}
			rDO.setResultList(list);
		} catch (ManagerException e) {
			logger.error("获取新手任务动态失败", e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}

	@Override
	public Object house2GiftsInit() {
		ResultDO<ActivityForMillionCoupon> rDO = new ResultDO<ActivityForMillionCoupon>();
		ActivityForMillionCoupon model = new ActivityForMillionCoupon();
		try {
			Optional<Activity> optOfGift1 = LotteryContainer.getInstance().getActivityByName(ActivityConstant.ACTIVITY_HOUSE1GIFT_NAME);
			Optional<Activity> optOfGift2 = LotteryContainer.getInstance().getActivityByName(ActivityConstant.ACTIVITY_HOUSE2GIFT_NAME);
			if (!optOfGift1.isPresent() || !optOfGift2.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			Activity gift1 = optOfGift1.get();
			Activity gift2 = optOfGift2.get();
			model.setStartTime(gift1.getStartTime());
			model.setActivityStatus(gift1.getActivityStatus());
			int fund1 = 0;
			int fund2 = 0;
			Activity nowAct = gift1;
			if (model.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_AUDIT.getStatus()) {
				fund1 = Integer.valueOf(gift1.getObtainConditionsJson());
				fund2 = Integer.valueOf(gift2.getObtainConditionsJson());
				model.setFund1(fund1);
				model.setFund2(fund2);
				rDO.setResult(model);
				return rDO;
			} else if (model.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
				fund1 = RedisActivityClient.getActivityCouponAmountLimit(gift1.getId(), gift1.getObtainConditionsJson()).intValue();
				fund2 = RedisActivityClient.getActivityCouponAmountLimit(gift2.getId(), gift2.getObtainConditionsJson()).intValue();
				// 负数不显示
				if (fund1 <= 0) {
					fund1 = 0;
					nowAct = gift2;
				}
				if (fund2 <= 0) {
					fund2 = 0;
				}
			}
			model.setFund1(fund1);
			model.setFund2(fund2);
			String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR + ActivityConstant.ACTIVITY_HOUSE2GIFT_LIST
					+ RedisConstant.REDIS_SEPERATOR + nowAct.getId();
			boolean isExit = RedisManager.isExitByObjectKey(key);
			List<ActivityLotteryResultBiz> list = null;
			if (isExit) {
				list = (List<ActivityLotteryResultBiz>) RedisManager.getObject(key);
				model.setInvestList(list);
			} else {
				getInvestList(nowAct.getId(), model, key);
			}
			rDO.setResult(model);
		} catch (Exception e) {
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("投房有礼两重礼初始化失败", e);
		}
		return rDO;
	}

	@Override
	public Object mayDay4GiftsInit(Optional<MemberSessionDto> optMember) {
		ResultDO<ActivityForMayDay4Gifts> rDO = new ResultDO<ActivityForMayDay4Gifts>();
		ActivityForMayDay4Gifts model = new ActivityForMayDay4Gifts();
		rDO.setResult(model);
		try {
			Optional<Activity> optOfMayDay = LotteryContainer.getInstance().getActivityByName(ActivityConstant.ACTIVITY_MAYDAY_4GIFTS_NAME);
			if (!optOfMayDay.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			Activity mayDay = optOfMayDay.get();
			model.setActivityStatus(mayDay.getActivityStatus());
			model.setStartTime(mayDay.getStartTime());
			model.setEndTime(mayDay.getEndTime());
			ActivityForMayDay4Gifts rule = LotteryContainer.getInstance().getObtainConditions(mayDay, ActivityForMayDay4Gifts.class,
					ActivityConstant.ACTIVITY_MAYDAY_4GIFTS_KEY);
			model.setReceiveStartTime(rule.getReceiveStartTime());
			model.setReceiveEndTime(rule.getReceiveEndTime());
			model.setTotalInvestLv1(rule.getTotalInvestLv1());
			model.setTotalInvestLv2(rule.getTotalInvestLv2());
			model.setTotalInvestLv3(rule.getTotalInvestLv3());
			String receiveEndDate = DateUtils.getDateStrFromDate(mayDay.getEndTime()) + " " + rule.getReceiveEndTime();
			model.setLastReceiveEndTime(DateUtils.getDateTimeFromString(receiveEndDate));
			if (!optMember.isPresent() || StatusEnum.ACTIVITY_STATUS_IS_START.getStatus() != mayDay.getActivityStatus()) {
				return rDO;
			}
			// 用户已登录，查询当天总投资额
			Date nowDate = DateUtils.getCurrentDate();
			Date startTime = DateUtils.zerolizedTime(nowDate);
			Date endTime = DateUtils.getEndTime(nowDate);
			BigDecimal totalAmount = getTotalAmountByMemberId(optMember.get().getId(), startTime, endTime);
			model.setInvestAmount(totalAmount);
			// 判断券的领用情况
			String cycleStr = DateUtils.getDateStrFromDate(nowDate);
			Map<String, Object> queryMap = Maps.newHashMap();
			queryMap.put("memberId", optMember.get().getId());
			queryMap.put("activityId", mayDay.getId());
			// 30元券
			queryMap.put("cycleConstraint", cycleStr + "-30");
			ActivityLottery alFor30 = activityLotteryManager.selectByMemberActivity(queryMap);
			if (alFor30 != null) {
				model.setCouponForDayFlag(true);
			}
			// 88元券
			queryMap.put("cycleConstraint", cycleStr + "-88");
			ActivityLottery alForLv1 = activityLotteryManager.selectByMemberActivity(queryMap);
			if (alForLv1 != null) {
				model.setCouponForLv1(true);
			}
			// 188元券
			queryMap.put("cycleConstraint", cycleStr + "-188");
			ActivityLottery alForLv2 = activityLotteryManager.selectByMemberActivity(queryMap);
			if (alForLv2 != null) {
				model.setCouponForLv2(true);
			}
			// 500元券
			queryMap.put("cycleConstraint", cycleStr + "-500");
			ActivityLottery alForLv3 = activityLotteryManager.selectByMemberActivity(queryMap);
			if (alForLv3 != null) {
				model.setCouponForLv3(true);
			}
		} catch (Exception e) {
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("五一四重礼初始化失败", e);
		}
		return rDO;
	}


	@Override
	public Object mayDay4GiftsReceive(Long memberId, int couponAmount) {
		ResultDO<ActivityForMayDay4Gifts> rDO = new ResultDO<ActivityForMayDay4Gifts>();
		try {
			Optional<Activity> optOfMayDay = LotteryContainer.getInstance().getActivityByName(ActivityConstant.ACTIVITY_MAYDAY_4GIFTS_NAME);
			if (!optOfMayDay.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			Activity mayDay = optOfMayDay.get();
			if (StatusEnum.ACTIVITY_STATUS_IS_START.getStatus() != mayDay.getActivityStatus()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
				return rDO;
			}
			ActivityForMayDay4Gifts rule = LotteryContainer.getInstance().getObtainConditions(mayDay, ActivityForMayDay4Gifts.class,
					ActivityConstant.ACTIVITY_MAYDAY_4GIFTS_KEY);
			// 获取用户当前投资总额
			Date nowDate = DateUtils.getCurrentDate();
			Date startTime = DateUtils.zerolizedTime(nowDate);
			Date endTime = DateUtils.getEndTime(nowDate);
			BigDecimal totalAmount = getTotalAmountByMemberId(memberId, startTime, endTime);
			// 根据前端面额领券
			String cycleStr = DateUtils.getDateStrFromDate(nowDate);
			RewardsBase rBase = new RewardsBase();
			rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
			rBase.setRewardName(couponAmount + ActivityConstant.couponDesc);
			rBase.setRewardValue(couponAmount);
			switch (couponAmount) {
			case 30:
				String startTimeStr = cycleStr + " " + rule.getReceiveStartTime();
				String endTimeStr = cycleStr + " " + rule.getReceiveEndTime();
				Date receiveStartTime = DateUtils.getDateFromString(startTimeStr, DateUtils.TIME_PATTERN);
				Date receiveEndTime = DateUtils.getDateFromString(endTimeStr, DateUtils.TIME_PATTERN);
				// 30元券
				String cycleStrFor30 = cycleStr + "-30";
				if (DateUtils.isDateBetween(DateUtils.getCurrentDate(), receiveStartTime, receiveEndTime)) {
					rBase.setTemplateId(rule.getTemplateIdForDay());
					receiveCouponCommon(memberId, mayDay, rBase, cycleStrFor30, rDO);
				} else {
					rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
				}
				return rDO;
			case 88:
				// 88元券
				String cycleStrForLv1 = cycleStr + "-88";
				if (totalAmount.compareTo(rule.getTotalInvestLv1()) > -1) {
					rBase.setTemplateId(rule.getTemplateIdForLv1());
					receiveCouponCommon(memberId, mayDay, rBase, cycleStrForLv1, rDO);
				} else {
					rDO.setResultCode(ResultCode.ACTIVITY_NOT_MEET_INVEST_CONDITIONS_ERROR);
				}
				return rDO;
			case 188:
				// 188元券
				String cycleStrForLv2 = cycleStr + "-188";
				if (totalAmount.compareTo(rule.getTotalInvestLv2()) > -1) {
					rBase.setTemplateId(rule.getTemplateIdForLv2());
					receiveCouponCommon(memberId, mayDay, rBase, cycleStrForLv2, rDO);
				} else {
					rDO.setResultCode(ResultCode.ACTIVITY_NOT_MEET_INVEST_CONDITIONS_ERROR);
				}
				return rDO;
			case 500:
				// 188元券
				String cycleStrForLv3 = cycleStr + "-500";
				if (totalAmount.compareTo(rule.getTotalInvestLv3()) > -1) {
					rBase.setTemplateId(rule.getTemplateIdForLv3());
					receiveCouponCommon(memberId, mayDay, rBase, cycleStrForLv3, rDO);
				} else {
					rDO.setResultCode(ResultCode.ACTIVITY_NOT_MEET_INVEST_CONDITIONS_ERROR);
				}
				return rDO;
			default:
				rDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
				return rDO;
			}
		} catch (Exception e) {
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("五一四重礼领券失败", e);
		}
		return rDO;
	}

	/**
	 * 
	 * @Description:获取时间范围内的用户总投资额
	 * @param memberId
	 * @param startTime
	 * @param endTime
	 * @return
	 * @author: wangyanji
	 * @throws ManagerException
	 * @time:2016年4月18日 下午3:24:16
	 */
	private BigDecimal getTotalAmountByMemberId(Long memberId, Date startTime, Date endTime) throws ManagerException {
		TransactionQuery query = new TransactionQuery();
		query.setMemberId(memberId);
		query.setTransactionStartTime(startTime);
		query.setTransactionEndTime(endTime);
		return transactionManager.getMemberTotalInvest(query);
	}

	/**
	 * 
	 * @Description:专题页领券通用方法
	 * @param memberId
	 * @param activity
	 * @param templateId
	 * @param rDO
	 * @author: wangyanji
	 * @throws Exception
	 * @time:2016年4月18日 下午6:12:24
	 */
	private void receiveCouponCommon(Long memberId, Activity activity, RewardsBase rewardsBase, String cycleStr,
			ResultDO<?> rDO) throws Exception {
		// 领取
		RuleBody ruleBody = new RuleBody();
		ruleBody.setActivityId(activity.getId());
		ruleBody.setMemberId(memberId);
		ruleBody.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
		ruleBody.setCycleStr(cycleStr);
		ruleBody.setActivityName(activity.getActivityName());
		if (drawByPrizeDirectly.validate(ruleBody, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
			drawByPrizeDirectly.drawLottery(ruleBody, rewardsBase, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode());
			// 发送站内信
			MessageClient.sendMsgForSPEngin(memberId, ruleBody.getActivityName(), rewardsBase.getRewardName());
		} else {
			rDO.setResultCode(ResultCode.ACTIVITY_YET_JOIN_ACTIVITY_ERROR);
		}
	}

	@Override
	public Object fellInLoveFor520Init(Optional<MemberSessionDto> optMember) {
		ResultDO<ActivityFor520> rDO = new ResultDO<ActivityFor520>();
		ActivityFor520 model = new ActivityFor520();
		rDO.setResult(model);
		try {
			Optional<Activity> optOfActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_FELL_IN_LOVE_NAME);
			if (!optOfActivity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			Activity activity = optOfActivity.get();
			ActivityFor520 rule = LotteryContainer.getInstance().getObtainConditions(activity, ActivityFor520.class,
					ActivityConstant.ACTIVITY_FELL_IN_LOVE_NAME);
			model.setStartTime(activity.getStartTime());
			model.setEndTime(activity.getEndTime());
			model.setActivityStatus(activity.getActivityStatus());
			model.setReceiveGiftPacksAmount(rule.getReceiveGiftPacksAmount());
			if (StatusEnum.ACTIVITY_STATUS_IS_AUDIT.getStatus() == activity.getActivityStatus()) {
				model.setCouponNumber(rule.getCouponNumber());
				return rDO;
			}
			// 活动三排行榜
			String startTime = DateUtils.getStrFromDate(activity.getStartTime(), DateUtils.TIME_PATTERN);
			String endTime = DateUtils.getStrFromDate(activity.getEndTime(), DateUtils.TIME_PATTERN);
			List<ActivityForRankListBiz> rankList = getMemberMeetTotalInvestRange(startTime, endTime, 10, "asc");
			model.setRankList(rankList);
			// 活动二校验
			if (optMember.isPresent()) {
				Long memberId = optMember.get().getId();
				BigDecimal memberTotalAmount = getTotalAmountByMemberId(memberId, activity.getStartTime(), activity.getEndTime());
				model.setMemberTotalAmount(memberTotalAmount);
			}
			if (StatusEnum.ACTIVITY_STATUS_IS_END.getStatus() == activity.getActivityStatus()) {
				model.setCouponNumber(0l);
				return rDO;
			}
			// 活动一校验
			Date nowDate = DateUtils.getCurrentDate();
			String key = activity.getId().toString() + DateUtils.getYear(nowDate) + DateUtils.getMonth(nowDate)
					+ DateUtils.getDate(nowDate);
			Long fund = RedisActivityClient.getActivityCouponAmountLimit(Long.valueOf(key), rule.getCouponNumber().toString());
			if (fund < 0l) {
				fund = 0l;
			}
			model.setCouponNumber(fund);
		} catch (Exception e) {
			logger.error("520活动初始化失败", e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}

	@Override
	public Object break2BillionInit(Optional<MemberSessionDto> optMember) {
		ResultDO<ActivityForBreak2Billion> rDO = new ResultDO<ActivityForBreak2Billion>();
		ActivityForBreak2Billion model = new ActivityForBreak2Billion();
		rDO.setResult(model);
		try {
			Optional<Activity> optOfActivity = LotteryContainer.getInstance().getActivityByName(
					ActivityConstant.ACTIVITY_2_BREAK_BILLION_NAME);
			if (!optOfActivity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			Activity activity = optOfActivity.get();
			ActivityForBreak2Billion rule = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForBreak2Billion.class,
					ActivityConstant.ACTIVITY_2_BREAK_BILLION);
			model.setStartTime(activity.getStartTime());
			model.setEndTime(activity.getEndTime());
			model.setActivityStatus(activity.getActivityStatus());
			model.setGiftOutTime(rule.getGiftOutTime());
			// 判断20亿有没有到
			if (StringUtil.isBlank(rule.getAmountUnit())) {
				rDO.setResultCode(ResultCode.ERROR_SYSTEM);
				return rDO;
			}
			// 活动未开始，直接返回
			if (StatusEnum.ACTIVITY_STATUS_IS_AUDIT.getStatus() == activity.getActivityStatus()) {
				model.setFund(rule.getFund());
				return rDO;
			}
			// 已经破亿，活动结束
			model.setGiftLevel(rule.getGiftLevel());
			String key = RedisConstant.REDIS_PLATFORM_TOTAL_INVEST_TIME;
			String break2BillionTimeStr = RedisManager.hget(key, rule.getAmountUnit());
			if (StringUtil.isNotBlank(break2BillionTimeStr)) {
				model.setHasBreak(true);
				model.setBreak2BillionTime(DateUtils.getDateFromString(break2BillionTimeStr, DateUtils.TIME_PATTERN_SESSION));
				activity.setEndTime(model.getBreak2BillionTime());
				LotteryContainer.getInstance().getActivityStatusFromTime(activity);
				model.setEndTime(activity.getEndTime());
				model.setActivityStatus(activity.getActivityStatus());
				model.setFund(0);
			} else {
				Long fund = RedisActivityClient.getActivityCouponAmountLimit(activity.getId(), rule.getFund().toString());
				// 负数不显示
				if (fund < 0)
					fund = 0l;
				model.setFund(Integer.valueOf(fund.toString()));
			}
			// 获取投资列表
			String startTime = DateUtils.getStrFromDate(activity.getStartTime(), DateUtils.TIME_PATTERN);
			String endTime = DateUtils.getStrFromDate(activity.getEndTime(), DateUtils.TIME_PATTERN);
			List<ActivityForRankListBiz> rankList = getMemberMeetTotalInvestRange(startTime, endTime, 10, "asc");
			model.setRankList(rankList);
			// 抽奖列表
			break2BillionLotteryList(activity, 40, model);
			if (!optMember.isPresent()) {
				return rDO;
			}
			// 登录会员判断是否抽奖
			Long memberId = optMember.get().getId();
			RuleBody rb = new RuleBody();
			rb.setActivityId(activity.getId());
			rb.setMemberId(memberId);
			rb.setCycleStr(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate()));
			if (!verificationByParticipate.validate(rb)) {
				model.setHasLottery(true);
			}
			// 是否领取礼包
			rb.setActivityId(activity.getId());
			rb.setMemberId(memberId);
			rb.setCycleStr(activity.getId().toString());
			if (!verificationByParticipate.validate(rb)) {
				model.setHasReceiveGift(true);
			}
			// 活动期间总投资额
			BigDecimal memberTotalAmount = getTotalAmountByMemberId(memberId, activity.getStartTime(), activity.getEndTime());
			model.setMemberTotalAmount(memberTotalAmount);
			// 当日投资额
			if (StatusEnum.ACTIVITY_STATUS_IS_START.getStatus() == activity.getActivityStatus()) {
				Date dayStartTime = DateUtils.zerolizedTime(DateUtils.getCurrentDate());
				if (dayStartTime.before(activity.getStartTime())) {
					dayStartTime = activity.getStartTime();
				}
				Date dayEndTime = DateUtils.getEndTime(DateUtils.getCurrentDate());
				if (dayEndTime.after(activity.getEndTime())) {
					dayEndTime = activity.getEndTime();
				}
				BigDecimal memberDayAmount = getTotalAmountByMemberId(memberId, dayStartTime, dayEndTime);
				model.setMemberDayAmount(memberDayAmount);
			}
		} catch (Exception e) {
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("破20亿活动初始化失败", e);
		}
		return rDO;
	}

	@SuppressWarnings("unchecked")
	private void break2BillionLotteryList(Activity activity, int rowNum, ActivityForBreak2Billion model) throws Exception {
		String key = ActivityConstant.ACTIVITY_2_BREAK_BILLION_LOTTERY;
		boolean isExit = RedisManager.isExitByObjectKey(key);
		List<ActivityLotteryResultBiz> list = null;
		if (isExit) {
			list = (List<ActivityLotteryResultBiz>) RedisManager.getObject(key);
		} else {
			List<ActivityLotteryResult> modelList = activityLotteryManager.queryNewLotteryResult(activity.getId(),
					ActivityConstant.ACTIVITY_2_BREAK_BILLION_PART2, rowNum);
			if (CollectionUtils.isNotEmpty(modelList)) {
				list = BeanCopyUtil.mapList(modelList, ActivityLotteryResultBiz.class);
				for (ActivityLotteryResultBiz biz : list) {
					biz.setMemberName(ServletUtil.getMemberUserName(biz.getMemberId()));
					biz.setAvatar(ServletUtil.getMemberAvatarById(biz.getMemberId()));
					biz.setMemberId(null);
				}
				RedisManager.putObject(key, list);
				RedisManager.expireObject(key, 60);
			}
		}
		model.setLotteryList(list);
	}

	@Override
	public Object break2BillionReceive(Long memberId, int activityPart) {
		ResultDO<ActivityForBreak2Billion> rDO = new ResultDO<ActivityForBreak2Billion>();
		ActivityForBreak2Billion model = new ActivityForBreak2Billion();
		rDO.setResult(model);
		try {
			if (memberId == null || activityPart < 1 || activityPart > 2) {
				rDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
				return rDO;
			}
			Optional<Activity> optOfActivity = LotteryContainer.getInstance().getActivityByName(
					ActivityConstant.ACTIVITY_2_BREAK_BILLION_NAME);
			if (!optOfActivity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			Activity activity = optOfActivity.get();
			ActivityForBreak2Billion rule = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForBreak2Billion.class,
					ActivityConstant.ACTIVITY_2_BREAK_BILLION);
			// 已经破亿，活动结束
			String key = RedisConstant.REDIS_PLATFORM_TOTAL_INVEST_TIME;
			String break2BillionTimeStr = RedisManager.hget(key, rule.getAmountUnit());
			if (StringUtil.isNotBlank(break2BillionTimeStr)) {
				model.setHasBreak(true);
				model.setBreak2BillionTime(DateUtils.getDateFromString(break2BillionTimeStr, DateUtils.TIME_PATTERN_SESSION));
				activity.setEndTime(model.getBreak2BillionTime());
				LotteryContainer.getInstance().getActivityStatusFromTime(activity);
				model.setEndTime(activity.getEndTime());
				model.setActivityStatus(activity.getActivityStatus());
				model.setFund(0);
			}
			if (activityPart == 1) {
				// 每日抽奖
				break2BillionLottery(memberId, rDO, activity, rule, model);
			} else {
				// 20亿礼包
				break2BillionGift(memberId, rDO, activity, rule, model);
			}
		} catch (Exception e) {
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("破二十亿活动互动失败, memberId={}, activityPart={}", memberId, activityPart, e);
		}
		return rDO;
	}

	private void break2BillionLottery(Long memberId, ResultDO<ActivityForBreak2Billion> rDO, Activity activity,
			ActivityForBreak2Billion rule, ActivityForBreak2Billion model) throws Exception {
		if (StatusEnum.ACTIVITY_STATUS_IS_START.getStatus() != activity.getActivityStatus()) {
			rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
			return;
		}
		// 获取当日总投资额
		Date startTime = DateUtils.zerolizedTime(DateUtils.getCurrentDate());
		Date endTime = DateUtils.getEndTime(DateUtils.getCurrentDate());
		if (startTime.before(activity.getStartTime())) {
			startTime = activity.getStartTime();
		}
		if (endTime.after(activity.getEndTime())) {
			endTime = activity.getEndTime();
		}
		BigDecimal memberTotalAmount = getTotalAmountByMemberId(memberId, startTime, endTime);
		if (memberTotalAmount.intValue() < rule.getLotteryMinInvest()) {
			rDO.setResultCode(ResultCode.ACTIVITY_NOT_MEET_INVEST_CONDITIONS_ERROR);
			return;
		}
		// 开始抽奖
		RuleBody rb = new RuleBody();
		rb.setActivityName(activity.getActivityDesc());
		rb.setActivityId(activity.getId());
		rb.setMemberId(memberId);
		rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
		rb.setCycleStr(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate()));
		rb.setDeductRemark(ActivityConstant.ACTIVITY_2_BREAK_BILLION_PART2);
		rb.setRewardsAvailableNum(8);
		rb.setRewardsPoolMaxNum(8);
		// 校验
		if (drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
			// 抽奖
			RewardsBodyForProbility rfp = (RewardsBodyForProbility) drawByProbability.drawLottery(rb, null,
					TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode());
			model.setLotteryRewardName(rfp.getRewardName());
			model.setLotteryRewardCode(rfp.getRewardCode());
			// 发送站内信
			MessageClient.sendMsgForSPEngin(memberId, activity.getActivityDesc(), rfp.getRewardName());
			String key = ActivityConstant.ACTIVITY_2_BREAK_BILLION_LOTTERY;
			RedisManager.removeObject(key);
			return;
		} else {
			rDO.setResultCode(ResultCode.ACTIVITY_CURRENT_YET_JOIN_ACTIVITY_ERROR);
			return;
		}
	}

	private void break2BillionGift(Long memberId, ResultDO<ActivityForBreak2Billion> rDO, Activity activity, ActivityForBreak2Billion rule,
			ActivityForBreak2Billion model) throws Exception {
		if (StatusEnum.ACTIVITY_STATUS_IS_END.getStatus() != activity.getActivityStatus() || model.getBreak2BillionTime() == null) {
			rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_ERROR);
			return;
		}
		Date outTime = DateUtils.addHour(model.getBreak2BillionTime(), rule.getGiftOutTime());
		if (DateUtils.getCurrentDate().after(outTime)) {
			rDO.setResultCode(ResultCode.ACTIVITY_XIAOMING_STORY_YET_END_ERROR);
			return;
		}
		// 获取总投资额
		BigDecimal memberTotalAmount = getTotalAmountByMemberId(memberId, activity.getStartTime(), model.getBreak2BillionTime());
		if (memberTotalAmount.intValue() < rule.getGiftLevel().get(0)) {
			rDO.setResultCode(ResultCode.ACTIVITY_REDBAG_LESSINVEST_ERROR);
			return;
		}
		// 发放礼包
		String cycleConstraint = activity.getId().toString();
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("activityId", activity.getId());
		paraMap.put("memberId", memberId);
		paraMap.put("cycleConstraint", cycleConstraint);
		ActivityLottery al = activityLotteryManager.selectByMemberActivity(paraMap);
		if (al != null) {
			rDO.setResultCode(ResultCode.ACTIVITY_REDBAG_JOINED_ERROR);
			return;
		}
		// 送礼包
		ActivityLottery activityLottery = new ActivityLottery();
		activityLottery.setActivityId(activity.getId());
		activityLottery.setMemberId(memberId);
		activityLottery.setCycleConstraint(cycleConstraint);
		activityLottery.setTotalCount(1);
		activityLottery.setRealCount(0);
		paraMap.put("cycleConstraint", cycleConstraint);
		try {
			activityLotteryManager.insertActivityLottery(activityLottery);
		} catch (Exception e) {
			logger.error("20亿礼包重复领取, memberId={}, cycleKey={}", memberId, cycleConstraint);
			rDO.setResultCode(ResultCode.ACTIVITY_REDBAG_JOINED_ERROR);
			return;
		}
		// 发放礼包
		String prizeInfo = null;
		int index = 0;
		for (int size = rule.getGiftLevel().size(); index < size; index++) {
			if (index == size - 1) {
				break;
			}
			if (memberTotalAmount.intValue() < rule.getGiftLevel().get(index + 1)) {
				break;
			}
		}
		prizeInfo = rule.getGiftPrizeLevel().get(index);
		List<String> templateIds = Lists.newArrayList(prizeInfo.split(ActivityConstant.redBagCodeSplit));
		if (Collections3.isEmpty(templateIds)) {
			logger.error("解析破亿红包失败");
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
			return;
		}
		for (String templateIdStr : templateIds) {
			// 收益券or现金券
			Long templateId = Long.valueOf(templateIdStr);
			Coupon c = couponManager.receiveCoupon(memberId, activity.getId(), templateId, -1L);
			if (c == null) {
				// 优惠券赠送失败;
				logger.error("优惠券赠送失败，接口返回Coupon=null!, memberId={}, activityId={}, templateId={}", memberId,
						activity.getId(), templateId);
			}
		}
		// 站内信
		MessageClient.sendMsgForSPEngin(memberId, activity.getActivityDesc(), ActivityConstant.ACTIVITY_2_BREAK_BILLION_PART3);
		model.setGiftIndex(index);
	}

	@Override
	public Object inviteFriendInit(Optional<MemberSessionDto> optMember) {
		ResultDO<ActivityForFriendShip> rDO = new ResultDO<ActivityForFriendShip>();
		ActivityForFriendShip model = new ActivityForFriendShip();
		rDO.setResult(model);
		try {
			Optional<Activity> optOfActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_FRIEND_SHIP_YEARS);
			if (!optOfActivity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			Activity activity = optOfActivity.get();
			model.setStartTime(activity.getStartTime());
			model.setActivityStatus(activity.getActivityStatus());
			if (StatusEnum.ACTIVITY_STATUS_IS_AUDIT.getStatus() == activity.getActivityStatus()) {
				return rDO;
			}
			// 加载排行榜
			loadFriendShipTopList(activity, model);
			if(!optMember.isPresent()) {
				return rDO;
			}
			Long memberId = optMember.get().getId();
			model.setAvatar(ServletUtil.getMemberAvatarById(memberId));
			// 加载用户信息
			Map<String, Object> paraMap = new HashMap<String, Object>();
			paraMap.put("activityId", activity.getId());
			paraMap.put("memberId", memberId);
			paraMap.put("cycleConstraint", activity.getId().toString());
			ActivityLottery km = activityLotteryManager.selectByMemberActivity(paraMap);
			if (km == null) {
				model.setTotalKm(0);
				model.setPackages(0);
				model.setBoxes(0);
				return rDO;
			}
			model.setTotalKm(km.getTotalCount());
			paraMap.put("cycleConstraint", "packages");
			// 获取已获得的现金券数
			ActivityLotteryResult query = new ActivityLotteryResult();
			query.setActivityId(activity.getId());
			query.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
			query.setMemberId(memberId);
			int couponNum = activityLotteryResultManager.sumRewrdsByMemberActivity(query);
			model.setTotalCoupon(couponNum);
			// 获取人气值总额
			query.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_POPULARITY.getType());
			int popNum = activityLotteryResultManager.sumRewrdsByMemberActivity(query);
			model.setTotalPop(popNum);
			ActivityLottery packages = activityLotteryManager.selectByMemberActivity(paraMap);
			model.setPackages(packages.getRealCount());
			paraMap.put("cycleConstraint", "boxes");
			ActivityLottery boxes = activityLotteryManager.selectByMemberActivity(paraMap);
			model.setBoxes(boxes.getRealCount());
		} catch (Exception e) {
			logger.error("里程拉新初始化失败", e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}

	/**
	 * 
	 * @Description:里程拉新排行榜
	 * @param activity
	 * @param model
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年6月11日 下午9:57:00
	 */
	@SuppressWarnings("unchecked")
	private void loadFriendShipTopList(Activity activity, ActivityForFriendShip model) throws ManagerException {
		String key = ActivityConstant.ACTIVITY_FRIEND_SHIP_TOP_KEY;
		boolean isExit = RedisManager.isExitByObjectKey(key);
		List<ActivityLotteryResultBiz> list = null;
		if (isExit) {
			list = (List<ActivityLotteryResultBiz>) RedisManager.getObject(key);
		} else {
			ActivityLotteryResult alr = new ActivityLotteryResult();
			alr.setActivityId(activity.getId());
			alr.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_OTHER.getType());
			List<ActivityLotteryResult> modelList = activityLotteryResultManager.topByRewardType(alr);
			if (CollectionUtils.isNotEmpty(modelList)) {
				list = BeanCopyUtil.mapList(modelList, ActivityLotteryResultBiz.class);
				for (ActivityLotteryResultBiz biz : list) {
					ActivityLotteryResult query = new ActivityLotteryResult();
					biz.setMemberName(ServletUtil.getMemberUserName(biz.getMemberId()));
					biz.setAvatar(ServletUtil.getMemberAvatarById(biz.getMemberId()));
					// 获取现金券总额
					query.setActivityId(activity.getId());
					query.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
					query.setMemberId(biz.getMemberId());
					int couponNum = activityLotteryResultManager.sumRewrdsByMemberActivity(query);
					// 获取人气值总额
					query.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_POPULARITY.getType());
					int popNum = activityLotteryResultManager.sumRewrdsByMemberActivity(query);
					biz.setMemberId(null);
					biz.setRemark("奖励" + couponNum + ActivityConstant.couponDesc + "及" + popNum + ActivityConstant.popularityDesc);
					biz.setMemberId(null);
				}
				RedisManager.putObject(key, list);
				RedisManager.expireObject(key, 120);
			}
		}
		model.setTopList(list);
	}

	@Override
	public Object inviteFriendReceive(Long memberId, int rewardType) {
		ResultDO<ActivityForFriendShip> rDO = new ResultDO<ActivityForFriendShip>();
		ActivityForFriendShip model = new ActivityForFriendShip();
		rDO.setResult(model);
		try {
			Optional<Activity> optOfActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_FRIEND_SHIP_YEARS);
			if (!optOfActivity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			Activity activity = optOfActivity.get();
			if (activity.getActivityStatus() != StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
				return rDO;
			}
			if (memberId < 1l || rewardType < 1 || rewardType > 2) {
				rDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
				return rDO;
			}
			ActivityForFriendShip rule = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForFriendShip.class,
					ActivityConstant.ACTIVITY_FRIEND_SHIP_KEY);
			// 领取幸运背包
			if (rewardType == 1) {
				int num = activityLotteryManager.updateRealCount(activity.getId(), memberId, "packages");
				if (num == 0) {
					rDO.setResultCode(ResultCode.ACTIVITY_INVITE_FRIEND_KM_ERROR);
					return rDO;
				}
				// 发放奖励
				int value = RandomUtils.getRandomNumberByRange(rule.getPackagesRange().get(0), rule.getPackagesRange().get(1));
				RuleBody ruleBody = new RuleBody();
				ruleBody.setActivityId(activity.getId());
				ruleBody.setMemberId(memberId);
				ruleBody.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_COIN.getStatus());
				ruleBody.setActivityName(activity.getActivityDesc());
				ruleBody.setCycleStr("packages");
				ruleBody.setDeductRemark("packages");
				ruleBody.setRewardId(activity.getId().toString());
				RewardsBase rBase = new RewardsBase();
				rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_POPULARITY.getType());
				rBase.setRewardName(value + ActivityConstant.popularityDesc);
				rBase.setRewardValue(value);
				drawByPrizeDirectly.drawLottery(ruleBody, rBase, "");
				MessageClient.sendMsgForSPEngin(memberId, activity.getActivityDesc(), "幸运背包奖励");
				model.setReceiveReturnStr(rBase.getRewardName());
			}
			// 领取宝箱
			if (rewardType == 2) {
				// 加载用户信息
				Map<String, Object> paraMap = new HashMap<String, Object>();
				paraMap.put("activityId", activity.getId());
				paraMap.put("memberId", memberId);
				paraMap.put("cycleConstraint", "boxes");
				ActivityLottery boxes = activityLotteryManager.selectByMemberActivity(paraMap);
				if (boxes == null || boxes.getRealCount() == 0) {
					rDO.setResultCode(ResultCode.ACTIVITY_INVITE_FRIEND_KM_ERROR);
					return rDO;
				}
				int value = boxes.getTotalCount() - boxes.getRealCount();
				int level = value;
				if (level > rule.getBoxesRange().size() - 1) {
					level = rule.getBoxesRange().size() - 1;
				}
				int num = activityLotteryManager.updateRealCount(activity.getId(), memberId, "boxes");
				if (num == 0) {
					rDO.setResultCode(ResultCode.ACTIVITY_INVITE_FRIEND_KM_ERROR);
					return rDO;
				}
				List<Long> boxesCoupons = rule.getBoxesRange().get(level);
				for (Long templateId : boxesCoupons) {
					// 发放奖励
					RuleBody ruleBody = new RuleBody();
					ruleBody.setActivityId(activity.getId());
					ruleBody.setMemberId(memberId);
					ruleBody.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_COIN.getStatus());
					ruleBody.setActivityName(activity.getActivityDesc());
					ruleBody.setCycleStr("boxes");
					ruleBody.setDeductRemark("boxes-" + value);
					ruleBody.setRewardId(activity.getId().toString());
					RewardsBase rBase = new RewardsBase();
					CouponTemplate c = couponTemplateManager.selectByPrimaryKey(templateId);
					rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
					rBase.setRewardName(c.getAmount().intValue() + ActivityConstant.couponDesc);
					rBase.setRewardValue(c.getAmount().intValue());
					rBase.setTemplateId(templateId);
					drawByPrizeDirectly.drawLottery(ruleBody, rBase, "");
				}
				MessageClient.sendMsgForSPEngin(memberId, activity.getActivityDesc(), "幸运宝箱奖励");
				model.setReceiveReturnStr(rule.getBoxesStr().get(level));
			}
		} catch (Exception e) {
			logger.error("里程拉新领取失败", e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}

	@Override
	public Object inviteFriendList(Long memberId, int startNo) {
		ResultDO<ActivityForFriendShip> rDO = new ResultDO<ActivityForFriendShip>();
		ActivityForFriendShip model = new ActivityForFriendShip();
		rDO.setResult(model);
		try {
			Optional<Activity> optOfActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_FRIEND_SHIP_YEARS);
			if (!optOfActivity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			Activity activity = optOfActivity.get();
			if (activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_AUDIT.getStatus()) {
				rDO.setResultCode(ResultCode.ACTIVITY_BIRTHDAY_IS_NOT_START);
				return rDO;
			}
			if (memberId < 1l || startNo < 0) {
				rDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
				return rDO;
			}
			int count = activityLotteryResultManager.inviteFriendListCount(activity.getId(), memberId, activity.getStartTime(),
					activity.getEndTime());
			model.setFriendListCount(count);
			if (count == 0) {
				return rDO;
			}
			List<ActivityLotteryResult> invitelist = activityLotteryResultManager.inviteFriendList(activity.getId(), memberId, startNo,
					activity.getStartTime(), activity.getEndTime());
			if (Collections3.isEmpty(invitelist)) {
				return rDO;
			}
			List<ActivityLotteryResultBiz> list = BeanCopyUtil.mapList(invitelist, ActivityLotteryResultBiz.class);
			for (ActivityLotteryResultBiz biz : list) {
				biz.setMemberName(ServletUtil.getMemberUserName(biz.getMemberId()));
				biz.setAvatar(ServletUtil.getMemberAvatarById(biz.getMemberId()));
				biz.setMemberId(null);
			}
			model.setFriendList(list);
		} catch (Exception e) {
			logger.error("里程拉新领取失败", e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}

	@Override
	public ResultDO<ActivityForAnniversary> receiveCelebrationA(Long memberId) {
		ResultDO<ActivityForAnniversary> rDO = new ResultDO<ActivityForAnniversary>();
		try {
			rDO = activityLotteryManager.receiveCelebrationA(memberId);
		} catch (Exception e) {
			logger.error("庆A轮领取红包失败 memberId={}", memberId, e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}
	/**
	 *  判断当天是否领取红包
	 * @param memberId
	 * @param activityId
	 * @return
	 * @throws Exception
	 */
	@Override
	public boolean isReceived(Long memberId,Long activityId) throws Exception{
		RuleBody rb = new RuleBody();
		rb.setActivityId(activityId);
		rb.setMemberId(memberId);
		rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
		rb.setCycleStr(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate()));
		rb.setDeductRemark("融光焕发");
		rb.setRewardsAvailableNum(2);
		rb.setRewardsPoolMaxNum(2);
		// 校验
		if (drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
			return false;
		}
		return true;
		
	}

	@Override
	public Object initCelebration(Optional<MemberSessionDto> optMember) throws Exception {
		ResultDO<ActivityForA> rDO = new ResultDO<ActivityForA>();
		ActivityForA model = new ActivityForA();
		rDO.setResult(model);
		String activityId = PropertiesUtil.getProperties("activity.celebrationA.id");
		Optional<Activity> optOfActivity = LotteryContainer.getInstance()
				.getActivity(Long.parseLong(activityId));
		if(optOfActivity.isPresent()){
			Activity activity = optOfActivity.get();
			model.setActivityStatus(activity.getActivityStatus());
			model.setStartTime(activity.getStartTime());
			model.setEndTime(activity.getEndTime());
		}
		if(optMember.isPresent()){
			model.setLogined(true);
			if(isReceived(optMember.get().getId(),Long.parseLong(activityId))){
				model.setReceived(true);
			}
		}
		return rDO;
	}
	/**
	 * 
	 * @desc 玩转奥运初始化
	 * @param fromNullable
	 * @return
	 * @author chaisen
	 * @throws Exception 
	 * @time 2016年7月11日 下午2:25:11
	 *
	 */
	@Override
	public Object olympicInit(Optional<MemberSessionDto> optMember) throws Exception {
		ResultDO<ActivityForOlympic> rDO = new ResultDO<ActivityForOlympic>();
		ActivityForOlympic model = new ActivityForOlympic();
		rDO.setResult(model);
		Optional<Activity> olympicActivity = LotteryContainer.getInstance()
				.getActivityByName(ActivityConstant.ACTIVITY_PLAY_OLYMPIC);
		if (olympicActivity.isPresent()) {
		Activity activity = olympicActivity.get();
		model.setActivityStatus(activity.getActivityStatus());
		model.setStartTime(activity.getStartTime());
		model.setEndTime(activity.getEndTime());
		ActivityForOlympicDate olympicDate = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForOlympicDate.class,
				ActivityConstant.ACTIVITY_PLAY_OLYMPIC_KEY);
		model.setInvestAmount(olympicDate.getInvestAmount());
		//猜奖牌开始结束时间（大活动时间）
		model.setGuessMedalStartTime(olympicDate.getGuessMedalStartTime());
		model.setGuessMedalEndTime(olympicDate.getGuessMedalEndTime());
		//猜奖牌开始时间和结束时间  当天
		model.setGuessStartTime(olympicDate.getGuessStartTime());
		model.setGuessEndTime(olympicDate.getGuessEndTime());
		//猜奖牌比赛时间
		model.setMatchStartTime(olympicDate.getMatchStartTime());
		model.setMatchEndTime(olympicDate.getMatchEndTime());
		//猜奖牌公布时间
		model.setPublishTime(olympicDate.getPublishTime());
		//猜金牌开始和结束时间
		model.setGuessGoldStartTime(olympicDate.getGuessGoldStartTime());
		model.setGuessGoldEndTime(olympicDate.getGuessGoldEndTime());
		//猜金牌区间时间
		model.setSixEndTime(olympicDate.getSixEndTime());
		model.setSevenStartTime(olympicDate.getSevenStartTime());
		model.setEightEndTime(olympicDate.getEightEndTime());
		model.setNineStartTime(olympicDate.getNineStartTime());
		model.setGuessGoldPublishTime(olympicDate.getGuessGoldPublishTime());
		//竞奥运开始结束时间
		model.setRaceStartTime(olympicDate.getRaceStartTime());
		model.setRaceEndTime(olympicDate.getRaceEndTime());
		try {
		//活动时间内
		if(DateUtils.isDateBetween(DateUtils.getCurrentDate(),activity.getStartTime() ,activity.getEndTime())){
			//登录过
			if(optMember.isPresent()){
				//我的投资额
				model.setTodayMyInvestAmount(transactionManager.getMyTotalInvestByMemberIdAndTime(optMember.get().getId(),DateUtils.getDateTimeFromString(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+" "+olympicDate.getRaceStartTime()),DateUtils.getDateTimeFromString(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+" "+olympicDate.getRaceEndTime())));
				//我的累计投资额
				model.setTotalMyInvestAmount(transactionManager.getMyTotalInvestByMemberIdAndTime(optMember.get().getId(), activity.getStartTime(), activity.getEndTime()));
				//亮奥运
				model.setBrightOlympic(activityLotteryResultManager.getBrightOlympicEveryDay(activity.getId(),optMember.get().getId()));
				//亮奥运奖励是否领取
				//model.setBrightOlympicReceive(activityLotteryResultManager.ifBrightOlympicReceived(activity, optMember.get().getId()));
				model=activityLotteryResultManager.ifBrightOlympicReceived(activity, optMember.get().getId(),model);
				//当天是否竞猜过奖牌数
				RuleBody rb = new RuleBody();
				rb.setActivityId(activity.getId());
				rb.setMemberId(optMember.get().getId());
				rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
				rb.setCycleStr(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+ActivityConstant.ACTIVITY_OLYMPIC_GUESS_MEDAL);
				if (!drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
					model.setIfGuessMedal(true);
					model.setGuessMedalNumber(activityLotteryManager.getMedalType(activity.getId(), optMember.get().getId()));
				}
				//当天是否竞猜过金牌
				rb.setCycleStr(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+ActivityConstant.ACTIVITY_OLYMPIC_GUESS_GOLE);
				if (!drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
					model.setIfGuessGold(true);
				}
				//奖牌奇偶数竞猜记录
				model.setGuessMedalRecord(activityLotteryManager.getGuessMedalRecord(optMember.get().getId(),olympicActivity.get().getId()));
				//金牌竞猜记录
				model.setGuessGoldRecord(activityLotteryManager.getGuessGoldRecord(optMember.get().getId(),olympicActivity.get().getId(),olympicDate,activity.getStartTime()));
				//拼图剩余数量
				//model.setPuzzleRemind(activityLotteryManager.getPuzzleRemind(activity.getId(),optMember.get().getId(),olympicDate));
				model=activityLotteryManager.getPuzzleRemind(activity.getId(),optMember.get().getId(),olympicDate,model);
				// 刷新人气值
				Balance balance = balanceService.queryBalance(optMember.get().getId(), TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY);
				if (balance != null) {
					model.setRemindPopularityVaule(balance.getAvailableBalance().intValue());
				}
				
			}
			// 竞奥运  累计投资额排名前8的用户
			List<TransactionForFirstInvestAct> firstEightList=transactionManager.selectTopEightInvestAmount(DateUtils.getDateTimeFromString(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+" "+olympicDate.getRaceStartTime()),DateUtils.getDateTimeFromString(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+" "+olympicDate.getRaceEndTime()));
			model.setEverydayTotalAmountList(firstEightList);
			// 赛奥运
			List<TransactionForFirstInvestAct> totalAmountList=transactionManager.selectTopTenInvestAmount(activity.getStartTime(),activity.getEndTime());
			model.setTotalAmountList(totalAmountList);
			//竞猜总人数
			model.setGuessTotalNumber(activityLotteryManager.countActivityLotteryByActivityId(olympicActivity.get().getId(), DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+ActivityConstant.ACTIVITY_OLYMPIC_GUESS_MEDAL));
			model.setLuckyList(activityLotteryManager.getRealPrize(activity.getId()));
		}else{
			if(optMember.isPresent()){
				//亮奥运
				model.setBrightOlympic(activityLotteryResultManager.getBrightOlympicEveryDay(activity.getId(),optMember.get().getId()));
				model=activityLotteryResultManager.ifBrightOlympicReceived(activity, optMember.get().getId(),model);
				//奖牌奇偶数竞猜记录
				model.setGuessMedalRecord(activityLotteryManager.getGuessMedalRecord(optMember.get().getId(),olympicActivity.get().getId()));
				//金牌竞猜记录
				model.setGuessGoldRecord(activityLotteryManager.getGuessGoldRecord(optMember.get().getId(),olympicActivity.get().getId(),olympicDate,activity.getStartTime()));
				//我的累计投资额
				model.setTotalMyInvestAmount(transactionManager.getMyTotalInvestByMemberIdAndTime(optMember.get().getId(), activity.getStartTime(), activity.getEndTime()));
			}
			// 赛奥运
			List<TransactionForFirstInvestAct> totalAmountList=transactionManager.selectTopTenInvestAmount(activity.getStartTime(),activity.getEndTime());
			model.setTotalAmountList(totalAmountList);
			model.setLuckyList(activityLotteryManager.getRealPrize(activity.getId()));
		}
		} catch (ManagerException e) {
			logger.error("玩转奥运初始化失败, activityId={}", activity.getId(), e);
		}finally {
			logger.info("玩转奥运初始化结束",activity.getId());
		}
		}
		return rDO;
	}
	/**
	 * 
	 * @desc 猜奖牌
	 * @param memberId
	 * @param medalType （1 奇数  2 偶数）
	 * @return
	 * @author chaisen
	 * @time 2016年7月12日 上午9:16:04
	 *
	 */
	@Override
	public Object guessMedal(Long memberId, int medalType) {
		ResultDO<ActivityForOlympicReturn> rDO = new ResultDO<ActivityForOlympicReturn>();
		try {
			rDO = activityLotteryManager.guessMedal(memberId,medalType);
		} catch (Exception e) {
			logger.error("竞猜奖牌失败 memberId={}", memberId, e);
		}
		return rDO;
	}
	/**
	 * 
	 * @desc 猜金牌
	 * @param memberId
	 * @param popularityValue
	 * @param goldNumber
	 * @return
	 * @author chaisen
	 * @time 2016年7月12日 上午9:16:29
	 *
	 */
	@Override
	public Object guessGold(Long memberId, int popularityValue, int goldNumber) {
		ResultDO<ActivityForOlympicReturn> rDO = new ResultDO<ActivityForOlympicReturn>();
		try {
			rDO = activityLotteryManager.guessGold(memberId,popularityValue,goldNumber);
		} catch (Exception e) {
			logger.error("竞猜金牌失败 memberId={}", memberId, e);
		}
		return rDO;
	}
	/**
	 * 
	 * @desc 集奥运
	 * @param memberId
	 * @param puzzle
	 * @return
	 * @author chaisen
	 * @time 2016年7月12日 下午3:06:52
	 *
	 */
	@Override
	public Object setOlympic(Long memberId, String puzzle) {
		ResultDO<ActivityForOlympicReturn> rDO = new ResultDO<ActivityForOlympicReturn>();
		try {
			rDO = activityLotteryManager.setOlympic(memberId,puzzle);
		} catch (Exception e) {
			logger.error("集奥运兑换奖品失败 memberId={}", memberId, e);
		}
		return rDO;
	}
	
	/**
	 * 
	 * @desc 七月战队初始化
	 * @param optMember
	 * @return
	 * @throws Exception
	 * @author chaisen
	 * @time 2016年6月29日 下午5:10:07
	 *
	 */
	@Override
	public Object julyTeamInit(Optional<MemberSessionDto> optMember) throws Exception {
		ResultDO<ActivityForJuly> rDO = new ResultDO<ActivityForJuly>();
		ActivityForJuly model = new ActivityForJuly();
		rDO.setResult(model);
		Optional<Activity> teamActivity = LotteryContainer.getInstance()
				.getActivityByName(ActivityConstant.ACTIVITY_JULY_TEAM);
		if (teamActivity.isPresent()) {
		Activity activity = teamActivity.get();
		try {
		model.setActivityStatus(activity.getActivityStatus());
		model.setStartTime(activity.getStartTime());
		model.setEndTime(activity.getEndTime());
		ActivityForJulyDate julyDate = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForJulyDate.class,
				ActivityConstant.ACTIVITY_JULY_TEAM_KEY);
		//加入战队时间
		model.setJoinStartTime(julyDate.getJoinStartTime());
		model.setJoinEndTime(julyDate.getJoinEndTime());
		//押注时间
		model.setBetStartTime(julyDate.getBetStartTime());
		model.setBetEndTime(julyDate.getBetEndTime());
		//领红包时间
		model.setReceiveStartTime(julyDate.getReceiveStartTime());
		model.setReceiveEndTime(julyDate.getReceiveEndTime());
		//统计金额时间
		model.setCountStartTime(julyDate.getCountStartTime());
		model.setCountEndTime(julyDate.getCountEndTime());
		if(DateUtils.isDateBetween(DateUtils.getCurrentDate(),activity.getStartTime() ,activity.getEndTime())){
		Date nowDate = DateUtils.getCurrentDate();
		//pk 历史记录
		List<ActivityForJulyHistory> historyList=activityGroupManager.getPKforJulyHistory(activity.getId());
		model.setPkHistoryList(historyList);
		//是否第一轮押注
		if(DateUtils.getDateStrFromDate(nowDate).equals(DateUtils.getDateStrFromDate(activity.getStartTime()))){
			model.setIfFirstBet(true);
		}
		//本轮押注人数
		model.setCurrentBetMember(activityLotterManager.countBetTotal(activity.getId(),DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+"-betjulyteam"));
		//上轮翻倍人数
		model.setLastBetMember(activityLotteryResultManager.countLastBetTotal(activity.getId(),DateUtils.getDateStrFromDate(DateUtils.getYesterDay())+"-betjulyteam"));
		BigDecimal totalAmountA=BigDecimal.ZERO;
		BigDecimal totalAmountB=BigDecimal.ZERO;
		if(DateUtils.isDateBetween(DateUtils.getCurrentDate(), DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountStartTime()), DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountEndTime()))){
			//骄阳似火的当天投资总额
			totalAmountA = activityGroupManager.getTotalAmountByMemberIdAndActivityGroupType(activity,ActivityEnum.JULYTEAM_GROUPTYPE_JYSH.getCode());
			//清凉一夏的当天投资总额 
			totalAmountB = activityGroupManager.getTotalAmountByMemberIdAndActivityGroupType(activity,ActivityEnum.JULYTEAM_GROUPTYPE_QLYX.getCode());
		}else{
			totalAmountA=activityGroupManager.getTotalAmountByActivityId(activity.getId(),ActivityEnum.JULYTEAM_GROUPTYPE_JYSH.getCode());
			totalAmountB=activityGroupManager.getTotalAmountByActivityId(activity.getId(),ActivityEnum.JULYTEAM_GROUPTYPE_QLYX.getCode());
		}
		model.setTodayInvestAmountA(totalAmountA);
		model.setTodayInvestAmountB(totalAmountB);
		if(!DateUtils.isDateBetween(DateUtils.getCurrentDate(), DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountStartTime()), DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountEndTime()))){
			//胜利标识
			int successFlag=activityGroupManager.getSuccessTeam(activity.getId());
			model.setSuccessFlag(successFlag);
		}
		if(optMember.isPresent()){
			//当前时间是否可以加入战队
			if(DateUtils.isDateBetween(DateUtils.getCurrentDate(), DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getJoinStartTime()), DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getJoinEndTime()))){
				model.setCanJoinTeam(true);
			}
			//当前时间是否可以押注
			if(DateUtils.isDateBetween(DateUtils.getCurrentDate(), DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getBetStartTime()), DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getBetEndTime()))){
				model.setCanBet(true);
			}
			//当前时间是否可以领取红包
			if(DateUtils.isDateBetween(DateUtils.getCurrentDate(), DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getReceiveStartTime()), DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getReceiveEndTime()))){
				model.setCanReceive(true);
			}
			//是否加入过战队
			if(activityGroupManager.checkGroupByActivityIdAndMemberId(activity.getId(), optMember.get().getId())){
				model.setJoined(true);
				BigDecimal totalAmount=BigDecimal.ZERO;
				if(DateUtils.isDateBetween(DateUtils.getCurrentDate(), DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountStartTime()), DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountEndTime()))){
					// 获取用户当前投资总额
					totalAmount = getTotalAmountByMemberId(optMember.get().getId(),DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountStartTime()),DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountEndTime()));
				}else{
					ActivityGroup activityGroup=activityGroupMapper.getCurrentMemberGroupBy(activity.getId(), optMember.get().getId());
					if(activityGroup!=null){
						if(activityGroup.getGroupInfo()!=null){
							totalAmount=new BigDecimal(activityGroup.getGroupInfo());
						}
					}
				}
				model.setTodayInvestAmountMy(totalAmount);
				//当前用户所属战队
				int groupType=activityGroupManager.getGroupTypeByMemberId(activity.getId(), optMember.get().getId());
				model.setCurrentGroupType(groupType);
				//当前用户所在战队投资排名前十
				List<TransactionForFirstInvestAct> currentGroupFirstTen=Lists.newArrayList();
				if(DateUtils.isDateBetween(DateUtils.getCurrentDate(), DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountStartTime()), DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountEndTime()))){
					currentGroupFirstTen=activityGroupManager.selectTopTenInvestByGroupType(groupType,activity.getId(),DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountStartTime()),DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountEndTime()));
				}else{
					ActivityMessage jysh=new ActivityMessage();
					Long templateId=0L;
					if(groupType==ActivityEnum.JULYTEAM_GROUPTYPE_JYSH.getCode()){
						templateId=1001L;
					}else if(groupType==ActivityEnum.JULYTEAM_GROUPTYPE_QLYX.getCode()){
						templateId=1002L;
					}
					jysh=activityMessageMapper.selectFirstTenHistroyByActivityId(activity.getId(), templateId);
					if(jysh!=null&&jysh.getRemark()!=null){
						currentGroupFirstTen= JSONObject.parseArray(jysh.getRemark(), TransactionForFirstInvestAct.class);
					}
					//胜利标识
					//int successFlag=activityGroupManager.getSuccessTeam(activity.getId());
					//model.setSuccessFlag(successFlag);
				}
				model.setJulyTeamContribution(currentGroupFirstTen);
				//当前用户所在战队红包剩余数量
				ActivityForJulyBet remindCoupon=new ActivityForJulyBet();
				if(DateUtils.isDateBetween(DateUtils.getCurrentDate(), DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountStartTime()), DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountEndTime()))){
					remindCoupon=activityGroupManager.getRemindCoupon(activity,groupType);
				}else{
					remindCoupon=activityGroupManager.initRemindCoupon(activity);
				}
				model.setRemindCoupon(remindCoupon);
			}
			//当天是否押注过
			RuleBody rb = new RuleBody();
			rb.setActivityId(activity.getId());
			rb.setMemberId(optMember.get().getId());
			rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
			rb.setCycleStr(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+"-betjulyteam");
			// 校验
			if (!drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
				model.setBeted(true);
			}
			//押注记录
			ActivityLotteryResult activityResult=new ActivityLotteryResult();
			activityResult.setActivityId(activity.getId());
			activityResult.setMemberId(optMember.get().getId());
			activityResult.setRewardId(optMember.get().getId().toString());
			List<ActivityForJulyBet> betList=activityLotteryResultManager.getBetRecordList(activityResult);
			model.setBetList(betList);
		}
		}else{
			if(optMember.isPresent()){
				int groupType=activityGroupManager.getGroupTypeByMemberId(activity.getId(), optMember.get().getId());
				//押注记录
				ActivityLotteryResult activityResult=new ActivityLotteryResult();
				activityResult.setActivityId(activity.getId());
				activityResult.setMemberId(optMember.get().getId());
				activityResult.setRewardId(optMember.get().getId().toString());
				List<ActivityForJulyBet> betList=activityLotteryResultManager.getBetRecordList(activityResult);
				model.setBetList(betList);
				
				//当前用户所在战队投资排名前十
				List<TransactionForFirstInvestAct> currentGroupFirstTen=Lists.newArrayList();
				//if(DateUtils.isDateBetween(DateUtils.getCurrentDate(), DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountStartTime()), DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountEndTime()))){
				//	currentGroupFirstTen=activityGroupManager.selectTopTenInvestByGroupType(groupType,activity.getId(),DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountStartTime()),DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountEndTime()));
				//}else{
					ActivityMessage jysh=new ActivityMessage();
					Long templateId=0L;
					if(groupType==ActivityEnum.JULYTEAM_GROUPTYPE_JYSH.getCode()){
						templateId=1001L;
					}else if(groupType==ActivityEnum.JULYTEAM_GROUPTYPE_QLYX.getCode()){
						templateId=1002L;
					}
					jysh=activityMessageMapper.selectFirstTenHistroyByActivityId(activity.getId(), templateId);
					if(jysh!=null&&jysh.getRemark()!=null){
						currentGroupFirstTen= JSONObject.parseArray(jysh.getRemark(), TransactionForFirstInvestAct.class);
					}
					//胜利标识
					//int successFlag=activityGroupManager.getSuccessTeam(activity.getId());
					//model.setSuccessFlag(successFlag);
				//}
				model.setJulyTeamContribution(currentGroupFirstTen);
			}
			//pk 历史记录
			List<ActivityForJulyHistory> historyList=activityGroupManager.getPKforJulyHistory(activity.getId());
			model.setPkHistoryList(historyList);
			BigDecimal totalAmountA=BigDecimal.ZERO;
			BigDecimal totalAmountB=BigDecimal.ZERO;
			//if(DateUtils.isDateBetween(DateUtils.getCurrentDate(), DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountStartTime()), DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountEndTime()))){
				//骄阳似火的当天投资总额
			//	totalAmountA = activityGroupManager.getTotalAmountByMemberIdAndActivityGroupType(activity,ActivityEnum.JULYTEAM_GROUPTYPE_JYSH.getCode());
				//清凉一夏的当天投资总额 
			//	totalAmountB = activityGroupManager.getTotalAmountByMemberIdAndActivityGroupType(activity,ActivityEnum.JULYTEAM_GROUPTYPE_QLYX.getCode());
			//}else{
				totalAmountA=activityGroupManager.getTotalAmountByActivityId(activity.getId(),ActivityEnum.JULYTEAM_GROUPTYPE_JYSH.getCode());
				totalAmountB=activityGroupManager.getTotalAmountByActivityId(activity.getId(),ActivityEnum.JULYTEAM_GROUPTYPE_QLYX.getCode());
			//}
			model.setTodayInvestAmountA(totalAmountA);
			model.setTodayInvestAmountB(totalAmountB);
			
		}
		} catch (ManagerException e) {
			logger.error("组团大作战初始化失败, activityId={}", activity.getId(), e);
		}finally {
			logger.info("组团大作战初始化",activity.getId());
		}
		}
		return rDO;
	}

	@Override
	public ResultDO<ActivityForJulyRetrun> julyTeamJoin(Long memberId) {
		ResultDO<ActivityForJulyRetrun> rDO = new ResultDO<ActivityForJulyRetrun>();
		try {
			rDO = activityLotteryManager.julyTeamJoin(memberId);
		} catch (Exception e) {
			logger.error("加入战队失败 memberId={}", memberId, e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}

	@Override
	public Object betJulyTeam(Long memberId, int popularityValue ,int groupType) {
		ResultDO<ActivityForJulyRetrun> rDO = new ResultDO<ActivityForJulyRetrun>();
		try {
			rDO = activityLotteryManager.betJulyTeam(memberId,popularityValue,groupType);
		} catch (Exception e) {
			logger.error("押注战队失败 memberId={}", memberId, e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}

	@Override
	public ResultDO<ActivityForJulyRetrun> receiveJulyTeamCoupon(Long memberId, int couponAmount) {
		ResultDO<ActivityForJulyRetrun> rDO = new ResultDO<ActivityForJulyRetrun>();
		try {
			rDO = activityLotteryManager.receiveJulyTeamCoupon(memberId,couponAmount);
		} catch (Exception e) {
			logger.error("领取战队现金券失败 memberId={}", memberId, e);
		}
		return rDO;
	}

	@Override
	public Object dataChannelInit() {
		ResultDO<ChannelData> rDO = new ResultDO<ChannelData>();
		ChannelData model = new ChannelData();
		rDO.setResult(model);
		try {
			//用户累计投资总额
			List<PdGeneralMonth> listTotalAmount=channelDataManager.selectTotalInvestAmountMonth();
			model.setListTotalAmount(listTotalAmount);
			//获取最新项目数据
			PdGeneralMonth projectData=channelDataManager.selectPdGeneralNewMonth();
			model.setProjectData(projectData);
			//获取最新的地域分布数据
			List<PdRegionMonth> listRegionMonth=channelDataManager.selectPdRegionMonth();
			model.setListRegionMonth(listRegionMonth);
			
		} catch (Exception e) {
			logger.error("平台数据频道获取失败 ", e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}

	@Override
	public Object realTimeData() {
		ResultDO<ChannelData> rDO = new ResultDO<ChannelData>();
		ChannelData model = new ChannelData();
		rDO.setResult(model);
		try {
			model.setTotalInvest(PaltformCapitalUtils.getPaltformTotalInvest());
			model.setMemberCount(RedisPlatformClient.getMemberCount());
			model.setTotalInvestInterest(PaltformCapitalUtils.getPaltformTotalInterest());
			model.setTransactionCount((long) transactionManager.getTotalTransactionCount());
		} catch (Exception e) {
			logger.error("平台实时数据获取失败 ", e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}
	/**
	 * 
	 * @desc 周年庆初始化
	 * @param optMember
	 * @return
	 * @throws Exception
	 * @author chaisen
	 * @time 2016年10月17日 下午1:38:25
	 *
	 */
	@Override
	public Object anniversaryInit(Optional<MemberSessionDto> optMember) throws Exception {
		ResultDO<ActivityForAnniversaryCelebrate> rDO = new ResultDO<ActivityForAnniversaryCelebrate>();
		ActivityForAnniversaryCelebrate model = new ActivityForAnniversaryCelebrate();
		rDO.setResult(model);
		Optional<Activity> anniverysaryActivity = LotteryContainer.getInstance()
				.getActivityByName(ActivityConstant.ACTIVITY_ANNIVERSARY_CELEBRATE_NAME);
		if (anniverysaryActivity.isPresent()) {
		Activity activity = anniverysaryActivity.get();
		model.setActivityStatus(activity.getActivityStatus());
		model.setStartTime(activity.getStartTime());
		model.setEndTime(activity.getEndTime());
		ActivityForAnniversaryCelebrate anniverDate = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForAnniversaryCelebrate.class,
				ActivityConstant.ACTIVITY_ANNIVERSARY_CELEBRATE_KEY);
		model.setCoupon88StartTime(anniverDate.getCoupon88StartTime());
		model.setEightCouponStartTime(anniverDate.getEightCouponStartTime());
		ActivityData activityData=activityGroupManager.selectActivityDateByActivityId(activity.getId(),null);
		if(activityData!=null){
			model.setTotalCoupon88(activityData.getDataGole());
		}
		try {
		//活动时间内
		Long memberId=0L;
		if(optMember.isPresent()){
			memberId=optMember.get().getId();
			model.setTotalMyInvestAmount(transactionManager.getMemberTotalInvestByMemberId(memberId,activity.getStartTime(),activity.getEndTime(),anniverDate.getTotalDays()));
		}
		if(activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()){
			int coupon88=0;
			if(model.getTotalCoupon88()!=null){
				coupon88=model.getTotalCoupon88()-RedisActivityClient.getReceivedCouponEightNum();
			}
			if(coupon88>0){
				model.setCoupon88Remind(coupon88);
			}
			//登录过
			if(optMember.isPresent()){
				//收益券是否领取
				memberId=optMember.get().getId();
				model.setTotalMyInvestAmount(transactionManager.getMemberTotalInvestByMemberId(memberId,activity.getStartTime(),activity.getEndTime(),anniverDate.getTotalDays()));
				model.setPopularity19(activityLotteryResultManager.isReceived(activity.getId(),memberId,"popularity19"));
				model.setPopularity199(activityLotteryResultManager.isReceived(activity.getId(),memberId,"popularity199"));
				model.setPopularity659(activityLotteryResultManager.isReceived(activity.getId(),memberId,"popularity659"));
				model.setPopularity1119(activityLotteryResultManager.isReceived(activity.getId(),memberId,"popularity1119"));
				model.setPopularity2016(activityLotteryResultManager.isReceived(activity.getId(),memberId,"popularity2016"));
				model.setIphone7(activityLotteryResultManager.isReceived(activity.getId(),memberId,"iphone7"));
				//翻牌奖品
				ActivityLotteryResult lottery=new ActivityLotteryResult();
				//lottery.setActivityId(activity.getId());
				lottery.setRemark(memberId+":"+DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+":reward");
				List<ActivityLotteryResult> list=activityLotteryResultManager.getLotteryResultBySelective(lottery);
				if(Collections3.isNotEmpty(list)){
					model.setPosition(list.get(0).getChip());
					model.setRewardResult(list.get(0).getRewardResult());
				}
				// 刷新人气值
				Balance balance = balanceManager.queryBalance(memberId, TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY);
				if (balance != null) {
					model.setMyPopularity(balance.getAvailableBalance().intValue());
				}
				Map<String, Object> paraMap = new HashMap<String, Object>();
				paraMap.put("activityId", activity.getId());
				paraMap.put("memberId", memberId);
				paraMap.put("cycleConstraint", activity.getId()+":"+memberId+":coupon");
				ActivityLottery activityLottery=activityLotterManager.selectByMemberActivity(paraMap);
				if(activityLottery!=null){
					model.setMyNumber(activityLottery.getRealCount());
				}
			}
		}else{
			if(optMember.isPresent()){
				// 刷新人气值
				memberId=optMember.get().getId();
				Balance balance = balanceManager.queryBalance(memberId, TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY);
				if (balance != null) {
					model.setMyPopularity(balance.getAvailableBalance().intValue());
				}
				Map<String, Object> map = Maps.newHashMap();
				map.put("memberId", memberId);
				map.put("startTime", activity.getStartTime());
				map.put("endTime", activity.getEndTime());
				map.put("totalDays", anniverDate.getTotalDays());
				//model.setTotalMyInvestAmount(transactionManager.getMemberTotalInvestByMemberIdAndTotalDays(map));
				Map<String, Object> paraMap = new HashMap<String, Object>();
				paraMap.put("activityId", activity.getId());
				paraMap.put("memberId", memberId);
				paraMap.put("cycleConstraint", activity.getId()+":"+memberId+":coupon");
				ActivityLottery activityLottery=activityLotterManager.selectByMemberActivity(paraMap);
				if(activityLottery!=null){
					model.setMyNumber(activityLottery.getRealCount());
				}
				model.setPopularity19(activityLotteryResultManager.isReceived(activity.getId(),memberId,"popularity19"));
				model.setPopularity199(activityLotteryResultManager.isReceived(activity.getId(),memberId,"popularity199"));
				model.setPopularity659(activityLotteryResultManager.isReceived(activity.getId(),memberId,"popularity659"));
				model.setPopularity1119(activityLotteryResultManager.isReceived(activity.getId(),memberId,"popularity1119"));
				model.setPopularity2016(activityLotteryResultManager.isReceived(activity.getId(),memberId,"popularity2016"));
				model.setIphone7(activityLotteryResultManager.isReceived(activity.getId(),memberId,"iphone7"));
			}
		}
		} catch (ManagerException e) {
			logger.error("【四季变换，有你相伴】初始化失败, activityId={}", activity.getId(), e);
		}
		}
		return rDO;
	}

	@Override
	public Object receiveCouponAnniversary(Long memberId, int type, int couponAmount) {
		ResultDO<ActivityForAnniversaryRetrun> rDO = new ResultDO<ActivityForAnniversaryRetrun>();
		try {
			rDO = activityLotteryManager.receiveCouponAnniversary(memberId,couponAmount,type);
		} catch (Exception e) {
			logger.error("【四季变换，有你相伴】领取战队现金券失败 memberId={}", memberId, e);
		}
		return rDO;
	}

	@Override
	public Object receiveRewardAnniversary(Long memberId, int chip, int popularValue) {
		ResultDO<ActivityForAnniversaryRetrun> rDO = new ResultDO<ActivityForAnniversaryRetrun>();
		try {
			rDO = activityLotteryManager.receiveRewardAnniversary(memberId,popularValue,chip);
		} catch (Exception e) {
			logger.error("【四季变换，有你相伴】消耗人气值领取奖品失败 memberId={}", memberId, e);
		}
		return rDO;
	}

	@Override
	public Object octoberInit(Optional<MemberSessionDto> optMember)  {
		ResultDO<ActivityForOctober> rDO = new ResultDO<ActivityForOctober>();
		ActivityForOctober model = new ActivityForOctober();
		rDO.setResult(model);
		Optional<Activity> olympicActivity = LotteryContainer.getInstance()
				.getActivityByName(ActivityConstant.ACTIVITY_OCTOBER_NAME);
		if (olympicActivity.isPresent()) {
		try {
			Activity activity = olympicActivity.get();
			model.setActivityStatus(activity.getActivityStatus());
			model.setStartTime(activity.getStartTime());
			model.setEndTime(activity.getEndTime());
			model.setFirstTenInvestAmount(transactionManager.selectTopTenInvestAmountByActivityId(activity.getStartTime(),activity.getEndTime(),activity.getId()));
			if(optMember.isPresent()){
				model.setMyInvestAmount(transactionManager.getInvestAmountByMemberId(optMember.get().getId(),activity.getId(),activity.getEndTime(),activity.getStartTime()));
			}
		} catch (ManagerException e) {
			logger.error("【双12狂欢季】初始化失败 ", e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		}
		return rDO;
	}

	@Override
	public Object doubleInit(Optional<MemberSessionDto> optMember) {
		ResultDO<ActivityForDouble> rDO = new ResultDO<ActivityForDouble>();
		ActivityForDouble model = new ActivityForDouble();
		rDO.setResult(model);
		Optional<Activity> doubleActivity = LotteryContainer.getInstance()
				.getActivityByName(ActivityConstant.ACTIVITY_DOUBLE_NAME);
		if (doubleActivity.isPresent()) {
			Activity activity = doubleActivity.get();
			ActivityForDouble doubleDate = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForDouble.class,
					ActivityConstant.ACTIVITY_DOUBLE_KEY);
		try {
			model.setStatus(activity.getActivityStatus());
			model.setStartTime(activity.getStartTime());
			model.setEndTime(activity.getEndTime());
			model.setTen(doubleDate.getTen());
			model.setFifTeen(doubleDate.getFifTeen());
			model.setTotalRed(doubleDate.getTotalRed());
			//model.setFirstInvestAmount(transactionManager.selectTopInvestAmountByActivityIdAndTime(DateUtils.zerolizedTime(DateUtils.getCurrentDate()),DateUtils.getEndTime(DateUtils.getCurrentDate()),activity.getStartTime()));
			model.setFirstInvestAmount(transactionManager.getFirstEightInvestAmount(activity.getId(),activity.getEndTime()));
			model.setEveryDayFirstInvestAmount(transactionManager.getEverydayFirstAmount(activity.getId(), activity.getStartTime(), activity.getEndTime()));
			if(optMember.isPresent()){
				model.setMyInvestAmount(transactionManager.getInvestAmountByMemberIdAndTime(optMember.get().getId(),DateUtils.zerolizedTime(DateUtils.getCurrentDate()),DateUtils.getEndTime(DateUtils.getCurrentDate()),activity.getStartTime()));
			}
			if(model.getMyInvestAmount()==null){
				model.setMyInvestAmount(new BigDecimal(0));
			}
			model.setCountFirstAmountNumber(transactionManager.getCountEverydayFirstAmount(activity.getId(), activity.getStartTime(), activity.getEndTime()));
			Integer param=0;
			if(DateUtils.isDateBetween(DateUtils.getCurrentDate(),DateUtils.getSpecialTime(DateUtils.getCurrentDate(), doubleDate.getTen()),DateUtils.getTimes(DateUtils.getSpecialTime(DateUtils.getCurrentDate(), doubleDate.getFifTeen())))){
				param=doubleDate.getTen();
			}else if(DateUtils.isDateBetween(DateUtils.getCurrentDate(),DateUtils.getSpecialTime(DateUtils.getCurrentDate(), doubleDate.getFifTeen()),DateUtils.getEndTime(DateUtils.getCurrentDate()))){
				param=doubleDate.getFifTeen();
			}else{
				param=0;
			}
			model.setRedRemind(doubleDate.getTotalRed()-RedisActivityClient.getReceivedCouponTotal(param));
		} catch (ManagerException e) {
			logger.error("【双旦狂欢惠】初始化失败 ", e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		}
		return rDO;
	}

	@Override
	public Object doubleReceiveCoupon(Long memberId) {
		ResultDO<ActivityForDouble> rDO = new ResultDO<ActivityForDouble>();
		try {
			rDO = activityLotteryManager.doubleReceiveCoupon(memberId);
		} catch (Exception e) {
			logger.error("【双旦狂欢惠】普天同庆抢红包失败 memberId={}", memberId, e);
		}
		return rDO;
	}

	@Override
	public ActivityLottery selectByMemberActivity(Map<String, Object> paraMap) {
		ActivityLottery activityLottery=new ActivityLottery();
		try {
			activityLottery= activityLotteryManager.selectByMemberActivity(paraMap);
		} catch (ManagerException e) {
			logger.error("查询活动抽奖数据异常", paraMap, e);
		}
		return activityLottery;
	}

	@Override
	public Object newYearLuckyMoney(Long memberId,Long templateId) {
		ResultDO<ActivityForNewYear> rDO = new ResultDO<ActivityForNewYear>();
		try {
			rDO = activityLotteryManager.newYearLuckyMoney(memberId,templateId);
		} catch (Exception e) {
			logger.error("【鸡年新年活动】抢红包失败 memberId={}", memberId, e);
		}
		return rDO;
	}

	@Override
	public ResultDO<Activity> lanternFestival() {
		ResultDO<Activity> rDO = new ResultDO<Activity>();
		try {
			rDO = activityLotteryManager.lanternFestival();
		} catch (Exception e) {
			logger.error("【元宵情人节活动】异常", e);
		}
		return rDO;
	}

	@Override
	public ResultDO<ActivityForWomensDay> womensDayData(Long memberId) {
		ResultDO<ActivityForWomensDay> rDO = new ResultDO<ActivityForWomensDay>();
		try {
			rDO = activityLotteryManager.womensDayData(memberId);
		} catch (Exception e) {
			logger.error("【38节活动】异常", e);
		}
		return rDO;
	}

	@Override
	public ResultDO<ActivityForWomensDay> womensDayBag(Long memberId) {
		ResultDO<ActivityForWomensDay> rDO = new ResultDO<ActivityForWomensDay>();
		try {
			rDO=activityLotteryManager.womensDayBag(memberId);
		} catch (ManagerException e) {
			logger.error("【38节活动】领取礼包异常", e);
		}
		return rDO;
	}
	@Override
	public Object receiveLuckBag(Long memberId, int couponAmount) {
		ResultDO<ActivityForFiveBillionRetrun> rDO = new ResultDO<ActivityForFiveBillionRetrun>();
		try {
			rDO = activityLotteryManager.receiveLuckBag(memberId,couponAmount);
		} catch (Exception e) {
			logger.error("【福临50亿活动】领取福袋失败 memberId={}", memberId, e);
		}
		return rDO;
	}

	
	@Override
	public Object lotteryLuckBoth(Long memberId,int type) {
		ResultDO<ActivityForFiveBillionRetrun> rDO = new ResultDO<ActivityForFiveBillionRetrun>();
		try {
			rDO = activityLotteryManager.lotteryLuckBoth(memberId,type);
		} catch (Exception e) {
			logger.error("【福临50亿活动】抽奖失败 memberId={}", memberId, e);
		}
		return rDO;
	}

	/**
	 * 50亿活动初始化
	 */
	@Override
	public Object fiveBillionInit(Optional<MemberSessionDto> optMember) {
		ResultDO<ActivityForFiveBillionInit> rDO = new ResultDO<ActivityForFiveBillionInit>();
		ActivityForFiveBillionInit model = new ActivityForFiveBillionInit();
		rDO.setResult(model);
		Optional<Activity> fiveActivity = LotteryContainer.getInstance()
				.getActivityByName(ActivityConstant.ACTIVITY_FIVEBILLION_NAME);
		if (fiveActivity.isPresent()) {
				Activity activity = fiveActivity.get();
				ActivityForFiveBillion fiveBillion = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForFiveBillion.class,
						ActivityConstant.ACTIVITY_FIVEBILLION_KEY);
			try {
				model.setStatus(activity.getActivityStatus());
				model.setStartTime(activity.getStartTime());
				model.setEndTime(activity.getEndTime());
				model.setNumber(fiveBillion.getNumber());
				Long memberId=0L;
				if(optMember.isPresent()){
					memberId=optMember.get().getId();
					//福袋是否领取
					 ActivityLotteryResult luckResult=activityLotteryResultManager.queryLotteryResultByRemark(activity.getId(),memberId, memberId+":luckBag");
					 if(luckResult!=null&&luckResult.getChip()>0){
						 model.setReward(luckResult.getChip());
					 }
					//累计投资额
					//model.setMyInvestAmount(transactionManager.findTotalInvestAmount(memberId, activity.getStartTime(), activity.getEndTime()));
					//福禄双全抽奖机会
					 ActivityLotteryResultNew lottery=new ActivityLotteryResultNew();
					lottery.setActivityId(activity.getId());
					lottery.setMemberId(memberId);
					lottery.setRemark("luckLotteryBag");
					lottery.setLotteryStatus(0);
					int count=activityLotteryResultNewMapper.countNewLotteryResult(lottery);
					model.setCountLuckBoth(count);
					//福禄齐天抽奖机会
					if(count>0){
						model.setCountLuckMonkey(count/fiveBillion.getNumber());
					}
					//福禄双全我的中奖记录
					//model.setMyListLuckBoth(activityLotteryResultManager.getMyLotteryRecord(memberId,activity.getId(),"luckBoth"));
					//福禄齐天我的中奖记录
					//model.setMyListLuckMonkey(activityLotteryResultManager.getMyLotteryRecord(memberId,activity.getId(),"luckMonkey"));
				}
				//福禄双全中奖记录
				model.setLuckBothRecord(activityLotteryResultManager.getLotteryRecord(activity.getId(),"luckBoth"));
				//福禄齐天中奖记录
				model.setLuckMonkeyRecord(activityLotteryResultManager.getLotteryRecord(activity.getId(),"luckMonkey"));
			} catch (ManagerException e) {
				logger.error("【福临50亿】初始化失败 ", e);
				rDO.setResultCode(ResultCode.ERROR_SYSTEM);
			}
		}
		return rDO;
	}

	
	@Override
	public Page<ActivityForFiveBillionRetrun> myLotteryRecord(
			ActivityLotteryResultQuery query) {
		try {
			Optional<Activity> fiveActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_FIVEBILLION_NAME);
			if (!fiveActivity.isPresent()) {
				return null;
			}
			Activity activity = fiveActivity.get();
			query.setActivityId(activity.getId());
			return activityLotteryManager.activityLotteryResultListByPage(query);
		} catch (ManagerException e) {
			logger.error("获取中奖记录列表失败,query={}", query, e);
		}
		return null;
	}


	@Override
	public Object dayDropInit(Optional<MemberSessionDto> optMember) {
		ResultDO<ActivityForDayDropInit> rDO = new ResultDO<ActivityForDayDropInit>();
		ActivityForDayDropInit model = new ActivityForDayDropInit();
		rDO.setResult(model);
		Optional<Activity> dayActivity = LotteryContainer.getInstance()
				.getActivityByName(ActivityConstant.ACTIVITY_DAY_DROP_GOLD_NAME);
		if (dayActivity.isPresent()) {
			Activity activity = dayActivity.get();
			ActivityForDayDrop dayDrop = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForDayDrop.class,
					ActivityConstant.ACTIVITY_DAY_DROP_GOLD_KEY);
		try {
			model.setStatus(activity.getActivityStatus());
			model.setStartTime(activity.getStartTime());
			model.setEndTime(activity.getEndTime());
			if(optMember.isPresent()){
				String dateStr = DateUtils.getStrFromDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3);
				ActivityLotteryResultNew newA=new ActivityLotteryResultNew();
				newA.setRemark(activity.getId()+":"+dateStr);
				newA.setActivityId(activity.getId());
				newA.setMemberId(optMember.get().getId());
				BigDecimal totalAmount=activityLotteryResultNewMapper.sumRewardInfoByProjectId(newA);
				if (activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()){
					//我的今日投资额
					model.setMyInvestAmount(totalAmount);
					ActivityLotteryResultNew totalNew=new ActivityLotteryResultNew();
					totalNew.setActivityId(activity.getId());
					totalNew.setMemberId(optMember.get().getId());
					totalNew.setStartTime(DateUtils.zerolizedTime(activity.getStartTime()));
					totalNew.setEndTime(DateUtils.getEndTime(activity.getEndTime()));
					//我的累计投资额
					model.setMyTotalInvestAmount(activityLotteryResultNewMapper.sumRewardInfoByProjectId(totalNew));
				}
			}
			//现金券剩余数量
			if(StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()==activity.getActivityStatus()){
				int red=dayDrop.getTotalRed()-RedisActivityClient.getReceivedCouponTotalForDayDrop();
				if(red<0){
					red=0;
				}
				model.setTotalRed(red);
			}
			//投资排行榜
			ActivityLotteryResultNew firstInvest=new ActivityLotteryResultNew();
			firstInvest.setActivityId(activity.getId());
			firstInvest.setStartTime(DateUtils.zerolizedTime(activity.getStartTime()));
			firstInvest.setEndTime(DateUtils.getEndTime(activity.getEndTime()));
			firstInvest.setRewardInfo("investAmount");
			model.setInvestFirstTen(activityLotteryResultManager.getFirstTotalInvestAmount(firstInvest));
		} catch (Exception e) {
			logger.error("【天降金喜】初始化失败 ", e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		}
		return rDO;
	}

	@Override
	public Object receiveCouponGold(Long memberId, int couponAmount) {
		ResultDO<Object> rDO = new ResultDO<Object>();
		try {
			String way="pc";
			rDO = activityLotteryManager.receiveCouponGold(memberId,couponAmount,way);
		} catch (Exception e) {
			logger.error("【天降金喜】领取现金券失败 memberId={}", memberId, e);
		}
		return rDO;
	}
	
	@Override
	public ResultDO<ActivityForInviteFriend> inviteFriendData(Long memberId) {
		try {
			return activityLotteryManager.inviteFriendData(memberId);
		} catch (Exception e) {
			logger.error("获取邀请好友数据异常,memberId={}", memberId, e);
		}
		return null;
	}

	@Override
	public Page<ActivityForInviteFriendDetail> inviteFriendDetail(Long memberId,Integer pageNo) {
		Page<ActivityForInviteFriendDetail> page=new Page<ActivityForInviteFriendDetail>();
		try {
			page=activityLotteryResultManager.queryInviteFriendDetail(memberId,pageNo);
		} catch (Exception e) {
			logger.error("邀请好友详情异常,memberId={}", memberId, e);
			return null;
		}
		return page;
	}
	@Override
	public Object springComingActivity(Long memberId, Long templateId) {
		ResultDO<Object> rDO = new ResultDO<Object>();
		try {
			rDO = activityLotteryManager.springComingReceiveCoupon(memberId,templateId);
		} catch (Exception e) {
			logger.error("【好春来】领取现金券失败 memberId={}", memberId, e);
		}
		return rDO;
	}
	
	@Override
	public ResultDO<Object> springComingInit(Optional<MemberSessionDto> optMember) {
		ResultDO<Object> rDO = new ResultDO<Object>();
		try {
			Long memberId = null;
			if(optMember.isPresent()){
				memberId = optMember.get().getId();
			}
			rDO = activityLotteryManager.springComingInit(memberId);
		} catch (Exception e) {
			logger.error("【好春来】初始化 memberId={}",optMember, e);
		}
		return rDO;
	}

	@Override
	public ResultDO<ActivityForLabor> laborInit(Long memberId) {
		ResultDO<ActivityForLabor> resultDO=new ResultDO<>();
		try {
			resultDO = activityLotteryManager.laborInit(memberId);
			return resultDO;
		} catch (Exception e) {
			resultDO.setSuccess(false);
			logger.error("51活动数据异常,memberId={}", memberId, e);
		}
		return resultDO;
	}

	@Override
	public ResultDO<Activity> receiveLabor(Long memberId) {
		ResultDO<Activity> resultDO=new ResultDO<>();
		try {
			resultDO = activityLotteryManager.laborGift(memberId);
			return resultDO;
		} catch (Exception e) {
			resultDO.setSuccess(false);
			logger.error("51活动领取礼包异常,memberId={}", memberId, e);
		}
		return resultDO;
	}
	
}
