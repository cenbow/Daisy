package com.yourong.common.baidu.push.model;

import com.yourong.common.baidu.yun.annotation.JSonPath;

import java.util.LinkedList;
import java.util.List;


public class QueryStatisticMsgResponse extends PushResponse{

	@JSonPath(path="response_params\\result")
	private List<MsgStatInfo> msgStats = new LinkedList<MsgStatInfo>();
	
	public List<MsgStatInfo> getMsgStats () {
		return msgStats;
	}
}
