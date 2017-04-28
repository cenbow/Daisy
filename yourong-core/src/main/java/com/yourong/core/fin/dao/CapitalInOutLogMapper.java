package com.yourong.core.fin.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.yourong.common.pageable.Page;
import com.yourong.core.fin.model.CapitalInOutLog;
import com.yourong.core.fin.model.biz.BonusBiz;
import com.yourong.core.fin.model.biz.CapitalInOutForMemberCenter;
import com.yourong.core.fin.model.query.CapitalInOutLogQuery;
import com.yourong.core.fin.model.query.CapitalQuery;

@Repository
public interface CapitalInOutLogMapper {
   
//    @Insert({
//        "insert into fin_capital_in_out_log (id, member_id, ",
//        "pay_account_type, balance, ",
//        "type, income, outlay, ",
//        "source_id, remark, ",
//        "happen_time)",
//        "values (#{id,jdbcType=INTEGER}, #{memberId,jdbcType=BIGINT}, ",
//        "#{payAccountType,jdbcType=INTEGER}, #{balance,jdbcType=DECIMAL}, ",
//        "#{type,jdbcType=INTEGER}, #{income,jdbcType=DECIMAL}, #{outlay,jdbcType=DECIMAL}, ",
//        "#{sourceId,jdbcType=VARCHAR}, #{remark,jdbcType=VARCHAR}, ",
//        "now())"
//    })
//    int insert(CapitalInOutLog record);

    int insertSelective(CapitalInOutLog record);

    @Select({
        "select",
        "id, member_id, pay_account_type, balance, type, income, outlay, source_id, remark, ",
        "happen_time",
        "from fin_capital_in_out_log",
        "where id = #{id,jdbcType=INTEGER}"
    })
    @ResultMap("BaseResultMap")
    CapitalInOutLog selectByPrimaryKey(Integer id);
    
    CapitalInOutLog selectByHappenTime(
    		@Param("memberId") Long memberId,
    		@Param("payAccountType") int payAccountType,
    		@Param("type") int type,
    		@Param("startTime") Date startTime,
    		@Param("endTime") Date endTime
    		);
    
    
    @Select({
        "select",
        "id, member_id, pay_account_type, balance, type, income, outlay, source_id, remark, ",
        "happen_time",
        "from fin_capital_in_out_log",
        "where member_id = #{memberId,jdbcType=BIGINT} ",
        " and pay_account_type  = #{payAccountType,jdbcType=INTEGER} ",
        " and type  = #{type,jdbcType=INTEGER} ORDER BY happen_time  DESC  limit 0,#{length,jdbcType=INTEGER}"
        
    })
    @ResultMap("BaseResultMap")
    List<CapitalInOutLog> selectEaring(
    		@Param("memberId") Long memberId,
    		@Param("payAccountType") int payAccountType,
    		@Param("type") int type,
    		@Param("length") int length    		
    );
    

    int updateByPrimaryKeySelective(CapitalInOutLog record);

    @Update({
        "update fin_capital_in_out_log",
        "set member_id = #{memberId,jdbcType=BIGINT},",
          "pay_account_type = #{payAccountType,jdbcType=INTEGER},",
          "balance = #{balance,jdbcType=DECIMAL},",
          "type = #{type,jdbcType=INTEGER},",
          "income = #{income,jdbcType=DECIMAL},",
          "outlay = #{outlay,jdbcType=DECIMAL},",
          "source_id = #{sourceId,jdbcType=VARCHAR},",
          "remark = #{remark,jdbcType=VARCHAR},",
          "happen_time = #{happenTime,jdbcType=TIMESTAMP}",
        "where id = #{id,jdbcType=INTEGER}"
    })
    int updateByPrimaryKey(CapitalInOutLog record);

    Page findByPage(Page pageRequest, @Param("map") Map map);   

    List selectForPagin(@Param("map") Map map);

    int selectForPaginTotalCount(@Param("map") Map map);

    /**
     * 查询存钱罐收益
     * @param query
     * @return
     */
	List<BonusBiz> selectBonusByQuery(CapitalInOutLogQuery query);
	
	/**
	 * 后台资金流水查询
	 * @param pageRequest
	 * @param map
	 * @return
	 */
	Page<CapitalInOutLog> queryLogByPage(Page<CapitalInOutLog> pageRequest, @Param("map") Map<String, Object> map);
	
	/**
	 * 我的账户资金流水分页数据
	 * @param capitalQuery
	 * @return
	 */
	List<CapitalInOutForMemberCenter> selectForPaginCapitalInOutLog(@Param("capitalQuery")CapitalQuery capitalQuery);
	
	/**
	 * 我的账户资金流水分页数据剔除P2P数据
	 * @param capitalQuery
	 * @return
	 */
	List<CapitalInOutForMemberCenter> p2pSelectForPaginCapitalInOutLog(@Param("capitalQuery")CapitalQuery capitalQuery);
	
	/**
	 * 我的账户资金流水数据总数剔除P2P数据
	 * @param capitalQuery
	 * @return
	 */
	int p2pSelectForPaginCapitalInOutLogCount(@Param("capitalQuery")CapitalQuery capitalQuery);
	
	/**
	 * 我的账户资金流水数据总数
	 * @param capitalQuery
	 * @return
	 */
	int selectForPaginCapitalInOutLogCount(@Param("capitalQuery")CapitalQuery capitalQuery);
	
	/**
	 * 出借人资金流水
	 * @param map
	 * @return
	 */
	List<CapitalInOutLog> queryLenderLogByPage( @Param("map") Map<String, Object> map);
	
	/**
	 * 出借人资金流水数量
	 * @param map
	 * @return
	 */
	int queryLenderLogByPageCount(@Param("map") Map<String, Object> map);
	
	/**
	 * 资金流水列表
	 * @param pageRequest
	 * @param map
	 * @return
	 */
	List<CapitalInOutLog> selectFinCapitalInOutLogList(@Param("map") Map map);
	/**
	 * 资金流水列表数据数量
	 * @param pageRequest
	 * @param map
	 * @return
	 */
	int selectFinCapitalInOutLogListCount(@Param("map") Map map);

	BigDecimal queryTransferAmountByMemberId(@Param("memberId") Long  memberId);
	
	int queryTransferAmountByMemberIdNum(@Param("memberId") Long  memberId);

	List<CapitalInOutLog> getTotalTransferAmountForMemberTransfer(
			@Param("query")CapitalInOutLogQuery query);
	
}