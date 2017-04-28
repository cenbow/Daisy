package com.yourong.core.mc.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.yourong.common.pageable.Page;
import com.yourong.core.mc.model.Coupon;
import com.yourong.core.mc.model.CouponTemplate;

public interface CouponTemplateMapper {
    @Delete({
        "delete from mc_coupon_template",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into mc_coupon_template (id, name, coupon_type, ",
        "amount, vaild_calc_type, ",
        "web_scope, wap_scope, app_scope, amount_scope, days_scope,",
        "start_date, end_date, days, ",
        "status, use_condition, ",
        "create_by, create_time, ",
        "update_time, del_flag ,extra_interest_type ,extra_interest_day)",
        "values (#{id,jdbcType=BIGINT}, #{name,jdbcType=VARCHAR}, #{couponType,jdbcType=INTEGER}, ",
        "#{amount,jdbcType=DECIMAL}, #{vaildCalcType,jdbcType=INTEGER}, ",
        "#{webScope,jdbcType=INTEGER}, #{wapScope,jdbcType=INTEGER}, #{appScope,jdbcType=INTEGER}, #{amountScope,jdbcType=DECIMAL}, #{daysScope,jdbcType=INTEGER},",
        "#{startDate,jdbcType=DATE}, #{endDate,jdbcType=DATE}, #{days,jdbcType=INTEGER}, ",
        "0, #{useCondition,jdbcType=VARCHAR}, ",
        "#{createBy,jdbcType=BIGINT}, now(), ",
        "now(),1,#{extraInterestType,jdbcType=INTEGER},#{extraInterestDay,jdbcType=INTEGER})"
    })
    int insert(CouponTemplate record);

    int insertSelective(CouponTemplate record);

    @Select({
        "select",
        "id, name, coupon_type, amount, vaild_calc_type, web_scope, wap_scope, app_scope, amount_scope, days_scope, start_date, end_date, days, status, ",
        "use_condition, create_by, create_time, update_time, extra_interest_type,extra_interest_day ",
        "from mc_coupon_template",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("BaseResultMap")
    CouponTemplate selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CouponTemplate record);
    
    int updateByPrimaryKeyPartSelective(CouponTemplate record);

    @Update({
        "update mc_coupon_template",
        "set name = #{name,jdbcType=VARCHAR},",
          "coupon_type = #{couponType,jdbcType=INTEGER},",
          "amount = #{amount,jdbcType=DECIMAL},",
          "vaild_calc_type = #{vaildCalcType,jdbcType=INTEGER},",
          "web_scope = #{webScope,jdbcType=INTEGER},",
          "wap_scope = #{wapScope,jdbcType=INTEGER},",
          "app_scope = #{appScope,jdbcType=INTEGER},",
          "amount_scope = #{amountScope,jdbcType=DECIMAL},",
          "days_scope = #{daysScope,jdbcType=INTEGER},",
          "start_date = #{startDate,jdbcType=DATE},",
          "end_date = #{endDate,jdbcType=DATE},",
          "days = #{days,jdbcType=INTEGER},",
          "status = #{status,jdbcType=INTEGER},",
          "use_condition = #{useCondition,jdbcType=VARCHAR},",
          "update_time = #{updateTime,jdbcType=TIMESTAMP},",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(CouponTemplate record);

    Page<CouponTemplate> findByPage(Page<CouponTemplate> pageRequest, @Param("map") Map<String, Object> map);

    int batchDelete(@Param("ids") int[] ids);

    List<CouponTemplate> selectForPagin(@Param("map") Map<String, Object> map);

    int selectForPaginTotalCount(@Param("map") Map<String, Object> map);
    
    int deleteByCouponTemplateId(Long id);

	/**
	 * 获取所有用于人气值兑换的优惠券模板
	 * @param ids
	 * @return
	 */
	List<CouponTemplate> findExchangeCouponsByIds(@Param("ids") Long[] ids);

    /**
     * 更新模板排序时间
     * @param sorttime
     * @param id
     * @return
     */
    int updateSort(@Param("sorttime") Date sorttime,@Param("id") Long id);
}