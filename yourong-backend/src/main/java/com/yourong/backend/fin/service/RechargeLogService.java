package com.yourong.backend.fin.service;

import java.util.Map;

import com.yourong.common.pageable.Page;
import com.yourong.core.fin.model.RechargeLog;

public interface RechargeLogService {

	/**
	 * 充值查询
	 * @param pageRequest
	 * @param map
	 * @return
	 */
	public Page<RechargeLog> findByPage(Page<RechargeLog> pageRequest, Map<String, Object> map);
	
	/**
	 * 根据条件同步充值记录
	 * 
	 * @param status
	 * @param rechargeNo
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public boolean synchronizedRecharge(Integer status, String rechargeNo, String startTime, String endTime);
	
}
