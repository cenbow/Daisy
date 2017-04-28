package com.yourong.core.sys.manager;

import java.util.List;
import java.util.Map;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.sys.model.SysDict;

public interface SysDictManager {
	Integer deleteByPrimaryKey(Long id) throws ManagerException;

	Integer insert(SysDict record) throws ManagerException;

    SysDict selectByPrimaryKey(Long id) throws ManagerException;

    Integer updateByPrimaryKey(SysDict record) throws ManagerException;

    Integer updateByPrimaryKeySelective(SysDict record) throws ManagerException;

    Integer batchDelete(long[] ids) throws ManagerException;

    Page<SysDict> findByPage(Page<SysDict> pageRequest, Map<String, Object> map) throws ManagerException;

	List<SysDict> findByGroupName(String groupName) throws ManagerException;

    SysDict findByGroupNameAndKey(String groupName,String key)throws ManagerException;
    
    Integer updateByGroupNameAndKey(SysDict sysDict) throws ManagerException;

	Integer insertSelective(SysDict sysDict)throws ManagerException;
    /**
     * 
     * @Description:短信提醒
     * @throws ManagerException
     * @author: chaisen
     * @time:2016年4月7日 下午1:05:28
     */
	void sendSmsRemind()throws ManagerException;

	SysDict selectByKey(String key)throws ManagerException;

}