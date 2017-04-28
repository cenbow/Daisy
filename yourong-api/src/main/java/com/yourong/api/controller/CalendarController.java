package com.yourong.api.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.api.service.CalendarService;

/**
 * 会员活动控制类
 * 
 * @author wangyanji
 *
 */
@Controller
@RequestMapping(value = "security/calendar")
public class CalendarController extends BaseController {

	@Autowired
	private CalendarService calendarService;

	/**
	 * 
	 * @Description:账户中心日历
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月18日 下午1:06:44
	 */
	@RequestMapping(value = "init", method = RequestMethod.POST, headers = { "Accept-Version=1.0.0" })
	@ResponseBody
	public Object initCalendar(HttpServletRequest req, HttpServletResponse resp) {
		Long memberId = getMemberID(req);
		return calendarService.initCalendar(memberId);
	}
	
	/**
	 * 
	 * @Description:账户中心日历适配1.3.0app，新增新版本接口
	 * @param req
	 * @param resp
	 * @return
	 * @author: zhanghao
	 * @time:2015年12月18日 下午1:06:44
	 */
	@RequestMapping(value = "init", method = RequestMethod.POST, headers = { "Accept-Version=1.4.0" })
	@ResponseBody
	public Object initCalendarNew(HttpServletRequest req, HttpServletResponse resp) {
		Long memberId = getMemberID(req);
		return calendarService.initCalendarNew(memberId);
	}
	
	/**
	 * 
	 * @Description:账户中心日历1.5.0 当日回款信息方法重构
	 * @param req
	 * @param resp
	 * @return
	 * @author: zhanghao
	 * @time:2015年12月18日 下午1:06:44
	 */
	@RequestMapping(value = "init", method = RequestMethod.POST, headers = { "Accept-Version=1.5.0" })
	@ResponseBody
	public Object initCalendarForNew(HttpServletRequest req, HttpServletResponse resp) {
		Long memberId = getMemberID(req);
		return calendarService.initCalendarForNew(memberId);
	}

	/**
	 * 
	 * @Description:日历获取单日数据
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2016年4月6日 下午3:38:16
	 */
	@RequestMapping(value = "getDayData", method = RequestMethod.POST, headers = { "Accept-Version=1.0.0" })
	@ResponseBody
	public Object getCalendarByDate(HttpServletRequest req, HttpServletResponse resp) {
		Long memberId = getMemberID(req);
		String dateStr = ServletRequestUtils.getStringParameter(req, "date", null);
		return calendarService.getCalendarByDate(memberId, dateStr);
	}
	
	/**
	 * 
	 * @Description:日历获取单日数据
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2016年4月6日 下午3:38:16
	 */
	@RequestMapping(value = "getDayData", method = RequestMethod.POST, headers = { "Accept-Version=1.5.0" })
	@ResponseBody
	public Object getCalendarByDateNew(HttpServletRequest req, HttpServletResponse resp) {
		Long memberId = getMemberID(req);
		String dateStr = ServletRequestUtils.getStringParameter(req, "date", null);
		return calendarService.getCalendarByDateNew(memberId, dateStr);
	}
	
	/**
	 * 
	 * @Description:日历获取单日数据，当日可还款的项目
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji 
	 * @time:2016年4月6日 下午3:38:16
	 */
	@RequestMapping(value = "getProjectByEndData", method = RequestMethod.POST, headers = { "Accept-Version=1.4.0" })
	@ResponseBody
	public Object getProjectByEndData(HttpServletRequest req, HttpServletResponse resp) {
		String dateStr = ServletRequestUtils.getStringParameter(req, "date", null);
		return calendarService.getProjectByEndData(dateStr);
	}
	
	
}
