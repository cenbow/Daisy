package com.yourong.backend.msc.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.backend.BaseController;
import com.yourong.backend.msc.service.FeedbackService;
import com.yourong.common.pageable.Page;
import com.yourong.core.msg.model.Feedback;
import com.yourong.core.msg.model.biz.FeedbackForMember;

@Controller
@RequestMapping("feedback")
public class FeedbackController extends BaseController{

	@Autowired
	private FeedbackService feedbackService;
	
	@RequestMapping(value = "index")
	public String feedback(HttpServletRequest req, HttpServletResponse resp){
		return "/msg/feedback/index";
	}
	
	@RequestMapping(value = "ajax")
	@ResponseBody
	public Object queryFeedbackList(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException{
		Page<FeedbackForMember> pageRequest = new Page<FeedbackForMember>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		return feedbackService.queryFeedbackByPage(pageRequest, map);
	}
	
	
	@RequestMapping(value = "queryFeedback")
	@ResponseBody
	public Object selectByPrimaryKey(@RequestParam("id") Long id,HttpServletRequest req, HttpServletResponse resp) 
			throws ServletRequestBindingException{
		return feedbackService.selectByPrimaryKey(id);
	}
	

	@RequestMapping(value = "replyFeedback")
	@ResponseBody
	public Object saveFeedback(@ModelAttribute Feedback feedback,HttpServletRequest req, HttpServletResponse resp) 
			throws ServletRequestBindingException{
		return feedbackService.saveFeedback(feedback);
	}
	
	
}
