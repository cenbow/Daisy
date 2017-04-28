package com.yourong.core.ic.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.ProjectExtra;

public interface ProjectExtraMapper {

	int deleteByPrimaryKey(Long id);

	int insert(ProjectExtra record);

	int insertSelective(ProjectExtra record);

	ProjectExtra selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(ProjectExtra record);

	int updateByPrimaryKey(ProjectExtra record);

	Page findByPage(Page pageRequest, @Param("map") Map map);

	int batchDelete(@Param("ids") int[] ids);

	List selectForPagin(@Param("map") Map map);

	int selectForPaginTotalCount(@Param("map") Map map);
	
	ProjectExtra getProjectActivitySign(@Param("projectId") Long projectId);
	
	ProjectExtra getNewProjectExtra();
	
	ProjectExtra getProjectQucikReward(@Param("projectId") Long projectId);
	
	ProjectExtra getProjectExtraInforByMap(@Param("map") Map map);
	
	List<ProjectExtra> getProjectExtraListByMap(@Param("map") Map map);
	
	int getExtraProjectNumByStatus(@Param("map") Map map);
	
	int getProjectExtraByMap(@Param("map") Map map);
	
	int getProjectExtraNoticeByMap(@Param("map") Map map);
	
	List<Long> getQuickProjectLately();
}