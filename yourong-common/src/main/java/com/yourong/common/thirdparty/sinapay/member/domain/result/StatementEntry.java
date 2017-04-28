/**
 * 
 */
package com.yourong.common.thirdparty.sinapay.member.domain.result;

import java.io.Serializable;

import com.yourong.common.thirdparty.sinapay.common.domain.Money;
import com.yourong.common.util.StringUtil;

/**
 * 对账单条目
 * @version 0.1 2014年5月20日 上午11:29:27
 */
public class StatementEntry implements Serializable{

    private static final long serialVersionUID = 1L;
    
    //摘要
    private String summary;
    //入账时间
    private String recordedTime;
    //指示收入还是支出
    private String direction;
    //发生额
    private Money amount;
    //余额
    private Money balance;
    //存钱罐交易类型
    private String transactionType;
    /**
     * 
     * @param summary 摘要
     * @param recordedTime 入账时间
     * @param direction 加减方向
     * @param amount 发生额，保留两位小数
     * @param balance 交易后余额，保留两位小数
     * @throws IllegalArgumentException 参数为空，或者参数格式不正确
     */
    public StatementEntry(String summary, String recordedTime, String direction, Money amount, Money balance, String transactionType) {
        if(summary == null)
            throw new IllegalArgumentException("summary is required");
        
        if(recordedTime == null)
            throw new IllegalArgumentException("recordedTime is required");
        
        if(direction == null)
            throw new IllegalArgumentException("direction is required");
        
        if(amount == null)
            throw new IllegalArgumentException("amount is required");
        
        if(balance == null)
            throw new IllegalArgumentException("balance is required");
        
        if(StringUtil.isBlank(transactionType))
        	throw new IllegalArgumentException("transactionType is required");
        
        this.summary = summary;
        this.recordedTime = recordedTime;
        this.direction = direction;
        this.amount = amount;
        this.balance = balance;
        this.transactionType = transactionType;
    }
    /**
     * 摘要
     * @return not null
     */
    public String getSummary() {
        return summary;
    }
    
    /**
     * 入账时间, 格式yyyyMMddhhmmss
     * @return not null
     */
    public String getRecordedTime() {
        return recordedTime;
    }
    
    /**
     * 收支方向。
     * @return + 或 -
     */
    public String getDirection() {
        return direction;
    }
    
    /**
     * 发生额，精确到小数点后两位
     * @return not null
     */
    public Money getAmount() {
        return amount;
    }
    
    /**
     * 交易后余额，精确到小数点后两位
     * @return not null
     */
    public Money getBalance() {
        return balance;
    }
    
	public String getTransactionType() {
		return transactionType;
	}
	
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
    
    
}
