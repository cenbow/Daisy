package com.yourong.core.bsc.manager.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rop.thirdparty.com.google.common.collect.Maps;

import com.aliyun.common.utils.DateUtil;
import com.yourong.common.constant.Constant;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.DateUtils;
import com.yourong.core.bsc.dao.AuditMapper;
import com.yourong.core.bsc.manager.AuditManager;
import com.yourong.core.bsc.model.Audit;

@Component
public class AuditManagerImpl implements AuditManager {

	@Autowired
	private AuditMapper auditMapper;

	@Override
	public int insertAudit(Long auditId,String msg,int result,int type,Long processId,int step) throws ManagerException {
		try {
			Audit audit = new Audit();
			audit.setAuditId(auditId);
			audit.setAuditMsg(msg);
			audit.setAuditResult(result);
			audit.setType(type);
			audit.setAuditTime(DateUtils.getCurrentDate());
			audit.setProcessId(processId);
			audit.setAuditStep(step);
			return auditMapper.insertSelective(audit);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Page<Audit> findByPage(Page<Audit> pageRequest, Map<String, Object> map) throws ManagerException {
		try {
			return auditMapper.findByPage(pageRequest, map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Page<Audit> findByPageD(Page<Audit> pageRequest, Map<String, Object> map) throws ManagerException {
		try {
			map.put(Constant.STARTROW, pageRequest.getiDisplayStart());
			map.put(Constant.PAGESIZE, pageRequest.getiDisplayLength());
			int totalCount = auditMapper.selectForPaginTotalCount(map);
			pageRequest.setiTotalDisplayRecords(totalCount);
			pageRequest.setiTotalRecords(totalCount);
			List<Audit> selectForPagin = auditMapper.selectForPagin(map);
			pageRequest.setData(selectForPagin);
			return pageRequest;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	
	@Override 
	public Audit selectByProcessId (Long id) throws ManagerException{
		Map<String, Object> map = Maps.newHashMap();
		
		map.put("processId", id);
		map.put("auditStep", StatusEnum.PROJECT_STATUS_FULL.getStatus());
		map.put("auditResult", 1);
		
		return auditMapper.selectByMap(map);
	}
	
}
