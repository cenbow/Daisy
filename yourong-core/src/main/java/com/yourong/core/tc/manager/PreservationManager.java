package com.yourong.core.tc.manager;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.tc.model.Preservation;

public interface PreservationManager {

	/**
	 * 插入保全记录
	 * 
	 * @param preservation
	 * @return
	 * @throws ManagerException
	 */
	public Integer insert(Preservation preservation) throws ManagerException;

	/**
	 * 通过id查询保全记录
	 * 
	 * @param transactionId
	 * @return
	 * @throws ManagerException
	 */
	public Preservation selectByTransactionId(Long transactionId) throws ManagerException;

	/**
	 * 创建保全
	 * @param preservation
	 * @return
	 * @throws ManagerException
	 */
	public Preservation createPreservation(Preservation preservation);
	
	/**
	 * 根据保全ID更新保全表数据
	 * @param map
	 * @return
	 */
	public int updateByPreservationIdSelective(Preservation preservation) throws ManagerException;
	
	public Preservation getPreservationLink(Long preservationId); 
	
}