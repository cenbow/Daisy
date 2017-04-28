package com.yourong.backend.ic.service;

import java.util.Map;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.Project;

public interface ProjectRecommendService {

	/**
	 * 添加推荐
	 * @param id
	 * @return
	 */
	public ResultDO addRecommend(Long id);
	
	/**
	 * 更新推荐权重
	 * @param id
	 * @param weight
	 * @return
	 */
	public ResultDO updateRecommend(Long id, int weight, int recommentType);
	
	/**
	 * 取消推荐
	 * @param id
	 * @return
	 */
	public ResultDO cancelRecommend(Long id, int recommentType);
	
	/**
	 * 
	 * @param pageRequest
	 * @param map
	 * @return
	 */
	Page<Project> findProjectRecommendByPage(Page<Project> pageRequest, Map<String, Object> map);
	
	
	public ResultDO addAppRecommend(Long projectId);
	
}
