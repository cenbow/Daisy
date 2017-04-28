package com.yourong.common.thirdparty.sinapay.pay.domain.dto;

import java.util.Map;

import com.yourong.common.thirdparty.sinapay.common.annotations.NotNull;
import com.yourong.common.thirdparty.sinapay.common.domain.Money;
import com.yourong.common.thirdparty.sinapay.common.enums.AccountType;
import com.yourong.common.thirdparty.sinapay.common.enums.IdType;

/**
 * <p>提现</p>
 * @author Wallis Wang
 * @version $Id: CreateWithDrawDto.java, v 0.1 2014年5月26日 上午11:26:03 wangqiang Exp $
 */
public class CreateWithDrawDto extends RequestDto {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2047291456364534544L;

	/**
     * 交易订单号
     * 必填
     */
    @NotNull
    private String              outTradeNo;

    /**
     * 用户ID
     * 必填
     * <p>商户系统用户id(字母或数字)</p>
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
     * 金额
     * 必填
     */
    @NotNull
    private Money               amount;

    /**
     * 银行卡ID
     * 必填
     * <p>用户绑定银行卡ID，即绑定银行卡返回的ID</p>
     */
    private String              cardId;
    
    @NotNull
    private AccountType accountType;
    
    /**
     * 会员IP
     */
    @NotNull
    private String              userIp;
    
    /**
     * 提现方式
     */
    private String              withdrawMode;
    
    /**
	 * 提现关闭时间
	 * 设置未付款出款提现交易的超时时间，一旦超时，该笔交易就会自动被关闭。
	 */
    private String withdrawCloseTime;

	/**
     * 扩展信息
     */
    private Map<String, String> extendParam;

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
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

    public Money getAmount() {
        return amount;
    }

    public void setAmount(Money amount) {
        this.amount = amount;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public Map<String, String> getExtendParam() {
        return extendParam;
    }

    public void setExtendParam(Map<String, String> extendParam) {
        this.extendParam = extendParam;
    }

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public String getUserIp() {
		return userIp;
	}

	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}

	public String getWithdrawCloseTime() {
		return withdrawCloseTime;
	}

	public void setWithdrawCloseTime(String withdrawCloseTime) {
		this.withdrawCloseTime = withdrawCloseTime;
	}

	public String getWithdrawMode() {
		return withdrawMode;
	}

	public void setWithdrawMode(String withdrawMode) {
		this.withdrawMode = withdrawMode;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CreateWithDrawDto [outTradeNo=");
		builder.append(outTradeNo);
		builder.append(", identityId=");
		builder.append(identityId);
		builder.append(", identityType=");
		builder.append(identityType);
		builder.append(", amount=");
		builder.append(amount);
		builder.append(", cardId=");
		builder.append(cardId);
		builder.append(", userIp=");
		builder.append(userIp);
		builder.append(", withdrawCloseTime=");
		builder.append(withdrawCloseTime);
		builder.append(", withdrawMode=");
		builder.append(withdrawMode);
		builder.append(", extendParam=");
		builder.append(extendParam);
		builder.append("]");
		return builder.toString();
	}
}
