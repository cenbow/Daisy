package com.yourong.api.service.impl;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;


import com.yourong.core.mc.model.biz.*;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import rop.thirdparty.com.google.common.collect.Lists;
import rop.thirdparty.com.google.common.collect.Maps;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Optional;
import com.yourong.api.dto.MemberSessionDto;
import com.yourong.api.dto.QuickRewardDto;
import com.yourong.api.dto.ResultDTO;
import com.yourong.api.service.ActivityLotteryService;
import com.yourong.api.service.BalanceService;
import com.yourong.api.service.MemberService;
import com.yourong.api.service.TransactionService;
import com.yourong.api.utils.APIPropertiesUtil;
import com.yourong.api.utils.PaltformCapitalUtils;
import com.yourong.api.utils.ServletUtil;
import com.yourong.common.cache.RedisActivityClient;
import com.yourong.common.cache.RedisManager;
import com.yourong.common.cache.RedisPlatformClient;
import com.yourong.common.constant.ActivityConstant;
import com.yourong.common.constant.Constant;
import com.yourong.common.constant.RedisConstant;
import com.yourong.common.domain.DynamicParamBuilder;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ActivityEnum;
import com.yourong.common.enums.MessageEnum;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.AES;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.CryptHelper;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.common.util.RandomUtils;
import com.yourong.common.util.RegexUtils;
import com.yourong.common.util.StringUtil;
import com.yourong.core.cms.manager.BannerManager;
import com.yourong.core.cms.model.Banner;
import com.yourong.core.common.MessageClient;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.ic.manager.ProjectExtraManager;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.model.LotteryRuleAmountNumber;
import com.yourong.core.ic.model.Project;
import com.yourong.core.ic.model.QuickRewardConfig;
import com.yourong.core.lottery.container.LotteryContainer;
import com.yourong.core.lottery.draw.DrawByPrizeDirectly;
import com.yourong.core.lottery.draw.DrawByProbability;
import com.yourong.core.lottery.manager.RedPackageManager;
import com.yourong.core.lottery.model.RewardsBase;
import com.yourong.core.lottery.model.RewardsBodyForProbility;
import com.yourong.core.lottery.model.RuleBody;
import com.yourong.core.lottery.model.RuleForRedPackage;
import com.yourong.core.lottery.validation.impl.VerificationByParticipate;
import com.yourong.core.lottery.validation.impl.VerificationByPopularity;
import com.yourong.core.mc.dao.ActivityGroupMapper;
import com.yourong.core.mc.dao.ActivityLotteryMapper;
import com.yourong.core.mc.dao.ActivityLotteryResultMapper;
import com.yourong.core.mc.dao.ActivityLotteryResultNewMapper;
import com.yourong.core.mc.dao.ActivityMessageMapper;
import com.yourong.core.mc.manager.ActivityGroupManager;
import com.yourong.core.mc.manager.ActivityHistoryManager;
import com.yourong.core.mc.manager.ActivityLotteryManager;
import com.yourong.core.mc.manager.ActivityLotteryPretreatManager;
import com.yourong.core.mc.manager.ActivityLotteryResultManager;
import com.yourong.core.mc.manager.ActivityManager;
import com.yourong.core.mc.manager.ActivityMessageManager;
import com.yourong.core.mc.manager.ChannelDataManager;
import com.yourong.core.mc.manager.CouponManager;
import com.yourong.core.mc.manager.CouponTemplateManager;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.mc.model.ActivityData;
import com.yourong.core.mc.model.ActivityGroup;
import com.yourong.core.mc.model.ActivityHistory;
import com.yourong.core.mc.model.ActivityLottery;
import com.yourong.core.mc.model.ActivityLotteryPretreat;
import com.yourong.core.mc.model.ActivityLotteryResult;
import com.yourong.core.mc.model.ActivityLotteryResultNew;
import com.yourong.core.mc.model.ActivityMessage;
import com.yourong.core.mc.model.Coupon;
import com.yourong.core.mc.model.CouponTemplate;
import com.yourong.core.mc.model.PdGeneralMonth;
import com.yourong.core.mc.model.PdRegionMonth;
import com.yourong.core.mc.model.query.ActivityLotteryResultQuery;
import com.yourong.core.tc.manager.OrderManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.Order;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.tc.model.biz.MemberTransactionCapital;
import com.yourong.core.tc.model.biz.TransactionForFirstInvestAct;
import com.yourong.core.tc.model.query.TransactionQuery;
import com.yourong.core.uc.manager.MemberInfoManager;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.manager.MemberTokenManager;
import com.yourong.core.uc.manager.MemberVipManager;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberInfo;
import com.yourong.core.uc.model.MemberToken;


@Service
public class ActivityLotteryServiceImpl implements ActivityLotteryService {
	private static final Logger logger = LoggerFactory.getLogger(ActivityLotteryServiceImpl.class);

	@Autowired
	private MemberManager memberManager;

	@Autowired
	private TransactionManager transactionManager;
	
	@Autowired
	private TransactionService transactionService;

	@Autowired
	private ActivityManager activityManager;

	@Autowired
	private ActivityLotteryResultManager activityLotteryResultManager;

	@Autowired
	private DrawByPrizeDirectly drawByPrizeDirectly;

	@Autowired
	private DrawByProbability drawByProbability;

	@Autowired
	private ActivityMessageManager activityMessageManager;

	@Autowired
	private CouponManager couponManager;

	@Autowired
	private ActivityLotteryManager activityLotteryManager;
	
	@Autowired
	private ActivityLotteryResultNewMapper activityLotteryResultNewMapper;
	

	@Autowired
	private ActivityLotteryPretreatManager activityLotteryPretreatManager;

	@Autowired
	private OrderManager orderManager;

	@Autowired
	private ProjectExtraManager projectExtraManager;

	@Autowired
	private RedPackageManager redPackageManager;

	@Autowired
	private ProjectManager projectManager;
	
	@Autowired
	private MemberService memberService;

	@Autowired
	private VerificationByParticipate verificationByParticipate;

	@Autowired
	private CouponTemplateManager couponTemplateManager;
	
	@Autowired
	private MemberInfoManager memberInfoManager;
	
	@Autowired
	private VerificationByPopularity verificationByPopularity;
	
	@Autowired
	private BalanceManager balanceManager;
	
	@Autowired
	private ActivityHistoryManager activityHistoryManager;
	
	@Autowired
	private ActivityGroupManager activityGroupManager;
	
	@Autowired
	private ActivityGroupMapper activityGroupMapper;
	
	
	@Autowired
	private ActivityLotteryResultMapper activityLotteryResultMapper;
	
	
	@Autowired
	private ActivityLotteryMapper activityLotteryMapper;
	
	@Autowired
	private ActivityMessageMapper activityMessageMapper;
	
	
	@Autowired
	private ChannelDataManager channelDataManager;
	
	@Autowired
	private MemberTokenManager memberTokenManager;
	
	@Autowired
	private MemberVipManager memberVipManager;
	
	@Autowired
	private BalanceService balanceService;

	@Autowired
	private BannerManager bannerManager;

	@Resource
	private ThreadPoolTaskExecutor taskExecutor;

