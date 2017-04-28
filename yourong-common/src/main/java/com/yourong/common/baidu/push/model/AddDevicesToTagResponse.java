package com.yourong.common.baidu.push.model;

import com.yourong.common.baidu.yun.annotation.JSonPath;

import java.util.LinkedList;
import java.util.List;
public class AddDevicesToTagResponse extends PushResponse{

	@JSonPath(path="response_params\\result")
	private List<DeviceInfo> devicesInfoAfterAdded = new LinkedList<DeviceInfo> ();
	
	// get
	public List<DeviceInfo> getDevicesInfoAfterAdded () {
		return devicesInfoAfterAdded;
	}
}
