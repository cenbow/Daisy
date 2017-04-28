package com.yourong.common.thirdparty.sinapay.member.domain.dto;

import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.yourong.common.annotation.ValueCheck;

/**
 * 
 * @desc 设置支付密码
 * @author wangyanji 2016年7月12日下午9:33:07
 */
public class PayPasswordDto extends RequestDto {

    /**
	 * 用户标识信息
	 */
	@ValueCheck(nullable = false, maxLength = 50)
	private String identityId;

    /**
	 * 用户标识类型
	 */
	@ValueCheck(nullable = false, maxLength = 16)
	private String identityType;

    /**
     * 扩展信息
     */
    private Map<String, String> extendParam;

	/**
	 * 响应类参数:
	 * 
	 * 是否已经设置支付密码，Y：已设置；N：未设置
	 */
	private String isSetPaypass;

	public String getIdentityId() {
		return identityId;
	}

	public void setIdentityId(String identityId) {
		this.identityId = identityId;
	}

	public String getIdentityType() {
		return identityType;
	}

	public void setIdentityType(String identityType) {
		this.identityType = identityType;
	}

	public Map<String, String> getExtendParam() {
		return extendParam;
	}

	public void setExtendParam(Map<String, String> extendParam) {
		this.extendParam = extendParam;
	}

	public String getIsSetPaypass() {
		return isSetPaypass;
	}

	public void setIsSetPaypass(String isSetPaypass) {
		this.isSetPaypass = isSetPaypass;
	}

	public PayPasswordDto() {
		super();
	}

	public PayPasswordDto(String identityId, String identityType, Map<String, String> extendParam) {
		setIdentityId(identityId);
		setIdentityType(identityType);
		setExtendParam(extendParam);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE, false);
	}
}
