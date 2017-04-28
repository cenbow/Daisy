package com.yourong.core.fin.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.yourong.common.pageable.Page;
import com.yourong.core.fin.model.RechargeLog;

@Repository
public interface RechargeLogMapper {

    @Insert({
            "insert into fin_recharge_log (id, amount, ",
            "recharge_time, member_id, ",
            "pay_method, remarks, ",
            "bank_code, trade_no, ",
            "out_trade_no, bank_card_id,status, ",
            "update_time,recharge_source)",
            "values (#{id,jdbcType=BIGINT}, #{amount,jdbcType=DECIMAL}, ",
            "#{rechargeTime,jdbcType=TIMESTAMP}, #{memberId,jdbcType=BIGINT}, ",
            "#{payMethod,jdbcType=INTEGER}, #{remarks,jdbcType=VARCHAR}, ",
            "#{bankCode,jdbcType=VARCHAR}, #{tradeNo,jdbcType=VARCHAR}, ",
            "#{outTradeNo,jdbcType=VARCHAR}, #{bankCardId,jdbcType=BIGINT},  ",
            "#{status,jdbcType=INTEGER},#{updateTime,jdbcType=TIMESTAMP},#{rechargeSource,jdbcType=INTEGER})"
    })
    int insert(RechargeLog record);

    int insertSelective(RechargeLog record);

    @Select({
            "select",
            "id, amount, recharge_time, member_id, pay_method, remarks, bank_code, recharge_no, ",
            "outer_recharge_no,bank_card_id, status, update_time,recharge_source",
            "from fin_recharge_log",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("BaseResultMap")
    RechargeLog selectByPrimaryKey(Long id);
    
    @Select({
    	"select",
    	"id, amount, recharge_time, member_id, pay_method, remarks, bank_code, recharge_no, ",
    	"outer_recharge_no,bank_card_id, status, update_time,recharge_source",
    	"from fin_recharge_log",
    	"where id = #{id,jdbcType=BIGINT} for update"
    })
    @ResultMap("BaseResultMap")
    RechargeLog selectByPrimaryKeyForLock(Long id);


    @Select({
            "select",
            "id, amount, recharge_time, member_id, pay_method, remarks, bank_code, recharge_no, type, ",
			"outer_recharge_no,bank_card_id,status, update_time, order_no,recharge_source, payer_ip ",
            "from fin_recharge_log",
            "where recharge_no = #{rechargeNo,jdbcType=VARCHAR}"
    })
    @ResultMap("BaseResultMap")
    RechargeLog selectRechargeLogByTradeNo(@Param("rechargeNo") String  rechargeNo);


    int updateByPrimaryKeySelective(RechargeLog record);

    @Update({
            "update fin_recharge_log",
            "set amount = #{amount,jdbcType=DECIMAL},",
            "recharge_time = #{rechargeTime,jdbcType=TIMESTAMP},",
            "member_id = #{memberId,jdbcType=BIGINT},",
            "pay_method = #{payMethod,jdbcType=INTEGER},",
            "remarks = #{remarks,jdbcType=VARCHAR},",
            "bank_code = #{bankCode,jdbcType=VARCHAR},",
            "trade_no = #{tradeNo,jdbcType=VARCHAR},",
            "out_trade_no = #{outTradeNo,jdbcType=VARCHAR},",
            "bank_card_id = #{bankCardId,jdbcType=BIGINT},",
            "status = #{status,jdbcType=INTEGER},",
            "update_time = #{updateTime,jdbcType=TIMESTAMP}",
            "recharge_source = #{rechargeSource,jdbcType=INTEGER}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(RechargeLog record);
    
  
    @Select({
        "select",
        "count(1) ",
        "from fin_recharge_log",
        "where member_id = #{memberId,jdbcType=BIGINT} and status =5"
     })    
	 int countRecharge(long memberId);
    
    
    @Select({
        "select",
        "sum(amount)",
        "from fin_recharge_log",
        "where member_id = #{memberId,jdbcType=BIGINT} and status =5"
     })	
	 BigDecimal totalRecharge(long memberId) ;
    
    


    @Update({
            "update fin_recharge_log   set ",
            "status = #{status,jdbcType=INTEGER},",
            "notice = #{notice,jdbcType=VARCHAR},",
            "update_time = now()",          
            "where id = #{id,jdbcType=BIGINT} and status = #{eqStatus,jdbcType=INTEGER} "
    })
    int updateStateByID(@Param("id") long id, @Param("status") int status,@Param("eqStatus")int eqStatus, @Param("notice") String notice);


    @Update({
            "update fin_recharge_log",
            "set amount = #{amount,jdbcType=DECIMAL},",
            "status = #{status,jdbcType=INTEGER},",
            "outer_recharge_no = #{outerRechargeNo,jdbcType=VARCHAR},",
            "update_time = now()",
            "where recharge_no = #{rechargeNo,jdbcType=VARCHAR}"
    })
    int updateStateByOutTradeNo(@Param("amount") BigDecimal amount, @Param("status") int status, @Param("rechargeNo") String rechargeNo , @Param("outerRechargeNo") String outerRechargeNo);

    Page<RechargeLog> findByPage(Page<RechargeLog> pageRequest, @Param("map") Map<String, Object> map);

    List<RechargeLog> selectForPagin(@Param("map") Map<String, Object> map);

    int selectForPaginTotalCount(@Param("map") Map<String, Object> map);

    List<RechargeLog> selectRechargeByMap(@Param("map") Map<String, Object> map);

    List<RechargeLog> queryLogByPage(@Param("map") Map<String, Object> map);
    
    int queryLogByPageCount(@Param("map") Map<String, Object> map);
    
    /**
     * 查询用户首次在APP直接充值记录
     * @param memberId
     * @param startDate
     * @param endDate
     * @return
     */
    RechargeLog queryAppFirstRechargeAmount(@Param("memberId")Long memberId, @Param("startDate")Date startDate, @Param("endDate")Date endDate);
    
    /**
     *  查询充值记录
     * @param status 状态
     * @param rechargeNo 充值交易号
     * @param startTime
     * @param endTime
     * @return
     */
    List<RechargeLog> selectSynchronizedRecharge(@Param("status")Integer status, @Param("rechargeNo")String rechargeNo, 
    					@Param("startTime")String startTime, @Param("endTime")String endTime);
    
}