package com.yourong.common.baidu.push.model;


import com.yourong.common.baidu.push.constants.BaiduPushConstants;
import com.yourong.common.baidu.yun.annotation.HttpParamKeyName;
import com.yourong.common.baidu.yun.annotation.R;
import com.yourong.common.baidu.yun.annotation.RangeRestrict;

public class QueryDeviceNumInTagRequest extends PushRequest{

	@HttpParamKeyName(name= BaiduPushConstants.TAG_NAME, param= R.REQUIRE)
	@RangeRestrict(minLength=1, maxLength=128)
	private String tagName = null;
	
	// get
	public String getTagName () {
		return tagName;
	}
	// set
	public void setTagName (String tagName) {
		this.tagName = tagName;
	}
	// add
	public QueryDeviceNumInTagRequest addTagName (String tagName) {
		this.tagName = tagName;
		return this;
	}
    public QueryDeviceNumInTagRequest addDeviceType (Integer deviceType) {
    	this.deviceType = deviceType;
    	return this;
    }
	public QueryDeviceNumInTagRequest addExpires(Long requestTimeOut) {
		this.expires = requestTimeOut;
		return this;
	}
}
