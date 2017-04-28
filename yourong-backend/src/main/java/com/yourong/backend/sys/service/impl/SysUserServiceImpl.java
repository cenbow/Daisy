package com.yourong.backend.sys.service.impl;

import java.util.Map;

import com.yourong.common.thirdparty.sinapay.common.util.StringUtil;
import org.apache.shiro.authc.credential.PasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.backend.sys.service.SysUserRoleService;
import com.yourong.backend.sys.service.SysUserService;
import com.yourong.common.annotation.BussAnnotation;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.CryptHelper;
import com.yourong.core.sys.manager.SysUserManager;
import com.yourong.core.sys.model.SysUser;

@Service
public class SysUserServiceImpl implements SysUserService {
	private static Logger logger = LoggerFactory.getLogger(SysUserServiceImpl.class);
	@Autowired
	private SysUserManager sysUserManager;
	
	// 账号加密
	@Autowired
	private PasswordService passwordService;
	
	//角色
	@Autowired
	private SysUserRoleService sysUserRoleService;
	

	public int deleteByPrimaryKey(Long id) {
		int result = 0;
		try {
			result = sysUserManager.deleteByPrimaryKey(id);
		} catch (ManagerException e) {
			logger.error("deleteByPrimaryKey", e);
		}
		return result;
	}

	public int insert(SysUser sysUser) {
		int result = 0;
		try {
			String password = sysUser.getPassword();
			String encryptToSHA = CryptHelper.encryptByase(password);
			sysUser.setPassword(encryptToSHA);
			sysUser.setDelFlag(0);
			result = sysUserManager.insert(sysUser);
			addSysUserRole(sysUser);
		} catch (ManagerException e) {
			logger.error("insert", e);
		}
		return result;
	}

	public SysUser selectByPrimaryKey(Long id) {
		try {
			SysUser sysUser = sysUserManager.selectByPrimaryKey(id);
			if(sysUser != null){
				sysUser.setRoleIds(sysUserRoleService.getSysUserRoleIdsByUserId(sysUser.getId()));
			}
			return sysUser;
		} catch (ManagerException e) {
			logger.error("insert", e);
		}
		return null;
	}

	public int updateByPrimaryKey(SysUser sysUser) {
		try {
			addSysUserRole(sysUser);
			return sysUserManager.updateByPrimaryKey(sysUser);
		} catch (ManagerException e) {
			logger.error("updateByPrimaryKey", e);
		}
		return 0;
	}

	public int updateByPrimaryKeySelective(SysUser sysUser) {
		try {
			addSysUserRole(sysUser);

			if(com.yourong.common.util.StringUtil.isNotBlank(sysUser.getNewPassword())){
				String password = sysUser.getNewPassword();
				String encryptToSHA = CryptHelper.encryptByase(password);
				sysUser.setPassword(encryptToSHA);
			}else{
				SysUser user = sysUserManager.selectByPrimaryKey(sysUser.getId());
				sysUser.setPassword(user.getPassword());
			}
			return sysUserManager.updateByPrimaryKeySelective(sysUser);
		} catch (ManagerException e) {
			logger.error("updateByPrimaryKeySelective", e);
		}
		return 0;
	}

	public int batchDelete(long[] ids) {
		try {
			if(ids.length < 1) {
				return 0;
			}
			return sysUserManager.batchDelete(ids);
		} catch (ManagerException e) {
			logger.error("batchDelete", e);
		}
		return 0;
	}
	@BussAnnotation(moduleName="用户角色分页查询",option="查询")
	public Page<SysUser> findByPage(Page<SysUser> pageRequest, Map<String, Object> map) {
		try {
			return sysUserManager.findByPage(pageRequest, map);
		} catch (ManagerException e) {
			logger.error("findByPage", e);
		}
		return pageRequest;
	}

	@Override
	public SysUser selectByLoginName(String loginName) {
		
		try {
			return sysUserManager.selectByLoginName(loginName);
		} catch (ManagerException e) {
			logger.error("ManagerException", e);
		}
		return null;
	}

	@Override
	public int updateLoginIPandDate(SysUser record) {
		try {
			return sysUserManager.updateByPrimaryKeySelective(record);
		} catch (ManagerException e) {
			logger.error("updateByPrimaryKeySelective", e);
		}
		return 0;
	}

	@Override
	public SysUser selectSysUserRoleByLoginName(String loginName) {
		try {
			return sysUserManager.selectSysUserRoleByLoginName(loginName);
		} catch (ManagerException e) {
			logger.error("selectSysUserRoleByLoginName", e);
		}
		return null;
	}

	@Override
	public void addSysUserRole(SysUser user) {
		if(user != null){
			if(user.getId() != null){//修改用户
				sysUserRoleService.deleteSysUserRoleByUserId(user.getId());
				sysUserRoleService.addSysUserRole(user.getId(), user.getRoleIds());
			}else{//新增用户
				SysUser sysUser = selectByLoginName(user.getLoginName());
				if(sysUser != null && user.getRoleIds().length > 0) {
					sysUserRoleService.addSysUserRole(sysUser.getId(), user.getRoleIds());
				}
			}
		}
	}

	@Override
	public boolean checkLoginNameExists(String loginName) {
		try {
			SysUser user = sysUserManager.checkLoginNameExists(loginName, -1);
			if(user == null){
				return false;
			}
		} catch (ManagerException e) {
			logger.error("checkLoginNameExists", e);
		}
		return true;
	}

	@Override
	public boolean checkLoginNameExists(String loginName, long selfId) {
		try {
			SysUser user = sysUserManager.checkLoginNameExists(loginName, selfId);
			if(user == null){
				return false;
			}
		} catch (ManagerException e) {
			logger.error("checkLoginNameExists", e);
		}
		return true;
	}
}