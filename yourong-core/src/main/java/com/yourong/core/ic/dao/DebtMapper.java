package com.yourong.core.ic.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.Debt;

public interface DebtMapper {
    @Delete({
        "delete from ic_debt",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into ic_debt (id, serial_number, ",
        "original_debt_number, debt_type, borrower_type, lender_type, guaranty_type, ",
        "amount, ",
        "return_type, start_date, ",
        "end_date, borrower_id, ",
        "annualized_rate,offline_annualized_rate,",
        "fee_rate, ",
        "remarks, publish_id, ",
        "lender_id, status, ",
        "interest_from, del_flag, ",
        "return_source, loan_use, ",
        "safeguards, audit_id, ",
        "audit_time, audit_message, ",
        "created_time, updated_time,instalment,enterprise_id)",
        "values (#{id,jdbcType=BIGINT}, #{serialNumber,jdbcType=VARCHAR}, ",
        "#{originalDebtNumber,jdbcType=VARCHAR}, #{debtType,jdbcType=VARCHAR}, ",
        "#{borrowerType,jdbcType=INTEGER},#{lenderType,jdbcType=INTEGER}, ",
        "#{guarantyType,jdbcType=VARCHAR},#{amount,jdbcType=DECIMAL}, ",
        "#{returnType,jdbcType=VARCHAR}, #{startDate,jdbcType=DATE}, ",
        "#{endDate,jdbcType=DATE}, #{borrowerId,jdbcType=BIGINT}, ",
        "#{annualizedRate,jdbcType=DECIMAL}, #{offlineAnnualizedRate,jdbcType=DECIMAL},",
        "#{feeRate,jdbcType=DECIMAL}, ",
        "#{remarks,jdbcType=VARCHAR}, #{publishId,jdbcType=BIGINT}, ",
        "#{lenderId,jdbcType=BIGINT}, #{status,jdbcType=INTEGER}, ",
        "#{interestFrom,jdbcType=INTEGER}, 1, ",
        "#{returnSource,jdbcType=VARCHAR}, #{loanUse,jdbcType=VARCHAR}, ",
        "#{safeguards,jdbcType=VARCHAR}, #{auditId,jdbcType=TIMESTAMP}, ",
        "#{auditTime,jdbcType=TIMESTAMP}, #{auditMessage,jdbcType=VARCHAR}, ",
        "now(), now(),#{instalment,jdbcType=INTEGER},#{enterprise,jdbcType=BIGINT})"
    })
    int insert(Debt record);

    int insertSelective(Debt record);

    @Select({
        "select",
        "id, serial_number, original_debt_number, debt_type, borrower_type, lender_type, guaranty_type, amount, return_type, ",
        "start_date, end_date, borrower_id, annualized_rate, offline_annualized_rate, fee_rate, remarks, publish_id, ",
        "lender_id, status, interest_from, del_flag, return_source, loan_use, safeguards, ",
        "audit_id, audit_time, audit_message, created_time, updated_time,instalment,enterprise_id ,lender_enterprise_id",
        "from ic_debt",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("BaseResultMap")
    Debt selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Debt record);
    
    /**紧急修改更新*/
    int emergencyUpdateByPrimaryKeySelective(Debt record);

    @Update({
        "update ic_debt",
        "set serial_number = #{serialNumber,jdbcType=VARCHAR},",
          "original_debt_number = #{originalDebtNumber,jdbcType=VARCHAR},",
          "debt_type = #{debtType,jdbcType=VARCHAR},",
          "borrower_type = #{borrowerType,jdbcType=INTEGER},",
          "lender_type = #{lenderType,jdbcType=INTEGER},",
          "guaranty_type = #{guarantyType,jdbcType=VARCHAR},",
          "amount = #{amount,jdbcType=DECIMAL},",
          "return_type = #{returnType,jdbcType=VARCHAR},",
          "start_date = #{startDate,jdbcType=DATE},",
          "end_date = #{endDate,jdbcType=DATE},",
          "borrower_id = #{borrowerId,jdbcType=BIGINT},",
          "annualized_rate = #{annualizedRate,jdbcType=DECIMAL},",
          "offline_annualized_rate = #{offlineAnnualizedRate,jdbcType=DECIMAL},",
          "fee_rate = #{feeRate,jdbcType=DECIMAL},",
          "remarks = #{remarks,jdbcType=VARCHAR},",
          "publish_id = #{publishId,jdbcType=BIGINT},",
          "lender_id = #{lenderId,jdbcType=BIGINT},",
          "status = #{status,jdbcType=INTEGER},",
          "interest_from = #{interestFrom,jdbcType=INTEGER},",
          "del_flag = #{delFlag,jdbcType=INTEGER},",
          "return_source = #{returnSource,jdbcType=VARCHAR},",
          "loan_use = #{loanUse,jdbcType=VARCHAR},",
          "safeguards = #{safeguards,jdbcType=VARCHAR},",
          "audit_id = #{auditId,jdbcType=TIMESTAMP},",
          "audit_time = #{auditTime,jdbcType=TIMESTAMP},",
          "audit_message = #{auditMessage,jdbcType=VARCHAR},",
          "updated_time = now(),instalment = #{instalment,jdbcType=INTEGER},",
          "enterprise_id = #{enterpriseId,jdbcType=BIGINT}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(Debt record);

    Page<Debt> findByPage(Page<Debt> pageRequest, @Param("map") Map<String, Object> map);

    int batchDelete(@Param("ids") long[] ids);

    List<Debt> selectForPagin(@Param("map") Map<String, Object> map);

    int selectForPaginTotalCount(@Param("map") Map<String, Object> map);
    
    Debt findDebtBySerialNumber(String serialNumber);
    
    List<Map<String, String>> findSerialNumberAndMemberName(@Param("serialNumber") String serialNumber);
    
    @Update({
    	"update ic_debt set status = #{status,jdbcType=INTEGER} where id = #{id,jdbcType=BIGINT}"
    })
    int updateDebtStatusById(@Param("status") Integer status, @Param("id") Long id);
    
    int deleteByDebtId(Long id);
    
    int findDebtByOriginalDebtNumber(String originalDebtNumber);
    
    /* 根据用户姓名或用户手机模糊查询对应债权 */
    List<Long> findDebtIdByMemberInfo(@Param("map")Map<String, Object> map);
    
    /* 根据借款人姓名模糊查询对应债权 */
    List<Long> findDebtIdByMemberName(@Param("map")Map<String, Object> map);
    
    List<Debt> findDebtInfoByPage(@Param("map") Map<String, Object> map); 
    
    int findDebtInfoByPageCount(@Param("map") Map<String, Object> map);
    
    int updateControlRemarksById(Debt record);
    
    Debt findDebtAndProjectStatusByDebtId(@Param("id")Long id);
    
    /**
     * 根据项目id获取债权
     * @return
     */
	public Debt selectEnterpriseIdByProjectId(@Param("projectId")Long projectId);
	/**
	 * 统计用户出借人身份下的债权数量
	 * @param memberId
	 * @return
	 */
	Integer countDebtByLenderId(@Param("memberId")Long memberId);
	/**
	 * 
	 * @Description:根据借款人企业id 查询
	 * @param enterpriseId
	 * @return
	 * @author: chaisen
	 * @time:2016年4月28日 下午4:29:26
	 */
	Integer countDebtByEnterpriseId(@Param("enterpriseId")Long enterpriseId);
	/**
	 * 
	 * @Description:根据出借人企业id查询
	 * @param lenderEnterpriseId
	 * @return
	 * @author: chaisen
	 * @time:2016年4月28日 下午4:29:31
	 */
	Integer countDebtByLenderEnterpriseId(@Param("lenderEnterpriseId")Long lenderEnterpriseId);
	/**
	 * 
	 * @Description:查询是否为原始债权人
	 * @param lenderEnterpriseId
	 * @return
	 * @author: zhanghao	
	 * @time:2016年5月6日 上午10:02:31
	 */
	Integer ifOriginators(@Param("memberId") Long memberId);
}