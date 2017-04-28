package com.yourong.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.yourong.common.exception.ManagerException;
import com.yourong.core.ic.manager.ProjectNoticeManager;
import com.yourong.core.ic.model.biz.ProjectNoticeForFront;
import com.yourong.web.service.ProjectNoticeService;

@Service
public class ProjectNoticeServiceImpl implements ProjectNoticeService {
	@Autowired
	private ProjectNoticeManager projectNoticeManager;

	@Override
	public List<ProjectNoticeForFront> getProjectNoticeForFront(int num) {
		List<ProjectNoticeForFront> noticeList = Lists.newArrayList();
		try {
			noticeList = projectNoticeManager.getProjectNoticeForFront(num);
		} catch (ManagerException e) {
			
		}
		return noticeList;
	}

}
