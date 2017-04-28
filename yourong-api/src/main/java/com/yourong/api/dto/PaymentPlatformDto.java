package com.yourong.api.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang3.RandomUtils;

import com.alibaba.fastjson.annotation.JSONField;
import com.yourong.common.util.FormulaUtil;

public class PaymentPlatformDto {
	
	/**银行编码**/
	private String code;
	
	 /**限制类型: 1,网银; 2,快捷**/
	@JSONField(serialize=false)
    private Integer typeLimit;

    /**单笔限额**/
	@JSONField(serialize=false)
    private BigDecimal singleLimit;

    /**每日限额**/
	@JSONField(serialize=false)
    private BigDecimal dailyLimit;
    
    /**最低支付额度**/
    private BigDecimal minLimit;
    
    /**服务状态: 0,不可用; 1,可用;**/
    @JSONField(serialize=false)
    private Integer serviceStatus;

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

	public Integer getServiceStatus() {
		return serviceStatus;
	}

	public void setServiceStatus(Integer serviceStatus) {
		this.serviceStatus = serviceStatus;
	}
    
	public BigDecimal getMinLimit() {
		return minLimit;
	}

	public void setMinLimit(BigDecimal minLimit) {
		this.minLimit = minLimit;
	}

	public boolean isDisable(){
		if(typeLimit != null && typeLimit == 2 && serviceStatus != null && serviceStatus == 0){
			return true;
		}
		return false;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public String getSingleLimitStr(){
		if(singleLimit != null){
			return FormulaUtil.getFormatPriceNoSep(singleLimit.divide(new BigDecimal(10000)));
		}
		return "";
	}
	
	public String getDailyLimitStr() {
		if(dailyLimit != null){
			return FormulaUtil.getFormatPriceNoSep(dailyLimit.divide(new BigDecimal(10000)));
		}
		return "";
	}
}
