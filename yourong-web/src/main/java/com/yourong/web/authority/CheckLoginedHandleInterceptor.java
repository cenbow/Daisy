package com.yourong.web.authority;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.yourong.common.constant.Constant;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.common.web.SavedRequest;
import com.yourong.web.utils.ServletUtil;

/**
 * 检查是否登陆
 */
public class CheckLoginedHandleInterceptor  extends HandlerInterceptorAdapter{
	//是否要拦截
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {		
		 HttpSession session=request.getSession();  
         Object obj=session.getAttribute(Constant.CURRENT_USER);
         if(obj==null||"".equals(obj.toString())){
        	 String requestType = request.getHeader("X-Requested-With");
             boolean mobile = ServletUtil.isMobile(request);
             if (mobile){
            	 String m = PropertiesUtil.getMstationRootUrl();
                 response.sendRedirect(m+"/mstation/login");
             }else{
            	 if(requestType!=null && "XMLHttpRequest".equals(requestType)){
            		 response.sendError(901, "session time out");
            	 }else{
            		 response.sendRedirect(request.getContextPath()+"/security/login");
            	 }
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
         }		
		return true;
	}

    /**
     * 是否要记录上次URL
     * @param request
     * @return
     */
    private boolean isRemberLastUrl(HttpServletRequest request) {
        String path = request.getRequestURI().trim();
        boolean  isRemberLastUrl = true;
        if ( path.indexOf("/member/logout") >= 0){
            isRemberLastUrl = false;
        }
        return isRemberLastUrl;
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


}
