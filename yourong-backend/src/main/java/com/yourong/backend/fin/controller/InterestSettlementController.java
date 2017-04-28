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

import com.yourong.backend.BaseController;
import com.yourong.backend.fin.service.InterestSettlementService;
import com.yourong.common.pageable.Page;
import com.yourong.core.fin.model.biz.InterestSettlementBiz;

/**
 * 线上线下利息营收结算
 * 
 * @author fuyili 2014年12月29日下午3:39:31
 */
@Controller
@RequestMapping("interestSettlement")
public class InterestSettlementController extends BaseController {

	@Autowired
	private InterestSettlementService interestSettlementService;

	@RequestMapping(value = "index")
	@RequiresPermissions("interestSettlement:index")
	public String showPage() {
		return "/fin/interestSettlement/index";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "ajax")
	@RequiresPermissions("interestSettlement:ajax")
	@ResponseBody
	public Object showPageDate(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		Page<InterestSettlementBiz> pageRequest = new Page<InterestSettlementBiz>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		return interestSettlementService.findByPage(pageRequest, map);
	}

}
