package com.yourong.core.fin.model.biz;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class BalanceBiz implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2827208227226428074L;

    /**总余额**/
    private BigDecimal balance;

    /**可用余额**/
    private BigDecimal availableBalance;
    
    
    private boolean flag=false;
    
    
    private BigDecimal channelBalance;
    
    
    private BigDecimal channelAvailableBalance;

    private String borrowerName;
    
    private String channelName;
    
    

	/**
	 * @return the borrowerName
	 */
	public String getBorrowerName() {
		return borrowerName;
	}


	/**
	 * @param borrowerName the borrowerName to set
	 */
	public void setBorrowerName(String borrowerName) {
		this.borrowerName = borrowerName;
	}


	/**
	 * @return the channelName
	 */
	public String getChannelName() {
		return channelName;
	}


	/**
	 * @param channelName the channelName to set
	 */
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}


	/**
	 * @return the balance
	 */
	public BigDecimal getBalance() {
		return balance;
	}


	/**
	 * @param balance the balance to set
	 */
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}


	/**
	 * @return the availableBalance
	 */
	public BigDecimal getAvailableBalance() {
		return availableBalance;
	}


	/**
	 * @param availableBalance the availableBalance to set
	 */
	public void setAvailableBalance(BigDecimal availableBalance) {
		this.availableBalance = availableBalance;
	}


	/**
	 * @return the flag
	 */
	public boolean isFlag() {
		return flag;
	}


	/**
	 * @param flag the flag to set
	 */
	public void setFlag(boolean flag) {
		this.flag = flag;
	}


	/**
	 * @return the channelBalance
	 */
	public BigDecimal getChannelBalance() {
		return channelBalance;
	}


	/**
	 * @param channelBalance the channelBalance to set
	 */
	public void setChannelBalance(BigDecimal channelBalance) {
		this.channelBalance = channelBalance;
	}


	/**
	 * @return the channelAvailableBalance
	 */
	public BigDecimal getChannelAvailableBalance() {
		return channelAvailableBalance;
	}


	/**
	 * @param channelAvailableBalance the channelAvailableBalance to set
	 */
	public void setChannelAvailableBalance(BigDecimal channelAvailableBalance) {
		this.channelAvailableBalance = channelAvailableBalance;
	}
    
    
}