package com.yourong.common.thirdparty.sinapay.member.domain.dto;

import java.io.Serializable;
import java.util.Map;

import com.yourong.common.thirdparty.sinapay.common.annotations.NotNull;

public class QueryCtrlResultDto extends RequestDto implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 冻结解冻订单号 必填
	 */
	@NotNull
	private String outCtrlNo;
	
	/**
	 * 订单状态
	 */
	private String ctrlStatus;
	
	/**
	 * 错误信息
	 */
	private String errorMsg;
	 /**
     * 扩展信息	
     */
    private Map<String, String> extendParam;

	

	public String getOutCtrlNo() {
		return outCtrlNo;
	}



	public void setOutCtrlNo(String outCtrlNo) {
		this.outCtrlNo = outCtrlNo;
	}



	public Map<String, String> getExtendParam() {
		return extendParam;
	}



	public void setExtendParam(Map<String, String> extendParam) {
		this.extendParam = extendParam;
	}



	public String getCtrlStatus() {
		return ctrlStatus;
	}



	public void setCtrlStatus(String ctrlStatus) {
		this.ctrlStatus = ctrlStatus;
	}



	public String getErrorMsg() {
		return errorMsg;
	}



	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}



	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("QueryCtrlResultDto [outCtrlNo=").append(outCtrlNo)
				.append(", ctrlStatus=").append(ctrlStatus)
				.append(", errorMsg=").append(errorMsg)
				.append(", extendParam=").append(extendParam)
				.append("]");
		return builder.toString();
	}

	
    
}
