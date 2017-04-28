package com.yourong.core.sys.manager.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.sys.dao.SysUserMapper;
import com.yourong.core.sys.manager.SysUserManager;
import com.yourong.core.sys.model.SysUser;

@Component
public class SysUserManagerImpl implements SysUserManager {

	@Autowired
	private SysUserMapper sysUserMapper;

	@Override
	public int deleteByPrimaryKey(Long id) throws ManagerException {
		try {
			return sysUserMapper.deleteByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}

	}

	@Override
	public int insert(SysUser record) throws ManagerException {
		try {
			return sysUserMapper.insert(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public SysUser selectByPrimaryKey(Long id) throws ManagerException {
		try {
			return sysUserMapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateByPrimaryKey(SysUser record) throws ManagerException {
		try {
			return sysUserMapper.updateByPrimaryKey(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateByPrimaryKeySelective(SysUser record)
			throws ManagerException {
		try {
			return sysUserMapper.updateByPrimaryKeySelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int batchDelete(long[] ids) throws ManagerException {
		try {
			return sysUserMapper.batchDelete(ids);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Page<SysUser> findByPage(Page<SysUser> pageRequest, Map<String, Object> map)throws ManagerException {
		try {
			return sysUserMapper.findByPage(pageRequest, map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public SysUser selectByLoginName(String loginName) throws ManagerException {
		try {
			return sysUserMapper.selectByLoginName(loginName);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public SysUser selectSysUserRoleByLoginName(String loginName)
			throws ManagerException {
		try {
    		return sysUserMapper.selectSysUserRoleByLoginName(loginName);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public SysUser checkLoginNameExists(String loginName, long selfId) throws ManagerException {
		try {
			return sysUserMapper.checkLoginNameExists(loginName, selfId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

}
