package com.yourong.backend;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yourong.common.constant.Constant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.StringUtil;
import com.yourong.core.sys.model.SysUser;


public abstract class BaseController {
	

	/**
	 * 日志对象
	 */
	protected Logger logger = LoggerFactory.getLogger(getClass());

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

	protected Map getPageInfoFromRequest(HttpServletRequest req,Page pageRequest) throws ServletRequestBindingException{
		Map<String,Object> map = Maps.newHashMap();
		
		int iDisplayLength = ServletRequestUtils.getIntParameter(req, "iDisplayLength", 10);
		int iDisplayStart = ServletRequestUtils.getIntParameter(req, "iDisplayStart", 0);
		int iColumns = ServletRequestUtils.getIntParameter(req, "iColumns", 0);
		
		String[] fields = new String[iColumns];
		for(int i=0;i<iColumns;i++){
			fields[i] = ServletRequestUtils.getStringParameter(req, "mDataProp_"+i);
		}
		String sSortDir = ServletRequestUtils.getStringParameter(req, "sSortDir_0");
		int iSortingCol = ServletRequestUtils.getIntParameter(req, "iSortCol_0", 0);
		String sortfile = "";
		if(StringUtil.isBlank(fields[iSortingCol])){
			sortfile =fields[0];
		}else{
			
			sortfile = StringUtil.getTableColumName(fields[iSortingCol]);
		}
		
		map.put(sSortDir, sortfile);		
		pageRequest.setiDisplayStart(iDisplayStart);
		pageRequest.setiDisplayLength(iDisplayLength);
		
		getFilterMapFromRequest(req, map);
		
		
		return map;
	}
	protected List<String> changeListObjectErrorType(List<ObjectError> erros){
		List<String> strings = Lists.newArrayList();
		for (ObjectError error : erros) {
			strings.add(error.getDefaultMessage());
		}		
		return strings;
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
	
	
	protected void getFilterMapFromRequest(HttpServletRequest req,Map<String,Object> map ){		
		Map parameterMap = req.getParameterMap();
		Set entrySet = parameterMap.entrySet();
		Iterator iterator = entrySet.iterator();
		while (iterator.hasNext()) {
			Entry next = (Entry) iterator.next();
			String key = (String) next.getKey();
			if (key.startsWith("search_")) {
				if (next.getValue() instanceof String[]) {
					String[] demo = (String[]) next.getValue();
					if (demo != null && demo.length > 0) {
						if (StringUtils.isNotEmpty(demo[0])) {
							String attr  = key.substring("search_".length(), key.length());						
							map.put(attr, demo[0]);
						}

					}
				}

			}
		}
		
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
	
	/**
	 * 把参数根据逗号分隔转换成int数组
	 * @param req
	 * @param param 参数名称
	 * @return
	 */
	protected long[] getParametersIds(HttpServletRequest req, String param){
		String name = req.getParameter(param);
		long[] ids = null;
		if(name != null && !name.equals("")) {
			String[] split = name.split(",");
			ids = new long[split.length];		
			for(int i=0;i<split.length;i++){
				ids[i] = NumberUtils.toLong(split[i]);
			}
		}
		return ids;
	}
	
	protected Map<String,Object> getParameterMap(HttpServletRequest request) {
        Map<String,Object> map = new HashMap<String,Object>();
		Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            String value = request.getParameter(parameterName);
			if (StringUtil.isBlank(value))
				continue;
			if (parameterName.toLowerCase().endsWith("time")) {
				map.put(parameterName, DateUtils.toDate(value, DateUtils.DATE_FMT_3));
			}else{
				map.put(parameterName, value);
			}
        }
        return map;
    }

	/**
	 * 把前台提交的json值转换成对象
	 * @param request
	 * @param paramName json参数名称
	 * @param target    目标对象
	 * @return
	 */
	protected List parseJsonToObject(HttpServletRequest request, String paramName, Class target){
		String json = request.getParameter(paramName);
		List objList = null;
		if(StringUtil.isNotBlank(json)){
			try{
				objList = Lists.newArrayList();
				JSONArray arr = JSON.parseArray(json);
				for(int i=0; i<arr.size();i++){
					objList.add(BeanCopyUtil.map(arr.get(i), target));
				}
			}catch(Exception ex){
				objList = null;
				logger.error("json转换异常",ex);
			}
		}
		return objList;
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
	
	public static SysUser getCurrentLoginUserInfo(){
	      SysUser  sysUser = null;
		Subject subject = SecurityUtils.getSubject();		
		if(subject != null && subject.isAuthenticated() ){
		    sysUser =  (SysUser) subject.getSession().getAttribute(Constant.CURRENT_USER);
		}
		return sysUser;
	}
	
}
