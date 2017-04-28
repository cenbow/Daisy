package com.yourong.backend.tc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.backend.BaseController;
import com.yourong.backend.tc.service.PreservationService;

@Controller
@RequestMapping("preservation")
public class PreservationController extends BaseController {

	@Autowired
	private PreservationService preservationService;
	

	/**
	 * 生成合同
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "createIndex")
	@RequiresPermissions("preservation:create")
	public Object createIndex(HttpServletRequest req, HttpServletResponse resp) {
		return "tc/preservation/createPre";
	}
	
	@RequestMapping(value = "create")
	@RequiresPermissions("preservation:create")
	@ResponseBody
	public Object createAjax(HttpServletRequest req, HttpServletResponse resp) {
		Long id = ServletRequestUtils.getLongParameter(req, "id", 0L);
		return preservationService.createPreservationByTransactionId(id);
	}
}
