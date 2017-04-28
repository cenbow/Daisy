package com.yourong.core.os.biz;

import com.yourong.common.domain.AbstractBaseObject;

public class ProjectStatus extends AbstractBaseObject {

	private String projectId;
	private Integer status;

	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}
