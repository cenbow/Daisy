package com.yourong.common.thirdparty.sinapay.pay.domain.payargs;

import com.yourong.common.thirdparty.sinapay.common.annotations.NotNull;
import com.yourong.common.thirdparty.sinapay.common.enums.PayMethod;

/**
 * <p>线下汇款</p>
 * @author Wallis Wang
 * @version $Id: OfflinePayArgs.java, v 0.1 2014年5月23日 上午11:37:40 wangqiang Exp $
 */
public class OfflinePayArgs extends PayArgsBase {
    /**
     * 获取支付方式名称
     *
     * @return 支付方式名称
     */
    @Override
    public PayMethod getPayMethod() {
        return PayMethod.OFFLINE_PAY;
    }

    /**
     * 线下汇款银行单号
     * 必填
     */
    @NotNull
    private String bankOrderId;

    /**
     * 线下汇款付款人姓名
     * 必填
     */
    @NotNull
    private String payerName;

    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    public String getBankOrderId() {
        return bankOrderId;
    }

    public void setBankOrderId(String bankOrderId) {
        this.bankOrderId = bankOrderId;
    }
}
