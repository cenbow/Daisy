package com.yourong.api.service;

import java.util.List;

import com.yourong.api.dto.ProjectNoticeDto;
import com.yourong.core.ic.model.biz.ProjectNoticeForFront;

public interface ProjectNoticeService {
	
	/**
	 * 获得预告项目列表
	 * @return
	 */
	public List<ProjectNoticeDto> getProjectNoticeList();

	/**
	 * 获得预告项目列表,提供给旧版本APP,过滤P2P项目
	 * @return
	 */
	public List<ProjectNoticeDto> p2pGetProjectNoticeList();
}
