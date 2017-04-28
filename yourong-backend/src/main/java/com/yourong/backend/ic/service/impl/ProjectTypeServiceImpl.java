package com.yourong.backend.ic.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.backend.ic.service.ProjectTypeService;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.manager.ProjectTypeManager;
import com.yourong.core.ic.model.ProjectType;

@Service
public class ProjectTypeServiceImpl implements ProjectTypeService {
	private static Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);
	@Autowired
	private ProjectTypeManager projectTypeManager;

	public int deleteByPrimaryKey(Long id) {
		int result = 0;
		try {
			result = projectTypeManager.deleteByPrimaryKey(id);
		} catch (ManagerException e) {
			logger.error("删除项目类型失败，id={}", id, e);
		}
		return result;
	}

	public int insert(ProjectType projectType) {
		int result = 0;
		try {
			result = projectTypeManager.insert(projectType);
		} catch (ManagerException e) {
			logger.error("插入项目类型失败，projectTtype={}", projectType, e);
		}
		return result;
	}

	public ProjectType selectByPrimaryKey(Long id) {
		try {
			return projectTypeManager.selectByPrimaryKey(id);
		} catch (ManagerException e) {
			logger.error("查询项目类型失败，id={}", id, e);
		}
		return null;
	}

	public int updateByPrimaryKey(ProjectType projectType) {
		try {
			return projectTypeManager.updateByPrimaryKey(projectType);
		} catch (ManagerException e) {
			logger.error("更新项目类型失败，projectType={}", projectType, e);
		}
		return 0;
	}

	public int updateByPrimaryKeySelective(ProjectType projectType) {
		try {
			return projectTypeManager.updateByPrimaryKeySelective(projectType);
		} catch (ManagerException e) {
			logger.error("更新项目类型失败，projectType={}", projectType, e);
		}
		return 0;
	}

	@Override
	public Page findByPage(Page pageRequest, Map map) {
		try {
			return projectTypeManager.findByPage(pageRequest, map);
		} catch (ManagerException e) {
			logger.error("分页获取项目类型失败，pageRequest={}，map={}", pageRequest, map, e);
		}
		return null;
	}

	@Override
	public int batchDelete(long[] id) {
		try {
			return projectTypeManager.batchDelete(id);
		} catch (ManagerException e) {
			logger.error("批量删除项目类型失败，id", id, e);
		}
		return 0;
	}

	@Override
	public ProjectType selectProjectTypeDetail(ProjectType projectType) {
		try {
			return projectTypeManager.selectProjectTypeDetail(projectType);
		} catch (ManagerException e) {
			logger.error("更具担保方式、担保物类型，是否分期查询的失败，projectType", projectType, e);
		}
		return null;
	}


}