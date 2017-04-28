package com.yourong.api.dto;

import com.yourong.core.ic.model.ProjectPackage;
import com.yourong.core.ic.model.ProjectPackageLinkModel;

public class ProjectPackageDto {

	private ProjectPackageListDto<ProjectPackageLinkModel> data;
	 
	private ProjectPackage projectPackage;
	 
	private ProjectPackageListDto<ProjectPackage> packDto;

	private String minRewardLimit;
	
	public String getMinRewardLimit() {
		return minRewardLimit;
	}

	public void setMinRewardLimit(String minRewardLimit) {
		this.minRewardLimit = minRewardLimit;
	}

	public ProjectPackageListDto<ProjectPackage> getPackDto() {
		return packDto;
	}

	public void setPackDto(ProjectPackageListDto<ProjectPackage> packDto) {
		this.packDto = packDto;
	}

	public ProjectPackageListDto<ProjectPackageLinkModel> getData() {
		return data;
	}

	public void setData(ProjectPackageListDto<ProjectPackageLinkModel> data) {
		this.data = data;
	}

	public ProjectPackage getProjectPackage() {
		return projectPackage;
	}

	public void setProjectPackage(ProjectPackage projectPackage) {
		this.projectPackage = projectPackage;
	}



 
	 
}
