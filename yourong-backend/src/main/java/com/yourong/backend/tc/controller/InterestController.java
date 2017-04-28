package com.yourong.backend.tc.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.backend.BaseController;
import com.yourong.backend.tc.service.TransactionInterestService;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.Debt;
import com.yourong.core.tc.model.TransactionInterest;

@Controller
@RequestMapping("interest")
public class InterestController extends BaseController {

	@Autowired
	private TransactionInterestService transactionInterestService;
	
	/**
	 * 本息查询页面
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "index")
	@RequiresPermissions("interest:index")
	public String showInterestIndex(HttpServletRequest req, HttpServletResponse resp) {
		return "tc/interest/index";
	}
	
	/**
	 * 本息查询
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "ajax")
	@RequiresPermissions("interest:ajax")
	@ResponseBody
	public Object showInterestPages(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		Page<TransactionInterest> pageRequest = new Page<TransactionInterest>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		Page<TransactionInterest> pager = transactionInterestService.findByPage(pageRequest, map);
		return pager;
	}

}
