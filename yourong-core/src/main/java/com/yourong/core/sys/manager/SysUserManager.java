package com.yourong.core.sys.manager;

import java.util.Map;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.sys.model.SysUser;

public interface SysUserManager {
	int deleteByPrimaryKey(Long id) throws ManagerException;

    int insert(SysUser record) throws ManagerException ;

    SysUser selectByPrimaryKey(Long id) throws ManagerException;

    int updateByPrimaryKey(SysUser record) throws ManagerException ;

    int updateByPrimaryKeySelective(SysUser record) throws ManagerException ;

    int batchDelete(long[] ids) throws ManagerException;

    Page<SysUser> findByPage(Page<SysUser> pageRequest, Map<String, Object> map) throws ManagerException;
    
    
    SysUser selectByLoginName(String loginName) throws ManagerException;
    
    SysUser selectSysUserRoleByLoginName(String loginName) throws ManagerException;
    
    /**
     * 检查登陆账号是否存在
     * @param loginName 登陆账号
     * @param selfId 当前用户ID
     * @return
     */
    SysUser checkLoginNameExists(String loginName, long selfId) throws ManagerException;

}
