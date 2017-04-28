package com.yourong.core.ic.dao;

import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.DebtInterest;
import com.yourong.core.ic.model.ProjectInterestBiz;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Repository
public interface DebtInterestMapper {
    @Delete({
        "delete from ic_debt_interest",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into ic_debt_interest (id, debt_id, project_id,",
        "start_date, end_date, unit_principal, ",
        "unit_interest, payable_interest, ",
        "payable_principal, real_pay_principal, ",
        "real_pay_interest, periods, status, ",
        "pay_time, create_time, ",
        "update_time,del_flag)",
        "values (#{id,jdbcType=BIGINT}, #{debtId,jdbcType=BIGINT},  #{projectId,jdbcType=BIGINT}, ",
        "#{startDate,jdbcType=DATE}, #{endDate,jdbcType=DATE}, #{unitPrincipal,jdbcType=DECIMAL}, ",
        "#{unitInterest,jdbcType=DECIMAL}, #{payableInterest,jdbcType=DECIMAL}, ",
        "#{payablePrincipal,jdbcType=DECIMAL}, #{realPayPrincipal,jdbcType=DECIMAL}, ",
        "#{realPayInterest,jdbcType=DECIMAL},#{periods,jdbcType=VARCHAR}, #{status,jdbcType=INTEGER}, ",
        "#{payTime,jdbcType=TIMESTAMP}, now(), ",
        "now(),1)"
    })
    int insert(DebtInterest record);

    int insertSelective(DebtInterest record);

    @Select({
        "select",
        "id, debt_id, project_id, start_date, end_date, unit_principal, unit_interest, payable_interest, ",
        "payable_principal, real_pay_principal, real_pay_interest, periods,status, pay_time, ",
        "create_time, update_time",
        "from ic_debt_interest",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("BaseResultMap")
    DebtInterest selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DebtInterest record);

    @Update({
        "update ic_debt_interest",
        "set debt_id = #{debtId,jdbcType=BIGINT},",
          "project_id = #{projectId,jdbcType=BIGINT},",
          "start_date = #{startDate,jdbcType=DATE},",
          "end_date = #{endDate,jdbcType=DATE},",
          "unit_principal = #{unitPrincipal,jdbcType=DECIMAL},",
          "unit_interest = #{unitInterest,jdbcType=DECIMAL},",
          "payable_interest = #{payableInterest,jdbcType=DECIMAL},",
          "payable_principal = #{payablePrincipal,jdbcType=DECIMAL},",
          "real_pay_principal = #{realPayPrincipal,jdbcType=DECIMAL},",
          "real_pay_interest = #{realPayInterest,jdbcType=DECIMAL},",
          "periods = #{periods,jdbcType=VARCHAR},",
          "status = #{status,jdbcType=INTEGER},",
          "pay_time = #{payTime,jdbcType=TIMESTAMP},",
          "create_time = #{createTime,jdbcType=TIMESTAMP},",
          "update_time = #{updateTime,jdbcType=TIMESTAMP}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(DebtInterest record);

    Page<DebtInterest> findByPage(Page<DebtInterest> pageRequest, @Param("map") Map<String, Object> map);

    int batchDelete(@Param("ids") int[] ids);

    List<DebtInterest> selectForPagin(@Param("map") Map<String, Object> map);

    int selectForPaginTotalCount(@Param("map") Map<String, Object> map);
    
    List<DebtInterest> findInterestsByDebtId(@Param("debtId")Long debtId);

    /**
     * 定时更新债权本息还本付息记录状态
     * @param afterStatus 
     * @param beforeStatus 
     * @return
     */
	int updateStatusForPayInterestAndPrincipal(@Param("beforeStatus")int beforeStatus, @Param("afterStatus")int afterStatus);
    
	/**
	 * 根据项目获取债权本息期数
	 * @param map
	 * @return
	 */
	int findPeriodsByProjectId(@Param("map") Map<String, Object> map);
	
	/**
	 * 根据项目id获取债权本息数据
	 * @param projectId
	 * @return
	 */
	List<DebtInterest> findDebtInterestByProjectId(@Param("projectId")Long projectId);

	List<ProjectInterestBiz> findRepayInterestForPagin(@Param("map") Map<String, Object> map);

	int selectForPaginInterestTotalCount(@Param("map") Map<String, Object> map);

	int findDPeriodsByProjectId(@Param("map") Map<String, Object> map);

	ProjectInterestBiz selectInterestInfoByPrimaryKey(Long id);
	
	List<DebtInterest> findInterestsByProjectId(@Param("projectId")Long projectId);
	/**
	 * 
	 * @Description:查询本息记录 默认排序
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年3月4日 上午11:45:49
	 */
	List<DebtInterest> findInterestlistByProjectId(@Param("projectId")Long projectId);
	/**
	 * 
	 * @Description:还款日期
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年1月27日 上午9:52:22
	 */
	DebtInterest selectOverdueDyasByProjectId(@Param("projectId")Long projectId);
	
	/**
	 * 
	 * @Description: 逾期本金和利息
	 * @param map
	 * @return
	 * @author: chaisen
	 * @time:2016年1月27日 上午9:55:50
	 */
	DebtInterest selectOverdueAmountByProjectId(@Param("map") Map<String, Object> map);
	/**
	 * 
	 * @Description:已支付本金和利息
	 * @param map
	 * @return
	 * @author: chaisen
	 * @time:2016年1月27日 下午3:12:59
	 */
	DebtInterest selectHadPayAmountByProjectId(@Param("map") Map<String, Object> map);
	/**
	 * 
	 * @Description:待支付本金和利息
	 * @param map
	 * @return
	 * @author: chaisen
	 * @time:2016年2月23日 下午2:31:50
	 */
	DebtInterest selectWaitPayAmountByBorrowerId(@Param("map") Map<String, Object> map);
	/**
	 * 
	 * @Description:统计本息支付情况
	 * @param map
	 * @return
	 * @author: chaisen
	 * @time:2016年1月27日 下午3:33:25
	 */
	int findInterestsByProjectIdTotalCount(@Param("map") Map<String, Object> map);
	/**
	 * 
	 * @Description:根据条件查询逾期的本金和利息
	 * @param map
	 * @return
	 * @author: chaisen
	 * @time:2016年1月28日 下午5:36:47
	 */
	ProjectInterestBiz selectOverduePayAmountByProjectId(@Param("map") Map<String, Object> map);
	/**
	 * 
	 * @Description:TODO
	 * @param map
	 * @return
	 * @author: chaisen
	 * @time:2016年2月1日 下午2:20:37
	 */
	List<ProjectInterestBiz> getOverdueInfoListByMap(@Param("map") Map<String, Object> map);
	/**
	 * 
	 * @Description: 结束时间小于当前时间并且离当前时间最近
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年2月16日 下午2:42:29
	 */
	DebtInterest selectMinTimeByProjectId(@Param("projectId")Long projectId);
	/**
	 * 
	 * @Description:结束时间大于当前时间并且离当前时间最近
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年2月16日 下午2:42:34
	 */
	DebtInterest selectMaxTimeByProjectId(@Param("projectId")Long projectId);
	
	/**
	 * @Description:根据项目id和结束时间获取项目本息记录
	 * @param projectId
	 * @param endDate
	 * @return
	 * @author: fuyili
	 * @time:2016年2月19日 下午3:40:05
	 */
	DebtInterest findDebtInterestByEndDateAndProjectId(@Param("projectId")Long projectId,@Param("endDate")Date endDate);



    /**
     * @Description:根据垫资记录状态为  未垫资 和 本息时间 ，状态  查询出 本息记录
     * @return
     * @author: py
     * @time:2016年2月22日 下午3:40:05
     */
    List<DebtInterest>  findUnderWriteDebtInterest();

	
	/**
	 * 
	 * @Description:根据borrowerId 获取待支付本金和利息
	 * @param borrowerId
	 * @return
	 * @author: chaisen
	 * @time:2016年2月23日 下午3:05:39
	 */
	DebtInterest selectNOPayAmountByBorrowerId(@Param("borrowerId")Long borrowerId);
	
	/**
	 * @Description:根据项目id获取项目本息记录 
	 * @param projectId
	 * @return
	 * @author: fuyili
	 * @time:2016年3月3日 下午4:58:45
	 */
	List<DebtInterest> findProjectInterestsByProjectId(@Param("projectId")Long projectId);
	/**
	 * 
	 * @Description:查询待支付
	 * @param borrowerId
	 * @return
	 * @author: chaisen
	 * @time:2016年3月9日 上午10:00:42
	 */
	DebtInterest selectWaitPayAmountByMemberId(@Param("borrowerId")Long borrowerId);
	
	
	 /**
     * @Description:根据项目获取债权本息状态
     * @return
     * @author: zhanghao
     * @time:2016年4月7日 上午9:40:05
     */
    DebtInterest  findStatusByProjectId(@Param("map") Map<String, Object> map);
    /**
     * 
     * @Description:统计逾期记录数
     * @param projectId
     * @return
     * @author: chaisen
     * @time:2016年5月26日 下午4:36:58
     */
    int findOverduesByProjectId(@Param("projectId")Long projectId);

	/**
	 * @Description:根据id更新每一期的项目本息为已还款
	 * @param id
	 * @param beforeStatus
	 * @param afterStatus
	 * @return
	 * @author: fuyili
	 * @time:2016年5月24日 下午3:04:46
	 */
	int updateStatusById(@Param("id") Long id,@Param("beforeStatus") int beforeStatus, @Param("afterStatus") int afterStatus);
	
	
	/**
	 * @Description:直投项目下未还款项目本息个数
	 * @param projectId
	 * @return
	 * @author: fuyili
	 * @time:2016年5月24日 下午10:51:13
	 */
	int getCountUnReturnByProjectId(@Param("projectId")Long projectId);
	
	/**
	 * @Description:更新已逾期的项目本息状态（未垫资且为直投项目）
	 * @return
	 * @author: fuyili
	 * @time:2016年5月31日 下午8:11:54
	 */
	int updateStatusForOverdueProjectInterest(@Param("id")Long id);
	
	/**
	 * @Description:查询已逾期的项目本息（未垫资且为直投项目）
	 * @return
	 * @author: fuyili
	 * @time:2016年6月1日 下午1:56:29
	 */
	List<DebtInterest> selectOverduedProjectInterest();
	/**
	 * 
	 * @Description:获取逾期记录
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年5月30日 下午4:10:32
	 */
	List<DebtInterest> findOverdueInterestsByProjectId(@Param("projectId")Long projectId);
	/**
	 * 
	 * @Description:逾期金额
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年5月31日 上午10:43:04
	 */
	DebtInterest selectOverdueInterestAmountByProjectId(@Param("projectId")Long projectId);
	
	/**
	 * 
	 * @Description:最早逾期记录
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年5月31日 上午10:59:01
	 */
	DebtInterest findOverdueDayByProjectId(@Param("projectId")Long projectId);
	/**
	 * 
	 * @Description:待支付本息记录
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年6月1日 上午9:38:20
	 */
	List<DebtInterest> findWaitPayInterestsByProjectId(@Param("projectId")Long projectId);
	/**
	 * 
	 * @Description实付本金和利息
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年6月1日 上午11:44:49
	 */
	DebtInterest selectRealPayAmountByProjectId(@Param("projectId")Long projectId);
	/**
	 * 
	 * @Description:待支付本金和利息
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年6月2日 下午7:17:02
	 */
	DebtInterest selectWaitPayPrincipalByProjectId(@Param("projectId")Long projectId);

	/**
	 * @Description:还本付息成功后项目本息实际支付值的更新
	 * @param interestId
	 * @param beforeStatus
	 * @param afterStatus
	 * @param realPayInterest
	 * @param realPayPrincipal
	 * @return
	 * @author: fuyili
	 * @time:2016年6月18日 下午5:28:24
	 */
	int updateRealPayForPrincipalAndInterestSuccess(@Param("id") Long id,@Param("realPayInterest") BigDecimal realPayInterest, @Param("realPayPrincipal") BigDecimal realPayPrincipal);
	/**
	 * 
	 * @Description:普通逾期金额
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年6月22日 上午9:43:23
	 */
	DebtInterest selectCommonOverdueByProjectId(@Param("projectId")Long projectId);
}