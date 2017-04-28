package com.yourong.core.cms.manager.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.cms.dao.CmsCategoryMapper;
import com.yourong.core.cms.manager.CmsCategoryManager;
import com.yourong.core.cms.model.CmsCategory;

@Service
public class CmsCategoryManagerImpl implements CmsCategoryManager{

	@Autowired
	private CmsCategoryMapper cmsCategoryMapper;
	@Override
	public int deleteByPrimaryKey(Long id) throws ManagerException {
		try {
			int result = cmsCategoryMapper.deleteByPrimaryKey(id);
			return result;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int insert(CmsCategory record) throws ManagerException {
		try {
			return cmsCategoryMapper.insert(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateByPrimaryKeySelective(CmsCategory record)
			throws ManagerException {
		try {
			return cmsCategoryMapper.updateByPrimaryKeySelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public CmsCategory selectByPrimaryKey(Long id) throws ManagerException {
		try {
			return cmsCategoryMapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateByPrimaryKey(CmsCategory record) throws ManagerException {
		try {
			return cmsCategoryMapper.updateByPrimaryKey(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Page<CmsCategory> findByPage(Page<CmsCategory> pageRequest,
			Map<String, Object> map) throws ManagerException {
		try {
			return cmsCategoryMapper.findByPage(pageRequest, map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int batchDelete(long[] ids) throws ManagerException {
		try {
			return cmsCategoryMapper.batchDelete(ids);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<CmsCategory> selectAllCmsCategory() throws ManagerException {
		try {
			return cmsCategoryMapper.selectAllCmsCategory();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public String selectNameById(Long id) throws ManagerException {
		try {
			return cmsCategoryMapper.selectNameById(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
}