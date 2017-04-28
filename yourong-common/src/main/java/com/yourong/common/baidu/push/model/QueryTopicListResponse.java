package com.yourong.common.baidu.push.model;

import com.yourong.common.baidu.yun.annotation.JSonPath;

import java.util.LinkedList;
import java.util.List;


public class QueryTopicListResponse extends PushResponse{

	@JSonPath(path="response_params\\total_num")
	private int totalNum;
	
	@JSonPath(path="response_params\\result")
	private List<TopicResultInfo> topicResultInfos = new LinkedList<TopicResultInfo> ();
	
	// get
	public int getTotalNum () {
		return totalNum;
	}
	public List<TopicResultInfo> getTimerResultInfos () {
		return topicResultInfos;
	}
}
