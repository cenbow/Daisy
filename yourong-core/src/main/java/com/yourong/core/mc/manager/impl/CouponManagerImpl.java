package com.yourong.core.mc.manager.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.model.Project;
import com.yourong.core.mc.model.biz.CouponTemplateSMSBiz;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yourong.common.cache.RedisManager;
import com.yourong.common.constant.Config;
import com.yourong.common.constant.Constant;
import com.yourong.common.constant.RedisConstant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.CouponEnum;
import com.yourong.common.enums.RemarksEnum;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.StringUtil;
import com.yourong.core.mc.dao.ActivityRuleMapper;
import com.yourong.core.mc.dao.CouponMapper;
import com.yourong.core.mc.dao.CouponTemplateMapper;
import com.yourong.core.mc.dao.CouponTemplatePrintMapper;
import com.yourong.core.mc.manager.ActivityManager;
import com.yourong.core.mc.manager.CouponManager;
import com.yourong.core.mc.model.ActivityRule;
import com.yourong.core.mc.model.Coupon;
import com.yourong.core.mc.model.CouponTemplate;
import com.yourong.core.mc.model.CouponTemplatePrint;
import com.yourong.core.mc.model.biz.ActivityBiz;
import com.yourong.core.mc.model.biz.ActivityForRedFriday;
import com.yourong.core.mc.model.biz.CouponReceiveBiz;
import com.yourong.core.mc.model.query.CouponQuery;
import com.yourong.core.sales.SPGift;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.Order;
import com.yourong.core.uc.dao.MemberMapper;
import com.yourong.core.uc.model.Member;

@Component
public class CouponManagerImpl implements CouponManager {

	private static Logger logger = LoggerFactory.getLogger(CouponManagerImpl.class);
	@Autowired
	private CouponMapper couponMapper;

	@Autowired
	private ActivityRuleMapper activityRuleMapper;

	@Autowired
	private CouponTemplateMapper couponTemplateMapper;

	@Autowired
	private CouponTemplatePrintMapper couponTemplatePrintMapper;
	
	@Autowired
	private MemberMapper memberMapper;
	
	@Autowired
	private TransactionManager transactionManager;
	
	@Autowired
	private ActivityManager activityManager;
	
	@Autowired
	private SysDictManager sysDictManager;
	
	@Autowired
	private ProjectManager projectManager;

