package com.yourong.core.uc.model;

import java.util.Date;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.util.StringUtil;

public class MemberBankCard extends AbstractBaseObject {
    /**
	 * 
	 */
	private static final long serialVersionUID = -7121742824277896460L;

	/****/
    private Long id;

    /**会员id**/
    private Long memberId;

    /**银行卡号**/
    private String cardNumber;

    /**持卡人**/
    private String cardHolder;

    ///**银行id**/
    //private Integer bankId;
    
    /**银行唯一code**/
    private String bankCode;

    /**银行所属省份**/
    private String bankProvince;

    /**银行所属城市**/
    private String bankCity;

    /**银行所属所在区**/
    private String bankDistrict;

    /**开卡行所属开户行(分行)的名称**/
    private String branchName;

    /**开卡行所属支行的名称**/
    private String subBranchName;

    /**是否为默认银行卡(0-不是 1-是)**/
    private Integer isDefault = 0;
    
    /****/
    private Date createTime;

    /****/
    private Date updateTime;

    /**-1-已删除的银行卡，1-正常绑定成功**/
    private Integer delFlag;

    /**备注**/
    private String remarks;
    

    /**外部sourceId**/
    private String outerSourceId;

	/****银行预留手机号码***/
	private Long  bankMobile;
	/******卡类型，1一般卡，2快捷支付卡**/
	private int cardType;

	private String simpleName;
	
	/**是否为安全卡(0-不是 1-是)**/
	private Integer isSecurity;
	
	private boolean isUpgradeSecurity=false;

	private String trueName;
	
	/**会员绑卡IP**/
	private String userBindIp;
	
	/**会员解绑卡IP**/
	private String userUnBindIp;
	
	/**
     * 是否新浪添加的银行卡
     */
    private Integer isSinaAdded;
	
	public int getCardType() {
		return cardType;
	}

	public void setCardType(int cardType) {
		this.cardType = cardType;
	}

	public Long getBankMobile() {

		return bankMobile;
	}

	public Integer getIsSecurity() {
		return isSecurity;
	}

	public void setIsSecurity(Integer isSecurity) {
		this.isSecurity = isSecurity;
	}

	public void setBankMobile(Long bankMobile) {
		this.bankMobile = bankMobile;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber == null ? null : cardNumber.trim();
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(String cardHolder) {
        this.cardHolder = cardHolder == null ? null : cardHolder.trim();
    }

    /*
    public Integer getBankId() {
        return bankId;
    }

    public void setBankId(Integer bankId) {
        this.bankId = bankId;
    }
    */

    public String getBankProvince() {
        return bankProvince;
    }

    public void setBankProvince(String bankProvince) {
        this.bankProvince = bankProvince == null ? null : bankProvince.trim();
    }

    public String getBankCity() {
        return bankCity;
    }

    public void setBankCity(String bankCity) {
        this.bankCity = bankCity == null ? null : bankCity.trim();
    }

    public String getBankDistrict() {
        return bankDistrict;
    }

    public void setBankDistrict(String bankDistrict) {
        this.bankDistrict = bankDistrict == null ? null : bankDistrict.trim();
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName == null ? null : branchName.trim();
    }

    public String getSubBranchName() {
        return subBranchName;
    }

    public void setSubBranchName(String subBranchName) {
        this.subBranchName = subBranchName == null ? null : subBranchName.trim();
    }

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks == null ? null : remarks.trim();
    }

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	
	

	public String getOuterSourceId() {
		return outerSourceId;
	}

	public void setOuterSourceId(String outerSourceId) {
		this.outerSourceId = outerSourceId;
	}

	public boolean isUpgradeSecurity() {
		return isUpgradeSecurity;
	}

	public void setUpgradeSecurity(boolean isUpgradeSecurity) {
		this.isUpgradeSecurity = isUpgradeSecurity;
	}

	public String getSimpleName() {
		return simpleName;
	}

	public void setSimpleName(String simpleName) {
		this.simpleName = simpleName;
	}

	public String getTrueName() {
		return trueName;
	}

	public void setTrueName(String trueName) {
		this.trueName = trueName;
	}
    
    public String getCardNumberFormat() {
    	if(StringUtil.isNotBlank(this.cardNumber)) {
//    		return StringUtil.maskBankCodeNumberEnd(this.cardNumber);
    		return StringUtil.maskFormatBankCodeNumberWithBlank(this.cardNumber,4);
    	}
    	return "";
    }

	public String getUserBindIp() {
		return userBindIp;
	}

	public void setUserBindIp(String userBindIp) {
		this.userBindIp = userBindIp;
	}

	public String getUserUnBindIp() {
		return userUnBindIp;
	}

	public void setUserUnBindIp(String userUnBindIp) {
		this.userUnBindIp = userUnBindIp;
	}

	public Integer getIsSinaAdded() {
		return isSinaAdded;
	}

	public void setIsSinaAdded(Integer isSinaAdded) {
		this.isSinaAdded = isSinaAdded;
	}
	
}