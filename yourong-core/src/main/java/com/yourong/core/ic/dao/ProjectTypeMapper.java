package com.yourong.core.ic.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yourong.core.ic.model.ProjectType;

public interface ProjectTypeMapper {
	int deleteByPrimaryKey(Long id);

	int insert(ProjectType record);

	int insertSelective(ProjectType record);

	ProjectType selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(ProjectType record);

	int updateByPrimaryKey(ProjectType record);

	List<ProjectType> selectForPagin(@Param("map") Map<String, Object> map);

	int selectForPaginTotalCount(@Param("map") Map<String, Object> map);

	int batchDelete(@Param("ids") long[] ids);
	
	ProjectType selectProjectTypeDetail(ProjectType projectType);
 }