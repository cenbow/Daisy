package com.yourong.backend.fin.service;

import java.util.Map;

import com.yourong.common.pageable.Page;
import com.yourong.core.fin.model.biz.LoanBiz;

public interface LoanService {
	/**
	 * 获取放款管理列表 
	 * @param pageRequest
	 * @param map
	 * @return
	 */
	public Page<LoanBiz> findByPage(Page<LoanBiz> pageRequest, Map<String, Object> map);
	
	/**
	 * 根据项目id获取放款信息
	 * @param projectId
	 * @return
	 */
	public LoanBiz findLoanBiz(Long projectId);
	
}
