package com.yourong.core.os.biz;

import com.yourong.common.domain.AbstractBaseObject;

public class TokenOutPut extends AbstractBaseObject {
	
	private Token data;

	public TokenOutPut(Token token) {
		this.data = token;
	}

	public Token getData() {
		return data;
	}

	public void setData(Token data) {
		this.data = data;
	}

}
