package com.yourong.api.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.util.DateUtils;

public class PopularityInOutLogDto {
	/****/
    private Integer id;

    /**余额**/
    private BigDecimal balance;

    /**1-推荐好友；2-平台活动；3-平台派送 4-兑换优惠券 **/
    private Integer type;

    /**收入**/
    private BigDecimal income;

    /**支出**/
    private BigDecimal outlay;

    /**来源id(交易id，会员id等)**/
    private Long sourceId;

    /****/
    private String remark;

    /**发生时间**/
    private Date happenTime;

    private String typeName;
    
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public BigDecimal getIncome() {
		return income;
	}

	public void setIncome(BigDecimal income) {
		this.income = income;
	}

	public BigDecimal getOutlay() {
		return outlay;
	}

	public void setOutlay(BigDecimal outlay) {
		this.outlay = outlay;
	}

	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getHappenTime() {
		return happenTime;
	}

	public void setHappenTime(Date happenTime) {
		this.happenTime = happenTime;
	}
    
	public String getHappenTimeStr(){
		if (happenTime != null) {
			return DateUtils.formatDatetoString(happenTime,
					DateUtils.TIME_PATTERN);
		}
		return null;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
    
	
	
	
}
