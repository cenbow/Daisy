package com.yourong.api.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.api.dto.IndexProjectDto;
import com.yourong.api.dto.ProjectNoticeDto;
import com.yourong.api.service.ProjectNoticeService;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.NumberFormatUtil;
import com.yourong.core.ic.manager.ProjectExtraManager;
import com.yourong.core.ic.manager.ProjectNoticeManager;
import com.yourong.core.ic.model.ProjectExtra;
import com.yourong.core.ic.model.biz.ProjectNoticeForFront;

@Service
public class ProjectNoticeServiceImpl implements ProjectNoticeService {
	@Autowired
	private ProjectNoticeManager projectNoticeManager;
	
	@Autowired
	private ProjectExtraManager projectExtraManager;

	/**
	 * 日志对象
	 */
	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public List<ProjectNoticeDto> getProjectNoticeList() {
		List<ProjectNoticeDto> projectNoticeDtoList = null;
		try {
			List<ProjectNoticeForFront> noticeList = projectNoticeManager.getProjectNoticeForFront(10);
			projectNoticeDtoList = BeanCopyUtil.mapList(noticeList, ProjectNoticeDto.class);
			for(ProjectNoticeDto p :projectNoticeDtoList){
				this.getExtraProject(p);
			}
		} catch (ManagerException e) {
			logger.error("查询预告项目异常", e);
		}
		return projectNoticeDtoList;
	}
	
	private void getExtraProject(ProjectNoticeDto p) throws ManagerException{
		
		p.setQuickRewardFlag(0);
		int extraType = 0;//特殊业务类型: 1-活动 2-项目加息
		ProjectExtra pro = projectExtraManager.getProjectQucikReward(p.getProjectId());
		if(pro.getActivitySign()!=null&&pro.getActivitySign()==TypeEnum.PROJECT_ACTIVITY_CATALYZER.getType()){
			p.setQuickRewardFlag(1);
		}
		ProjectExtra projectExtraAddRate = projectExtraManager.getAddRateProject(p.getProjectId());
		if(projectExtraAddRate!=null){
			extraType=projectExtraAddRate.getExtraType();
	        p.setAddRate(projectExtraAddRate.getExtraAmount().toString());
		}
		p.setExtraType(extraType);
	}
	
	@Override
	public List<ProjectNoticeDto> p2pGetProjectNoticeList() {
		List<ProjectNoticeDto> projectNoticeDtoList = null;
		try {
			List<ProjectNoticeForFront> noticeList = projectNoticeManager.p2pGetProjectNoticeForFront(10);
			projectNoticeDtoList = BeanCopyUtil.mapList(noticeList, ProjectNoticeDto.class);
		} catch (ManagerException e) {
			logger.error("查询预告项目异常", e);
		}
		return projectNoticeDtoList;
	}

}
