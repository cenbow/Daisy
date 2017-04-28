package com.yourong.core.ic.model.biz;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.core.ic.model.LotteryRuleAmountNumber;
import com.yourong.core.ic.model.PrizeInPool;
import com.yourong.core.ic.model.PrizePool;

public class ProjectForDirectLottery extends AbstractBaseObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9193339820515134532L;

	private List<PrizeInPool> prizeInPoolList;
	
	//不同天数下奖池系数变化
	private List<PrizePool> prizePoolList;
	
	private List<LotteryRuleAmountNumber> ruleAmountList;
	
	private List<ProjectForFront> projectFrontList;
	
	
	private List<ProjectForReward> projectForReward;
	
	private String projectLotteryJson;
	
	private Integer totalDays;
	
	private BigDecimal ratioAmount;
	
	private Integer status;
	
	private Date saleComplatedTime;
	
	private List<ProjectForLevel> projectForLevel;//各等级奖项
	
	private List<ProjectForRewardMember> projectRwardMember;
	
	private boolean hasReward;//没有超出奖励期限
	
	/**
	 * 
	 */
	private List<ProjectForRewardDetail> listRewardDetail;
	
	
	private Date endDate;
	
	private String proportion; //履约中最后一天奖池系数百分比
	
	private Integer prizeAmount;
	
	private Integer day;
	
	private String maxReward;//最高奖金
	
	private boolean isQuick2Project; //是否快投二期项目
	
	
	
	
	
	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	public Integer getPrizeAmount() {
		return prizeAmount;
	}

	public void setPrizeAmount(Integer prizeAmount) {
		this.prizeAmount = prizeAmount;
	}

	public String getProportion() {
		return proportion;
	}

	public void setProportion(String proportion) {
		this.proportion = proportion;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public List<ProjectForRewardDetail> getListRewardDetail() {
		return listRewardDetail;
	}

	public void setListRewardDetail(List<ProjectForRewardDetail> listRewardDetail) {
		this.listRewardDetail = listRewardDetail;
	}

	public boolean isHasReward() {
		return hasReward;
	}

	public void setHasReward(boolean hasReward) {
		this.hasReward = hasReward;
	}

	public List<ProjectForRewardMember> getProjectRwardMember() {
		return projectRwardMember;
	}

	public void setProjectRwardMember(List<ProjectForRewardMember> projectRwardMember) {
		this.projectRwardMember = projectRwardMember;
	}

	public List<ProjectForLevel> getProjectForLevel() {
		return projectForLevel;
	}

	public void setProjectForLevel(List<ProjectForLevel> projectForLevel) {
		this.projectForLevel = projectForLevel;
	}

	public Date getSaleComplatedTime() {
		return saleComplatedTime;
	}

	public void setSaleComplatedTime(Date saleComplatedTime) {
		this.saleComplatedTime = saleComplatedTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public BigDecimal getRatioAmount() {
		return ratioAmount;
	}

	public void setRatioAmount(BigDecimal ratioAmount) {
		this.ratioAmount = ratioAmount;
	}

	public Integer getTotalDays() {
		return totalDays;
	}

	public void setTotalDays(Integer totalDays) {
		this.totalDays = totalDays;
	}

	public List<PrizeInPool> getPrizeInPoolList() {
		return prizeInPoolList;
	}

	public void setPrizeInPoolList(List<PrizeInPool> prizeInPoolList) {
		this.prizeInPoolList = prizeInPoolList;
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

	public String getMaxReward() {
		return maxReward;
	}
	public void setMaxReward(String maxReward) {
		this.maxReward = maxReward;
	}

	public boolean getIsQuick2Project() {
		return isQuick2Project;
	}

	public void setIsQuick2Project(boolean isQuick2Project) {
		this.isQuick2Project = isQuick2Project;
	}
	
	
	
	
	
}
