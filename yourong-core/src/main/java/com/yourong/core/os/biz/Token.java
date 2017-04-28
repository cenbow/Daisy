package com.yourong.core.os.biz;

import com.yourong.common.domain.AbstractBaseObject;

public class Token extends AbstractBaseObject {

	public Token(String accessToken) {
		this.token = accessToken;
	}

	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
