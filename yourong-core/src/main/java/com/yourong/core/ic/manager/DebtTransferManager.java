package com.yourong.core.ic.manager;

import java.util.Map;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.DebtTransfer;

public interface DebtTransferManager {
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
	int insert(DebtTransfer record) throws ManagerException;

	int insertSelective(DebtTransfer record) throws ManagerException;

	DebtTransfer selectByPrimaryKey(Long id) throws ManagerException;

	int updateByPrimaryKeySelective(DebtTransfer record) throws ManagerException;

	int updateByPrimaryKey(DebtTransfer record) throws ManagerException;

	Page<DebtTransfer> findByPage(Page<DebtTransfer> pageRequest,
			Map<String, Object> map) throws ManagerException;

	/**
	 * 批量删除质押物
	 * 
	 * @param ids
	 * @return
	 * @throws ManagerException
	 */
	int batchDelete(int[] ids) throws ManagerException;
}
