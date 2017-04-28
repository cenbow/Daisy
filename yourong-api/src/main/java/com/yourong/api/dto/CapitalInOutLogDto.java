package com.yourong.api.dto;

import java.math.BigDecimal;
import java.util.Date;

import org.codehaus.jackson.map.annotate.JsonFilter;

import com.alibaba.fastjson.annotation.JSONField;

public class CapitalInOutLogDto {

	/**1:充值  2:提现 3:充值手续费 4：提现手续费  5:用户投资 6：现金券支出 7：收益券支出 8:利息收支  9：本金收支 **/
    private Integer type;

    /**收入**/
    @JSONField(serialize=false)
    private BigDecimal income;

    /**支出**/
    @JSONField(serialize=false)
    private BigDecimal outlay;
    
    /**备注**/
    private String remark;

    /**发生时间**/
    private Date happenTime;
    
    /**余额**/
    private BigDecimal balance;
    
    /**类型描述**/
    private String typeDesc;

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

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
   
	/**
	 * 交易类型
	 * @return
	 */
	public int getTransactionType(){
		if(getIncome() != null && getIncome().compareTo(BigDecimal.ZERO) > 0){
			return 1;
		}
		return 2;
	}
	
	/**
	 * 交易金额
	 * @return
	 */
	public BigDecimal getAmount(){
		if(getTransactionType() == 1){
			return getIncome();
		}
		return getOutlay();
	}
	
	public String getTypeDesc() {
		return typeDesc;
	}

	public void setTypeDesc(String typeDesc) {
		this.typeDesc = typeDesc;
	}
    
}
