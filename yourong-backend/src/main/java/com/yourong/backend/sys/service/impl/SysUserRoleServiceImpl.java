package com.yourong.backend.sys.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.backend.sys.service.SysUserRoleService;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.web.ResultObject;
import com.yourong.core.sys.manager.SysUserRoleManager;
import com.yourong.core.sys.model.SysUserRole;


@Service
public class SysUserRoleServiceImpl implements SysUserRoleService {
	
	private static Logger logger = LoggerFactory.getLogger(SysUserRoleServiceImpl.class);
	
    @Autowired
    private SysUserRoleManager sysUserRoleManager;
    
    
	@Override
	public void deleteSysUserRoleByUserId(Long userId) {
		try {
			sysUserRoleManager.deleteSysUserRoleByUserId(userId);
		} catch (ManagerException e) {
			logger.error("deleteSysUserRoleByUserId",e);
		}
		
	}
	
	public ResultObject addSysUserRole(Long userId, long roleIds[]){
		ResultObject resultObject = new ResultObject();
		try {
			if(roleIds !=null && roleIds.length > 0){
				int status = sysUserRoleManager.addSysUserRole(userId, roleIds);
				if(status > 0){
					resultObject.isSuccess();
				}
			}else{
				resultObject.isError();
			}
		} catch (ManagerException e) {
			logger.error("addSysUserRole",e);
			resultObject.isError();
		}
		return resultObject;
	}

	@Override
	public long[] getSysUserRoleIdsByUserId(Long userId) {
		long ids[] = null;
		List<SysUserRole> userRoleList = getSysUserRoleByUserId(userId);
		if(userRoleList != null && userRoleList.size() > 0){
			int size = userRoleList.size();
			ids = new long[size];
			for(int i=0; i < size; i++){
				ids[i] = userRoleList.get(i).getRoleId();
			}
		}
		return ids;
	}

	
	@Override
	public List<SysUserRole> getSysUserRoleByUserId(Long userId) {
		try{
			return sysUserRoleManager.getSysUserRoleByUserId(userId);
		} catch (ManagerException e) {
			logger.error("getSysUserRoleByUserId",e);
		}
		return null;
	}
}