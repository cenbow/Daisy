package com.yourong.core.fin.model.biz;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * 用户任务、投资、活动获取人气值
 * 
 * @author fuyili 创建时间:2015年8月13日下午3:08:53
 */
public class PopularityMemberBiz extends AbstractBaseObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 任务赚取人气值
	 */
	private int tastTotalPop;

	/**
	 * 投资赚取人气值
	 */
	private int investTotalPop;

	/**
	 * 活动赚取人气值
	 */
	private int activityTotalPop;

	public int getTastTotalPop() {
		return tastTotalPop;
	}

	public void setTastTotalPop(int tastTotalPop) {
		this.tastTotalPop = tastTotalPop;
	}

	public int getInvestTotalPop() {
		return investTotalPop;
	}

	public void setInvestTotalPop(int investTotalPop) {
		this.investTotalPop = investTotalPop;
	}

	public int getActivityTotalPop() {
		return activityTotalPop;
	}

	public void setActivityTotalPop(int activityTotalPop) {
		this.activityTotalPop = activityTotalPop;
	}

}