package com.yourong.common.weixin.aes;

import java.util.List;

/**
 * @Author:chaisen
 * @date:2015年9月25日下午4:12:45
 */
public class Weixin {
	private String type;
	private String name;
	private String key;
	private String url;
	private List<Weixin> sub_button;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public List<Weixin> getSub_button() {
		return sub_button;
	}
	public void setSub_button(List<Weixin> sub_button) {
		this.sub_button = sub_button;
	}
	
}
