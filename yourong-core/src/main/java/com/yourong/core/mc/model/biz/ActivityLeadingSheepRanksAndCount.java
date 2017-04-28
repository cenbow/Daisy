package com.yourong.core.mc.model.biz;

import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;

public class ActivityLeadingSheepRanksAndCount extends AbstractBaseObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 获取一羊领头等的用户数据
	 */
	private List<ActivityLeadingSheepRanks> ranks;

	/**
	 * 获取一羊领头等的用户总人数
	 */
	private Integer totalMembers;

	public List<ActivityLeadingSheepRanks> getRanks() {
		return ranks;
	}

	public void setRanks(List<ActivityLeadingSheepRanks> ranks) {
		this.ranks = ranks;
	}

	public Integer getTotalMembers() {
		return totalMembers;
	}

	public void setTotalMembers(Integer totalMembers) {
		this.totalMembers = totalMembers;
	}

}
