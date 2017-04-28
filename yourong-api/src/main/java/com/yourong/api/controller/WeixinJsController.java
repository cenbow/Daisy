package com.yourong.api.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.yourong.api.dto.ResultDTO;
import com.yourong.api.service.WeixinApiService;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.util.StringUtil;

/**
 * 
 * @desc 微信JS控制类
 * @author wangyanji 2015年12月24日下午6:18:28
 */
@Controller
@RequestMapping(value = "wechat/js")
public class WeixinJsController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(WeixinJsController.class);

	@Autowired
	private WeixinApiService weixinApiService;

	/**
	 * 
	 * @Description:微信分享页
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月24日 下午6:20:51
	 */
	@RequestMapping("/share")
	public ModelAndView downLoading(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		String url = ServletRequestUtils.getStringParameter(req, "shareUrl", null);
		if (StringUtil.isBlank(url)) {
			logger.error("跳转微信分享页失败shareUrl={}", url);
			model.setViewName("/weixin/activity/anniversaryTimeOut");
			return model;
		}
		Map<String, String> map=weixinApiService.getSign(url);
		if(!map.isEmpty()){
			String json=JSON.toJSONString(map);
			String weixinShare="wxCallback("+json+")";
			req.setAttribute("weixinShare", weixinShare);
		}
		//model.addAllObjects(weixinApiService.getSign(url));
		model.setViewName("/weixin/share");
		return model;
	}
}
