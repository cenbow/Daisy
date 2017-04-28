package com.yourong.backend.mc.controller;

import java.util.Map;

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

import com.yourong.backend.BaseController;
import com.yourong.backend.mc.service.ActivityHistoryService;
import com.yourong.backend.mc.service.ActivityService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.mc.model.biz.ActivityBiz;
import com.yourong.core.mc.model.biz.ActivityHistoryBiz;
import com.yourong.core.mc.model.biz.ActivityHistoryDetail;
import com.yourong.core.sales.SPParameter;
import com.yourong.core.sales.engine.SPEngine;

@Controller
@RequestMapping("salesPromotion")
public class SalesPromotionController extends BaseController{
	@Autowired
	ActivityService activityService;
	@Autowired
	ActivityHistoryService activityHistoryService;
	
	@RequestMapping(value = "index")
	@RequiresPermissions("sales:index")
	public String showCouponTemplateIndex(HttpServletRequest req,
			HttpServletResponse resp) {
		return "/mc/sales/add";
	}
	
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "add")
	@ResponseBody
	public ResultDO addActivity(@ModelAttribute("activityDto")ActivityBiz activityDto, HttpServletRequest req, HttpServletResponse resp){
		ResultDO bo = null;
		try {
			bo = activityService.addActivity(activityDto);
		} catch (ManagerException e) {
			e.printStackTrace();
		}
		return bo;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "update")
	@ResponseBody
	public ResultDO updateActivity(@ModelAttribute("activityDto")ActivityBiz activityDto, HttpServletRequest req, HttpServletResponse resp){
		ResultDO bo = null;
		try {
			bo = activityService.updateActivity(activityDto);
		} catch (ManagerException e) {
			e.printStackTrace();
		}
		return bo;
	}
	
	
	
	@RequestMapping(value = "ajax")
	@ResponseBody
	public Page<Activity> showActivityPages(HttpServletRequest req,
			  HttpServletResponse resp) throws ServletRequestBindingException {
		Page<Activity> pageRequest = new Page<Activity>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		Page<Activity> page = activityService.findByPage(pageRequest, map);
		return page;
	}
	
	
	@RequestMapping(value = "history")
	@ResponseBody
	public Page<ActivityHistoryBiz> showActivityHistoryPages(HttpServletRequest req,
			  HttpServletResponse resp) throws ServletRequestBindingException {
		Page<ActivityHistoryBiz> pageRequest = new Page<ActivityHistoryBiz>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		Page<ActivityHistoryBiz> page = activityHistoryService.findByPage(pageRequest, map);
		return page;
	}
	
	
	@RequestMapping(value = "del")
	@ResponseBody
	public ResultDO deleteActivity(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException{
		ResultDO bo = null;
		try {
			Long activityId = ServletRequestUtils.getLongParameter(req, "activityId");
			bo = activityService.deleteActivityById(activityId);
		} catch (ManagerException e) {
			e.printStackTrace();
		}
		return bo;
	}
	
	@RequestMapping(value = "review")
	@ResponseBody
	public ResultDO reviewActivity(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException{
		ResultDO bo = null;
		try {
			Long activityId = ServletRequestUtils.getLongParameter(req, "activityId");
			bo = activityService.reviewActivityById(activityId, getCurrentLoginUserInfo().getId());
		} catch (ManagerException e) {
			e.printStackTrace();
		}
		return bo;
	}
	
	@RequestMapping(value = "submittedForReview")
	@ResponseBody
	public ResultDO submittedForReview(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException{
		Long activityId = ServletRequestUtils.getLongParameter(req, "activityId");
		ResultDO bo = activityService.submittedForReview(activityId);
		return bo;
	}
	
	
	
	@RequestMapping(value = "test")
	@ResponseBody
	public ResultDO testSales(HttpServletRequest req, HttpServletResponse resp){
		SPParameter sp = new SPParameter();
		sp.setMemberId(110800000200L);
		SPEngine.getSPEngineInstance().run(sp);
		return null;
	}
	
	@RequestMapping(value = "select")
	@ResponseBody
	public ActivityBiz selectActivityById(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException{
		Long activityId = ServletRequestUtils.getLongParameter(req, "activityId");
		ActivityBiz biz = activityService.findActivityById(activityId);
		return biz;
	}
	
	@RequestMapping(value = "historyDetail")
	@ResponseBody
	public Page<ActivityHistoryDetail> activityHistoryDetail(HttpServletRequest req,
			HttpServletResponse resp) throws ServletRequestBindingException {
		Page<ActivityHistoryDetail> pageRequest = new Page<ActivityHistoryDetail>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		Page<ActivityHistoryDetail> page = activityHistoryService.findActivityHistoryDetailPage(pageRequest, map);
		return page;
	}
}
