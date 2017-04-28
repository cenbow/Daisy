package com.yourong.common.thirdparty.sinapay.member.domain.dto;

import java.io.Serializable;
import java.util.Map;

import com.yourong.common.thirdparty.sinapay.common.annotations.NotNull;
import com.yourong.common.thirdparty.sinapay.common.enums.IdType;
import com.yourong.common.thirdparty.sinapay.common.enums.VerifyType;

public class QueryVerifyDto extends RequestDto implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 用户标识信息 必填
	 */
	@NotNull
	private String identityId;

	/**
	 * 用户标识类型 必填
	 */
	@NotNull
	private IdType identityType;

	/**
	 * 认证类型 必填
	 */
	@NotNull
	private VerifyType verifyType;
	
	/**
	 * 是否掩码
	 */
	private String  isMask;	
	
	 /**
     * 扩展信息
     */
    private Map<String, String> extendParam;

	public String getIdentityId() {
		return identityId;
	}

	public void setIdentityId(String identityId) {
		this.identityId = identityId;
	}

	public IdType getIdentityType() {
		return identityType;
	}

	public void setIdentityType(IdType identityType) {
		this.identityType = identityType;
	}

	public VerifyType getVerifyType() {
		return verifyType;
	}

	public void setVerifyType(VerifyType verifyType) {
		this.verifyType = verifyType;
	}

	public String getIsMask() {
		return isMask;
	}

	public void setIsMask(String isMask) {
		this.isMask = isMask;
	}

	public Map<String, String> getExtendParam() {
		return extendParam;
	}

	public void setExtendParam(Map<String, String> extendParam) {
		this.extendParam = extendParam;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("QueryVerifyDto [identityId=").append(identityId)
				.append(", identityType=").append(identityType)
				.append(", verifyType=").append(verifyType).append(", isMask=")
				.append(isMask).append(", extendParam=").append(extendParam)
				.append("]");
		return builder.toString();
	}

	
    
}
