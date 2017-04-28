package com.yourong.core.fin.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.yourong.common.pageable.Page;
import com.yourong.core.fin.model.WithdrawLog;

public interface WithdrawLogMapper {
    @Delete({
        "delete from fin_withdraw_log",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into fin_withdraw_log (id, withdraw_amount, ",
        "arrived_amount, withdraw_time, ",
        "bank_card_id, member_id, ",
        "fee, notice, status, withdraw_source,",
        "withdraw_no, update_time,withdraw_fee)",
        "values (#{id,jdbcType=BIGINT}, #{withdrawAmount,jdbcType=DECIMAL}, ",
        "#{arrivedAmount,jdbcType=DECIMAL}, #{withdrawTime,jdbcType=TIMESTAMP}, ",
        "#{bankCardId,jdbcType=BIGINT}, #{memberId,jdbcType=BIGINT}, ",
        "#{fee,jdbcType=DECIMAL}, #{notice,jdbcType=VARCHAR}, #{status,jdbcType=INTEGER},#{withdrawSource,jdbcType=INTEGER}, ",
        "#{withdrawNo,jdbcType=VARCHAR}, #{updateTime,jdbcType=TIMESTAMP},#{withdrawFee,jdbcType=INTEGER})"
    })
    int insert(WithdrawLog record);

    int insertSelective(WithdrawLog record);

    @Select({
        "select",
        "id,outer_withdraw_no, withdraw_amount, arrived_amount, withdraw_time, bank_card_id, member_id, ",
        "fee, notice, status, withdraw_no,withdraw_source, update_time,withdraw_fee",
        "from fin_withdraw_log",
        "where outer_withdraw_no = #{outerWithdrawNo,jdbcType=VARCHAR}"
    })
    @ResultMap("BaseResultMap")
    WithdrawLog selectByOuterWithdrawNo(String outerWithdrawNo);

    
    @Select({
        "select",
        "id,outer_withdraw_no, withdraw_amount, arrived_amount, withdraw_time, bank_card_id, member_id, ",
        "fee, notice, status, withdraw_no, withdraw_source,update_time,withdraw_fee",
        "from fin_withdraw_log",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("BaseResultMap")
    WithdrawLog selectByPrimaryKey(Long id);


    @Select({
            "select",
            "id,outer_withdraw_no, withdraw_amount, arrived_amount, withdraw_time, bank_card_id, member_id, ",
            "fee, notice, status, withdraw_no, withdraw_source,update_time,withdraw_fee",
            "from fin_withdraw_log",
            "where id = #{id,jdbcType=BIGINT}  for update"
    })
    @ResultMap("BaseResultMap")
    WithdrawLog selectByPrimaryKeyForLock(Long id);

    

    @Select({
            "select",
            "id, withdraw_amount, arrived_amount, withdraw_time, bank_card_id, member_id, ",
            "fee, notice, status, withdraw_no,outer_withdraw_no,withdraw_source, update_time,withdraw_fee",
            "from fin_withdraw_log",
            "where withdraw_no = #{withdrawNo,jdbcType=VARCHAR}"
    })
    @ResultMap("BaseResultMap")
    WithdrawLog selectByWithdrawNo(String  withdrawNo);
    
    
    
    
    @Select({
        "select count(1)",       
        "from fin_withdraw_log",
        "where member_id = #{memberId,jdbcType=BIGINT} and status = 5"
    })
    int countWithdrawNo(Long  memberId);
    
    @Select({
        "select sum(arrived_amount)",       
        "from fin_withdraw_log",
        "where member_id = #{memberId,jdbcType=BIGINT} and status = 5"
    })
    BigDecimal totalWithDraw(long memberId);
    
    



    int updateByPrimaryKeySelective(WithdrawLog record);

    @Update({
        "update fin_withdraw_log",
        "set withdraw_amount = #{withdrawAmount,jdbcType=DECIMAL},",
          "arrived_amount = #{arrivedAmount,jdbcType=DECIMAL},",
          "withdraw_time = #{withdrawTime,jdbcType=TIMESTAMP},",
          "bank_card_id = #{bankCardId,jdbcType=BIGINT},",
          "member_id = #{memberId,jdbcType=BIGINT},",
          "fee = #{fee,jdbcType=DECIMAL},",
          "notice = #{notice,jdbcType=VARCHAR},",
          "status = #{status,jdbcType=INTEGER},",
            "withdraw_source = #{withdrawSource,jdbcType=INTEGER},",
          "withdraw_no = #{withdrawNo,jdbcType=VARCHAR},",
          "update_time = #{updateTime,jdbcType=TIMESTAMP},",
          "withdraw_fee = #{withdrawFee,jdbcType=INTEGER}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(WithdrawLog record);
    
    @Update({
        "update fin_withdraw_log",
        "set ",          
          "status = #{status,jdbcType=INTEGER},",        
          "update_time = now()",
        "where withdraw_no = #{withdrawNo,jdbcType=VARCHAR}"
    })
    int updateBywithdrawNo(String withdrawNo,int status);


    @Update({
            "update fin_withdraw_log",
            "set ",
            "process_time = now()",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateProssTimeById(@Param("id")Long id);

    @Update({
            "update fin_withdraw_log",
            "set ",
            "status = #{status,jdbcType=INTEGER},",
            "arrived_amount = #{arrivedAmount,jdbcType=DECIMAL},",
            "outer_withdraw_no = #{outerWithdrawNo,jdbcType=VARCHAR},",
            "notice = #{notice,jdbcType=VARCHAR},",
            "update_time = now()",
            "where id = #{id,jdbcType=BIGINT}   and  status =  #{eqStatus,jdbcType=INTEGER}"
    })
    int updateStateByID(@Param("id")Long id, @Param("status")int status,@Param("eqStatus")int eqStatus,@Param("outerWithdrawNo") String outerWithdrawNo
    		,@Param("notice") String notice,@Param("arrivedAmount")BigDecimal withAmout);

    @Update({
        "update fin_withdraw_log",
        "set ",          
          "status = #{status,jdbcType=INTEGER},", 
          "notice = #{notice,jdbcType=VARCHAR},",
          "update_time = now(),",
          "process_time = now()",
          "where id = #{id,jdbcType=BIGINT} and status = #{eqStatus,jdbcType=INTEGER}"
    })
    int updateStateByParimarkey(@Param("id")Long id, @Param("status")int status, @Param("eqStatus")int eqStatus,  @Param("notice") String notice);
       

    Page<WithdrawLog> findByPage(Page<WithdrawLog> pageRequest, @Param("map") Map<String, Object> map);
   

    List<WithdrawLog> selectForPagin(@Param("map") Map<String, Object> map);

    int selectForPaginTotalCount(@Param("map") Map<String, Object> map);
    
    /**
     * 网站前台分页
     * @param map
     * @return
     * 
     */
    List<WithdrawLog> selectForPaginWeb(@Param("map") Map<String, Object> map);
    /**
     * 网站前台分页
     * @param map
     * @return
     * 
     */
    int selectForPaginTotalCountWeb(@Param("map") Map<String, Object> map);

    @Select({
            "select 1",
            "from fin_withdraw_log",
            "where member_id = #{memberID,jdbcType=BIGINT}  and  bank_card_id = #{cardID,jdbcType=BIGINT}   and status >= 0 and  status < 5  limit 1 "
    })
    Integer  cardIsWithDrawIng(@Param("memberID") Long memberID, @Param("cardID") Long cardID);


	List<WithdrawLog> selectSynchronizedWithdraws();
	
	
	int countWithDrawFree(@Param("memberId") Long memberId,@Param("withdrawTime") String withdrawTime, @Param("remarks") String remarks);
}