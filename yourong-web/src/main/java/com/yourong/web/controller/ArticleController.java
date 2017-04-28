package com.yourong.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yourong.common.domain.ResultDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.yourong.common.pageable.Page;
import com.yourong.common.util.StringUtil;
import com.yourong.core.cms.model.CmsArticle;
import com.yourong.core.cms.model.query.CmsArticleQuery;
import com.yourong.web.service.ArticleService;

/** 媒体报道、网站公告 */
@Controller
@RequestMapping("article")
public class ArticleController extends BaseController {
	@Autowired
	private ArticleService articleService;

	@RequestMapping(value = "news-{categoryId}-{currentPage}.html", method = RequestMethod.GET)
	public ModelAndView showArticlePage(@ModelAttribute("CmsArticleQuery") CmsArticleQuery cmsArticleQuery,
			HttpServletRequest req, HttpServletResponse resps) {
		ModelAndView mv = new ModelAndView();
		if(req.getParameter("newsId") != null){
			Integer newsId = Integer.parseInt(req.getParameter("newsId"));
			int cmsArticleFront = articleService.selectArticles(newsId);
			int currentPage = cmsArticleFront / cmsArticleQuery.getPageSize() + 1;
			cmsArticleQuery.setCurrentPage(currentPage);
		}
		Page<CmsArticle> cmsPage = articleService.findArticlesByPage(cmsArticleQuery);
		mv.setViewName("post/news");
		mv.addObject("newsPage", cmsPage);
		mv.addObject("query", cmsArticleQuery);
		return mv;
	}

	@RequestMapping(value = "news")
	public String news(){
		return "post/condition";
	}

	@ResponseBody
	@RequestMapping(value = "newsajax")
	public ResultDO<Page<CmsArticle>> ajaxArticlePage(CmsArticleQuery cmsArticleQuery,@RequestParam(value = "newsId",required = false) Integer newsId,
									HttpServletRequest req, HttpServletResponse resps) {
		ResultDO<Page<CmsArticle>> resultDO=new ResultDO<>();
		if(newsId != null){
			int cmsArticleFront = articleService.selectArticles(newsId);
			int currentPage = cmsArticleFront / cmsArticleQuery.getPageSize() + 1;
			cmsArticleQuery.setCurrentPage(currentPage);
		}
		Page<CmsArticle> cmsPage = articleService.findArticlesByPage(cmsArticleQuery);
		resultDO.setResult(cmsPage);
		return resultDO;
	}

	@RequestMapping(value = "detail-{articleId}.html", method = RequestMethod.GET)
	public String getNewsDetail(@PathVariable String articleId, HttpServletRequest req, HttpServletResponse resp) {
		Long id = 0L;
		if (StringUtil.isNotBlank(articleId) && StringUtil.isNumeric(articleId)) {
			id = Long.parseLong(articleId);
		}
		if (id <= 0) {
			return "/404";
		}
		CmsArticle article = articleService.selectNewDetailById(id);
		if (article == null) {
			return "/404";
		}
		
		Long nextArticleId = articleService.selectNextNews(id);
		Long preArticleId = articleService.selectPreNews(id);
		req.setAttribute("newsDetail", article);
		req.setAttribute("nextArticle", nextArticleId);
		req.setAttribute("preArticle", preArticleId);
		return "/post/newsDetail";
	}
	
	@RequestMapping(value="/news/detail")
	public String getNewsDetail(){
		return "/post/newsDetail";
	}
	
}
