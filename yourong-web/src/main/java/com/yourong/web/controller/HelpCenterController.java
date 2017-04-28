package com.yourong.web.controller;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.MemberLogEnum;
import com.yourong.core.mc.model.HelpCenterQuestionShow;
import com.yourong.web.service.HelpCenterService;

@Controller
@RequestMapping("helpCenter")
public class HelpCenterController extends BaseController {
	
	@Autowired
	private HelpCenterService helpCenterService;

	/**
	 * 热门问题接口
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "helpHomePage", method = RequestMethod.GET)
	@ResponseBody
	public ResultDO<List<HelpCenterQuestionShow>> showHotQuestionIndex(HttpServletRequest req,
			HttpServletResponse resp) {
		ResultDO<List<HelpCenterQuestionShow>> resultDO = new ResultDO<List<HelpCenterQuestionShow>>();
		List<HelpCenterQuestionShow> hotQuestionList = helpCenterService.getHotFromRedis();
		resultDO.setResult(hotQuestionList);
		return resultDO;
	}
	
	/**
	 * 常见问题接口
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "helpQuestionTypes")
	@ResponseBody
	public ResultDO<HashMap<String,List<HelpCenterQuestionShow>>> showCommonQuestionIndex(HttpServletRequest req,
			HttpServletResponse resp) {
		ResultDO<HashMap<String,List<HelpCenterQuestionShow>>> resultDO = new ResultDO<>();
		HashMap<String,List<HelpCenterQuestionShow>> commonQuestionMap = helpCenterService.getCommonFromRedis();
		resultDO.setResult(commonQuestionMap);
		return resultDO;
	}
	
	/**
	 * 问题答案接口
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "newComer")
	@ResponseBody
	public ResultDO<List<HelpCenterQuestionShow>> showQuesAnswerIndex(HttpServletRequest req,
			HttpServletResponse resp) {
		ResultDO<List<HelpCenterQuestionShow>> resultDO = new ResultDO<List<HelpCenterQuestionShow>>();
		List<HelpCenterQuestionShow> quesAnswerList = helpCenterService.getNewComerFromRedis();
		resultDO.setResult(quesAnswerList);
		return resultDO;
	}
	
	/**
	 * 帮助中心用户行为记录
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "helpCenterBehavior",method = RequestMethod.POST)
	@ResponseBody
	public void helpCenterBehavior(HttpServletRequest req, HttpServletResponse resp) {
		String sourceId = ServletRequestUtils.getStringParameter(req,
				"sourceId", "");
		String anchor = ServletRequestUtils.getStringParameter(req, "anchor",
				"");
		String remarks = ServletRequestUtils.getStringParameter(req, "remarks",
				"");
		helpCenterService.memberBehaviorLogInsert((long)1, sourceId,
				MemberLogEnum.MEMBER_BEHAVIOR_TYPE_WEB_HELPCENTER.getType(),
				MemberLogEnum.MEMBER_BEHAVIOR_WEB.getType(), null,
				null, anchor, remarks);
	}

}
