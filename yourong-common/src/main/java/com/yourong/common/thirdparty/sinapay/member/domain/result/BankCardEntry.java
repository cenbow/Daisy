/**
 * 
 */
package com.yourong.common.thirdparty.sinapay.member.domain.result;

import java.io.Serializable;

import com.yourong.common.thirdparty.sinapay.common.enums.BankCardType;
import com.yourong.common.thirdparty.sinapay.common.enums.BankCode;
import com.yourong.common.thirdparty.sinapay.common.enums.BankServiceType;

/**
 * 钱包账户绑定的银行卡列表条目
 * @version 0.1 2014年5月20日 上午11:21:33
 */
public class BankCardEntry implements Serializable {

    private static final long serialVersionUID = 1L;
    //
    private String            cardId;
    //银行代码
    private BankCode          bankCode;
    //账号
    private String            accountNo;
    //开户名称
    private String            accountName;
    //卡类型
    private BankCardType      cardType;
    //卡属性
    private BankServiceType   cardAttr;
    //是否已认证
    private boolean           verified;
    //绑定时间
    private String            createdTime;

    private boolean  isSecurity;

    /**
     * @param cardId 绑定的银行卡标识
     * @param bankCode 开户银行代码
     * @param accountNo 开户账号
     * @param accountName 开户名称
     * @param createdTime 绑定时间
     * @throws IllegalArgumentException 任一参数为null
     */
    public BankCardEntry(String cardId, BankCode bankCode, String accountNo, String accountName,
                         boolean verified, String createdTime,boolean isSecurity) {
        if (cardId == null)
            throw new IllegalArgumentException("cardId is required");

        if (bankCode == null)
            throw new IllegalArgumentException("bankCode is required");

        if (accountNo == null)
            throw new IllegalArgumentException("accountNo is required");

        if (accountName == null)
            throw new IllegalArgumentException("accountName is required");

        if (createdTime == null)
            throw new IllegalArgumentException("createdTime is required");

        this.cardId = cardId;
        this.bankCode = bankCode;
        this.accountNo = accountNo;
        this.accountName = accountName;
        this.createdTime = createdTime;
        this.verified = verified;
        this.isSecurity = isSecurity;
    }

    /**
     * 绑定时间，格式yyyyMMddhhmmss
     * @return not null
     */
    public String getCreatedTime() {
        return createdTime;
    }

    /**
     * 绑定标识
     * @return not null
     */
    public String getCardId() {
        return cardId;
    }

    /**
     * 银行代码
     * @return not null
     */
    public BankCode getBankCode() {
        return bankCode;
    }

    /**
     * 卡号
     * @return not null
     */
    public String getAccountNo() {
        return accountNo;
    }

    /**
     * 开户名称
     * @return not null
     */
    public String getAccountName() {
        return accountName;
    }

    /**
     * @return 银行卡类型或者null
     */
    public BankCardType getCardType() {
        return cardType;
    }

    /**
     * @return 银行卡属性或者null
     */
    public BankServiceType getCardAttr() {
        return cardAttr;
    }

    /**
     * 设置银行卡类型
     * @param cardType 银行卡类型
     */
    public void setCardType(BankCardType cardType) {
        this.cardType = cardType;
    }

    /**
     * 设置银行卡属性
     * @param cardAttr 银行卡属性
     */
    public void setCardAttr(BankServiceType cardAttr) {
        this.cardAttr = cardAttr;
    }
    
    public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
        this.verified = verified;
    }
	
    public boolean isSecurity() {
        return isSecurity;
    }

    public void setSecurity(boolean isSecurity) {
        this.isSecurity = isSecurity;
    }
}
