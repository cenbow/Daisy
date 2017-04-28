package com.yourong.core.fin.manager;

import java.util.Map;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.fin.model.biz.InterestSettlementBiz;

public interface InterestSettlementManager {

	/**
	 * 获取线上线下利息营收结算
	 * 
	 * @param pageRequest
	 * @param map
	 * @return
	 * @throws ManagerException
	 */
	public Page<InterestSettlementBiz> findByPage(
			Page<InterestSettlementBiz> pageRequest, Map<String, Object> map)
			throws ManagerException;

}
