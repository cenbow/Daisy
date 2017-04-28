package com.yourong.api.dto;

import java.math.BigDecimal;

/**
 * 充值form
 * @author Administrator
 *
 */
public class ChargeDto {
    
    
    //会员ID
    private Long id; 
    
    //支付方式
    private String payMethod ;
    
    //支付金额
    private BigDecimal amount;
    
    //银行代码
    private String banlkCode;
    
    
    public String getBanlkCode() {
        return banlkCode;
    }
    public void setBanlkCode(String banlkCode) {
        this.banlkCode = banlkCode;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getPayMethod() {
        return payMethod;
    }
    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    
    
    
}
