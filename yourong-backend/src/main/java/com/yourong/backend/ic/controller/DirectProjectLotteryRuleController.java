package com.yourong.backend.ic.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.backend.ic.service.DirectProjectLotteryRuleService;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.DirectLotteryRuleForBackend;
import com.yourong.core.ic.model.DirectProjectBiz;


@Controller
@RequestMapping("directProjectLotteryRule")
public class DirectProjectLotteryRuleController {

	
	@Autowired
	private DirectProjectLotteryRuleService directProjectLotteryRuleService;
	
	/**
	 * 活动规则保存页面
	 */
	@RequestMapping(value = "index")
	@RequiresPermissions("directProjectLotteryRule:index")
	public String showLotteryRuleIndex(HttpServletRequest req, HttpServletResponse resp) {
		return "/ic/lotteryRule/index";
	}
	
	/**
	 * 活动规则保存
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "save")
	@RequiresPermissions("directProjectLotteryRule:save")
	@ResponseBody
	public Object showLotteryRule(@ModelAttribute("dir") DirectLotteryRuleForBackend dir,HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		return directProjectLotteryRuleService.save(dir);
	}
	
	
	/**
	 * 活动规则初始化
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "ajax")
	@RequiresPermissions("directProjectLotteryRule:ajax")
	@ResponseBody
	public Object ajaxInit(@ModelAttribute("dir") DirectLotteryRuleForBackend dir,HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		return directProjectLotteryRuleService.ajaxInit(dir);
	}
}
