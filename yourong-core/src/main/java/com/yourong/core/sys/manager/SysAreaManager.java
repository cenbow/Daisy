package com.yourong.core.sys.manager;

import java.util.List;
import java.util.Map;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.sys.model.SysArea;

public interface SysAreaManager {
    int deleteByPrimaryKey(Long id) throws ManagerException;

    int insert(SysArea record) throws ManagerException;

    SysArea selectByPrimaryKey(Long id) throws ManagerException;

    int updateByPrimaryKey(SysArea record) throws ManagerException;

    int updateByPrimaryKeySelective(SysArea record) throws ManagerException;

    int batchDelete(long[] ids) throws ManagerException;

    Page<SysArea> findByPage(Page<SysArea> pageRequest, Map<String, Object> map) throws ManagerException;

	public List<SysArea> getAllSysArea() throws ManagerException;
	
	/**根据类别查找区域  type=2查询所有省，type=3 查询所有市，type=4 查询所有区 **/
	public List<SysArea> getSysAreasByType(Integer type) throws ManagerException;
	
	/**根据父级区域编码查找区域**/
	public List<SysArea> getSysAreasByParentId(Long parentId) throws ManagerException;
	
	/**
	 * 根据code获取parentid
	 * @param code
	 * @return
	 * @throws ManagerException
	 */
	public Long getParentIdByCode(String code) throws ManagerException;
	
	/**
	 * 根据code获得parentids
	 * @param code
	 * @return
	 * @throws ManagerException
	 */
	public List<Long> getParentIdsByCode(Long code) throws ManagerException;
}