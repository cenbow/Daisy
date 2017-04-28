package com.yourong.core.mc.manager;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.mc.model.Coupon;
import com.yourong.core.mc.model.biz.ActivityForRedFriday;
import com.yourong.core.mc.model.biz.CouponReceiveBiz;
import com.yourong.core.mc.model.biz.CouponTemplateSMSBiz;
import com.yourong.core.mc.model.query.CouponQuery;
import com.yourong.core.tc.model.Order;

public interface CouponManager {
	Integer deleteByPrimaryKey(Long id) throws ManagerException;

	Integer insert(Coupon coupon) throws ManagerException;

	Coupon selectByPrimaryKey(Long id) throws ManagerException;

	Integer updateByPrimaryKey(Coupon coupon) throws ManagerException;

	Integer updateByPrimaryKeySelective(Coupon coupon) throws ManagerException;

	/**
	 * 后台分页获取优惠券
	 * 
	 * @param pageRequest
	 * @param map
	 * @return
	 * @throws ManagerException
	 */
	Page<Coupon> findByPage(Page<Coupon> pageRequest, Map<String, Object> map) throws ManagerException;

	/**
	 * 通过优惠券编码获取优惠券信息
	 * 
	 * @param couponNo
	 * @return
	 * @throws ManagerException
	 */
	public Coupon getCouponByCouponNo(String couponNo) throws ManagerException;

	/**
	 * 批量插入优惠券
	 * 
	 * @param coupons
	 * @return
	 * @throws ManagerException
	 */
	Integer batchInsertCoupon(List<Coupon> coupons) throws ManagerException;

	/**
	 * 定时把过期的优惠券的状态设置为过期
	 * 
	 * @return
	 * @throws ManagerException
	 */
	public Integer expireCouponTask() throws ManagerException;

	/**
	 * 领取优惠券
	 * 
	 * @param userId
	 * @param activityId
	 * @return
	 * @throws ManagerException
	 */
	public Coupon receiveCoupon(Long userId, Long activityId, Long couponTemplateId, Long senderId) throws ManagerException;

	
	/**
	 * 领取优惠券,支持发送渠道,获取方式
	 * 
	 * @param userId
	 * @param activityId
	 * @return
	 * @throws ManagerException
	 */
	public Coupon receiveCouponSource(Long userId, Long activityId, Long couponTemplateId, Long senderId,Integer way,Integer source) throws ManagerException;
	/**
	 * 使用优惠券
	 * 
	 * @param id
	 * @param projectId
	 * @param transactionId
	 * @return
	 * @throws ManagerException
	 */
	public Integer useCoupon(String couponNo, Long projectId, Long transactionId) throws ManagerException;

	/**
	 * 用户的所有状态为已领用，未使用优惠券列表
	 * 
	 * @param memberId
	 * @param couponType
	 * @return
	 * @throws ManagerException
	 */
	public List<Coupon> findUsableCouponsByMemberId(Long memberId, Integer couponType) throws ManagerException;

	/**
	 * 前台分页获取优惠券
	 * 
	 * @param memberId
	 * @param couponType
	 * @param status
	 * @return
	 * @throws ManagerException
	 */
	Page<Coupon> findFrontCouponsByPage(CouponQuery couponQuery) throws ManagerException;

	/**
	 * 根据状态获取优惠券总额
	 * 
	 * @param couponQuery
	 * @return
	 */
	Coupon findTotalAmountByStatus(CouponQuery couponQuery) throws ManagerException;

	/**
	 * 送红包调用接口
	 * 
	 * @param memberId
	 *            会员id
	 * @param sourceType
	 *            来源类型，TypeEnum 中COUPON_SEND_SOURCE
	 * @return
	 * @throws ManagerException
	 */
	public Coupon sendCoupon(Long memberId, TypeEnum sourceType, Long couponTemplateId) throws ManagerException;

	/**
	 * 通过会员id和活动查询优惠券
	 * 
	 * @param memberId
	 * @param activityId
	 * @return
	 * @throws ManagerException
	 */
	List<Coupon> getCouponByMemberIdAndActivity(Long memberId, long activityId) throws ManagerException;

