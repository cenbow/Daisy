package com.yourong.common.thirdparty.sinapay.pay.domain.payargs;

import com.yourong.common.thirdparty.sinapay.common.annotations.NotNull;
import com.yourong.common.thirdparty.sinapay.common.enums.PayMethod;

/**
 * <p>绑定支付</p>
 * @author Wallis Wang
 * @version $Id: BindingPay.java, v 0.1 2014年5月14日 下午4:58:10 wangqiang Exp $
 */
public class BindingPayArgs extends PayArgsBase {

    /**
     * 获取支付方式名称
     * @return 支付方式名称
     */
    @Override
    public PayMethod getPayMethod() {
        return PayMethod.BINDING_PAY;
    }

    /**
     * 已绑定的快捷支付银行卡ID，非银行卡号
     * 必填
     */
    @NotNull
    private String cardId;

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

}
