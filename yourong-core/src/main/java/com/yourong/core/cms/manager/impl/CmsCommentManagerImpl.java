package com.yourong.core.cms.manager.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.cms.dao.CmsCommentMapper;
import com.yourong.core.cms.manager.CmsCommentManager;
import com.yourong.core.cms.model.CmsComment;

@Service
public class CmsCommentManagerImpl implements CmsCommentManager {

	@Autowired
	private CmsCommentMapper cmsCommentMapper;

	@Override
	public int deleteByPrimaryKey(Long id) throws ManagerException {
		try {
			return cmsCommentMapper.deleteByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int insert(CmsComment record) throws ManagerException {
		try {
			return cmsCommentMapper.insert(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateByPrimaryKeySelective(CmsComment record)
			throws ManagerException {
		try {
			return cmsCommentMapper.updateByPrimaryKeySelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public CmsComment selectByPrimaryKey(Long id) throws ManagerException {
		try {
			return cmsCommentMapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateByPrimaryKey(CmsComment record) throws ManagerException {
		try {
			return cmsCommentMapper.updateByPrimaryKey(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Page<CmsComment> findByPage(Page<CmsComment> pageRequest,
			Map<String, Object> map) throws ManagerException {
		try {
			return cmsCommentMapper.findByPage(pageRequest, map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int batchDelete(long[] ids) throws ManagerException {
		try {
			return cmsCommentMapper.batchDelete(ids);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

}
