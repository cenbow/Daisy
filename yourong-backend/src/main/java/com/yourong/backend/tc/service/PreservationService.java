package com.yourong.backend.tc.service;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.core.tc.model.Preservation;

public interface PreservationService {
    
	/**
	 * 根据保全ID获取保全链接
	 * @param preservationId
	 * @return
	 */
	Preservation getPreservationLink(Long preservationId);
	
	/**
	 * 根据保全ID更新保全表数据
	 * @param map
	 * @return
	 */
	int updateByPreservationIdSelective(Preservation preservation); 
	
	/**
	 * 通过id查询保全记录
	 * 
	 * @param transactionId
	 * @return
	 * @throws ManagerException
	 */
	public Preservation selectByTransactionId(Long transactionId);

	/**
	 * 根据交易号创建保全
	 * @param transaction
	 * @return
	 */
	public ResultDO<Preservation> createPreservationByTransactionId(Long transactionId);
	
	/**
	 * 批量创建电子保全，针对未做保全的合同
	 * 
	 * @author wangyanji
	 * @return
	 * @throws Exception
	 */
	public int createPreservationTask() throws Exception ;
}