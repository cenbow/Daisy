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
import com.yourong.backend.fin.service.RechargeLogService;
import com.yourong.common.pageable.Page;
import com.yourong.core.fin.model.RechargeLog;

@Controller
@RequestMapping("rechargeLog")
public class RechargeLogController extends BaseController {

	@Autowired
    private RechargeLogService rechargeLogService;
	
	@RequestMapping(value = "index")
	@RequiresPermissions("rechargeLog:index")
	public String showWithdrawLogIndex(HttpServletRequest req, HttpServletResponse resp) {
		return "fin/rechargeLog/index";
	}
	
	@RequestMapping(value = "ajax")
    @RequiresPermissions("rechargeLog:ajax")
    @ResponseBody
    public Object showRechargeLogPages(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
    	Page<RechargeLog> pageRequest = new Page<RechargeLog>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
        	Page<RechargeLog> pager = rechargeLogService.findByPage(pageRequest,map);		 
         return pager;
    }
}
