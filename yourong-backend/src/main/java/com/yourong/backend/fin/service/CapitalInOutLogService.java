package com.yourong.backend.fin.service;

import java.util.Map;

import com.yourong.common.pageable.Page;
import com.yourong.core.fin.model.CapitalInOutLog;

public interface CapitalInOutLogService {

	/**
	 * 资金流水查询
	 * @param pageRequest
	 * @param map
	 * @return
	 */
	public Page<CapitalInOutLog> findByPage(Page<CapitalInOutLog> pageRequest, Map<String, Object> map);
	
	/**
	 * 出借人资金流水
	 * @param pageRequest
	 * @param map
	 * @return
	 */
	public Page<CapitalInOutLog> findLenderCapitalInOutLogPage(Page<CapitalInOutLog> pageRequest,
			Map<String, Object> map);
	
}
