/**
 * 
 */
package com.yourong.core.ic.model.biz;

import org.apache.commons.lang3.StringUtils;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * @author wanglei
 * 快投有奖中奖名单VO
 *
 */
public class LotteryRewardBiz extends AbstractBaseObject{
	
	//项目id
	private Long projectId;
	
	//项目名称
	private String projectName;
	
	//奖项等级
	private int chip;
	
	//奖品
	private String rewardInfo;
	
	//奖项是否领取
	private int rewardStatus;
	
	//中奖用户手机号码
	private String memberMobile;
	
	//中奖用户头像
	private String memberAvatars;
	
	
	/** 项目名 期数 **/
	private String prefixProjectName;
	
	

	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public int getChip() {
		return chip;
	}
	public void setChip(int chip) {
		this.chip = chip;
	}
	public String getRewardInfo() {
		return rewardInfo;
	}
	public void setRewardInfo(String rewardInfo) {
		this.rewardInfo = rewardInfo;
	}
	public int getRewardStatus() {
		return rewardStatus;
	}
	public void setRewardStatus(int rewardStatus) {
		this.rewardStatus = rewardStatus;
	}
	public String getMemberMobile() {
		return memberMobile;
	}
	public void setMemberMobile(String memberMobile) {
		this.memberMobile = memberMobile;
	}
	public String getMemberAvatars() {
		return memberAvatars;
	}
	public void setMemberAvatars(String memberAvatars) {
		this.memberAvatars = memberAvatars;
	}
	
	public String getPrefixProjectName() {
		if (StringUtils.isNoneBlank(projectName) && projectName.contains("期")) {
			return projectName.substring(0, projectName.indexOf("期") + 1);
		} else {
			return "";
		}
	}
	

}
