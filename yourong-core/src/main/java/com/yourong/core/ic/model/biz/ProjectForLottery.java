package com.yourong.core.ic.model.biz;

import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.core.ic.model.LotteryRuleAmountNumber;
import com.yourong.core.ic.model.PrizeInPool;
import com.yourong.core.ic.model.PrizePool;
import com.yourong.core.mc.model.CouponTemplate;
import com.yourong.core.mc.model.biz.ActivityProject;

public class ProjectForLottery extends AbstractBaseObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9193339820515134532L;

	
	private List<PrizePool> prizePoolList;
	
	private List<LotteryRuleAmountNumber> ruleAmountList;
	
	private List<ProjectForFront> projectFrontList;
	
	private Long projectId;
	
	private String prizePool;
	
	private List<ProjectForReward> projectForReward;
	
	private String projectLotteryJson;
	
	private List<ActivityProject> listProjectLottery;
	
	private List<PrizeInPool> prizeInPoolList;
	
	private  List<ProjectForReward> listReward;
	
	private List<CouponTemplate> listTemplate;
	
	private String rewardHour;
	
	
	public List<CouponTemplate> getListTemplate() {
		return listTemplate;
	}

	public void setListTemplate(List<CouponTemplate> listTemplate) {
		this.listTemplate = listTemplate;
	}

	public List<ProjectForReward> getListReward() {
		return listReward;
	}

	public void setListReward(List<ProjectForReward> listReward) {
		this.listReward = listReward;
	}

	public List<PrizeInPool> getPrizeInPoolList() {
		return prizeInPoolList;
	}

	public void setPrizeInPoolList(List<PrizeInPool> prizeInPoolList) {
		this.prizeInPoolList = prizeInPoolList;
	}

	public List<ActivityProject> getListProjectLottery() {
		return listProjectLottery;
	}

	public void setListProjectLottery(List<ActivityProject> listProjectLottery) {
		this.listProjectLottery = listProjectLottery;
	}

	public String getProjectLotteryJson() {
		return projectLotteryJson;
	}

	public void setProjectLotteryJson(String projectLotteryJson) {
		this.projectLotteryJson = projectLotteryJson;
	}


	public List<ProjectForReward> getProjectForReward() {
		return projectForReward;
	}

	public void setProjectForReward(List<ProjectForReward> projectForReward) {
		this.projectForReward = projectForReward;
	}


	public List<ProjectForFront> getProjectFrontList() {
		return projectFrontList;
	}

	public void setProjectFrontList(List<ProjectForFront> projectFrontList) {
		this.projectFrontList = projectFrontList;
	}

	public List<PrizePool> getPrizePoolList() {
		return prizePoolList;
	}

	public void setPrizePoolList(List<PrizePool> prizePoolList) {
		this.prizePoolList = prizePoolList;
	}

	public List<LotteryRuleAmountNumber> getRuleAmountList() {
		return ruleAmountList;
	}

	public void setRuleAmountList(List<LotteryRuleAmountNumber> ruleAmountList) {
		this.ruleAmountList = ruleAmountList;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public String getPrizePool() {
		return prizePool;
	}

	public void setPrizePool(String prizePool) {
		this.prizePool = prizePool;
	}

	public String getRewardHour() {
		return rewardHour;
	}

	public void setRewardHour(String rewardHour) {
		this.rewardHour = rewardHour;
	}
	
}
