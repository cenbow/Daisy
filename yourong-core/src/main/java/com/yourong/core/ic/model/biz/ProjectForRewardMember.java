package com.yourong.core.ic.model.biz;

import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.util.StringUtil;

public class ProjectForRewardMember extends AbstractBaseObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9193339820515134532L;

	private String avatars;
	
	private String username;

	private String reward;


	private Long memberId;
	
	private Long mobile;

	
	private List<ProjectForLevel> projectForLevelDetail;
	
	

	public Long getMobile() {
		return mobile;
	}


	public void setMobile(Long mobile) {
		this.mobile = mobile;
	}


	public List<ProjectForLevel> getProjectForLevelDetail() {
		return projectForLevelDetail;
	}


	public void setProjectForLevelDetail(List<ProjectForLevel> projectForLevelDetail) {
		this.projectForLevelDetail = projectForLevelDetail;
	}


	public String getAvatars() {
		return avatars;
	}


	public void setAvatars(String avatars) {
		this.avatars = avatars;
	}


	public String getUsername() {
		return StringUtil.maskUserNameOrMobile(username, mobile);
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getReward() {
		return reward;
	}


	public void setReward(String reward) {
		this.reward = reward;
	}


	public Long getMemberId() {
		return memberId;
	}


	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}
    
    


	
	
	
}
