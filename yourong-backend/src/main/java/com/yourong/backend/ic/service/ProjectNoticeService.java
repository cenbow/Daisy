package com.yourong.backend.ic.service;

import java.util.List;
import java.util.Map;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.Project;
import com.yourong.core.ic.model.ProjectNotice;

public interface ProjectNoticeService {
	/**
	 * 分页查询项目预告信息
	 * @param pageRequest
	 * @param map
	 * @return
	 * @throws ManagerException
	 */
	public Page<ProjectNotice> findByPage(Page<ProjectNotice> pageRequest, Map<String, Object> map);

	/**
	 * 添加项目预告
	 * @param notice
	 * @return
	 * @throws ManagerException
	 */
	public ResultDO insertProjectNotice(ProjectNotice notice);
	
	/**
	 * 更新项目预告
	 * @param notice
	 * @return
	 * @throws ManagerException
	 */
	public ResultDO updateProjectNotice(ProjectNotice notice);
	
	/**
	 * 逻辑删除项目预告
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	public ResultDO deleteProjectNoticeById(Long id);
	
	
	/**
	 * 根据编号获得项目预告信息
	 * @param id
	 * @return
	 */
	public ProjectNotice findProjectNoticeById(Long id);
	
	/**
	 * 根据编号暂停项目预告
	 * @param id
	 * @return
	 */
	public ResultDO stopProjectNoticeById(Long id);
	
	/**
	 * 根据编号恢复项目预告
	 * @param id
	 * @return
	 */
	public ResultDO startProjectNoticeById(Long id);
	
	/**
	 * 推荐项目预告至首页
	 * @param id
	 * @return
	 */
	public ResultDO recommendProjectNotice(Long id) throws ManagerException;
	
	/**
	 * 取消首页项目预告推荐
	 * @param id
	 * @return
	 */
	public ResultDO cancelRecommendProjectNotice(Long id);
	
	
	/**
	 * 更新预告的排序
	 * @param id
	 * @param sort
	 * @return
	 */
	public ResultDO updateProjectNoticeSort(Long id, int sort);
	
	/**
	 * 获得可设置成预告的项目
	 * @param projectName
	 * @return
	 */
	public List<Project> queryProjectFromNotice(String projectName);
}
