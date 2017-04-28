package com.yourong.core.sys.manager;

import java.util.Map;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.sys.model.SysOperateInfo;


public interface SysOperateInfoManager {
	
	public int insertSelective(SysOperateInfo sysOperateInfo) throws ManagerException;

	int updateByPrimaryKeySelective(SysOperateInfo record) throws ManagerException;

	int insert(SysOperateInfo record) throws ManagerException;
	
	SysOperateInfo selectOperateBySourceId(Map<String, Object> map)throws ManagerException;


	int saveOperateInfo(Long sourceId, int operateTableType, Long operateId, String operateMsg, String operateCode,String remarks)throws ManagerException;

}
