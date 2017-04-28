package com.yourong.core.ic.manager;

import java.util.List;
import java.util.Map;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.LeaseBonusDetail;
import com.yourong.core.ic.model.query.LeaseBonusDetailQuery;

public interface LeaseBonusDetailManager {
	int deleteByPrimaryKey(Long id) throws ManagerException;

	int insert(LeaseBonusDetail record) throws ManagerException;

	int insertSelective(LeaseBonusDetail record) throws ManagerException;

	LeaseBonusDetail selectByPrimaryKey(Long id) throws ManagerException;

	int updateByPrimaryKeySelective(LeaseBonusDetail record) throws ManagerException;

	int updateByPrimaryKey(LeaseBonusDetail record) throws ManagerException;

	Page<LeaseBonusDetail> findByPage(Page<LeaseBonusDetail> pageRequest, Map<String, Object> map)
			throws ManagerException;

	int batchDelete(long[] ids) throws ManagerException;

	Page<LeaseBonusDetail> findByBonusDetailPage(LeaseBonusDetailQuery leaseBonusDetailQuery) throws ManagerException;

	/**
	 * 根据交易id获取分红明细
	 * 
	 * @param transactionId
	 * @return
	 */
	List<LeaseBonusDetail> findListByTransactionId(Long transactionId) throws ManagerException;

	/**
	 * 根据交易ID获取总收益
	 * 
	 * @param transactionId
	 * @return
	 */
	LeaseBonusDetail getLeaseTotalIncomeByTransactionId(Long transactionId) throws ManagerException;
}