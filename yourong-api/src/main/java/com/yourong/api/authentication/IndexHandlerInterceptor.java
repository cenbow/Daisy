package com.yourong.api.authentication;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.yourong.common.util.AES;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.StringUtil;

public class IndexHandlerInterceptor extends HandlerInterceptorAdapter {
	protected static Logger logger = LoggerFactory.getLogger(IndexHandlerInterceptor.class);
	private static final String MEMBER_ID = "api_memberID";
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String token = request.getParameter("token");
		if (StringUtil.isNotBlank(token)) {
			List<String> tokenDecrypt = null;
			try {
				tokenDecrypt = AES.getInstance().tokenDecrypt(token);
			} catch (Exception e) {
				logger.error("解密token失败:" + token);
			}
			if (Collections3.isNotEmpty(tokenDecrypt)) {
				String memberID = tokenDecrypt.get(0);
				if (StringUtil.isNumeric(memberID)) {
					Long id = Long.parseLong(memberID);
					if(id > 0){
						request.setAttribute(MEMBER_ID, Long.parseLong(memberID));
						return true;
					}
				}
			}
			
		}
		return super.preHandle(request, response, handler);
	}

}
