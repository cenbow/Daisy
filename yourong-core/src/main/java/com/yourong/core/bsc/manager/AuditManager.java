package com.yourong.core.bsc.manager;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.bsc.model.Audit;

@Component
public interface AuditManager {
	/**
	 * @Description:插入审核记录
	 * @param auditId 审核人
	 * @param msg 审核信息
	 * @param result 审核结果
	 * @param processId 审核内容id
	 * @param type 审核类型
	 * @param step 审核环节
	 * @return
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2016年1月20日 下午7:12:43
	 */
	int insertAudit(Long auditId,String msg,int result,int type,Long processId,int step) throws ManagerException;

	Page<Audit> findByPage(Page<Audit> pageRequest, Map<String, Object> map) throws ManagerException;

	Page<Audit> findByPageD(Page<Audit> pageRequest, Map<String, Object> map) throws ManagerException;
	
	public Audit selectByProcessId (Long id) throws ManagerException;
	
}
