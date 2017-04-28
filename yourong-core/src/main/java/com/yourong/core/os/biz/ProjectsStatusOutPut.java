package com.yourong.core.os.biz;

import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;

public class ProjectsStatusOutPut extends AbstractBaseObject {

	private Integer totalLoan;

	private List<ProjectStatus> borrowStatusList;

	public Integer getTotalLoan() {
		return totalLoan;
	}

	public void setTotalLoan(Integer totalLoan) {
		this.totalLoan = totalLoan;
	}

	public List<ProjectStatus> getBorrowStatusList() {
		return borrowStatusList;
	}

	public void setBorrowStatusList(List<ProjectStatus> borrowStatusList) {
		this.borrowStatusList = borrowStatusList;
	}

}
