package com.yourong.core.ic.dao;

import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.DebtPledge;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface DebtPledgeMapper {
    @Delete({
        "delete from ic_debt_pledge",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into ic_debt_pledge (id, debt_id, ",
        "pledge_type, pledge_details, ",
        "pledge_verify, remarks, pledge_valuation, ",
        "create_time, update_time,del_flag)",
        "values (#{id,jdbcType=BIGINT}, #{debtId,jdbcType=BIGINT}, ",
        "#{pledgeType,jdbcType=VARCHAR}, #{pledgeDetails,jdbcType=VARCHAR}, ",
        "#{pledgeVerify,jdbcType=VARCHAR}, #{remarks,jdbcType=VARCHAR}, #{pledgeValuation,jdbcType=DOUBLE},",
        "now(), now(),1)"
    })
    int insert(DebtPledge record);

    int insertSelective(DebtPledge record);

    @Select({
        "select",
        "id, debt_id, pledge_type, pledge_details, pledge_verify, remarks, create_time, ",
        "update_time,del_flag",
        "from ic_debt_pledge",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("BaseResultMap")
    DebtPledge selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DebtPledge record);

    @Update({
        "update ic_debt_pledge",
        "set debt_id = #{debtId,jdbcType=BIGINT},",
          "pledge_type = #{pledgeType,jdbcType=VARCHAR},",
          "pledge_details = #{pledgeDetails,jdbcType=VARCHAR},",
          "pledge_verify = #{pledgeVerify,jdbcType=VARCHAR},",
          "remarks = #{remarks,jdbcType=VARCHAR},",
          "update_time = now()",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(DebtPledge record);

    Page<DebtPledge> findByPage(Page<DebtPledge> pageRequest, @Param("map") Map<String, Object> map);

    int batchDelete(@Param("ids") int[] ids);

    List<DebtPledge> selectForPagin(@Param("map") Map<String, Object> map);

    int selectForPaginTotalCount(@Param("map") Map<String, Object> map);
    
    //根据债权id获取质押物信息 
    DebtPledge findPledgeByDebtId(@Param("debtId")Long id);
    
    //根据债权id删除质押物信息 
    int deletePledgeByDebtId(Long debtId);
}