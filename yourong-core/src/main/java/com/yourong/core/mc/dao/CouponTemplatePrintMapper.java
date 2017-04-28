package com.yourong.core.mc.dao;

import com.yourong.common.pageable.Page;
import com.yourong.core.mc.model.CouponTemplatePrint;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface CouponTemplatePrintMapper {
    @Delete({
        "delete from mc_coupon_template_print",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into mc_coupon_template_print (id, coupon_template_id, ",
        "print_time, print_num, ",
        "received_num, used_num,total_amount, ",
        "create_by, remarks, ",
        "update_time)",
        "values (#{id,jdbcType=BIGINT}, #{couponTemplateId,jdbcType=BIGINT}, ",
        "now(), #{printNum,jdbcType=INTEGER}, ",
        "#{receivedNum,jdbcType=INTEGER},#{usedNum,jdbcType=INTEGER}, #{totalAmount,jdbcType=DECIMAL}, ",
        "#{createBy,jdbcType=BIGINT}, #{remarks,jdbcType=VARCHAR}, ",
        "now())"
    })
    int insert(CouponTemplatePrint record);

    int insertSelective(CouponTemplatePrint record);

    @Select({
        "select",
        "id, coupon_template_id, print_time, print_num,received_num,used_num,total_amount, create_by, ",
        "remarks, update_time",
        "from mc_coupon_template_print",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("BaseResultMap")
    CouponTemplatePrint selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CouponTemplatePrint record);

    @Update({
        "update mc_coupon_template_print",
        "set coupon_template_id = #{couponTemplateId,jdbcType=BIGINT},",
          "print_time = #{printTime,jdbcType=TIMESTAMP},",
          "print_num = #{printNum,jdbcType=INTEGER},",
          "total_amount = #{totalAmount,jdbcType=DECIMAL},",
          "create_by = #{createBy,jdbcType=BIGINT},",
          "remarks = #{remarks,jdbcType=VARCHAR},",
          "update_time = #{updateTime,jdbcType=TIMESTAMP}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(CouponTemplatePrint record);

    Page<CouponTemplatePrint> findByPage(Page<CouponTemplatePrint> pageRequest, @Param("map") Map<String, Object> map);

    int batchDelete(@Param("ids") int[] ids);

    List<CouponTemplatePrint> selectForPagin(@Param("map") Map<String, Object> map);

    int selectForPaginTotalCount(@Param("map") Map<String, Object> map);
    
    int updateCouponReceiveNum(@Param("id")Long id);
    
    /**
     * 根据优惠券模板ID查询印刷数据
     * @param couponTemplateId
     * @return
     */
    CouponTemplatePrint selectByTemplateId(Long couponTemplateId);
    
    /**
     * 根据优惠券模板ID查询优惠券使用信息
     * @param couponTemplateId
     * @return
     */
    CouponTemplatePrint selectInforByTemplateId(Long couponTemplateId);
    
    
    
    
    
    
    
}