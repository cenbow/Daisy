package com.yourong.core.ic.model.biz;

import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.core.ic.model.DirectLotteryResultList;

public class ProjectForLotteryReturn extends AbstractBaseObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9193339820515134532L;

	private List<DirectLotteryResultList> directLotteryResultList;
	
	private String rewardInfo;
	
	private String popularity;
	
	private boolean isLottery=false;

	public String getRewardInfo() {
		return rewardInfo;
	}

	public void setRewardInfo(String rewardInfo) {
		this.rewardInfo = rewardInfo;
	}

	public boolean isLottery() {
		return isLottery;
	}

	public void setLottery(boolean isLottery) {
		this.isLottery = isLottery;
	}

	public List<DirectLotteryResultList> getDirectLotteryResultList() {
		return directLotteryResultList;
	}

	public void setDirectLotteryResultList(
			List<DirectLotteryResultList> directLotteryResultList) {
		this.directLotteryResultList = directLotteryResultList;
	}

	public String getPopularity() {
		return popularity;
	}

	public void setPopularity(String popularity) {
		this.popularity = popularity;
	}
	
}
