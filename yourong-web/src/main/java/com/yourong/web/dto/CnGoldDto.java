package com.yourong.web.dto;

import java.math.BigDecimal;
import java.util.List;

public class CnGoldDto {
	private int pageSize;
	private int currentPage;
	private int totalCount;
	private BigDecimal totalAmount;
	private String shortName;
	private List<CnGoldProjectDto> borrowList;
	
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public List<CnGoldProjectDto> getBorrowList() {
		return borrowList;
	}
	public void setBorrowList(List<CnGoldProjectDto> borrowList) {
		this.borrowList = borrowList;
	}
	
	
}
