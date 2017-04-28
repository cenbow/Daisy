/**
 * 
 */
package com.yourong.common.thirdparty.sinapay.member.domain.dto;

import java.io.Serializable;
import java.util.Map;

import com.yourong.common.thirdparty.sinapay.common.annotations.NotNull;
import com.yourong.common.thirdparty.sinapay.common.enums.BankCardType;
import com.yourong.common.thirdparty.sinapay.common.enums.BankCode;
import com.yourong.common.thirdparty.sinapay.common.enums.BankServiceType;
import com.yourong.common.thirdparty.sinapay.common.enums.CredentialsType;
import com.yourong.common.thirdparty.sinapay.common.enums.IdType;
import com.yourong.common.thirdparty.sinapay.common.enums.VerifyMode;

/**
 * 钱包账户绑定的银行卡信息
 * @version 0.1 2014年5月20日 上午11:08:45
 */
public class BindingBankCardDto extends RequestDto implements Serializable {

    private static final long   serialVersionUID = 1L;

    /***
     * 绑卡流水号
     */
    private String requestNo;

    /**
     * 用户标识信息
     * 必填
     */
    @NotNull
    private String              identityId;

    /**
     * 用户标识类型
     * 必填
     */
    @NotNull
    private IdType              identityType;

    /**
     * 银行编号
     * 必填
     */
    @NotNull
    private BankCode            bankCode;
    /**
     * 银行卡号
     * 必填
     */
    @NotNull
    private String              bankAccountNo;
    /**
     * 户名
     * 必填
     */
    private String              accountName;

    /**
     * 卡类型
     */
    @NotNull
    private BankCardType        cardType;
    /**
     * 卡属性
     */
    @NotNull
    private BankServiceType     cardAttr;
    /**
     * 证件类型
     */
    private CredentialsType     certType;

    /**
     * 证件号码
     */
    private String              certNo;

    /**
     * 手机号
     */
    private String              phoneNo;

    /**
     * 有效期
     */
    private String              validityPeriod;

    /**
     * CVV2
     */
    private String              verificationValue;

    /**
     * 省份
     */
    private String              province;

    /**
     * 城市
     */
    private String              city;

    /**
     * 支行名称
     */
    private String              bankBranch;

    /**
     * 认证方式
     */
    private VerifyMode          verifyMode;
    
	/**会员绑卡IP**/
	private String              userBindIp;
	
    /**
     * 扩展信息
     */
    private Map<String, String> extendParam;

  

    public String getRequestNo() {
		return requestNo;
	}

	public void setRequestNo(String requestNo) {
		this.requestNo = requestNo;
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

    public BankCode getBankCode() {
        return bankCode;
    }

    public void setBankCode(BankCode bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankAccountNo() {
        return bankAccountNo;
    }

    public void setBankAccountNo(String bankAccountNo) {
        this.bankAccountNo = bankAccountNo;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public BankCardType getCardType() {
        return cardType;
    }

    public void setCardType(BankCardType cardType) {
        this.cardType = cardType;
    }

    public BankServiceType getCardAttr() {
        return cardAttr;
    }

    public void setCardAttr(BankServiceType cardAttr) {
        this.cardAttr = cardAttr;
    }

    public CredentialsType getCertType() {
        return certType;
    }

    public void setCertType(CredentialsType certType) {
        this.certType = certType;
    }

    public String getCertNo() {
        return certNo;
    }

    public void setCertNo(String certNo) {
        this.certNo = certNo;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(String validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public String getVerificationValue() {
        return verificationValue;
    }

    public void setVerificationValue(String verificationValue) {
        this.verificationValue = verificationValue;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBankBranch() {
        return bankBranch;
    }

    public void setBankBranch(String bankBranch) {
        this.bankBranch = bankBranch;
    }

    public VerifyMode getVerifyMode() {
        return verifyMode;
    }

    public void setVerifyMode(VerifyMode verifyMode) {
        this.verifyMode = verifyMode;
    }
    
	public String getUserBindIp() {
		return userBindIp;
	}

	public void setUserBindIp(String userBindIp) {
		this.userBindIp = userBindIp;
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
		builder.append("BindingBankCardDto [identityId=");
		builder.append(identityId);
		builder.append(", identityType=");
		builder.append(identityType);
		builder.append(", bankCode=");
		builder.append(bankCode);
		builder.append(", bankAccountNo=");
		builder.append(bankAccountNo);
		builder.append(", accountName=");
		builder.append(accountName);
		builder.append(", cardType=");
		builder.append(cardType);
		builder.append(", cardAttr=");
		builder.append(cardAttr);
		builder.append(", certType=");
		builder.append(certType);
		builder.append(", certNo=");
		builder.append(certNo);
		builder.append(", phoneNo=");
		builder.append(phoneNo);
		builder.append(", validityPeriod=");
		builder.append(validityPeriod);
		builder.append(", verificationValue=");
		builder.append(verificationValue);
		builder.append(", province=");
		builder.append(province);
		builder.append(", city=");
		builder.append(city);
		builder.append(", bankBranch=");
		builder.append(bankBranch);
		builder.append(", verifyMode=");
		builder.append(verifyMode);
		builder.append(", userBindIp=");
		builder.append(userBindIp);
		builder.append(", extendParam=");
		builder.append(extendParam);
		builder.append("]");
		return builder.toString();
	}

}
