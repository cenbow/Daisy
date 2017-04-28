package com.yourong.backend.ic.controller;

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
import com.yourong.backend.ic.service.ProjectService;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.DirectSettlementBiz;
/**
 * 
 * @desc 直投项目营收结算
 * @author chaisen
 * 2016年5月6日下午3:51:58
 */
@Controller
@RequestMapping("directSettlement")
public class DirectSettlementController extends BaseController {

	@Autowired
	private ProjectService projectService;

	@RequestMapping(value = "index")
	@RequiresPermissions("directSettlement:index")	
	public String showPage() {
		return "/p2p/directSettlement/index";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "ajax")
	@RequiresPermissions("directSettlement:ajax")
	@ResponseBody
	public Object showPageDate(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		Page<DirectSettlementBiz> pageRequest = new Page<DirectSettlementBiz>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		return projectService.findDirectSettlementByPage(pageRequest, map);
	}

}
