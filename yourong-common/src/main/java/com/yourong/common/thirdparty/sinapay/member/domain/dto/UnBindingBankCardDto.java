package com.yourong.common.thirdparty.sinapay.member.domain.dto;

import java.io.Serializable;
import java.util.Map;

import com.yourong.common.thirdparty.sinapay.common.annotations.NotNull;
import com.yourong.common.thirdparty.sinapay.common.enums.IdType;

/**
 * <p>解绑银行卡</p>
 * @author Wallis Wang
 * @version $Id: UnBindingBankCardDto.java, v 0.1 2014年6月6日 下午5:37:24 wangqiang Exp $
 */
public class UnBindingBankCardDto extends RequestDto implements Serializable {

    /**
     * 
     */
    private static final long   serialVersionUID = 6575886011417180355L;

    /**
     * 用户标识信息
     * 必填
     */
    @NotNull
    private String              indentityId;

    /**
     * 用户标识类型
     * 必填
     */
    @NotNull
    private IdType              indentityType;

    /**
     * 卡ID
     * 必填
     */
    @NotNull
    private String              cardId;
    
	/**会员解绑卡IP**/
	private String              userUnBindIp;

    /**
     * 扩展信息
     */
    private Map<String, String> extendParam;

    public String getIndentityId() {
        return indentityId;
    }

    public void setIndentityId(String indentityId) {
        this.indentityId = indentityId;
    }

    public IdType getIndentityType() {
        return indentityType;
    }

    public void setIndentityType(IdType indentityType) {
        this.indentityType = indentityType;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getUserUnBindIp() {
		return userUnBindIp;
	}

	public void setUserUnBindIp(String userUnBindIp) {
		this.userUnBindIp = userUnBindIp;
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
		builder.append("UnBindingBankCardDto [indentityId=");
		builder.append(indentityId);
		builder.append(", indentityType=");
		builder.append(indentityType);
		builder.append(", cardId=");
		builder.append(cardId);
		builder.append(", userUnBindIp=");
		builder.append(userUnBindIp);
		builder.append(", extendParam=");
		builder.append(extendParam);
		builder.append("]");
		return builder.toString();
	}

}
