package com.yourong.core.mc.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.yourong.core.mc.model.biz.CouponTemplateSMSBiz;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.yourong.common.pageable.Page;
import com.yourong.core.mc.model.Coupon;
import com.yourong.core.mc.model.biz.ActivityForRedFriday;
import com.yourong.core.mc.model.query.CouponQuery;

public interface CouponMapper {
	@Delete({ "delete from mc_coupon", "where id = #{id,jdbcType=BIGINT}" })
	int deleteByPrimaryKey(Long id);

	@Insert({
			"insert into mc_coupon (id, coupon_code, ",
			"coupon_template_id, coupon_type, ",
			"amount, status, ",
			"vaild_calc_type, days, ",
			"web_scope, wap_scope, app_scope, amount_scope, days_scope,",
			"start_date, end_date, holder_id, ",
			"receive_time, used_time, ",
			"remarks, activity_id, ",
			"sender_id, use_condition, ",
			"project_id, transaction_id, ",
			"create_time, update_time)",
			"values (#{id,jdbcType=BIGINT}, #{couponCode,jdbcType=VARCHAR}, ",
			"#{couponTemplateId,jdbcType=BIGINT}, #{couponType,jdbcType=INTEGER}, ",
			"#{amount,jdbcType=DECIMAL}, #{status,jdbcType=INTEGER}, ",
			"#{vaildCalcType,jdbcType=INTEGER}, #{days,jdbcType=INTEGER}, ",
			"#{webScope,jdbcType=INTEGER}, #{wapScope,jdbcType=INTEGER}, #{appScope,jdbcType=INTEGER}, #{amountScope,jdbcType=DECIMAL}, #{daysScope,jdbcType=INTEGER},",
			"#{startDate,jdbcType=DATE}, #{endDate,jdbcType=DATE}, #{holderId,jdbcType=BIGINT}, ",
			"#{receiveTime,jdbcType=TIMESTAMP}, #{usedTime,jdbcType=TIMESTAMP}, ",
			"#{remarks,jdbcType=VARCHAR}, #{activityId,jdbcType=BIGINT}, ",
			"#{senderId,jdbcType=BIGINT}, #{useCondition,jdbcType=VARCHAR}, ",
			"#{projectId,jdbcType=BIGINT}, #{transactionId,jdbcType=BIGINT}, ",
			"#{createTime,jdbcType=TIMESTAMP}, #{updateTime,jdbcType=TIMESTAMP})" })
	int insert(Coupon record);

	int insertSelective(Coupon record);

	@Select({
			"select",
			"id, coupon_code, coupon_template_id, coupon_type, amount, status, vaild_calc_type, ",
			"days, web_scope, wap_scope, app_scope, amount_scope, days_scope, start_date, end_date, holder_id, receive_time, used_time, remarks, activity_id, ",
			"sender_id, use_condition, project_id, transaction_id, create_time, update_time", "from mc_coupon",
			"where id = #{id,jdbcType=BIGINT}" })
	@ResultMap("BaseResultMap")
	Coupon selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(Coupon record);

	@Update({ "update mc_coupon", "set coupon_code = #{couponCode,jdbcType=VARCHAR},",
			"coupon_template_id = #{couponTemplateId,jdbcType=BIGINT},", "coupon_type = #{couponType,jdbcType=INTEGER},",
			"amount = #{amount,jdbcType=DECIMAL},", "status = #{status,jdbcType=INTEGER},",
			"vaild_calc_type = #{vaildCalcType,jdbcType=INTEGER},", "days = #{days,jdbcType=INTEGER},",
			"web_scope = #{webScope,jdbcType=INTEGER},", "wap_scope = #{wapScope,jdbcType=INTEGER},",
			"app_scope = #{appScope,jdbcType=INTEGER},", "amount_scope = #{amountScope,jdbcType=DECIMAL},",
			"days_scope = #{daysScope,jdbcType=INTEGER},", "start_date = #{startDate,jdbcType=DATE},",
			"end_date = #{endDate,jdbcType=DATE},", "holder_id = #{holderId,jdbcType=BIGINT},",
			"receive_time = #{receiveTime,jdbcType=TIMESTAMP},", "used_time = #{usedTime,jdbcType=TIMESTAMP},",
			"remarks = #{remarks,jdbcType=VARCHAR},", "activity_id = #{activityId,jdbcType=BIGINT},",
			"sender_id = #{senderId,jdbcType=BIGINT},", "use_condition = #{useCondition,jdbcType=VARCHAR},",
			"project_id = #{projectId,jdbcType=BIGINT},", "transaction_id = #{transactionId,jdbcType=BIGINT},",
			"create_time = #{createTime,jdbcType=TIMESTAMP},", "update_time = #{updateTime,jdbcType=TIMESTAMP}",
			"where id = #{id,jdbcType=BIGINT}" })
	int updateByPrimaryKey(Coupon record);

