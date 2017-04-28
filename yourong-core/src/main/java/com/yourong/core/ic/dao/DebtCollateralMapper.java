package com.yourong.core.ic.dao;

import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.DebtCollateral;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface DebtCollateralMapper {
    @Delete({
        "delete from ic_debt_collateral",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into ic_debt_collateral (debt_id, ",
        "collateral_type, collateral_details, ",
        "collateral_verify, remarks, collateral_valuation ,",
        "create_time, update_time,del_flag,debt_type,project_id)",
        "values (#{debtId,jdbcType=BIGINT}, ",
        "#{collateralType,jdbcType=VARCHAR}, #{collateralDetails,jdbcType=VARCHAR}, ",
        "#{collateralVerify,jdbcType=VARCHAR}, #{remarks,jdbcType=VARCHAR}, #{collateralValuation,jdbcType=VARCHAR},",
        "now(), now(),1,#{debtType,jdbcType=VARCHAR}, #{projectId,jdbcType=BIGINT})"
    })
    int insert(DebtCollateral record);

    int insertSelective(DebtCollateral record);

    @Select({
        "select",
        "id, debt_id, collateral_type, collateral_details, collateral_verify, remarks, ",
        "create_time, update_time",
        "from ic_debt_collateral",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("BaseResultMap")
    DebtCollateral selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DebtCollateral record);

    @Update({
        "update ic_debt_collateral",
        "set debt_id = #{debtId,jdbcType=BIGINT},",
          "collateral_type = #{collateralType,jdbcType=VARCHAR},",
          "collateral_details = #{collateralDetails,jdbcType=VARCHAR},",
          "collateral_verify = #{collateralVerify,jdbcType=VARCHAR},",
          "remarks = #{remarks,jdbcType=VARCHAR},",
          "create_time = #{createTime,jdbcType=TIMESTAMP},",
          "update_time = #{updateTime,jdbcType=TIMESTAMP}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(DebtCollateral record);

    Page<DebtCollateral> findByPage(Page<DebtCollateral> pageRequest, @Param("map") Map<String, Object> map);

    int batchDelete(@Param("ids") int[] ids);

    List<DebtCollateral> selectForPagin(@Param("map") Map<String, Object> map);

    int selectForPaginTotalCount(@Param("map") Map<String, Object> map);
    
    DebtCollateral findCollateralByDebtId(@Param("debtId")Long debtId);
    
    int deleteCollateralByDebtId(@Param("debtId")Long debtId);
    
    DebtCollateral findCollateralByProjectId(@Param("projectId")Long projectId);
}