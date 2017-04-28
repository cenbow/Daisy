package com.yourong.backend.sys.service;

import java.util.List;
import java.util.Map;

import com.yourong.common.pageable.Page;
import com.yourong.core.sys.model.SysArea;

public interface SysAreaService {
    Integer deleteByPrimaryKey(Long id);

    Integer insert(SysArea record);

    SysArea selectByPrimaryKey(Long id);

    Integer updateByPrimaryKey(SysArea record);

    Integer updateByPrimaryKeySelective(SysArea record);

    Integer batchDelete(long[] ids);

    Page<SysArea> findByPage(Page<SysArea> pageRequest, Map<String, Object> map);

	public List<SysArea> getAllSysArea();
	
	public List<SysArea> getSysAreasByType(Integer type);
	
	public List<SysArea> getSysAreasByParentId(Long parentId);

	public List getParentIdsByCode(Long code);
}