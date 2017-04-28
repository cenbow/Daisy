package com.yourong.backend.ic.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.yourong.backend.BaseController;
import com.yourong.backend.ic.service.ProjectTypeService;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.ProjectType;

@Controller
@RequestMapping("projectType")
public class ProjectTypeController extends BaseController {
	@Autowired
	private ProjectTypeService projectTypeService;

	@RequestMapping(value = "index")
	@RequiresPermissions("projectType:index")
	public String showProjectTypeIndex(HttpServletRequest req, HttpServletResponse resp) {
		return "ic/projectType/index";
	}

	@RequestMapping(value = "ajax")
	@RequiresPermissions("projectType:ajax")
	@ResponseBody
	public Object showProjectTypePages(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		Map<String, Object> map = Maps.newHashMap();
		getFilterMapFromRequest(req, map);
		Page<ProjectType> pageRequest = new Page<ProjectType>();
		getPageInfoFromRequest(req, pageRequest);
		Page<ProjectType> pager = projectTypeService.findByPage(pageRequest, map);
		return pager;
	}

	@RequestMapping(value = "save")
	@RequiresPermissions("projectType:save")
	@ResponseBody
	public Object saveProjectType(@ModelAttribute ProjectType projectType, HttpServletRequest req, HttpServletResponse resp) {
		if (projectType.getId() != null) {
			int insertSelective = projectTypeService.updateByPrimaryKeySelective(projectType);
		} else {
			int insertSelective = projectTypeService.insert(projectType);
		}
		return "1";
	}

	@RequestMapping(value = "show")
	@RequiresPermissions("projectType:show")
	@ResponseBody
	public Object showProjectType(HttpServletRequest req, HttpServletResponse resp) {
		Long id = ServletRequestUtils.getLongParameter(req, "id", 0);
		ProjectType projectType = projectTypeService.selectByPrimaryKey(id);
		return projectType;
	}

	@RequestMapping(value = "delete")
	@RequiresPermissions("projectType:delete")
	@ResponseBody
	public Object deleteProjectType(HttpServletRequest req, HttpServletResponse resp) {
		long[] id = ServletRequestUtils.getLongParameters(req, "id[]");
		projectTypeService.batchDelete(id);
		return "1";
	}

	/**
	 * 根据担保方式、担保物类型、是否分期获取详细信息
	 */
	@RequestMapping("search")
	@RequiresPermissions("projectType:search")
	@ResponseBody
	public Object searchProjectType(@ModelAttribute("projectType") ProjectType projectType, HttpServletRequest request) {
		projectType = projectTypeService.selectProjectTypeDetail(projectType);
		return projectType;

	}
	
	
}