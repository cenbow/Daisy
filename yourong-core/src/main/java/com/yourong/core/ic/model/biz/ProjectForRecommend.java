package com.yourong.core.ic.model.biz;

import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;

public class ProjectForRecommend extends AbstractBaseObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9193339820515134532L;

	
	private ProjectForFront newCustomerProject;
	
	
	private List<ProjectForFront> shortProjectForList;
	
	
	private List<ProjectForFront> middleProjectForList;
	
	
	private List<ProjectForFront> longProjectForList;
	
	private Integer shortNumber;
	
	private Integer middleNumber;
	
	private Integer longNumber;
	
	
	
	

	public Integer getShortNumber() {
		return shortNumber;
	}


	public void setShortNumber(Integer shortNumber) {
		this.shortNumber = shortNumber;
	}


	public Integer getMiddleNumber() {
		return middleNumber;
	}


	public void setMiddleNumber(Integer middleNumber) {
		this.middleNumber = middleNumber;
	}


	public Integer getLongNumber() {
		return longNumber;
	}


	public void setLongNumber(Integer longNumber) {
		this.longNumber = longNumber;
	}


	public List<ProjectForFront> getShortProjectForList() {
		return shortProjectForList;
	}


	public void setShortProjectForList(List<ProjectForFront> shortProjectForList) {
		this.shortProjectForList = shortProjectForList;
	}


	public List<ProjectForFront> getMiddleProjectForList() {
		return middleProjectForList;
	}


	public void setMiddleProjectForList(List<ProjectForFront> middleProjectForList) {
		this.middleProjectForList = middleProjectForList;
	}


	public List<ProjectForFront> getLongProjectForList() {
		return longProjectForList;
	}


	public void setLongProjectForList(List<ProjectForFront> longProjectForList) {
		this.longProjectForList = longProjectForList;
	}


	public ProjectForFront getNewCustomerProject() {
		return newCustomerProject;
	}


	public void setNewCustomerProject(ProjectForFront newCustomerProject) {
		this.newCustomerProject = newCustomerProject;
	}
	
	
	
	
}
