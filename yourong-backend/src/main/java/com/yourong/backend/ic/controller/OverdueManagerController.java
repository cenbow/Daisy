package com.yourong.backend.ic.controller;

import java.math.BigDecimal;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.backend.BaseController;
import com.yourong.backend.ic.service.ProjectService;
import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.StringUtil;
import com.yourong.core.fin.model.OverdueRepayLog;
import com.yourong.core.ic.model.ProjectInterestBiz;
	
/**
 * 
 * @desc 垫资管理
 * @author chaisen
 * 2015年12月30日下午3:16:59
 */
@Controller
@RequestMapping("overdueManager")
public class OverdueManagerController extends BaseController{
	
	@Autowired
	private ProjectService projectService;
	/**
	 * 
	 * @Description:逾期还款管理页面
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 * @author: chaisen
	 * @time:2016年1月20日 上午10:41:24
	 */
	@RequestMapping(value = "index")
	@RequiresPermissions("overdueManager:index")
	public String overdueIndex(HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		return "/p2p/overdue/index";
	}
	
	/**
	 * 
	 * @Description:分页查询逾期还款列表
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 * @author: chaisen
	 * @time:2016年1月20日 下午1:53:23
	 */
	@RequestMapping(value = "ajaxOverdue")
	@RequiresPermissions("overdueManager:ajaxOverdue")
	@ResponseBody
	public Object findOverdueList(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException{
		Page<ProjectInterestBiz> pageRequest = new Page<ProjectInterestBiz>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		Page<ProjectInterestBiz> pager = projectService.findOverdueList(pageRequest, map);
		return pager;
	}
	
	
	/**
	 * 
	 * @Description:获取逾期还款记录
	 * @param req
	 * @param resp
	 * @return
	 * @author: chaisen
	 * @time:2016年1月7日 下午8:13:17
	 */
	@RequestMapping(value = "getOverdueInfo")
	@RequiresPermissions("overdueManager:getOverdueInfo")
	@ResponseBody
	public Object getOverdueInfo(HttpServletRequest req, HttpServletResponse resp) {
	    long id =  ServletRequestUtils.getLongParameter(req, "id", 0); 
	    ProjectInterestBiz projectInterest = projectService.getOverdueInfo(id);	 
	    return projectInterest;		 
	}
	/**
	 * 
	 * @Description:逾期还款标记
	 * @param project
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 * @author: chaisen
	 * @time:2016年1月7日 下午8:52:12
	 */
	@RequestMapping(value = "saveRepayFlagInterest")
	@RequiresPermissions("overdueManager:saveRepayFlagInterest")
	@ResponseBody
	@LogInfoAnnotation( moduleName ="还本付息",desc = "逾期还款标记")
	public Object saveRepayFlagInterest(@ModelAttribute("project") ProjectInterestBiz project, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		project.setOperateId(this.getCurrentLoginUserInfo().getId());
		return projectService.saveRepayFlagInterest(project);
	}
	
	/**
	 * 
	 * @Description:查询还款记录-项目基本信息
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 * @author: chaisen
	 * @time:2016年1月12日 上午10:10:38
	 */
	@RequestMapping(value = "showPayRecord")
	@RequiresPermissions("overdueManager:showPayRecord")
	@ResponseBody
	public Object showPayRecord(@RequestParam("id") Long id,HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		ProjectInterestBiz biz = projectService.showProject(id);
		return biz;
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
	@RequiresPermissions("overdueManager:getPayRecord")
	@ResponseBody
	public Object getPayRecord(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		Page<ProjectInterestBiz> pageRequest = new Page<ProjectInterestBiz>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("projectId", ServletRequestUtils.getLongParameter(req, "id"));
		map.put("isMark", ServletRequestUtils.getStringParameter(req, "isMark"));	
		Page<ProjectInterestBiz> pager = projectService.showProjectInterest(pageRequest, map);
		return pager;
	}
	/**
	 * 
	 * @Description:查询逾期还款记录
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 * @author: chaisen
	 * @time:2016年1月28日 下午1:15:01
	 */
	@RequestMapping(value = "getOverdueRecord")
	@RequiresPermissions("overdueManager:getOverdueRecord")
	@ResponseBody
	public Object getOverdueRecord(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		Page<OverdueRepayLog> pageRequest = new Page<OverdueRepayLog>();
		Map<String, Object> map = new HashMap<String, Object>();
		Long projectId =ServletRequestUtils.getLongParameter(req, "id");
		map.put("projectId", projectId);
		Page<OverdueRepayLog> pager = projectService.getOverdueRecord(pageRequest, map);
		return pager;
	}
	/**
	 * 
	 * @Description:根据 projectId 获取 滞纳金
	 * @param projectId
	 * @param repayDate
	 * @return
	 * @author: chaisen
	 * @time:2016年1月28日 下午4:30:28
	 */
	@RequestMapping(value="getOverdueAmount")
	@ResponseBody
	public ProjectInterestBiz getOverdueAmount(@ModelAttribute("projectId")Long projectId,@ModelAttribute("repayDate")String repayDate){
		return projectService.getOverdueAmount(projectId,repayDate);
	}
	/**
	 * 
	 * @Description:根据违约金计算总额
	 * @param payableAmount
	 * @param overdueFine
	 * @return
	 * @author: chaisen
	 * @time:2016年2月1日 下午1:17:42
	 */
	@RequestMapping(value="getPayableAmount")
	@ResponseBody
	public Object getPayableAmount(@ModelAttribute("payableAmount")BigDecimal payableAmount,@ModelAttribute("overdueFine")BigDecimal overdueFine){
		return projectService.getPayableAmount(payableAmount,overdueFine);
	}
	
	/**
	 * 债权项目逾期还款标记
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "debtOverdueIndex")
	@RequiresPermissions("overdueManager:debtOverdueIndex")
	public String debtOverdueIndex(HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		return "/ic/overdue/index";
	}
	
	/**
	 * 垫资逾期还款标记
	 * 
	 * @param project
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "saveDebtOverdueRepay")
//	@RequiresPermissions("overdueManager:saveDebtOverdueRepay")
	@ResponseBody
	@LogInfoAnnotation( moduleName ="债权项目管理",desc = "垫资逾期还款标记")
	public Object saveDebtOverdueRepay(@ModelAttribute("project") ProjectInterestBiz project, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		project.setOperateId(this.getCurrentLoginUserInfo().getId());
		return projectService.toUnderWriteRepay(project);
	}
	
}
