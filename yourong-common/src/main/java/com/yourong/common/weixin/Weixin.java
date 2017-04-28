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
public class Weixin {
	private String name;
	private List<Weixin> sub_button;
	private List<Weixin> children;
	
	
	private String type;
	private String key;
	private String url;
	private String id;
	private String pid;
	private String text;
	private String status;
	private Integer sort;
	
	
	public List<Weixin> getSub_button() {
		return sub_button;
	}
	public void setSub_button(List<Weixin> sub_button) {
		this.sub_button = sub_button;
	}
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
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public List<Weixin> getChildren() {
		return children;
	}
	public void setChildren(List<Weixin> children) {
		this.children = children;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Weixin [id=");
		builder.append(id);
		builder.append(", pid=");
		builder.append(pid);
		builder.append(", name=");
		builder.append(name);
		builder.append(", type=");
		builder.append(type);
		builder.append(", key=");
		builder.append(key);
		builder.append("]");
		return builder.toString();
	}
}
