package com.yourong.common.thirdparty.sinapay.pay.domain.payargs;

import com.yourong.common.thirdparty.sinapay.common.annotations.NotNull;
import com.yourong.common.thirdparty.sinapay.common.domain.Money;
import com.yourong.common.thirdparty.sinapay.common.enums.PayMethod;

/**
 * <p>支付方式</p>
 * @author Wallis Wang
 * @version $Id: PayArgsBase.java, v 0.1 2014年5月23日 上午11:38:08 wangqiang Exp $
 */
public abstract class PayArgsBase {
    /**
     * 获取支付方式名称
     * @return 支付方式名称
     */
    public abstract PayMethod getPayMethod();

    /**
     * 金额
     * 必填
     */
    @NotNull
    private Money amount;

    public Money getAmount() {
        return amount;
    }

    public void setAmount(Money amount) {
        this.amount = amount;
    }
}
