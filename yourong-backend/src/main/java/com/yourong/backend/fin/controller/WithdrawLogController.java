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

import com.google.common.collect.Maps;
import com.yourong.backend.BaseController;
import com.yourong.backend.fin.service.WithdrawLogService;
import com.yourong.common.pageable.Page;
import com.yourong.core.fin.model.WithdrawLog;
import com.yourong.core.sys.manager.SysOperateInfoManager;

@Controller
@RequestMapping("withdrawLog")
public class WithdrawLogController extends BaseController {
	@Autowired
	private WithdrawLogService withdrawLogService;

	@Autowired
	private SysOperateInfoManager sysOperateInfoManager;
	
	@RequestMapping(value = "index")
	@RequiresPermissions("withdrawLog:index")
	public String showWithdrawLogIndex(HttpServletRequest req, HttpServletResponse resp) {
		return "fin/withdrawLog/index";
	}

	@RequestMapping(value = "indexBorrower")
	@RequiresPermissions("withdrawLog:indexBorrower")
	public String showWithdrawLogBorrowerIndex(HttpServletRequest req, HttpServletResponse resp) {
		return "fin/withdrawLogBorrower/index";
	}
	
	@RequestMapping(value = "ajax")
	@RequiresPermissions("withdrawLog:ajax")
	@ResponseBody
	public Object showWithdrawLogPages(HttpServletRequest req, HttpServletResponse resp)	throws ServletRequestBindingException {
		Map<String, Object> map = Maps.newHashMap();
		getFilterMapFromRequest(req, map);
		
		Page<WithdrawLog> pageRequest = new Page<WithdrawLog>();
		//getPageInfoFromRequest(req, pageRequest);
		map = getPageInfoFromRequest(req, pageRequest);
		Page<WithdrawLog> pager = withdrawLogService.findByPage(pageRequest, map);
		return pager;
	}
	
}