package com.yourong.api.service;

import java.util.List;
import java.util.Map;

import com.yourong.api.dto.CmsArticleListDto;
import com.yourong.common.pageable.Page;
import com.yourong.core.cms.model.CmsArticle;
import com.yourong.core.cms.model.query.CmsArticleQuery;

public interface ArticleService {

	public Page<CmsArticleListDto> findArticlesByPage(CmsArticleQuery cmsArticleQuery);

	public List<CmsArticle> selectHomeNotice(Map<String, Object> map);

	public CmsArticle selectNewDetailById(Long articleId);

	/* 新闻 后一条 */
	public Long selectNextNews(Long articleId);

	/* 新闻 前一条 */
	public Long selectPreNews(Long articleId);
	
	/*弹框公告*/
	public CmsArticle selectNoticeArticle();
	
	/**
	 * 查看新闻详情
	 * @param articleId
	 * @return
	 */
	public CmsArticle findArticle(Long articleId);

}
