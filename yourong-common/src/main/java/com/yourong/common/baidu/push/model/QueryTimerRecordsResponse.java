package com.yourong.common.baidu.push.model;

import com.yourong.common.baidu.push.constants.BaiduPushConstants;
import com.yourong.common.baidu.yun.annotation.HttpParamKeyName;
import com.yourong.common.baidu.yun.annotation.JSonPath;
import com.yourong.common.baidu.yun.annotation.R;

import java.util.LinkedList;
import java.util.List;



public class QueryTimerRecordsResponse extends PushResponse{

	@JSonPath(path="response_params\\timer_id")
	@HttpParamKeyName(name= BaiduPushConstants.TIMER_ID, param=R.REQUIRE)
	private String timerId;
	
	@JSonPath(path="response_params\\result")
	@HttpParamKeyName(name=BaiduPushConstants.TIMER_RECORDS, param= R.REQUIRE)
	private List<Record> timerRecords = new LinkedList<Record>();
	
	// get
	public String getTimerId () {
		return timerId;
	}
	public List<Record> getTimerRecords () {
		return timerRecords;
	}
}
