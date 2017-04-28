package com.yourong.backend.ic.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.yourong.backend.ic.service.ProjectNoticeService;
import com.yourong.common.constant.Constant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.DateUtils;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.manager.ProjectNoticeManager;
import com.yourong.core.ic.model.Project;
import com.yourong.core.ic.model.ProjectNotice;

@Service
public class ProjectNoticeServiceImpl implements ProjectNoticeService {
	
	private static Logger logger = LoggerFactory.getLogger(ProjectNoticeServiceImpl.class);
	
	@Autowired
	private ProjectNoticeManager projectNoticeManager;
	
	@Autowired
	private ProjectManager projectManager;
	
	@Override
	public Page<ProjectNotice> findByPage(Page<ProjectNotice> pageRequest,
			Map<String, Object> map) {
		try {
			return projectNoticeManager.findByPage(pageRequest, map);
		} catch (ManagerException e) {
			logger.error("分页查询项目预告异常", e);
		}
		return null;
	}

	@Override
	public ResultDO updateProjectNotice(ProjectNotice notice) {
		ResultDO result = new ResultDO();
		try {
			Project project = projectManager.selectByPrimaryKey(notice.getProjectId());
			if(project != null && project.getStatus() < StatusEnum.PROJECT_STATUS_INVESTING.getStatus()){
				if(notice.getEndTime().getTime() != project.getOnlineTime().getTime()){
					result.setSuccess(false);
					return result;
				}
			}else{
				result.setSuccess(false);
			}
			if(notice.getStartTime()==null){
				result.setResultCode(ResultCode.DEBT_SAVE_PROJECT_NOTICE_NOT_NULL);
				return result;
			}
			notice.setUpdateTime(DateUtils.getCurrentDateTime());
			int row = projectNoticeManager.updateProjectNotice(notice);
		} catch (ManagerException e) {
			logger.error("更新项目预告异常", e);
			result.setSuccess(false);
		}
		return result;
	}

	@Override
	public ResultDO insertProjectNotice(ProjectNotice notice){
		ResultDO result = new ResultDO();
		try {
			Project project = projectManager.selectByPrimaryKey(notice.getProjectId());
			if(project != null && project.getStatus() < StatusEnum.PROJECT_STATUS_INVESTING.getStatus()){
				if(notice.getEndTime().getTime() != project.getOnlineTime().getTime()){
					result.setSuccess(false);
					return result;
				}
				if(notice.getStartTime()==null){
					result.setResultCode(ResultCode.DEBT_SAVE_PROJECT_NOTICE_NOT_NULL);
					return result;
				}
				notice.setCreateTime(DateUtils.getCurrentDateTime());
				notice.setUpdateTime(DateUtils.getCurrentDateTime());
				notice.setStatus(Constant.ENABLE);
				notice.setSort(Constant.ENABLE);
				notice.setDelFlag(Constant.ENABLE);
				//重新排序
				projectNoticeManager.resetProjectNoticeSort(Constant.ENABLE);
				int row = projectNoticeManager.insertProjectNotice(notice);
			}else{
				result.setSuccess(false);
			}
		} catch (ManagerException e) {
			logger.error("添加项目预告异常", e);
			result.setSuccess(false);
		}
		return result;
	}

	@Override
	public ResultDO deleteProjectNoticeById(Long id) {
		ResultDO result = new ResultDO();
		try {
			int row = projectNoticeManager.deleteProjectNoticeById(id);
		} catch (ManagerException e) {
			logger.error("删除项目预告异常", e);
			result.setSuccess(false);
		}
		return result;
	}

	@Override
	public ProjectNotice findProjectNoticeById(Long id) {
		try {
			ProjectNotice notice =  projectNoticeManager.findProjectNoticeById(id);
			if(notice != null){
				Project project = projectManager.selectByPrimaryKey(notice.getProjectId());
				notice.setProjectName(project.getName());
			}
			return notice;
		} catch (ManagerException e) {
			logger.error("获取项目预告异常", e);
		}
		return null;
	}

