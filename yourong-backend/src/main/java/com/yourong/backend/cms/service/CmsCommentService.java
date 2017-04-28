package com.yourong.backend.cms.service;

import java.util.Map;

import com.yourong.common.pageable.Page;
import com.yourong.core.cms.model.CmsComment;

public interface CmsCommentService {
	/** 删除 **/
	Integer deleteByPrimaryKey(Long id);

	/** 插入 **/
	Integer insert(CmsComment record);

	/** 更新有选择的 **/
	Integer updateByPrimaryKeySelective(CmsComment record);

	/** 查询根据主键 **/
	CmsComment selectByPrimaryKey(Long id);

	/** 更新根据主键 **/
	Integer updateByPrimaryKey(CmsComment record);

	/** 分页查询 **/
	Page<CmsComment> findByPage(Page<CmsComment> pageRequest,
			Map<String, Object> map);

	/** 批量删除 **/
	Integer batchDelete(long[] ids);
}
