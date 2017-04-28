/**
 * 
 */
package com.yourong.common.thirdparty.sinapay.member.domain.dto;

import java.io.Serializable;
import java.util.Map;

import com.yourong.common.thirdparty.sinapay.common.annotations.NotNull;
import com.yourong.common.thirdparty.sinapay.common.enums.IdType;
import com.yourong.common.thirdparty.sinapay.common.enums.VerifyType;

/**
 * 绑定认证信息dto
 * 
 * @version 0.1 2014年5月20日 上午11:08:45
 */
public class BindingVerifyDto extends RequestDto implements Serializable {

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
	 * 认证内容 必填
	 */
	@NotNull
	private String verifyEntity;
	
	/**
	 * 会员请求IP
	 */
	private String clientIp;

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

	public String getVerifyEntity() {
		return verifyEntity;
	}

	public void setVerifyEntity(String verifyEntity) {
		this.verifyEntity = verifyEntity;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
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
		builder.append("BindingVerifyDto [identityId=");
		builder.append(identityId);
		builder.append(", identityType=");
		builder.append(identityType);
		builder.append(", verifyType=");
		builder.append(verifyType);
		builder.append(", verifyEntity=");
		builder.append(verifyEntity);
		builder.append(", clientIp=");
		builder.append(clientIp);
		builder.append(", extendParam=");
		builder.append(extendParam);
		builder.append("]");
		return builder.toString();
	}

}
