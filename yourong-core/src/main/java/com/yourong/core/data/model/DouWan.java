package com.yourong.core.data.model;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * Created by XR on 2016/11/28.
 */
public class DouWan extends AbstractBaseObject {
    private String registerTime;
    private Long memberId;
    private String registerTraceNo;
	public String getRegisterTime() {
		return registerTime;
	}
	public void setRegisterTime(String registerTime) {
		this.registerTime = registerTime;
	}
	public Long getMemberId() {
		return memberId;
	}
	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}
	public String getRegisterTraceNo() {
		return registerTraceNo;
	}
	public void setRegisterTraceNO(String registerTraceNo) {
		this.registerTraceNo = registerTraceNo;
	}

  
}
