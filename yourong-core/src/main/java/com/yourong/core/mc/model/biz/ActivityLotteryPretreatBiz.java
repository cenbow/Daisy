package com.yourong.core.mc.model.biz;

import java.util.List;

import com.yourong.common.enums.ResultCode;
import com.yourong.core.mc.model.ActivityLotteryPretreat;

public class ActivityLotteryPretreatBiz {

	private ActivityLotteryPretreat activityLotteryPretreat;

	private List<ActivityLotteryPretreat> list;

	private ResultCode resultCode;

	public ActivityLotteryPretreat getActivityLotteryPretreat() {
		return activityLotteryPretreat;
	}

	public void setActivityLotteryPretreat(ActivityLotteryPretreat activityLotteryPretreat) {
		this.activityLotteryPretreat = activityLotteryPretreat;
	}

	public List<ActivityLotteryPretreat> getList() {
		return list;
	}

	public void setList(List<ActivityLotteryPretreat> list) {
		this.list = list;
	}

	public ResultCode getResultCode() {
		return resultCode;
	}

	public void setResultCode(ResultCode resultCode) {
		this.resultCode = resultCode;
	}

}