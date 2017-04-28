package com.yourong.web.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.mc.model.Coupon;
import com.yourong.core.mc.model.CouponTemplate;
import com.yourong.core.mc.model.biz.ActivityBiz;
import com.yourong.core.mc.model.biz.ActivityForRedFriday;
import com.yourong.core.mc.model.query.CouponQuery;

public interface CouponService {

	/**
	 * 分页获取优惠券列表
	 * 
	 * @param memberId
	 * @param couponType
	 * @param status
	 * @return
	 */
	Page<Coupon> findCouponsByPage(CouponQuery couponQuery);
	
	/**
	 * 根据状态获取优惠券总额
	 * @param couponQuery
	 * @return
	 */
    BigDecimal findTotalAmountByStatus(CouponQuery couponQuery);

    /**
     * 兑换优惠券
     * @param memberId
     * @param couponTemplateId
     * @param num
     * @return
     * @throws Exception
     */
	ResultDO<CouponTemplate> exchangeCoupon(Long memberId, Long couponTemplateId,
			int num) throws Exception;

	/**
	 * 通过会员id和活动查询优惠券
	 * @param id
	 * @param l
	 * @return
	 */
	Coupon getCouponByMemberIdAndActivity(Long memberId, long activityId);

	/**
	 * 获取优惠券数量
	 * @param id
	 * @return
	 */
	int getMemberCouponCount(Long id);
	
	/**
	 * 获取不同类型优惠券数量
	 * @param memberId
	 * @param couponType
	 * @return
	 */
	int getMemberCouponCountByType(Long memberId,Integer couponType);

	/**
	 * 领取压岁钱现金券
	 * @param id
	 * @return
	 */
	public ResultDO<Coupon> receiveGiftmoney(Long memberId, Long activityId);
	
	 /**
     * 根据会员id和活动id领取优惠券
     * @param memberId
     * @param acitityId
     * @return
     */
    ResultDO<Coupon> receiveCoupon(Long memberId,Long acitityId);
    
	/**
	 * 根据活动Id获取几条领取过的优惠券失败
	 * @param couponTemplateId
	 * @param showNum
	 * @return
	 */
	List<Coupon> getCouponsByCouponTemplateId(Long ActivityId);
	
	/**
	 * 获取所有用于人气值兑换的优惠券模板
	 * @param ids
	 * @return
	 */
	List<CouponTemplate> findExchangeCouponsByIds(Long[] ids);

	/**
	 * 领取红色星期五活动的现金券
	 * @param id
	 * @param parseLong
	 * @return
	 */
	ResultDO<Coupon> receiveRedFriday(Long memberId, Long activityId);

	/**
	 * 根据活动id查询被领取的数量
	 * @param activityId
	 * @return
	 */
	int countReceivedCouponByActivityId(Long activityId);
	
	/**
	 * 根据活动id查询被领取的数量
	 * @param activityId
	 * @return
	 */
	List<ActivityForRedFriday> getReceivedCouponByActivityId(Long activityId);

	/**
	 * 判断是否参与过本期红色星期五
	 * @param memberId
	 * @param activityId
	 * @return
	 */
	boolean redFridayIsReceived(Long memberId, Long activityId);
	
	/**
	 * 获取不同类型（现金券、优惠券）可用优惠券数量
	 * @param holderId
	 * @param couponType
	 * @return
	 */
	int getMemberActivedCouponCountByType(Long holderId,int couponType);
	
	/**
	 * 领取生日50元优惠券
	 * @param memberId
	 * @param birthday
	 * @return
	 */
	ResultDO receiveBirthday50Coupon(Long memberId, Date birthday);
	
	/**
	 * 领取生日1%优惠券
	 * @param memberId
	 * @param birthday
	 * @return
	 */
	ResultDO receiveBirthday001Coupon(Long memberId, Date birthday);

	/**
	 * 通过会员id查询被推荐会员总数
	 * @param memberId
	 * @return
	 */
	long getReferralMemberByIdCount(Long memberId);
	/**
	 * 领取中秋活动现金券
	 * @param id
	 * @param parseLong
	 * @return
	 */
	ResultDO<ActivityBiz> midAutumnCoupon(Long memberId, Long activityId);

	/**
	 * 获取模板id集合
	 * @param code
	 * @return
	 */
	List<Long> getTemplateids(String code);

	/**
	 * 获取模板id数组
	 * @param code
	 * @return
     */
	Long[] getTemplateidsArray(String code);
	
	boolean giveCouponForQuestion(Long memberId, Long couponTemplateId, Long senderId);
}
