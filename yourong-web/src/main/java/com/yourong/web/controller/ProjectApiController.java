package com.yourong.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.web.service.ProjectService;

@Controller
@RequestMapping("/project")
public class ProjectApiController extends BaseController {
	@Autowired
	private ProjectService projectService;
	
	@RequestMapping(value = "/api/queryProjectList",method = RequestMethod.GET)
	@ResponseBody
	public Object queryInvestingProjectToPinYou(HttpServletRequest req, HttpServletResponse resp){
		return projectService.queryInvestingProjectToPinYou();
	}
	
	@RequestMapping(value = "/api/getAllInvestingProject",method = RequestMethod.GET)
	@ResponseBody
	public Object getAllInvestingProject(HttpServletRequest req, HttpServletResponse resp){
		return projectService.queryCnGoldProjectList();
	}
	
}
