package com.yourong.core.ic.manager.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.dao.ProjectTypeMapper;
import com.yourong.core.ic.manager.ProjectTypeManager;
import com.yourong.core.ic.model.ProjectType;

@Component
public class ProjectTypeManagerImpl implements ProjectTypeManager {

	@Autowired
	private ProjectTypeMapper projectTypeMapper;

	@Override
	public int insert(ProjectType record) throws ManagerException {
		try {
			return projectTypeMapper.insert(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int insertSelective(ProjectType record) throws ManagerException {
		try {
			return projectTypeMapper.insertSelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public ProjectType selectByPrimaryKey(Long id) throws ManagerException {
		try {
			return projectTypeMapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateByPrimaryKeySelective(ProjectType record) throws ManagerException {
		try {
			return projectTypeMapper.updateByPrimaryKeySelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateByPrimaryKey(ProjectType record) throws ManagerException {
		try {
			return projectTypeMapper.updateByPrimaryKey(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Page<ProjectType> findByPage(Page<ProjectType> pageRequest, Map<String, Object> map) throws ManagerException {
		try {
			map.put("startRow", pageRequest.getiDisplayStart());
			map.put("pageSize", pageRequest.getiDisplayLength());
			int totalCount = projectTypeMapper.selectForPaginTotalCount(map);
			pageRequest.setiTotalDisplayRecords(totalCount);
			pageRequest.setiTotalRecords(totalCount);
			List<ProjectType> selectForPagin = projectTypeMapper.selectForPagin(map);
			pageRequest.setData(selectForPagin);
			return pageRequest;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int deleteByPrimaryKey(Long id) throws ManagerException{
		try {
			projectTypeMapper.deleteByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
		return 0;
	}

	@Override
	public int batchDelete(long[] ids) throws ManagerException {
		try {
			projectTypeMapper.batchDelete(ids);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
		return 0;
	}

	@Override
	public ProjectType selectProjectTypeDetail(ProjectType projectType) throws ManagerException{
		try {
			return projectTypeMapper.selectProjectTypeDetail(projectType);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

}
