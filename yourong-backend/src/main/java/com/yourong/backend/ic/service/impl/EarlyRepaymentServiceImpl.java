package com.yourong.backend.ic.service.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.backend.ic.service.EarlyRepaymentService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.manager.EarlyRepaymentManager;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.model.ProjectEarlyRepayment;

@Service
public class EarlyRepaymentServiceImpl  implements EarlyRepaymentService{
	
	@Autowired
	private ProjectManager projectManager;
	
	@Autowired
	private EarlyRepaymentManager earlyRepaymentManager;
	
	

	private static Logger logger = LoggerFactory
			.getLogger(EarlyRepaymentServiceImpl.class);
	
	public Page<ProjectEarlyRepayment> findByPage(Page<ProjectEarlyRepayment> pageRequest, Map<String, Object> map){
		try {
			return earlyRepaymentManager.findByPage(pageRequest, map);
		} catch (ManagerException e) {
			logger.error("分页查询提前还款列表失败", e);
		}
		return null;
	}
	
	
	public ResultDO<Object> prepayment(Date prepaymentDate,Long projectId,String prepaymentRemarkSys,String prepaymentRemarkFront,Long operaId){
		ResultDO<Object> result = new ResultDO<Object>();
		try {
			return earlyRepaymentManager.prepayment(prepaymentDate, projectId, prepaymentRemarkSys, prepaymentRemarkFront,operaId);
		} catch (ManagerException e) {
			logger.error("提前还款失败，prepaymentDate={},projectId={},prepaymentRemarkSys={},prepaymentRemarkFront={}",
					prepaymentDate,projectId,prepaymentRemarkSys,prepaymentRemarkFront,e);
		}
		return result;
	}
		
	
}
