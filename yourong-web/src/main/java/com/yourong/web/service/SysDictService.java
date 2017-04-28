package com.yourong.web.service;

import java.util.List;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.sys.model.SysDict;

public interface SysDictService {
	
	/**
	 * 通过组名获取数据字典记录
	 * @param groupName
	 * @return
	 */
	public  List<SysDict>  findByGroupName (String groupName);

	public SysDict findByGroupNameAndKey(String groupName,String key) throws ManagerException;
}