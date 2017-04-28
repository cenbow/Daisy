package com.yourong.web.authority;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yourong.common.constant.Constant;
import com.yourong.common.util.SpringContextHolder;
/**
 * 检查是否登录
 * @author Administrator
 *
 */
public class CheckLoginedFilter implements Filter{
	
	protected Logger logger = LoggerFactory.getLogger(CheckLoginedFilter.class);

	

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest req = ((HttpServletRequest) request);
		if (!checkRequestURIIntNotFilterList(req)) {
			HttpServletResponse resp = ((HttpServletResponse) response);
			HttpSession session = req.getSession();
			Object obj = session.getAttribute(Constant.CURRENT_USER);
			if (obj == null || "".equals(obj.toString())) {
				resp.sendRedirect(req.getContextPath() + "/login");
				return;
			}
		}
		chain.doFilter(request, response);  
	}
	
	/**
	 * 不需要过滤的URL
	 * @param request
	 * @return
	 * author: pengyong
	 * 下午7:39:56
	 */
	private boolean checkRequestURIIntNotFilterList(HttpServletRequest request) {
		String uri = request.getServletPath()
				+ (request.getPathInfo() == null ? "" : request.getPathInfo());	
		if(logger.isDebugEnabled()){
			logger.debug("请求URL:"+uri);
		}
		//如果是根目录， 也不需要过滤
		if(uri.equalsIgnoreCase("/")){
			return true;
		}	
		List noFilterUrl = SpringContextHolder.getBean("noFilterUrl");
		Iterator iterator = noFilterUrl.iterator();
		boolean result = false;
		while (iterator.hasNext()) {
			String  next = (String)iterator.next();
			//TODO 优化
			result = uri.startsWith(next);
			if(result){
				if(logger.isDebugEnabled()){
					logger.debug("不需要过滤URL:"+uri);
				}
				return result;
			}
		}		
		return result;
	}
	
	
	@Override
	public void destroy() {		
		
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {	
		
	}

}
