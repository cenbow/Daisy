package com.yourong.common.thirdparty.sinapay.pay.domain.payargs;

import com.yourong.common.thirdparty.sinapay.common.annotations.NotNull;
import com.yourong.common.thirdparty.sinapay.common.domain.Money;
import com.yourong.common.thirdparty.sinapay.common.enums.AccountType;
import com.yourong.common.thirdparty.sinapay.common.enums.IdType;

/**
 * <p>分账参数</p>
 * @author Wallis Wang
 * @version $Id: SplitArgs.java, v 0.1 2014年5月14日 下午1:49:26 wangqiang Exp $
 */
public class SplitArgs {

    /**
     * 付款人标识
     * 必填
     */
    @NotNull
    private String      payerId;

    /**
     * 付款人标识类型
     * 必填
     */
    @NotNull
    private IdType      payerIdType;

    /**
     * 付款人账户类型
     * 
     */
    private AccountType payerAccountType;

    /**
     * 收款人标识
     * 必填
     */
    @NotNull
    private String      payeeId;

    /**
     * 收款人标识类型
     * 必填
     */
    @NotNull
    private IdType      payeeIdType;

    /**
     * 收款人账户类型
     * 
     */
    private AccountType payeeAccountType;

    /**
     * 金额
     * 必填
     */
    @NotNull
    private Money       amount;

    /**
     * 备注
     */
    private String      remark;

    public String getPayerId() {
        return payerId;
    }

    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }

    public String getPayeeId() {
        return payeeId;
    }

    public void setPayeeId(String payeeId) {
        this.payeeId = payeeId;
    }

    public Money getAmount() {
        return amount;
    }

    public void setAmount(Money amount) {
        this.amount = amount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public IdType getPayerIdType() {
        return payerIdType;
    }

    public void setPayerIdType(IdType payerIdType) {
        this.payerIdType = payerIdType;
    }

    public AccountType getPayerAccountType() {
        return payerAccountType;
    }

    public void setPayerAccountType(AccountType payerAccountType) {
        this.payerAccountType = payerAccountType;
    }

    public IdType getPayeeIdType() {
        return payeeIdType;
    }

    public void setPayeeIdType(IdType payeeIdType) {
        this.payeeIdType = payeeIdType;
    }

    public AccountType getPayeeAccountType() {
        return payeeAccountType;
    }

    public void setPayeeAccountType(AccountType payeeAccountType) {
        this.payeeAccountType = payeeAccountType;
    }

}
