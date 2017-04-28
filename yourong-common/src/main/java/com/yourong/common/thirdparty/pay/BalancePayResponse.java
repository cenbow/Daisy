package com.yourong.common.thirdparty.pay;

import java.math.BigDecimal;

import com.yourong.common.domain.AbstractBaseObject;

public class BalancePayResponse extends AbstractBaseObject {    
    private static final long serialVersionUID = 6968359048382179071L;
    //余额
    private String balance;
    //可用余额
    private String available_balance;
    
    public String getBalance() {
        return balance;
    }
    public BigDecimal getBalanceBigDecimal(){
	return new BigDecimal(balance);
    }
    
    public void setBalance(String balance) {
        this.balance = balance;
    }
    
    public String getAvailable_balance() {
        return available_balance;
    }
    
    public BigDecimal getAvailableBalanceBigDecimal() {
	return new BigDecimal(available_balance);
    }
    
    public void setAvailable_balance(String available_balance) {
        this.available_balance = available_balance;
    }
    
    
}
