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
import com.yourong.backend.fin.service.CapitalInOutLogService;
import com.yourong.common.pageable.Page;
import com.yourong.core.fin.model.CapitalInOutLog;

@Controller
@RequestMapping("capitalInOutLog")
public class CapitalInOutLogController extends BaseController {

	@Autowired
    private CapitalInOutLogService capitalInOutLogService;
	
	@RequestMapping(value = "index")
	@RequiresPermissions("capitalInOutLog:index")
	public String showWithdrawLogIndex(HttpServletRequest req, HttpServletResponse resp) {
		return "fin/capitalInOutLog/index";
	}
	
	@RequestMapping(value = "ajax")
    @RequiresPermissions("capitalInOutLog:ajax")
    @ResponseBody
    public Object showRechargeLogPages(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
    	Page<CapitalInOutLog> pageRequest = new Page<CapitalInOutLog>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
        	Page<CapitalInOutLog> pager = capitalInOutLogService.findByPage(pageRequest,map);		 
         return pager;
    }
	
	/**
	 * 出借人资金流水
	 * @return
	 */
	@RequestMapping(value = "lenderIndex")
	@RequiresPermissions("capitalInOutLog:lenderIndex")
	public String showLenderIndex() {
		return "fin/lenderCapitalInOutLog/lenderIndex";
	}
	
	/**
	 * 出借人资金流水数据
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@RequestMapping(value = "lenderAjax")
    @RequiresPermissions("capitalInOutLog:lenderAjax")
    @ResponseBody
    public Object showLenderPages(HttpServletRequest req) throws ServletRequestBindingException {
    	Page<CapitalInOutLog> pageRequest = new Page<CapitalInOutLog>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		Page<CapitalInOutLog> pager = capitalInOutLogService.findLenderCapitalInOutLogPage(pageRequest,map);		 
         return pager;
    }
	
	
}
