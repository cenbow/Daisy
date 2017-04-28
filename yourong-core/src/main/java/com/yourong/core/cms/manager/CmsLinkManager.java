package com.yourong.core.cms.manager;

import java.util.Map;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.cms.model.CmsLink;

public interface CmsLinkManager {
	/** 删除 **/
	int deleteByPrimaryKey(Long id) throws ManagerException;

	/** 插入 **/
	int insert(CmsLink record) throws ManagerException;

	/** 更新有选择的 **/
	int updateByPrimaryKeySelective(CmsLink record) throws ManagerException;

	/** 查询 **/
	CmsLink selectByPrimaryKey(Long id) throws ManagerException;

	/** 更新 **/
	int updateByPrimaryKey(CmsLink record) throws ManagerException;

	/** 分页查询 **/
	Page<CmsLink> findByPage(Page<CmsLink> pageRequest,
			Map<String, Object> map) throws ManagerException;

	/** 批量删除 **/
	int batchDelete(long[] ids) throws ManagerException;
}
