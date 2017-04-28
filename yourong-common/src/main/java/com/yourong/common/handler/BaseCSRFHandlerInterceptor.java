package com.yourong.common.handler;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.resource.DefaultServletHttpRequestHandler;

import com.alibaba.fastjson.JSON;
import com.yourong.common.cache.RedisMemberClient;
import com.yourong.common.constant.Constant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.common.web.CSRFTokenManager;
import com.yourong.common.web.SavedRequest;
import com.yourong.common.web.TokenHelper;

/**
 * @desc CSRF拦截器
 * @author fuyili 2015年12月3日上午10:26:12
 */
public abstract class BaseCSRFHandlerInterceptor extends HandlerInterceptorAdapter {
	private static final List<String> DEFAULT_IGNORE_SUFFIX = Arrays.asList("css;js;jpg;ico;jpeg;bmp;gif;png;css;swf"
			.split(";"));
	private static final String REGEX = "_";
	Logger logger = LoggerFactory.getLogger(BaseCSRFHandlerInterceptor.class);
	private static String REGISTERTRACESOURCE = "registerTraceSource";
	private static String TRACKID = "trackid";
	private static String REGISTERTRACENO = "registerTraceNo";
	protected static final String SHORT_URL = "inviteCode_shortURL";

	/**
	 * @Description:需要排除的URL
	 * @return
	 * @author: fuyili
	 * @time:2015年12月4日 下午12:48:59
	 */
	public abstract List<String> getExcludeUrls();

	/**
	 * @Description:需要验证onceToken的url
	 * @return
	 * @author: fuyili
	 * @time:2015年12月4日 下午12:48:27
	 */
	public abstract List<String> getOnceTokenValidateUrls();

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (handler instanceof DefaultServletHttpRequestHandler) {
			return true;
		}
		String registerTraceSource = request.getParameter(REGISTERTRACESOURCE);
		String registerTraceNo = request.getParameter(REGISTERTRACENO);
		String trackid = request.getParameter(TRACKID);
		if (StringUtil.isNotBlank(trackid)) {
			String[] temp = trackid.split(REGEX, 2);
			registerTraceSource = temp[0];
			registerTraceNo = temp[1];
		}

		String memberShortUrl = request.getParameter(SHORT_URL);

		if (StringUtil.isNotBlank(registerTraceSource)) {
			request.getSession().setAttribute(Constant.REGISTERTRACESOURCE, registerTraceSource);
		}
		if (StringUtil.isNotBlank(registerTraceNo)) {
			request.getSession().setAttribute(Constant.REGISTERTRACENO, registerTraceNo);
		}
		if (StringUtil.isNotBlank(memberShortUrl)) {// 邀请码
			if (RedisMemberClient.isExistMemberInviteCode(memberShortUrl)) {
				request.getSession().setAttribute(SHORT_URL, memberShortUrl);
			}
		}
		HttpServletRequest httprequest = (HttpServletRequest) request;
		String path = httprequest.getRequestURI().trim();
		if (path.indexOf(".") > 0) {
			String suffix = path.substring(path.lastIndexOf(".") + 1).toLowerCase();
			if (DEFAULT_IGNORE_SUFFIX.contains(suffix))
				return true;
		}
		if (request.getMethod().equalsIgnoreCase("GET")) {
			return true;
		} else {
			if (ignore(path)) {
				return true;
			}
			String sessionToken = CSRFTokenManager.getTokenForSession(request.getSession());
			String requestToken = CSRFTokenManager.getTokenFromRequest(request);
			if (sessionToken.equals(requestToken)) {
				return isJudgeOnceToken(request, response, path);
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug(request.getRequestURL() + "  Bad or missing CSRF value");
				}
				// 判断登陆跳转的情况
				if (checkedLoginRedirect(request, response)) {
					response.sendRedirect(PropertiesUtil.getWebRootUrl());
				}
				return false;
			}
		}
	}

	/**
	 * 判断oncetoken 是否存在
	 * 
	 * @param request
	 * @param response
	 * @param path
	 * @return
	 * @throws IOException
	 */
	private boolean isJudgeOnceToken(HttpServletRequest request, HttpServletResponse response, String path)
			throws IOException {
		if (isExist(path)) {
			HttpServletRequest _request = request;
			boolean flag = TokenHelper.validToken(_request);
			return isResponError(response, flag);
		} else {
			return true;
		}
	}

	/**
	 * 是否重复提交返回错误
	 *
	 * @param response
	 * @param flag
	 * @return
	 * @throws IOException
	 */
	private boolean isResponError(HttpServletResponse response, boolean flag) throws IOException {
		if (flag) {
			return true;
		} else {
			ResultDO resultObject = new ResultDO();
			resultObject.setResultCode(ResultCode.SUBMIT_REPEAT_DATA);
			String jsonString = JSON.toJSONString(resultObject);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json; charset=utf-8");
			ServletOutputStream outputStream = response.getOutputStream();
			outputStream.write(jsonString.getBytes(Constant.DEFAULT_CHARSET));
			return false;
		}
	}

	/**
	 * 判断是否忽略该条请求
	 *
	 * @return
	 */
	private boolean ignore(String path) {
		if (isInclude(path, getExcludeUrls()))
			return true;
		return false;
	}

	private boolean isExist(String path) {
		if (isInclude(path, getOnceTokenValidateUrls()))
			return true;
		return false;
	}

	private boolean isInclude(String path, List<String> urls) {
		for (String excludeUrl : urls) {
			if (excludeUrl.indexOf("*") >= 0) {
				if (path.matches(excludeUrl.replaceAll("\\*", ".*"))) {
					return true;
				}
			} else {
				if (path.indexOf(excludeUrl) >= 0) {
					return true;
				}
			}
		}
		return false;
	}

	/** 验证登陆跳转 **/
	public boolean checkedLoginRedirect(HttpServletRequest req, HttpServletResponse resp) throws Exception{
		String requestType = req.getHeader("X-Requested-With");
		if (requestType != null && "XMLHttpRequest".equals(requestType)) {
			SavedRequest saved = new SavedRequest(req);
			req.getSession().setAttribute(Constant.SAVED_REQUEST_KEY, saved);
			req.getSession().setAttribute(Constant.SAVED_REQUEST_KEY_IS_IGNORE, true);
			resp.sendError(901, "session time out");
			return false;
		}
		String contentType = req.getHeader("Content-Type");
		if (contentType != null) {
			SavedRequest saved = new SavedRequest(req);
			req.getSession().setAttribute(Constant.SAVED_REQUEST_KEY_IS_IGNORE, true);
			req.getSession().setAttribute(Constant.SAVED_REQUEST_KEY, saved);
			resp.sendRedirect(req.getContextPath() + "/security/login");
			return false;
		}
		return false;
	}

}
