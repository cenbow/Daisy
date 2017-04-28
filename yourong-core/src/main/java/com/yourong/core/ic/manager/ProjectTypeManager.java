package com.yourong.core.ic.manager;

import java.util.Map;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.ProjectType;

public interface ProjectTypeManager {
	int deleteByPrimaryKey(Long id) throws ManagerException;
	
	int insert(ProjectType record) throws ManagerException;

	int insertSelective(ProjectType record) throws ManagerException;

	ProjectType selectByPrimaryKey(Long id) throws ManagerException;

	int updateByPrimaryKeySelective(ProjectType record) throws ManagerException;

	int updateByPrimaryKey(ProjectType record) throws ManagerException;

	Page<ProjectType> findByPage(Page<ProjectType> pageRequest, Map<String, Object> map) throws ManagerException;

	public int batchDelete(long[] ids) throws ManagerException;
	
	ProjectType selectProjectTypeDetail(ProjectType projectType) throws ManagerException;
}
