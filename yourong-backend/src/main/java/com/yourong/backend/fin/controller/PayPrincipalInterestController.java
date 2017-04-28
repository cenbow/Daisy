package com.yourong.backend.fin.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.yourong.backend.BaseController;
import com.yourong.backend.fin.service.PayPrincipalInterestService;
import com.yourong.backend.ic.service.ProjectService;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.pageable.Page;
import com.yourong.core.fin.model.biz.PayPrincipalInterestBiz;

/**
 * 托管还本付息
 * 
 * @author fuyili 2014年12月24日上午11:39:35
 */	
@Controller
@RequestMapping("payPrincipalInterest")
public class PayPrincipalInterestController extends BaseController {
	@Autowired
	private PayPrincipalInterestService payPrincipalInterestService;
	@Autowired
	private ProjectService projectService;

	/**
	 * 列表页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "index")
	@RequiresPermissions("payPrincipalInterest:index")
	private ModelAndView showPage() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/fin/payPrincipalInterest/index");
		int fullCount = projectService.findProjectCountByStatus(StatusEnum.PROJECT_STATUS_FULL.getStatus());
		int repayCount = projectService.findProjectCountByStatus(StatusEnum.PROJECT_STATUS_REPAYMENT.getStatus());
		Map<String, Object> map = Maps.newHashMap();
		map.put("status", StatusEnum.FIN_PAY_PRINCIPAL_INTEREST_WAIT_PAY.getStatus());
		PayPrincipalInterestBiz waitPayInterestBiz = payPrincipalInterestService
				.findTotalPrincipalAndInterestByStatus(map);
		map.put("status", StatusEnum.FIN_PAY_PRINCIPAL_INTEREST_CURRENT_PAID.getStatus());
		PayPrincipalInterestBiz paidInterestBiz = payPrincipalInterestService
				.findTotalPrincipalAndInterestByStatus(map);
		map.put("status", null);
		PayPrincipalInterestBiz allInterestBiz = payPrincipalInterestService
				.findTotalPrincipalAndInterestByStatus(map);
		mv.addObject("repayCount", repayCount);
		mv.addObject("fullCount", fullCount);
		mv.addObject("waitPayInterestBiz", waitPayInterestBiz);
		mv.addObject("paidInterestBiz", paidInterestBiz);
		mv.addObject("allInterestBiz", allInterestBiz);
		return mv;
	}

	/**
	 * 列表数据
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "ajax")
	@RequiresPermissions("payPrincipalInterest:ajax")
	@ResponseBody
	private Object showPageData(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		Page<PayPrincipalInterestBiz> pageRequest = new Page<PayPrincipalInterestBiz>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		return payPrincipalInterestService.findByPage(pageRequest, map);
	}
}
