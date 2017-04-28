package com.yourong.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.common.util.StringUtil;
import com.yourong.web.service.ProjectService;

@Controller
@RequestMapping("zhongNiu")
public class ZhongNiuController extends BaseController{
	private static Logger logger = LoggerFactory.getLogger(ZhongNiuController.class);
	@Autowired
	private ProjectService projectService;
	
	@RequestMapping(value = "/getList",method = RequestMethod.GET)
	@ResponseBody
	public Object getProjectList(HttpServletRequest req, HttpServletResponse resp){
		logger.info("众牛平台，请求查询项目列表数据");
		return projectService.getProjectListToZhongNiu();
	}
	
	@RequestMapping(value = "getData/pid/{projectId}",method = RequestMethod.GET)
	@ResponseBody
	public Object getProjectById(@PathVariable String projectId){
		logger.info("众牛平台，请求查询项目"+projectId+"数据");
		Long id = 0L;
		if(StringUtil.isNotBlank(projectId) && StringUtil.isNumeric(projectId)){
			id = Long.parseLong(projectId);
		}
		return projectService.getProjectDetailToZhongNiu(id);
	}

}
