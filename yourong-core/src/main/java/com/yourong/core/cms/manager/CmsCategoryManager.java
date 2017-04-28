package com.yourong.core.cms.manager;

import java.util.List;
import java.util.Map;



import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.cms.model.CmsCategory;

public interface CmsCategoryManager {
	int deleteByPrimaryKey(Long id) throws ManagerException;

	int insert(CmsCategory record) throws ManagerException;

	int updateByPrimaryKeySelective(CmsCategory record) throws ManagerException;

	CmsCategory selectByPrimaryKey(Long id) throws ManagerException;
	
	String selectNameById(Long id) throws ManagerException;

	int updateByPrimaryKey(CmsCategory record) throws ManagerException;

	Page<CmsCategory> findByPage(Page<CmsCategory> pageRequest,Map<String, Object> map) throws ManagerException;

	int batchDelete(long[] ids) throws ManagerException;
	
	List<CmsCategory> selectAllCmsCategory() throws ManagerException;
}