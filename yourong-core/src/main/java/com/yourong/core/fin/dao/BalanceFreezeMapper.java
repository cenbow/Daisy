package com.yourong.core.fin.dao;

import com.yourong.common.pageable.Page;
import com.yourong.core.fin.model.BalanceFreeze;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface BalanceFreezeMapper {
    @Delete({
        "delete from fin_balance_freeze",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into fin_balance_freeze (id, freeze_no, ",
        "unfreeze_no, member_id, ",
        "type, amount, balance, ",
        "summary, remarks, ",
        "freeze_time, unfreeze_time)",
        "values (#{id,jdbcType=BIGINT}, #{freezeNo,jdbcType=VARCHAR}, ",
        "#{unfreezeNo,jdbcType=VARCHAR}, #{memberId,jdbcType=BIGINT}, ",
        "#{type,jdbcType=INTEGER}, #{amount,jdbcType=DECIMAL}, #{balance,jdbcType=DECIMAL}, ",
        "#{summary,jdbcType=VARCHAR}, #{remarks,jdbcType=VARCHAR}, ",
        "#{freezeTime,jdbcType=TIMESTAMP}, #{unfreezeTime,jdbcType=TIMESTAMP})"
    })
    int insert(BalanceFreeze record);

    int insertSelective(BalanceFreeze record);

    @Select({
        "select",
        "id, freeze_no, unfreeze_no, member_id, type, amount, balance, summary, remarks, ",
        "freeze_time, unfreeze_time",
        "from fin_balance_freeze",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("BaseResultMap")
    BalanceFreeze selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BalanceFreeze record);

    @Update({
        "update fin_balance_freeze",
        "set freeze_no = #{freezeNo,jdbcType=VARCHAR},",
          "unfreeze_no = #{unfreezeNo,jdbcType=VARCHAR},",
          "member_id = #{memberId,jdbcType=BIGINT},",
          "type = #{type,jdbcType=INTEGER},",
          "amount = #{amount,jdbcType=DECIMAL},",
          "balance = #{balance,jdbcType=DECIMAL},",
          "summary = #{summary,jdbcType=VARCHAR},",
          "remarks = #{remarks,jdbcType=VARCHAR},",
          "freeze_time = #{freezeTime,jdbcType=TIMESTAMP},",
          "unfreeze_time = #{unfreezeTime,jdbcType=TIMESTAMP}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(BalanceFreeze record);

    Page findByPage(Page pageRequest, @Param("map") Map map);

    int batchDelete(@Param("ids") int[] ids);

    List selectForPagin(@Param("map") Map map);

    int selectForPaginTotalCount(@Param("map") Map map);
}