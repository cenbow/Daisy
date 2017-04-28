package com.yourong.backend.cms.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.backend.cms.service.CmsCategoryService;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.DateUtils;
import com.yourong.core.cms.manager.CmsCategoryManager;
import com.yourong.core.cms.model.CmsCategory;
@Service
public class CmsCategoryServiceImpl implements CmsCategoryService {

	private static Logger logger = LoggerFactory
			.getLogger(CmsCategoryServiceImpl.class);
	@Autowired
	private CmsCategoryManager cmsCategoryManager;

	@Override
	public Integer deleteByPrimaryKey(Long id) {
		try {
			return cmsCategoryManager.deleteByPrimaryKey(id);
		} catch (Exception e) {
			logger.error("删除栏目信息失败,id=" + id, e);
		}
		return null;
	}

	@Override
	public Integer insert(CmsCategory record) {
		try {
			return cmsCategoryManager.insert(record);
		} catch (Exception e) {
			logger.error("插入栏目信息失败,cmsCategory=" + record, e);
		}
		return null;
	}

	@Override
	public Integer updateByPrimaryKeySelective(CmsCategory record) {
		try {
			return cmsCategoryManager.updateByPrimaryKeySelective(record);
		} catch (Exception e) {
			logger.error("更新栏目信息失败,cmsCategory=" + record, e);
		}
		return null;
	}

	@Override
	public CmsCategory selectByPrimaryKey(Long id) {
		try {
			return cmsCategoryManager.selectByPrimaryKey(id);
		} catch (Exception e) {
			logger.error("查询类目信息失败,id=" + id, e);
		}
		return null;
	}

	@Override
	public Integer updateByPrimaryKey(CmsCategory record) {
		try {
			return cmsCategoryManager.updateByPrimaryKey(record);
		} catch (Exception e) {
			logger.error("更新栏目信息失败,cmsCategory=" + record, e);
		}
		return null;
	}

	@Override
	public Page<CmsCategory> findByPage(Page<CmsCategory> pageRequest,
			Map<String, Object> map) {
		try {
			return cmsCategoryManager.findByPage(pageRequest, map);
		} catch (Exception e) {
			logger.error("分页查询栏目信息失败,", e);
		}
		return null;
	}

	@Override
	public Integer batchDelete(long[] ids) {
		try {
			return cmsCategoryManager.batchDelete(ids);
		} catch (Exception e) {
			logger.error("批量删除栏目信息失败,ids=" + ids, e);
		}
		return null;
	}

	@Override
	public List<CmsCategory> selectAllCmsCategory() {
		try {
			return cmsCategoryManager.selectAllCmsCategory();
		} catch (Exception e) {
			logger.error("查询所有栏目信息失败", e);
		}
		return null;
	}

}
