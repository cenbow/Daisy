package com.yourong.common.http.common;

import java.io.IOException;

public class HttpTools {
	public final static String getString(HttpResponse response)
			throws IOException {
		String rtn = null;
		rtn = StreamReader.getStringFromStream(response.getInputStream(),
				response.getEncoding());
		return rtn;
	}
}