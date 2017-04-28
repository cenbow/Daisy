package com.yourong.core.ic.model;

import java.math.BigDecimal;
import java.util.List;


public class ProjectPackageModel extends ProjectPackage{

	private String appPath;
    /**
     * 项目IDJson
     */
	private String projectIdJson;
	/**
	 * 图片显示的全部路径
	 */
	private String pathImg;
    /**
     * 资产包项目列表
     */
	private List<ProjectPackageLink> projectList;
	/**
	 * 收益范围
	 */
	private String annualizedRate;
	/**
	 * 借款周期
	 */
	private String borrowPeriod;
	
	public String getAnnualizedRate() {
		return annualizedRate;
	}

	public void setAnnualizedRate(String annualizedRate) {
		this.annualizedRate = annualizedRate;
	}

	public String getBorrowPeriod() {
		return borrowPeriod;
	}

	public void setBorrowPeriod(String borrowPeriod) {
		this.borrowPeriod = borrowPeriod;
	}

	public String getPathImg() {
		return pathImg;
	}

	public String getAppPath() {
		return appPath;
	}

	public void setAppPath(String appPath) {
		this.appPath = appPath;
	}

	public void setPathImg(String pathImg) {
		this.pathImg = pathImg;
	}

	public List<ProjectPackageLink> getProjectList() {
		return projectList;
	}

	public void setProjectList(List<ProjectPackageLink> projectList) {
		this.projectList = projectList;
	}

	public String getProjectIdJson() {
		return projectIdJson;
	}

	public void setProjectIdJson(String projectIdJson) {
		this.projectIdJson = projectIdJson;
	}
	
}