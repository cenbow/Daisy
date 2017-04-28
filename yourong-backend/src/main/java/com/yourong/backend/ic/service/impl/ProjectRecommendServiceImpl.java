package com.yourong.backend.ic.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.backend.ic.service.ProjectRecommendService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.model.Project;

@Service
public class ProjectRecommendServiceImpl implements ProjectRecommendService {
	
	private static Logger logger = LoggerFactory.getLogger(ProjectRecommendServiceImpl.class);
	
	@Autowired
	private ProjectManager projectManager;

	@Override
	public ResultDO addRecommend(Long id) {
		ResultDO result = new ResultDO();
		try {
			Project project = projectManager.selectByPrimaryKey(id);
			if(project != null && project.getRecommend() < 1 && project.getStatus() <= StatusEnum.PROJECT_STATUS_INVESTING.getStatus()){
				int maxWeight = projectManager.findMaxRecommendWeight();
				int row = projectManager.recommendProject(project.getId(), (maxWeight+1));
				if(row < 0){
					result.setSuccess(false);
				}
			}else{
				result.setSuccess(false);
			}
		} catch (ManagerException e) {
			result.setSuccess(false);
			logger.error("添加推荐失败",e);
		}
		return result;
	}

	@Override
	public ResultDO updateRecommend(Long id, int weight, int recommentType) {
		ResultDO result = new ResultDO();
		try {
			Project project = projectManager.selectByPrimaryKey(id);
			if(recommentType == 1){
				if(project != null && project.getRecommend() > 0){
					Project p = projectManager.getProjectBySortIndex(weight, recommentType);
					int row = 0;
					if(p != null && p.getRecommendWeight() > project.getRecommendWeight()){
						projectManager.resetRecommendWeight(p.getRecommendWeight()+1);
						row = projectManager.recommendProject(project.getId(), p.getRecommendWeight()+1);
					}else{
						projectManager.resetRecommendWeight(weight);
						row =projectManager.recommendProject(project.getId(), p.getRecommendWeight());
					}
					if(row < 0){
						result.setSuccess(false);
					}
				}
			}else{
				if(project != null && project.getAppRecommend() > 0){
					Project p = projectManager.getProjectBySortIndex(weight, recommentType);
					int row = 0;
					if(p != null && p.getAppRecommendWeight() > project.getAppRecommendWeight()){
						projectManager.resetAppRecommendWeight(p.getAppRecommendWeight()+1);
						row = projectManager.recommendAppProject(project.getId(), p.getAppRecommendWeight()+1);
					}else{
						projectManager.resetAppRecommendWeight(weight);
						row = projectManager.recommendAppProject(project.getId(), p.getAppRecommendWeight());
					}
					if(row < 0){
						result.setSuccess(false);
					}
				}
			}
		} catch (ManagerException e) {
			result.setSuccess(false);
			logger.error("更新推荐权重失败",e);
		}
		return result;
	}

	@Override
	public ResultDO cancelRecommend(Long id, int recommentType) {
		ResultDO result = new ResultDO();
		try {
			if(recommentType == 1){
				int row = projectManager.cancelRecommendByProjectId(id);
				if(row < 0){
					result.setSuccess(false);
				}
			}else{
				int row = projectManager.cancelAppRecommendByProjectId(id);
				if(row < 0){
					result.setSuccess(false);
				}
			}
		} catch (ManagerException e) {
			result.setSuccess(false);
			logger.error("取消推荐失败",e);
		}
		return result;
	}

	@Override
	public Page<Project> findProjectRecommendByPage(Page<Project> pageRequest,
			Map<String, Object> map) {
		try {
			return projectManager.findProjectRecommendByPage(pageRequest, map);
		} catch (ManagerException e) {
			logger.error("分页查询推荐项目失败",e);
		}
		return null;
	}

	@Override
	public ResultDO addAppRecommend(Long projectId) {
		ResultDO result = new ResultDO();
		try {
			Project project = projectManager.selectByPrimaryKey(projectId);
			if(project != null && project.getAppRecommend() < 1 && project.getStatus() <= StatusEnum.PROJECT_STATUS_INVESTING.getStatus()){
				int maxWeight = projectManager.findMaxAppRecommendWeight();
				int row = projectManager.recommendAppProject(project.getId(), (maxWeight+1));
				if(row < 0){
					result.setSuccess(false);
				}
			}else{
				result.setSuccess(false);
			}
		} catch (ManagerException e) {
			result.setSuccess(false);
			logger.error("添加推荐失败",e);
		}
		return result;
	}
	
	

}
