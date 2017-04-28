package com.yourong.api.dto;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 因Android有特殊需求，为此为项目自定义一个Page对象
 */
public class ProjectPageDto<T> {
	private static final long serialVersionUID = 867755909294344406L;

	private List<T> data;
	/**
	 * 总条数
	 */
	private long iTotalRecords;
	/**
	 * 过滤条件后查询的总数
	 */
	private long iTotalDisplayRecords;
	/**
	     *
	     */
	private int iDisplayLength;

	// 开始显示行
	private int iDisplayStart;

	/**
	 * 前台分页条件*
	 */

	// 总页数
	private int totalPageCount;
	// 页码
	private int pageNo;
	
	private String statusCode;

	public int getiDisplayLength() {
		return iDisplayLength;
	}

	/**
	 * 每页总数
	 */
	public void setiDisplayLength(int iDisplayLength) {
		this.iDisplayLength = iDisplayLength;
	}

	public int getiDisplayStart() {
		return iDisplayStart;
	}

	/**
	 * 开始行
	 */
	public void setiDisplayStart(int iDisplayStart) {
		this.iDisplayStart = iDisplayStart;
	}

	public long getiTotalDisplayRecords() {
		return iTotalDisplayRecords;
	}

	public void setiTotalDisplayRecords(long iTotalDisplayRecords) {
		this.iTotalDisplayRecords = iTotalDisplayRecords;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

	public long getiTotalRecords() {
		return iTotalRecords;
	}

	/**
	 * 总数
	 */
	public void setiTotalRecords(long iTotalRecords) {
		this.iTotalRecords = iTotalRecords;
		recalc();
	}

	public long getTotalElements() {
		return iTotalRecords;
	}

	public Iterator<T> iterator() {
		return data.iterator();
	}

	public List<T> getData() {
		return Collections.unmodifiableList(data);
	}

	public boolean hasContent() {
		return !data.isEmpty();
	}

	private void recalc() {
		totalPageCount = (int) Math.ceil((double) iTotalRecords
				/ (double) iDisplayLength);
	}

	public int getTotalPageCount() {
		return totalPageCount;
	}

	public void setTotalPageCount(int totalPageCount) {
		this.totalPageCount = totalPageCount;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

}
