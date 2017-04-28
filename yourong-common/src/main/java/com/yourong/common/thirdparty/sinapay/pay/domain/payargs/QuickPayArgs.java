package com.yourong.common.thirdparty.sinapay.pay.domain.payargs;

import com.yourong.common.thirdparty.sinapay.common.annotations.NotNull;
import com.yourong.common.thirdparty.sinapay.common.enums.BankCardType;
import com.yourong.common.thirdparty.sinapay.common.enums.BankCode;
import com.yourong.common.thirdparty.sinapay.common.enums.BankServiceType;
import com.yourong.common.thirdparty.sinapay.common.enums.CredentialsType;
import com.yourong.common.thirdparty.sinapay.common.enums.PayMethod;

/**
 * <p>快捷支付</p>
 * @author Wallis Wang
 * @version $Id: QuickPayArgs.java, v 0.1 2014年5月23日 上午11:38:50 wangqiang Exp $
 */
public class QuickPayArgs extends PayArgsBase {
    /**
     * 获取支付方式名称
     *
     * @return 支付方式名称
     */
    @Override
    public PayMethod getPayMethod() {
        return PayMethod.QUICK_PAY;
    }

    /**
     * 银行代码
     * 必填
     */
    @NotNull
    private BankCode        bankCode;

    /**
     * 银行卡号(密文，使用RSA 加密。明文长度)
     * 必填
     */
    @NotNull
    private String          bankCardNo;

    /**
     * 户名(密文，使用RSA 加密)
     * 必填
     */
    @NotNull
    private String          accountName;

    /**
     * 卡类型
     * 必填
     */
    @NotNull
    private BankCardType    bankCardType;

    /**
     * 卡属性
     * 必填
     */
   @NotNull
    private BankServiceType bankServiceType;

    /**
     * 证件类型
     */
    private CredentialsType credentialsType;

    /**
     * 证件号码
     * 
     */
    private String          credentialsTypeNo;

    /**
     * 银行预留手机号(银行预留手机号)
     * 
     */
    private String          bankLeavePhone;

    /**
     * 有效期(密文，使用RSA 加密。明文长度)
     * 
     */
    private String          validTime;

    /**
     * CVV2(密文，使用RSA 加密。明文长度)
     * 
     */
    private String          CVV2;

    public BankCode getBankCode() {
        return bankCode;
    }

    public void setBankCode(BankCode bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankCardNo() {
        return bankCardNo;
    }

    public void setBankCardNo(String bankCardNo) {
        this.bankCardNo = bankCardNo;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public BankCardType getBankCardType() {
        return bankCardType;
    }

    public void setBankCardType(BankCardType bankCardType) {
        this.bankCardType = bankCardType;
    }

    public BankServiceType getBankServiceType() {
        return bankServiceType;
    }

    public void setBankServiceType(BankServiceType bankServiceType) {
        this.bankServiceType = bankServiceType;
    }

    public CredentialsType getCredentialsType() {
        return credentialsType;
    }

    public void setCredentialsType(CredentialsType credentialsType) {
        this.credentialsType = credentialsType;
    }

    public String getCredentialsTypeNo() {
        return credentialsTypeNo;
    }

    public void setCredentialsTypeNo(String credentialsTypeNo) {
        this.credentialsTypeNo = credentialsTypeNo;
    }

    public String getBankLeavePhone() {
        return bankLeavePhone;
    }

    public void setBankLeavePhone(String bankLeavePhone) {
        this.bankLeavePhone = bankLeavePhone;
    }

    public String getValidTime() {
        return validTime;
    }

    public void setValidTime(String validTime) {
        this.validTime = validTime;
    }

    public String getCVV2() {
        return CVV2;
    }

    public void setCVV2(String cVV2) {
        CVV2 = cVV2;
    }
}
