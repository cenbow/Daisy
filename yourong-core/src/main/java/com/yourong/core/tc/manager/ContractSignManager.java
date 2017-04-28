/**
 * 
 */
package com.yourong.core.tc.manager;

import java.util.List;
import java.util.Map;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.tc.model.ContractSign;
import com.yourong.core.tc.model.ContractSignDto;

/**
 * @desc 合同签署信息
 * @author zhanghao
 * 2016年7月6日下午3:59:29
 */
public interface ContractSignManager {
	
	public Page<ContractSignDto> findByPage(Page<ContractSignDto> pageRequest,
			Map<String, Object> map) throws ManagerException;

	/**批量插入**/
	public int batchInsert(List<ContractSign> records) throws ManagerException ;
	
	/**根据ID查询**/
	public ContractSign selectByPrimaryKey(Long id) throws ManagerException; 
	
	/**查询**/
	public List<ContractSign> selectListByPrimaryKey(Map<String,Object> map) throws ManagerException;
	
	/**根据ID更新**/
	public int updateByPrimaryKey(ContractSign record) throws ManagerException;
	
	/**根据条件查询**/
	public ContractSign selectByMap(Map<String,Object> map) throws ManagerException;
	
	public int deletByTransactionId(Long transactionId) throws ManagerException;
	
}
