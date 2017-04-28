package com.yourong.core.sys.manager.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.yourong.common.exception.ManagerException;
import com.yourong.core.sys.dao.SysUserRoleMapper;
import com.yourong.core.sys.manager.SysUserRoleManager;
import com.yourong.core.sys.model.SysUserRole;

@Component
public class SysUserRoleManagerImpl implements SysUserRoleManager {
	
	@Autowired
	SysUserRoleMapper sysUserRoleMapper;
	
	@Override
	public void deleteSysUserRoleByUserId(Long userId) throws ManagerException {
		try{
			sysUserRoleMapper.deleteSysUserRoleByUserId(userId);
		} catch(Exception ex){
			throw new ManagerException(ex);
		}
	}

	@Override
	public int addSysUserRole(Long userId, long[] roleIds) throws ManagerException {
		try{
			List<SysUserRole> roleList = Lists.newArrayList();
			for(long roleId : roleIds){
				SysUserRole role = new SysUserRole();
				role.setRoleId(roleId);
				role.setUserId(userId);
				roleList.add(role);
			}
			return sysUserRoleMapper.batchInsertSysUserRole(roleList);
		} catch(Exception ex){
			throw new ManagerException(ex);
		}
	}
	
	@Override
	public List<SysUserRole> getSysUserRoleByUserId(Long userId) throws ManagerException {
		try{
			return sysUserRoleMapper.getSysUserRoleByUserId(userId);
		} catch(Exception ex){
			throw new ManagerException(ex);
		}
	}

}
