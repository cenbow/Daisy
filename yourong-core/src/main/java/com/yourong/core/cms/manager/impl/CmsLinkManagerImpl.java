package com.yourong.core.cms.manager.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.cms.dao.CmsLinkMapper;
import com.yourong.core.cms.manager.CmsLinkManager;
import com.yourong.core.cms.model.CmsLink;

@Service
public class CmsLinkManagerImpl implements CmsLinkManager {

	@Autowired
	private CmsLinkMapper cmsLinkMapper;

	@Override
	public int deleteByPrimaryKey(Long id) throws ManagerException {
		try {
			return cmsLinkMapper.deleteByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int insert(CmsLink record) throws ManagerException {
		try {
			return cmsLinkMapper.insert(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateByPrimaryKeySelective(CmsLink record)
			throws ManagerException {
		try {
			return cmsLinkMapper.updateByPrimaryKeySelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public CmsLink selectByPrimaryKey(Long id) throws ManagerException {
		try {
			return cmsLinkMapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateByPrimaryKey(CmsLink record) throws ManagerException {
		try {
			return cmsLinkMapper.updateByPrimaryKey(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Page<CmsLink> findByPage(Page<CmsLink> pageRequest,
			Map<String, Object> map) throws ManagerException {
		try {
			return cmsLinkMapper.findByPage(pageRequest, map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int batchDelete(long[] ids) throws ManagerException {
		try {
			return cmsLinkMapper.batchDelete(ids);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

}
