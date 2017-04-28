package com.yourong.common.baidu.push.model;

import com.yourong.common.baidu.push.constants.BaiduPushConstants;
import com.yourong.common.baidu.yun.annotation.HttpParamKeyName;
import com.yourong.common.baidu.yun.annotation.JSonPath;
import com.yourong.common.baidu.yun.annotation.R;
import com.yourong.common.baidu.yun.annotation.RangeRestrict;

import java.util.LinkedList;
import java.util.List;



public class QueryTopicRecordsResponse extends PushResponse{

	@JSonPath(path="response_params\\topic_id")
	@HttpParamKeyName(name= BaiduPushConstants.TOPIC_ID, param= R.REQUIRE)
	@RangeRestrict(minLength=1, maxLength=128)
	private String topicId = null;
	
	@JSonPath(path="response_params\\result")
	@HttpParamKeyName(name=BaiduPushConstants.TOPIC_RECORDS, param=R.REQUIRE)
	private List<Record> topicRecords = new LinkedList<Record>();
	
	// get
	public String getTopicId () {
		return topicId;
	}
	public List<Record> getTopicRecords () {
		return topicRecords;
	}
}

