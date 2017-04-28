package com.yourong.core.mc.model.biz;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.yourong.common.constant.Config;
import com.yourong.common.util.StringUtil;

public class ActivityProject implements Serializable {

	private String projectName;

	private Long projectId;

	private Integer nummber;

	private String thumbnail;

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Integer getNummber() {
		return nummber;
	}

	public void setNummber(Integer nummber) {
		this.nummber = nummber;
	}

	public String getThumbnail() {
		if(thumbnail != null){
			if(getProjectId() != null && getProjectId() >= 989800156L){
				return Config.ossPicUrl+StringUtil.getFilePath(thumbnail,"300");
			}else{
				return Config.ossPicUrl+thumbnail;
			}
		}
		return null;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	
}
