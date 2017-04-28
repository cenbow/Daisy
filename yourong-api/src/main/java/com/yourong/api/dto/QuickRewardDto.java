package com.yourong.api.dto;

import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.core.ic.model.LotteryRuleAmountNumber;
import com.yourong.core.ic.model.PrizeInPool;
import com.yourong.core.ic.model.PrizePool;
import com.yourong.core.ic.model.QuickRewardConfig;
import com.yourong.core.mc.model.CouponTemplate;

public class QuickRewardDto extends AbstractBaseObject{
	
	private Long projectId;
	
	private String rewardHour;
	
	private List<PrizePool> prizePool;
	
	private List<PrizeInPool> prizeInPool;
	
	private List<LotteryRuleAmountNumber> lottery;
	
	private List<CouponTemplate> couponList;
	
	private QuickRewardConfig  quickRewardConfig;

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public List<PrizePool> getPrizePool() {
		return prizePool;
	}

	public void setPrizePool(List<PrizePool> prizePool) {
		this.prizePool = prizePool;
	}

	public List<PrizeInPool> getPrizeInPool() {
		return prizeInPool;
	}

	public void setPrizeInPool(List<PrizeInPool> prizeInPool) {
		this.prizeInPool = prizeInPool;
	}

	public List<LotteryRuleAmountNumber> getLottery() {
		return lottery;
	}

	public void setLottery(List<LotteryRuleAmountNumber> lottery) {
		this.lottery = lottery;
	}

	public List<CouponTemplate> getCouponList() {
		return couponList;
	}

	public void setCouponList(List<CouponTemplate> couponList) {
		this.couponList = couponList;
	}

	public String getRewardHour() {
		return rewardHour;
	}

	public void setRewardHour(String rewardHour) {
		this.rewardHour = rewardHour;
	}

	public QuickRewardConfig getQuickRewardConfig() {
		return quickRewardConfig;
	}

	public void setQuickRewardConfig(QuickRewardConfig quickRewardConfig) {
		this.quickRewardConfig = quickRewardConfig;
	}
	
}
