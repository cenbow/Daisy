package com.yourong.common.baidu.push.model;


import com.yourong.common.baidu.yun.annotation.JSonPath;

public class QueryDeviceNumInTagResponse extends PushResponse{

	@JSonPath(path="response_params\\device_num")
	private int deviceNum;
	
	// get
	public int getDeviceNum () {
		return deviceNum;
	}
}
