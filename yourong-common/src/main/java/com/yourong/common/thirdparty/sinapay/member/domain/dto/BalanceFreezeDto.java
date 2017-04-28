package com.yourong.common.thirdparty.sinapay.member.domain.dto;

import java.io.Serializable;
import java.util.Map;

import com.yourong.common.thirdparty.sinapay.common.annotations.NotNull;
import com.yourong.common.thirdparty.sinapay.common.domain.Money;
import com.yourong.common.thirdparty.sinapay.common.enums.AccountType;
import com.yourong.common.thirdparty.sinapay.common.enums.IdType;

/**
 * <p>冻结余额</p>
 * @author Wallis Wang
 * @version $Id: BalanceFreezeDto.java, v 0.1 2014年6月6日 下午6:55:11 wangqiang Exp $
 */
public class BalanceFreezeDto extends RequestDto implements Serializable {

    private static final long   serialVersionUID = 8860555848035606029L;

    /**
     * 冻结订单号
     * 必填
     */
    @NotNull
    private String              outFreezeNo;

    /**
     * 用户标识信息
     * 必填
     */
    @NotNull
    private String              identityId;

    /**
     * 用户标识信息
     * 必填
     */
    @NotNull
    private IdType              identityType;

    /**
     * 账户类型
     */
    private AccountType         accountType;

    /**
     * 金额
     * 必填
     */
    @NotNull
    private Money               amount;

    /**
     * 摘要
     * 必填
     */
    @NotNull
    private String              summary;
    
    /**
     * 请求者IP
     */
    @NotNull
    private String              clientIp;

    /**
     * 扩展信息
     */
    private Map<String, String> extendParam;

    public String getOutFreezeNo() {
        return outFreezeNo;
    }

    public void setOutFreezeNo(String outFreezeNo) {
        this.outFreezeNo = outFreezeNo;
    }

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

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public Money getAmount() {
        return amount;
    }

    public void setAmount(Money amount) {
        this.amount = amount;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Map<String, String> getExtendParam() {
        return extendParam;
    }

    public void setExtendParam(Map<String, String> extendParam) {
        this.extendParam = extendParam;
    }

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BalanceFreezeDto [outFreezeNo=");
		builder.append(outFreezeNo);
		builder.append(", identityId=");
		builder.append(identityId);
		builder.append(", identityType=");
		builder.append(identityType);
		builder.append(", accountType=");
		builder.append(accountType);
		builder.append(", amount=");
		builder.append(amount);
		builder.append(", summary=");
		builder.append(summary);
		builder.append(", clientIp=");
		builder.append(clientIp);
		builder.append(", extendParam=");
		builder.append(extendParam);
		builder.append("]");
		return builder.toString();
	}

}
