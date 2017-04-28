/**
 * 
 */
package com.yourong.backend.tc.service;

import java.util.Map;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.tc.model.ContractSignDto;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年7月22日下午4:34:25
 */
public interface ContractSignService {
	
	/**
	 * 合同查询
	 * 
	 * @author zhanghao
	 * @param pageRequest
	 * @param map
	 * @return
	 */
	public Page<ContractSignDto> findByPage(Page<ContractSignDto> pageRequest, Map<String, Object> map);
	
	/**
	 * 合同数据重新初始化
	 * 
	 * @author zhanghao
	 * @param pageRequest
	 * @param map
	 * @return
	 */
	public ResultDO<Object> contractReInit(Long transactionId) throws ManagerException;
	
	/**
	 * 合同历史数据处理
	 * 
	 * @author zhanghao
	 * @return
	 */
	public ResultDO<Object> contractHistoryInit() throws ManagerException;
	
	/**
	 * 同步
	 * 
	 * @author zhanghao
	 * @param transactionId
	 * @return
	 */
	public ResultDO<Object> queryContractInfo(Long transactionId);
	

	/**
	 * 乙方自动签署
	 * 
	 * @author zhanghao
	 * @param transactionId
	 * @return
	 */
	public ResultDO<Object> autoSignSecond(Long transactionId);
	
	
}
