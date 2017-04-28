/**
 * 
 */
package com.yourong.api.dto;

import com.yourong.common.constant.Config;

/**
 * @author wanglei
 *
 */
public class IconDto {

	/** 主键 **/
	private Long id;

	/** icon标题 **/
	private String name;

	/** icon图片(jpg、png) **/
	private String image;
	
	/** 链接 **/
	private String href;
	
	
	

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
	
	
	
	
	
	
}
