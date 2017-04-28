package com.yourong.backend.sys.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.sys.model.SysDict;

public interface SysDictService {
	public Integer deleteByPrimaryKey(Long id);

	public Integer insert(SysDict record);

	public SysDict selectByPrimaryKey(Long id);	

	public Integer updateByPrimaryKey(SysDict record);

	public Integer updateByPrimaryKeySelective(SysDict record);

	public Integer batchDelete(long[] ids);

    public Page<SysDict> findByPage(Page<SysDict> pageRequest, Map<String, Object> map);
    
    
    public  List<SysDict>  findByGroupName (String groupName);

	public Integer insertSelective(SysDict sysDict)throws ManagerException;

	public ResultDO<SysDict> deleteId(Long id);

	public ResultDO<BigDecimal> queryBalance(Long id);

	public SysDict selectByKey(String key);

	public SysDict findByGroupNameAndKey(String groupName,String key);
}