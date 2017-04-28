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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.backend.BaseController;
import com.yourong.backend.ic.service.ProjectNoticeService;
import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.ProjectNotice;

@Controller
@RequestMapping("notice")
public class ProjectNoticeController extends BaseController {

	@Autowired
	private ProjectNoticeService projectNoticeService;
	
	/**
	 * 项目预告首页
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "index")
	@RequiresPermissions("notice:index")
	public String showNoticeIndex(HttpServletRequest req, HttpServletResponse resp){
		return "/ic/notice/index";
	}
	
	/**
	 * 项目预告列表
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException 
	 */
	@RequestMapping(value = "ajax")
	@RequiresPermissions("notice:index")
	@ResponseBody
	public Object showNoticeList(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException{
		Page<ProjectNotice> pageRequest = new Page<ProjectNotice>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		return projectNoticeService.findByPage(pageRequest, map);
	}
	
	/**
	 * 添加项目预告
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "save")
	@RequiresPermissions("notice:save")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "项目预告模块",desc = "添加项目预告")
	public ResultDO addNotice(@ModelAttribute ProjectNotice projectNotice, HttpServletRequest req, HttpServletResponse resp){
		ResultDO result = new ResultDO();
		if(projectNotice.getId() != null){
			result = projectNoticeService.updateProjectNotice(projectNotice);
		}else{
			result = projectNoticeService.insertProjectNotice(projectNotice);
		}
		return result;
	}
	
	/**
	 * 查看项目预告
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@RequestMapping(value = "show")
	@RequiresPermissions("notice:find")
	@ResponseBody
	public ProjectNotice showNotice(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException{
		Long id = ServletRequestUtils.getLongParameter(req, "id");
		return projectNoticeService.findProjectNoticeById(id);
	}
	
	/**
	 * 更新项目预告
	 * @param projectNotice
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "update")
	@RequiresPermissions("notice:edit")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "项目预告模块",desc = "更新项目预告")
	public ResultDO updateProjectNotice(@ModelAttribute ProjectNotice projectNotice ,HttpServletRequest req, HttpServletResponse resp){
		return projectNoticeService.updateProjectNotice(projectNotice);
	}
	
	/**
	 * 删除项目预告
	 * @param projectNotice
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException 
	 */
	@RequestMapping(value = "delete")
	@RequiresPermissions("notice:delete")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "项目预告模块",desc = "删除项目预告")
	public ResultDO deleteProjectNotice(@RequestParam("id")Long id,HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException{
//		Long id = ServletRequestUtils.getLongParameter(req, "id");
		return projectNoticeService.deleteProjectNoticeById(id);
	}
	
	
	/**
	 * 暂停项目预告
	 * @param projectNotice
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException 
	 */
	@RequestMapping(value = "stop")
	@RequiresPermissions("notice:stop")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "项目预告模块",desc = "暂停项目预告")
	public ResultDO stopProjectNotice(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException{
		Long id = ServletRequestUtils.getLongParameter(req, "id");
		return projectNoticeService.stopProjectNoticeById(id);
	}
	
	/**
	 * 恢复项目预告
	 * @param projectNotice
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException 
	 */
	@RequestMapping(value = "start")
	@RequiresPermissions("notice:start")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "项目预告模块",desc = "恢复项目预告")
	public ResultDO startProjectNotice(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException{
		Long id = ServletRequestUtils.getLongParameter(req, "id");
		return projectNoticeService.startProjectNoticeById(id);
	}
	
	/**
	 * 推荐项目预告
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@RequestMapping(value = "recommend")
	@RequiresPermissions("notice:recommend")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "项目预告模块",desc = "推荐项目预告")
	public ResultDO recommendProjectNotice(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException{
		Long id = ServletRequestUtils.getLongParameter(req, "id");
		ResultDO result = new ResultDO();
		try {
			result = projectNoticeService.recommendProjectNotice(id);
		} catch (ManagerException e) {
			result.setSuccess(false);
		}
		return result;
	}
	
	/**
	 * 取消推荐
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@RequestMapping(value = "cancelRecommend")
	@RequiresPermissions("notice:recommend")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "项目预告模块",desc = "取消推荐")
	public ResultDO cancelRecommendProjectNotice(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException{
		Long id = ServletRequestUtils.getLongParameter(req, "id");
		return projectNoticeService.cancelRecommendProjectNotice(id);
	}
	
	
	/**
	 * 更新推荐权重
	 * @param projectNotice
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "updateSort")
	@RequiresPermissions("notice:edit")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "项目预告模块",desc = "更新预告权重")
	public ResultDO updateSort(HttpServletRequest req, HttpServletResponse resp){
		ResultDO result = new ResultDO();
		try {
			Long id = ServletRequestUtils.getLongParameter(req, "id");
			int sort = ServletRequestUtils.getIntParameter(req, "sort");
			result = projectNoticeService.updateProjectNoticeSort(id, sort);
		}catch(Exception ex){
			result.setSuccess(false);
		}
		return result;
	}
	
	@RequestMapping(value = "queryProjectFromNotice")
	@RequiresPermissions("notice:save")
	@ResponseBody
	public Object queryProjectFromNotice(HttpServletRequest req, HttpServletResponse resp){
		String projectName = ServletRequestUtils.getStringParameter(req, "projectName", null);
		return projectNoticeService.queryProjectFromNotice(projectName);
	}
	
}
