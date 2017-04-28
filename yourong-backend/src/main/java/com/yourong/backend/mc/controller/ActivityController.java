package com.yourong.backend.mc.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.backend.BaseController;
import com.yourong.backend.mc.service.ActivityService;
import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.DateUtils;
import com.yourong.core.mc.model.biz.ActivityBiz;

@Controller
@RequestMapping("activity")
public class ActivityController extends BaseController {

	@Autowired
	ActivityService activityService;

	@RequestMapping(value = "index")
	@RequiresPermissions("activity:index")
	public String showCouponTemplateIndex(HttpServletRequest req, HttpServletResponse resp) {
		return "/mc/activity/index";
	}

	/**
	 * 
	 * @Description:定制活动管理
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 * @author: wangyanji
	 * @time:2015年12月8日 下午1:49:58
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "ajax")
	@RequiresPermissions("activity:ajax")
	@ResponseBody
	public Object showCustomActivityPages(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		Page<ActivityBiz> pageRequest = new Page<ActivityBiz>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		Page<ActivityBiz> pager = activityService.showCustomActivityPages(pageRequest, map);
		return pager;
	}

	/**
	 * 
	 * @Description:编辑规则
	 * @param activityBiz
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月8日 下午5:30:23
	 */
	@RequestMapping(value = "saveRule")
	@RequiresPermissions("activity:edit")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "定制活动管理", desc = "编辑规则")
	public Object saveRule(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		Long id = ServletRequestUtils.getLongParameter(req, "id");
		String startTime = ServletRequestUtils.getStringParameter(req, "startTime");
		String endTime = ServletRequestUtils.getStringParameter(req, "endTime");
		String rule = ServletRequestUtils.getStringParameter(req, "rule");
		ActivityBiz biz = new ActivityBiz();
		biz.setId(id);
		biz.setStartTime(DateUtils.getDateTimeFromString(startTime));
		biz.setEndTime(DateUtils.getDateTimeFromString(endTime));
		biz.setRuleParameterJson(rule);
		return activityService.saveRule(biz);
	}
}
