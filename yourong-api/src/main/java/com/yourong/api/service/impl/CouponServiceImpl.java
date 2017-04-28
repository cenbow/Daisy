package com.yourong.api.service.impl;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.yourong.core.mc.manager.CouponTemplateRelationManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import rop.thirdparty.com.google.common.collect.Maps;

import com.yourong.api.dto.CouponDto;
import com.yourong.api.dto.CouponTemplateDto;
import com.yourong.api.dto.ResultDTO;
import com.yourong.api.service.CouponService;
import com.yourong.api.utils.APIPropertiesUtil;
import com.yourong.common.cache.RedisPlatformClient;
import com.yourong.common.constant.Config;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.CouponEnum;
import com.yourong.common.enums.RemarksEnum;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.core.common.MessageClient;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.PopularityInOutLogManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.mc.manager.ActivityManager;
import com.yourong.core.mc.manager.CouponManager;
import com.yourong.core.mc.manager.CouponTemplateManager;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.mc.model.Coupon;
import com.yourong.core.mc.model.CouponTemplate;
import com.yourong.core.mc.model.query.CouponQuery;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.Member;

@Service
public class CouponServiceImpl implements CouponService {
	private static Logger logger = LoggerFactory
			.getLogger(CouponServiceImpl.class);

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
	/**
	 * 分页获取优惠券列表
	 */
	@Override
	public Page<CouponDto> queryCoupons(CouponQuery couponQuery) {
		Page<CouponDto> page = new Page<CouponDto>();
		try {
			if(couponQuery.getCouponType() != null){
				Page<Coupon> data = couponManager.findFrontCouponsByPage(couponQuery);
				if(data != null && Collections3.isNotEmpty(data.getData())){
					List<CouponDto> dto = BeanCopyUtil.mapList(data.getData(), CouponDto.class);
					if(Collections3.isNotEmpty(dto)){
						for(CouponDto biz:dto){
							if(biz.getExtraInterestType()==1){
								biz.setExtraName("自起息日起，加息"+biz.getExtraInterestDay()+"天");
							}
						}
					}
					page.setData(dto);
					page.setiDisplayLength(data.getiDisplayLength());
					page.setiDisplayStart(data.getiDisplayStart());
					page.setiTotalRecords(data.getiTotalRecords());
					page.setPageNo(data.getPageNo());
				}		
			}
		} catch (ManagerException e) {
			logger.error("前台分页获取优惠券列表失败,couponQuery=" + couponQuery.toString(), e);
		}
		return page;
	}
	
	/**
	 * 分页获取优惠券列表
	 */
	@Override
	public Map<String,Object> queryCouponsHistory(CouponQuery couponQuery) {
		Map<String,Object> map = Maps.newHashMap();
		Page<CouponDto> page = new Page<CouponDto>();
		try {
			if(couponQuery.getCouponType() != null){
				Page<Coupon> data = couponManager.findFrontCouponsByPage(couponQuery);
				if(data != null && Collections3.isNotEmpty(data.getData())){
					List<CouponDto> dto = BeanCopyUtil.mapList(data.getData(), CouponDto.class);
					if(Collections3.isNotEmpty(dto)){
						for(CouponDto biz:dto){
							if(biz.getExtraInterestType()==1){
								biz.setExtraName("加息"+biz.getExtraInterestDay()+"天");
							}
							biz.setRemainDay(0);
							if(biz.getVaildCalcType()==1||biz.getVaildCalcType()==2){
								if(biz.getStatus()==1||biz.getStatus()==5){
									biz.setRemainDay(DateUtils.daysBetween(DateUtils.getCurrentDate(), biz.getEndDate())+1);
								}
							}
						}
					}
					page.setData(dto);
					page.setiDisplayLength(data.getiDisplayLength());
					page.setiDisplayStart(data.getiDisplayStart());
					page.setiTotalRecords(data.getiTotalRecords());
					page.setPageNo(data.getPageNo());
				}
				map.put("page", page);
				boolean history = false;
				couponQuery.setStatus(2);
				Page<Coupon> historyData = couponManager.findFrontCouponsByPage(couponQuery);
				if(historyData != null && Collections3.isNotEmpty(historyData.getData())){
					history = true;
				}
				map.put("history", history);
			}
		} catch (ManagerException e) {
			logger.error("前台分页获取优惠券列表失败,couponQuery=" + couponQuery.toString(), e);
		}
		
		return map;
	}

