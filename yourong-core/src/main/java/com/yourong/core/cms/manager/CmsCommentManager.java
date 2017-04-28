package com.yourong.core.cms.manager;

import java.util.Map;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.cms.model.CmsComment;

public interface CmsCommentManager {
	/** 删除 **/
	int deleteByPrimaryKey(Long id) throws ManagerException;

	/** 插入 **/
	int insert(CmsComment record) throws ManagerException;

	/** 更新有选择的 **/
	int updateByPrimaryKeySelective(CmsComment record) throws ManagerException;

	/** 查询 **/
	CmsComment selectByPrimaryKey(Long id) throws ManagerException;

	/** 更新 **/
	int updateByPrimaryKey(CmsComment record) throws ManagerException;

	/** 分页查询 **/
	Page<CmsComment> findByPage(Page<CmsComment> pageRequest,
			Map<String, Object> map) throws ManagerException;

	/** 批量删除 **/
	int batchDelete(long[] ids) throws ManagerException;

}
