package com.yourong.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yourong.common.domain.ResultDO;
import com.yourong.core.fin.model.biz.QuadrupleGiftCount;
import com.yourong.core.fin.model.query.PopularityInOutLogQuery;
import com.yourong.core.ic.model.biz.ProjectForFront;
import com.yourong.core.mc.model.biz.ActivityForFirstInvest;
import com.yourong.core.mc.model.biz.ActivityLeadingSheep;
import com.yourong.web.service.ActivityLeadingSheepService;
import com.yourong.web.service.ArticleService;
import com.yourong.web.service.PopularityInOutLogService;
import com.yourong.web.service.ProjectService;

@Controller
@RequestMapping("landing")
public class LandingController {

	@Autowired
	private ProjectService projectService;

	@Autowired
	private ArticleService articleService;

	@Autowired
	private PopularityInOutLogService popularityInOutLogService;

	@Autowired
	private ActivityLeadingSheepService activityLeadingSheepService;


	/**
	 * 着落页1
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/earnings/register")
	public ModelAndView earningsLand(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		ResultDO<ProjectForFront> result = projectService.findLandProjectList();
		model.addObject("projectList", result.getResultList());
		model.setViewName("/landing/earningsLand");
		return model;
	}

	/**
	 * 着落页1_A
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/earningsA/register")
	public ModelAndView earningsALand(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		ResultDO<ProjectForFront> result = projectService.findLandProjectList();
		model.addObject("projectList", result.getResultList());
		model.setViewName("/landing/earningsLand_A");
		return model;
	}

	/**
	 * 着落页4
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/seasonFour/register")
	public ModelAndView seasonFourLand(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		model.addObject("landing_url", "/seasonFour/register");
		model.setViewName("/landing/seasonFour");
		return model;
	}

	/**
	 * 投房有喜着陆页
	 *
	 * @param req
	 * @param resp
	 * @return
	 */
	// @RequestMapping("/investHouse/register")
	// public ModelAndView investHouseAct(HttpServletRequest req,
	// HttpServletResponse resp) {
	// ModelAndView model = new ModelAndView();
	// model.setViewName("/landing/investHouseLand");
	// return model;
	// }

	/**
	 * 着落页通用链接
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/page/{earningsName:\\w+}")
	public ModelAndView earnings(HttpServletRequest req, @PathVariable("earningsName") String earningsName, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		ResultDO<ProjectForFront> result = projectService.findLandProjectList();
		model.addObject("projectList", result.getResultList());
		model.setViewName("/landing/" + earningsName);
		return model;
	}

	/**
	 * @Description:平台五重礼每一种获取总次数和人气值总数
	 * @return
	 * @author: fuyili
	 * @time:2015年12月11日 下午4:59:56
	 */
	@RequestMapping(value = "/quadrupleGift/count", method = RequestMethod.POST)
	@ResponseBody
	public Object getQuintupleCount() {
		QuadrupleGiftCount quadrupleGiftCount = popularityInOutLogService.getQuintupleGiftCount();
		return quadrupleGiftCount;
	}

	/**
	 * @Description:五重礼获取记录
	 * @return
	 * @author: fuyili
	 * @time:2015年12月11日 下午4:59:56
	 */
	@RequestMapping(value = "/quintupleGift/gain/list", method = RequestMethod.POST)
	@ResponseBody
	public Object getQuintupleGiftGainList() {
		PopularityInOutLogQuery query = new PopularityInOutLogQuery();
		query.setLimitSize(10);
		List<ActivityForFirstInvest> quintupleGiftGainList = popularityInOutLogService.selectQuintupleGiftMemberList(query);
		return quintupleGiftGainList;
	}

	/**
	 * @Description:获取支持一锤定音的项目
	 * @return
	 * @author: fuyili
	 * @time:2015年12月11日 下午4:59:56
	 */
	@RequestMapping(value = "/quintupleGift/lastProjects", method = RequestMethod.POST)
	@ResponseBody
	public Object getQuintupleGiftLastProjects() {
		ActivityLeadingSheep project = activityLeadingSheepService.getLastProjectsForQuintupleGift();
		return project;
	}
}
