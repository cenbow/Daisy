/**
 * 
 */
package com.yourong.api.dto;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年10月26日下午5:50:28
 */
public class Icon {
	
	private String name;
	
	/** 图片(jpg、png) **/
	private String image;
	
	/** 链接 **/
	private String href;
	
	private Integer jumpFlag;
	
	private String remark;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the image
	 */
	public String getImage() {
		return image;
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(String image) {
		this.image = image;
	}

	/**
	 * @return the href
	 */
	public String getHref() {
		return href;
	}

	/**
	 * @param href the href to set
	 */
	public void setHref(String href) {
		this.href = href;
	}

	/**
	 * @return the jumpFlag
	 */
	public Integer getJumpFlag() {
		return jumpFlag;
	}

	/**
	 * @param jumpFlag the jumpFlag to set
	 */
	public void setJumpFlag(Integer jumpFlag) {
		this.jumpFlag = jumpFlag;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
