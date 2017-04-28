package com.yourong.backend.ic.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.backend.BaseController;
import com.yourong.backend.ic.service.ProjectRecommendService;
import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.Project;

@Controller
@RequestMapping("recommend")
public class ProjectRecommendController extends BaseController {
	
	@Autowired
	private ProjectRecommendService projectRecommendService;
	
	/**
	 * 项目推荐首页
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "index")
	@RequiresPermissions("recommend:index")
	public String showRecommendIndex(HttpServletRequest req, HttpServletResponse resp){
		return "/ic/recommend/index";
	}
	
	/**
	 * 项目推荐列表
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException 
	 */
	@RequestMapping(value = "ajax")
	@RequiresPermissions("recommend:index")
	@ResponseBody
	public Object showRecommendDataList(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException{
		Page<Project> pageRequest = new Page<Project>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		return projectRecommendService.findProjectRecommendByPage(pageRequest, map);
	}
	
	/**
	 * 添加项目推荐
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException 
	 */
	@RequestMapping(value = "save")
	@RequiresPermissions("recommend:save")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "项目推荐模块",desc = "添加项目推荐")
	public ResultDO addRecommend(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException{
		ResultDO result = new ResultDO();
		try {
			Long projectId = ServletRequestUtils.getLongParameter(req, "projectId");
			String recommendList[] = ServletRequestUtils.getStringParameters(req, "recommendList");
			if(recommendList == null || recommendList.length < 1){
				result.setSuccess(false);
				return result;
			}
			for(String s  : recommendList){
				if(s.equals("1")){
					result = projectRecommendService.addRecommend(projectId);
				}else{
					result = projectRecommendService.addAppRecommend(projectId);
				}
			}
		}catch(Exception ex){
			result.setSuccess(false);
		}
		return result;
	}
	
	/**
	 * 更新推荐权重
	 * @param projectNotice
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "update")
	@RequiresPermissions("recommend:edit")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "项目推荐模块",desc = "更新推荐权重")
	public ResultDO updateRecommend(HttpServletRequest req, HttpServletResponse resp){
		ResultDO result = new ResultDO();
		try {
			Long projectId = ServletRequestUtils.getLongParameter(req, "projectId");
			int weight = ServletRequestUtils.getIntParameter(req, "weight");
			int recommendType = ServletRequestUtils.getIntParameter(req, "recommendType");
			result = projectRecommendService.updateRecommend(projectId, weight,recommendType);
		}catch(Exception ex){
			result.setSuccess(false);
		}
		return result;
	}
	
	
	/**
	 * 取消推荐
	 * @param projectNotice
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "delete")
	@RequiresPermissions("recommend:delete")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "项目推荐模块",desc = "取消推荐")
	public ResultDO deleteRecommend(HttpServletRequest req, HttpServletResponse resp){
		ResultDO result = new ResultDO();
		try {
			Long projectId = ServletRequestUtils.getLongParameter(req, "id");
			int recommendType = ServletRequestUtils.getIntParameter(req, "recommendType");
			result = projectRecommendService.cancelRecommend(projectId,recommendType);
		}catch(Exception ex){
			result.setSuccess(false);
		}
		return result;
	}
	
}
