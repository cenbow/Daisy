package com.yourong.core.sys.manager.impl;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.sys.dao.SysRoleMapper;
import com.yourong.core.sys.manager.SysRoleManager;
import com.yourong.core.sys.model.SysRole;
import com.yourong.core.sys.model.SysRoleMenu;

@Service
public class SysRoleManagerImpl implements SysRoleManager {

	@Autowired
	private SysRoleMapper sysRoleMapper;

	@Override
	public int deleteByPrimaryKey(Long id) throws ManagerException {

		try {
			return sysRoleMapper.deleteByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}

	}

	@Override
	public int insert(SysRole record) throws ManagerException {
		try {
			return sysRoleMapper.insert(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int insertSelective(SysRole record) throws ManagerException {

		try {
			return sysRoleMapper.insertSelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public SysRole selectByPrimaryKey(Long id) throws ManagerException {
		try {
			return sysRoleMapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public SysRole selectRoleMenus(Long id) throws ManagerException {
		try {
			return sysRoleMapper.selectRoleMenus(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateByPrimaryKeySelective(SysRole record)
			throws ManagerException {
		try {
			return sysRoleMapper.updateByPrimaryKeySelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateByPrimaryKey(SysRole record) throws ManagerException {
		try {
			return sysRoleMapper.updateByPrimaryKey(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Page<SysRole> findByPage(Page<SysRole> pageRequest, @Param("map") Map<String, Object> map) throws ManagerException {
		try {
			return sysRoleMapper.findByPage(pageRequest, map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int batchDelete(long[] ids) throws ManagerException {
		try {
			return sysRoleMapper.batchDelete(ids);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int batchInsertRoleAndMenus(Long id, long[] menus) throws ManagerException{
		
		try {
			List<SysRoleMenu> list = Lists.newArrayList();
			SysRoleMenu  temp = null;
			for(long menuID: menus){
				temp = new  SysRoleMenu();
				temp.setRoleId(id);
				temp.setMenuId(menuID);
				list.add(temp);
			}			
			return sysRoleMapper.batchInsertRoleAndMenus(list);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int batchDeleteRoleAndMenus(Long id) throws ManagerException {
		try {					
			return sysRoleMapper.batchDeleteRoleAndMenus(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<SysRole> selectAllSysRole() throws ManagerException{
		try {
			return sysRoleMapper.selectAllSysRole();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public SysRole checkRoleNameExists(String name, long selfId)
			throws ManagerException {
		try {
			return sysRoleMapper.checkRoleNameExists(name, selfId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

}
