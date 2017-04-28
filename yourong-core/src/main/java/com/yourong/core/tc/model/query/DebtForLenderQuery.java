/**
 * 
 */
package com.yourong.core.tc.model.query;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.yourong.common.domain.BaseQueryParam;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年3月23日下午4:05:50
 */
public class DebtForLenderQuery extends BaseQueryParam{
	
	/**
	 * 每页显示的条数
	 */
	private int pageSize = 12;
	
	/**
	 * 当前页码
	 */
	private int currentPage = 1;
	/**
	 * 查询起始行
	 */
	private int startRow = 0;
	
	
	/**
	 * 每页显示的条数
	 */
	private String borrowerName ;
	
	
	/**
	 * 查询起始时间
	 */
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date startDate;

	/**
	 * 查询结束时间
	 */
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date endDate;

	/**
	 * 查询三天标志位
	 */
	private int threeDayFlag;
	
	/**
	 * @return the pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the borrowerName
	 */
	public String getBorrowerName() {
		if("".equals(borrowerName)){
			return null;
		}
		return borrowerName;
	}

	/**
	 * @param borrowerName the borrowerName to set
	 */
	public void setBorrowerName(String borrowerName) {
		this.borrowerName = borrowerName;
	}

	/**
	 * @return the threeDayFlag
	 */
	public int getThreeDayFlag() {
		return threeDayFlag;
	}

	/**
	 * @param threeDayFlag the threeDayFlag to set
	 */
	public void setThreeDayFlag(int threeDayFlag) {
		this.threeDayFlag = threeDayFlag;
	}
	
	public int getStartRow() {
		startRow = (currentPage - 1) * pageSize;
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	/**
	 * @return the currentPage
	 */
	public int getCurrentPage() {
		return currentPage;
	}

	/**
	 * @param currentPage the currentPage to set
	 */
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	
	
}
