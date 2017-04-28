package com.yourong.core.sys.manager;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.sys.model.SysRole;

public interface SysRoleManager {

	int deleteByPrimaryKey(Long id) throws ManagerException  ;

	int insert(SysRole record) throws ManagerException  ;

	int insertSelective(SysRole record) throws ManagerException  ;

	SysRole selectByPrimaryKey(Long id) throws ManagerException ;

	SysRole selectRoleMenus(Long id) throws ManagerException  ;

	int updateByPrimaryKeySelective(SysRole record) throws ManagerException  ;

	int updateByPrimaryKey(SysRole record)  throws ManagerException   ;

	Page<SysRole> findByPage(Page<SysRole> pageRequest, @Param("map") Map<String, Object> map) throws ManagerException   ;

	int batchDelete(@Param("ids") long[] ids) throws ManagerException;
	
	int batchInsertRoleAndMenus(Long id,long[] menus)throws ManagerException;	
	
	int batchDeleteRoleAndMenus(Long id)throws ManagerException;
	
	/**
	 * 查找所有有效的角色
	 */
	List<SysRole> selectAllSysRole() throws ManagerException;	
	
	/**
     * 检查登陆账号是否存在
     * @param name 角色名称
     * @param selfId 当前角色编号
     * @return
     */
	 SysRole checkRoleNameExists(String name, long selfId) throws ManagerException;
}
