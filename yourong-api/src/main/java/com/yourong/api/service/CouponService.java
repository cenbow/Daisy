package com.yourong.api.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.yourong.api.dto.CouponDto;
import com.yourong.api.dto.CouponTemplateDto;
import com.yourong.api.dto.ResultDTO;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.mc.model.Coupon;
import com.yourong.core.mc.model.CouponTemplate;
import com.yourong.core.mc.model.query.CouponQuery;

public interface CouponService {
	
	/**
	 * 分页获取优惠券列表
	 * @param couponQuery
	 * @return
	 */
	Page<CouponDto> queryCoupons(CouponQuery couponQuery);
	
	/**
	 * 分页获取优惠券列表
	 * @param couponQuery
	 * @return
	 */
	Map<String,Object> queryCouponsHistory(CouponQuery couponQuery);

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
	ResultDTO exchangeCoupon(Long memberId, Long couponTemplateId,
			int num,Integer source) throws Exception;

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
	Page<CouponTemplateDto> findExchangeCouponsByIds(Long[] ids);

	/**
	 * 领取生日50元优惠券
	 * @param memberId
	 * @param birthday
	 * @return
	 */
	ResultDTO receiveBirthday50Coupon(Long memberId, Date birthday);

	/**
	 * 领取生日1%优惠券
	 * @param memberId
	 * @param birthday
	 * @return
	 */
	ResultDTO receiveBirthday001Coupon(Long memberId, Date birthday);
	/**
	 * 
	 * @Description:优惠券数量 过滤p2p
	 * @param id
	 * @return
	 * @author: chaisen
	 * @time:2016年3月8日 下午1:57:38
	 */
	int getMemberCouponCountFilterP2p(Long id);
	/**
	 * 
	 * @Description:获取开通存钱罐优惠券模板
	 * @return
	 * @author: zhanghao
	 * @time:2016年5月18日 上午10:20:38
	 */
	public CouponTemplate getkaiTongCunQianGuanCouponTemplate() ;


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
}
