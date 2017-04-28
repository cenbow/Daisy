package com.yourong.core.sys.manager.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.sys.dao.SysMenuMapper;
import com.yourong.core.sys.manager.SysMenuManager;
import com.yourong.core.sys.model.SysMenu;

@Component
public class SysMenuManagerImpl implements SysMenuManager {
    @Autowired
    private SysMenuMapper sysMenuMapper;

    public int deleteByPrimaryKey(Long id)  throws ManagerException{
    	try {
    		return sysMenuMapper.deleteByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
    }

    public int insert(SysMenu sysMenu)  throws ManagerException{
    	try {
    		return sysMenuMapper.insert( sysMenu );
		} catch (Exception e) {
			throw new ManagerException(e);
		}
    }

    public SysMenu selectByPrimaryKey(Long id)  throws ManagerException{
    	try {
    		 return sysMenuMapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
    }

    public int updateByPrimaryKey(SysMenu sysMenu)  throws ManagerException{
    	try {
    		return sysMenuMapper.updateByPrimaryKey(sysMenu );
		} catch (Exception e) {
			throw new ManagerException(e);
		}
    }

    public int updateByPrimaryKeySelective(SysMenu sysMenu)  throws ManagerException{
    	try {
    		return sysMenuMapper.updateByPrimaryKeySelective(sysMenu );
		} catch (Exception e) {
			throw new ManagerException(e);
		}
    }

    public int batchDelete(long[] ids)  throws ManagerException{
    	try {
    		return sysMenuMapper.batchDelete(ids);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
    }

    public Page<SysMenu> findByPage(Page<SysMenu> pageRequest, Map<String, Object> map)  throws ManagerException{
    	try {
    		map.put("delFlag", 0);
    		return sysMenuMapper.findByPage(pageRequest,map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
    }

	@Override
	public List<SysMenu> getAllSysmenu() throws ManagerException {
		try {
    		return sysMenuMapper.selectAllSysMenu();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<SysMenu> selectChildByParent(Long parentid,Integer type) throws ManagerException {
		try {
    		return sysMenuMapper.selectChildByParent(parentid,type);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	
}