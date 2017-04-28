package com.yourong.backend.cms.service;

import java.util.List;
import java.util.Map;

import com.yourong.common.pageable.Page;
import com.yourong.core.cms.model.CmsCategory;

public interface CmsCategoryService {
	Integer deleteByPrimaryKey(Long id);

	Integer insert(CmsCategory record);

	Integer updateByPrimaryKeySelective(CmsCategory record);

	CmsCategory selectByPrimaryKey(Long id);

	Integer updateByPrimaryKey(CmsCategory record);

	Page<CmsCategory> findByPage(Page<CmsCategory> pageRequest,Map<String, Object> map);

	Integer batchDelete(long[] ids);
	
	List<CmsCategory> selectAllCmsCategory();
}
