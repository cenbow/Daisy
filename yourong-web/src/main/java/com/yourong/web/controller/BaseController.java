package com.yourong.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.google.common.collect.Lists;
import com.yourong.common.constant.Constant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.util.StringUtil;
import com.yourong.web.dto.MemberSessionDto;
import com.yourong.web.utils.ServletUtil;


public abstract class BaseController {

	/**
	 * 日志对象
	 */
	protected Logger logger = LoggerFactory.getLogger(BaseController.class);
	protected static final String  INVITECODE = "inviteCode_shortURL";
	/***
	 * 统一异常处理
	 * @param request
	 * @param ex
	 * @return
	 * author: pengyong	 * 
	 */
	@ExceptionHandler
	public String exp(HttpServletRequest request, Exception ex) {
		logger.error("页面请求异常：URL"+request.getRequestURI(), ex);
		request.setAttribute("ex", ex);		
		return "/500";		
	}	
	protected List<ResultCode> formatListObjectErrorType(List<ObjectError> erros){
		List<ResultCode> resultCodeList= Lists.newArrayList();
		for (ObjectError error : erros) {
			ResultCode resultCode = ResultCode.getResultCodeByCode(error.getDefaultMessage());
			if(resultCode != null){
				if(!resultCodeList.contains(resultCode)){
					resultCodeList.add(resultCode);
				}
			}
		}		
		return resultCodeList;
	}
	protected void setResponseHeaders(HttpServletResponse response) {
		response.setContentType("image/png");
		response.setHeader("Cache-Control", "no-cache, no-store");
		response.setHeader("Pragma", "no-cache");
		long time = System.currentTimeMillis();
		response.setDateHeader("Last-Modified", time);
		response.setDateHeader("Date", time);
		response.setDateHeader("Expires", time);
	}
	
	
	protected ResultDO<?> validateResult(ResultDO<?> resultObject, BindingResult result) {
		int errorCount = result.getErrorCount();
		if (errorCount > 0) {	
			resultObject.setResultCodeList(formatListObjectErrorType(result.getAllErrors()));
		}	
		return resultObject;
	}
	
	protected HttpSession getHttpSession(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			session = request.getSession();
		}
		return session;
	}
	/**
	 * 从session获取member
	 */
	protected MemberSessionDto getMember() {
		return ServletUtil.getUserDO();
	}
	/**
	 * 从session获取member
	 */
	protected MemberSessionDto getMember(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			session = request.getSession();
		}
		MemberSessionDto username = (MemberSessionDto) session.getAttribute(Constant.CURRENT_USER);
		return username;
	} 
	
	/**
	 * 更新登录会员信息
	 * @param request
	 * @param memberSession
	 */
	protected void updateMemberSessionDto(HttpServletRequest request,MemberSessionDto memberSession){
		HttpSession session = request.getSession(false);
		if (session == null) {
			session = request.getSession();
		}
		session.removeAttribute(Constant.CURRENT_USER);
		session.setAttribute(Constant.CURRENT_USER, memberSession);
	}

	/**
	 * 
	 * @Description:获取真实IP
	 * @param request
	 * @return
	 * @author: wangyanji
	 * @time:2016年3月28日 上午10:15:41
	 */
	protected String getIp(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (StringUtil.isBlank(ip)) {
			ip = request.getRemoteAddr();
		}
		// 如果是负载均衡，取第一个IP
		if (StringUtil.isNotBlank(ip) && ip.indexOf(",") > -1) {
			String[] arr = ip.split(",");
			ip = arr[0];
		}
		return ip;
	}
}
