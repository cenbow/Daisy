package com.yourong.core.mc.model.biz;

import java.util.List;

import com.yourong.core.tc.model.biz.TransactionForFirstInvestAct;

public class ActivityForRankingList2nd {
	/** 满足投资额的会员列表 **/
	private List<TransactionForFirstInvestAct> rankingList;
	/** 会员数量 **/
	private int memberCount;

	public List<TransactionForFirstInvestAct> getRankingList() {
		return rankingList;
	}

	public void setRankingList(List<TransactionForFirstInvestAct> rankingList) {
		this.rankingList = rankingList;
	}

	public int getMemberCount() {
		return memberCount;
	}

	public void setMemberCount(int memberCount) {
		this.memberCount = memberCount;
	}

}