	@Override
	public ResultDTO<Object> anniversaryGetRed(String param, String mobile) {
		ResultDTO<Object> resultDTO = new ResultDTO<>();
		resultDTO.setIsError();
		try {
			if (StringUtil.isBlank(param) || mobile == null || !RegexUtils.checkMobileLocal(mobile)) {
				resultDTO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
				return resultDTO;
			}
			String activityIdStr = PropertiesUtil.getProperties("activity.anniversary.red.id");
			Long actId = Long.parseLong(activityIdStr);
			if (drawByPrizeDirectly.isInActivityTime(actId)) {
				// 判断领红包次数是否达到上限
				String participate = PropertiesUtil.getProperties("activity.anniversary.red.participate");
				boolean isParticipate = false;
				if (!RedisActivityClient.checkActivityIsParticipate(actId, Long.parseLong(mobile), Integer.parseInt(participate))) {
					// 达到次数规定
					isParticipate = true;
				}
				Member member = memberManager.selectByMobile(Long.parseLong(mobile));
				AES aes = AES.getInstance();
				param = URLDecoder.decode(param, Constant.DEFAULT_CODE);
				List<String> strings = aes.tokenDecrypt(param);
				String transactionId = strings.get(0);
				String inputMemberId = strings.get(1);
				Long startTime = Long.parseLong(strings.get(2));
				Long endTime = Long.parseLong(strings.get(3));
				if (DateUtils.getCurrentDate().getTime() >= startTime && DateUtils.getCurrentDate().getTime() <= endTime) {
					Long activityMember = Long.parseLong(inputMemberId);
					if (member != null && member.getId().equals(activityMember)) {
						RuleBody ruleBody = new RuleBody();
						ruleBody.setActivityId(actId);
						ruleBody.setMemberId(member.getId());
						ruleBody.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
						ruleBody.setCycleStr(transactionId.toString());
						boolean drawFlag = drawByPrizeDirectly.validate(ruleBody, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode());
						if (!isParticipate && drawFlag) {
							// 记录抽奖次数
							Long participateNum = RedisActivityClient.saveActivityParticipateNum(actId, Long.parseLong(mobile), 1);
							if (participateNum == null) {
								participateNum = 0l;
							}
							if (participateNum > Long.parseLong(participate)) {
								logger.error("周年庆抽奖次数超过上限, mobile={}, participateNum={}", mobile, participateNum);
								// 达到次数规定
								resultDTO.setResult(ResultCode.ANNIVERSARY_RED_PARTICIPATE_ERROR.getMsg());
								return resultDTO;
							}
							// 获得0.2%人气值
							Transaction t = transactionManager.selectTransactionById(Long.parseLong(transactionId));
							int value = t.getInvestAmount().multiply(Constant.AnniversarySharePercent).intValue();
							if (value == 0) {
								resultDTO.setResultCode(ResultCode.ANNIVERSARY_SHARE_INVEST_ERROR);
								;
							}
							RewardsBase rBase = new RewardsBase();
							rBase.setRewardCode("renQiZhi0.2%");
							rBase.setRewardName("投资额0.2%的人气值");
							rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_POPULARITY.getType());
							rBase.setRewardValue(value);
							RewardsBase rb = (RewardsBase) drawByPrizeDirectly.drawLottery(ruleBody, rBase,
									TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode());
							MessageClient.sendMsgForSPEngin(member.getId(), "【干杯！我们的纪念日】投资分红包", rb.getRewardValue() + "点人气值");
							resultDTO.setResult(rb.getRewardValue().toString() + "点人气值");
						} else if (!drawFlag) {
							resultDTO.setResult("已抢过");
						} else {
							resultDTO.setResult(ResultCode.ANNIVERSARY_RED_PARTICIPATE_ERROR.getMsg());
						}
						resultDTO.setIsSuccess();
						return resultDTO;
					} else if (member != null) {
						// 抽奖
						RuleBody ruleBody = new RuleBody();
						ruleBody.setActivityId(actId);
						ruleBody.setMemberId(member.getId());
						ruleBody.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
						ruleBody.setCycleStr(transactionId.toString());
						ruleBody.setRewardsAvailableNum(4);
						ruleBody.setRewardsPoolMaxNum(4);
						ruleBody.setRepeatDrawFlag(false);
						boolean drawFlag = drawByProbability.validate(ruleBody, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode());
						if (!isParticipate && drawFlag) {
							// 记录抽奖次数
							Long participateNum = RedisActivityClient.saveActivityParticipateNum(actId, Long.parseLong(mobile), 1);
							if (participateNum == null) {
								participateNum = 0l;
							}
							if (participateNum > Long.parseLong(participate)) {
								logger.error("周年庆抽奖次数超过上限, mobile={}, participateNum={}", mobile, participateNum);
								// 达到次数规定
								resultDTO.setResult(ResultCode.ANNIVERSARY_RED_PARTICIPATE_ERROR.getMsg());
								return resultDTO;
							}
							RewardsBodyForProbility rfp = (RewardsBodyForProbility) drawByProbability.drawLottery(ruleBody, null,
									TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode());
							MessageClient.sendMsgForSPEngin(member.getId(), "【干杯！我们的纪念日】投资分红包", rfp.getRewardValue() + "点人气值");
							resultDTO.setResult(rfp.getRewardValue() + "点人气值");
						} else if (!drawFlag) {
							resultDTO.setResult("已抢过");
						} else {
							resultDTO.setResult(ResultCode.ANNIVERSARY_RED_PARTICIPATE_ERROR.getMsg());
						}
						resultDTO.setIsSuccess();
						return resultDTO;
					} else {
						// 非会员
						String retCode = outsiderAnniversaryGetRed(actId, Long.parseLong(mobile), transactionId.toString(), isParticipate);
						if (StringUtil.isNotBlank(retCode)) {
							resultDTO.setResult(retCode);
							return resultDTO;
						} else {
							resultDTO.setResultCode(ResultCode.ANNIVERSARY_RED_OUTSIDER_ERROR);
							return resultDTO;
						}
					}
				} else {
					resultDTO.setResultCode(ResultCode.ANNIVERSARY_REDURL_TIMEOUT_ERROR);
					return resultDTO;
				}
			} else {
				resultDTO.setResultCode(ResultCode.ANNIVERSARY_REDURL_TIMEOUT_ERROR);
				return resultDTO;
			}
		} catch (Exception e) {
			logger.error("周年庆-瓜分红包失败 param={}", param, e);
			resultDTO.setResultCode(ResultCode.ERROR_SYSTEM);
			if (e.getClass().equals(ManagerException.class) && e.getMessage().startsWith("重复参加活动")) {
				resultDTO.setResultCode(ResultCode.ACTIVITY_YET_JOIN_ACTIVITY_ERROR);
			} else {
				resultDTO.setResultCode(ResultCode.ERROR_SYSTEM);
			}
		}
		return resultDTO;
	}

	/**
	 * 周年庆-非会员抽红包简易实现
	 * 
	 * @return
	 */
	private String outsiderAnniversaryGetRed(Long activityId, Long mobile, String uniqueCode, boolean isParticipate) throws Exception {
		String code = RedisActivityClient.getActivityOutsiderMobile(activityId, mobile, uniqueCode);
		if (StringUtil.isNotBlank(code)) {
			return "已抢过";
		} else if (!isParticipate) {
			String[] rewards = new String[] { "PopularityFor5", "PopularityFor6", "PopularityFor7", "PopularityFor8" };
			Integer[] rewardValues = new Integer[] { 5, 6, 7, 8 };
			code = null;
			// 简易抽奖
			BigDecimal[] propbilities = new BigDecimal[] { new BigDecimal("0.5"), new BigDecimal("0.7"), new BigDecimal("0.9"),
					new BigDecimal("1.0") };
			BigDecimal randomNumber = new BigDecimal(Math.random());
			Integer value = null;
			for (int i = 0; i < propbilities.length; i++) {
				if (randomNumber.doubleValue() <= propbilities[i].doubleValue()) {
					code = rewards[i];
					value = rewardValues[i];
					break;
				}
			}
			if (StringUtil.isNotBlank(code)) {
				// 记录抽奖次数
				Long participateNum = RedisActivityClient.saveActivityParticipateNum(activityId, mobile, 1);
				if (participateNum == null) {
					participateNum = 0l;
				}
				if (participateNum > 2l) {
					logger.error("周年庆抽奖次数超过上限, mobile={}, participateNum={}", mobile, participateNum);
					// 达到次数规定
					return ResultCode.ANNIVERSARY_RED_PARTICIPATE_ERROR.getMsg();
				}
				RedisActivityClient.setActivityOutsiderMobile(activityId, mobile, uniqueCode, code);
				// 记录抽奖次数
				// RedisActivityClient.saveActivityParticipateNum(activityId,
				// mobile, 1);
				return value.toString() + "点人气值";
			}
		} else {
			return ResultCode.ANNIVERSARY_RED_PARTICIPATE_ERROR.getMsg();
		}
		return null;
	}

	@Override
	public Activity getActivityById(Long activityId) {
		try {
			return activityManager.selectByPrimaryKey(activityId);
		} catch (ManagerException e) {
			logger.error("获取活动失败 activityId={}", activityId, e);
		}
		return null;
	}

	@Override
	public Integer getMillionCouponFund() {
		try {
			String activityIdStr = PropertiesUtil.getProperties("activity.millionCoupon.id");
			Long activityId = Long.parseLong(activityIdStr);
			Optional<Activity> optActivity = LotteryContainer.getInstance().getActivity(activityId);
			if (optActivity.isPresent()) {
				if (optActivity.get().getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_AUDIT.getStatus()) {
					return Integer.parseInt(Constant.ACTIVITY_MILLIONCOUPON_FUND);
				} else if (optActivity.get().getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
					return RedisActivityClient.millionCoupon(null);
				} else {
					return 0;
				}
			} else {
				return 0;
			}
		} catch (Exception e) {
			logger.error("百万现金券活动显示余额失败", e);
		}
		return 0;
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

	@Override
	public Object springFestivalInit(Optional<MemberSessionDto> optOfMember) {
		ResultDTO<ActivityForSpringFestival> rDO = new ResultDTO<ActivityForSpringFestival>();
		try {
			// 春节活动
			Optional<Activity> optOfSpring = LotteryContainer.getInstance().getActivityByName(Constant.SPRING_FESTIVAL_NAME);
			if (!optOfSpring.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			Activity activity = optOfSpring.get();
			// 匹配除夕领券时间
			ActivityForSpringFestival springFestival = LotteryContainer.getInstance().getObtainConditions(activity,
					ActivityForSpringFestival.class, RedisConstant.REDIS_KEY_ACTIVITY_SPRING);
			springFestival.setCouponTemplateId(null);
			springFestival.setActivityStatus(activity.getActivityStatus());
			springFestival.setStartTime(activity.getStartTime());
			springFestival.setEndTime(activity.getEndTime());
			if (activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_AUDIT.getStatus()) {
				// 活动未开始，返回活动状态
				rDO.setResult(springFestival);
				return rDO;
			}
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
			// 判断领取压岁钱的个数
			ActivityLotteryResult model = new ActivityLotteryResult();
			model.setActivityId(activity.getId());
			model.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
			int participateNum = activityLotteryManager.countNewLotteryResult(model);
			springFestival.setReceiveCouponNum(participateNum);
			springLoadRedBag(activity.getId(), springFestival);
			rDO.setResult(springFestival);
		} catch (Exception e) {
			logger.error("春节初始化失败", e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
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
			if (optOfActivity.get().getActivityStatus() != StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
				return rDO;
			}
			int updateNum = activityMessageManager.insert(Constant.SPRING_FESTIVAL_NAME, memberId, messageTemplateId);
			if (updateNum == 1) {
				String wishKey = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR + RedisConstant.REDIS_KEY_ACTIVITY_SPRING
						+ RedisConstant.REDIS_SEPERATOR + RedisConstant.REDIS_KEY_ACTIVITY_SPRING_WISHLIST;
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
		ResultDTO<Object> rDTO = new ResultDTO<Object>();
		try {
			ResultDO<Object> rDO = activityLotteryManager.springFestivalReceiveCoupon(memberId);
			if (rDO.isSuccess()) {
				rDTO.setIsSuccess();
			} else {
				rDTO.setIsError();
				rDTO.setResultCode(rDO.getResultCode());
			}
		} catch (Exception e) {
			logger.error("春节领券失败 memberId={}", memberId, e);
			rDTO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDTO;
	}

	@Override
	public Object getTransactionRedBagUrl(Long orderId, Long memberId) {
		ResultDTO<Object> resultDTO = new ResultDTO<>();
		resultDTO.setIsSuccess();
		try {
			if (orderId == null || orderId.longValue() == 0L) {
				resultDTO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
				return resultDTO;
			}
			Order o = orderManager.selectByPrimaryKey(orderId);
			Transaction t = transactionManager.getTransactionByOrderId(orderId);
			if (o == null) {
				resultDTO.setResultCode(ResultCode.TRANSACTION_NOT_EXIST_ERROR);
				return resultDTO;
			}
			if (!memberId.equals(o.getMemberId())) {
				resultDTO.setResultCode(ResultCode.REDPACKAGE_CHECK_INVESTSELF_ERROR);
				return resultDTO;
			}
			PopularityRedBagBiz biz = new PopularityRedBagBiz();
			biz.setOrderStatus(o.getStatus());
			if (t == null || o.getStatus() != StatusEnum.ORDER_PAYED_INVESTED.getStatus()) {
				logger.info("红包分享失败 订单状态={} orderId={}", o.getStatus(), orderId);
				biz.setActivity(false);
				resultDTO.setResult(biz);
				resultDTO.setIsSuccess();
				return resultDTO;
			}
			ActivityBiz activityBiz = new ActivityBiz();
			activityBiz.setBizType(TypeEnum.ACTIVITY_BIZ_TYPE_TRANSACTION.getType());
			activityBiz.setRuleType(ActivityConstant.ACTIVITY_RULE_TYPE_POPULARITY_RED_PACKAGE);
			List<ActivityBiz> actList = activityManager.findInProgressActivityByBizType(activityBiz);
			ActivityBiz actBiz = null;
			// 不存在进行中的红包活动
			if (Collections3.isEmpty(actList)) {
				biz.setActivity(false);
				resultDTO.setResult(biz);
				resultDTO.setIsSuccess();
				return resultDTO;
			}
			// 校验红包规则
			actBiz = actList.get(0);
			RuleForRedPackage rule = LotteryContainer.getInstance().getRuleConditions(actBiz, RuleForRedPackage.class,
					actBiz.getId().toString());
			ResultDO<Object> rDO = new ResultDO<Object>();
			redPackageManager.checkRedPackage(rule, t, rDO, TypeEnum.ACTIVITY_SOURCE_TYPE_JOIN.getType());
			if (rDO.isSuccess()) {
				String encryptCode = activityLotteryManager.encryptRedBag(actBiz.getId(), t.getId());
				String redBagCode = APIPropertiesUtil.getProperties("redPackage.share.url") + encryptCode;
				biz.setRedBagCode(redBagCode);
				biz.setActivity(true);
				biz.setWechatShareFriends(rule.getWechatShareFriends());
				biz.setWechatShareCircle(rule.getWechatShareCircle());
				resultDTO.setResult(biz);
				resultDTO.setIsSuccess();
				return resultDTO;
			}
			biz.setActivity(false);
			resultDTO.setResult(biz);
			resultDTO.setIsSuccess();
			return resultDTO;
		} catch (Exception e) {
			logger.error("生成红包链接失败 orderId={}", orderId, e);
			resultDTO.setResultCode(ResultCode.ERROR_SYSTEM);
			return resultDTO;
		}
	}

	/**
	 * 
	 * @Description:获取红包领取记录
	 * @param mobile
	 * @param transactionId
	 * @param redBag
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年1月25日 下午1:23:40
	 */
	private ActivityForRedBag queryRedBagRecord(Long mobile, Long transactionId, ActivityForRedBag redBag) throws ManagerException {
		ActivityLotteryPretreat model = new ActivityLotteryPretreat();
		model.setSourceId(transactionId);
		List<ActivityLotteryPretreat> list = activityLotteryPretreatManager.selectClaimedBySourceId(model);
		if (Collections3.isEmpty(list)) {
			return redBag;
		}
		List<ActivityForRedBag> bagList = Lists.newArrayListWithCapacity(list.size());
		for (ActivityLotteryPretreat pretreat : list) {
			ActivityForRedBag bag = new ActivityForRedBag();
			// 如果是已抢过则从查询数组里获取这个用户的历史值
			if (redBag.getRewardValue() == null && pretreat.getMobile().equals(mobile)) {
				BigDecimal rewardValue = new BigDecimal(pretreat.getRewardValue());
				bag.setMobile(mobile);
				redBag.setRewardValue(rewardValue);
			}
			if (StringUtil.isNotBlank(pretreat.getActivityRole())) {
				bag.setActivityRole(pretreat.getActivityRole());
			}
			bag.setReceiveFlag(pretreat.getReceiveFlag());
			bag.setRewardValue(new BigDecimal(pretreat.getRewardValue()));
			String mobileStr = pretreat.getMobile().toString();
			mobileStr = StringUtil.maskString(mobileStr, com.yourong.common.util.StringUtil.ASTERISK, 3, 2, 4);
			bag.setMobileStr(mobileStr);
			bag.setReceiveTime(pretreat.getClaimTime());
			bagList.add(bag);
		}
		redBag.setReceiveList(bagList);
		return redBag;
	}

	@Override
	public boolean hasActivityFlag(Order order, int activitySourceType) {
		try {
			if (activitySourceType == TypeEnum.ACTIVITY_SOURCE_TYPE_CREATE.getType()) {
				Project project = projectManager.selectByPrimaryKey(order.getProjectId());
				if (project == null || project.isDirectProject()) {
					return false;
				}
			}
			ActivityBiz activityBiz = new ActivityBiz();
			activityBiz.setBizType(TypeEnum.ACTIVITY_BIZ_TYPE_TRANSACTION.getType());
			activityBiz.setRuleType(ActivityConstant.ACTIVITY_RULE_TYPE_POPULARITY_RED_PACKAGE);
			List<ActivityBiz> actList = activityManager.findInProgressActivityByBizType(activityBiz);
			if (Collections3.isEmpty(actList)) {
				return false;
			}
			ActivityBiz actBiz = actList.get(0);
			RuleForRedPackage rule = LotteryContainer.getInstance().getRuleConditions(actBiz, RuleForRedPackage.class,
					actBiz.getId().toString());
			// 模拟一个交易校验是否符合发红包的条件
			Transaction t = new Transaction();
			t.setOrderId(order.getId());
			t.setInvestAmount(order.getInvestAmount());
			t.setTransactionTime(DateUtils.getCurrentDate());
			t.setProjectId(order.getProjectId());
			ResultDO<Object> rDO = new ResultDO<Object>();
			redPackageManager.checkRedPackage(rule, t, rDO, TypeEnum.ACTIVITY_SOURCE_TYPE_JOIN.getType());
			if (rDO.isSuccess()) {
				return true;
			}
		} catch (Exception e) {
			logger.error("支付后判断是否有活动失败, orderId={}", order.getId(), e);
		}
		return false;
	}

	@Override
	public Object redBagInit(String redBagCode) {
		ResultDTO<Object> resultDTO = new ResultDTO<Object>();
		ActivityForRedBag redBag = new ActivityForRedBag();
		Map<String,Object> map=new HashMap<>();
		resultDTO.setIsSuccess();
		try {
			Banner banner= bannerManager.queryBannerByType(5);
			if (banner!=null){
				redBag.setImage(banner.getImage());
				redBag.setHref(banner.getHref());
				map.put("image",banner.getImage());
				map.put("href",banner.getHref());
			}
			// 校验入参
			if (StringUtil.isBlank(redBagCode)) {
				resultDTO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
				resultDTO.setResult(map);
				return resultDTO;
			}
			// 红包解密
			String decryptCode = CryptHelper.decryptByase(redBagCode);
			String[] decryptArr = decryptCode.split(ActivityConstant.redBagCodeSplit);
			Long activityId = new Long(decryptArr[0]);
			Long transactionId = new Long(decryptArr[1]);
			// 校验交易是否存在
			Transaction t = transactionManager.selectTransactionById(transactionId);
			if (t == null) {
				resultDTO.setResult(map);
				resultDTO.setResultCode(ResultCode.TRANSACTION_NOT_EXIST_ERROR);
				return resultDTO;
			}
			ActivityBiz activityBiz = new ActivityBiz();
			activityBiz.setBizType(TypeEnum.ACTIVITY_BIZ_TYPE_TRANSACTION.getType());
			activityBiz.setRuleType(ActivityConstant.ACTIVITY_RULE_TYPE_POPULARITY_RED_PACKAGE);
			List<ActivityBiz> actList = activityManager.findInProgressActivityByBizType(activityBiz);
			// 不存在进行中的红包活动
			if (Collections3.isEmpty(actList)) {
				resultDTO.setResult(map);
				resultDTO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return resultDTO;
			}
			// 校验红包规则
			ActivityBiz actBiz = actList.get(0);
			if (!actBiz.getId().equals(activityId)) {
				// 不是同一个活动的红包
				resultDTO.setResult(map);
				resultDTO.setResultCode(ResultCode.REDPACKAGE_CHECK_ACTIVITY_ERROR);
				return resultDTO;
			}
			RuleForRedPackage rule = LotteryContainer.getInstance().getRuleConditions(actBiz, RuleForRedPackage.class,
					actBiz.getId().toString());
			ResultDO<Object> rDO = new ResultDO<Object>();
			redPackageManager.checkRedPackage(rule, t, rDO, TypeEnum.ACTIVITY_SOURCE_TYPE_JOIN.getType());
			if (rDO.isError()) {
				resultDTO.setResultCode(rDO.getResultCode());
				if (rDO.getResultCode().getCode().equals(ResultCode.REDPACKAGE_CHECK_TIME_OUT_ERROR.getCode())) {
					// 已过期直接返回
					resultDTO.setResult(map);
					return resultDTO;
				}
				return getRedPackageReceiveList(resultDTO, redBag, t, actBiz);
			}
			String key = RedisConstant.REDIS_KEY_ACTIVITY_REDBAG_VALUES + RedisConstant.REDIS_SEPERATOR + transactionId.toString();
			if (RedisManager.isExit(key)) {
				// 存在未领红包
				redBag.setHasEmpty(false);
				resultDTO.setResult(map);
				return resultDTO;
			}
			// 获取红包领取记录
			return getRedPackageReceiveList(resultDTO, redBag, t, actBiz);
		} catch (Exception e) {
			logger.error("红包初始化失败, redBagCode={}", redBagCode, e);
			resultDTO.setResult(map);
			resultDTO.setResultCode(ResultCode.ERROR_SYSTEM);
			return resultDTO;
		}
	}

	private Object getRedPackageReceiveList(ResultDTO<Object> resultDTO, ActivityForRedBag redBag, Transaction transaction,
			ActivityBiz activityBiz) throws ManagerException {
		ActivityLotteryPretreat model = new ActivityLotteryPretreat();
		model.setSourceId(transaction.getId());
		List<ActivityLotteryPretreat> list = activityLotteryPretreatManager.selectClaimedBySourceId(model);
		if (Collections3.isEmpty(list)) {
			// 没有领取记录 且redis没值需要重新生成红包
			redPackageManager.popularityRedPackage(activityBiz, transaction);
			redBag.setHasEmpty(false);
			resultDTO.setResult(redBag);
			return resultDTO;
		}
		List<ActivityForRedBag> bagList = Lists.newArrayListWithCapacity(list.size());
		for (ActivityLotteryPretreat pretreat : list) {
			ActivityForRedBag bag = new ActivityForRedBag();
			if (StringUtil.isNotBlank(pretreat.getActivityRole())) {
				bag.setActivityRole(pretreat.getActivityRole());
			}
			bag.setReceiveFlag(pretreat.getReceiveFlag());
			bag.setRewardValue(new BigDecimal(pretreat.getRewardValue()));
			String mobileStr = pretreat.getMobile().toString();
			mobileStr = StringUtil.maskString(mobileStr, com.yourong.common.util.StringUtil.ASTERISK, 3, 2, 4);
			bag.setMobileStr(mobileStr);
			bag.setReceiveTime(pretreat.getClaimTime());
			bagList.add(bag);
		}
		redBag.setHasEmpty(true);
		redBag.setReceiveList(bagList);
		resultDTO.setResult(redBag);
		return resultDTO;
	}

	@Override
	public Object redBagReceive(String redBagCode, Long mobile) {
		ResultDTO<Object> resultDTO = new ResultDTO<Object>();
		resultDTO.setIsSuccess();
		try {
			// 校验入参
			if (StringUtil.isBlank(redBagCode) || mobile == null || !RegexUtils.checkMobileLocal(mobile.toString())) {
				resultDTO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
				return resultDTO;
			}
			// 红包解密
			String decryptCode = CryptHelper.decryptByase(redBagCode);
			String[] decryptArr = decryptCode.split(ActivityConstant.redBagCodeSplit);
			Long activityId = new Long(decryptArr[0]);
			Long transactionId = new Long(decryptArr[1]);
			// 校验交易是否存在
			Transaction t = transactionManager.selectTransactionById(transactionId);
			if (t == null) {
				resultDTO.setResultCode(ResultCode.TRANSACTION_NOT_EXIST_ERROR);
				return resultDTO;
			}
			ActivityBiz activityBiz = new ActivityBiz();
			activityBiz.setBizType(TypeEnum.ACTIVITY_BIZ_TYPE_TRANSACTION.getType());
			activityBiz.setRuleType(ActivityConstant.ACTIVITY_RULE_TYPE_POPULARITY_RED_PACKAGE);
			List<ActivityBiz> actList = activityManager.findInProgressActivityByBizType(activityBiz);
			// 不存在进行中的红包活动
			if (Collections3.isEmpty(actList)) {
				resultDTO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return resultDTO;
			}
			// 校验红包规则
			ActivityBiz actBiz = actList.get(0);
			if (!actBiz.getId().equals(activityId)) {
				// 不是同一个活动的红包
				resultDTO.setResultCode(ResultCode.REDPACKAGE_CHECK_ACTIVITY_ERROR);
				return resultDTO;
			}
			RuleForRedPackage rule = LotteryContainer.getInstance().getRuleConditions(actBiz, RuleForRedPackage.class,
					actBiz.getId().toString());
			ResultDO<Object> rDO = new ResultDO<Object>();
			redPackageManager.checkRedPackage(rule, t, rDO, TypeEnum.ACTIVITY_SOURCE_TYPE_JOIN.getType());
			if (rDO.isError()) {
				resultDTO.setResultCode(rDO.getResultCode());
				return resultDTO;
			}
			String key = RedisConstant.REDIS_KEY_ACTIVITY_REDBAG_VALUES + RedisConstant.REDIS_SEPERATOR + transactionId.toString();
			ActivityForRedBag redBag = new ActivityForRedBag();
			if (RedisManager.isExit(key)) {
				// 领红包主程序
				redBag = redPackageManager.receivePopularityRedPackage(mobile, t, actBiz, rule);
				logger.info("领取红包, mobile={}, 返回数据={}", mobile, redBag);
				// 获取红包领取记录
				queryRedBagRecord(mobile, transactionId, redBag);
				redBag.setReceive(mobile);
				resultDTO.setResult(redBag);
				return resultDTO;
			}
			queryRedBagRecord(mobile, transactionId, redBag);
			redBag.setHasEmpty(true);
			redBag.setReceive(mobile);
			resultDTO.setResult(redBag);
			return resultDTO;
		} catch (Exception e) {
			logger.error("领红包失败, redBagCode={} mobile={}", redBagCode, mobile, e);
		}
		return resultDTO;
	}

	@Override
	public Object mayDay4GiftsInit(DynamicParamBuilder paramBuilder) {
		ResultDTO<ActivityForMayDay4Gifts> rDO = new ResultDTO<ActivityForMayDay4Gifts>();
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
			if (!paramBuilder.isMemberFlag() || StatusEnum.ACTIVITY_STATUS_IS_START.getStatus() != mayDay.getActivityStatus()) {
				return rDO;
			}
			// 用户已登录，查询当天总投资额
			Long memberId = paramBuilder.getMemberId();
			Date nowDate = DateUtils.getCurrentDate();
			Date startTime = DateUtils.zerolizedTime(nowDate);
			Date endTime = DateUtils.getEndTime(nowDate);
			BigDecimal totalAmount = getTotalAmountByMemberId(memberId, startTime, endTime);
			model.setInvestAmount(totalAmount);
			// 判断券的领用情况
			String cycleStr = DateUtils.getDateStrFromDate(nowDate);
			Map<String, Object> queryMap = Maps.newHashMap();
			queryMap.put("memberId", memberId);
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
	public Object mayDay4GiftsReceive(DynamicParamBuilder paramBuilder) {
		ResultDTO<ActivityForMayDay4Gifts> rDO = new ResultDTO<ActivityForMayDay4Gifts>();
		rDO.setIsSuccess();
		try {
			if (!paramBuilder.isMemberFlag() || !paramBuilder.getParamMap().containsKey("couponAmount")) {
				rDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
				return rDO;
			}
			Long memberId = paramBuilder.getMemberId();
			int couponAmount = Integer.valueOf(paramBuilder.getParamMap().get("couponAmount").toString());
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
	private void receiveCouponCommon(Long memberId, Activity activity, RewardsBase rewardsBase, String cycleStr, ResultDTO<?> rDO)
			throws Exception {
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
	public Object popRedPackageMineInit(DynamicParamBuilder paramBuilder) {
		ResultDO<ActivityForMyRedPackageInfo> rDO = new ResultDO<ActivityForMyRedPackageInfo>();
		ActivityForMyRedPackageInfo model = new ActivityForMyRedPackageInfo();
		rDO.setResult(model);
		try {
			
			ActivityBiz activityBiz = new ActivityBiz();
			activityBiz.setBizType(TypeEnum.ACTIVITY_BIZ_TYPE_TRANSACTION.getType());
			activityBiz.setRuleType(ActivityConstant.ACTIVITY_RULE_TYPE_POPULARITY_RED_PACKAGE);
			List<ActivityBiz> actList = activityManager.findInProgressActivityByBizType(activityBiz);
			// 不存在进行中的红包活动
			if (Collections3.isEmpty(actList)) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			// 校验红包规则
			ActivityBiz actBiz = actList.get(0);
			// 统计平台总共发出
			String totalKey = RedisConstant.REDIS_KEY_ACTIVITY_REDBAG_TOTALNUM + RedisConstant.REDIS_SEPERATOR + actBiz.getId();
			String platformTotalSend = RedisManager.get(totalKey);
			model.setPlatformTotalSend(StringUtil.isBlank(platformTotalSend) ? 0 : Integer.valueOf(platformTotalSend));
			// 统计平台总共领取
			String claimKey = RedisConstant.REDIS_KEY_ACTIVITY_REDBAG_CLAIMNUM + RedisConstant.REDIS_SEPERATOR + actBiz.getId();
			String platformTotalReceive = RedisManager.get(claimKey);
			model.setPlatformTotalReceive(StringUtil.isBlank(platformTotalReceive) ? 0 : Integer.valueOf(platformTotalReceive));
			if (!paramBuilder.isMemberFlag()) {
				rDO.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
				return rDO;
			}
			// 统计总共发了多少人气值
			ActivityLotteryPretreat query = new ActivityLotteryPretreat();
			query.setActivityId(actBiz.getId());
			query.setSourceHolder(paramBuilder.getMemberId());
			query.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_POPULARITY.getType());
			model.setSendTotalPrize(activityLotteryPretreatManager.sendTotalPrize(query));
			// 统计总共抢了多少人气值
			Member member = memberManager.selectByPrimaryKey(paramBuilder.getMemberId());
			query.setMobile(member.getMobile());
			model.setReceiveTotalPrize(activityLotteryPretreatManager.receiveTotalPrize(query));
			// 统计总共抢了多少次手气最佳
			query.setActivityRole(ActivityConstant.ACTIVITY_ROLE_TOP);
			model.setTotalTop(activityLotteryPretreatManager.totalTop(query));
			//头像
			String avatar = memberService.getMemberAvatar(paramBuilder.getMemberId());
			model.setAvatarImg(avatar);
		} catch (Exception e) {
			logger.error("红包适配页初始化失败", e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}

	@Override
	public Object fellInLoveFor520Init(DynamicParamBuilder paramBuilder) {
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
			if (paramBuilder.isMemberFlag()) {
				Long memberId = paramBuilder.getMemberId();
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

	@Override
	public Object break2BillionInit(DynamicParamBuilder paramBuilder) {
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
			if (!paramBuilder.isMemberFlag()) {
				return rDO;
			}
			// 登录会员判断是否抽奖
			Long memberId = paramBuilder.getMemberId();
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
	public Object break2BillionReceive(DynamicParamBuilder paramBuilder) {
		ResultDTO<ActivityForBreak2Billion> rDO = new ResultDTO<ActivityForBreak2Billion>();
		ActivityForBreak2Billion model = new ActivityForBreak2Billion();
		rDO.setResult(model);
		try {
			Long memberId = paramBuilder.getMemberId();
			int activityPart = 0;
			if (paramBuilder.getParamMap().containsKey("activityPart")) {
				activityPart = Integer.valueOf(paramBuilder.getParamMap().get("activityPart").toString());
			}
			model.setActivityPart(activityPart);
			if (memberId == null || activityPart < 1 || activityPart > 2) {
				rDO.setResultCode(ResultCode.ERROR_SYSTEM_REQUEST_PARAM_DECODE);
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
			logger.error("破二十亿活动互动失败", e);
		}
		return rDO;
	}

	private void break2BillionLottery(Long memberId, ResultDTO<ActivityForBreak2Billion> rDO, Activity activity,
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

	private void break2BillionGift(Long memberId, ResultDTO<ActivityForBreak2Billion> rDO, Activity activity,
			ActivityForBreak2Billion rule,
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
				logger.error("优惠券赠送失败，接口返回Coupon=null!, memberId={}, activityId={}, templateId={}", memberId, activity.getId(), templateId);
			}
		}
		// 站内信
		MessageClient.sendMsgForSPEngin(memberId, activity.getActivityDesc(), ActivityConstant.ACTIVITY_2_BREAK_BILLION_PART3);
		model.setGiftIndex(index);
	}

	@Override
	public Object inviteFriendInit(DynamicParamBuilder paramBuilder) {
        ResultDTO<ActivityForFriendShip> rDO = new ResultDTO<ActivityForFriendShip>();
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
			if (!paramBuilder.isMemberFlag()) {
				return rDO;
			}
			Long memberId = paramBuilder.getMemberId();
			model.setActivityStatus(activity.getActivityStatus());
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
			paraMap.put("cycleConstraint", "packages");
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
	public Object inviteFriendReceive(DynamicParamBuilder paramBuilder) {
		ResultDTO<Object> rDO = new ResultDTO<Object>();
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
			if (!paramBuilder.isMemberFlag() || !paramBuilder.getParamMap().containsKey("rewardType")) {
				rDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
				return rDO;
			}
			Long memberId = paramBuilder.getMemberId();
			int rewardType = Integer.valueOf(paramBuilder.getParamMap().get("rewardType").toString());
			model.setRewardType(rewardType);
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
	public Object inviteFriendList(DynamicParamBuilder paramBuilder) {
		ResultDTO<ActivityForFriendShip> rDO = new ResultDTO<ActivityForFriendShip>();
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
			if (!paramBuilder.isMemberFlag() || !paramBuilder.getParamMap().containsKey("startNo")) {
				rDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
				return rDO;
			}
			Long memberId = paramBuilder.getMemberId();
			model.setAvatar(ServletUtil.getMemberAvatarById(memberId));
			int startNo = Integer.valueOf(paramBuilder.getParamMap().get("startNo").toString());
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
	public Object saveEvaluation(DynamicParamBuilder paramBuilder) { 
		ResultDTO<MemberInfo> result = new ResultDTO<MemberInfo>();
		//登录认证
		if (!paramBuilder.isMemberFlag()) {
			result.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
			return result;
		}
		Long memberId = paramBuilder.getMemberId();
		int evaluationScore = Integer.valueOf(paramBuilder.getParamMap().get("evaluationScore").toString());
		if(evaluationScore==0){
			result.setResultCode(ResultCode.EVALUATION_SCORE_RESULT);
			return result;
		}
	    try {
	    	MemberInfo info= memberInfoManager.updateMemberInfoByMemberId(memberId,evaluationScore);
	    	result.setResult(info);
	    } catch (ManagerException e) {
	        logger.error("测评失败,memberId:" + memberId);
	    }
	    return result;
    }

	@Override
	public boolean isReceived(Long memberId, Long activityId) throws Exception {
		RuleBody rb = new RuleBody();
		rb.setActivityId(activityId);
		rb.setMemberId(memberId);
		rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
		rb.setCycleStr(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate()));
		rb.setDeductRemark("感恩壕礼庆A轮百万现金任性狂撒");
		rb.setRewardsAvailableNum(2);
		rb.setRewardsPoolMaxNum(2);
		// 校验
		if (drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
			return false;
		}
		return true;
		
	}

	/*@Override
	public ResultDTO<ActivityForAnniversary> receiveCelebrationA(Long memberId) {
		ResultDTO<ActivityForAnniversary> rDO = new ResultDTO<ActivityForAnniversary>();
		try {
			rDO = activityLotteryManager.receiveCelebrationA(memberId);
		} catch (Exception e) {
			logger.error("庆A轮领取红包失败 memberId={}", memberId, e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}*/
	
	
	@Override
	public Object receiveCelebrationA(DynamicParamBuilder paramBuilder) {
		ResultDTO<Object> rDO = new ResultDTO<Object>();
		//登录认证
		if (!paramBuilder.isMemberFlag()) {
			rDO.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
				return rDO;
		}
		try {
			String activityIdStr = PropertiesUtil.getProperties("activity.celebrationA.id");
			Long actId = Long.parseLong(activityIdStr);
			// 判断是否在活动期间内
			if (drawByProbability.isInActivityTime(actId)) {
				RuleBody rb = new RuleBody();
				rb.setActivityId(actId);
				rb.setMemberId(paramBuilder.getMemberId());
				rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
				rb.setCycleStr(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate()));
				rb.setDeductRemark("融光焕发");
				rb.setRewardsAvailableNum(2);
				rb.setRewardsPoolMaxNum(2);
				rb.setActivityName(ActivityConstant.ACTIVITY_CELEBRATE_A_NAME);
				// 校验
				if (drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
					Optional<List<Object>> retList = LotteryContainer.getInstance().getRewardsListMap(actId, RewardsBase.class);
					if (retList.isPresent()) {
						//RewardsBase rewardsBase = (RewardsBase) (retList.get().get(0));
						//drawByProbability
						//		.drawLottery(rb, rewardsBase, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode());
						RewardsBodyForProbility rfp = (RewardsBodyForProbility) drawByProbability.drawLottery(rb, null,
								TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode());
						// 发送站内信
						MessageClient.sendMsgForSPEngin(paramBuilder.getMemberId(), rb.getActivityName(), rfp.getRewardName());
						rDO.setResult(rfp.getRewardValue().toString());
						rDO.setIsSuccess();
						return rDO;
					} else {
						rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
						return rDO;
					}
				} else {
					rDO.setResultCode(ResultCode.ACTIVITY_TODAY_RED_HAD_RECEIVED_ERROR);
					return rDO;
				}

			} else {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
				return rDO;
			}
		} catch (Exception e) {
			logger.error("庆A轮活动-领取红包错误, memberId={}", paramBuilder.getMemberId(), e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}
	@Override
	public Object celebrationActivityInit(DynamicParamBuilder builder) throws NumberFormatException, Exception {
		ResultDTO<Map<String, Object>> result = new ResultDTO();
		try {
			Map<String, Object> map = Maps.newHashMap();
			String activityId = PropertiesUtil.getProperties("activity.celebrationA.id");
			Optional<Activity> optOfActivity = LotteryContainer.getInstance()
					.getActivity(Long.parseLong(activityId));
			if(optOfActivity.isPresent()){
				Activity activity = optOfActivity.get();
				map.put("activityStatus", activity.getActivityStatus());
				map.put("startTime", activity.getStartTime());
				map.put("endTime", activity.getEndTime());
			}
			boolean isLogined=false;
			boolean isReceived=false;
			if (builder.isMemberFlag()) {
				isLogined=true;
				if (isReceived(builder.getMemberId(), Long.parseLong(activityId))) {
					isReceived=true;
				}
			}
			map.put("isLogined", isLogined);
			map.put("isReceived", isReceived);
			result.setResult(map);
		} catch (ManagerException e) {
			logger.error("获取A轮活动初始化失败", e);
			result.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return result;
	}
	/**
	 * 
	 * @desc 奥运初始化
	 * @param builder
	 * @return
	 * @throws Exception
	 * @author chaisen
	 * @time 2016年7月19日 下午2:08:44
	 *
	 */
	@Override
	public Object olympicActivityInit(DynamicParamBuilder builder) throws Exception {
		ResultDTO<ActivityForOlympic> rDO = new ResultDTO<ActivityForOlympic>();
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
		
		model.setEightEndTimeM(DateUtils.getDateTimeFromString(olympicDate.getEightEndTime()));
		model.setGuessGoldEndTimeM(DateUtils.getDateTimeFromString(olympicDate.getGuessGoldEndTime()));
		model.setGuessGoldStartTimeM(DateUtils.getDateTimeFromString(olympicDate.getGuessGoldStartTime()));
		model.setGuessGoldPublishTimeM(DateUtils.getDateTimeFromString(olympicDate.getGuessGoldPublishTime()));
		model.setGuessMedalStartTimeM(DateUtils.getDateTimeFromString(olympicDate.getGuessMedalStartTime()));
		model.setGuessMedalEndTimeM(DateUtils.getDateTimeFromString(olympicDate.getGuessMedalEndTime()));
		model.setSevenStartTimeM(DateUtils.getDateTimeFromString(olympicDate.getSevenStartTime()));
		model.setSixEndTimeM(DateUtils.getDateTimeFromString(olympicDate.getSixEndTime()));
		model.setNineStartTimeM(DateUtils.getDateTimeFromString(olympicDate.getNineStartTime()));
		try {
		//活动时间内
		if(DateUtils.isDateBetween(DateUtils.getCurrentDate(),activity.getStartTime() ,activity.getEndTime())){
			//登录过
			if(builder.isMemberFlag()){
				//我的投资额
				model.setTodayMyInvestAmount(transactionManager.getMyTotalInvestByMemberIdAndTime(builder.getMemberId(),DateUtils.getDateTimeFromString(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+" "+olympicDate.getRaceStartTime()),DateUtils.getDateTimeFromString(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+" "+olympicDate.getRaceEndTime())));
				//我的累计投资额
				model.setTotalMyInvestAmount(transactionManager.getMyTotalInvestByMemberIdAndTime(builder.getMemberId(), activity.getStartTime(), activity.getEndTime()));
				//亮奥运
				model.setBrightOlympic(activityLotteryResultManager.getBrightOlympicEveryDay(activity.getId(),builder.getMemberId()));
				//亮奥运奖励是否领取
				model=activityLotteryResultManager.ifBrightOlympicReceived(activity, builder.getMemberId(),model);
				//当天是否竞猜过奖牌数
				RuleBody rb = new RuleBody();
				rb.setActivityId(activity.getId());
				rb.setMemberId(builder.getMemberId());
				rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
				rb.setCycleStr(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+ActivityConstant.ACTIVITY_OLYMPIC_GUESS_MEDAL);
				if (!drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
					model.setIfGuessMedal(true);
					model.setGuessMedalNumber(activityLotteryManager.getMedalType(activity.getId(),builder.getMemberId()));
				}
				//当天是否竞猜过金牌
				rb.setCycleStr(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+ActivityConstant.ACTIVITY_OLYMPIC_GUESS_GOLE);
				if (!drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
					model.setIfGuessGold(true);
				}
				//奖牌奇偶数竞猜记录
				model.setGuessMedalRecord(activityLotteryManager.getGuessMedalRecord(builder.getMemberId(),olympicActivity.get().getId()));
				//金牌竞猜记录
				model.setGuessGoldRecord(activityLotteryManager.getGuessGoldRecord(builder.getMemberId(),olympicActivity.get().getId(),olympicDate,activity.getStartTime()));
				//拼图剩余数量
				//model.setPuzzleRemind(activityLotteryManager.getPuzzleRemind(activity.getId(),builder.getMemberId(),olympicDate));
				model=activityLotteryManager.getPuzzleRemind(activity.getId(),builder.getMemberId(),olympicDate,model);
				// 刷新人气值
				Balance balance = balanceManager.queryBalance(builder.getMemberId(), TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY);
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
			if(builder.isMemberFlag()){
				//亮奥运
				model.setBrightOlympic(activityLotteryResultManager.getBrightOlympicEveryDay(activity.getId(),builder.getMemberId()));
				//亮奥运奖励是否领取
				model=activityLotteryResultManager.ifBrightOlympicReceived(activity, builder.getMemberId(),model);
				//奖牌奇偶数竞猜记录
				model.setGuessMedalRecord(activityLotteryManager.getGuessMedalRecord(builder.getMemberId(),olympicActivity.get().getId()));
				//金牌竞猜记录
				model.setGuessGoldRecord(activityLotteryManager.getGuessGoldRecord(builder.getMemberId(),olympicActivity.get().getId(),olympicDate,activity.getStartTime()));
				//我的累计投资额
				model.setTotalMyInvestAmount(transactionManager.getMyTotalInvestByMemberIdAndTime(builder.getMemberId(), activity.getStartTime(), activity.getEndTime()));
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
	 * @param paramBuilder
	 * @return
	 * @author chaisen
	 * @time 2016年7月19日 下午2:14:35
	 *
	 */
	@Override
	public Object guessMedal(DynamicParamBuilder paramBuilder) {
		ResultDTO<ActivityForOlympicReturn> rDO = new ResultDTO<ActivityForOlympicReturn>();
		ActivityForOlympicReturn activityOlympic=new ActivityForOlympicReturn();
		try {
			if (!paramBuilder.isMemberFlag()) {
				rDO.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
					return rDO;
			}
			rDO.setResult(activityOlympic);
			Optional<Activity> olympicActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_PLAY_OLYMPIC);
			if (!olympicActivity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			if(!paramBuilder.getParamMap().containsKey("medalType")){
				rDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
				return rDO;
			}
			int medalType = Integer.valueOf(paramBuilder.getParamMap().get("medalType").toString());
			if(medalType==0){
				rDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
				return rDO;
			}
			Activity activity=olympicActivity.get();
			if (activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
				ActivityForOlympicDate olympicDate = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForOlympicDate.class,
						ActivityConstant.ACTIVITY_PLAY_OLYMPIC_KEY);
				//8.6到8.21 猜可以猜
				if(!DateUtils.isDateBetween(DateUtils.getCurrentDate(), DateUtils.zerolizedTime(DateUtils.getDateTimeFromString(olympicDate.getGuessMedalStartTime())), DateUtils.getEndTime(DateUtils.getDateTimeFromString(olympicDate.getSetFourEndTime())))){
					rDO.setResultCode(ResultCode.ACTIVITY_PLAYOLYMPIC_GUESSMEDALTIME_ERROR);
					return rDO;
				}
				//是否在当天的竞猜时间内
				if(!DateUtils.isDateBetween(DateUtils.getCurrentDate(), DateUtils.getSpecialTime(DateUtils.getCurrentDate(),olympicDate.getGuessStartTime()), DateUtils.getSpecialTime(DateUtils.getCurrentDate(),olympicDate.getGuessEndTime()))){
					//竞猜总人数
					activityOlympic.setGuessTotalNumber(activityLotteryManager.countActivityLotteryByActivityId(olympicActivity.get().getId(), DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+ActivityConstant.ACTIVITY_OLYMPIC_GUESS_MEDAL));
					rDO.setResultCode(ResultCode.ACTIVITY_PLAYOLYMPIC_GUESSMEDAL_ERROR);
					return rDO;
				}
				RuleBody rb = new RuleBody();
				rb.setActivityId(olympicActivity.get().getId());
				rb.setMemberId(paramBuilder.getMemberId());
				rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
				rb.setCycleStr(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+ActivityConstant.ACTIVITY_OLYMPIC_GUESS_MEDAL);
				rb.setDeductRemark(ActivityConstant.ACTIVITY_PLAY_OLYMPIC);
				rb.setRewardsAvailableNum(2);
				rb.setRewardsPoolMaxNum(2);
				rb.setDeductValue(olympicDate.getDeductPopularvalue());
				rb.setActivityName(ActivityConstant.ACTIVITY_PLAY_OLYMPIC);
				// 校验
				if (!drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
					rDO.setResultCode(ResultCode.ACTIVITY_PLAYOLYMPIC_GUESSMEDAL_HADONCE_ERROR);
					return rDO;
				}
				rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_POPULAR.getStatus());
				if (drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_POPULARITY.getCode())) {
					//扣除人气值，记录竞猜记录
					verificationByPopularity.prepare(rb);
					ActivityLotteryResult betResult = new ActivityLotteryResult();
					betResult.setMemberId(paramBuilder.getMemberId());
					betResult.setActivityId(olympicActivity.get().getId());
					betResult.setChip(olympicDate.getDeductPopularvalue());
					betResult.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_POPULARITY.getType());
					betResult.setRemark(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+ActivityConstant.ACTIVITY_OLYMPIC_GUESS_MEDAL+medalType);
					betResult.setRewardId(paramBuilder.getMemberId()+ActivityConstant.ACTIVITY_OLYMPIC_GUESS_MEDAL);
					betResult.setRewardResult("0");
					betResult.setStatus(0);
					betResult.setRewardInfo(olympicDate.getDeductPopularvalue() + ActivityConstant.popularityDesc);
					betResult.setCycleStr(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+ActivityConstant.ACTIVITY_OLYMPIC_GUESS_MEDAL);
					activityLotteryResultManager.insertSelective(betResult);
					//竞猜记录
					activityOlympic.setGuessMedalRecord(activityLotteryManager.getGuessMedalRecord(paramBuilder.getMemberId(),olympicActivity.get().getId()));
					//竞猜总人数
					activityOlympic.setGuessTotalNumber(activityLotteryManager.countActivityLotteryByActivityId(olympicActivity.get().getId(), DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+ActivityConstant.ACTIVITY_OLYMPIC_GUESS_MEDAL));
					rDO.isSuccess();
					return rDO;
				}else{
					rDO.setResultCode(ResultCode.ACTIVITY_POPULARITY__ERROR);
					return rDO;
				}
			}else{
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
				return rDO;
			}
			
		} catch (Exception e) {
			logger.error("玩转奥运-竞猜奖牌失败, memberId={}", paramBuilder.getMemberId(), e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}
	/**
	 * 
	 * @desc 猜金牌
	 * @param paramBuilder
	 * @return
	 * @author chaisen
	 * @time 2016年7月19日 下午2:14:48
	 *
	 */
	@Override
	public Object guessGold(DynamicParamBuilder paramBuilder) {
		ResultDTO<ActivityForOlympicReturn> rDO = new ResultDTO<ActivityForOlympicReturn>();
		ActivityForOlympicReturn activityOlympic=new ActivityForOlympicReturn();
		try {
			if (!paramBuilder.isMemberFlag()) {
				rDO.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
					return rDO;
			}
			Optional<Activity> olympicActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_PLAY_OLYMPIC);
			if (!olympicActivity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			Activity activity=olympicActivity.get();
			if(!paramBuilder.getParamMap().containsKey("popularityValue")||!paramBuilder.getParamMap().containsKey("goldNumber")){
				rDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
				return rDO;
			}
			if(paramBuilder.getParamMap().get("popularityValue").toString().contains(".")||paramBuilder.getParamMap().get("goldNumber").toString().contains(".")){
				rDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
				return rDO;
			}
			int popularityValue = Integer.valueOf(paramBuilder.getParamMap().get("popularityValue").toString());
			int goldNumber = Integer.valueOf(paramBuilder.getParamMap().get("goldNumber").toString());
			if(popularityValue==0||goldNumber<0||popularityValue<0){
				rDO.setResultCode(ResultCode.ACTIVITY_PARAM_WRONG_ERROR);
				return rDO;
			}
			if (activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
				ActivityForOlympicDate olympicDate = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForOlympicDate.class,
						ActivityConstant.ACTIVITY_PLAY_OLYMPIC_KEY);
				//是否在8月5日00:00~8月12日23:59时间内
				if(!DateUtils.isDateBetween(DateUtils.getCurrentDate(),DateUtils.getDateTimeFromString(olympicDate.getGuessGoldStartTime()), DateUtils.getDateTimeFromString(olympicDate.getGuessGoldEndTime()))){
					rDO.setResultCode(ResultCode.ACTIVITY_PLAYOLYMPIC_GUESSGOLD_OUTTIME_ERROR);
					return rDO;
				}
				RuleBody rb = new RuleBody();
				rb.setActivityId(olympicActivity.get().getId());
				rb.setMemberId(paramBuilder.getMemberId());
				rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
				rb.setCycleStr(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+ActivityConstant.ACTIVITY_OLYMPIC_GUESS_GOLE);
				rb.setDeductRemark(ActivityConstant.ACTIVITY_PLAY_OLYMPIC);
				rb.setRewardsAvailableNum(2);
				rb.setRewardsPoolMaxNum(2);
				rb.setDeductValue(popularityValue);
				rb.setActivityName(ActivityConstant.ACTIVITY_PLAY_OLYMPIC);
				// 校验
				if (!drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
					rDO.setResultCode(ResultCode.ACTIVITY_PLAYOLYMPIC_GUESSMEDAL_HADONCE_ERROR);
					return rDO;
				}
				rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_POPULAR.getStatus());
				if (drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_POPULARITY.getCode())) {
					//扣除人气值，记录竞猜记录
					verificationByPopularity.prepare(rb);
					ActivityLotteryResult betResult = new ActivityLotteryResult();
					betResult.setMemberId(paramBuilder.getMemberId());
					betResult.setActivityId(olympicActivity.get().getId());
					betResult.setChip(popularityValue);
					betResult.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_POPULARITY.getType());
					betResult.setRemark(ActivityConstant.ACTIVITY_OLYMPIC_GUESS_GOLE);
					betResult.setRewardId(paramBuilder.getMemberId()+ActivityConstant.ACTIVITY_OLYMPIC_GUESS_GOLE);
					//金牌数量
					betResult.setRewardResult(Integer.toString(goldNumber));
					betResult.setStatus(0);
					betResult.setRewardInfo(popularityValue+ ActivityConstant.popularityDesc);
					betResult.setCycleStr(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+ActivityConstant.ACTIVITY_OLYMPIC_GUESS_GOLE);
					activityLotteryResultManager.insertSelective(betResult);
					//竞猜记录
					activityOlympic.setGuessGoldRecord(activityLotteryManager.getGuessGoldRecord(paramBuilder.getMemberId(),olympicActivity.get().getId(),olympicDate,activity.getStartTime()));
					rDO.setResult(activityOlympic);
					rDO.setIsSuccess();
					return rDO;
				}else{
					rDO.setResultCode(ResultCode.ACTIVITY_POPULARITY__ERROR);
					return rDO;
				}
			}else{
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
				return rDO;
			}
			
		} catch (Exception e) {
			logger.error("玩转奥运-竞猜金牌失败, memberId={}", paramBuilder.getMemberId(), e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}
	/**
	 * 
	 * @desc 集奥运
	 * @param paramBuilder
	 * @return
	 * @author chaisen
	 * @time 2016年7月19日 下午2:15:02
	 *
	 */
	@Override
	public Object setOlympic(DynamicParamBuilder paramBuilder) {
		ResultDTO<ActivityForOlympicReturn> rDO = new ResultDTO<ActivityForOlympicReturn>();
		ActivityForOlympicReturn activityOlympic=new ActivityForOlympicReturn();
		ActivityForOlympic model = new ActivityForOlympic();
		try {
			if (!paramBuilder.isMemberFlag()) {
				rDO.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
					return rDO;
			}
			rDO.setResult(activityOlympic);
			Optional<Activity> olympicActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_PLAY_OLYMPIC);
			if (!olympicActivity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			if(!paramBuilder.getParamMap().containsKey("puzzle")){
				rDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
				return rDO;
			}
			String param=paramBuilder.getParamMap().get("puzzle").toString();
			String[] puzzle=param.split(",");
			if(puzzle==null||puzzle.length==0){
				rDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
				return rDO;
			}
			 if(StringUtil.isSame(puzzle)){
			    	rDO.setResultCode(ResultCode.ACTIVITY_PARAM_SAME_ERROR);
					return rDO;
			    }
			 Activity activity=olympicActivity.get();
			 if (activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
				//校验拼图剩余数量
				ActivityLottery activityLottery=new ActivityLottery();
				activityLottery.setActivityId(activity.getId());
				activityLottery.setMemberId(paramBuilder.getMemberId());
				for(int i=0;i<puzzle.length;i++){
					activityLottery.setCycleConstraint(paramBuilder.getMemberId()+ActivityConstant.redBagCodeSplit+ActivityConstant.ACTIVITY_OLYMPIC_SET_OLYMPIC+ActivityConstant.redBagCodeSplit+puzzle[i]);
					ActivityLottery lotteryRemind=activityLotteryManager.checkExistLottery(activityLottery);
					if(lotteryRemind==null){
						rDO.setResultCode(ResultCode.ACTIVITY_PLAYOLYMPIC_PUZZE_NO_ERROR);
						return rDO;
					}
					if(lotteryRemind.getRealCount()<1){
						rDO.setResultCode(ResultCode.ACTIVITY_PLAYOLYMPIC_PUZZE_NO_ERROR);
						return rDO;
					}
				}
				RuleBody rb = new RuleBody();
				rb.setActivityId(activity.getId());
				rb.setMemberId(paramBuilder.getMemberId());
				rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_COIN.getStatus());
				rb.setActivityName(ActivityConstant.ACTIVITY_PLAY_OLYMPIC);
				rb.setCycleStr(activityLottery.getCycleConstraint());
				ActivityForOlympicDate olympicDate = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForOlympicDate.class,
						ActivityConstant.ACTIVITY_PLAY_OLYMPIC_KEY);
				Long templateId=1L;
				if(puzzle.length==1){
					templateId=olympicDate.getTemplateId().get(0);
				}else if(puzzle.length==2){
					templateId=olympicDate.getTemplateId().get(2);
				}else if(puzzle.length==3){
					templateId=olympicDate.getTemplateId().get(3);
				//实物大奖
				}else if(puzzle.length==4){
					ActivityLotteryResult betResult = new ActivityLotteryResult();
					betResult.setMemberId(paramBuilder.getMemberId());
					betResult.setActivityId(olympicActivity.get().getId());
					betResult.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_GIFT.getType());
					betResult.setRewardInfo("奥运吉祥物拼图-兑换荣耀手环zero一个");
					betResult.setRemark(ActivityConstant.ACTIVITY_OLYMPIC_REAL_PRIZE);
					betResult.setCycleStr(paramBuilder.getMemberId()+ActivityConstant.redBagCodeSplit+ActivityConstant.ACTIVITY_OLYMPIC_SET_OLYMPIC+ActivityConstant.redBagCodeSplit+puzzle[3]);
					activityLotteryResultManager.insertSelective(betResult);
					//发放成功减去吉祥物张数
					for(int i=0;i<puzzle.length;i++){
						activityLottery.setRealCount(-1);
						activityLottery.setCycleConstraint(paramBuilder.getMemberId()+ActivityConstant.redBagCodeSplit+ActivityConstant.ACTIVITY_OLYMPIC_SET_OLYMPIC+ActivityConstant.redBagCodeSplit+puzzle[i]);
						activityLotteryManager.updateRealCount(activityLottery);
					}
					//幸运儿-实物大奖列表
					activityOlympic.setLuckyList(activityLotteryManager.getRealPrize(activity.getId()));
					//返回吉祥物剩余数量
					//activityOlympic.setPuzzleRemind(activityLotteryManager.getPuzzleRemind(activity.getId(),paramBuilder.getMemberId(),olympicDate));
					model=activityLotteryManager.getPuzzleRemind(activity.getId(),paramBuilder.getMemberId(),olympicDate,model);
					if(model!=null){
						activityOlympic.setPuzzle1(model.getPuzzle1());
						activityOlympic.setPuzzle2(model.getPuzzle2());
						activityOlympic.setPuzzle3(model.getPuzzle3());
						activityOlympic.setPuzzle4(model.getPuzzle4());
					}
					//发送短信
					MessageClient.sendMsgForCommon(paramBuilder.getMemberId(),Constant.MSG_TEMPLATE_TYPE_PHONE, MessageEnum.SET_OLYMPIC.getCode(),
							ActivityConstant.ACTIVITY_PLAY_OLYMPIC);
					rDO.setIsSuccess();
					return rDO;
				}
				CouponTemplate c = couponTemplateManager.selectByPrimaryKey(templateId);
				if(c==null){
					rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_COUPON_NOTEXITS_ERROR);
					return rDO;
				}
				// 发放奖励
				RewardsBase rBase = new RewardsBase();
				rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
				rBase.setRewardValue(c.getAmount().intValue());
				rBase.setTemplateId(templateId);
				rBase.setRewardName(c.getAmount().intValue() + ActivityConstant.couponDesc);
				drawByPrizeDirectly.drawLottery(rb, rBase, "");
				//站内信
				MessageClient.sendMsgForSPEngin(paramBuilder.getMemberId(), ActivityConstant.ACTIVITY_OLYMPIC_SET_NAME, rBase.getRewardName());
				//发放成功减去吉祥物张数
				for(int i=0;i<puzzle.length;i++){
					activityLottery.setRealCount(-1);
					activityLottery.setCycleConstraint(paramBuilder.getMemberId()+ActivityConstant.redBagCodeSplit+ActivityConstant.ACTIVITY_OLYMPIC_SET_OLYMPIC+ActivityConstant.redBagCodeSplit+puzzle[i]);
					activityLotteryManager.updateRealCount(activityLottery);
				}
				//返回实物大奖列表
				activityOlympic.setLuckyList(activityLotteryManager.getRealPrize(activity.getId()));
				//返回吉祥物剩余数量
				//activityOlympic.setPuzzleRemind(activityLotteryManager.getPuzzleRemind(activity.getId(),paramBuilder.getMemberId(),olympicDate));
				model=activityLotteryManager.getPuzzleRemind(activity.getId(),paramBuilder.getMemberId(),olympicDate,model);
				if(model!=null){
					activityOlympic.setPuzzle1(model.getPuzzle1());
					activityOlympic.setPuzzle2(model.getPuzzle2());
					activityOlympic.setPuzzle3(model.getPuzzle3());
					activityOlympic.setPuzzle4(model.getPuzzle4());
				}
				rDO.setIsSuccess();
				return rDO;
			}else{
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
				return rDO;
			}
			
		} catch (Exception e) {
			logger.error("玩转奥运-兑换奖品, memberId={}", paramBuilder.getMemberId(), e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}
	
	@Override
	public ResultDTO<Object> signContract(DynamicParamBuilder builder) {
		ResultDTO<Object> result = new ResultDTO<Object>();
		try {
			Long transactionId = Long.valueOf(builder.getParamMap().get("transactionId").toString());
			Integer source = Integer.valueOf(builder.getParamMap().get("source").toString());
			ResultDO<Object> queryContractInfoResult = transactionManager.queryContractInfo(transactionId);
			if(queryContractInfoResult.isSuccess()&&Integer.valueOf(queryContractInfoResult.getResult().toString())>0){
				result.setResultCode(ResultCode.CONTRACT_SIGN_ALREADY_SIGN);
				result.setIsError();
				return result;
			}
			
			Transaction transaction = transactionManager.selectTransactionById(transactionId);
			if(transaction.getSignStatus()==StatusEnum.CONTRACT_STATUS_INIT.getStatus()){
				result.setResultCode(ResultCode.CONTRACT_SIGN_FAIL_TO_INIT);
				result.setIsError();
				return result;
			}
			
			if(transaction.getSignStatus()==StatusEnum.CONTRACT_STATUS_ALREADY_SIGN.getStatus()){
				result.setResultCode(ResultCode.CONTRACT_SIGN_ALREADY_SIGN);
				result.setIsError();
				return result;
			} 
			
			if(transaction.getSignStatus()==StatusEnum.CONTRACT_STATUS_EXPIRED.getStatus()){
				result.setResultCode(ResultCode.CONTRACT_SIGN_EXPIRED);
				result.setIsError();
				return result;
			} 
			
			String signUrl = transactionManager.signContract(transactionId,StatusEnum.CONTRACT_SIGN_WAY_MOBILE.getStatus(),source);
			if(StringUtil.isBlank(signUrl)){
				result.setResultCode(ResultCode.CONTRACT_SIGN_FAIL_TO_INIT);
				result.setIsError();
				return result;
			}
			result.setResult(signUrl);
		} catch (Exception e) {
			logger.error("手动签署异常,builder =" + builder, e);
		}
		return result;
	}
	/**
	 * 六重礼
	 */
	@Override
	public Object newSixGiftInit(DynamicParamBuilder builder) {
				ResultDO  result = new ResultDO();
				Map<String, Object> map = Maps.newHashMap();
				Long memberId=0L;
		try {
			if(builder.isMemberFlag()){
				memberId=builder.getMemberId();
				Date activityStartDate = DateUtils.getDateFromString(APIPropertiesUtil.getProperties("noviceTaskStartTime"));
				ActivityHistory bindEmailActivityHistory = activityHistoryManager.getActivityHistory(memberId, PropertiesUtil.getActivityBindEmailId());
				map.put("isBindEmail", isJoinActivity(bindEmailActivityHistory, activityStartDate, 1));
				map.put("showBindEmailReward", isJoinActivity(bindEmailActivityHistory, activityStartDate, 2));
				
				ActivityHistory completedInfoActivity = activityHistoryManager.getActivityHistory(memberId, PropertiesUtil.getActivityCompletedMemberInfoId());
				map.put("isCompletedInfo", isJoinActivity(completedInfoActivity, activityStartDate, 1));
				map.put("showCompletedInfoReward", isJoinActivity(completedInfoActivity, activityStartDate, 2));
				
				ActivityHistory verifyTrueNameActivity = activityHistoryManager.getActivityHistory(memberId, PropertiesUtil.getActivityVerifyTrueNameId());
				map.put("isverifyTrueName", isJoinActivity(verifyTrueNameActivity, activityStartDate, 1));
				map.put("showverifyTrueNameReward", isJoinActivity(verifyTrueNameActivity, activityStartDate, 2));
				
				Transaction transaction = transactionManager.getFirstTransaction(memberId);
				map.put("isInvestment", transaction != null ? true : false);
				map.put("showInvestmentReward", (transaction != null && transaction.getTransactionTime().after(activityStartDate)) ? true : false);
				
				MemberToken weixinToken = memberTokenManager.selectFirstLoginWeiXin(memberId);
				map.put("isFollowWeiXin", weixinToken != null ? true : false);
				map.put("showFollowWeiXinReward", (weixinToken != null && weixinToken.getCreateTime().after(activityStartDate)) ? true : false);
				
				MemberToken appToken = memberTokenManager.selectFirstLoginApp(memberId);
				map.put("isUseApp", appToken != null ? true : false);
				map.put("showUseAppReward", (appToken != null && appToken.getCreateTime().after(activityStartDate)) ? true : false);
			}
				result.setResult(map);
		} catch (ManagerException e) {
			logger.error("新手任务状态异常："+memberId);
		}
		return result;
	}
	private boolean isJoinActivity(ActivityHistory activityHistory, Date activityStartDate, int type){
		if(type == 1){
			if(activityHistory != null){
				return true;
			}
			return false;
		}
		if(activityHistory != null && activityHistory.getCreateTime().after(activityStartDate)){
			return true;
		}
		return false;
	}
	/**
	 * 
	 * @desc 加入战队
	 * @param paramBuilder
	 * @return
	 * @author chaisen
	 * @time 2016年7月1日 下午12:54:19
	 *
	 */
	@Override
	public Object joinJulyTeam(DynamicParamBuilder paramBuilder) {
		ResultDTO<ActivityForJulyRetrun> rDO = new ResultDTO<ActivityForJulyRetrun>();
		ActivityForJulyRetrun activityReturn=new ActivityForJulyRetrun();
		//登录认证
		if (!paramBuilder.isMemberFlag()) {
			rDO.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
				return rDO;
		}
		try {
			Optional<Activity> teamActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_JULY_TEAM);
			if (!teamActivity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			Activity activity=teamActivity.get();
			if(!DateUtils.isDateBetween(DateUtils.getCurrentDate(), activity.getStartTime(), activity.getEndTime())){
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
				return rDO;
			}
			ActivityForJulyDate julyDate = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForJulyDate.class,
					ActivityConstant.ACTIVITY_JULY_TEAM_KEY);
			
			if(!DateUtils.isDateBetween(DateUtils.getCurrentDate(), DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getJoinStartTime()), DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getJoinEndTime()))){
				rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_WAIT_TIME_ERROR);
				return rDO;
			}
			if(activityGroupManager.checkGroupByActivityIdAndMemberId(teamActivity.get().getId(), paramBuilder.getMemberId())){
				rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_HAD_JOINED_ERROR);
				return rDO;
			}
			int groupType=RandomUtils.getRandomNumberByRange(ActivityEnum.JULYTEAM_GROUPTYPE_JYSH.getCode(),ActivityEnum.JULYTEAM_GROUPTYPE_QLYX.getCode());
			ActivityGroup group=new ActivityGroup();
			group.setMemberId(paramBuilder.getMemberId());
			group.setActivityId(teamActivity.get().getId());
			group.setGroupType(groupType);
			group.setCreateTime(DateUtils.getCurrentDate());
			group.setDelFlag(1);
			int i=activityGroupMapper.insertSelective(group);
			if(i>0){
				ActivityForJulyBet couponRemind=activityGroupManager.getRemindCoupon(activity,groupType);
				activityReturn.setCouponRemind(couponRemind);
				List<TransactionForFirstInvestAct> currentGroupFirstTen=currentGroupFirstTen=activityGroupManager.selectTopTenInvestByGroupType(groupType,activity.getId(),DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountStartTime()),DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountEndTime()));
				activityReturn.setCurrentGroupType(groupType);
				activityReturn.setJulyTeamContribution(currentGroupFirstTen);
				rDO.setResult(activityReturn);
				rDO.setIsSuccess();
				return rDO;
			}
		} catch (Exception e) {
			logger.error("组团大作战-加入战队错误, memberId={}", paramBuilder.getMemberId(), e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}
	/**
	 * 
	 * @desc 押注战队
	 * @param paramBuilder
	 * @return
	 * @author chaisen
	 * @time 2016年7月1日 下午2:14:26
	 *
	 */
	@Override
	public Object betJulyTeam(DynamicParamBuilder paramBuilder) {
		ResultDTO<ActivityForJulyRetrun> rDO = new ResultDTO<ActivityForJulyRetrun>();
		ActivityForJulyRetrun activityJuly=new ActivityForJulyRetrun();
		try {
			//登录认证
			if (!paramBuilder.isMemberFlag()) {
				rDO.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
					return rDO;
			}
			Optional<Activity> teamActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_JULY_TEAM);
			if (!teamActivity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			int popularityValue = Integer.valueOf(paramBuilder.getParamMap().get("popularityValue").toString());
			int groupType = Integer.valueOf(paramBuilder.getParamMap().get("groupType").toString());
			if(popularityValue>20){
				rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_CANT_BETED_TWENTY_ERROR);
				return rDO;
			}
			if(popularityValue==0||groupType==0){
				rDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
				return rDO;
			}
			if (drawByProbability.isInActivityTime(teamActivity.get().getId())) {
				Activity activity=teamActivity.get();
				ActivityForJulyDate julyDate = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForJulyDate.class,
						ActivityConstant.ACTIVITY_JULY_TEAM_KEY);
				//是否在当天的押注时间内
				if(!DateUtils.isDateBetween(DateUtils.getCurrentDate(), DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getBetStartTime()), DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getBetEndTime()))){
					rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_BET_TIME_ERROR);
					return rDO;
				}
				RuleBody rb = new RuleBody();
				rb.setActivityId(teamActivity.get().getId());
				rb.setMemberId(paramBuilder.getMemberId());
				rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
				rb.setCycleStr(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+"-betjulyteam");
				rb.setDeductRemark(activity.getActivityDesc());
				rb.setRewardsAvailableNum(2);
				rb.setRewardsPoolMaxNum(2);
				rb.setDeductValue(popularityValue);
				rb.setActivityName(ActivityConstant.ACTIVITY_JULY_TEAM);
				// 校验
				if (!drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
					rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_HAD_BETED_ERROR);
					return rDO;
				}
				rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_POPULAR.getStatus());
				if (drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_POPULARITY.getCode())) {
					//扣除人气值，记录押注记录
					verificationByPopularity.prepare(rb);
					ActivityLotteryResult betResult = new ActivityLotteryResult();
					betResult.setMemberId(paramBuilder.getMemberId());
					betResult.setActivityId(teamActivity.get().getId());
					betResult.setChip(popularityValue);
					betResult.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_POPULARITY.getType());
					betResult.setRemark(Integer.toString(groupType));
					betResult.setRewardId(paramBuilder.getMemberId().toString());
					betResult.setCycleStr(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+"-betjulyteam");
					betResult.setStatus(0);
					betResult.setRewardInfo(popularityValue + ActivityConstant.popularityDesc);
					activityLotteryResultMapper.insertSelective(betResult);
					// 刷新人气值
					Balance balance = balanceManager.queryBalance(paramBuilder.getMemberId(), TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY);
					if (balance != null) {
						activityJuly.setPopularityValue(balance.getAvailableBalance().intValue());
					}
					ActivityLotteryResult activityResult=new ActivityLotteryResult();
					activityResult.setActivityId(activity.getId());
					activityResult.setMemberId(paramBuilder.getMemberId());
					activityResult.setRewardId(paramBuilder.getMemberId().toString());
					//押注记录
					List<ActivityForJulyBet> betList=activityLotteryResultManager.getBetRecordList(activityResult);
					activityJuly.setBetList(betList);
					activityJuly.setBetTotals(activityLotteryManager.countBetTotal(activity.getId(),DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+"-betjulyteam"));
					rDO.setResult(activityJuly);
					rDO.setIsSuccess();
					return rDO;
				}else{
					rDO.setResultCode(ResultCode.ACTIVITY_POPULARITY__ERROR);
					return rDO;
				}
			}else{
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
				return rDO;
			}
			
		} catch (Exception e) {
			logger.error("组团大作战-押注失败, memberId={}", paramBuilder.getMemberId(), e);
		}
		return rDO;
	}
	/**
	 * 
	 * @desc 七月战队  -领取现金券
	 * @param paramBuilder
	 * @return
	 * @author chaisen
	 * @time 2016年7月2日 上午9:18:02
	 *
	 */
	@Override
	public Object receiveJulyTeamCoupon(DynamicParamBuilder paramBuilder) {
		ResultDTO<ActivityForJulyRetrun> rDO = new ResultDTO<ActivityForJulyRetrun>();
		ActivityForJulyRetrun activityJuly=new ActivityForJulyRetrun();
		try {
			//登录认证
			if (!paramBuilder.isMemberFlag()) {
				rDO.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
					return rDO;
			}
			Optional<Activity> teamActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_JULY_TEAM);
			if (!teamActivity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			int couponAmount = Integer.valueOf(paramBuilder.getParamMap().get("couponAmount").toString());
			if (drawByProbability.isInActivityTime(teamActivity.get().getId())) {
				Activity activity=teamActivity.get();
				ActivityForJulyDate julyDate = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForJulyDate.class,
						ActivityConstant.ACTIVITY_JULY_TEAM_KEY);
				//是否加入过战队
				if(!activityGroupManager.checkGroupByActivityIdAndMemberId(teamActivity.get().getId(), paramBuilder.getMemberId())){
					rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_COUPON_JOIN_ERROR);
					return rDO;
				}
				//是否在当天的领取时间内
				if(!DateUtils.isDateBetween(DateUtils.getCurrentDate(), DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getReceiveStartTime()), DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getReceiveEndTime()))){
					rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_RECEIVE_TIME_ERROR);
					return rDO;
				}
				RuleBody rb = new RuleBody();
				rb.setActivityId(activity.getId());
				rb.setMemberId(paramBuilder.getMemberId());
				rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_COIN.getStatus());
				rb.setActivityName(activity.getActivityDesc());
				rb.setCycleStr(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate()));
				rb.setDeductRemark(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate()));
				// 校验剩余次数
				ActivityLottery lotter=new ActivityLottery();
				lotter.setActivityId(activity.getId());
				lotter.setMemberId(paramBuilder.getMemberId());
				lotter.setCycleConstraint(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate()));
				ActivityLottery activityLottery=activityLotteryMapper.checkExistLottery(lotter);
				if(activityLottery==null){
					rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_COUPON_ERROR);
					return rDO;
				}
				if(activityLottery.getRealCount()<1){
					rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_COUPON_ERROR);
					return rDO;
				}
				lotter.setRealCount(-1);
				Long templateId=1L;
				int totalCoupon=0;
				int receivedCoupon=0;
				for(int i=0;i<julyDate.getCouponAmount().size();i++){
					if(julyDate.getCouponAmount().get(i)==couponAmount){
						templateId=julyDate.getTemplateId().get(i);
						totalCoupon=julyDate.getTotalCoupon().get(i);
						break;
					}
				}
				int groupType=activityGroupManager.getGroupTypeByMemberId(activity.getId(), paramBuilder.getMemberId());
				String totalKey = RedisConstant.ACTIVITY_SETP_TEAM_CURRENT_COUPONAMOUNT + RedisConstant.REDIS_SEPERATOR + DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+ RedisConstant.REDIS_SEPERATOR+couponAmount+ RedisConstant.REDIS_SEPERATOR+groupType;
				if(RedisManager.isExit(totalKey)){
					String total=RedisManager.get(totalKey);
					if(total!=null){
						receivedCoupon=Integer.parseInt(total);
					}
				}
				if(receivedCoupon>totalCoupon){
					rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_COUPON_HADEND_ERROR);
					return rDO;
				}
				if(totalCoupon-activityLotteryManager.getRemindCouponNumber(lotter,templateId,groupType)<=0){
					rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_COUPON_HADEND_ERROR);
					return rDO;
				}
				// 发放奖励
				RewardsBase rBase = new RewardsBase();
				CouponTemplate c = couponTemplateManager.selectByPrimaryKey(templateId);
				if(c==null){
					rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_COUPON_NOTEXITS_ERROR);
					return rDO;
				}
				rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
				rBase.setRewardName(c.getAmount().intValue() + ActivityConstant.couponDesc);
				rBase.setRewardValue(c.getAmount().intValue());
				rBase.setTemplateId(templateId);
				rb.setRewardId(templateId.toString());
				drawByPrizeDirectly.drawLottery(rb, rBase, "");
				RedisManager.incrby(totalKey, 1L);
				activityLotteryMapper.updateByActivityAndMember(lotter);
				activityLottery=activityLotteryMapper.checkExistLottery(lotter);
				if(activityLottery!=null){
					activityJuly.setCouponValue(activityLottery.getRealCount());
				}
				ActivityForJulyBet couponRemind=activityGroupManager.getRemindCoupon(activity,groupType);
				//站内信
				MessageClient.sendMsgForSPEngin(paramBuilder.getMemberId(), activity.getActivityDesc(), activity.getReleaseReason());
				activityJuly.setCouponRemind(couponRemind);
				rDO.setResult(activityJuly);
				rDO.setIsSuccess();
				return rDO;
			}else{
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
				return rDO;
			}
			
		} catch (Exception e) {
			logger.error("组团大作战-领取红包, memberId={}", paramBuilder.getMemberId(), e);
		}
		return rDO;
	}
	/**
	 * 
	 * @desc 七月战队初始化
	 * @param builder
	 * @return
	 * @throws Exception
	 * @author chaisen
	 * @time 2016年7月4日 下午8:45:44
	 *
	 */
	@Override
	public Object julyTeamInit(DynamicParamBuilder builder) throws Exception {
		ResultDTO<ActivityForJuly> rDO = new ResultDTO<ActivityForJuly>();
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
		model.setCurrentBetMember(activityLotteryManager.countBetTotal(activity.getId(),DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+"-betjulyteam"));
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
		if(builder.isMemberFlag()){
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
			if(activityGroupManager.checkGroupByActivityIdAndMemberId(activity.getId(), builder.getMemberId())){
				model.setJoined(true);
				BigDecimal totalAmount=BigDecimal.ZERO;
				if(DateUtils.isDateBetween(DateUtils.getCurrentDate(), DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountStartTime()), DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountEndTime()))){
					// 获取用户当前投资总额
					totalAmount = getTotalAmountByMemberId(builder.getMemberId(),DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountStartTime()),DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountEndTime()));
				}else{
					ActivityGroup activityGroup=activityGroupMapper.getCurrentMemberGroupBy(activity.getId(), builder.getMemberId());
					if(activityGroup!=null){
						if(activityGroup.getGroupInfo()!=null){
							totalAmount=new BigDecimal(activityGroup.getGroupInfo());
						}
					}
				}
				model.setTodayInvestAmountMy(totalAmount);
				//当前用户所属战队
				int groupType=activityGroupManager.getGroupTypeByMemberId(activity.getId(), builder.getMemberId());
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
			rb.setMemberId(builder.getMemberId());
			rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
			rb.setCycleStr(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+"-betjulyteam");
			// 校验
			if (!drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
				model.setBeted(true);
			}
			//押注记录
			ActivityLotteryResult activityResult=new ActivityLotteryResult();
			activityResult.setActivityId(activity.getId());
			activityResult.setMemberId(builder.getMemberId());
			activityResult.setRewardId(builder.getMemberId().toString());
			List<ActivityForJulyBet> betList=activityLotteryResultManager.getBetRecordList(activityResult);
			model.setBetList(betList);
		}
		}else{
			if(builder.isMemberFlag()){
				//押注记录
				ActivityLotteryResult activityResult=new ActivityLotteryResult();
				activityResult.setActivityId(activity.getId());
				activityResult.setMemberId(builder.getMemberId());
				activityResult.setRewardId(builder.getMemberId().toString());
				List<ActivityForJulyBet> betList=activityLotteryResultManager.getBetRecordList(activityResult);
				model.setBetList(betList);
				
				//当前用户所属战队
				int groupType=activityGroupManager.getGroupTypeByMemberId(activity.getId(), builder.getMemberId());
				model.setCurrentGroupType(groupType);
				//当前用户所在战队投资排名前十
				List<TransactionForFirstInvestAct> currentGroupFirstTen=Lists.newArrayList();
				//if(DateUtils.isDateBetween(DateUtils.getCurrentDate(), DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountStartTime()), DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountEndTime()))){
					//currentGroupFirstTen=activityGroupManager.selectTopTenInvestByGroupType(groupType,activity.getId(),DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountStartTime()),DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountEndTime()));
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
				//totalAmountA = activityGroupManager.getTotalAmountByMemberIdAndActivityGroupType(activity,ActivityEnum.JULYTEAM_GROUPTYPE_JYSH.getCode());
				//清凉一夏的当天投资总额 
				//totalAmountB = activityGroupManager.getTotalAmountByMemberIdAndActivityGroupType(activity,ActivityEnum.JULYTEAM_GROUPTYPE_QLYX.getCode());
			//}else{
				totalAmountA=activityGroupManager.getTotalAmountByActivityId(activity.getId(),ActivityEnum.JULYTEAM_GROUPTYPE_JYSH.getCode());
				totalAmountB=activityGroupManager.getTotalAmountByActivityId(activity.getId(),ActivityEnum.JULYTEAM_GROUPTYPE_QLYX.getCode());
			//}
			model.setTodayInvestAmountA(totalAmountA);
			model.setTodayInvestAmountB(totalAmountB);
			
		}
		} catch (ManagerException e) {
			logger.error("七月战队初始化失败, activityId={}", activity.getId(), e);
		}finally {
			logger.info("七月战队初始化",activity.getId());
		}
		}
		return rDO;
	}

	@Override
	public Object dataChannelInit(DynamicParamBuilder builder) {
		ResultDTO<ChannelData> rDO = new ResultDTO<ChannelData>();
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
		ResultDTO<ChannelData> rDO = new ResultDTO<ChannelData>();
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

	@Override
	public Object anniversaryLastInit(DynamicParamBuilder builder) {
		ResultDTO<ActivityForFourChange> rDO = new ResultDTO<ActivityForFourChange>();
		ActivityForFourChange model = new ActivityForFourChange();
		rDO.setResult(model);
		Optional<Activity> fourActivity = LotteryContainer.getInstance()
				.getActivityByName(ActivityConstant.ACTIVITY_FOUR_CHANGE_NAME);
		if (fourActivity.isPresent()) {
		Activity activity = fourActivity.get();
		model.setActivityStatus(activity.getActivityStatus());
		model.setStartTime(activity.getStartTime());
		model.setEndTime(activity.getEndTime());
		ActivityForFourChange fourDate = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForFourChange.class,
				ActivityConstant.ACTIVITY_FOUR_CHANGE_KEY);
		model.setReceiveStartTime(fourDate.getReceiveStartTime());
		model.setReceiveEndTime(fourDate.getReceiveEndTime());
		model.setDays(DateUtils.daysBetween(DateUtils.getCurrentDate(), fourDate.getWeakStartTime()));
		model.setWeakStartTime(fourDate.getWeakStartTime());
		
		//活动时间内
		String date=DateUtils.getDateStrFromDate(DateUtils.getCurrentDate());
		if(activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()){
			if(DateUtils.isDateBetween(DateUtils.getCurrentDate(), DateUtils.getDateTimeFromString(date+" "+fourDate.getReceiveStartTime()), DateUtils.getDateTimeFromString(date+" "+fourDate.getReceiveEndTime()))){
				int coupon10=fourDate.getTotalCoupon().get(0)-RedisActivityClient.getReceivedCouponNum(fourDate.getCouponAmountKey().get(0),date);
				int coupon30=fourDate.getTotalCoupon().get(1)-RedisActivityClient.getReceivedCouponNum(fourDate.getCouponAmountKey().get(1),date);
				int coupon100=fourDate.getTotalCoupon().get(2)-RedisActivityClient.getReceivedCouponNum(fourDate.getCouponAmountKey().get(2),date);
				int coupon200=fourDate.getTotalCoupon().get(3)-RedisActivityClient.getReceivedCouponNum(fourDate.getCouponAmountKey().get(3),date);
				if(coupon10>0){
					model.setCoupon10(coupon10);
				}else{
					model.setCoupon10(0);
				}
				
				if(coupon30>0){
					model.setCoupon30(coupon30);
				}else{
					model.setCoupon30(0);
				}
				
				if(coupon100>0){
					model.setCoupon100(coupon100);
				}else{
					model.setCoupon100(0);
				}
				
				if(coupon200>0){
					model.setCoupon200(coupon200);
				}else{
					model.setCoupon200(0);
				}
				
				//model.setCoupon10(fourDate.getTotalCoupon().get(0)-RedisActivityClient.getReceivedCouponNum(fourDate.getCouponAmountKey().get(0),date));
				//model.setCoupon30(fourDate.getTotalCoupon().get(1)-RedisActivityClient.getReceivedCouponNum(fourDate.getCouponAmountKey().get(1),date));
				//model.setCoupon100(fourDate.getTotalCoupon().get(2)-RedisActivityClient.getReceivedCouponNum(fourDate.getCouponAmountKey().get(2),date));
				//model.setCoupon200(fourDate.getTotalCoupon().get(3)-RedisActivityClient.getReceivedCouponNum(fourDate.getCouponAmountKey().get(3),date));
				//model.setCouponRemind(model.getCoupon10()+model.getCoupon30()+model.getCoupon100()+model.getCoupon200());
			}else if(DateUtils.isDateBetween(DateUtils.getCurrentDate(), DateUtils.getDateTimeFromString(date+" "+fourDate.getReceiveEndTime()), DateUtils.getEndTime(DateUtils.getCurrentDate()))){
				model.setCoupon10(0);
				model.setCoupon30(0);
				model.setCoupon100(0);
				model.setCoupon200(0); 
			}else{
				model.setCoupon10(fourDate.getTotalCoupon().get(0));
				model.setCoupon30(fourDate.getTotalCoupon().get(1));
				model.setCoupon100(fourDate.getTotalCoupon().get(2));
				model.setCoupon200(fourDate.getTotalCoupon().get(3)); 
			}
			
			//登录
			if(builder.isMemberFlag()){
				//当天是否领取过
				RuleBody rb = new RuleBody();
				rb.setActivityId(activity.getId());
				rb.setMemberId(builder.getMemberId());
				rb.setCycleStr(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate()));
				// 校验
				try {
					if (!drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
						model.setIsreceived(true);
					}
				} catch (Exception e) {
					logger.error("【四季变换，有你相伴】初始化失败, activityId={}", activity.getId(), e);
				}
			}else{
				/*	model.setCoupon10(fourDate.getTotalCoupon().get(0));
					model.setCoupon30(fourDate.getTotalCoupon().get(1));
					model.setCoupon100(fourDate.getTotalCoupon().get(2));
					model.setCoupon200(fourDate.getTotalCoupon().get(3)); */
			}
		}else if(activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_AUDIT.getStatus()){
			model.setCoupon10(fourDate.getTotalCoupon().get(0));
			model.setCoupon30(fourDate.getTotalCoupon().get(1));
			model.setCoupon100(fourDate.getTotalCoupon().get(2));
			model.setCoupon200(fourDate.getTotalCoupon().get(3)); 
			if(builder.isMemberFlag()){
				
			}
			
		}else if(activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_END.getStatus()){
			model.setCoupon10(0);
			model.setCoupon30(0);
			model.setCoupon100(0);
			model.setCoupon200(0); 
		}
		}
		return rDO;
	}

	@Override
	public Object receiveAnniversaryCoupon(DynamicParamBuilder paramBuilder) {
		ResultDTO<ActivityForFourChange> rDO = new ResultDTO<ActivityForFourChange>();
		ActivityForFourChange activityReturn=new ActivityForFourChange();
		try {
			//登录认证
			if (!paramBuilder.isMemberFlag()) {
				rDO.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
					return rDO;
			}
			Optional<Activity> fourActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_FOUR_CHANGE_NAME);
			if (!fourActivity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			Activity activity=fourActivity.get();
			if(!paramBuilder.getParamMap().containsKey("couponAmount")){
				rDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
				return rDO;
			}
			int couponAmount = Integer.valueOf(paramBuilder.getParamMap().get("couponAmount").toString());
			if (activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
				String date=DateUtils.getDateStrFromDate(DateUtils.getCurrentDate());
				ActivityForFourChange fourDate = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForFourChange.class,
						ActivityConstant.ACTIVITY_FOUR_CHANGE_KEY);
				//是否在当天的领取时间内
				if(!DateUtils.isDateBetween(DateUtils.getCurrentDate(), DateUtils.getDateTimeFromString(date+" "+fourDate.getReceiveStartTime()), DateUtils.getDateTimeFromString(date+" "+fourDate.getReceiveEndTime()))){
					rDO.setResultCode(ResultCode.ACTIVITY_FOURCHANGE_NOT_START_ERROR);
					return rDO;
				}
				RuleBody rb = new RuleBody();
				rb.setActivityId(activity.getId());
				rb.setMemberId(paramBuilder.getMemberId());
				rb.setActivityName(activity.getActivityDesc());
				rb.setCycleStr(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate()));
				rb.setDeductRemark(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate()));
				//校验当天是否领取过
				if (drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
					Long templateId=1L;
					String amountKey="";
					int totalCoupon=0;
					for(int i=0;i<fourDate.getCouponAmount().size();i++){
						if(fourDate.getCouponAmount().get(i)==couponAmount){
							templateId=fourDate.getTemplateId().get(i);
							amountKey=fourDate.getCouponAmountKey().get(i);
							totalCoupon=fourDate.getTotalCoupon().get(i);
							break;
						}
					}
					//校验红包剩余数量
					if(totalCoupon-RedisActivityClient.getReceivedCouponNum(amountKey,date)<=0){
						rDO.setResultCode(ResultCode.ACTIVITY_FOURCHANGE_RECEIVEDEND_ERROR);
						return rDO;
					}
					// 发放奖励
					RewardsBase rBase = new RewardsBase();
					CouponTemplate c = couponTemplateManager.selectByPrimaryKey(templateId);
					if(c==null){
						rDO.setResultCode(ResultCode.ACTIVITY_JULYTEAM_COUPON_NOTEXITS_ERROR);
						return rDO;
					}
					rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
					rBase.setRewardName(c.getAmount().intValue() + ActivityConstant.couponDesc);
					rBase.setRewardValue(c.getAmount().intValue());
					rBase.setTemplateId(templateId);
					drawByPrizeDirectly.drawLottery(rb, rBase,TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode());
					RedisActivityClient.incrFourChangeCoupon(amountKey,date);
					MessageClient.sendMsgForSPEngin(paramBuilder.getMemberId(), activity.getActivityDesc(), rBase.getRewardName());
					activityReturn.setCoupon10(fourDate.getTotalCoupon().get(0)-RedisActivityClient.getReceivedCouponNum(fourDate.getCouponAmountKey().get(0),date));
					activityReturn.setCoupon30(fourDate.getTotalCoupon().get(1)-RedisActivityClient.getReceivedCouponNum(fourDate.getCouponAmountKey().get(1),date));
					activityReturn.setCoupon100(fourDate.getTotalCoupon().get(2)-RedisActivityClient.getReceivedCouponNum(fourDate.getCouponAmountKey().get(2),date));
					activityReturn.setCoupon200(fourDate.getTotalCoupon().get(3)-RedisActivityClient.getReceivedCouponNum(fourDate.getCouponAmountKey().get(3),date));
				}else{
					rDO.setResultCode(ResultCode.ACTIVITY_FOURCHANGE_RECEIVED_ERROR);
					return rDO;
				}
				rDO.setResult(activityReturn);
				rDO.setIsSuccess();
				return rDO;
			}else{
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
				return rDO;
			}
			
		} catch (Exception e) {
			logger.error("【四季变换，有你相伴】-领取现金券, memberId={}", paramBuilder.getMemberId(), e);
		}
		return rDO;
	}
	/**
	 * 
	 * @desc 周年庆专题页
	 * @param memberId
	 * @return
	 * @author chaisen
	 * @time 2016年10月28日 下午12:41:52
	 *
	 */
	@Override
	public Object anniversarySpecial(DynamicParamBuilder builder) {
		ResultDTO<ActivityAnniversarySpecial> rDO = new ResultDTO<ActivityAnniversarySpecial>();
		ActivityAnniversarySpecial model = new ActivityAnniversarySpecial();
		rDO.setResult(model);
		rDO.setIsSuccess();
		try {
			
			if(builder.isMemberFlag()){
				Long memberId=builder.getMemberId();
				Member member=memberManager.selectByPrimaryKey(memberId);
				if(member!=null){
					model.setRegisterDate(member.getRegisterTime());
					model.setRank(Integer.parseInt(memberId.toString().substring(5, memberId.toString().length())));
					int totalDays=DateUtils.getIntervalDays(DateUtils.formatDate(member.getRegisterTime(), DateUtils.DATE_FMT_3),
							DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3))+1;
					model.setTotalDays(totalDays);
				}
				Transaction tran=transactionManager.firstInvestmentByMemberId(memberId);
				if(tran!=null){
					model.setProjectName(tran.getProjectName());
					model.setFirstInvestDate(tran.getTransactionTime());
					model.setTotalInvest(tran.getTotalInterest());
				}
				model.setTransactionCount(transactionManager.getTransactionCountByMember(memberId));
				Integer vip=memberVipManager.selectMemberVipNewByMemberId(memberId);
				model.setVipLevel(vip);
				//总共赚取
				
				//Transaction transaction=transactionManager.totalInvestInterest(memberId);
				
				MemberTransactionCapital capital = transactionService.getMemberTransactionCapitalTemp(memberId);
				// 待收收益
				BigDecimal receivableInterest = BigDecimal.ZERO;
				// 已收收益
				BigDecimal receivedInterest = BigDecimal.ZERO;
				// 累计投资
		  	     BigDecimal overdueFine = BigDecimal.ZERO;
		  	     //其他收益
		  	     BigDecimal otherIncome = BigDecimal.ZERO;
				if (capital != null) {
					receivableInterest = capital.getReceivableInterest();
					receivedInterest = capital.getReceivedInterest();
					 overdueFine = capital.getOverdueFine();
				}
				BigDecimal savingPotEarnig = balanceService.queryBalance(memberId, TypeEnum.BALANCE_TYPE_MEMBER_TOTAL_PIGGY).getBalance();
				if(savingPotEarnig==null){
					savingPotEarnig=BigDecimal.ZERO;
				}
				//使用现金券
		  	    BigDecimal totalMemberUsedCouponAmount = transactionManager.totalMemberUsedCouponAmount(memberId);
		  	    BigDecimal totalMemberLeaseBonusAmounts = transactionManager.totalMemberLeaseBonusAmounts(memberId);
		  	    otherIncome = totalMemberLeaseBonusAmounts.add(overdueFine);
		  	    BigDecimal totalEarnings = savingPotEarnig.add(receivableInterest).add(receivedInterest).add(otherIncome).add(totalMemberUsedCouponAmount); 
				model.setTotalInvestInterest(totalEarnings);
				if(model.getTotalInvestInterest()!=null){
					//int number=transaction.getInvestAmount().divide(project.getMinInvestAmount()).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
					model.setNumber(model.getTotalInvestInterest().divide(new BigDecimal(3),2,BigDecimal.ROUND_HALF_UP).setScale(0, BigDecimal.ROUND_HALF_UP).intValue());
				}
				
			}
		} catch (Exception e) {
			logger.error("获取用户两周年理财日记失败 ", e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}


	@Override
	public Object anniversaryInit(DynamicParamBuilder builder) {
		ResultDTO<ActivityForAnniversaryCelebrate> rDO = new ResultDTO<ActivityForAnniversaryCelebrate>();
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
		model.setAvailableBalance(anniverDate.getAvailableBalance());
		try {
			ActivityData activityData=activityGroupManager.selectActivityDateByActivityId(activity.getId(),null);
			if(activityData!=null){
				model.setTotalCoupon88(activityData.getDataGole());
			}
		//活动时间内
		Long memberId=0L;
		if(activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()){
			int coupon88=0;
			if(model.getTotalCoupon88()!=null){
				coupon88=model.getTotalCoupon88()-RedisActivityClient.getReceivedCouponEightNum();
			}
			if(coupon88>0){
				model.setCoupon88Remind(coupon88);
			}
			//登录过
			if(builder.isMemberFlag()){
				//收益券是否领取
				memberId=builder.getMemberId();
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
				ActivityLottery activityLottery=activityLotteryManager.selectByMemberActivity(paraMap);
				if(activityLottery!=null){
					model.setMyNumber(activityLottery.getRealCount());
				}
			}
		}else{
			if(builder.isMemberFlag()){
				memberId=builder.getMemberId();
				// 刷新人气值
				Balance balance = balanceManager.queryBalance(memberId, TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY);
				if (balance != null) {
					model.setMyPopularity(balance.getAvailableBalance().intValue());
				}
				Map<String, Object> paraMap = new HashMap<String, Object>();
				paraMap.put("activityId", activity.getId());
				paraMap.put("memberId", memberId);
				paraMap.put("cycleConstraint", activity.getId()+":"+memberId+":coupon");
				ActivityLottery activityLottery=activityLotteryManager.selectByMemberActivity(paraMap);
				if(activityLottery!=null){
					model.setMyNumber(activityLottery.getRealCount());
				}
				model.setPopularity19(activityLotteryResultManager.isReceived(activity.getId(),memberId,"popularity19"));
				model.setPopularity199(activityLotteryResultManager.isReceived(activity.getId(),memberId,"popularity199"));
				model.setPopularity659(activityLotteryResultManager.isReceived(activity.getId(),memberId,"popularity659"));
				model.setPopularity1119(activityLotteryResultManager.isReceived(activity.getId(),memberId,"popularity1119"));
				model.setPopularity2016(activityLotteryResultManager.isReceived(activity.getId(),memberId,"popularity2016"));
				model.setIphone7(activityLotteryResultManager.isReceived(activity.getId(),memberId,"iphone7"));
				model.setTotalMyInvestAmount(transactionManager.getMemberTotalInvestByMemberId(memberId,activity.getStartTime(),activity.getEndTime(),anniverDate.getTotalDays()));
			}
		}
		} catch (ManagerException e) {
			logger.error("【四季变换，有你相伴】初始化失败, activityId={}", activity.getId(), e);
		}
		}
		return rDO;
	}
	
	@Override
	public Object octoberInit(DynamicParamBuilder builder) {
		ResultDTO<ActivityForOctober> rDO = new ResultDTO<ActivityForOctober>();
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
				if(builder.isMemberFlag()){
					model.setMyInvestAmount(transactionManager.getInvestAmountByMemberId(builder.getMemberId(),activity.getId(),activity.getEndTime(),activity.getStartTime()));
				}
			} catch (ManagerException e) {
				logger.error("【金秋狂欢季】初始化失败 ", e);
				rDO.setResultCode(ResultCode.ERROR_SYSTEM);
			}
		}
		return rDO;
	}
	
	/**
	 * 
	 * @desc 周年庆领取现金券
	 * @param paramBuilder
	 * @return
	 * @author chaisen
	 * @time 2016年10月21日 上午9:41:02
	 *
	 */
	@Override
	public Object receiveCouponAnniversary(DynamicParamBuilder paramBuilder) {
		ResultDTO<Object> resultDTO = new ResultDTO<Object>();
		//登录认证
		if (!paramBuilder.isMemberFlag()) {
			resultDTO.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
				return resultDTO;
		}
		if(!paramBuilder.getParamMap().containsKey("couponAmount")||!paramBuilder.getParamMap().containsKey("type")){
			resultDTO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
			return resultDTO;
		}
		int couponAmount = Integer.valueOf(paramBuilder.getParamMap().get("couponAmount").toString());
		int type = Integer.valueOf(paramBuilder.getParamMap().get("type").toString());
		
		try {
			ResultDO<ActivityForAnniversaryRetrun> resultDO= activityLotteryManager.receiveCouponAnniversary(paramBuilder.getMemberId(), couponAmount, type);
			resultDTO.setResultCode(resultDO.getResultCode());
			resultDTO.setResult(resultDO.getResult());
			if(resultDO.isSuccess()){
				resultDTO.setIsSuccess();
			}else{
				resultDTO.setIsError();
			}
		} catch (ManagerException e) {
			resultDTO.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("周年庆领取现金券失败, memberId=" + paramBuilder.getMemberId() + ",errormsg=" + e);
		}
		return resultDTO;
	}
	
	/**
	 * 
	 * @desc 周年庆翻牌赢豪礼
	 * @param paramBuilder
	 * @return
	 * @author chaisen
	 * @time 2016年10月21日 上午9:41:15
	 *
	 */
	@Override
	public Object receiveRewardAnniversary(DynamicParamBuilder paramBuilder) {
		ResultDTO<Object> resultDTO = new ResultDTO<Object>();
		//登录认证
		if (!paramBuilder.isMemberFlag()) {
			resultDTO.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
				return resultDTO;
		}
		if(!paramBuilder.getParamMap().containsKey("popularValue")||!paramBuilder.getParamMap().containsKey("chip")){
			resultDTO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
			return resultDTO;
		}
		int popularValue = Integer.valueOf(paramBuilder.getParamMap().get("popularValue").toString());
		int chip = Integer.valueOf(paramBuilder.getParamMap().get("chip").toString());
		
		try {
			ResultDO<ActivityForAnniversaryRetrun> resultDO= activityLotteryManager.receiveRewardAnniversary(paramBuilder.getMemberId(),popularValue, chip);
			resultDTO.setResultCode(resultDO.getResultCode());
			resultDTO.setResult(resultDO.getResult());
			if(resultDO.isSuccess()){
				resultDTO.setIsSuccess();
			}else{
				resultDTO.setIsError();
			}
		} catch (ManagerException e) {
			resultDTO.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("周年庆翻牌赢豪礼失败, memberId=" + paramBuilder.getMemberId() + ",errormsg=" + e);
		}
		return resultDTO;
	}

	/**
	 * 
	 * @desc 周年庆公益活动初始化
	 * @param builder
	 * @return
	 * @author chaisen
	 * @time 2016年11月15日 上午11:01:43
	 *
	 */
	@Override
	public Object publicWelfareInit(DynamicParamBuilder builder) {
		ResultDTO<ActivityForPublicWelfare> rDO = new ResultDTO<ActivityForPublicWelfare>();
		ActivityForPublicWelfare model = new ActivityForPublicWelfare();
		rDO.setResult(model);
		Optional<Activity> publicActivity = LotteryContainer.getInstance()
				.getActivityByName(ActivityConstant.ACTIVITY_PUBLIC_WELFARE_NAME);
		if (publicActivity.isPresent()) {
		Activity activity = publicActivity.get();
		model.setStatus(activity.getActivityStatus());
		model.setStartTime(activity.getStartTime());
		model.setEndTime(activity.getEndTime());
		try {
			//活动时间内
			if(activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()){
				ActivityLotteryResult activityResult=new ActivityLotteryResult();
				activityResult.setActivityId(activity.getId());
				List<ActivityLotteryResult> resultList= activityLotteryResultMapper.getLotteryResultBySelectiveOrderBy(activityResult);
				List<ActivityForMember> memberList=Lists.newArrayList();
				if(Collections3.isNotEmpty(resultList)){
					for(ActivityLotteryResult bean:resultList){
						ActivityForMember memberActivity=new ActivityForMember();
						Member member=memberManager.selectByPrimaryKey(bean.getMemberId());
						if(member!=null){
							memberActivity.setAvatars(member.getAvatars());
							memberActivity.setUsername(member.getUsername());
							memberActivity.setDateTime(bean.getCreateTime());
						}
						memberList.add(memberActivity);
					}
				}
				model.setMemberList(memberList);
				if(builder.isMemberFlag()){
					Map<String, Object> paraMap = new HashMap<String, Object>();
					paraMap.put("activityId", activity.getId());
					paraMap.put("memberId", builder.getMemberId());
					paraMap.put("cycleConstraint", DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+":publicWelfare");
					ActivityLottery al = activityLotteryMapper.selectByMemberActivity(paraMap);
					if(al!=null){
						model.setReceived(true);
					}
				}
				
			}else{
				
			}
			//支持人数
			model.setPoint(activityLotteryResultManager.countLastBetTotal(activity.getId(),activity.getActivityName()));
			} catch (ManagerException e) {
				logger.error("【有融我心，爱在行动】初始化失败, activityId={}", activity.getId(), e);
			}
			}
		return rDO;
	
	}
	
	/**
	 * 
	 * @desc 支持公益获得人气值
	 * @param paramBuilder
	 * @return
	 * @author chaisen
	 * @time 2016年11月15日 上午11:21:55
	 *
	 */
	@Override
	public Object supportWelfare(DynamicParamBuilder paramBuilder) {
		ResultDTO<ActivityForPublicWelfare> rDO = new ResultDTO<ActivityForPublicWelfare>();
		ActivityForPublicWelfare activityPublic=new ActivityForPublicWelfare();
		try {
			//登录认证
			if (!paramBuilder.isMemberFlag()) {
				rDO.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
					return rDO;
			}
			Optional<Activity> teamActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_PUBLIC_WELFARE_NAME);
			if (!teamActivity.isPresent()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDO;
			}
			Activity activity=teamActivity.get();
			if (activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
				ActivityForPublicWelfare ruleData = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForPublicWelfare.class,
						ActivityConstant.ACTIVITY_PUBLIC_WELFARE_KEY);
				
				RuleBody rb = new RuleBody();
				rb.setActivityId(teamActivity.get().getId());
				rb.setMemberId(paramBuilder.getMemberId());
				rb.setCycleStr(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+":publicWelfare");
				rb.setDeductRemark(activity.getActivityDesc());
				rb.setActivityName(ActivityConstant.ACTIVITY_PUBLIC_WELFARE_NAME);
				// 校验是否参加
				if (!drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
					rDO.setResultCode(ResultCode.ACTIVITY_ANNIVERSARY_NO_CHANCE_ERROR);
					return rDO;
				}
				RewardsBase rBase = new RewardsBase();
				rBase.setRewardValue(ruleData.getPopularity());
				rBase.setRewardName(ruleData.getPopularity() + ActivityConstant.popularityDesc);
				rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_POPULARITY.getType());
				//rb.setDeductRemark("popularity"+popularValueData);
				drawByPrizeDirectly.drawLottery(rb, rBase, "");
				addLotteryRecord(activity.getId(),paramBuilder.getMemberId(),1,1,rb.getCycleStr());
				activityPublic.setPopularity(ruleData.getPopularity());
				activityPublic.setPoint(activityLotteryResultManager.countLastBetTotal(activity.getId(),activity.getActivityName()));
				rDO.setResult(activityPublic);
				rDO.setIsSuccess();
			} else if (activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_AUDIT.getStatus()) {
				rDO.setResultCode(ResultCode.ACTIVITY_NOT_START_ERROR);
				return rDO;
			} else {
				rDO.setResultCode(ResultCode.ACTIVITY_YET_END_ERROR);
				return rDO;
			}
			
		} catch (Exception e) {
			logger.error("【有融我心，爱在行动】爱心支持送人气值失败, memberId={}", paramBuilder.getMemberId(), e);
		}
		return rDO;
	}
	
	private void addLotteryRecord(Long activityId, Long memberId, int totalNum, int realNum, String jsonCondition) throws ManagerException {
		try {
			ActivityLottery model = new ActivityLottery();
			model.setActivityId(activityId);
			model.setMemberId(memberId);
			model.setTotalCount(totalNum);
			model.setRealCount(realNum);
			model.setCycleConstraint(jsonCondition);
			if(activityLotteryMapper.checkExistLottery(model) == null) {
				//第一次获得抽奖机会
				activityLotteryMapper.insertSelective(model);
			} else {
				//累积抽奖机会
				activityLotteryMapper.updateByActivityAndMember(model);
			}
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Object doubleDanInit(DynamicParamBuilder builder) {
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
			if(builder.isMemberFlag()){
				model.setMyInvestAmount(transactionManager.getInvestAmountByMemberIdAndTime(builder.getMemberId(),DateUtils.zerolizedTime(DateUtils.getCurrentDate()),DateUtils.getEndTime(DateUtils.getCurrentDate()),activity.getStartTime()));
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
	public Object doubleReceiveCoupon(DynamicParamBuilder paramBuilder) {
		ResultDTO<Object> resultDTO = new ResultDTO<Object>();
		//登录认证
		if (!paramBuilder.isMemberFlag()) {
			resultDTO.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
				return resultDTO;
		}
		try {
			ResultDO<ActivityForDouble> resultDO= activityLotteryManager.doubleReceiveCoupon(paramBuilder.getMemberId());
			resultDTO.setResultCode(resultDO.getResultCode());
			resultDTO.setResult(resultDO.getResult());
			if(resultDO.isSuccess()){
				resultDTO.setIsSuccess();
			}else{
				resultDTO.setIsError();
			}
		} catch (ManagerException e) {
			resultDTO.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("【双旦狂欢惠】 领取现金券失败, memberId=" + paramBuilder.getMemberId() + ",errormsg=" + e);
		}
		return resultDTO;
	}

	@Override
	public ActivityLottery selectByMemberActivity(Map<String, Object> paraMap) {
		ActivityLottery activityLottery=new ActivityLottery();
		try {
			activityLottery=activityLotteryManager.selectByMemberActivity(paraMap);
		} catch (ManagerException e) {
			logger.error("查询活动抽奖数据异常", paraMap, e);
		}
		return activityLottery;
	}

	@Override
	public Object newYearLuckyMoney(DynamicParamBuilder paramBuilder) {
		ResultDTO<ActivityForNewYear> resultDTO = new ResultDTO<ActivityForNewYear>();
		//登录认证
		if (!paramBuilder.isMemberFlag()) {
			resultDTO.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
			return resultDTO;
		}
		try {
			Long templateId= Long.parseLong(paramBuilder.getParamMap().get("templateid").toString());
			ResultDO<ActivityForNewYear> rDO = activityLotteryManager.newYearLuckyMoney(paramBuilder.getMemberId(),templateId);
			resultDTO.setResultCode(rDO.getResultCode());
			resultDTO.setResult(rDO.getResult());
			if(rDO.isSuccess()){
				resultDTO.setIsSuccess();
			}else{
				resultDTO.setIsError();
			}
		} catch (Exception e) {
			logger.error("【鸡年新年活动】抢红包失败 memberId={}", paramBuilder.getMemberId(), e);
		}
		return resultDTO;
	}

	
	@Override
	public Object getQuickRewardRule() {
		QuickRewardDto quickReward = new QuickRewardDto();
		try {
			Activity activity = new Activity();
			activity.setActivityDesc(ActivityConstant.ACTIVITY_DIRECT_CATALYZER);
			List<Activity> activityList = activityManager
					.getActivityBySelective(activity);
			if (activityList != null) {
				Activity act = activityList.get(activityList.size() - 1);

				String mapJson = act.getObtainConditionsJson();

				Map map = JSON.parseObject(mapJson);

				/*String prizeInPoolJson = map.get("prizeInPool").toString();
				List<PrizeInPool> prizeInPool = JSON.parseArray(
						prizeInPoolJson, PrizeInPool.class);*/
				/*String rewardHour = "3";
				if(map.containsKey("rewardHour")){
					rewardHour = map.get("rewardHour").toString();
				}*/
				
				String lotteryJson = map.get("lottery").toString();
				List<LotteryRuleAmountNumber> lottery = JSON.parseArray(
						lotteryJson, LotteryRuleAmountNumber.class);
				
				QuickRewardConfig  quickRewardConfig = projectExtraManager.getQuickRewardConfig(null);
				
				quickReward.setLottery(lottery);
				//quickReward.setRewardHour(rewardHour);
				quickReward.setQuickRewardConfig(quickRewardConfig);
				
			}
		} catch (Exception e) {
			logger.error("获取最新实时规则", e);
		}
		return quickReward;
	}

	@Override
	public Object grab(DynamicParamBuilder paramBuilder) {
		ResultDTO<Activity> resultDTO = new ResultDTO<Activity>();
		//登录认证
		if (!paramBuilder.isMemberFlag()) {
			resultDTO.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
			return resultDTO;
		}
		try {
			ResultDO<Activity> rDO = activityLotteryManager.newYearGrab(paramBuilder.getMemberId());
			if (rDO.isSuccess()){
				Activity activity=rDO.getResult();
				grabSendPopularity(activity,paramBuilder.getMemberId());
			}
			resultDTO.setResultCode(rDO.getResultCode());
			if(rDO.isSuccess()){
				resultDTO.setIsSuccess();
			}else{
				resultDTO.setIsError();
			}
		} catch (Exception e) {
			logger.error("【鸡年新年活动】抢红包失败 memberId={}", paramBuilder.getMemberId(), e);
		}
		return resultDTO;
	}

	/**
	 * 除夕活动发放人气值
	 * @param activity
	 * @param memberId
	 */
	private void grabSendPopularity(final Activity activity,final Long memberId){
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					ActivityForNewYearGrab grab = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForNewYearGrab.class,
							ActivityConstant.ACTIVITY_GRABBAG_KEY);
					RewardsBase rBase = new RewardsBase();
					RuleBody rb = new RuleBody();
					rBase.setRewardName(grab.getPopularity() + ActivityConstant.popularityDesc);
					rBase.setRewardValue(grab.getPopularity());
					rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_POPULARITY.getType());
					rb.setDeductRemark(activity.getActivityDesc());
					rb.setMemberId(memberId);
					rb.setActivityId(activity.getId());
					rb.setActivityName(activity.getActivityDesc());
					rb.setCycleStr(activity.getId()+"-"+memberId);
					drawByPrizeDirectly.drawLottery(rb, rBase, "");
					String rewardInfo=grab.getPopularity()+ActivityConstant.popularityDesc;
					//发送站内信
					MessageClient.sendMsgForSPEngin(memberId, ActivityConstant.ACTIVITY_GRABBAG, rewardInfo);
					List<ActivityGrabResultBiz> list= activityLotteryResultManager.queryActivityGrabResult(activity.getId());
					//中奖结果存入redis
					String grabResultkey=RedisConstant.REDIS_KEY_GRAB+RedisConstant.REDIS_SEPERATOR+RedisConstant.REDIS_KEY_GRAB_RESULT;
					RedisManager.putObject(grabResultkey,list);
					RedisManager.expire(grabResultkey,DateUtils.calculateCurrentToEndTime());
				} catch (Exception e) {
					logger.error("【除夕抢压岁钱】 发送人气值失败memberId={}",memberId, e);
				}
			}
		});
	}

	@Override
	public List<ActivityGrabResultBiz> queryGrabResult(Long activityId) {
		List<ActivityGrabResultBiz> list=new ArrayList<>();
		//中奖结果
		String grabResultkey=RedisConstant.REDIS_KEY_GRAB+RedisConstant.REDIS_SEPERATOR+RedisConstant.REDIS_KEY_GRAB_RESULT;
		try {
			list=(List<ActivityGrabResultBiz>)RedisManager.getObject(grabResultkey);
		} catch (Exception e) {
			try {
				list = activityLotteryResultManager.queryActivityGrabResult(activityId);
			} catch (ManagerException e1) {
				logger.error("查询活动中奖结果失败 activityId={}",activityId, e);
			}
		}
		return list;
	}

	@Override
	public Integer queryCountByActivityId(Long activityId) {
		//查询已抢压岁钱数目
		String grabedCount=RedisConstant.REDIS_KEY_GRAB+RedisConstant.REDIS_SEPERATOR+RedisConstant.REDIS_KEY_GRAB_COUNT;
		try {
			String hget = RedisManager.hget(grabedCount, RedisConstant.REDIS_KEY_GRAB_COUNT);
			if (StringUtil.isNumeric(hget)) {
				return Integer.valueOf(hget);
			}
		} catch (Exception e) {
			Integer count= activityLotteryManager.queryCountByActivityId(activityId);
			return count;
		}
		return 0;
	}

	@Override
	public boolean queryGrabStatus(Long memberId,Long activityId) {
		//标示用户是否已经抢过key
		String key = RedisConstant.REDIS_KEY_GRAB+RedisConstant.REDIS_SEPERATOR+memberId;
		try {
			if (RedisManager.isExit(key)){
				return false;
			}else {
				return true;
			}
		} catch (Exception e) {
			Map<String,Object> map=new HashMap<>();
			map.put("activityId",activityId);
			map.put("memberId",memberId);
			String cycleConstraint = activityId + "-" + memberId + ":newYearGrab";
			map.put("cycleConstraint",cycleConstraint);
			ActivityLottery activityLottery= null;
			try {
				activityLottery = activityLotteryManager.selectByMemberActivity(map);
			} catch (ManagerException e1) {
				logger.error("查询用户是否中奖异常 memberid={} activityId={}",memberId,activityId, e);
				return false;
			}
			if (activityLottery!=null){
				return false;
			}else {
				return true;
			}
		}
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
	public ResultDTO<Activity> goddessSign(DynamicParamBuilder paramBuilder) {
		ResultDTO<Activity> resultDTO = new ResultDTO<Activity>();
		//登录认证
		if (!paramBuilder.isMemberFlag()) {
			resultDTO.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
			return resultDTO;
		}
		try {
			ResultDO<Activity> rDO = activityLotteryManager.womensDaySign(paramBuilder.getMemberId());
			resultDTO.setResultCode(rDO.getResultCode());
			if(rDO.isSuccess()){
				resultDTO.setIsSuccess();
			}else{
				resultDTO.setIsError();
			}
		} catch (Exception e) {
			logger.error("【鸡年新年活动】抢红包失败 memberId={}", paramBuilder.getMemberId(), e);
		}
		return resultDTO;
	}

	@Override
	public ResultDTO<ActivityForWomensDay> womensDayInit(Long memberId) {
		ResultDTO<ActivityForWomensDay> resultDTO = new ResultDTO<ActivityForWomensDay>();
		ResultDO<ActivityForWomensDay> rDO=new ResultDO<>();
		try {
			rDO = activityLotteryManager.womensDayData(memberId);
			resultDTO.setResultCode(rDO.getResultCode());
			resultDTO.setResult(rDO.getResult());
			if(rDO.isSuccess()){
				resultDTO.setIsSuccess();
			}else{
				resultDTO.setIsError();
			}
		} catch (Exception e) {
			logger.error("【38节】初始数据异常 memberId={}", memberId, e);
		}
		return resultDTO;
	}

	@Override
	public ResultDTO<Activity> womensDayBag(DynamicParamBuilder paramBuilder) {
		ResultDTO<Activity> resultDTO = new ResultDTO<Activity>();
		//登录认证
		if (!paramBuilder.isMemberFlag()) {
			resultDTO.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
			return resultDTO;
		}
		try {
			ResultDO<ActivityForWomensDay> rDO = activityLotteryManager.womensDayBag(paramBuilder.getMemberId());
			resultDTO.setResultCode(rDO.getResultCode());
			if(rDO.isSuccess()){
				resultDTO.setIsSuccess();
			}else{
				resultDTO.setIsError();
			}
		} catch (Exception e) {
			logger.error("【38节】领取礼包失败 memberId={}", paramBuilder.getMemberId(), e);
		}
		return resultDTO;
	}


	/**
	 * 50亿初始化
	 */
	@Override
	public Object fiveBillionIndex(DynamicParamBuilder builder) {
		ResultDTO<ActivityForFiveBillionInit> rDO = new ResultDTO<ActivityForFiveBillionInit>();
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
				if(builder.isMemberFlag()){
					memberId=builder.getMemberId();
					//福袋是否领取
					 ActivityLotteryResult luckResult=activityLotteryResultManager.queryLotteryResultByRemark(activity.getId(),memberId, memberId+":luckBag");
					 if(luckResult!=null&&luckResult.getChip()>0){
						 model.setReward(luckResult.getChip());
					 }
					//累计投资额
					/*BigDecimal myInvestAmount=transactionManager.findTotalInvestAmount(memberId, activity.getStartTime(), activity.getEndTime());
					if(myInvestAmount==null){
						myInvestAmount=BigDecimal.ZERO;
					}
					model.setMyInvestAmount(myInvestAmount);*/
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
	public Object receiveLuckBag(DynamicParamBuilder paramBuilder) {
		ResultDTO<Object> resultDTO = new ResultDTO<Object>();
		//登录认证
		if (!paramBuilder.isMemberFlag()) {
			resultDTO.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
				return resultDTO;
		}
		if(!paramBuilder.getParamMap().containsKey("couponAmount")){
			resultDTO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
			return resultDTO;
		}
		int couponAmount = Integer.valueOf(paramBuilder.getParamMap().get("couponAmount").toString());
		try {
			ResultDO<ActivityForFiveBillionRetrun> resultDO= activityLotteryManager.receiveLuckBag(paramBuilder.getMemberId(),couponAmount);
			resultDTO.setResultCode(resultDO.getResultCode());
			resultDTO.setResult(resultDO.getResult());
			if(resultDO.isSuccess()){
				resultDTO.setIsSuccess();
			}else{
				resultDTO.setIsError();
			}
		} catch (ManagerException e) {
			resultDTO.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("【福临50亿】-领取福袋失败, memberId=" + paramBuilder.getMemberId() + ",errormsg=" + e);
		}
		return resultDTO;
	}

	@Override
	public Object lotteryLuckBoth(DynamicParamBuilder paramBuilder) {
		ResultDTO<Object> resultDTO = new ResultDTO<Object>();
		//登录认证
		if (!paramBuilder.isMemberFlag()) {
			resultDTO.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
				return resultDTO;
		}
		if(!paramBuilder.getParamMap().containsKey("type")){
			resultDTO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
			return resultDTO;
		}
		int type = Integer.valueOf(paramBuilder.getParamMap().get("type").toString());
		try {
			ResultDO<ActivityForFiveBillionRetrun> resultDO= activityLotteryManager.lotteryLuckBoth(paramBuilder.getMemberId(),type);
			resultDTO.setResultCode(resultDO.getResultCode());
			resultDTO.setResult(resultDO.getResult());
			if(resultDO.isSuccess()){
				resultDTO.setIsSuccess();
			}else{
				resultDTO.setIsError();
			}
		} catch (ManagerException e) {
			resultDTO.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("【福临50亿】-抽奖失败, memberId=" + paramBuilder.getMemberId() + ",errormsg=" + e);
		}
		return resultDTO;
	}

	/*@Override
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
	}*/

	
	@Override
	public Object myLotteryRecord(DynamicParamBuilder paramBuilder) {
		ResultDTO<Object> resultDTO = new ResultDTO<Object>();
		//登录认证
		if (!paramBuilder.isMemberFlag()) {
				return resultDTO;
		}
		if(!paramBuilder.getParamMap().containsKey("type")){
			return resultDTO;
		}
		if(!paramBuilder.getParamMap().containsKey("pageNo")){
			return resultDTO;
		}
		int type = Integer.valueOf(paramBuilder.getParamMap().get("type").toString());
		int pageNo = Integer.valueOf(paramBuilder.getParamMap().get("pageNo").toString());
		ActivityLotteryResultQuery query = new ActivityLotteryResultQuery();
		 query.setMemberId(paramBuilder.getMemberId());
		 query.setCurrentPage(pageNo);
		 query.setPageSize(20);
		 query.setType(type);
		 if(query.getType()==1){
	    	 query.setRemark("luckBoth");
	     }else if(query.getType()==2){
	    	 query.setRemark("luckMonkey");
	     }
		 Optional<Activity> fiveActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_FIVEBILLION_NAME);
			if (!fiveActivity.isPresent()) {
				return resultDTO;
			}
			Activity activity = fiveActivity.get();
			query.setActivityId(activity.getId());
			try {
				Page<ActivityForFiveBillionRetrun> pager = activityLotteryManager.activityLotteryResultListByPage(query);
				resultDTO.setResult(pager);
			} catch (ManagerException e) {
				logger.error("获取中奖记录列表失败,query={}", query, e);
			}
			return resultDTO;
	}

	@Override
	public ResultDTO<Activity> subscription(DynamicParamBuilder paramBuilder) {
		ResultDTO<Activity> resultDTO = new ResultDTO<Activity>();
		//登录认证
		if (!paramBuilder.isMemberFlag()) {
			resultDTO.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
			return resultDTO;
		}
		try {
			ResultDO<Activity> rDO = activityLotteryManager.subscription(paramBuilder.getMemberId());
			if(rDO.isSuccess()){
                resultDTO.setIsSuccess();
            }else{
                resultDTO.setIsError();
				resultDTO.setResultCode(rDO.getResultCode());
            }
		} catch (Exception e) {
			logger.error("【订阅号】活动领取失败 memberId={}", paramBuilder.getMemberId(), e);
		}
		return resultDTO;
	}

	@Override
	public ResultDO<ActivityForSubscription> subscriptionData(Long memberId) {
		return activityLotteryManager.subscriptionData(memberId);
	}

	
	@Override
	public Object dayDropGoldInit(DynamicParamBuilder builder) {
		ResultDTO<ActivityForDayDropInit> rDO = new ResultDTO<ActivityForDayDropInit>();
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
			if(builder.isMemberFlag()){
				String dateStr = DateUtils.getStrFromDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3);
				ActivityLotteryResultNew newA=new ActivityLotteryResultNew();
				newA.setRemark(activity.getId()+":"+dateStr);
				newA.setActivityId(activity.getId());
				newA.setMemberId(builder.getMemberId());
				BigDecimal totalAmount=activityLotteryResultNewMapper.sumRewardInfoByProjectId(newA);
				//我的今日投资额
				model.setMyInvestAmount(totalAmount);
				ActivityLotteryResultNew totalNew=new ActivityLotteryResultNew();
				totalNew.setActivityId(activity.getId());
				totalNew.setMemberId(builder.getMemberId());
				totalNew.setStartTime(DateUtils.zerolizedTime(activity.getStartTime()));
				totalNew.setEndTime(DateUtils.getEndTime(activity.getEndTime()));
				//我的累计投资额
				model.setMyTotalInvestAmount(activityLotteryResultNewMapper.sumRewardInfoByProjectId(totalNew));
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
	public Object receiveCouponGold(DynamicParamBuilder paramBuilder) {
		ResultDTO<Object> resultDTO = new ResultDTO<Object>();
		//登录认证
		if (!paramBuilder.isMemberFlag()) {
			resultDTO.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
				return resultDTO;
		}
		if(!paramBuilder.getParamMap().containsKey("couponAmount")){
			resultDTO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
			return resultDTO;
		}
		int couponAmount = Integer.valueOf(paramBuilder.getParamMap().get("couponAmount").toString());
		try {
			ResultDO<Object> resultDO= activityLotteryManager.receiveCouponGold(paramBuilder.getMemberId(),couponAmount,"app");
			resultDTO.setResultCode(resultDO.getResultCode());
			resultDTO.setResult(resultDO.getResult());
			if(resultDO.isSuccess()){
				resultDTO.setIsSuccess();
			}else{
				resultDTO.setIsError();
			}
		} catch (Exception e) {
			resultDTO.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("【天降金喜】-领取现金券失败, memberId=" + paramBuilder.getMemberId() + ",errormsg=" + e);
		}
		return resultDTO;
	}

	@Override
	public ResultDTO<ActivityForInviteFriend> inviteFriendData(DynamicParamBuilder paramBuilder) {
		ResultDTO<ActivityForInviteFriend> resultDTO = new ResultDTO<ActivityForInviteFriend>();
		ResultDO<ActivityForInviteFriend> resultDO=null;
		try {
			//登录认证
			if (!paramBuilder.isMemberFlag()) {
				resultDO=activityLotteryManager.inviteFriendData(null);
			}else {
				resultDO=activityLotteryManager.inviteFriendData(paramBuilder.getMemberId());
			}
			if(resultDO.isSuccess()){
				resultDTO.setIsSuccess();
				resultDTO.setResult(resultDO.getResult());
			}else{
				resultDTO.setIsError();
				resultDTO.setResultCode(resultDO.getResultCode());
			}
			return resultDTO;
		} catch (Exception e) {
			logger.error("获取邀请好友数据异常,memberId={}", paramBuilder.getMemberId(), e);
		}
		return null;
	}

	@Override
	public ResultDTO<Page<ActivityForInviteFriendDetail>> inviteFriendDetail(DynamicParamBuilder paramBuilder) {
		ResultDTO<Page<ActivityForInviteFriendDetail>> resultDTO=new ResultDTO<Page<ActivityForInviteFriendDetail>>();
		Page<ActivityForInviteFriendDetail> page=new Page<ActivityForInviteFriendDetail>();
		Integer pageNo=1;
		if (paramBuilder.getParamMap().containsKey("pageNo")){
			try {
				pageNo=(Integer) paramBuilder.getParamMap().get("pageNo");
			} catch (Exception e) {
				logger.error("获取邀请好友详情数据页面转换异常,memberId={}", paramBuilder.getMemberId(), e);
				pageNo=1;
			}
		}
		try {
			if (!paramBuilder.isMemberFlag()){
				page=activityLotteryResultManager.queryInviteFriendDetail(null,pageNo);
            }else {
				page=activityLotteryResultManager.queryInviteFriendDetail(paramBuilder.getMemberId(),pageNo);
            }
			resultDTO.setIsSuccess();
			resultDTO.setResult(page);
		} catch (Exception e) {
			resultDTO.setIsError();
			logger.error("获取邀请好友详情数据异常,memberId={}", paramBuilder.getMemberId(), e);
		}
		return resultDTO;
	}

	@Override
	public ResultDTO<Activity> receiveFlowers(DynamicParamBuilder paramBuilder) {
		ResultDTO<Activity> resultDTO = new ResultDTO<Activity>();
		try {
			//登录认证
			if (!paramBuilder.isMemberFlag()) {
                resultDTO.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
                return resultDTO;
            }
			ResultDO<Activity> resultDO = activityLotteryManager.receiveFlowers(paramBuilder.getMemberId());
			resultDTO.setResultCode(resultDO.getResultCode());
			resultDTO.setResult(resultDO.getResult());
			if(resultDO.isSuccess()){
				resultDTO.setIsSuccess();
			}else{
				resultDTO.setIsError();
			}
			return resultDTO;
		} catch (Exception e) {
			logger.error("【女神送花】初始数据异常, memberId=" + paramBuilder.getMemberId() + ",errormsg=" + e);
		}
		return resultDTO;
	}

	@Override
	public ResultDTO<ActivityForSubscription> flowersData(Long memberId) {
		ResultDTO<ActivityForSubscription> resultDTO = new ResultDTO<ActivityForSubscription>();
		try {
			ResultDO<ActivityForSubscription> resultDO= activityLotteryManager.flowersData(memberId);
			resultDTO.setResultCode(resultDO.getResultCode());
			resultDTO.setResult(resultDO.getResult());
			if(resultDO.isSuccess()){
                resultDTO.setIsSuccess();
            }else{
                resultDTO.setIsError();
            }
			return resultDTO;
		} catch (Exception e) {
			resultDTO.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("【女神送花】初始数据异常, memberId=" + memberId + ",errormsg=" + e);
		}
		return resultDTO;
	}
	
	@Override
	public Object springComingInit(DynamicParamBuilder builder) {
		ResultDTO<Object> rDTO = new ResultDTO<Object>();
		try {
			Long memberId = null;
			if(builder.isMemberFlag()){
				memberId =builder.getMemberId();
			}
			ResultDO<Object> rDO = activityLotteryManager.springComingInit(memberId);
			rDTO.setResultCode(rDO.getResultCode());
			rDTO.setResult(rDO.getResult());
			if(rDO.isSuccess()){
				rDTO.setIsSuccess();
			}else{
				rDTO.setIsError();
			}
		} catch (Exception e) {
			logger.error("【好春来】初始化 builder={}",builder, e);
		}
		return rDTO;
	}
	
	@Override
	public Object springComingActivity(DynamicParamBuilder paramBuilder) {
		ResultDTO<Object> resultDTO = new ResultDTO<Object>();
		//登录认证
		if (!paramBuilder.isMemberFlag()) {
			resultDTO.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
				return resultDTO;
		}
		if(!paramBuilder.getParamMap().containsKey("templateId")){
			resultDTO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
			return resultDTO;
		}
		Long templateId = Long.valueOf(paramBuilder.getParamMap().get("templateId").toString());
		try {
			ResultDO<Object> resultDO= activityLotteryManager.springComingReceiveCoupon(paramBuilder.getMemberId(),templateId);
			resultDTO.setResultCode(resultDO.getResultCode());
			resultDTO.setResult(resultDO.getResult());
			if(resultDO.isSuccess()){
				resultDTO.setIsSuccess();
			}else{
				resultDTO.setIsError();
			}
		} catch (Exception e) {
			resultDTO.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("【好春来】-领取现金券失败, memberId=" + paramBuilder.getMemberId() + ",errormsg=" + e);
		}
		return resultDTO;
	}

	@Override
	public ResultDTO<ActivityForLabor> laborData(Long memberId) {
		ResultDTO<ActivityForLabor> resultDTO = new ResultDTO<ActivityForLabor>();
		try {
			ResultDO<ActivityForLabor> resultDO= activityLotteryManager.laborInit(memberId);
			if(resultDO.isSuccess()){
				resultDTO.setIsSuccess();
				resultDTO.setResult(resultDO.getResult());
			}else{
				resultDTO.setIsError();
				resultDTO.setResultCode(resultDO.getResultCode());
			}
			return resultDTO;
		} catch (Exception e) {
			resultDTO.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("【劳动最光荣】初始数据异常, memberId=" + memberId + ",errormsg=" + e);
		}
		return resultDTO;
	}

	@Override
	public ResultDTO<Activity> receiveLabor(DynamicParamBuilder paramBuilder) {
		ResultDTO<Activity> resultDTO = new ResultDTO<Activity>();
		try {
			//登录认证
			if (!paramBuilder.isMemberFlag()) {
				resultDTO.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
				return resultDTO;
			}
			ResultDO<Activity> resultDO = activityLotteryManager.laborGift(paramBuilder.getMemberId());
			resultDTO.setResultCode(resultDO.getResultCode());
			resultDTO.setResult(resultDO.getResult());
			if(resultDO.isSuccess()){
				resultDTO.setIsSuccess();
			}else{
				resultDTO.setIsError();
			}
			return resultDTO;
		} catch (Exception e) {
			logger.error("【女神送花】初始数据异常, memberId=" + paramBuilder.getMemberId() + ",errormsg=" + e);
		}
		return resultDTO;
	}
}
