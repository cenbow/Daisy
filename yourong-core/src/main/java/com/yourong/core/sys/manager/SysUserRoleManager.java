package com.yourong.core.sys.manager;

import java.util.List;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.sys.model.SysUserRole;

public interface SysUserRoleManager {
	
	/**
	 * 根据用户编号删除角色
	 * @param id
	 */
	public void deleteSysUserRoleByUserId(Long userId) throws ManagerException;

	/**
	 * 给用户添加角色
	 * @param userId 用户编号
	 * @param roleIds 角色ID
	 * @throws ManagerException
	 */
	public int addSysUserRole(Long userId, long roleIds[]) throws ManagerException;
	
	/**
	 * 根据用户ID找出用户分配的角色
	 * @param userId
	 * @return
	 * @throws ManagerException
	 */
	public List<SysUserRole> getSysUserRoleByUserId(Long userId) throws ManagerException;
}
