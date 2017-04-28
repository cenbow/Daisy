/**
 * 
 */
package com.yourong.api.dto;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年10月18日上午11:31:08
 */
public class DynamicMenuDto {
	
	//菜单名称
	private String title;
	
	//菜单链接
	private String url;
	
	//菜单角标
	private int corner;

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the corner
	 */
	public int getCorner() {
		return corner;
	}

	/**
	 * @param corner the corner to set
	 */
	public void setCorner(int corner) {
		this.corner = corner;
	}
	
	
	
}
