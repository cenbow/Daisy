package com.yourong.core.ic.manager;

import java.util.Map;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.DebtPledge;

public interface DebtPledgeManager {
	/**
	 * 单个删除质押物
	 * 
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	int deleteByPrimaryKey(Long id) throws ManagerException;

	/**
	 * 插入质押物信息
	 * 
	 * @param record
	 * @return
	 * @throws ManagerException
	 */
	int insert(DebtPledge record) throws ManagerException;

	int insertSelective(DebtPledge record) throws ManagerException;

	DebtPledge selectByPrimaryKey(Long id) throws ManagerException;

	int updateByPrimaryKeySelective(DebtPledge record) throws ManagerException;

	int updateByPrimaryKey(DebtPledge record) throws ManagerException;

	Page<DebtPledge> findByPage(Page<DebtPledge> pageRequest, Map<String, Object> map)
			throws ManagerException;

	/**
	 * 批量删除质押物
	 * 
	 * @param ids
	 * @return
	 * @throws ManagerException
	 */
	int batchDelete(int[] ids) throws ManagerException;
	
	/**
	 * 根据债权id获取质押物信息 
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	DebtPledge findPledgeByDebtId(Long debtId) throws ManagerException;
	
	/**
	 * 根据债权id删除质押物信息 
	 * @param debtId
	 * @return
	 * @throws ManagerException
	 */
	int deletePledgeByDebtId(Long debtId) throws ManagerException;
}
