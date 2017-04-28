package com.yourong.core.ic.manager;

import java.util.List;
import java.util.Map;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.ProjectNotice;
import com.yourong.core.ic.model.biz.ProjectNoticeForFront;

public interface ProjectNoticeManager {
	
	/**
	 * 分页查询项目预告信息
	 * @param pageRequest
	 * @param map
	 * @return
	 * @throws ManagerException
	 */
	public Page<ProjectNotice> findByPage(Page<ProjectNotice> pageRequest, Map<String, Object> map) throws ManagerException;

	/**
	 * 添加项目预告
	 * @param notice
	 * @return
	 * @throws ManagerException
	 */
	public int insertProjectNotice(ProjectNotice notice) throws ManagerException;
	
	/**
	 * 更新项目预告
	 * @param notice
	 * @return
	 * @throws ManagerException
	 */
	public int updateProjectNotice(ProjectNotice notice) throws ManagerException;
	
	/**
	 * 逻辑删除项目预告
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	public int deleteProjectNoticeById(Long id) throws ManagerException;
	
	/**
	 * 自动开始项目预告，提供给定时器处理的方法
	 * @return
	 * @throws ManagerException
	 */
	public int autoStartProjectNoticeTask() throws ManagerException;
	
	/**
	 * 自动结束项目预告，提供给定时器处理的方法
	 * @return
	 * @throws ManagerException
	 */
	public int autoEndProjectNoticeTask() throws ManagerException;
	
	/**
	 * 根据编号获得项目预告信息
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	public ProjectNotice findProjectNoticeById(Long id) throws ManagerException;
	
	/**
	 * 更新项目状态
	 * @param newStatus 新状态
	 * @param currentStatus 当前状态
	 * @param id 编号
	 * @throws ManagerException
	 */
	public int updateProjectNoticeStatus(int newStatus, int currentStatus, Long id) throws ManagerException;
	
	/**
	 * 推荐项目预告至首页
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	public int recommendProjectNotice(Long id) throws ManagerException;	
	
	/**
	 * 取消首页项目预告推荐
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	public int cancelRecommendProjectNoticeById(Long id) throws ManagerException;
	
	/**
	 * 取消首页项目预告推荐
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	public int cancelRecommendProjectNotice() throws ManagerException;
	
	/**
	 * 
	 * @return
	 * @throws ManagerException
	 */
	public List<ProjectNoticeForFront> getProjectNoticeForFront(int num) throws ManagerException;
	
	/**
	 * app预告项目剔除P2P项目
	 * @return
	 * @throws ManagerException
	 */
	public List<ProjectNoticeForFront> p2pGetProjectNoticeForFront(int num) throws ManagerException;
	
	/**
	 * 获得首页预告
	 * @return
	 * @throws ManagerException
	 */
	public ProjectNoticeForFront getProjectNoticeByIndexShow() throws ManagerException;
	
	/**
	 * 获得首页预告,移动端，剔除P2P数据
	 * @return
	 * @throws ManagerException
	 */
	public ProjectNoticeForFront p2pGetProjectNoticeByIndexShow() throws ManagerException;
	
	/**
	 * 更新预告的排序
	 * @param id
	 * @param sort
	 * @return
	 * @throws ManagerException
	 */
	public int updateProjectNoticeSort(Long id, int sort) throws ManagerException;
	
	/**
	 * 更新预告的排序
	 * @param sort
	 * @return
	 * @throws ManagerException
	 */
	public int resetProjectNoticeSort(int sort) throws ManagerException;
	
	
	public ProjectNotice getProjectNoticeBySortIndex(int index) throws ManagerException;
	
	/**
	 * 查询即将预告的项目
	 * @return
	 * @throws ManagerException
	 */
	public List<ProjectNotice> findUpcomingProjectNotice() throws ManagerException;
	
	/**
	 * 根据项目ID获得预告中的项目
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 */
	public ProjectNotice getProjectNoticingByProjectId(Long projectId) throws ManagerException;
	
	/**
	 * 
	 * @return
	 * @throws ManagerException
	 */
	public ProjectNotice getSortFirstProjectNotice() throws ManagerException;
	
	/**
	 * 
	 * @return
	 * @throws ManagerException
	 */
	public ProjectNotice p2pGetSortFirstProjectNotice() throws ManagerException;
}
