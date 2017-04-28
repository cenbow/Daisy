/**
 * 
 */
package com.yourong.common.thirdparty.sinapay.member.domain.result;

import java.io.Serializable;

import com.yourong.common.thirdparty.sinapay.common.domain.Money;

/**
 * <p>资金账户余额
 * @version 0.1 2014年5月20日 上午11:22:55
 */
public class AccountBalance implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private Money balance;
    private Money available;
    /**
     * 昨日收益
     */
    private Money yestDayBonus;
    /**
     *  最近一周收益
     */
  //  private Money lastWeekBonus;
    /**
     * 最近一月收益
     */
    private Money lastMonthBonus;
    /**
     * 总收益
     */
    private Money bonus;
    /**
     * 
     * @param balance 账户余额
     * @param available 可用金额
     * @throws IllegalArgumentException 如果任一参数为null
     */
    public AccountBalance(Money balance, Money available) {
        if(balance == null)
            throw new IllegalArgumentException("balance is required");
        
        if(available == null)
            throw new IllegalArgumentException("available is required");
        
        this.balance = balance;
        this.available = available;
    }

    /**
     * 账户余额
     * @return not null
     */
    public Money getBalance() {
        return balance;
    }

    /**
     * 可用金额
     * @return not null
     */
    public Money getAvailable() {
        return available;
    }

    public void setBalance(Money balance) {
        this.balance = balance;
    }

    public void setAvailable(Money available) {
        this.available = available;
    }

   

    public Money getLastMonthBonus() {
    	if(lastMonthBonus == null){
    		lastMonthBonus = new Money("0.0");
    	}
        return lastMonthBonus;
    }

    public void setLastMonthBonus(Money lastMonthBonus) {
    	
        this.lastMonthBonus = lastMonthBonus;
    }

    public Money getBonus() {
    	if(bonus == null){
    		bonus = new Money("0.0");
    	}    	
        return bonus;
    }

    public void setBonus(Money bonus) {
        this.bonus = bonus;
    }

    public Money getYestDayBonus() {
    	if(yestDayBonus == null){
    		yestDayBonus = new Money("0.0");
    	}
        return yestDayBonus;
    }

    public void setYestDayBonus(Money yestDayBonus) {
        this.yestDayBonus = yestDayBonus;
    }


    @Override
    public String toString() {
        return "AccountBalance{" +
                "balance=" + balance +
                ", available=" + available +
                ", yestDayBonus=" + yestDayBonus +
                ", lastMonthBonus=" + lastMonthBonus +
                ", bonus=" + bonus +
                '}';
    }
}
