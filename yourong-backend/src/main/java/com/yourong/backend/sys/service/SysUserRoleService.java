package com.yourong.backend.sys.service;

import java.util.List;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.web.ResultObject;
import com.yourong.core.sys.model.SysUserRole;


public interface SysUserRoleService {
	/**
	 * 根据用户编号删除角色
	 * @param id
	 */
	public void deleteSysUserRoleByUserId(Long userId);
	
	/**
	 * 给用户添加角色
	 * @param userId 用户编号
	 * @param roleIds 角色ID
	 * @throws ManagerException
	 */
	public ResultObject addSysUserRole(Long userId, long roleIds[]);
	
	/**
	 * 根据用户ID获得用户分配的角色Id
	 * @param userId
	 * @return
	 * @throws ManagerException
	 */
	long[] getSysUserRoleIdsByUserId(Long userId);
	
	/**
	 * 根据用户ID找出用户分配的角色
	 * @param userId
	 * @return
	 */
	public List<SysUserRole> getSysUserRoleByUserId(Long userId);
}