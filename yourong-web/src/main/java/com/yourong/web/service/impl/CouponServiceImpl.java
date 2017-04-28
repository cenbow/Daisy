package com.yourong.web.service.impl;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.yourong.core.mc.manager.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.yourong.common.cache.RedisManager;
import com.yourong.common.cache.RedisPlatformClient;
import com.yourong.common.constant.ActivityConstant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.RemarksEnum;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.core.common.MessageClient;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.PopularityInOutLogManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.lottery.draw.DrawByPrizeDirectly;
import com.yourong.core.lottery.model.RewardsBase;
import com.yourong.core.lottery.model.RuleBody;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.mc.model.ActivityRule;
import com.yourong.core.mc.model.Coupon;
import com.yourong.core.mc.model.CouponTemplate;
import com.yourong.core.mc.model.biz.ActivityBiz;
import com.yourong.core.mc.model.biz.ActivityForRedFriday;
import com.yourong.core.mc.model.biz.RedFridayRuleBiz;
import com.yourong.core.mc.model.query.CouponQuery;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.Member;
import com.yourong.web.service.CouponService;
import com.yourong.web.utils.WebPropertiesUtil;

@Service
public class CouponServiceImpl implements CouponService {
	private static Logger logger = LoggerFactory.getLogger(CouponServiceImpl.class);

	@Autowired
	private CouponManager couponManager;
	@Autowired
	private BalanceManager balanceManager;
	@Autowired
	private CouponTemplateManager couponTemplateManager;
	@Autowired
	private CouponTemplateRelationManager couponTemplateRelationManager;
	@Autowired
	private PopularityInOutLogManager popularityInOutLogManager;
	@Autowired
	private MemberManager memberManager;
	@Autowired
	private ActivityManager activityManager;
	@Autowired
	private ActivityRuleManager activityRuleManager;
	@Autowired
	private DrawByPrizeDirectly drawByPrizeDirectly;
	/**
	 * 分页获取优惠券列表
	 */
	@Override
	public Page<Coupon> findCouponsByPage(CouponQuery couponQuery) {
		try {
			Page<Coupon> couponPage = couponManager.findFrontCouponsByPage(couponQuery);
			return couponPage;
		} catch (ManagerException e) {
			logger.error("前台分页获取优惠券列表失败,couponQuery=" + couponQuery.toString(), e);
		}

		return null;
	}

