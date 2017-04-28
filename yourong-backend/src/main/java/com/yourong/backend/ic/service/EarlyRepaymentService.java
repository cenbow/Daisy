package com.yourong.backend.ic.service;

import java.util.Date;
import java.util.Map;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.ProjectEarlyRepayment;

public interface EarlyRepaymentService {

	/** 分页查询 **/
	public Page<ProjectEarlyRepayment> findByPage(Page<ProjectEarlyRepayment> pageRequest, Map<String, Object> map);
	

	/**
	 * @Description:提前还款操作
	 * @param prepaymentDate
	 * @param projectId
	 * @param prepaymentRemarkSys
	 * @param prepaymentRemarkFront
	 * @return
	 * @author: zhanghao
	 * @time:2016年5月27日 上午9:06:08
	 */
	public ResultDO<Object> prepayment(Date prepaymentDate,Long projectId, String prepaymentRemarkSys,String prepaymentRemarkFront,Long operaId);
	 
	
}
