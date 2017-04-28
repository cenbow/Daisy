package com.yourong.backend.sys.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.backend.BaseController;
import com.yourong.backend.cache.MyCacheManager;
import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.common.cache.RedisManager;
import com.yourong.common.domain.ResultDO;

@Controller
@RequestMapping("myCache")
public class MyCacheController extends BaseController {
	
	@Autowired
	MyCacheManager myCacheManager;
	
	
	/**
	 * 根据Key清空缓存
	 * @param request
	 * @param response
	 * @throws ServletRequestBindingException 
	 */
	@RequestMapping(value = "clearCacheByKey")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "清空redis缓存模块",desc = "清空redis缓存")
	public ResultDO clearCacheByKey(HttpServletRequest request, HttpServletResponse response) throws ServletRequestBindingException{
		ResultDO resultDO = new ResultDO();
		int index = ServletRequestUtils.getIntParameter(request, "key", 1);
		RedisManager.flushDB(index);
		resultDO.setSuccess(true);
		return resultDO;
	}
}