	/**
	 * 通过会员id和优惠券模板id查询优惠券
	 * 
	 * @param memberId
	 * @param couponTemplateId
	 * @return
	 * @throws ManagerException
	 */
	public List<Coupon> getCouponByMemberIdAndCouponTemplateId(Long memberId, Long couponTemplateId) throws ManagerException;

	/**
	 * 获取优惠券数量
	 * 
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 */
	int getMemberCouponCount(Long memberId) throws ManagerException;

	/**
	 * 锁定优惠券
	 * 
	 * @param couponNo
	 * @return
	 * @throws ManagerException
	 */
	boolean lockCoupon(String couponNo) throws ManagerException;

	/**
	 * 解锁优惠券
	 * 
	 * @param couponNo
	 * @return
	 * @throws ManagerException
	 */
	boolean unLockCoupon(String couponNo) throws ManagerException;

	/**
	 * 获取不同类型优惠券数量
	 * 
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 */
	int getMemberCouponCountByType(Long memberId, Integer couponType) throws ManagerException;

	/**
	 * 超出或等于投资总额
	 */
	boolean moreOrEqualInvestTotalAmount(Long memberId, Long activityId) throws ManagerException;

	/**
	 * 根据活动ID获取优惠券模板ID
	 * 
	 * @param activityId
	 * @return
	 * @throws ManagerException
	 */
	public Long getTemplateIdByActivityId(Long activityId) throws ManagerException;
	
	/**
	 * 根据活动ID获取优惠券模板ID  解析方式不同
	 * 
	 * @param activityId
	 * @return
	 * @throws ManagerException
	 */
	public Long getTemplateIdByActivityId2(Long activityId) throws ManagerException;
	

	/**
	 * 根据模板id获取最新的几条领取过的优惠券
	 * 
	 * @param couponTemplateId
	 * @param showNum
	 * @return
	 */
	List<Coupon> getCouponsByCouponTemplateId(Long couponTemplateId, int showNum) throws ManagerException;

	/**
	 * 获得用户优惠券数量
	 * 
	 * @param memberId
	 * @param couponType
	 * @param status
	 * @return
	 * @throws ManagerException
	 */
	public int getCouponTotalCount(Long memberId, Integer couponType, Integer status) throws ManagerException;

	/**
	 * 获得即将过期的优惠券
	 * 
	 * @return
	 */
	List<Coupon> getExpiringCoupons() throws ManagerException;

	/**
	 * 根据活动id查询被领取的数量
	 * 
	 * @param activityId
	 * @return
	 * @throws ManagerException
	 */
	int countReceivedCouponByActivityId(Long activityId) throws ManagerException;

	/**
	 * 根据活动id查询被领取的列表
	 * 
	 * @param activityId
	 * @return
	 * @throws ManagerException
	 */
	List<ActivityForRedFriday> getReceivedCouponByActivityId(Long activityId) throws ManagerException;

	/**
	 * 领用优惠券 通过队列
	 */
	public Coupon receiveCouponByRedis(CouponReceiveBiz couponReceiveBiz) throws ManagerException;

	/**
	 * 用户所有状态为(已领用、未使用)的优惠券（区分当前投资是否可用）
	 */
	List<Coupon> getUsableAndLimitedCoupons(Long memberId, Integer couponType, String client, BigDecimal amountScope, Integer daysScope)
			throws ManagerException;

	/**
	 * 当前投资是否可用该编号优惠券
	 */
	ResultDO<?> couponForCurrentInvestIsUsable(ResultDO<?> result, Coupon coupon,String client,Integer days, Order order) throws ManagerException;
	
	/**
	 * 获取不同类型（现金券、优惠券）可用优惠券数量
	 * 
	 * @param holderId
	 * @param couponType
	 * @return
	 */
	int getMemberActivedCouponCountByType(Long holderId, int couponType) throws ManagerException;

	/**
	 * 优惠券解锁
	 * 
	 * @param couponNo
	 * @return
	 */
	int unlockedCouponByCouponNo(String couponNo) throws ManagerException;