	/**
	 * 根据状态获取优惠券总额
	 */
	@Override
	public BigDecimal findTotalAmountByStatus(CouponQuery couponQuery) {
		try {
			Coupon coupon = couponManager.findTotalAmountByStatus(couponQuery);
			if (coupon != null) {
				return coupon.getAmount();
			}
		} catch (Exception e) {
			logger.error("根据状态获取优惠券总额，couponQuery=" + couponQuery, e);
		}
		return BigDecimal.ZERO;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<CouponTemplate> exchangeCoupon(Long memberId, Long couponTemplateId, int num) throws Exception {
		ResultDO<CouponTemplate> result = new ResultDO<CouponTemplate>();
		try {
			if (couponTemplateId == null) {
				result.setResultCode(ResultCode.COUPONTEMPLATE_ID_IS_NULL_ERROR);
				return result;
			}
			CouponTemplate couponTemplate = couponTemplateManager.selectByPrimaryKey(couponTemplateId);
			if (couponTemplate == null) {
				result.setResultCode(ResultCode.COUPONTEMPLATE_NOT_EXIST_ERROR);
				return result;
			}
			// 判断用户是否有足够的人气值
			Balance PopularityBalance = balanceManager.queryBalanceLocked(memberId,
					TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY);
			BigDecimal exchangePopularityValue = BigDecimal.ZERO;
			String remark = "";
			TypeEnum type = TypeEnum.FIN_POPULARITY_TYPE_EXCHANGE; 
			if(couponTemplate.getCouponType().intValue()==TypeEnum.COUPON_TYPE_CASH.getType()){
				exchangePopularityValue = couponTemplate.getAmount().multiply(new BigDecimal(num));
				remark = MessageFormat.format(RemarksEnum.COUPON_EXCHANGE_POPULARITY_BALANCE.getRemarks(), couponTemplate.getAmount());
			}else if(couponTemplate.getCouponType().intValue()==TypeEnum.COUPON_TYPE_INCOME.getType()){
				remark = MessageFormat.format(RemarksEnum.COUPON_PROFIT_EXCHANGE_POPULARITY_BALANCE.getRemarks(), couponTemplate.getAmount());
				type = TypeEnum.FIN_POPULARITY_TYPE_PROFIT_EXCHANGE; 
				if (couponTemplate.getAmount().compareTo(new BigDecimal(0.5)) == 0) {
					couponTemplate.setExchangeAmount(new BigDecimal(WebPropertiesUtil
							.getProperties("business.exchangeProfitCouponAmount0.5")));
				}
				if (couponTemplate.getAmount().compareTo(new BigDecimal(1)) == 0) {
					couponTemplate.setExchangeAmount(new BigDecimal(WebPropertiesUtil
							.getProperties("business.exchangeProfitCouponAmount1")));
				}
				if (couponTemplate.getAmount().compareTo(new BigDecimal(1.5)) == 0) {
					couponTemplate.setExchangeAmount(new BigDecimal(WebPropertiesUtil
							.getProperties("business.exchangeProfitCouponAmount1.5")));
				}
				if (couponTemplate.getAmount().compareTo(new BigDecimal(2)) == 0) {
					couponTemplate.setExchangeAmount(new BigDecimal(WebPropertiesUtil
							.getProperties("business.exchangeProfitCouponAmount2")));
				}
				exchangePopularityValue = couponTemplate.getExchangeAmount().multiply(new BigDecimal(num));
			}
			if (PopularityBalance.getAvailableBalance().doubleValue() < exchangePopularityValue.doubleValue()) {
				result.setResultCode(ResultCode.COUPON_EXCHANGE_POPULARITY_NOT_ENOUGH_ERROR);
				return result;
			}
			// 通过模板id领取优惠券
			for (int i = 0; i < num; i++) {
				//couponManager.receiveCoupon(memberId, null, couponTemplateId, -1L);// -1:系统调用
				couponManager.receiveCouponSource(memberId, null, couponTemplateId, -1L,
						TypeEnum.COUPON_WAY_WEB.getType(),TypeEnum.COUPON_ACCESS_SOURCE_POPULARITY.getType());// -1:系统调用 web发送，人气值兑换
			}
			//调用扣减人气值方法
			
			popularityInOutLogManager.rechargeReducePopularity(couponTemplateId, memberId, type,
					exchangePopularityValue, remark);
			
			MessageClient.sendMsgForExchangePopularity(DateUtils.getCurrentDate(), exchangePopularityValue.intValue(),
					couponTemplate.getAmount(), memberId, couponTemplate.getCouponType());
			result.setResult(couponTemplate);
		} catch (Exception e) {
			throw e;
		}
		return result;
	}

	@Override
	public Coupon getCouponByMemberIdAndActivity(Long memberId, long activityId) {
		try {
			List<Coupon> coupons = couponManager.getCouponByMemberIdAndActivity(memberId, activityId);
			if (Collections3.isNotEmpty(coupons)) {
				return couponManager.getCouponByMemberIdAndActivity(memberId, activityId).get(0);
			}
		} catch (Exception e) {
			String string = String.format("通过会员id和活动查询优惠券，memberId=%s", memberId);
			logger.error(string, e);
		}

		return null;
	}

	@Override
	public int getMemberCouponCount(Long memberId) {
		try {
			return couponManager.getMemberCouponCount(memberId);
		} catch (Exception e) {
			logger.error("获取优惠券数量，memberId=" + memberId, e);
		}

		return 0;
	}

	@Override
	public int getMemberCouponCountByType(Long memberId, Integer couponType) {
		try {
			return couponManager.getMemberCouponCountByType(memberId, couponType);
		} catch (Exception e) {
			logger.error("获取不同类型的优惠券数量，holderId=" + memberId, e);
		}
		return 0;
	}

	@Override
	public ResultDO<Coupon> receiveGiftmoney(Long memberId, Long activityId) {
		try {
			ResultDO<Coupon> result = new ResultDO<Coupon>();
			// 取出活动信息
			Activity activity = activityManager.selectByPrimaryKey(activityId);
			if (activity == null) {
				result.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return result;
			}
			// 判断活动是否已经开始
			if (!DateUtils.isDateBetween(DateUtils.getCurrentDate(), activity.getStartTime(), activity.getEndTime())) {
				if (DateUtils.getCurrentDate().before(activity.getStartTime())) {
					result.setResultCode(ResultCode.ACTIVITY_NOT_START_ERROR);
					return result;
				}
				if (DateUtils.getCurrentDate().after(activity.getEndTime())) {
					result.setResultCode(ResultCode.ACTIVITY_YET_END_ERROR);
					return result;
				}
			}
			// 判断用户是否已经领取
			List<Coupon> coupons = couponManager.getCouponByMemberIdAndActivity(memberId, activityId);
			if (Collections3.isNotEmpty(coupons) && coupons.size() > 0) {
				result.setResultCode(ResultCode.ACTIVITY_YET_JOIN_ACTIVITY_ERROR);
				return result;
			}
			Coupon coupon = couponManager.receiveCoupon(memberId, activityId, null, -1L);
			// TODO 将已经赠送的会员放到redis里面
			if (coupon != null) {
				Member member = memberManager.selectByPrimaryKey(memberId);
				RedisPlatformClient.addReceiveGiftMoneyMember(memberId,
						StringUtil.maskUserNameOrMobile(member.getUsername(), member.getMobile()),
						StringUtil.getFilePath(member.getAvatars(), "40x40"));
				result.setResult(coupon);
			}
			return result;
		} catch (Exception e) {
			logger.error("领取压岁钱现金券发生异常，memberId=" + memberId + ", activityId=" + activityId, e);
		}
		return null;
	}

	/**
	 * 活动领取优惠券（1.必须在活动时间内 2.满足既定投资总额 3.不能重复领取）
	 */
	@Override
	public ResultDO<Coupon> receiveCoupon(Long memberId, Long activityId) {
		ResultDO<Coupon> result = new ResultDO<Coupon>();
		try {
			Date currentDate = DateUtils.getCurrentDate();
			// 根据活动id查找活动
			Activity act = activityManager.selectByPrimaryKey(activityId);
			if (act == null) {
				result.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return result;
			}
			// 判断当前是否在活动期间
			int compare2StartDate = DateUtils.compareDate(currentDate, act.getStartTime());
			int compare2EndDate = DateUtils.compareDate(currentDate, act.getEndTime());
			if (compare2StartDate == DateUtils.AFTER) {
				result.setResultCode(ResultCode.ACTIVITY_XIAOMING_STORY_NOT_START_ERROR);
				return result;
			}
			if (compare2EndDate == DateUtils.BEFORE) {
				result.setResultCode(ResultCode.ACTIVITY_XIAOMING_STORY_YET_END_ERROR);
				return result;
			}
			// 判断该活动是否已领取优惠券
			List<Coupon> coupon = couponManager.getCouponByMemberIdAndActivity(memberId, activityId);
			if (Collections3.isNotEmpty(coupon)) {
				result.setResultCode(ResultCode.ACTIVITY_XIAOMING_STORY_YET_JOIN_ACTIVITY_ERROR);
				return result;
			}
			// 判断用户是否满投资额
			// 如果没有这个条件会返回true
			boolean moreOrEqualInvestAmount = couponManager.moreOrEqualInvestTotalAmount(memberId, activityId);
			if (!moreOrEqualInvestAmount) {
				result.setResultCode(ResultCode.ACTIVITY_NOT_MEET_INVEST_CONDITIONS_ERROR);
				return result;
			}
			// 领取优惠券
			Coupon c = couponManager.receiveCoupon(memberId, activityId, null, -1L); // 系统发送优惠券
			if (c == null) {
				result.setResultCode(ResultCode.COUPON_INCOME_RECEIVE_ERROR);
			}
		} catch (ManagerException e) {
			logger.error("领用收益券失败，memberId =" + memberId + "acitityId = " + activityId, e);
			result.setResultCode(ResultCode.COUPON_INCOME_RECEIVE_ERROR);
		}
		return result;
	}

	@Override
	public List<Coupon> getCouponsByCouponTemplateId(Long activityId) {
		try {
			Long templateId = couponManager.getTemplateIdByActivityId(activityId);
			if (templateId != null) {
				int showNum = 28;
				return couponManager.getCouponsByCouponTemplateId(templateId, showNum);
			}
		} catch (Exception e) {
			logger.error("根据活动Id获取几条领取过的优惠券失败，activityId=" + activityId, e);
		}
		return null;
	}

	@Override
	public List<CouponTemplate> findExchangeCouponsByIds(Long[] ids) {
		try {
			List<CouponTemplate> coupons = couponTemplateManager.findExchangeCouponsByIds(ids);
			return coupons;
		} catch (Exception e) {
			logger.error("根据id获取用于兑换人气值的优惠券列表失败，ids=" + ids, e);
		}
		return null;
	}

    @Override
	public ResultDO<Coupon> receiveRedFriday(Long memberId, Long activityId) {
		try {
			ResultDO<Coupon> result = new ResultDO<Coupon>();
			// 根据活动id查找活动
			Activity	activity = activityManager.selectByPrimaryKey(activityId);
			if (activity != null) {
				// 判断活动是否已经开始
				if (DateUtils.isDateBetween(DateUtils.getCurrentDate(), activity.getStartTime(),
						activity.getEndTime())) {
					ActivityRule activityRule = activityRuleManager.findRuleByActivityId(activityId);
					List<RedFridayRuleBiz> redFridayRuleBizList = JSONObject.parseArray(activityRule.getRuleParameter(), RedFridayRuleBiz.class);
					if(Collections3.isNotEmpty(redFridayRuleBizList)) {
						boolean isStarted = false;
						boolean isReceived = false;
						Long couponTemplateId = null;
						for (RedFridayRuleBiz redFridayRuleBiz : redFridayRuleBizList) {
							if(DateUtils.isDateBetween(DateUtils.getCurrentDate(), redFridayRuleBiz.getStartTime(), redFridayRuleBiz.getEndTime())) {
								couponTemplateId = redFridayRuleBiz.getCouponTemplateId();
								isStarted = true;
								//判断用户本期是否已经领取
								List<Coupon> coupons = couponManager.getCouponByMemberIdAndCouponTemplateId(memberId, couponTemplateId);
								if(Collections3.isNotEmpty(coupons)
										&& coupons.size()>0) {
									for (Coupon coupon : coupons) {
										if(DateUtils.formatDatetoString(coupon.getReceiveTime(), DateUtils.DATE_FMT_3).equals(
												DateUtils.formatDatetoString(redFridayRuleBiz.getStartTime(), DateUtils.DATE_FMT_3))) {
											isReceived = true;
									}
								}
								}
							}
							
						}
						if(!isStarted) {
							result.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
							return result;
						}
						
						if(isStarted && isReceived) {
							result.setResultCode(ResultCode.ACTIVITY_CURRENT_YET_JOIN_ACTIVITY_ERROR);
		            		return result;
						}
						if(isStarted && !isReceived) {
							//领取优惠券
							Coupon coupon = couponManager.receiveCoupon(memberId, activityId, couponTemplateId, -1L);
							if(coupon != null){
								RedisPlatformClient.addRedFridayCouponNumber();
								MessageClient.sendMsgForRedFriday(memberId);
							}
							result.setResult(coupon);
						}
						
					}
					
				}

			}
		return result;
		} catch (Exception e) {
			logger.error("领取红色星期五活动的现金券发生异常，memberId=" + memberId + ", activityId=" + activityId, e);
		}
		return null;
	}

	@Override
	public int countReceivedCouponByActivityId(Long activityId) {
		try {
			return couponManager.countReceivedCouponByActivityId(activityId);
		} catch (Exception e) {
			logger.error("根据活动id查询被领取的数量失败，activityId="+activityId,e);
		}
		return 0;
	}

	@Override
	public List<ActivityForRedFriday> getReceivedCouponByActivityId(Long activityId) {
		try {
			String key = "redFridayactivity"+activityId;
			boolean isExit = RedisManager.isExitByObjectKey(key);
			if(isExit){
				return (List<ActivityForRedFriday>) RedisManager.getObject(key);
			}else{
				List<ActivityForRedFriday> list = couponManager.getReceivedCouponByActivityId(activityId);
				RedisManager.putObject(key, list);
				RedisManager.expireObject(key, 300);
				return list;
			}
		} catch (Exception e) {
			logger.error("根据活动id查询被领取的列表失败，activityId="+activityId,e);
		}
		return null;
	}

	@Override
	public boolean redFridayIsReceived(Long memberId, Long activityId) {
		try {
		// 根据活动id查找活动
					Activity	activity = activityManager.selectByPrimaryKey(activityId);
					if (activity != null) {
						// 判断活动是否已经开始
						if (DateUtils.isDateBetween(DateUtils.getCurrentDate(), activity.getStartTime(),
								activity.getEndTime())) {
							ActivityRule activityRule = activityRuleManager.findRuleByActivityId(activityId);
							List<RedFridayRuleBiz> redFridayRuleBizList = JSONObject.parseArray(activityRule.getRuleParameter(), RedFridayRuleBiz.class);
							if(Collections3.isNotEmpty(redFridayRuleBizList)) {
								boolean isReceived = false;
								Long couponTemplateId = null;
								for (RedFridayRuleBiz redFridayRuleBiz : redFridayRuleBizList) {
									if(DateUtils.isDateBetween(DateUtils.getCurrentDate(), redFridayRuleBiz.getStartTime(), redFridayRuleBiz.getEndTime())) {
										couponTemplateId = redFridayRuleBiz.getCouponTemplateId();
										//判断用户本期是否已经领取
										List<Coupon> coupons = couponManager.getCouponByMemberIdAndCouponTemplateId(memberId, couponTemplateId);
										if(Collections3.isNotEmpty(coupons)
												&& coupons.size()>0) {
											for (Coupon coupon : coupons) {
												if(DateUtils.formatDatetoString(coupon.getReceiveTime(), DateUtils.DATE_FMT_3).equals(
														DateUtils.formatDatetoString(redFridayRuleBiz.getStartTime(), DateUtils.DATE_FMT_3))) {
													isReceived = true;
											}
										}
										}
									}
									
								}
								if(isReceived) {
				            		return true;
								}
								
							}
							
						}

					}
		} catch (Exception e) {
			logger.error("判断是否参与过本期红色星期五失败，activityId="+activityId,e);
		}
		return false;
	}

	@Override
	public int getMemberActivedCouponCountByType(Long holderId, int couponType) {
		try {
			return couponManager.getMemberActivedCouponCountByType(holderId,couponType);
		} catch (Exception e) {
			logger.error("获取不同优惠券（现金券，收益券）可用数量，memberId={}，couponType={}" , holderId,couponType, e);
		}
		return 0;
	}

	/**
	 * 通过会员id查询被推荐会员总数
	 */
	@Override
	public long getReferralMemberByIdCount(Long memberId) {
		try {
			return memberManager.getReferralMemberByIdCount(memberId);
		} catch (Exception e) {
			logger.error("通过会员id查询被推荐会员总数，memberId={}" , memberId, e);
		}
		return 0;	
	}

	@Override
	public ResultDO receiveBirthday50Coupon(Long memberId, Date birthday) {
		return receiveBirthdayCoupon(memberId, PropertiesUtil.getCoupon50ActvityId(), birthday, "50" + ActivityConstant.couponDesc, 50,
				TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
	}

	@Override
	public ResultDO receiveBirthday001Coupon(Long memberId, Date birthday) {
		return receiveBirthdayCoupon(memberId, PropertiesUtil.getCoupon001ActvityId(), birthday, "1%" + ActivityConstant.annualizedDesc,
				null,
				TypeEnum.ACTIVITY_LOTTERY_TYPE_ANNUALIZED.getType());
	}

	/**
	 * 领取生日优惠券
	 * @param memberId
	 * @param activityId
	 * @param birthday
	 * @return
	 */
	private ResultDO receiveBirthdayCoupon(Long memberId, Long activityId, Date birthday, String rewardName, Integer rewardValue,
			Integer rewardType) {
		ResultDO result = new ResultDO();
		try {
			// 根据活动id查找活动
			Activity activity = activityManager.selectByPrimaryKey(activityId);
			if (activity == null) {
				result.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return result;
			}
			// 判断活动是否已经开始
			if (!DateUtils.isDateBetween(DateUtils.getCurrentDate(),
					activity.getStartTime(), activity.getEndTime())) {
				if (DateUtils.getCurrentDate().before(activity.getStartTime())) {
					result.setResultCode(ResultCode.ACTIVITY_BIRTHDAY_IS_NOT_START);
					return result;
				}
				if (DateUtils.getCurrentDate().after(activity.getEndTime())) {
					result.setResultCode(ResultCode.ACTIVITY_BIRTHDAY_IS_NOT_START);
					return result;
				}
			}
			boolean flag = couponManager.isReceiveBirthdayCoupon(memberId, activityId, birthday);
			if (flag) {
				result.setResultCode(ResultCode.MEMBER_RECEIVED_BIRTHDAY_GIFT);
				return result;
			}
			RewardsBase rBase = new RewardsBase();
			rBase.setRewardType(rewardType);
			rBase.setRewardName(rewardName);
			rBase.setRewardValue(rewardValue);
			rBase.setTemplateId(couponManager.getTemplateIdByActivityId(activityId));
			RuleBody ruleBody = new RuleBody();
			ruleBody.setActivityId(activity.getId());
			ruleBody.setMemberId(memberId);
			ruleBody.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
			ruleBody.setCycleStr(DateUtils.getYear(DateUtils.getCurrentDate()) + "-" + DateUtils.getStrFromDate(birthday, "MM-dd"));
			ruleBody.setActivityName(activity.getActivityName());
			if (drawByPrizeDirectly.validate(ruleBody, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
				drawByPrizeDirectly.drawLottery(ruleBody, rBase, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode());
			} else {
				result.setResultCode(ResultCode.MEMBER_RECEIVED_BIRTHDAY_GIFT);
			}
		} catch (Exception e) {
			logger.error("领取生日优惠券异常", e);
			result.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return result;
	}

	@Override
	public ResultDO<ActivityBiz> midAutumnCoupon(Long memberId, Long activityId) {
		ResultDO<ActivityBiz> result = new ResultDO<ActivityBiz>();
		try {
			String retCode = activityManager.reciveCoupon(memberId, activityId);
			if("-2".equals(retCode)) {
				result.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
			} else if("-1".equals(retCode)) {
				result.setResultCode(ResultCode.ACTIVITY_MIDAUTUMN_NOT_START_ERROR);
			} else if("0".equals(retCode)) {
				result.setResultCode(ResultCode.ACTIVITY_MIDAUTUMN_HAD_JOINED_ERROR);
			} else if("1".equals(retCode)) {
				MessageClient.sendMsgForMidAutumn(memberId);
			} else if("2".equals(retCode)) {
				result.setResultCode(ResultCode.ACTIVITY_MIDAUTUMN_NOCOUPON_ERROR);
			} else {
				result.setResultCode(ResultCode.ACTIVITY_MIDAUTUMN_END_ERROR);
			}
		} catch (Exception e) {
			logger.error("领取中秋活动现金券发生异常，memberId=" + memberId + ", activityId=" + activityId, e);
			result.setResultCode(ResultCode.ERROR);
		}
		return result;
	}

	@Override
	public List<Long> getTemplateids(String code) {
		String idstr= couponTemplateRelationManager.selectByCode(code);
		if (StringUtil.isBlank(idstr)){
			return null;
		}
		List<Long> templateIds=new ArrayList<>();
		String[] ids= idstr.split(",");
		for (String id:ids) {
			if (StringUtil.isNotBlank(id)){
				templateIds.add(Long.parseLong(id));
			}
		}
		return templateIds;
	}

	@Override
	public Long[] getTemplateidsArray(String code) {
		String idstr= couponTemplateRelationManager.selectByCode(code);
		if (StringUtil.isBlank(idstr)){
			return null;
		}
		List<Long> templateIds=new ArrayList<>();
		String[] ids= idstr.split(",");
		for (String id:ids) {
			if (StringUtil.isNotBlank(id)){
				templateIds.add(Long.parseLong(id));
			}
		}
		if (templateIds.size()>0&&templateIds!=null){
			return templateIds.toArray(new Long[templateIds.size()]);
		}
		return null;
	}
	
	
	@Override
	public boolean giveCouponForQuestion(Long memberId, Long couponTemplateId, Long senderId) {
		Boolean results = false;
		try {
			
			Member member = memberManager.selectByPrimaryKey(memberId);
			if (member != null) {
				List<Coupon> coupons = couponManager.getCouponByMemberIdAndCouponTemplateId(memberId, couponTemplateId);
				if(Collections3.isNotEmpty(coupons)){
					return false;
				}
				// 调用赠送优惠券方法,后台赠送标明优惠券发送渠道和优惠券获取方式
				Coupon coupon = couponManager.receiveCouponSource(member.getId(), null, couponTemplateId, senderId,
						TypeEnum.COUPON_WAY_WEB.getType(), TypeEnum.COUPON_ACCESS_SOURCE_ACTIVITY.getType());
				if (coupon != null) {
					results = true;
				}
			}
		} catch (ManagerException e) {
			logger.error("赠送优惠券失败，couponTemplateId={}", couponTemplateId, e);
		}
		return results;
	}
}
