package com.yourong.core.ic.dao;

import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.DebtTransfer;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface DebtTransferMapper {
    @Delete({
        "delete from ic_debt_transfer",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into ic_debt_transfer (id, debt_id, ",
        "owner_id, amount, ",
        "start_date, end_date, transfer_time, ",
        "annualized_rate, create_time)",
        "values (#{id,jdbcType=BIGINT}, #{debtId,jdbcType=BIGINT}, ",
        "#{ownerId,jdbcType=BIGINT}, #{amount,jdbcType=DECIMAL}, ",
        "#{startDate,jdbcType=DATE}, #{endDate,jdbcType=DATE}, #{transferTime,jdbcType=TIMESTAMP}, ",
        "#{annualizedRate,jdbcType=DECIMAL}, #{createTime,jdbcType=TIMESTAMP})"
    })
    int insert(DebtTransfer record);

    int insertSelective(DebtTransfer record);

    @Select({
        "select",
        "id, debt_id, owner_id, amount, start_date, end_date, transfer_time, annualized_rate, ",
        "create_time",
        "from ic_debt_transfer",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("BaseResultMap")
    DebtTransfer selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DebtTransfer record);

    @Update({
        "update ic_debt_transfer",
        "set debt_id = #{debtId,jdbcType=BIGINT},",
          "owner_id = #{ownerId,jdbcType=BIGINT},",
          "amount = #{amount,jdbcType=DECIMAL},",
          "start_date = #{startDate,jdbcType=DATE},",
          "end_date = #{endDate,jdbcType=DATE},",
          "transfer_time = #{transferTime,jdbcType=TIMESTAMP},",
          "annualized_rate = #{annualizedRate,jdbcType=DECIMAL},",
          "create_time = #{createTime,jdbcType=TIMESTAMP}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(DebtTransfer record);

    Page<DebtTransfer> findByPage(Page<DebtTransfer> pageRequest, @Param("map") Map<String, Object> map);

    int batchDelete(@Param("ids") int[] ids);

    List<DebtTransfer> selectForPagin(@Param("map") Map<String, Object> map);

    int selectForPaginTotalCount(@Param("map") Map<String, Object> map);
}