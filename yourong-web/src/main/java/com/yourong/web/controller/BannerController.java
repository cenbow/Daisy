package com.yourong.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yourong.common.domain.BaseQueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.web.service.BannerService;
import org.springframework.web.servlet.ModelAndView;

/**
 * Banner
 * @author wangyanji
 *
 */
@Controller
@RequestMapping("banner")
public class BannerController {
	
	@Autowired
    private BannerService bannerService;

	/**
	 * 根据页面区域获取banner
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/showBanner", method = RequestMethod.POST)
	@ResponseBody
	public Object showBanner(HttpServletRequest req, HttpServletResponse resp) {
		int bannerType = ServletRequestUtils.getIntParameter(req, "bannerType", 0);
		int rowStart = ServletRequestUtils.getIntParameter(req, "rowStart", 0);
		int rowLength = ServletRequestUtils.getIntParameter(req, "rowLength", 1);
		String areaSign = ServletRequestUtils.getStringParameter(req, "areaSign", null);
		return bannerService.showBannerByArea(bannerType, areaSign, rowStart, rowLength);
	}
	
	/**
	 * 活动集合页
     *
     * @param req
     * @param resp
     * @return
	 */
    @RequestMapping("/activityList")
    public ModelAndView activityListAct(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/post/activityList");
        return model;
    }


	/**
	 * 活动集合页数据
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/activityListData", method = RequestMethod.POST)
	@ResponseBody
	public Object activityListData(HttpServletRequest req, HttpServletResponse resp,@ModelAttribute("baseQueryParam")BaseQueryParam baseQueryParam) {
		baseQueryParam.setPageSize(5);
		return bannerService.findActivityBannerByPage(baseQueryParam);
	}

}
