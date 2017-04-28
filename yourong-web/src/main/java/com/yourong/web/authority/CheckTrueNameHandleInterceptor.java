package com.yourong.web.authority;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.yourong.core.mc.model.Coupon;
import com.yourong.web.utils.ServletUtil;

/**
 * 实名认证
 * 
 * @author Administrator
 *
 */
public class CheckTrueNameHandleInterceptor extends HandlerInterceptorAdapter {
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		if (ServletUtil.isVerifyTrueName()) {
			return super.preHandle(request, response, handler);
		} else {
			boolean mobile = ServletUtil.isMobile(request);
			if (mobile){
				response.sendRedirect(request.getContextPath() + "/member/landing/mSinapa");
			}else {
				response.sendRedirect(request.getContextPath() + "/member/sinapay");
			}
			return false;
		}
	}

}
