package com.yourong.api.authentication.m;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.yourong.common.web.SavedRequest;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.yourong.api.dto.MemberSessionDto;
import com.yourong.common.constant.Constant;

/**
 * 判断用户是否登录
 *
 */
public class LoginedHandleInterceptor extends HandlerInterceptorAdapter {

	private static final String MEMBER_ID = "api_memberID";
    private static final String REQUEST_SOURCE = "request_source";
    
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
	  HttpSession session = request.getSession();
      Object obj = session.getAttribute(Constant.CURRENT_USER);
		String requestType = request.getHeader("X-Requested-With");
      if(obj==null || "".equals(obj.toString())){
		  if(requestType!=null && "XMLHttpRequest".equals(requestType)){
			  response.sendError(901, "session time out");
		  }else{
			  response.sendRedirect(request.getContextPath()+"/mstation/login");
		  }
		  //把请求放入会话，登录后将用于跳转
		  boolean isRemberLastUrl = isRemberLastUrl(request);
		  boolean isIgnore = isIgnore(request);
		  if (isRemberLastUrl){
			  SavedRequest saved = new SavedRequest(request);
			  session.setAttribute(Constant.SAVED_REQUEST_KEY, saved);
			  session.setAttribute(Constant.SAVED_REQUEST_KEY_IS_IGNORE, isIgnore);
		  }
    	  return false;
	   }else {
		  setMemberId(request, obj);
	      return true;
	   }
	}

	//是否是需要过滤的url
	private boolean isIgnore(HttpServletRequest request){
		boolean  isAjax = false;
		String requestType = request.getHeader("X-Requested-With");
		if(requestType!=null && "XMLHttpRequest".equals(requestType)){
			isAjax = true;
		}
		String contentType = request.getHeader("Content-Type");
		if(contentType!=null && "application/x-www-form-urlencoded".equals(contentType) ){
			isAjax = true;
		}
		return isAjax;
	}
	
	/**
	 * 设置会员ID和请求来源
	 */
	private void setMemberId(HttpServletRequest request, Object obj){
		MemberSessionDto member = (MemberSessionDto)obj;
		request.setAttribute(MEMBER_ID, member.getId());
		request.setAttribute(REQUEST_SOURCE, 3);//WAP
	}

	/**
	 * 是否要记录上次URL
	 * @param request
	 * @return
	 */
	private boolean isRemberLastUrl(HttpServletRequest request) {
		String path = request.getRequestURI().trim();
		boolean  isRemberLastUrl = true;
		if ( path.indexOf("/security/member/logout") >= 0){
			isRemberLastUrl = false;
		}
		return isRemberLastUrl;
	}

}
