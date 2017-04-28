package com.yourong.core.fin.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.yourong.common.pageable.Page;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.fin.model.query.BalanceQuery;

@Repository
public interface BalanceMapper {


    @Insert({
        "insert into fin_balance ( balance_type, ",
        "balance, available_balance, ",
        "source_id, create_time",
        ")",
        "values (#{balanceType,jdbcType=INTEGER}, ",
        "#{balance,jdbcType=DECIMAL}, #{availableBalance,jdbcType=DECIMAL}, ",
        "#{sourceId,jdbcType=BIGINT}, now() ",
        "})"
    })
    int insert(Balance record);

    int insertSelective(Balance record);

    @Select({
        "select",
        "id, balance_type, balance, available_balance,source_id, create_time, update_time",
        "from fin_balance",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("BaseResultMap")
    Balance selectByPrimaryKey(Long id);
    
    
    @Select({
        "select",
        "id, balance_type, balance, available_balance, source_id, create_time, update_time",
        "from fin_balance",
        "where id = #{id,jdbcType=BIGINT} for update "
    })
    @ResultMap("BaseResultMap")
    Balance selectByPrimaryKeyLock(Long id);
    
    
    
    

    int updateByPrimaryKeySelective(Balance record);

    @Update({
        "update fin_balance",
        "set",
          "balance =  balance +#{balance,jdbcType=DECIMAL},",
          "available_balance = balance +  #{availableBalance,jdbcType=DECIMAL},",              
          "update_time = now()",
        "where source_id = #{sourceId,jdbcType=BIGINT} and balance_type = #{balanceType,jdbcType=INTEGER}"
    })
    int updateByTypeAndSource(Balance record);
    
    
    @Update({
        "update fin_balance",
        "set",
          "balance =  #{balance,jdbcType=DECIMAL},",
          "available_balance = #{availableBalance,jdbcType=DECIMAL},",              
          "update_time = now()",
        "where id = #{id,jdbcType=BIGINT} and source_id = #{sourceId,jdbcType=BIGINT} and balance_type = #{balanceType,jdbcType=INTEGER}"
    })
    int updateBalanceByTypeAndSource(Balance record);
    

    
    
    @Update({
        "update fin_balance",
        "set balance_type = #{balanceType,jdbcType=INTEGER},",
          "balance = #{balance,jdbcType=DECIMAL},",
          "available_balance = #{availableBalance,jdbcType=DECIMAL},",               
          "source_id = #{sourceId,jdbcType=BIGINT},",         
          "update_time = now()",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(Balance record);
    

    Page<Balance> findByPage(Page<Balance> pageRequest, @Param("map") Map<String, Object> map);

    Balance selectByQuery(BalanceQuery balanceQuery);

    List<Balance> selectForPagin(@Param("map") Map<String, Object> map);

    int selectForPaginTotalCount(@Param("map") Map<String, Object> map);

    int updatePlatformTotalPoint(Balance record);

    int updateByIdAndTypeForLotty(@Param("point")BigDecimal point, @Param("sourceId")Long sourceId,     @Param("balanceType") int balanceType);

    /**
	 * 查询当前人气值大于特定值的用户
	 * 
	 * @author wangyanji
	 * @param maxNum
	 * @return
	 */
	List<Long> selectMemberPopularity(@Param("map") Map<String, Object> map);
    
}