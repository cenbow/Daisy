package com.yourong.core.fin.manager;

import java.util.Map;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.fin.model.biz.LoanBiz;
import com.yourong.core.ic.model.Project;

public interface LoanManager {

	/**
	 * 托管放款列表
	 * @param pageRequest
	 * @param map
	 * @return
	 * @throws ManagerException
	 */
	public Page<LoanBiz> findByPage(Page<LoanBiz> pageRequest,
			Map<String, Object> map) throws ManagerException;
	
	/**
	 * 根据项目获取放款信息
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 */
	public LoanBiz findLoanByProject(Project project)throws ManagerException;
	
	public LoanBiz findLoanBiz(Long projectId) throws ManagerException;

}
