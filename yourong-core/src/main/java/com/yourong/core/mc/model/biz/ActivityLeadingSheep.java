package com.yourong.core.mc.model.biz;

import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;

public class ActivityLeadingSheep extends AbstractBaseObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<ActivityLeadingSheepProject> activityLeadingSheepProjects;
	
	/**
	 * 是否存在可参与一羊领头(一鸣惊人、幸运女神、一锤定音)的项目
	 */
	private boolean isExist;

	public List<ActivityLeadingSheepProject> getActivityLeadingSheepProjects() {
		return activityLeadingSheepProjects;
	}

	public void setActivityLeadingSheepProjects(List<ActivityLeadingSheepProject> activityLeadingSheepProjects) {
		this.activityLeadingSheepProjects = activityLeadingSheepProjects;
	}

	public boolean isExist() {
		return isExist;
	}

	public void setExist(boolean isExist) {
		this.isExist = isExist;
	}
	

}
