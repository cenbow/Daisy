package com.yourong.core.os.biz;

import com.yourong.common.domain.AbstractBaseObject;

public class ProjectsPageQuery extends AbstractBaseObject {

	private String token;

	private String date;

	private Integer page;

	private Integer pageSize;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

}
