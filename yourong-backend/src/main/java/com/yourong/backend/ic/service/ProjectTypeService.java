package com.yourong.backend.ic.service;

import java.util.Map;

import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.ProjectType;

public interface ProjectTypeService {
    int deleteByPrimaryKey(Long id);

    int insert(ProjectType record);

    ProjectType selectByPrimaryKey(Long id);


    int updateByPrimaryKey(ProjectType record);

    int updateByPrimaryKeySelective(ProjectType record);

    Page findByPage(Page pageRequest, Map map);

	int batchDelete(long[] id);
	
	ProjectType selectProjectTypeDetail(ProjectType projectType);
}