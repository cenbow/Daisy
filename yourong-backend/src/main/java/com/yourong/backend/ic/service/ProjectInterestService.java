package com.yourong.backend.ic.service;

import java.util.List;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.ic.model.ProjectInterest;

public interface ProjectInterestService {
	/**
	 * 批量插入阶梯收益表
	 * @param projectInterestList
	 * @return
	 * @throws ManagerException
	 */
	int batchInsertProjectInterest(List<ProjectInterest> projectInterestList);
	
	/**
	 * 根据项目编号删除阶梯收益
	 * @param projectId
	 * @return
	 */
	int deleteProjectInterestByProjectId(int projectId);
}
