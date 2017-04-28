package com.yourong.backend.ic.controller;

import com.google.common.collect.Maps;
import com.yourong.backend.BaseController;
import com.yourong.backend.ic.service.LeaseBonusDetailService;
import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.LeaseBonusDetail;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("leaseBonusDetail")
public class LeaseBonusDetailController extends BaseController {
	@Autowired
	private LeaseBonusDetailService leaseBonusDetailService;

	@RequestMapping(value = "index")
	@RequiresPermissions("leaseBonusDetail:index")
	public String showLeaseBonusDetailIndex(HttpServletRequest req, HttpServletResponse resp) {
		return "/leaseBonusDetail/index";
	}

	@RequestMapping(value = "ajax")
	@RequiresPermissions("leaseBonusDetail:ajax")
	@ResponseBody
	public Object showLeaseBonusDetailPages(HttpServletRequest req, HttpServletResponse resp)
			throws ServletRequestBindingException {
		Map<String, Object> map = Maps.newHashMap();
		getFilterMapFromRequest(req, map);
		Page<LeaseBonusDetail> pageRequest = new Page<LeaseBonusDetail>();
		getPageInfoFromRequest(req, pageRequest);
		Page<LeaseBonusDetail> pager = leaseBonusDetailService.findByPage(pageRequest, map);
		return pager;
	}

	@RequestMapping(value = "save")
	@RequiresPermissions("leaseBonusDetail:save")
	@ResponseBody
	public Object saveLeaseBonusDetail(@ModelAttribute LeaseBonusDetail leaseBonusDetail, HttpServletRequest req,
			HttpServletResponse resp) {
		int insertSelective;
		if (leaseBonusDetail.getId() != null) {
			insertSelective = leaseBonusDetailService.updateByPrimaryKeySelective(leaseBonusDetail);
		} else {
			insertSelective = leaseBonusDetailService.insert(leaseBonusDetail);
		}
		return insertSelective;
	}

	@RequestMapping(value = "show")
	@RequiresPermissions("leaseBonusDetail:show")
	@ResponseBody
	public Object showLeaseBonusDetail(HttpServletRequest req, HttpServletResponse resp) {
		long id = ServletRequestUtils.getLongParameter(req, "id", 0);
		LeaseBonusDetail leaseBonusDetail = leaseBonusDetailService.selectByPrimaryKey(id);
		return leaseBonusDetail;
	}

	@RequestMapping(value = "delete")
	@RequiresPermissions("leaseBonusDetail:delete")
	@ResponseBody
	public Object deleteLeaseBonusDetail(HttpServletRequest req, HttpServletResponse resp) {
		long[] id = ServletRequestUtils.getLongParameters(req, "id[]");
		leaseBonusDetailService.batchDelete(id);
		return "1";
	}
}