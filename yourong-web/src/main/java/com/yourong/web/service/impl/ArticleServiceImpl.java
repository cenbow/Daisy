package com.yourong.web.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.cms.manager.CmsArticleManager;
import com.yourong.core.cms.model.CmsArticle;
import com.yourong.core.cms.model.query.CmsArticleQuery;
import com.yourong.web.service.ArticleService;

@Service
public class ArticleServiceImpl implements ArticleService {
	private static final Logger logger = LoggerFactory.getLogger(ArticleServiceImpl.class);
	@Autowired
	private CmsArticleManager cmsArticleManager;

	@Override
	public Page<CmsArticle> findArticlesByPage(CmsArticleQuery cmsArticleQuery) {
		try {
			return cmsArticleManager.findByFrontPage(cmsArticleQuery);
		} catch (ManagerException e) {
			logger.error("前台分页获取文章是失败,cmsArticleQuery = " + cmsArticleQuery,e);
		}
		return null;
	}

	/**
	 * 首页公告滚动内容
	 */
	@Override
	public List<CmsArticle> selectHomeNotice(Map<String, Object> map) {
		try {
			return cmsArticleManager.selectHomeNotice(map);
		} catch (Exception e) {
			logger.error("首页公告滚动内容获取失败,map = " + map,e);
		}
		return null;
	}

	@Override
	public CmsArticle selectNewDetailById(Long articleId) {
		try {
			Map<String, Object> map = Maps.newHashMap();
			map.put("categoryId", 2);//媒体报道
			map.put("id", articleId);
			return cmsArticleManager.selectArticleByIdAndCategoryId(map);
		} catch (Exception e) {
			logger.error("获取媒体报道详情失败,articleId = " + articleId,e);
		}
		return null;
	}

	/*新闻 后一条*/
	@Override
	public Long selectNextNews(Long articleId) {
		try {
			Map<String, Object> map = Maps.newHashMap();
			map.put("categoryId", 2);//媒体报道
			map.put("id", articleId);
			return cmsArticleManager.selectNextArticle(map);
		} catch (Exception e) {
			logger.error("获取媒体报道后一条,articleId = " + articleId,e);
		}
		return null;
	}

	/* 新闻 前一条 */
	@Override
	public Long selectPreNews(Long articleId) {
		try {
			Map<String, Object> map = Maps.newHashMap();
			map.put("categoryId", 2);//媒体报道
			map.put("id", articleId);
			return cmsArticleManager.selectPreArticle(map);
		} catch (Exception e) {
			logger.error("获取媒体报道前一条,articleId = " + articleId,e);
		}
		return null;
	}

	@Override
	public int selectArticles(Integer newsId) {
		try{
			return cmsArticleManager.selectArticles(newsId);
		}catch (Exception e) {
			logger.error("获取全部公告失败",e);
		}
		return 0;
	}
	

}
