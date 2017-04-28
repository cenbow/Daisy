package com.yourong.web.service;

import java.util.List;

import com.yourong.core.ic.model.biz.ProjectNoticeForFront;

public interface ProjectNoticeService {
	/**
	 * 
	 * @return
	 */
	public List<ProjectNoticeForFront> getProjectNoticeForFront(int num);
}
