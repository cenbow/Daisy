package com.yourong.backend.ic.controller;

import java.util.HashMap;
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
import com.yourong.backend.ic.service.ProjectService;
import com.yourong.backend.tc.service.TransactionService;
import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.DirectProjectBiz;
import com.yourong.core.ic.model.Project;
import com.yourong.core.ic.model.ProjectInterestBiz;
	
/**
 * 
 * @desc 放款管理
 * @author chaisen
 * 2015年12月30日下午3:16:59
 */
@Controller
@RequestMapping("loanManager")
public class LoanManagerController extends BaseController{
	
	@Autowired
	private ProjectService projectService;
	@Autowired
	private TransactionService transactionService;
	/**
	 * 
	 * @Description:跳转到项目放款列表
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 * @author: chaisen
	 * @time:2016年1月4日 下午6:00:45
	 */
	@RequestMapping(value = "index")
	@RequiresPermissions("loanManager:index")
	public String showLoanManageIndex(HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		return "/p2p/loan/index";
	}
	/**
	 * 
	 * @Description:分页查询放款管理列表
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 * @author: chaisen
	 * @time:2016年1月4日 下午6:01:08
	 */
	@RequestMapping(value = "ajax")
	@RequiresPermissions("loanManager:ajax")
	@ResponseBody
	public Object findLoanList(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException{
		Map<String, Object> map = Maps.newHashMap();
		getFilterMapFromRequest(req, map);
		Page<DirectProjectBiz> pageRequest = new Page<DirectProjectBiz>();
		getPageInfoFromRequest(req, pageRequest);
		Page<DirectProjectBiz> pager = projectService.selectLoanForPagin(pageRequest, map);
		return pager;
	}
	
	/**
	 * 
	 * @Description:获取放款基本信息
	 * @param req
	 * @param resp
	 * @return
	 * @author: chaisen
	 * @time:2016年1月4日 下午6:01:22
	 */
	@RequestMapping(value = "getLoanInfo")
	@RequiresPermissions("loanManager:getLoanInfo")
	@ResponseBody
	public Object getLoanInfo(HttpServletRequest req, HttpServletResponse resp) {
	    long id =  ServletRequestUtils.getLongParameter(req, "id", 0); 
	    ProjectInterestBiz project = projectService.selectProjectInfoById(id);	 
	    return project;		 
	}
	
	/**
	 * 
	 * @Description:放款
	 * @param projectBiz
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 * @author: chaisen
	 * @time:2016年1月5日 上午9:47:13
	 */
	@RequestMapping(value = "loan")
	@RequiresPermissions("loanManager:loan")
	@ResponseBody
	@LogInfoAnnotation( moduleName ="放款管理",desc = "放款")
	public Object saveLoan(@ModelAttribute("project") Project project, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		return transactionService.beforeLoanToOriginalCreditor(project.getId(),this.getCurrentLoginUserInfo().getId());
	}
	/**
	 * 
	 * @Description:根据项目id查询项目本息
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 * @author: chaisen
	 * @time:2016年1月12日 上午10:42:41
	 */
	@RequestMapping(value = "getPayRecord")
	@RequiresPermissions("loanManager:getPayRecord")
	@ResponseBody
	public Object getPayRecord(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		Page<ProjectInterestBiz> pageRequest = new Page<ProjectInterestBiz>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("projectId", ServletRequestUtils.getLongParameter(req, "id"));
		this.getCurrentLoginUserInfo().getId();
		Page<ProjectInterestBiz> pager = projectService.findProjectInterest(pageRequest, map);
		return pager;
	}
}
