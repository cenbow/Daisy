package com.yourong.core.tc.dao;

import com.yourong.core.tc.model.Preservation;

public interface PreservationMapper {
    
	/**
	 * 插入保全信息
	 * @param record
	 * @return
	 */
	int insertSelective(Preservation record);

	/**
	 * 根据保全ID更新保全表数据
	 * @param map
	 * @return
	 */
	int updateByPreservationIdSelective(Preservation preservation);

	/**
	 * 根据交易号查询保全数据
	 * @param preservation
	 * @return
	 */
	Preservation selectByTransactionId(Long transactionId);
}