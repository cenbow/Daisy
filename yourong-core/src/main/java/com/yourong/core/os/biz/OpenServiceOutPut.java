package com.yourong.core.os.biz;

import com.yourong.common.domain.AbstractBaseObject;

public class OpenServiceOutPut extends AbstractBaseObject {

	/**
	 * 外部业务号
	 */
	private String outBizNo;

	/**
	 * 标的状态
	 */
	private Integer status;

	public String getOutBizNo() {
		return outBizNo;
	}

	public void setOutBizNo(String outBizNo) {
		this.outBizNo = outBizNo;
	}

}
