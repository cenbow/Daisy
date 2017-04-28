package com.yourong.core.mc.model.biz;


import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * 问卷调查notify实体
 * 
 * @author fuyili
 *
 *         创建时间:2015年5月11日下午3:11:16
 */
public class ActivityQuestion extends AbstractBaseObject {

	private static final long serialVersionUID = 1L;

	private String user;

	private String proj_id;

	private String time;

	private Integer status;

	private String md5;

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}


	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getProj_id() {
		return proj_id;
	}

	public void setProj_id(String proj_id) {
		this.proj_id = proj_id;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
