package com.yourong.common.thirdparty.sinapay.pay.domain.payargs;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.yourong.common.thirdparty.sinapay.common.annotations.NotNull;
import com.yourong.common.thirdparty.sinapay.common.enums.BankCardType;
import com.yourong.common.thirdparty.sinapay.common.enums.BankServiceType;
import com.yourong.common.thirdparty.sinapay.common.enums.PayMethod;
import com.yourong.common.thirdparty.sinapay.common.enums.RechargeBankCode;

/**
 * <p>网银</p>
 * @author Wallis Wang
 * @version $Id: BankPayArgs.java, v 0.1 2014年5月23日 上午11:35:44 wangqiang Exp $
 */
public class BankPayArgs extends PayArgsBase {
    /**
     * 获取支付方式名称
     *
     * @return 支付方式名称
     */
    @Override
    public PayMethod getPayMethod() {
        return PayMethod.ONLINE_BANK;
    }

    /**
     * 银行编码
     * 必填
     */
    @NotNull
    private RechargeBankCode        bankCode;

    /**
     * 银行卡类型
     * 借记/贷记/通用
     * 必填
     */
    @NotNull
    private BankCardType    bankCardType;

    /**
     * 银行服务类型
     * 对公/对私
     * 必填
     */
    @NotNull
    private BankServiceType bankServiceType;

    
    public RechargeBankCode getBankCode() {
		return bankCode;
	}

	public void setBankCode(RechargeBankCode bankCode) {
		this.bankCode = bankCode;
	}

	public BankServiceType getBankServiceType() {
        return bankServiceType;
    }

    public void setBankServiceType(BankServiceType bankServiceType) {
        this.bankServiceType = bankServiceType;
    }

    public BankCardType getBankCardType() {
        return bankCardType;
    }

    public void setBankCardType(BankCardType bankCardType) {
        this.bankCardType = bankCardType;
    }

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE, false);
	}
}
