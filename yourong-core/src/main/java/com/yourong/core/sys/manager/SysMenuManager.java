package com.yourong.core.sys.manager;

import java.util.List;
import java.util.Map;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.sys.model.SysMenu;

public interface SysMenuManager {
    int deleteByPrimaryKey(Long id) throws ManagerException;

    int insert(SysMenu record) throws ManagerException;

    SysMenu selectByPrimaryKey(Long id) throws ManagerException;

    int updateByPrimaryKey(SysMenu record) throws ManagerException;

    int updateByPrimaryKeySelective(SysMenu record) throws ManagerException;

    int batchDelete(long[] ids) throws ManagerException;

    Page<SysMenu> findByPage(Page<SysMenu> pageRequest, Map<String, Object> map) throws ManagerException;

	List<SysMenu> getAllSysmenu() throws ManagerException;
	
	List<SysMenu> selectChildByParent(Long parentid,Integer type) throws ManagerException;
	
	
	
}