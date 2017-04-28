package com.yourong.core.os.biz;

import com.yourong.common.domain.AbstractBaseObject;

public class ProjectsStatusQuery extends AbstractBaseObject {

	private String token;

	private String idStr;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getIdStr() {
		return idStr;
	}

	public void setIdStr(String idStr) {
		this.idStr = idStr;
	}

}
