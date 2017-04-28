/**
 * 
 */
package com.yourong.common.thirdparty.sinapay.member.domain.dto;

import java.io.Serializable;
import java.util.Map;

import com.yourong.common.thirdparty.sinapay.common.annotations.NotNull;
import com.yourong.common.thirdparty.sinapay.common.enums.CredentialsType;
import com.yourong.common.thirdparty.sinapay.common.enums.IdType;

/**
 * <p>个人会员的实名信息
 * @version 0.1 2014年5月20日 上午10:26:04
 */
public class PersonRealnameDto extends RequestDto implements Serializable {

    private static final long   serialVersionUID = 1546476328194123258L;

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
    private IdType              identityType;

    /**
     * 真实姓名
     * 必填
     */
    @NotNull
    private String              realName;

    /**
     * 证件类型
     * 必填
     */
    @NotNull
    private CredentialsType     certType;

    /**
     * 证件号码
     * 必填
     */
    @NotNull
    private String              certNo;

    /**
     * 是否认证
     */
    private Boolean             needConfirm;
    
    /**
     * 请求IP
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

    public IdType getIdentityType() {
        return identityType;
    }

    public void setIdentityType(IdType identityType) {
        this.identityType = identityType;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public CredentialsType getCertType() {
        return certType;
    }

    public void setCertType(CredentialsType certType) {
        this.certType = certType;
    }

    public String getCertNo() {
        return certNo;
    }

    public void setCertNo(String certNo) {
        this.certNo = certNo;
    }

    public Boolean getNeedConfirm() {
        return needConfirm;
    }

    public void setNeedConfirm(Boolean needConfirm) {
        this.needConfirm = needConfirm;
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
		builder.append("PersonRealnameDto [identityId=");
		builder.append(identityId);
		builder.append(", identityType=");
		builder.append(identityType);
		builder.append(", realName=");
		builder.append(realName);
		builder.append(", certType=");
		builder.append(certType);
		builder.append(", certNo=");
		builder.append(certNo);
		builder.append(", needConfirm=");
		builder.append(needConfirm);
		builder.append(", clientIp=");
		builder.append(clientIp);
		builder.append(", extendParam=");
		builder.append(extendParam);
		builder.append("]");
		return builder.toString();
	}

}
