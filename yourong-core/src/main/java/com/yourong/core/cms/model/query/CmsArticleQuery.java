package com.yourong.core.cms.model.query;

import com.yourong.common.domain.BaseQueryParam;

/**文章的查询条件*/

public class CmsArticleQuery extends BaseQueryParam {

	private Long categoryId;

	/**
	 * 类别
	 */
	private Integer genre;

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public Integer getGenre() {
		return genre;
	}

	public void setGenre(Integer genre) {
		this.genre = genre;
	}
}