	List<Coupon> findByPage(@Param("map") Map<String, Object> map);

	int batchDelete(@Param("ids") int[] ids);

	/***************************** 后台分页 *****************************/
	List<Coupon> selectForPagin(@Param("map") Map<String, Object> map);

	int selectForPaginTotalCount(@Param("map") Map<String, Object> map);

	/***************************** 后台分页 *****************************/

	Coupon getCouponByCouponNo(String couponCode);

	int batchInsertCoupon(List<Coupon> coupons);

	/**
	 * 设置未领取优惠券过期(status 1:未领取，3:未领取，已过期 vaild_calc_type 1:按时间计算)
	 * 
	 * @return
	 */
	@Update({ "update mc_coupon set status = 3 where status = 0 and vaild_calc_type = 1 and end_date< CURRENT_DATE()" })
	int expireUnReceivedCouponTask();

	/**
	 * 设置已领取优惠券过期(status 1:已领取，未使用，4:已领取，已过期 vaild_calc_type 2:按领取后天数计算)
	 * 
	 * @return
	 */
	@Update({ "UPDATE mc_coupon set status = 4 where status = 1 and vaild_calc_type in(1,2) and end_date< CURRENT_DATE()" })
	int expireReceivedCouponTask();

	/**
	 * 根据优惠券模板获取一个未领取的优惠券 ，根据印刷的先后顺序获取
	 * 
	 * @param couponTemplateId
	 * @return
	 */
	Coupon findUnreceviedCouponByTemplateId(Long couponTemplateId);

	int receiveDaysCoupon(Coupon coupon);

	int receiveForeverAndTimezoonCoupon(Coupon coupon);

	/**
	 * 使用优惠券
	 * 
	 * @param map
	 * @return
	 */
	int useCoupon(@Param("map") Map<String, Object> map);

	/**
	 * 获取会员可用优惠券
	 * 
	 * @param map
	 * @return
	 */
	List<Coupon> findUsableCouponsByMemberId(@Param("map") Map<String, Object> map);

	/********************************** 前台分页***start *********************************/
	List<Coupon> selectForPaginFront(@Param("couponQuery") CouponQuery couponQuery);

	int selectForPaginTotalCountFront(@Param("couponQuery") CouponQuery couponQuery);

	/********************************** 前台分页****end ********************************/

	/**
	 * 根据状态获取优惠券总额
	 * 
	 * @param couponQuery
	 * @return
	 */
	Coupon findTotalAmountByStatus(@Param("couponQuery") CouponQuery couponQuery);

	/**
	 * 通过会员id和活动查询优惠券
	 * 
	 * @param memberId
	 * @param activityId
	 * @return
	 */
	List<Coupon> getCouponByMemberIdAndCouponTemplateId(@Param("memberId") Long memberId, @Param("couponTemplateId") Long couponTemplateId);

	/**
	 * 获取优惠券数量
	 * 
	 * @param memberId
	 * @return
	 */
	int getMemberCouponCount(@Param("memberId") Long memberId, @Param("status") int status);

	/**
	 * 获取不同类型（现金券、优惠券）优惠券数量
	 * 
	 * @param holderId
	 * @param couponType
	 * @return
	 */
	int getMemberCouponCountByType(@Param("holderId") Long holderId, @Param("couponType") int couponType);

	/**
	 * 根据模板id获取最新的几条领取过的优惠券
	 * 
	 * @param couponTemplateId
	 * @param showNum
	 * @return
	 */
	List<Coupon> getCouponsByCouponTemplateId(@Param("couponTemplateId") Long couponTemplateId, @Param("showNum") int showNum);

	/**
	 * 根据优惠券编号查询优惠券模板印刷Id
	 * 
	 * @param couponCode
	 * @return
	 */
	Long findPrintIdByCouponCode(@Param("couponCode") String couponCode);

	/**
	 * 更新优惠券的使用数量
	 * 
	 * @param couponTemplatePrintId
	 * @return
	 */
	int updateUsedNumForCouponTemplatePrint(@Param("couponTemplatePrintId") Long couponTemplatePrintId);

	/**
	 * 获得即将过期的优惠券
	 * 
	 * @return
	 */
	List<Coupon> getExpiringCoupons();

	/**
	 * 根据活动id查询被领取的数量
	 * 
	 * @param activityId
	 * @return
	 */
	int countReceivedCouponByActivityId(@Param("activityId") Long activityId);

	/**
	 * 根据活动id查询被领取的列表
	 * 
	 * @param activityId
	 * @return
	 */
	List<ActivityForRedFriday> getReceivedCouponByActivityId(@Param("activityId") Long activityId);

	/**
	 * 领用优惠券根据优惠券模板id
	 * 
	 * @param coupon
	 * @return
	 */
	int receiveCouponByCouponTemplateId(Coupon coupon);

