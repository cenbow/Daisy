package com.yourong.api.dto;

import java.util.List;

import com.yourong.core.ic.model.ProjectPackage;


/**
 * app项目列表包含快投
 */
public class ProjectListAndQuickProjectDto {

	private ProjectPageDto<ProjectListDto> projectPageDto;
	
	private ProjectListDto quickProject;
	
	private ProjectPackageListDto<ProjectPackage> packDto;


	private List<String> titleConfig;
	
	private boolean isExistCompleted =false;

	
	public boolean isExistCompleted() {
		return isExistCompleted;
	}

	public void setExistCompleted(boolean isExistCompleted) {
		this.isExistCompleted = isExistCompleted;
	}

	public List<String> getTitleConfig() {
		return titleConfig;
	}

	public void setTitleConfig(List<String> titleConfig) {
		this.titleConfig = titleConfig;
	}


	public ProjectPackageListDto<ProjectPackage> getPackDto() {
		return packDto;
	}

	public void setPackDto(ProjectPackageListDto<ProjectPackage> packDto) {
		this.packDto = packDto;
	}

	public ProjectPageDto<ProjectListDto> getProjectPageDto() {
		return projectPageDto;
	}

	public void setProjectPageDto(ProjectPageDto<ProjectListDto> projectPageDto) {
		this.projectPageDto = projectPageDto;
	}

	public ProjectListDto getQuickProject() {
		return quickProject;
	}

	public void setQuickProject(ProjectListDto quickProject) {
		this.quickProject = quickProject;
	}

}
