package com.yourong.backend.sys.service;

import java.util.Map;

import com.yourong.common.pageable.Page;
import com.yourong.core.sys.model.SysUser;

public interface SysUserService {
    int deleteByPrimaryKey(Long id);

    int insert(SysUser record);

    SysUser selectByPrimaryKey(Long id);

    int updateByPrimaryKey(SysUser record);

    int updateByPrimaryKeySelective(SysUser record);
    
    int updateLoginIPandDate(SysUser record);

    int batchDelete(long[] ids);

    Page<SysUser> findByPage(Page<SysUser> pageRequest, Map<String, Object> map);
    
    
    SysUser selectByLoginName(String loginName) ;
    
    
    SysUser selectSysUserRoleByLoginName(String loginName);
    
    /**
     * 为用户分配角色
     * @param user
     */
    void addSysUserRole(SysUser user);
    
    /**
     * 检查登录账号是否存在
     * @param loginName 登录账号
     * @return
     */
    boolean checkLoginNameExists(String loginName);
    
    /**
     * 检查登录账号是否存在
     * @param loginName 登录账号
     * @param selfId 当前用户ID
     * @return
     */
    boolean checkLoginNameExists(String loginName, long selfId);
    
    
}