package com.yourong.core.ic.manager;

import java.util.Map;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.DebtCollateral;

public interface DebtCollateralManager {
	/**
	 * 单个删除债权转让
	 * 
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	int deleteByPrimaryKey(Long id) throws ManagerException;

	/**
	 * 插入债权信息
	 * 
	 * @param record
	 * @return
	 * @throws ManagerException
	 */
	int insert(DebtCollateral record) throws ManagerException;

	int insertSelective(DebtCollateral record) throws ManagerException;

	DebtCollateral selectByPrimaryKey(Long id) throws ManagerException;

	int updateByPrimaryKeySelective(DebtCollateral record) throws ManagerException;

	int updateByPrimaryKey(DebtCollateral record) throws ManagerException;

	Page<DebtCollateral> findByPage(Page<DebtCollateral> pageRequest, Map<String, Object> map)
			throws ManagerException;

	/**
	 * 批量删除用户
	 * 
	 * @param ids
	 * @return
	 * @throws ManagerException
	 */
	int batchDelete(int[] ids) throws ManagerException;
	
	/**
	 * 根据债权id获取抵押物信息
	 * @param debtId
	 * @return
	 * @throws ManagerException
	 */
	DebtCollateral findCollateralByDebtId(Long debtId) throws ManagerException;

	int deleteCollateralByDebtId(Long debtId) throws ManagerException;
	
	/**
	 * @Description:根据项目id获取抵押物信息
	 * @param debtId
	 * @return
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2016年1月7日 下午8:55:51
	 */
	DebtCollateral findCollateralByProjectId(Long debtId)throws ManagerException;
}