	/**
	 * 将优惠券置为已使用
	 * @param couponNo
	 * @return
	 */
	int usedCouponByCouponNo(String couponNo)throws ManagerException;
	
	/**
	 * 是否领取生日优惠券
	 * 
	 * @param memberId
	 * @param activityId
	 * @param birthday
	 * @return
	 */
	public boolean isReceiveBirthdayCoupon(Long memberId, Long activityId, Date birthday) throws ManagerException;

	/**
	 * 根据用户ID获取优惠券
	 * 
	 * @param memberId
	 * @param activityId
	 * @param birthday
	 * @return
	 */
	public Page<Coupon> selectCouponByMermberId(Page<Coupon> pageRequest,Map<String, Object> map) throws ManagerException;


	/**
	 * 锁住优惠劵
	 * @param id
	 * @return
	 */
	Coupon selectCouponforUpdate(Long id)throws ManagerException;

	/**
	 * 
	 * @Description:判断交易使用的优惠券是否是用人气值换的
	 * @param transaction
	 * @param ids
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月22日 下午5:56:05
	 */
	public Coupon checkTransactionCouponFromPopularity(Long orderId, Long[] ids, Date startTime, Date endTime) throws ManagerException;
	/**
	 * 
	 * @Description:元旦活动查询优惠券状态
	 * @param list
	 * @return
	 * @author: wangyanji
	 * @time:2015年11月24日 下午3:12:58
	 */
	public List<Coupon> selectNewYearCoupon(List<Object> list, Long memberId, Long activityId) throws ManagerException;

	/**
	 * 
	 * @Description:根据活动ID和memberId查询优惠券
	 * @param memberId
	 * @param activityId
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2015年12月31日 下午1:47:30
	 */
	List<Coupon> getCouponByMemberIdAndActivityId(Long memberId, long activityId) throws ManagerException;
	
	/**
	 * @Description:延长优惠券可用时间
	 * @param order
	 * @param transactionDate
	 * @param loseDate
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2016年2月17日 下午5:28:12
	 */
	public void extendCouponEndDateForProjectLose(Order order,Date transactionDate,Date loseDate) throws ManagerException;
	/**
	 * 
	 * @Description:优惠券数量 过滤p2p
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年3月8日 下午1:59:14
	 */
	int getMemberCouponCountFilterP2p(Long memberId)throws ManagerException;
	/**
	 * 用户所有状态为(已领用、未使用)的优惠券（区分当前投资是否可用）
	 */
	public List<Coupon> getUsableAndLimitedCouponsExceptAmount(Long memberId, Integer couponType, String client,Integer daysScope,BigDecimal projectRemainAmount) throws ManagerException;

	/**
	 * 查询模板到期时间优惠券持有用户信息
	 * @param templateid
	 * @param enddate
     * @return
     */
	public List<CouponTemplateSMSBiz> queryCouponMemberMobileByTemplate(Long templateid, Date enddate);
	
	/**
	 * 查询指定日期到期的已领取未使用的优惠券
	 * @param endDate
	 * @return
     */
	public List<CouponTemplateSMSBiz> queryCouponExpirationReminderMobile(Date endDate) ;
	/**
	 * 
	 * @Description:判断该用户是否特殊群体不允许使用优惠券
	 * @param member
	 * @return true-允许, false-不允许
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年9月11日 下午3:47:02
	 */
	public boolean useCouponSpecialLimit(Long memberId) throws ManagerException;
	
	/**
	 * 
	 * @Description 获取自动投标中会员可用优惠券
	 * @param memberId
	 * @param couponType
	 * @param queryType(1-优先使用收益最高优惠券,2-优先使用有效期最短优惠券)
	 * @return
	 * @throws ManagerException
	 * @author luwenshan
	 * @time 2016年10月14日 上午10:57:03
	 */
	public Coupon findUsableAutoInvestCouponsByMemberId(Long memberId, Integer couponType, Integer queryType) throws ManagerException;
	
}