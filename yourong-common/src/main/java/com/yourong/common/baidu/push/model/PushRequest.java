package com.yourong.common.baidu.push.model;


import com.yourong.common.baidu.push.constants.BaiduPushConstants;
import com.yourong.common.baidu.yun.annotation.HttpParamKeyName;
import com.yourong.common.baidu.yun.annotation.R;

public abstract class PushRequest {

	@HttpParamKeyName(name= BaiduPushConstants.VERSION, param=R.OPTIONAL)
	protected String v = null;
	
	@HttpParamKeyName(name=BaiduPushConstants.TIMESTAMP, param= R.REQUIRE)
	protected Long timestamp = System.currentTimeMillis() / 1000L;
	
	@HttpParamKeyName(name=BaiduPushConstants.EXPIRES, param=R.OPTIONAL)
	protected Long expires = null;
	
	@HttpParamKeyName(name=BaiduPushConstants.DEVICE_TYPE, param=R.OPTIONAL)
	protected Integer deviceType = null;
	
	// get
	public Long getExpires () {
		return expires;
	}
	public Integer getDevice () {
		return deviceType;
	}
	// set
	public void setExpires (Long expires) {
		this.expires = expires;
	}
	public void setDeviceType (Integer deviceType) {
		this.deviceType = deviceType;
	}
}
