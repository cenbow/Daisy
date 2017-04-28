package com.yourong.common.thirdparty.sinapay.pay.domain.dto;

import java.util.Map;

import com.yourong.common.thirdparty.sinapay.common.annotations.NotNull;
import com.yourong.common.thirdparty.sinapay.common.domain.Money;
import com.yourong.common.thirdparty.sinapay.common.enums.AccountType;
import com.yourong.common.thirdparty.sinapay.common.enums.IdType;
import com.yourong.common.thirdparty.sinapay.pay.domain.payargs.PayArgsBase;

/**
 * <p>充值</p>
 * @author Wallis Wang
 * @version $Id: CreateDepositDto.java, v 0.1 2014年5月26日 上午11:20:00 wangqiang Exp $
 */
public class CreateDepositDto extends RequestDto {

    /**
	 * 序列号
	 */
	private static final long serialVersionUID = -2007974964528823832L;

	/**
     * 交易订单号
     * 必填 
     * <p>商户网站交易订单号，商户内部保证唯一</p>
     */
    @NotNull
    private String outTradeNo;

    /**
     * 商户系统用户id
     * 必填 
     */
    @NotNull
    private String identityId;

    /**
     * 用户标识类型
     * 必填 
     */
    @NotNull
    private IdType identityType;

    /**
     * 金额
     * 必填 
     */
    @NotNull
    private Money amount;

    /**
     * 支付方式 
     * 必填 
     */
    @NotNull
    private PayArgsBase payMethod;

    @NotNull
    private AccountType accountType;
    
    /**
     * 支付用户IP
     * 必填
     */
	@NotNull
	private String payerIp;
	
	
	/**
	 * 充值关闭时间
	 * 设置未付款交易的超时时间，一旦超时，该笔交易就会自动被关闭。
	 */
	private String depositCloseTime;

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

    public PayArgsBase getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(PayArgsBase payMethod) {
        this.payMethod = payMethod;
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

	public String getPayerIp() {
		return payerIp;
	}

	public void setPayerIp(String payerIp) {
		this.payerIp = payerIp;
	}

	public String getDepositCloseTime() {
		return depositCloseTime;
	}

	public void setDepositCloseTime(String depositCloseTime) {
		this.depositCloseTime = depositCloseTime;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CreateDepositDto [outTradeNo=");
		builder.append(outTradeNo);
		builder.append(", identityId=");
		builder.append(identityId);
		builder.append(", identityType=");
		builder.append(identityType);
		builder.append(", amount=");
		builder.append(amount);
		builder.append(", payerIp=");
		builder.append(payerIp);
		builder.append(", depositCloseTime=");
		builder.append(depositCloseTime);
		builder.append(", payMethod=");
		builder.append(payMethod);
		builder.append(", extendParam=");
		builder.append(extendParam);
		builder.append("]");
		return builder.toString();
	}

}
