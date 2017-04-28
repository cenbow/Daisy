package com.yourong.common.thirdparty.sinapay.pay.domain.dto;

import java.util.List;
import java.util.Map;

import com.yourong.common.thirdparty.sinapay.common.annotations.NotNull;
import com.yourong.common.thirdparty.sinapay.common.domain.Money;
import com.yourong.common.thirdparty.sinapay.common.enums.AccountType;
import com.yourong.common.thirdparty.sinapay.common.enums.IdType;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeCode;
import com.yourong.common.thirdparty.sinapay.pay.domain.payargs.SplitArgs;

/**
 * <p>代付</p>
 * <pre>分账信息中的付款人必须为收款信息中的收款人，或分账信息中的收款人</pre>
 * @author Wallis Wang
 * @version $Id: CreatePayTradeDto.java, v 0.1 2014年5月14日 下午2:11:18 wangqiang Exp $
 */
public class CreateSinglePayTradeDto extends RequestDto {
    /**
     * 房金所系统交易订单号，对于外部必须唯一。
     * <p>商户网站交易订单号，商户内部保证唯一</p>
     * 必填
     */
    @NotNull
    private String              outTradeNo;

    /**
     * 交易码
     * <p>商户网站代收交易业务码</p>
     * 必填
     */
    @NotNull
    private TradeCode           outTradeCode;

    /**
     * 收款人标识
     * <p>商户系统用户ID、钱包系统会员ID</p>
     * 必填
     */
    @NotNull
    private String              payeeIdentityId;

    /**
     * 收款人标识类型
     * 必填
     */
    @NotNull
    private IdType              payeeIdentityType;

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
     * 分账信息列表
     */
    private List<SplitArgs>     splitList;

    /**
     * 摘要信息。
     * 必填
     */
    @NotNull
    private String              summary;
    
    /**
     * 用户ip
     */
    @NotNull
    private String              userIp;

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

    public TradeCode getOutTradeCode() {
        return outTradeCode;
    }

    public void setOutTradeCode(TradeCode outTradeCode) {
        this.outTradeCode = outTradeCode;
    }

    public String getPayeeIdentityId() {
        return payeeIdentityId;
    }

    public void setPayeeIdentityId(String payeeIdentityId) {
        this.payeeIdentityId = payeeIdentityId;
    }

    public IdType getPayeeIdentityType() {
        return payeeIdentityType;
    }

    public void setPayeeIdentityType(IdType payeeIdentityType) {
        this.payeeIdentityType = payeeIdentityType;
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

    public List<SplitArgs> getSplitList() {
        return splitList;
    }

    public void setSplitList(List<SplitArgs> splitList) {
        this.splitList = splitList;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getUserIp() {
		return userIp;
	}

	public void setUserIp(String userIp) {
		this.userIp = userIp;
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
		builder.append("CreateSinglePayTradeDto [outTradeNo=");
		builder.append(outTradeNo);
		builder.append(", outTradeCode=");
		builder.append(outTradeCode);
		builder.append(", payeeIdentityId=");
		builder.append(payeeIdentityId);
		builder.append(", payeeIdentityType=");
		builder.append(payeeIdentityType);
		builder.append(", accountType=");
		builder.append(accountType);
		builder.append(", amount=");
		builder.append(amount);
		builder.append(", splitList=");
		builder.append(splitList);
		builder.append(", summary=");
		builder.append(summary);
		builder.append(", userIp=");
		builder.append(userIp);
		builder.append(", extendParam=");
		builder.append(extendParam);
		builder.append("]");
		return builder.toString();
	}
}
