package com.yourong.api.service;

import java.util.List;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.sys.model.SysArea;

public interface SysAreaService {

	/**
	 * 获取所有区域
	 * @return
	 */
	public List<SysArea> getAllSysArea();
	
	/**
	 * 根据code获取parentid
	 * @param parentId
	 * @return
	 */
	public List<SysArea> getSysAreasByParentId(Long parentId);
	
	/**
	 * 根据code获得parentids
	 * @param code
	 * @return
	 */
	public List<Long> getParentIdsByCode(Long code);
	
}