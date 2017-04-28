/**
 * 
 */
package com.yourong.common.weixin;

import java.util.List;


/**
 * @desc TODO
 * @author chaisen
 * 2015年12月1日上午9:40:34
 */
public class WeixinMenu {
	
	private String name;
	private List<WeixinMenu> sub_button;
	
	private String type;
	private String key;
	private String url;
	
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<WeixinMenu> getSub_button() {
		return sub_button;
	}

	public void setSub_button(List<WeixinMenu> sub_button) {
		this.sub_button = sub_button;
	}

	

	
	
}
