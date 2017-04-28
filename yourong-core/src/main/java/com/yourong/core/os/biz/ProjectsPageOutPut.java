package com.yourong.core.os.biz;

import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;

public class ProjectsPageOutPut extends AbstractBaseObject {

	private String totalPage;

	private String currentPage;

	private Integer totalCount;

	private Double totalAmount;

	private List<ProjectForOpen> borrowList;

	private List<ProjectForOpen> onSaleBorrowList;

	public String getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(String totalPage) {
		this.totalPage = totalPage;
	}

	public String getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(String currentPage) {
		this.currentPage = currentPage;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public List<ProjectForOpen> getBorrowList() {
		return borrowList;
	}

	public void setBorrowList(List<ProjectForOpen> borrowList) {
		this.borrowList = borrowList;
	}

	public List<ProjectForOpen> getOnSaleBorrowList() {
		return onSaleBorrowList;
	}

	public void setOnSaleBorrowList(List<ProjectForOpen> onSaleBorrowList) {
		this.onSaleBorrowList = onSaleBorrowList;
	}

}
