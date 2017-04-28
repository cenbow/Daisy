package com.yourong.common.thirdparty.sinapay.pay.domain.dto;

import java.io.Serializable;
import java.util.Map;

import com.yourong.common.annotation.ValueCheck;
import com.yourong.common.thirdparty.sinapay.common.enums.AuthTypeWhitelist;
import com.yourong.common.thirdparty.sinapay.common.enums.IdType;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.RequestDto;

/**
 * 
 * <p>Description:委托扣款重定向</p>
 *
 * @Author lu.wenshan
 * @Date 2016年7月12日下午2:26:22
 *
 */
public class HandleWithholdAuthorityDto extends RequestDto implements Serializable {

    /**
     * 
     */
	private static final long serialVersionUID = 6178032309492851821L;

	/**
     * 外部系统用户ID
     * 必填
     * <p>商户系统用户id(字母或数字)</p>
     */
	@ValueCheck(nullable = false, maxLength = 50)
    private String identityId;

    /**
     * 用户标识类型
     * 必填
     */
	@ValueCheck(nullable = false, maxLength = 16)
    private IdType identityType;
    
    /**
     * 单笔额度
     * <p>授权可设置的最小日累计额度，++为无限大</p>
     */
    private String quota;

    /**
     * 日累计额度
     * <p>授权可设置的最小日累计额度，++为无限大</p>
     */
    private String dayQuota;
    
    /**
     * 代扣授权类型白名单
     * <p>期望的授权类型</p>
     */
    private AuthTypeWhitelist authTypeWhitelist;
    
    /**
     * 是否展示授权明细
     * <p>（Y:是 N：否），默认不展示</p>
     */
    private String isDetailDisp;
    
    /**
     * 是否代扣授权
     * <p>是否代扣授权，Y：是；N：否</p>
     */
    private String isWithholdAuthoity;
    
    /**
     * 授权时间
     * <p>授权的时间，未授权没有;格式yyyyMMddhhmmss</p>
     */
    private String withholdAuthoityTime;
    
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

	public String getQuota() {
		return quota;
	}

	public void setQuota(String quota) {
		this.quota = quota;
	}

	public String getDayQuota() {
		return dayQuota;
	}

	public void setDayQuota(String dayQuota) {
		this.dayQuota = dayQuota;
	}

	public AuthTypeWhitelist getAuthTypeWhitelist() {
		return authTypeWhitelist;
	}

	public void setAuthTypeWhitelist(AuthTypeWhitelist authTypeWhitelist) {
		this.authTypeWhitelist = authTypeWhitelist;
	}

	public String getIsDetailDisp() {
		return isDetailDisp;
	}

	public void setIsDetailDisp(String isDetailDisp) {
		this.isDetailDisp = isDetailDisp;
	}

	public String getIsWithholdAuthoity() {
		return isWithholdAuthoity;
	}

	public void setIsWithholdAuthoity(String isWithholdAuthoity) {
		this.isWithholdAuthoity = isWithholdAuthoity;
	}

	public Map<String, String> getExtendParam() {
		return extendParam;
	}

	public String getWithholdAuthoityTime() {
		return withholdAuthoityTime;
	}

	public void setWithholdAuthoityTime(String withholdAuthoityTime) {
		this.withholdAuthoityTime = withholdAuthoityTime;
	}

	public void setExtendParam(Map<String, String> extendParam) {
		this.extendParam = extendParam;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HandleWithholdAuthorityDto [identityId=");
		builder.append(identityId);
		builder.append(", identityType=");
		builder.append(identityType);
		builder.append(", quota=");
		builder.append(quota);
		builder.append(", dayQuota=");
		builder.append(dayQuota);
		builder.append(", authTypeWhitelist=");
		builder.append(authTypeWhitelist);
		builder.append(", isDetailDisp=");
		builder.append(isDetailDisp);
		builder.append(", isWithholdAuthoity=");
		builder.append(isWithholdAuthoity);
		builder.append(", withholdAuthoityTime=");
		builder.append(withholdAuthoityTime);
		builder.append(", extendParam=");
		builder.append(extendParam);
		builder.append("]");
		return builder.toString();
	}
	
}
