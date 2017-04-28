package com.yourong.web.dto;

import java.util.Comparator;

public class NoviceTaskDto{
	
	private int id;
	private String name;
	private boolean isJoin;
	private boolean isShow;
	private String url;
	private String code;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isJoin() {
		return isJoin;
	}
	public void setJoin(boolean isJoin) {
		this.isJoin = isJoin;
	}
	public boolean isShow() {
		return isShow;
	}
	public void setShow(boolean isShow) {
		this.isShow = isShow;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getBtnText(){
		if("绑定邮箱".equals(name) || "关注微信".equals(name)){
			return "绑定 送10点人气值";
		}else if("完善信息".equals(name)){
			return "完善 送10点人气值";
		}else if("首次投资".equals(name)){
			return "投资 送10点人气值";
		}
		return "体验  送10点人气值";
	}
	
	public boolean getIsEmail() {
		if("email".equals(code)){
			return true;
		}
		return false;
	}
	public boolean getIsInfo() {
		if("info".equals(code)){
			return true;
		}
		return false;
	}
	public boolean getIsProject() {
		if("project".equals(code)){
			return true;
		}
		return false;
	}
	public boolean getIsWeiXin() {
		if("weixin".equals(code)){
			return true;
		}
		return false;
	}
	public boolean getIsApp() {
		if("app".equals(code)){
			return true;
		}
		return false;
	}
}