	/**
	 * 根据状态获取优惠券总额
	 */
	@Override
	public BigDecimal findTotalAmountByStatus(CouponQuery couponQuery) {
		try {
			Coupon coupon = couponManager.findTotalAmountByStatus(couponQuery);
			if(coupon!=null){
				return coupon.getAmount();
			}
		} catch (Exception e) {
			logger.error("根据状态获取优惠券总额，couponQuery=" + couponQuery, e);
		}
		return BigDecimal.ZERO;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDTO exchangeCoupon(Long memberId,
			Long couponTemplateId, int num,Integer source) throws Exception {
		ResultDTO result = new ResultDTO();
		try {
			if(couponTemplateId==null) {
				result.setResultCode(ResultCode.COUPONTEMPLATE_ID_IS_NULL_ERROR);
				return result;
			}
			CouponTemplate couponTemplate = couponTemplateManager.selectByPrimaryKey(couponTemplateId);
			if(couponTemplate==null) {
				result.setResultCode(ResultCode.COUPONTEMPLATE_NOT_EXIST_ERROR);
				return result;
			}
			// 判断用户是否有足够的人气值
			Balance popularityBalance = balanceManager.queryBalanceLocked(memberId, TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY);
			BigDecimal exchangePopularityValue = BigDecimal.ZERO;
			String remark = "";
			TypeEnum couponType = TypeEnum.FIN_POPULARITY_TYPE_EXCHANGE;
			if(couponTemplate.getCouponType().intValue()==TypeEnum.COUPON_TYPE_CASH.getType()){
				exchangePopularityValue = couponTemplate.getAmount().multiply(new BigDecimal(num));
				remark = MessageFormat.format(RemarksEnum.COUPON_EXCHANGE_POPULARITY_BALANCE.getRemarks(), couponTemplate.getAmount());
			}else if(couponTemplate.getCouponType().intValue()==TypeEnum.COUPON_TYPE_INCOME.getType()){
				remark = MessageFormat.format(RemarksEnum.COUPON_PROFIT_EXCHANGE_POPULARITY_BALANCE.getRemarks(), couponTemplate.getAmount());
				if (couponTemplate.getAmount().compareTo(new BigDecimal(0.5)) == 0) {
					couponTemplate.setExchangeAmount(new BigDecimal(APIPropertiesUtil.getProperties("business.exchangeProfitCouponAmount0.5")));
				}
				if (couponTemplate.getAmount().compareTo(new BigDecimal(1)) == 0) {
					couponTemplate.setExchangeAmount(new BigDecimal(APIPropertiesUtil
							.getProperties("business.exchangeProfitCouponAmount1")));
				}
				if (couponTemplate.getAmount().compareTo(new BigDecimal(1.5)) == 0) {
					couponTemplate.setExchangeAmount(new BigDecimal(APIPropertiesUtil
							.getProperties("business.exchangeProfitCouponAmount1.5")));
				}
				if (couponTemplate.getAmount().compareTo(new BigDecimal(2)) == 0) {
					couponTemplate.setExchangeAmount(new BigDecimal(APIPropertiesUtil
							.getProperties("business.exchangeProfitCouponAmount2")));
				}
				exchangePopularityValue = couponTemplate.getExchangeAmount().multiply(new BigDecimal(num));
				couponType = TypeEnum.FIN_POPULARITY_TYPE_PROFIT_EXCHANGE;
			}
			if (popularityBalance.getAvailableBalance().doubleValue() < exchangePopularityValue.doubleValue()) {
				result.setResultCode(ResultCode.COUPON_EXCHANGE_POPULARITY_NOT_ENOUGH_ERROR);
				return result;
			}
			// 通过模板id领取优惠券
			for (int i = 0; i < num; i++) {
				//couponManager.receiveCoupon(memberId, null, couponTemplateId, -1L);// -1:系统调用
				couponManager.receiveCouponSource(memberId, null, couponTemplateId, -1L,
						source,TypeEnum.COUPON_ACCESS_SOURCE_POPULARITY.getType());// -1:系统调用 web发送，人气值兑换
			}
			//调用扣减人气值方法
			popularityInOutLogManager.rechargeReducePopularity(couponTemplateId, memberId, couponType, exchangePopularityValue, remark);
			result.setIsSuccess();
			MessageClient.sendMsgForExchangePopularity(DateUtils.getCurrentDate(), exchangePopularityValue.intValue(), couponTemplate.getAmount(), memberId, couponTemplate.getCouponType());
		} catch (Exception e) {
			throw e;
		}
		return result;
	}

	@Override
	public Coupon getCouponByMemberIdAndActivity(Long memberId, long activityId) {
		try {
			List<Coupon> coupons = couponManager.getCouponByMemberIdAndActivity(memberId, activityId);
			if(Collections3.isNotEmpty(coupons)) {
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
		if(activity==null) {
			result.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
			return result;
		}
		//判断活动是否已经开始
		if(!DateUtils.isDateBetween(DateUtils.getCurrentDate(), activity.getStartTime(), activity.getEndTime())) {
			if(DateUtils.getCurrentDate().before(activity.getStartTime())) {
				result.setResultCode(ResultCode.ACTIVITY_NOT_START_ERROR);
				return result;
			}
			if(DateUtils.getCurrentDate().after(activity.getEndTime())) {
				result.setResultCode(ResultCode.ACTIVITY_YET_END_ERROR);
				return result;
			}
		}
		//判断用户是否已经领取
		List<Coupon> coupons = couponManager.getCouponByMemberIdAndActivity(memberId, activityId);
		if(Collections3.isNotEmpty(coupons)
				&& coupons.size()>0) {
			result.setResultCode(ResultCode.ACTIVITY_YET_JOIN_ACTIVITY_ERROR);
			return result;
		}
		Coupon coupon = couponManager.receiveCoupon(memberId, activityId, null, -1L);
		//TODO 将已经赠送的会员放到redis里面
		if(coupon!=null) {
			Member member = memberManager.selectByPrimaryKey(memberId);
			RedisPlatformClient.addReceiveGiftMoneyMember(
					memberId, 
					StringUtil.maskUserNameOrMobile(member.getUsername(), member.getMobile()),
					StringUtil.getFilePath(member.getAvatars(), "40x40")
					);
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
                //根据活动id查找活动
                Activity act = activityManager.selectByPrimaryKey(activityId);
                if(act==null) {
	       			result.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
	       			return result;
       			}
            	//判断当前是否在活动期间
            	int compare2StartDate = DateUtils.compareDate(currentDate, act.getStartTime());
            	int compare2EndDate = DateUtils.compareDate(currentDate, act.getEndTime());
            	if( compare2StartDate==DateUtils.AFTER ){
            		result.setResultCode(ResultCode.ACTIVITY_XIAOMING_STORY_NOT_START_ERROR );
            		return result;
            	}
            	if( compare2EndDate==DateUtils.BEFORE ){
            		result.setResultCode(ResultCode.ACTIVITY_XIAOMING_STORY_YET_END_ERROR);
            		return result;
            	}
            	//判断该活动是否已领取优惠券
            	List<Coupon> coupon = couponManager.getCouponByMemberIdAndActivity(memberId , activityId);
            	if(Collections3.isNotEmpty(coupon)){
            		result.setResultCode(ResultCode.ACTIVITY_XIAOMING_STORY_YET_JOIN_ACTIVITY_ERROR);
            		return result;
            	}
            	//判断用户是否满投资额
            	//如果没有这个条件会返回true
            	 boolean moreOrEqualInvestAmount = couponManager.moreOrEqualInvestTotalAmount(memberId, activityId);
            	 if(!moreOrEqualInvestAmount){
            		 result.setResultCode(ResultCode.ACTIVITY_NOT_MEET_INVEST_CONDITIONS_ERROR);
            		 return result;
            	 }
                 //领取优惠券
                 Coupon c = couponManager.receiveCoupon( memberId, activityId, null, -1L); //系统发送优惠券
                 if(c==null){
                	 result.setResultCode(ResultCode.COUPON_INCOME_RECEIVE_ERROR);
                 }
          } catch (ManagerException e) {
                logger.error( "领用收益券失败，memberId ="+memberId +"acitityId = " + activityId ,e );
                result.setResultCode(ResultCode.COUPON_INCOME_RECEIVE_ERROR);
          }
          return result;
    }
    
    @Override
	public List<Coupon> getCouponsByCouponTemplateId(Long activityId) {
		try {
			Long templateId = couponManager.getTemplateIdByActivityId(activityId);
			if(templateId!=null){
				int showNum = 28;
				return couponManager.getCouponsByCouponTemplateId(templateId, showNum);
			}
		} catch (Exception e) {
			logger.error("根据活动Id获取几条领取过的优惠券失败，activityId="+activityId,e);
		}
		return null;
	}

	@Override
	public Page<CouponTemplateDto> findExchangeCouponsByIds(Long[] ids) {
		Page<CouponTemplateDto> page = new Page<CouponTemplateDto>();
		try {
			List<CouponTemplate> coupons = couponTemplateManager.findExchangeCouponsByIds(ids);
			if(Collections3.isNotEmpty(coupons)){
				List<CouponTemplateDto> couponTemplateList = BeanCopyUtil.mapList(coupons, CouponTemplateDto.class);
				page.setData(couponTemplateList);
				page.setiDisplayLength(couponTemplateList.size());
				page.setiDisplayStart(1);
				page.setiTotalRecords(couponTemplateList.size());
				page.setPageNo(1);
			}
		} catch (Exception e) {
			logger.error("根据id获取用于兑换人气值的优惠券列表失败，ids=" + ids, e);
		}
		return page;
	}
    
	@Override
	public ResultDTO receiveBirthday50Coupon(Long memberId, Date birthday) {
		return receiveBirthdayCoupon(memberId, PropertiesUtil.getCoupon50ActvityId(), birthday);
	}
	
	@Override
	public ResultDTO receiveBirthday001Coupon(Long memberId, Date birthday) {
		return receiveBirthdayCoupon(memberId, PropertiesUtil.getCoupon001ActvityId(), birthday);
	}
	
	/**
	 * 领取生日优惠券
	 * @param memberId
	 * @param activityId
	 * @param birthday
	 * @return
	 */
	private ResultDTO receiveBirthdayCoupon(Long memberId, Long activityId, Date birthday) {
		ResultDTO result = new ResultDTO();
		result.setIsSuccess();
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
			Coupon coupon = couponManager.receiveCoupon(memberId, activityId,
					null, -1L);
			if (coupon == null) {
				result.setResultCode(ResultCode.COUPON_INCOME_RECEIVE_ERROR);
				return result;
			}
		} catch (ManagerException e) {
			logger.error("领取生日优惠券异常", e);
			result.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return result;
	}

	@Override
	public int getMemberCouponCountFilterP2p(Long memberId) {
		try {
			return couponManager.getMemberCouponCountFilterP2p(memberId);
		} catch (Exception e) {
			logger.error("获取优惠券数量，memberId=" + memberId, e);
		}
		
		return 0;
	}
	
	@Override
	public CouponTemplate getkaiTongCunQianGuanCouponTemplate()  {
		CouponTemplate couponTemplate= new CouponTemplate();
		try {
			Long templateId = couponManager.getTemplateIdByActivityId2(PropertiesUtil.getActivityVerifyTrueNameId());
			couponTemplate =  couponTemplateManager.selectByPrimaryKey(templateId);
		} catch (Exception e) {
			logger.error("获取开通存钱罐优惠券模板" , e);
		}
		return couponTemplate;
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
}
