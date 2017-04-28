/**
 * 
 */
package com.yourong.core.ic.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yourong.core.ic.model.TransferProject;
import com.yourong.core.ic.model.biz.TransactionProjectDetailBiz;
import com.yourong.core.ic.model.biz.TransferProjectPageBiz;
import com.yourong.core.ic.model.query.TransactionProjectDetailBizQuery;
import com.yourong.core.ic.model.query.TransferProjectPageQuery;
import com.yourong.core.ic.model.query.TransferProjectQuery;
import com.yourong.core.ic.model.query.TransferRecordQuery;
import com.yourong.core.tc.model.query.TransactionInterestQuery;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年8月28日上午11:06:38
 */
public interface TransferProjectMapper {
	
	
	TransferProject selectByPrimaryKey(Long id);
	
	TransferProject selectByPrimaryKeyForLock(Long id);
	
	TransferProject selectByTransactionId(@Param("transactionId")Long transactionId);
	

	TransferProject selectByTransactionIdForLock(@Param("transactionId")Long transactionId);

	TransferProject selectByTransactionIdToday(@Param("transactionId")Long transactionId);

	
	TransferProject selectByTransactionIdStatus(@Param("transactionId")Long transactionId);
	
	List<TransferProject> selectTransferInfoByMemberId(@Param("query")TransferRecordQuery query);
	
	int selectCountTransferInfoByMemberId(@Param("query")TransferRecordQuery query);

	public int updateStatusByTransactionId(Integer afterStatus,Integer beforeStatus,Long transactionId) ;
	
	int insert(TransferProject transferProject);
	
	List<TransferProject> queryTransferProjectListByMap(@Param("map")Map<String, Object> map);
	
	int updateByPrimaryKeySelective(TransferProject transferProject);
	
	List<TransferProject> selectTransferList(@Param("transferProjectQuery")TransferProjectQuery transferProjectQuery);
	
    int selectCountTransferList(@Param("transferProjectQuery")TransferProjectQuery transferProjectQuery);
	
	int updateProjectStatus(@Param("status") int statusCode, @Param("currentStatus") int currentStatus, @Param("id") Long id);
	
	List<TransferProject> findIndexInvestingProjectList(@Param("map")Map<String, Object> map);
	
	List<TransferProject> findIndexNotInvestingProjectList(@Param("map")Map<String, Object> map);
	
	List<TransferProject> selectTransferListForMember(@Param("transferProjectQuery")TransferProjectQuery query);
	
	int selectCountTransferListForMember(@Param("transferProjectQuery")TransferProjectQuery transferProjectQuery);

	TransferProject totalTransferProjectByMemberId(@Param("memberId")Long memberId);
	
	List<TransferProject> totalTransferProjectByMemberIdNum(@Param("memberId")Long memberId);
	
	int updateStatusByProjectId(@Param("projectId") Long projectId, @Param("newStatus") int newStatus, @Param("oldStatus") int oldStatus);
	
	List<TransferProject> getTransferDetailForMember(
			@Param("query") TransactionInterestQuery query);

	TransferProject selectByTransactionIdAndMemberId(@Param("transactionId")Long transactionId,
			@Param("memberId")Long memberId);
	
	List<TransferProject> selectTransferByMemberId(@Param("memberId")Long memberId);

	List<TransferProjectPageBiz> queryTransferProjectByPage(@Param("query")TransferProjectPageQuery query);

	int queryTransferProjectCountByPage(@Param("query")TransferProjectPageQuery query);

	List<TransactionProjectDetailBiz> queryPageTransactionProjectDetailBiz(@Param("query")TransactionProjectDetailBizQuery query);

	TransactionProjectDetailBiz queryInvestmentedTransactionProjectDetailBiz(@Param("transactionId")Long transactionId, @Param("memberId")Long memberId);

	int queryPageCountTransactionProjectDetailBiz(@Param("query")TransactionProjectDetailBizQuery query);

	/**
	 * @Description:查询会员总转让本金
	 * @param memberId
	 * @return
	 * @author: wangyanji
	 * @time:2017年1月3日
	 */
	BigDecimal getTotalTransferPrincipal(@Param("memberId") Long memberId);
}
