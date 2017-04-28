package com.yourong.api.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.yourong.api.dto.CmsArticleListDto;
import com.yourong.api.service.ArticleService;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.Collections3;
import com.yourong.core.cms.manager.CmsArticleManager;
import com.yourong.core.cms.model.CmsArticle;
import com.yourong.core.cms.model.query.CmsArticleQuery;

@Service
public class ArticleServiceImpl implements ArticleService {
	private static final Logger logger = LoggerFactory.getLogger(ArticleServiceImpl.class);
	@Autowired
	private CmsArticleManager cmsArticleManager;

	@Override
	public Page<CmsArticleListDto> findArticlesByPage(CmsArticleQuery cmsArticleQuery) {
		 Page<CmsArticleListDto> pageList = new Page<CmsArticleListDto>();
		try {
			Page<CmsArticle> cmsArticlePage =  cmsArticleManager.findByFrontPage(cmsArticleQuery);
			if(cmsArticlePage != null){
				List<CmsArticleListDto> data = BeanCopyUtil.mapList(cmsArticlePage.getData(), CmsArticleListDto.class);
				pageList.setData(data);
				pageList.setiDisplayLength(cmsArticlePage.getiDisplayLength());
				pageList.setiDisplayStart(cmsArticlePage.getiDisplayStart());
				pageList.setiTotalRecords(cmsArticlePage.getiTotalRecords());
				pageList.setPageNo(cmsArticlePage.getPageNo());
			}
		} catch (ManagerException e) {
			logger.error("前台分页获取文章是失败,cmsArticleQuery = " + cmsArticleQuery,e);
		}
		return pageList;
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
	/**
	 * 首页弹框
	 */
	@Override
	public CmsArticle selectNoticeArticle() {
		try {
			return cmsArticleManager.selectNoticeArticle();
		} catch (Exception e) {
			logger.error("app弹框公告 ",e);
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
	public CmsArticle findArticle(Long articleId) {
		try {
			return cmsArticleManager.selectByPrimaryKey(articleId);
		} catch (ManagerException e) {
			logger.error("查看新闻详情异常,articleId = " + articleId,e);
		}
		return null;
	}
	

}