	/**
	 * 同一个用户同一个活动是否领取过当前优惠券
	 * 
	 * @param activity
	 * @param holderId
	 * @return
	 */
	Integer selectByMemberIdAndActivityId(@Param("activityId") Long activityId, @Param("holderId") Long holderId,
			@Param("couponTemplateId") Long couponTemplateId);

	/**
	 * 获取不同类型（现金券、优惠券）可用优惠券数量
	 * 
	 * @param holderId
	 * @param couponType
	 * @return
	 */
	int getMemberActivedCouponCountByType(@Param("holderId") Long holderId, @Param("couponType") int couponType);

	/**
	 * 解锁优惠券
	 * 
	 * @param couponNo
	 * @return
	 */
	int unlockedCouponByCouponNo(@Param("couponNo") String couponNo);

	/**
	 * 将优惠券置为已使用
	 * 
	 * @param couponNo
	 * @return
	 */
	int usedCouponByCouponNo(@Param("couponNo") String couponNo);

	/**
	 * 是否领取生日优惠券
	 * 
	 * @param holderId
	 * @param couponTemplateId
	 * @param receiveTime
	 * @return
	 */
	Integer isReceiveBirthdayCoupon(@Param("memberId") Long memberId, @Param("couponTemplateId") Long couponTemplateId,
			@Param("receiveTime") String receiveTime);

	/**
	 * 根据用户ID获取优惠券
	 */
	Page<Coupon> selectCouponByMermberId(Page<Coupon> pageRequest, @Param("map") Map<String, Object> map);

	/**
	 * 锁定未使用的优惠劵
	 * 
	 * @param couponNo
	 * @param memberId
	 * @return
	 */
	int lockedUnUseCouponByCouponNo(@Param("couponNo") String couponNo, @Param("memberId") Long memberId);

	/**
	 * 锁住优惠劵
	 * 
	 * @param id
	 * @return
	 */
	Coupon selectCouponforUpdate(@Param("id") Long id);

	/**
	 * 
	 * @Description:判断交易使用的优惠券是否是用人气值换的
	 * @param transactionId
	 * @param ids
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月28日 下午1:06:00
	 */
	Coupon checkTransactionCouponFromPopularity(@Param("orderId") Long orderId, @Param("ids") Long[] ids,
			@Param("receiveStartTime") Date receiveStartTime, @Param("receiveEndTime") Date receiveEndTime);

	/**
	 * 
	 * @Description:双旦活动初始化查询
	 * @param items
	 * @return
	 * @author: wangyanji
	 * @time:2015年11月24日 下午2:59:44
	 */
	List<Coupon> selectNewYearCoupon(@Param("items") List<Object> items, @Param("memberId") Long memberId,
			@Param("activityId") Long activityId);

	/**
	 * 
	 * @Description:根据memberId和activityId查询优惠券
	 * @param memberId
	 * @param activityId
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月31日 下午1:56:35
	 */
	List<Coupon> getCouponByMemberIdAndActivityId(@Param("memberId") Long memberId, @Param("activityId") Long activityId);
	/**
	 * 
	 * @Description:优惠券数量
	 * @param memberId
	 * @param status
	 * @return
	 * @author: chaisen
	 * @time:2016年3月8日 下午2:01:35
	 */
	int getMemberCouponCountFilterP2p(@Param("memberId") Long memberId, @Param("status") int status);
	
	/**
	 * @Description:退回优惠券，并且延迟优惠券的使用时间
	 * @param beforeStatus
	 * @param afterStatus
	 * @param couponCode
	 * @return
	 * @author: fuyili
	 * @time:2016年3月14日 下午3:38:08
	 */
	int extendCouponEndDate(@Param("beforeStatus") Integer beforeStatus, @Param("afterStatus") Integer afterStatus,
			@Param("endDate") Date endDate,@Param("remarks")String remarks, @Param("couponCode") String couponCode);

	/**
	 * 查询模板id优惠券用户手机号
	 * @param templateid
	 * @return
     */
	List<CouponTemplateSMSBiz> queryCouponMemberMobileByTemplate(@Param("templateid") Long templateid, @Param("enddate") Date enddate);
	
	/**
	 * 查询指定日期到期的已领取未使用的优惠券
	 * @param endDate
	 * @return
     */
	List<CouponTemplateSMSBiz> queryCouponExpirationReminderMobile(@Param("endDate") Date endDate);
	
	
	/**
	 * 
	 * @Description 获取自动投标中会员可用优惠券
	 * @param map
	 * @return
	 * @author luwenshan
	 * @time 2016年10月14日 上午10:53:25
	 */
	Coupon findUsableAutoInvestCouponsByMemberId(@Param("map") Map<String, Object> map);
	
}