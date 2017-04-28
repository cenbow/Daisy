package com.yourong.api.dto;

import com.yourong.common.constant.Config;

public class BannerDto {
	
	/** 主键 **/
	private Long id;
	
	/** banner标题 **/
	private String name;

	/** banner图片(jpg、png) **/
	private String image;

	/** 链接 **/
	private String href;
	
	/** 是否可以分享 **/
	private Integer shareFlag;

	/** 分享标题 **/
	private String shareTitle;
	
	/** 分享标题 **/
	private String shareWord;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		if(image != null){
			return Config.ossPicUrl+image;
		}
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	/**
	 * @return the shareFlag
	 */
	public Integer getShareFlag() {
		return shareFlag;
	}

	/**
	 * @param shareFlag the shareFlag to set
	 */
	public void setShareFlag(Integer shareFlag) {
		this.shareFlag = shareFlag;
	}

	/**
	 * @return the shareTitle
	 */
	public String getShareTitle() {
		return shareTitle;
	}

	/**
	 * @param shareTitle the shareTitle to set
	 */
	public void setShareTitle(String shareTitle) {
		this.shareTitle = shareTitle;
	}

	/**
	 * @return the shareWord
	 */
	public String getShareWord() {
		return shareWord;
	}

	/**
	 * @param shareWord the shareWord to set
	 */
	public void setShareWord(String shareWord) {
		this.shareWord = shareWord;
	}
	
}
