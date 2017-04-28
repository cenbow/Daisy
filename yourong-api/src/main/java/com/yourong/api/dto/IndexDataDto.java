package com.yourong.api.dto;

import java.util.List;

public class IndexDataDto {
	
	
	private List<ProjectListDto> projectList;
	
	private List<BannerDto> bannerList;
	
	private List<IconDto> iconList ;
	
	
	public List<ProjectListDto> getProjectList() {
		return projectList;
	}
	public void setProjectList(List<ProjectListDto> projectList) {
		this.projectList = projectList;
	}
	
	public List<BannerDto> getBannerList() {
		return bannerList;
	}
	public void setBannerList(List<BannerDto> bannerList) {
		this.bannerList = bannerList;
	}
	
	
	public List<IconDto> getIconList() {
		return iconList;
	}
	public void setIconList(List<IconDto> iconList) {
		this.iconList = iconList;
	}
	
	
	
}
