/**
 * 
 */
package com.yourong.common.thirdparty.sinapay.member.domain.dto;

import java.io.Serializable;
import java.util.Map;

import com.yourong.common.thirdparty.sinapay.common.annotations.NotNull;
import com.yourong.common.thirdparty.sinapay.common.enums.IdType;
import com.yourong.common.thirdparty.sinapay.common.enums.MemberType;

/**
 * 钱包账户标识
 * @version 0.1 2014年5月20日 下午12:02:44
 */
public class CreateActiveMemberDto extends RequestDto implements Serializable {

    private static final long   serialVersionUID = -2308517863223204468L;

    /**
     * 用户标识信息
     * 必填
     */
    @NotNull
    private String              identityId;

    /**
     * 用户标识类型
     * 必填
     */
    @NotNull
    private IdType              idType;

    /**
     * 会员类型
     * <p>企业还是个人</p>
     */
    private MemberType          memberType;
    
    /**
     * 会员请求IP
     */
    private String              clientIp;

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

    public IdType getIdType() {
        return idType;
    }

    public void setIdType(IdType idType) {
        this.idType = idType;
    }

    public MemberType getMemberType() {
        return memberType;
    }

    public void setMemberType(MemberType memberType) {
        this.memberType = memberType;
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
		builder.append("CreateActiveMemberDto [identityId=");
		builder.append(identityId);
		builder.append(", idType=");
		builder.append(idType);
		builder.append(", memberType=");
		builder.append(memberType);
		builder.append(", clientIp=");
		builder.append(clientIp);
		builder.append(", extendParam=");
		builder.append(extendParam);
		builder.append("]");
		return builder.toString();
	}
    
}
