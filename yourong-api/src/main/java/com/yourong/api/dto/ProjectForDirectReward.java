package com.yourong.api.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.core.ic.model.LotteryRuleAmountNumber;
import com.yourong.core.ic.model.PrizeInPool;
import com.yourong.core.ic.model.QuickRewardConfig;
import com.yourong.core.ic.model.biz.ProjectForLevel;
import com.yourong.core.ic.model.biz.ProjectForRewardMember;

public class ProjectForDirectReward extends AbstractBaseObject {

	// 1:投资抽奖期限中，2：投资中，已超出抽奖期限，3：履约中 ，4：流标
	private Integer flag;
	// 每天奖励情况
	private List<PrizeInPoolForProject> prizeInPoolForProject;
	// 投资金额和抽奖次数关系
	private List<LotteryRuleAmountNumber> lottery;
	//销售完成时间
	private Date saleComplatedTime;
    /**上线时间(年月日时分秒)**/
    private Date onlineTime;
	
	//奖池金额
	private BigDecimal ratioAmount;
	
	//下一天的奖池金额
	private BigDecimal nextDayAmount;
	
	private Integer totalDays;
	
	/**状态码(1:存盘,10:待审核,20:待发布,30:投资中,40:已暂停,50:已满额,60:已截止,70:已还款)**/
    private Integer status;
	
	private List<ProjectForLevel> projectForLevel;
	
	private List<ProjectForRewardMember> projectRewardList;
	
	private QuickRewardConfig  quickRewardConfig;
	
	private List<PrizeInPool> prizeInPoolList;
	
	  /**快投奖励截止时间**/
    private Date quickRewardDate;

	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	public List<PrizeInPoolForProject> getPrizeInPoolForProject() {
		return prizeInPoolForProject;
	}

	public void setPrizeInPoolForProject(
			List<PrizeInPoolForProject> prizeInPoolForProject) {
		this.prizeInPoolForProject = prizeInPoolForProject;
	}

	public List<LotteryRuleAmountNumber> getLottery() {
		return lottery;
	}

	public void setLottery(List<LotteryRuleAmountNumber> lottery) {
		this.lottery = lottery;
	}

	public Date getSaleComplatedTime() {
		return saleComplatedTime;
	}

	public void setSaleComplatedTime(Date saleComplatedTime) {
		this.saleComplatedTime = saleComplatedTime;
	}

	public BigDecimal getRatioAmount() {
		return ratioAmount;
	}

	public void setRatioAmount(BigDecimal ratioAmount) {
		this.ratioAmount = ratioAmount;
	}

	public List<ProjectForLevel> getProjectForLevel() {
		return projectForLevel;
	}

	public void setProjectForLevel(List<ProjectForLevel> projectForLevel) {
		this.projectForLevel = projectForLevel;
	}

	public List<ProjectForRewardMember> getProjectRewardList() {
		return projectRewardList;
	}

	public void setProjectRewardList(List<ProjectForRewardMember> projectRewardList) {
		this.projectRewardList = projectRewardList;
	}

	public Date getOnlineTime() {
		return onlineTime;
	}

	public void setOnlineTime(Date onlineTime) {
		this.onlineTime = onlineTime;
	}

	public Integer getTotalDays() {
		return totalDays;
	}

	public void setTotalDays(Integer totalDays) {
		this.totalDays = totalDays;
	}

	public BigDecimal getNextDayAmount() {
		return nextDayAmount;
	}

	public void setNextDayAmount(BigDecimal nextDayAmount) {
		this.nextDayAmount = nextDayAmount;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public QuickRewardConfig getQuickRewardConfig() {
		return quickRewardConfig;
	}

	public void setQuickRewardConfig(QuickRewardConfig quickRewardConfig) {
		this.quickRewardConfig = quickRewardConfig;
	}

	public List<PrizeInPool> getPrizeInPoolList() {
		return prizeInPoolList;
	}

	public void setPrizeInPoolList(List<PrizeInPool> prizeInPoolList) {
		this.prizeInPoolList = prizeInPoolList;
	}

	public Date getQuickRewardDate() {
		return quickRewardDate;
	}

	public void setQuickRewardDate(Date quickRewardDate) {
		this.quickRewardDate = quickRewardDate;
	}
}
