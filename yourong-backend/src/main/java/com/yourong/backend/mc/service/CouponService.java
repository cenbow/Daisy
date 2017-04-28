package com.yourong.backend.mc.service;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.yourong.common.pageable.Page;
import com.yourong.core.mc.model.Coupon;

public interface CouponService {
	/**
	 * 优惠券分页列表
	 * @param pageRequest
	 * @param map
	 * @return
	 */
	public Page<Coupon> findByPage(Page<Coupon> pageRequest,
			Map<String, Object> map);

	/**
	 * 根据id获取优惠券
	 * @param id
	 * @return
	 */
	Coupon selectByPrimaryKey(Long id);

	/**
	 * 领用优惠券
	 * @param userId
	 * @param activityId
	 * @return
	 */
	public Coupon receiveCoupon(Long userId, Long activityId, Long senderId);
	
	/**
	 * 使用优惠券
	 * @param id
	 * @param projectId
	 * @param transactionId
	 * @return
	 */
	public int useCoupon(String couponNo,Long projectId,Long transactionId);
	
	/**
	 * 获取会员可用优惠券
	 * @param memberId
	 * @param couponType
	 * @return
	 */
	public List<Coupon> findUsableCouponsByMemberId(Long memberId,Integer couponType,String client,BigDecimal amountScope,Integer daysScope);

	public Integer expireCouponTask();
	
	/**
	 * 赠送优惠券
	 * @param mobile
	 * @param couponTemplateId
	 * @return
	 */
	public Map<String, Object> giveCoupon(Long[] mobiles,Long couponTemplateId,Long senderId);
	
	/**
	 * 赠送人气值
	 * @param mobile
	 * @param income
	 * @param remarks
	 * @param senderId
	 * @return
	 */
	public boolean givePopularity(Long mobile,BigDecimal income,String remarks,Long senderId);
	
	/**
	 * 更新优惠券
	 * @param coupon
	 * @return
	 */
	Object unlockedCouponByCouponNo(String couponNo);
	
	/**
	 * 优惠券解锁列表
	 * @param pageRequest
	 * @param map
	 * @return
	 */
	public Page<Coupon> getCouponByMember(Page<Coupon> pageRequest,Map<String, Object> map);

	/**
	 * 赠送优惠 发放优惠券
	 * @param xssfSheet
	 * @param sms
	 * @param smsContent
	 * @param templateId
	 * @param successMobile
	 * @param senderId
     * @return
     */
	public Object sendCoupon(File file, Long templateId, Long successMobile, Long senderId);
	
}
