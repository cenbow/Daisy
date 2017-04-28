package com.yourong.backend.sys.service;

import java.util.List;
import java.util.Map;

import com.yourong.common.pageable.Page;
import com.yourong.common.web.ResultObject;
import com.yourong.core.sys.model.SysMenu;
import com.yourong.core.sys.model.SysRole;

public interface SysRoleService {
    int deleteByPrimaryKey(Long id);

    int insert(SysRole record);

    SysRole selectByPrimaryKey(Long id);

    int updateByPrimaryKey(SysRole record);

    int updateByPrimaryKeySelective(SysRole record);

    int batchDelete(long[] ids);

    Page findByPage(Page pageRequest, Map map);
    
    SysRole  selectRoleMenus(Long id);
    /**
     * 获取树状结构的菜单树
     * @param id
     * @return
     */
    List<SysMenu> selectRoleTree(Long id);
    
    /**
     *  批量插入权限和菜单的关系
     * @param id
     * @param menus
     * @return
     */
    ResultObject batchInsertRoleAndMenus(Long id,long[] menus);
    
    /**
	 * 查找所有有效的角色
	 */
	List<SysRole> selectAllSysRole();
	

	/**
	 * 检查角色名称是否已存在
	 * @param name 角色名称
	 * @param selfId 角色编号
	 * @return
	 */
	boolean checkRoleNameExists(String name, long selfId);
	
	/**
	 * 检查角色名称是否已存在
	 * @param name 角色名称
	 * @return
	 */
	boolean checkRoleNameExists(String name);
    
}