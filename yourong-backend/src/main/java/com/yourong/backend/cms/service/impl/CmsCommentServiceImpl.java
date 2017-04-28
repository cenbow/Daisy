package com.yourong.backend.cms.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.backend.cms.service.CmsCommentService;
import com.yourong.common.pageable.Page;
import com.yourong.core.cms.manager.CmsCommentManager;
import com.yourong.core.cms.model.CmsComment;

@Service
public class CmsCommentServiceImpl implements CmsCommentService {
	private static Logger logger = LoggerFactory
			.getLogger(CmsCommentServiceImpl.class);
	@Autowired
	private CmsCommentManager cmsCommentManager;

	@Override
	public Integer deleteByPrimaryKey(Long id) {
		try {
			return cmsCommentManager.deleteByPrimaryKey(id);
		} catch (Exception e) {
			logger.debug("删除评论失败,id=" + id, e);
		}
		return null;
	}

	@Override
	public Integer insert(CmsComment record) {
		try {
			return cmsCommentManager.insert(record);
		} catch (Exception e) {
			logger.debug("插入评论失败,cmsComment=" + record, e);
		}
		return null;
	}

	@Override
	public Integer updateByPrimaryKeySelective(CmsComment record) {
		try {
			return cmsCommentManager.updateByPrimaryKeySelective(record);
		} catch (Exception e) {
			logger.debug("更新评论失败,cmsComment=" + record, e);
		}
		return null;
	}

	@Override
	public CmsComment selectByPrimaryKey(Long id) {
		try {
			return cmsCommentManager.selectByPrimaryKey(id);
		} catch (Exception e) {
			logger.debug("查询评论信息失败,id=" + id, e);
		}
		return null;
	}

	@Override
	public Integer updateByPrimaryKey(CmsComment record) {
		try {
			return cmsCommentManager.updateByPrimaryKey(record);
		} catch (Exception e) {
			logger.debug("更新评论信息失败,cmsComment=" + record, e);
		}
		return null;
	}

	@Override
	public Page<CmsComment> findByPage(Page<CmsComment> pageRequest,
			Map<String, Object> map) {
		try {
			return cmsCommentManager.findByPage(pageRequest, map);
		} catch (Exception e) {
			logger.debug("分页获取评论信息失败,", e);
		}
		return null;
	}

	@Override
	public Integer batchDelete(long[] ids) {
		try {
			return cmsCommentManager.batchDelete(ids);
		} catch (Exception e) {
			logger.debug("批量删除评论信息失败,ids=" + ids, e);
		}
		return null;
	}

}
