package com.yourong.api.dto;

import java.util.Date;

public class CmsArticleListDto {
	/** 编号 **/
	private Long id;

	/** 栏目编号 **/
	private Long categoryId;
	
	/** 标题 **/
	private String title;
	
	/** 创建时间 **/
	private Date createTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	
}
