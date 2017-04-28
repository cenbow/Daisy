package com.yourong.api.dto;

import java.util.List;

public class ProjectPackageListDto<T> {
	private List<T> data;

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}
	
	
}
