package com.yourong.api.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.yourong.common.cache.RedisMemberClient;
import com.yourong.common.util.StringUtil;

public class InviteCodHandleInterceptor extends HandlerInterceptorAdapter {

	protected static Logger logger = LoggerFactory.getLogger(IndexHandlerInterceptor.class);
	protected static final String  INVITECODE = "inviteCode_shortURL";
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String inviteCod = request.getParameter("inviteCod");
		if (StringUtil.isNotBlank(inviteCod)&& RedisMemberClient.isExistMemberInviteCode(inviteCod)) {
			HttpSession session = getHttpSession(request);
			session.setAttribute(INVITECODE, inviteCod);
			return true;
		}
		return super.preHandle(request, response, handler);
	}
	
	protected HttpSession getHttpSession(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			session = request.getSession();
		}
		return session;
	}
}
