package com.yourong.backend.cms.service;

import java.util.Map;

import com.yourong.common.pageable.Page;
import com.yourong.core.cms.model.CmsLink;

public interface CmsLinkService {
	/** 删除 **/
	Integer deleteByPrimaryKey(Long id);

	/** 插入 **/
	Integer insert(CmsLink record);

	/** 更新有选择的 **/
	Integer updateByPrimaryKeySelective(CmsLink record);

	/** 查询根据主键 **/
	CmsLink selectByPrimaryKey(Long id);

	/** 更新根据主键 **/
	Integer updateByPrimaryKey(CmsLink record);

	/** 分页查询 **/
	Page<CmsLink> findByPage(Page<CmsLink> pageRequest, Map<String, Object> map);

	/** 批量删除 **/
	Integer batchDelete(long[] ids);
}
