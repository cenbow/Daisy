package com.yourong.core.ic.manager;

import java.util.Date;
import java.util.Map;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.ProjectEarlyRepayment;

public interface EarlyRepaymentManager {
	
	public Page<ProjectEarlyRepayment> findByPage(Page<ProjectEarlyRepayment> pageRequest, Map<String, Object> map)
			throws ManagerException;
	
	public ResultDO<Object> prepayment(Date prepaymentDate,Long projectId,String prepaymentRemarkSys,String prepaymentRemarkFront,Long operaId)
			throws ManagerException;
}
