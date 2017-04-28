package com.yourong.backend.sys.service;

import java.util.List;
import java.util.Map;

import com.yourong.common.pageable.Page;
import com.yourong.core.sys.model.SysMenu;

public interface SysMenuService {
	Integer deleteByPrimaryKey(Long id);

    Integer insert(SysMenu record);

    SysMenu selectByPrimaryKey(Long id);

    Integer updateByPrimaryKey(SysMenu record);

    Integer updateByPrimaryKeySelective(SysMenu record);

    Integer batchDelete(long[] ids);

    Page<SysMenu> findByPage(Page<SysMenu> pageRequest, Map<String, Object> map);
    
	public List<SysMenu> getAllSysmenu();
	
	List<SysMenu> getTreeSysmenus();
}