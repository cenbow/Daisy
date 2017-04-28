package com.yourong.api.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Optional;
import com.yourong.api.dto.CmsArticleListDto;
import com.yourong.api.dto.MemberSessionDto;
import com.yourong.api.dto.ResultDTO;
import com.yourong.api.service.ArticleService;
import com.yourong.api.service.FeedbackService;
import com.yourong.api.service.MemberService;
import com.yourong.common.domain.DynamicParamBuilder;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.common.util.StringUtil;
import com.yourong.common.util.AES;
import com.yourong.common.util.Collections3;
import com.yourong.core.cms.model.CmsArticle;
import com.yourong.core.cms.model.query.CmsArticleQuery;
import com.yourong.core.msg.model.Feedback;
import com.yourong.core.msg.model.query.FeedBackQuery;
import com.yourong.core.uc.model.Member;


@Controller
@RequestMapping("/article")
public class CmsArticleController extends BaseController{

	@Autowired
	private ArticleService articleService;
	
	
	@Autowired
	private MemberService memberService;
	
	 @Autowired
	    private FeedbackService feedbackService;
	/**
	 * 新闻公告
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "queryArticleList", method = RequestMethod.POST, headers = {"Accept-Version=1.0.2"})
    @ResponseBody
	public ResultDTO queryArticleList(HttpServletRequest req, HttpServletResponse resp) {
		ResultDTO result = new ResultDTO();
		int pageNo = ServletRequestUtils.getIntParameter(req, "pageNo", 1);
		Long categoryType = ServletRequestUtils.getLongParameter(req, "categoryType", 1L);
		CmsArticleQuery query = new CmsArticleQuery();
		query.setPageSize(20);
		query.setCurrentPage(pageNo);
		query.setCategoryId(categoryType);
		Page<CmsArticleListDto> data = articleService.findArticlesByPage(query);
		result.setResult(data);
		return result;
	}
	
	/**
	 * 新闻详情  1.0.2
	 * @param aid
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "detail-{aid}.html")
	public String findArticleInfo(@PathVariable String aid, HttpServletRequest req, HttpServletResponse resp){
		if(StringUtil.isNumeric(aid)){
			CmsArticle article = articleService.findArticle(Long.parseLong(aid));
			req.setAttribute("article", article);
		}
		return "/article/info";
	}
	
	/**
	 * 常见问题
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "question.html")
	public String question(HttpServletRequest req, HttpServletResponse resp){
		return "/article/question";
	}
	
	/**
	 * 名词解释
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "wiki.html")
	public String wiki(HttpServletRequest req, HttpServletResponse resp){
		return "/article/wiki";
	}
	
	/*@RequestMapping(value = "feedbackInfo.html")
	public String feedbackInfo(HttpServletRequest req, HttpServletResponse resp){

		return "/article/feedbackInfo";
	}*/
	
	@RequestMapping("feedbackInfo.html")
	public ModelAndView playOlympicActivity(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		ModelAndView model = new ModelAndView();
		Optional<Member> optOfMember = encryptionMemberFromApp(req, model);
		DynamicParamBuilder builder = new DynamicParamBuilder();
		if (optOfMember.isPresent()) {
			builder.setMemberId(optOfMember.get().getId());
			model.addObject("memberId", optOfMember.get().getId());
		}
		model.setViewName("/article/feedbackInfo");
		return model;
	}
	 /**
     * 
     * @Description:查询意见反馈
     * @param req
     * @param resp
     * @returns
     * @author: chaisen
     * @time:2016年9月23日 上午10:49:20
     */
    @RequestMapping(value = "queryFeedbackList", method = RequestMethod.POST, headers = {"Accept-Version=1.8.0"})
	@ResponseBody
	public ResultDTO queryMyTransactionList(HttpServletRequest req, HttpServletResponse resp) {
		int pageNo = ServletRequestUtils.getIntParameter(req, "pageNo", 1);
		Long memberId = ServletRequestUtils.getLongParameter(req, "memberId", 0L);
		ResultDTO resultDto = new ResultDTO();
		FeedBackQuery query = new FeedBackQuery();
		query.setMemberId(memberId);
		query.setPageSize(20); 
		query.setCurrentPage(pageNo);
		Page<Feedback> pager = feedbackService.queryFeedbackList(query);
		resultDto.setResult(pager);
		return resultDto;
	}
	
	private Optional<Member> encryptionMemberFromApp(HttpServletRequest req, ModelAndView model) {
		try {
			String loginSource = req.getHeader("loginSource");
			if (StringUtil.isBlank(loginSource)) {
				loginSource = "3";
				model.addObject("loginSource", loginSource);
				MemberSessionDto memberDto = getMember();
				if (memberDto == null) {
					return Optional.fromNullable(null);
				}
				Long memberId = memberDto.getId();
				Member member = memberService.selectByPrimaryKey(memberId);
				return Optional.fromNullable(member);
			} else {
				model.addObject("loginSource", loginSource);
			}
			String isNeedYRWtoken = req.getParameter("isNeedYRWtoken");
			if (StringUtil.isBlank(isNeedYRWtoken)) {
				return Optional.fromNullable(null);
			}
			String encryptionId = req.getParameter("encryptionId");
			if (StringUtil.isBlank(encryptionId)) {
				return Optional.fromNullable(null);
			}
			List<String> encryptionCode = AES.getInstance().tokenDecrypt(encryptionId);
			if (Collections3.isEmpty(encryptionCode)) {
				return Optional.fromNullable(null);
			} else {
				Long memberId = Long.valueOf(encryptionCode.get(0));
				Member member = memberService.selectByPrimaryKey(memberId);
				if (member != null) {
					model.addObject("memberId", memberId);
				}
				return Optional.fromNullable(member);
			}
		} catch (Exception e) {
			return Optional.fromNullable(null);
		}
	}
	
}
