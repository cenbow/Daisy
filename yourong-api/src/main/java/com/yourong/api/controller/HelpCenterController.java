package com.yourong.api.controller;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yourong.api.service.HelpCenterService;
import com.yourong.core.mc.model.HelpCenterQuestionShow;

@Controller
@RequestMapping(value = "mstation/post")
public class HelpCenterController extends BaseController {
	
	@Autowired
	private HelpCenterService helpCenterService;


	/**
	 * 热门问题接口
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "helpHomePage")
	@ResponseBody
	public ModelAndView showHotQuestionIndex(HttpServletRequest req,
			HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		List<HelpCenterQuestionShow> hotQuestionList = helpCenterService.getHotFromRedis();
		model.addObject("hotQuestionList",hotQuestionList);
		model.setViewName("/mobile/post/helpHomePage");
		return model;
	}
	
	/**
	 * 常见问题接口
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "helpQuestionTypes")
	@ResponseBody
	public ModelAndView showCommonQuestionIndex(HttpServletRequest req,HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		HashMap<String,List<HelpCenterQuestionShow>> commonQuestionMap = helpCenterService.getCommonFromRedis();
		model.addObject("commonQuestionMap",commonQuestionMap);
		model.setViewName("/mobile/post/helpQuestionTypes");
		return model;
	}
	
	/**
	 * 问题答案接口
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "helpAnswer")
	@ResponseBody
	public ModelAndView showQuesAnswerIndex(HttpServletRequest req,HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		List<HelpCenterQuestionShow> quesAnswerList = helpCenterService.getAnswerFromRedis();
		model.addObject("quesAnswerList",quesAnswerList);
		model.setViewName("/mobile/post/helpAnswer");
		return model;
	}
	
}
