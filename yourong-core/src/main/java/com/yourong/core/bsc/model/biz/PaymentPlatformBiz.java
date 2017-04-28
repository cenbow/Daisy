package com.yourong.core.bsc.model.biz;

import java.math.BigDecimal;

import org.apache.ibatis.type.Alias;

@Alias("PaymentPlatformBiz")
public class PaymentPlatformBiz {
	 /**银行卡主键**/
    private Long bankId;

    /**限制类型: 1,网银; 2,快捷**/
    private Integer typeLimit;

    /**单笔限额**/
    private BigDecimal singleLimit;

    /**每日限额**/
    private BigDecimal dailyLimit;

    /**最低支付额度**/
    private BigDecimal minLimit;

    /**服务状态: 0,不可用; 1,可用;**/
    private Integer serviceStatus;
    
    /**银行简称**/
    private String simpleName;

    /**银行编码**/
    private String code;

	public Long getBankId() {
		return bankId;
	}

	public void setBankId(Long bankId) {
		this.bankId = bankId;
	}

	public Integer getTypeLimit() {
		return typeLimit;
	}

	public void setTypeLimit(Integer typeLimit) {
		this.typeLimit = typeLimit;
	}

	public BigDecimal getSingleLimit() {
		return singleLimit;
	}

	public void setSingleLimit(BigDecimal singleLimit) {
		this.singleLimit = singleLimit;
	}

	public BigDecimal getDailyLimit() {
		return dailyLimit;
	}

	public void setDailyLimit(BigDecimal dailyLimit) {
		this.dailyLimit = dailyLimit;
	}

	public BigDecimal getMinLimit() {
		return minLimit;
	}

	public void setMinLimit(BigDecimal minLimit) {
		this.minLimit = minLimit;
	}

	public Integer getServiceStatus() {
		return serviceStatus;
	}

	public void setServiceStatus(Integer serviceStatus) {
		this.serviceStatus = serviceStatus;
	}

	public String getSimpleName() {
		return simpleName;
	}

	public void setSimpleName(String simpleName) {
		this.simpleName = simpleName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
    
    
}
