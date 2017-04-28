package com.yourong.common.web;

import javax.servlet.http.HttpServletRequest;

public class CSRFTool {
	public static String getToken(HttpServletRequest request) {
		return CSRFTokenManager.getTokenForSession(request.getSession());
		}
}
