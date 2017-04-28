package com.yourong.common.thirdparty.sinapay.pay.domain.payargs;

import com.yourong.common.thirdparty.sinapay.common.annotations.NotNull;
import com.yourong.common.thirdparty.sinapay.common.enums.AccountType;
import com.yourong.common.thirdparty.sinapay.common.enums.PayMethod;

/**
 * 余额支付参数
 * Created by Pandazki on 5/2/14.
 */
public class BalancePayArgs extends PayArgsBase {
    /**
     * 获取支付方式名称
     *
     * @return 支付方式名称
     */
    @Override
    public PayMethod getPayMethod() {
        return PayMethod.BALANCE;
    }

    /**
     * 账户类型
     * 必填
     */
    @NotNull
    private AccountType accountType;

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

}
