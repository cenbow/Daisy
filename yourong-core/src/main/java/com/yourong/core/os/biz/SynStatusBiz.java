package com.yourong.core.os.biz;

import javax.validation.constraints.NotNull;

import com.yourong.common.domain.AbstractBaseObject;

public class SynStatusBiz extends AbstractBaseObject {

	/**
	 * 渠道商
	 */
	@NotNull
	private String channelKey;

	/**
	 * 外部业务号
	 */
	@NotNull
	private String outBizNo;

	public String getOutBizNo() {
		return outBizNo;
	}

	public void setOutBizNo(String outBizNo) {
		this.outBizNo = outBizNo;
	}

	public String getChannelKey() {
		return channelKey;
	}

	public void setChannelKey(String channelKey) {
		this.channelKey = channelKey;
	}

}
