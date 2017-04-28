package com.yourong.backend.ic.controller;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.backend.BaseController;
import com.yourong.backend.ic.service.EarlyRepaymentService;
import com.yourong.backend.ic.service.ProjectService;
import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.DateUtils;
import com.yourong.core.ic.model.Debt;
import com.yourong.core.ic.model.ProjectEarlyRepayment;


@Controller
@RequestMapping("earlyRepayment")
public class EarlyRepaymentController  extends BaseController {

	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private EarlyRepaymentService earlyRepaymentService;
	
	/**
	 * 列表页面
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "index")
	@RequiresPermissions("earlyRepayment:index")
	public String showEarlyRepaymentIndex(HttpServletRequest req, HttpServletResponse resp) {
		return "/ic/earlyRepayment/index";
	}
	
	/**
	 * 列表数据
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@RequestMapping(value = "ajax")
	@RequiresPermissions("earlyRepayment:ajax")
	@ResponseBody
	public Object showEarlyRepaymentPages(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		Page<ProjectEarlyRepayment> pageRequest = new Page<ProjectEarlyRepayment>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		Page<ProjectEarlyRepayment> pager = earlyRepaymentService.findByPage(pageRequest, map);
		return pager;
	}
	
	
	/**
	 * 提前还款
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@RequestMapping(value = "repay")
	@RequiresPermissions("earlyRepayment:repay")
	@ResponseBody
	@LogInfoAnnotation( moduleName ="提前还款",desc = "提前还款操作")
	public Object repayment(@ModelAttribute("prepaymentTime") String prepaymentTime,@ModelAttribute("projectId") Long projectId,
			@ModelAttribute("prepaymentRemarkSys") String prepaymentRemarkSys,@ModelAttribute("prepaymentRemarkFront") String prepaymentRemarkFront,
			HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		Date prepaymentDate = DateUtils.getDateFromString(prepaymentTime, "yyyy-MM-dd");
		return earlyRepaymentService.prepayment(prepaymentDate,projectId, prepaymentRemarkSys,prepaymentRemarkFront,this.getCurrentLoginUserInfo().getId());
	}
	
}