	@Override
	public ResultDO stopProjectNoticeById(Long id) {
		ResultDO result = new ResultDO();
		try {
			int row = this.updateProjectNoticeStatus(StatusEnum.PROJECT_NOTICE_STATUS_STOP.getStatus(), StatusEnum.PROJECT_NOTICE_STATUS_RUNING.getStatus(), id);
			if(row < 1){
				result.setSuccess(false);
			}
		} catch (ManagerException e) {
			result.setSuccess(false);
			logger.error("项目预告暂停异常", e);
		}
		return result;
	}

	@Override
	public ResultDO startProjectNoticeById(Long id) {
		ResultDO result = new ResultDO();
		try {
			int row = this.updateProjectNoticeStatus(StatusEnum.PROJECT_NOTICE_STATUS_RUNING.getStatus(), StatusEnum.PROJECT_NOTICE_STATUS_STOP.getStatus(), id);
			if(row < 1){
				result.setSuccess(false);
			}
		} catch (ManagerException e) {
			result.setSuccess(false);
			logger.error("项目预告开启异常", e);
		}
		return result;
	}
	
	/**
	 * 更新预告状态
	 * @param newStatus 新状态
	 * @param currentStatus 当前状态
	 * @param id 编号
	 * @return
	 * @throws ManagerException 
	 */
	private int updateProjectNoticeStatus(int newStatus, int currentStatus, Long id) throws ManagerException{
		int row = 0;
		ProjectNotice notice = findProjectNoticeById(id);
		if(notice != null){
			row = projectNoticeManager.updateProjectNoticeStatus(newStatus, currentStatus, id);
			logger.info("项目预靠："+id+",将状态【"+currentStatus+"】改为【"+newStatus+"】");
		}
		return row;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO recommendProjectNotice(Long id) throws ManagerException{
		ResultDO result = new ResultDO();
		try {
			projectNoticeManager.cancelRecommendProjectNotice();
			int row = projectNoticeManager.recommendProjectNotice(id);
			if(row < 1){
				result.setSuccess(false);
			}
		} catch (ManagerException e) {
			result.setSuccess(false);
			logger.error("项目预告推荐异常", e);
			throw new ManagerException(e);
		}
		return result;
	}

	@Override
	public ResultDO cancelRecommendProjectNotice(Long id) {
		ResultDO result = new ResultDO();
		try {
			int row = projectNoticeManager.cancelRecommendProjectNoticeById(id);
			if(row < 1){
				result.setSuccess(false);
			}
		} catch (ManagerException e) {
			logger.error("项目预告取消推荐异常", e);
			result.setSuccess(false);
		}
		return result;
	}

	@Override
	public ResultDO updateProjectNoticeSort(Long id, int sort) {
		ResultDO result = new ResultDO();
		try {
			ProjectNotice notice = findProjectNoticeById(id);
			if(notice != null){
				ProjectNotice pn = projectNoticeManager.getProjectNoticeBySortIndex(sort);
				int row = 0;
				if(pn.getSort() > notice.getSort()){
					projectNoticeManager.resetProjectNoticeSort(notice.getSort());
					projectNoticeManager.updateProjectNoticeSort(pn.getId(), notice.getSort());
				}else{
					projectNoticeManager.resetProjectNoticeSort(sort);
					projectNoticeManager.updateProjectNoticeSort(id, sort);
				}
				if(row < 1){
					result.setSuccess(false);
				}
			}
		} catch (ManagerException e) {
			result.setSuccess(false);
			logger.error("项目预告排序异常", e);
		}
		return result;
	}
	
	@Override
	public List<Project> queryProjectFromNotice(String projectName) {
		try {
			return projectManager.queryProjectFromNotice(projectName);
		} catch (Exception e) {
			logger.error("获得可设置成预告的项目失败，projectName=" + projectName, e);
		}
		return null;
	}
}
