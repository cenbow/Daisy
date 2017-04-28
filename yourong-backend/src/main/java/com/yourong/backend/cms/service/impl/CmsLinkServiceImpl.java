package com.yourong.backend.cms.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.backend.cms.service.CmsLinkService;
import com.yourong.common.pageable.Page;
import com.yourong.core.cms.manager.CmsLinkManager;
import com.yourong.core.cms.model.CmsLink;

@Service
public class CmsLinkServiceImpl implements CmsLinkService {

	private static Logger logger = LoggerFactory
			.getLogger(CmsLinkServiceImpl.class);

	@Autowired
	private CmsLinkManager cmsLinkManager;

	@Override
	public Integer deleteByPrimaryKey(Long id) {
		try {
			return cmsLinkManager.deleteByPrimaryKey(id);
		} catch (Exception e) {
			logger.debug("删除友情链接失败,id=" + id, e);
		}
		return null;
	}

	@Override
	public Integer insert(CmsLink record) {
		try {
			return cmsLinkManager.insert(record);
		} catch (Exception e) {
			logger.debug("插入友情链接失败,cmsLink=" + record, e);
		}
		return null;
	}

	@Override
	public Integer updateByPrimaryKeySelective(CmsLink record) {
		try {
			return cmsLinkManager.updateByPrimaryKeySelective(record);
		} catch (Exception e) {
			logger.debug("更新友情链接失败,cmsLink=" + record, e);
		}
		return null;
	}

	@Override
	public CmsLink selectByPrimaryKey(Long id) {
		try {
			return cmsLinkManager.selectByPrimaryKey(id);
		} catch (Exception e) {
			logger.debug("查询所有友情链接信息失败,id=" + id, e);
		}
		return null;
	}

	@Override
	public Integer updateByPrimaryKey(CmsLink record) {
		try {
			return cmsLinkManager.updateByPrimaryKey(record);
		} catch (Exception e) {
			logger.debug("更新友情链接信息失败,cmsLink=" + record, e);
		}
		return null;
	}

	@Override
	public Page<CmsLink> findByPage(Page<CmsLink> pageRequest,
			Map<String, Object> map) {
		try {
			return cmsLinkManager.findByPage(pageRequest, map);
		} catch (Exception e) {
			logger.debug("分页查询友情链接信息失败,", e);
		}
		return null;
	}

	@Override
	public Integer batchDelete(long[] ids) {
		try {
			return cmsLinkManager.batchDelete(ids);
		} catch (Exception e) {
			logger.debug("批量删除友情链接信息失败,ids=" + ids, e);
		}
		return null;
	}

}