	public Integer deleteByPrimaryKey(Long id) throws ManagerException {
		try {
			int result = couponMapper.deleteByPrimaryKey(id);
			return result;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public Integer insert(Coupon coupon) throws ManagerException {
		try {
			int result = couponMapper.insert(coupon);
			return result;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public Coupon selectByPrimaryKey(Long id) throws ManagerException {
		try {
			return couponMapper.selectByPrimaryKey(id);

		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public Integer updateByPrimaryKey(Coupon coupon) throws ManagerException {
		try {

			return couponMapper.updateByPrimaryKey(coupon);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public Integer updateByPrimaryKeySelective(Coupon coupon) throws ManagerException {
		try {

			return couponMapper.updateByPrimaryKeySelective(coupon);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public Page<Coupon> findByPage( Page<Coupon> pageRequest,Map<String, Object> map) throws ManagerException {
		try {
			
			map.put(Constant.STARTROW, pageRequest.getiDisplayStart());
			map.put(Constant.PAGESIZE, pageRequest.getiDisplayLength());
			List<Coupon> couponList = couponMapper.findByPage( map);
			int totalCount = couponMapper.selectForPaginTotalCount(map);
			
			for(Coupon coupon : couponList)
			{
				Member member = memberMapper.selectByPrimaryKey(coupon.getHolderId());
				CouponTemplate couponTemplate= couponTemplateMapper.selectByPrimaryKey(coupon.getCouponTemplateId());
				coupon.setMember(member);
				coupon.setCouponTemplateName(couponTemplate.getName());
			}
			pageRequest.setiTotalDisplayRecords(totalCount);
			pageRequest.setiTotalRecords(totalCount);
			pageRequest.setData(couponList);
			
			return pageRequest;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Coupon getCouponByCouponNo(String couponNo) throws ManagerException {
		try {
			return couponMapper.getCouponByCouponNo(couponNo);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Integer batchInsertCoupon(List<Coupon> coupons) throws ManagerException {
		try {
			return couponMapper.batchInsertCoupon(coupons);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 定时任务把过期的优惠券状态设置为已过期
	 */
	@Override
	public Integer expireCouponTask() throws ManagerException {
		try {
			int unReceivedNum = couponMapper.expireUnReceivedCouponTask();
			int receivedNum = couponMapper.expireReceivedCouponTask();
			return unReceivedNum + receivedNum;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Coupon receiveCoupon(Long userId, Long activityId, Long couponTemplateId, Long senderId) throws ManagerException {
		return this.receiveCouponSource(userId, activityId, couponTemplateId, senderId, null,null);
	}
	/**
	 * 领取优惠券,支持领取渠道
	 */
	@Override
	public Coupon receiveCouponSource(Long userId, Long activityId, Long couponTemplateId, Long senderId, Integer way,Integer source) throws ManagerException {
		try {
			// 获取优惠券模板id
			if (couponTemplateId == null) {
				couponTemplateId = getTemplateIdByActivityId(activityId);
			}
			Coupon coupon = couponMapper.findUnreceviedCouponByTemplateId(couponTemplateId);
			if (coupon != null) {
				coupon.setActivityId(activityId);
				coupon.setHolderId(userId);
				coupon.setSenderId(senderId);
				coupon.setWay(way);
				coupon.setAccessSource(source);
				int receive = 0;
				if (CouponEnum.COUPONTEMPLATE_VAILD_CALC_TYPE_DAYS.getCode().equals(coupon.getVaildCalcType().toString())) {
					receive = couponMapper.receiveDaysCoupon(coupon);// 领用“按领取后天数计算”的优惠券
				} else {
					receive = couponMapper.receiveForeverAndTimezoonCoupon(coupon);// 领用“按时间计算”和“永久”的优惠券
				}
				if (receive > 0) {
					// 更新印刷表中已领用数量
					int updateRes = updatePrintReceiveNum(coupon);
					if (updateRes > 0) {
						return coupon;
					}
				} else {
					receviceCouponPushToRedis(userId, activityId, couponTemplateId, senderId);
					coupon = new Coupon();
					coupon.setCouponTemplateId(couponTemplateId);
					coupon.setHolderId(userId);
					coupon.setSenderId(senderId);
					coupon.setActivityId(activityId);
				}
				return coupon;
			}
			return null;
		} catch (Exception e) {
			logger.error("领用优惠券失败，userId=" + userId + "activityId=" + activityId, e);
			throw new ManagerException(e);
		}
	}

	/**
	 * 根据活动ID获取优惠券模板ID
	 * 
	 * @param activityId
	 * @return
	 * @throws ManagerException
	 */
	public Long getTemplateIdByActivityId(Long activityId) throws ManagerException {
		Long couponTemplateId = null;
		try {
			ActivityRule activityRule = activityRuleMapper.findRuleByActivityId(activityId);
			// 获取活动对应优惠券模板
			if (activityRule != null && StringUtil.isNotBlank(activityRule.getRuleParameter())) {
				Map<String, Object> ruleMap = JSON.parseObject(activityRule.getRuleParameter(), new TypeReference<Map<String, Object>>() {
				});
				couponTemplateId = Long.valueOf((String) ruleMap.get("couponTemplateId"));
				return couponTemplateId;
			}
		} catch (Exception e) {
			logger.error("根据活动ID获取优惠券模板失败，activityId = " + activityId, e);
			throw new ManagerException(e);
		}
		return couponTemplateId;
	}

	/**
	 * 根据活动ID获取优惠券模板ID 解析方式不同
	 * 
	 * @param activityId
	 * @return
	 * @throws ManagerException
	 */
	public Long getTemplateIdByActivityId2(Long activityId) throws ManagerException {
		Long couponTemplateId = null;
		try {
			ActivityBiz activityBiz = activityManager.findActivityById(activityId);
			// 获取活动对应优惠券模板
			if (activityBiz != null && StringUtil.isNotBlank(activityBiz.getRuleParameterJson())) {
				List<SPGift> giftList = JSONObject.parseArray(activityBiz.getRuleParameterJson(), SPGift.class);
				couponTemplateId = giftList.get(0).getTemplateId();
				return couponTemplateId;
			}
		} catch (Exception e) {
			logger.error("根据活动ID获取优惠券模板失败，activityId = " + activityId, e);
			throw new ManagerException(e);
		}
		return couponTemplateId;
	}

	
	
	/**
	 * 根据活动ID获取投资总额条件
	 * 
	 * @param activityId
	 * @return
	 * @throws ManagerException
	 */
	private BigDecimal getInvestTotalAmountByAcitity(Long activityId) throws ManagerException {
		BigDecimal investTotalAmount = BigDecimal.ZERO;
		try {
			ActivityRule activityRule = activityRuleMapper.findRuleByActivityId(activityId);
			// 获取活动对应优惠券模板
			if (activityRule != null && StringUtil.isNotBlank(activityRule.getRuleParameter())) {
				Map<String, Object> ruleMap = JSON.parseObject(activityRule.getRuleParameter(), new TypeReference<Map<String, Object>>() {
				});
				investTotalAmount = new BigDecimal((String) ruleMap.get("investTotalAmount"));
				return investTotalAmount;
			}
		} catch (Exception e) {
			logger.error("根据活动ID获取投资总额条件，activityId = " + activityId, e);
			throw new ManagerException(e);
		}
		return investTotalAmount;
	}

	/**
	 * 更新优惠券印刷表的领用数量
	 * 
	 * @param coupon
	 * @return
	 * @throws ManagerException
	 */
	private int updatePrintReceiveNum(Coupon coupon) throws ManagerException {
		int updateRes = 0;
		try {
			CouponTemplatePrint print = couponTemplatePrintMapper.selectByPrimaryKey(coupon.getCouponTemplatePrintId());
			if (print != null) {
				updateRes = couponTemplatePrintMapper.updateCouponReceiveNum(coupon.getCouponTemplatePrintId());
			}
		} catch (Exception e) {
			logger.error("更新印刷表的领用数量失败，coupon = " + coupon, e);
			throw new ManagerException(e);
		}
		return updateRes;

	}

	/**
	 * 使用优惠券
	 */
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer useCoupon(String couponNo, Long projectId, Long transactionId) throws ManagerException {
		int result = 0;
		try {
			Coupon coupon = couponMapper.getCouponByCouponNo(couponNo);
			if (coupon == null) {
				return result;
			}
			// 判断优惠券是否可使用状态
			if (StatusEnum.COUPON_STATUS_RECEIVED_NOT_USED.getStatus() != coupon.getStatus().intValue()
					&& StatusEnum.COUPON_STATUS_USING.getStatus() != coupon.getStatus().intValue()) {
				return result;
			}
			Map<String, Object> map = Maps.newHashMap();
			map.put("couponNo", couponNo);
			map.put("projectId", projectId);
			map.put("transactionId", transactionId);
			int couponResult = couponMapper.useCoupon(map);
			if (couponResult > 0) {
				Long printId = couponMapper.findPrintIdByCouponCode(couponNo);
				result = couponMapper.updateUsedNumForCouponTemplatePrint(printId);
			}
		} catch (Exception e) {
			throw new ManagerException(e);
		}
		return result;
	}

	/**
	 * 获取会员可用优惠券
	 */
	@Override
	public List<Coupon> findUsableCouponsByMemberId(Long memberId, Integer couponType) throws ManagerException {
		try {
			Map<String, Object> map = Maps.newHashMap();
			map.put("holderId", memberId);
			map.put("couponType", couponType);
			List<Coupon> coupons = couponMapper.findUsableCouponsByMemberId(map);
			return coupons;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 前台分页获取优惠券
	 */
	@Override
	public Page<Coupon> findFrontCouponsByPage(CouponQuery couponQuery) throws ManagerException {
		try {
			List<Coupon> coupons = Lists.newArrayList();
			int count = couponMapper.selectForPaginTotalCountFront(couponQuery);
			if (couponQuery.getPageSize() <= 0) {
				couponQuery.setPageSize(count);
			}
			coupons = couponMapper.selectForPaginFront(couponQuery);
			Page<Coupon> page = new Page<Coupon>();
			page.setData(coupons);
			// 每页总数
			page.setiDisplayLength(couponQuery.getPageSize());
			// 当前页
			page.setPageNo(couponQuery.getCurrentPage());
			// 总数
			page.setiTotalDisplayRecords(count);
			page.setiTotalRecords(count);
			return page;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 根据状态获取优惠券总额
	 */
	@Override
	public Coupon findTotalAmountByStatus(CouponQuery couponQuery) throws ManagerException {
		try {
			return couponMapper.findTotalAmountByStatus(couponQuery);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Coupon sendCoupon(Long memberId, TypeEnum sourceType, Long couponTemplateId) throws ManagerException {
		try {
			Long activityId = 0L;
			switch (sourceType) {
			case COUPON_SEND_SOURCE_TYPE_EMAIL_BIND:
				activityId = Long.parseLong(Config.activityIdForEmailBind);
				break;
			case COUPON_SEND_SOURCE_TYPE_IDENTITY_BIND:
				activityId = Long.parseLong(Config.activityIdForIdentityBind);
				break;
			case COUPON_SEND_SOURCE_TYPE_RECOMMEND_INVESTMENT:
				activityId = Long.parseLong(Config.activityIdForRecommendInvestment);
				break;
			case COUPON_SEND_SOURCE_TYPE_RECOMMEND_REGISTER:
				activityId = Long.parseLong(Config.activityIdForRecommendRegister);
				break;
			case COUPON_SEND_SOURCE_TYPE_COMPLETE_MEMBER_INFO:
				activityId = Long.parseLong(Config.activityIdForCompleteMemberInfo);
				break;

			default:
				break;
			}
			return this.receiveCoupon(memberId, activityId, couponTemplateId, -1L);// -1表示系统赠送优惠券
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<Coupon> getCouponByMemberIdAndActivity(Long memberId, long activityId) throws ManagerException {
		try {
			Long couponTemplateId = getTemplateIdByActivityId(activityId);
			return couponMapper.getCouponByMemberIdAndCouponTemplateId(memberId, couponTemplateId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<Coupon> getCouponByMemberIdAndCouponTemplateId(Long memberId, Long couponTemplateId) throws ManagerException {
		try {
			return couponMapper.getCouponByMemberIdAndCouponTemplateId(memberId, couponTemplateId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int getMemberCouponCount(Long memberId) throws ManagerException {
		try {
			return couponMapper.getMemberCouponCount(memberId, StatusEnum.COUPON_STATUS_RECEIVED_NOT_USED.getStatus());
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public boolean lockCoupon(String couponNo) throws ManagerException {
		try {
			if (StringUtil.isNotBlank(couponNo)) {
				Coupon coupon = this.getCouponByCouponNo(couponNo);
				if (coupon != null) {
					// 锁定现金券
					Coupon couponForLock = this.selectCouponforUpdate(coupon.getId());
					if(couponForLock.getStatus()==StatusEnum.COUPON_STATUS_RECEIVED_NOT_USED.getStatus()) {
						couponForLock.setStatus(StatusEnum.COUPON_STATUS_USING.getStatus());
						couponForLock.setRemarks(RemarksEnum.COUPON_LOCK_BY_COLLECT_TRADE_SUCCESS.getRemarks());
						if (this.updateByPrimaryKeySelective(couponForLock) > 0) {
							return true;
						}
					}
					
				}
			}
		} catch (Exception e) {
			throw new ManagerException(e);
		}
		return false;
	}

	@Override
	public boolean unLockCoupon(String couponNo) throws ManagerException {
		try {
			if (StringUtil.isNotBlank(couponNo)) {
				Coupon coupon = this.getCouponByCouponNo(couponNo);
				
				if (coupon != null) {
					// 解锁现金券
					Coupon couponForLock = this.selectCouponforUpdate(coupon.getId());
					couponForLock.setStatus(StatusEnum.COUPON_STATUS_RECEIVED_NOT_USED.getStatus());
					couponForLock.setRemarks(RemarksEnum.COUPON_UNLOCK_BY_COLLECT_TRADE_FAILED.getRemarks());
					if (this.updateByPrimaryKeySelective(couponForLock) > 0) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			throw new ManagerException(e);
		}
		return false;
	}

	@Override
	public int getMemberCouponCountByType(Long memberId, Integer couponType) throws ManagerException {
		try {
			return couponMapper.getMemberCouponCountByType(memberId, couponType);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public boolean moreOrEqualInvestTotalAmount(Long memberId, Long activityId) throws ManagerException {
		try {
			// 获取条件投资总额
			BigDecimal investTotalAmount = getInvestTotalAmountByAcitity(activityId);
			if (investTotalAmount.compareTo(BigDecimal.ZERO) == 0) {
				return true;// 当没有投资总额限制时，返回true
			}
			if (investTotalAmount.compareTo(BigDecimal.ZERO) > 0) {
				// 获取用户的投资总额
				BigDecimal memebrInvestAmount = transactionManager.getTotalInvestAmountByMemberId(memberId);
				// 为投资的不满足条件
				if (memebrInvestAmount == null) {
					return false;
				}
				if (memebrInvestAmount.compareTo(investTotalAmount) >= 0) {
					return true;
				}
			}
		} catch (Exception e) {
			throw new ManagerException(e);
		}
		return false;
	}

	@Override
	public List<Coupon> getCouponsByCouponTemplateId(Long couponTemplateId, int showNum) throws ManagerException {
		try {
			return couponMapper.getCouponsByCouponTemplateId(couponTemplateId, showNum);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int getCouponTotalCount(Long memberId, Integer couponType, Integer status) throws ManagerException {
		try {
			CouponQuery query = new CouponQuery();
			query.setMemberId(memberId);
			query.setCouponType(couponType);
			query.setStatus(status);
			return couponMapper.selectForPaginTotalCountFront(query);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<Coupon> getExpiringCoupons() throws ManagerException {
		try {
			return couponMapper.getExpiringCoupons();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int countReceivedCouponByActivityId(Long activityId) throws ManagerException {
		try {
			return couponMapper.countReceivedCouponByActivityId(activityId);

		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<ActivityForRedFriday> getReceivedCouponByActivityId(Long activityId) throws ManagerException {
		try {
			return couponMapper.getReceivedCouponByActivityId(activityId);

		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 领取优惠券 方法二 通过队列
	 */
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Coupon receiveCouponByRedis(CouponReceiveBiz couponReceiveBiz) throws ManagerException {
		try {
			Long couponTemplateId = couponReceiveBiz.getCouponTemplateId();
			Long activityId = couponReceiveBiz.getActivityId();
			Long userId = couponReceiveBiz.getMemberId();
			Long senderId = couponReceiveBiz.getSenderId();
			int isRepeat = couponReceiveBiz.getIsRepeat();
			// 获取优惠券模板id
			if (couponTemplateId == null) {
				couponTemplateId = getTemplateIdByActivityId(activityId);
			}
			if (isRepeat == 0) {// 不能重复
				Integer isExist = couponMapper.selectByMemberIdAndActivityId(activityId, userId, couponTemplateId);
				if (Integer.valueOf(1).equals(isExist)) {
					return null;// 已经存在，则不能重复领取
				}
			}
			Coupon coupon = couponMapper.findUnreceviedCouponByTemplateId(couponTemplateId);
			if (coupon != null) {
				coupon.setActivityId(activityId);
				coupon.setHolderId(userId);
				coupon.setSenderId(senderId);
				int receive = 0;
				if (CouponEnum.COUPONTEMPLATE_VAILD_CALC_TYPE_DAYS.getCode().equals(coupon.getVaildCalcType().toString())) {
					receive = couponMapper.receiveDaysCoupon(coupon);// 领用“按领取后天数计算”的优惠券
				} else {
					receive = couponMapper.receiveForeverAndTimezoonCoupon(coupon);// 领用“按时间计算”和“永久”的优惠券
				}
				if (receive > 0) {
					// 更新印刷表中已领用数量
					int updateRes = updatePrintReceiveNum(coupon);
					if (updateRes > 0) {
						return coupon;
					}
				}
			}
		} catch (Exception e) {
			logger.error("领用优惠券失败，couponReceiveBiz={}", couponReceiveBiz, e);
			throw new ManagerException(e);
		}
		return null;

	}

	public void receviceCouponPushToRedis(Long userId, Long activityId, Long couponTemplateId, Long senderId) throws ManagerException {
		CouponReceiveBiz receiveBiz = new CouponReceiveBiz();
		receiveBiz.setActivityId(activityId);
		receiveBiz.setMemberId(userId);
		receiveBiz.setCouponTemplateId(couponTemplateId);
		receiveBiz.setSenderId(senderId);
		String s = JSON.toJSONString(receiveBiz);
		logger.info("优惠劵领取失败，进行补偿机制,详细信息 :{}", s);
		RedisManager.rpush(RedisConstant.REDIS_KEY_ReceiveCoupon, s);
	}

	@Override
	public List<Coupon> getUsableAndLimitedCoupons(Long memberId, Integer couponType, String client, BigDecimal amountScope,
			Integer daysScope) throws ManagerException {
		try {
			StopWatch watch = new StopWatch();
			watch.start();
			Map<String, Object> map = Maps.newHashMap();
			map.put("holderId", memberId);
			map.put("couponType", couponType);
			List<Coupon> coupons = couponMapper.findUsableCouponsByMemberId(map);
			List<Coupon> couponLimited = Lists.newArrayList();
			for (Coupon coupon : coupons) {
				// 是否满足使用时间
				if (DateUtils.compareDate(coupon.getStartDate(), DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3)) == DateUtils.BEFORE) {
					coupon.setLimited(StatusEnum.COUPON_USE_LIMITED.getStatus());
					coupon.setShowCouponValidity(false);
					couponLimited.add(coupon);
					continue;
				}
				// 是否为有效的客户端
				if (CouponEnum.COUPON_CLIENT_WEB.getCode().equals(client)) {
					if (StatusEnum.COUPON_CLIENT_NOT_SUPPORT.getStatus() == coupon.getWebScope().intValue()) {
						coupon.setLimited(StatusEnum.COUPON_USE_LIMITED.getStatus());
						coupon.setShowClient(false);
						couponLimited.add(coupon);
						continue;
					}
				}
				if (CouponEnum.COUPON_CLIENT_WAP.getCode().equals(client)) {
					if (StatusEnum.COUPON_CLIENT_NOT_SUPPORT.getStatus() == coupon.getWapScope().intValue()) {
						coupon.setLimited(StatusEnum.COUPON_USE_LIMITED.getStatus());
						coupon.setShowClient(false);
						couponLimited.add(coupon);
						continue;
					}
				}
				if (CouponEnum.COUPON_CLIENT_APP.getCode().equals(client)) {
					if (StatusEnum.COUPON_CLIENT_NOT_SUPPORT.getStatus() == coupon.getAppScope().intValue()) {
						coupon.setLimited(StatusEnum.COUPON_USE_LIMITED.getStatus());
						coupon.setShowClient(false);
						couponLimited.add(coupon);
						continue;
					}
				}
				// 是否满足起投金额
				if (coupon.getAmountScope().compareTo(amountScope) > 0) {
					coupon.setLimited(StatusEnum.COUPON_USE_LIMITED.getStatus());
					coupon.setShowCouponAmountScope(false);
					couponLimited.add(coupon);
					continue;
				}
				// 是否满足起投期限
				if (coupon.getDaysScope() > daysScope) {
					coupon.setLimited(StatusEnum.COUPON_USE_LIMITED.getStatus());
					coupon.setShowCouponDaysScope(false);
					couponLimited.add(coupon);
					continue;
				}
				// 现金券金额大于投资本金
				if (TypeEnum.COUPON_TYPE_CASH.getType() == couponType && coupon.getAmount().compareTo(amountScope) == 1) {
					coupon.setLimited(StatusEnum.COUPON_USE_LIMITED.getStatus());
					coupon.setCashCouponAmountLessThanInvest(false);
					couponLimited.add(coupon);
					continue;
				}
			}
			coupons.removeAll(couponLimited);
			coupons.addAll(couponLimited);
			watch.stop();
			logger.info("投资列表加载优惠券时间：" + watch.getTime());
			return coupons;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public List<Coupon> getUsableAndLimitedCouponsExceptAmount(Long memberId, Integer couponType, String client,
			Integer daysScope,BigDecimal projectRemainAmount) throws ManagerException {
		try {
			StopWatch watch = new StopWatch();
			watch.start();
			Map<String, Object> map = Maps.newHashMap();
			map.put("holderId", memberId);
			map.put("couponType", couponType);
			List<Coupon> coupons = couponMapper.findUsableCouponsByMemberId(map);
			List<Coupon> couponLimited = Lists.newArrayList();
			for (Coupon coupon : coupons) {
				// 是否满足使用时间
				if (DateUtils.compareDate(coupon.getStartDate(), DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3)) == DateUtils.BEFORE) {
					coupon.setLimited(StatusEnum.COUPON_USE_LIMITED.getStatus());
					coupon.setShowCouponValidity(false);
					couponLimited.add(coupon);
					continue;
				}
				// 是否为有效的客户端
				if (CouponEnum.COUPON_CLIENT_WEB.getCode().equals(client)) {
					if (StatusEnum.COUPON_CLIENT_NOT_SUPPORT.getStatus() == coupon.getWebScope().intValue()) {
						coupon.setLimited(StatusEnum.COUPON_USE_LIMITED.getStatus());
						coupon.setShowClient(false);
						couponLimited.add(coupon);
						continue;
					}
				}
				if (CouponEnum.COUPON_CLIENT_WAP.getCode().equals(client)) {
					if (StatusEnum.COUPON_CLIENT_NOT_SUPPORT.getStatus() == coupon.getWapScope().intValue()) {
						coupon.setLimited(StatusEnum.COUPON_USE_LIMITED.getStatus());
						coupon.setShowClient(false);
						couponLimited.add(coupon);
						continue;
					}
				}
				if (CouponEnum.COUPON_CLIENT_APP.getCode().equals(client)) {
					if (StatusEnum.COUPON_CLIENT_NOT_SUPPORT.getStatus() == coupon.getAppScope().intValue()) {
						coupon.setLimited(StatusEnum.COUPON_USE_LIMITED.getStatus());
						coupon.setShowClient(false);
						couponLimited.add(coupon);
						continue;
					}
				}
				// 是否满足起投金额
				if (coupon.getAmountScope().compareTo(projectRemainAmount) > 0) {
					coupon.setLimited(StatusEnum.COUPON_USE_LIMITED.getStatus());
					coupon.setShowCouponAmountScope(false);
					couponLimited.add(coupon);
					continue;
				}
				// 是否满足起投期限
				if (coupon.getDaysScope() > daysScope) {
					coupon.setLimited(StatusEnum.COUPON_USE_LIMITED.getStatus());
					coupon.setShowCouponDaysScope(false);
					couponLimited.add(coupon);
					continue;
				}
			}
			coupons.removeAll(couponLimited);
			coupons.addAll(couponLimited);
			watch.stop();
			logger.info("投资列表加载优惠券时间：" + watch.getTime());
			return coupons;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 当前投资是否可用该编号优惠券
	 * 
	 * @param result
	 * @param holderId
	 * @param coupon
	 * @param client
	 * @param amount
	 * @param days
	 * @return
	 * @throws ManagerException
	 */
	@Override
	public ResultDO<?> couponForCurrentInvestIsUsable(ResultDO<?> result, Coupon coupon,String client,Integer days, Order order)
			throws ManagerException {
		//判断使用的收益权是否有效
		if(coupon==null) {
			result.setResultCode(ResultCode.COUPON_NOT_EXIST_ERROR);
			return result;
		}
		if (coupon != null) {
			if (coupon.getStatus().intValue() != StatusEnum.COUPON_STATUS_RECEIVED_NOT_USED.getStatus()) {
				result.setResultCode(ResultCode.COUPON_STATUS_NOT_VALID_ERROR);
				return result;
			}
			//判断收益券持有人是否正确
			if(!coupon.getHolderId().equals(order.getMemberId())) {
				result.setResultCode(ResultCode.COUPON_HOLDER_NOT_VALID_ERROR);
				return result;
			}
			// 优惠券的有效期是否开始
			if (DateUtils.compareDate(coupon.getStartDate(), DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3)) == DateUtils.BEFORE) {// startDate
				result.setResultCode(ResultCode.COUPON_NOT_VALID_TIME_ERROR);
				return result;
			}

			// 优惠券是否过期
			if (DateUtils.compareDate(coupon.getEndDate(), DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3)) == DateUtils.AFTER) {// startDate
				result.setResultCode(ResultCode.COUPON_NOT_VALID_TIME_ERROR);
				return result;
			}
			
			//验证订单中使用现金券金额是否和优惠券面额相等
			if(order.getUsedCouponAmount()!=null) {
				if (order.getUsedCouponAmount().doubleValue() != coupon.getAmount().doubleValue()) {
					result.setResultCode(ResultCode.TRANSACTION_COUPON_AMOUNT_NOT_MATCH_ERROR);
					logger.info("优惠券验证失败：" + ResultCode.TRANSACTION_COUPON_AMOUNT_NOT_MATCH_ERROR.getMsg()
							+ ", orderNo=" + order.getOrderNo() + ", CashCouponNo=" + order.getCashCouponNo());
					return result;
				}
			}
			
			//判断是否支持当前客户端
			if(CouponEnum.COUPON_CLIENT_WEB.getCode().equals(client)){
				if(coupon.getWebScope().intValue()==StatusEnum.COUPON_CLIENT_NOT_SUPPORT.getStatus()){
					result.setResultCode(ResultCode.COUPON_NOT_SUPPORT_CURRENT_CLIENT_ERROR);
					return result;
				}
			}
			if (CouponEnum.COUPON_CLIENT_WAP.getCode().equals(client)) {
				if (coupon.getWapScope().intValue() == StatusEnum.COUPON_CLIENT_NOT_SUPPORT.getStatus()) {
					result.setResultCode(ResultCode.COUPON_NOT_SUPPORT_CURRENT_CLIENT_ERROR);
					return result;
				}
			}
			if (CouponEnum.COUPON_CLIENT_APP.getCode().equals(client)) {
				if (coupon.getAppScope().intValue() == StatusEnum.COUPON_CLIENT_NOT_SUPPORT.getStatus()) {
					result.setResultCode(ResultCode.COUPON_NOT_SUPPORT_CURRENT_CLIENT_ERROR);
					return result;
				}
			}
			//判断是否支持当前投资额
			if(order.getInvestAmount().compareTo(coupon.getAmountScope())<0){
				result.setResultCode(ResultCode.COUPON_AMOUNT_NOT_MATCH_CONDITION_ERROR);
				return result;
			}
			// 判断是否支持当前投资天数
			if (days < coupon.getDaysScope()) {
				result.setResultCode(ResultCode.COUPON_DAYS_NOT_MATCH_CONDITION_ERROR);
				return result;
			}
			//新手项目不支持使用高额收益券
			if(coupon.getExtraInterestType()!=null && coupon.getExtraInterestType()==1
					&& coupon.getExtraInterestDay()!=null && coupon.getExtraInterestDay()>0){
				Project project = projectManager.selectByPrimaryKey(order.getProjectId());
				if(project!=null&&project.isNoviceProject()){
					result.setResultCode(ResultCode.COUPON_NOT_VALID_EXTRA_ERROR);
					return result;
				}
			}
			
			
		}
		return result;
	}

	/**
	 * 获取不同类型（现金券、优惠券）可用优惠券数量
	 */
	@Override
	public int getMemberActivedCouponCountByType(Long holderId, int couponType) throws ManagerException {
		try {
			return couponMapper.getMemberActivedCouponCountByType(holderId, couponType);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public boolean isReceiveBirthdayCoupon(Long memberId, Long activityId, Date birthday) throws ManagerException {
		boolean flag = true;
		Long couponTemplateId = getTemplateIdByActivityId(activityId);
		String _birthday = DateUtils.getYear(DateUtils.getCurrentDate()) + "-" + DateUtils.getStrFromDate(birthday, "MM-dd");
		Integer num = couponMapper.isReceiveBirthdayCoupon(memberId, couponTemplateId, _birthday);
		if (num == null) {
			flag = false;
		} else {
			flag = true;
		}
		return flag;
	}

	/**
	 * 优惠券解锁
	 */
	@Override
	public int unlockedCouponByCouponNo(String couponNo) throws ManagerException {
		try {
			int result = couponMapper.unlockedCouponByCouponNo(couponNo);
			return result;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	
	/**
	 * 将优惠券置为已使用
	 */
	@Override
	public int usedCouponByCouponNo(String couponNo) throws ManagerException{
		try {
			int result = couponMapper.usedCouponByCouponNo(couponNo);
			return result;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	

	
	/**
	 * 根据用户ID获取优惠券
	 */
	@Override
	public  Page<Coupon> selectCouponByMermberId(Page<Coupon> pageRequest,Map<String, Object> map) throws ManagerException{
		try {
			Page<Coupon> CouponList = couponMapper.selectCouponByMermberId(pageRequest,map);
			return CouponList;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}


	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Coupon selectCouponforUpdate( Long id) throws ManagerException {
		try {
			Coupon coupon = couponMapper.selectCouponforUpdate(id);
			return coupon;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<Coupon> selectNewYearCoupon(List<Object> list, Long memberId, Long activityId) throws ManagerException {
		try {
			return couponMapper.selectNewYearCoupon(list, memberId, activityId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<Coupon> getCouponByMemberIdAndActivityId(Long memberId, long activityId) throws ManagerException {
		try {
			return couponMapper.getCouponByMemberIdAndActivityId(memberId, activityId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	public void extendCouponEndDateForProjectLose(Order order,Date transactionDate,Date loseDate) throws ManagerException{
		try {
			int extendDays = DateUtils.getIntervalDays(transactionDate, loseDate);
			//订单是否使用现金券
			if(StringUtil.isNotBlank(order.getCashCouponNo())){
				extendCouponEndDate(order.getCashCouponNo(),extendDays);
			}
			//订单是否使用收益券
			if(StringUtil.isNotBlank(order.getProfitCouponNo())){
				extendCouponEndDate(order.getProfitCouponNo(),extendDays);
			}
		}catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	private void extendCouponEndDate(String couponNo,Integer extendDays){
		Coupon cashCoupon = couponMapper.getCouponByCouponNo(couponNo);
		if(cashCoupon!=null){
			Date endDate = DateUtils.addDate(cashCoupon.getEndDate(), extendDays);
			couponMapper.extendCouponEndDate(StatusEnum.COUPON_STATUS_USED.getStatus(),
					StatusEnum.COUPON_STATUS_RECEIVED_NOT_USED.getStatus(), endDate,"流标退回优惠券" ,couponNo);
		}
	}


	@Override
	public Coupon checkTransactionCouponFromPopularity(Long orderId, Long[] ids, Date startTime, Date endTime) throws ManagerException {
		try {
			return couponMapper.checkTransactionCouponFromPopularity(orderId, ids, startTime, endTime);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	@Override
	public int getMemberCouponCountFilterP2p(Long memberId) throws ManagerException {
		try {
			return couponMapper.getMemberCouponCountFilterP2p(memberId, StatusEnum.COUPON_STATUS_RECEIVED_NOT_USED.getStatus());
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<CouponTemplateSMSBiz> queryCouponMemberMobileByTemplate(Long templateid, Date enddate) {
		return couponMapper.queryCouponMemberMobileByTemplate(templateid,enddate);
	}

	@Override
	public List<CouponTemplateSMSBiz> queryCouponExpirationReminderMobile( Date endDate) {
		return couponMapper.queryCouponExpirationReminderMobile(endDate);
	}

	
	@Override
	public boolean useCouponSpecialLimit(Long memberId) throws ManagerException {
		try {
			if (memberId < 1) {
				return true;
			}
			Member member = memberMapper.selectByPrimaryKey(memberId);
			if (member == null) {
				return true;
			}
			// 特殊注册来源不允许使用现金券(后续有累加业务则抽象多个判断接口)
			if (StringUtil.isBlank(member.getRegisterTraceSource())) {
				return true;
			}
			String memberTrace = member.getRegisterTraceSource();
			SysDict limitDict = sysDictManager.findByGroupNameAndKey(Constant.DICT_GROUP_REGISTER_TRACE_SOURCE_LIMIT,
					Constant.DICT_KEY_USE_COUPON_LIMIT);
			if (limitDict == null || StringUtil.isBlank(limitDict.getValue())) {
				return true;
			} else {
				String[] registerTraceArr = limitDict.getValue().split(Constant.DICT_VALUE_SPLIT);
				for (String registerTrace : registerTraceArr) {
					if (memberTrace.equals(registerTrace)) {
						return false;
					}
				}
			}
			return true;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	/**
	 * 获取自动投标中会员可用优惠券
	 */
	@Override
	public Coupon findUsableAutoInvestCouponsByMemberId(Long memberId, Integer couponType, Integer queryType) throws ManagerException {
		try {
			Map<String, Object> map = Maps.newHashMap();
			map.put("holderId", memberId);
			map.put("couponType", couponType);
			map.put("queryType", queryType);
			Coupon coupon = couponMapper.findUsableAutoInvestCouponsByMemberId(map);
			return coupon;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

}