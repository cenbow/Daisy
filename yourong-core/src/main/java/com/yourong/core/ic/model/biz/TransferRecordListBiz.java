/**
 * 
 */
package com.yourong.core.ic.model.biz;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年12月19日上午10:09:10
 */
public class TransferRecordListBiz extends AbstractBaseObject{

private static final long serialVersionUID = 1L;
	
	/**转让项目id**/
    private Long id;
	
	 /**转让开始时间**/
    private Date transferStartDate;
    
    /**转让结束时间**/
    private Date transferEndDate;
    
    /**转让成功时间*/
    private Date transferSaleComplatedTime;
    
    /**转让剩余时间**/
    private String remainingTime;
    
    /**转让价格**/
    private BigDecimal transferAmount;
    
    /**折价**/
    private BigDecimal discount;
    
    /**状态（30：投资中，52：已转让,70：已还款,90：流标）**/
    private Integer status;
    
    /**0-未失败，1-流标，2-撤销**/
    private Integer failFlag;
    
    /**流标时间**/
    private Date failTime;

    /**备注**/
    private String remarks;
    
    /**转让手续费率 */
	private BigDecimal transferRate;

	/**已转让本金**/
    private BigDecimal 	transferPrincipal;
    
    /**累计获取**/
    private BigDecimal 	transferIncome;
    
    /**转让手续费 */
	private BigDecimal transferFee;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the transferStartDate
	 */
	public Date getTransferStartDate() {
		return transferStartDate;
	}

	/**
	 * @param transferStartDate the transferStartDate to set
	 */
	public void setTransferStartDate(Date transferStartDate) {
		this.transferStartDate = transferStartDate;
	}

	/**
	 * @return the transferEndDate
	 */
	public Date getTransferEndDate() {
		return transferEndDate;
	}

	/**
	 * @param transferEndDate the transferEndDate to set
	 */
	public void setTransferEndDate(Date transferEndDate) {
		this.transferEndDate = transferEndDate;
	}

	/**
	 * @return the transferSaleComplatedTime
	 */
	public Date getTransferSaleComplatedTime() {
		return transferSaleComplatedTime;
	}

	/**
	 * @param transferSaleComplatedTime the transferSaleComplatedTime to set
	 */
	public void setTransferSaleComplatedTime(Date transferSaleComplatedTime) {
		this.transferSaleComplatedTime = transferSaleComplatedTime;
	}
	
	
	/**
	 * @return the remainingTime
	 */
	public String getRemainingTime() {
		return remainingTime;
	}

	/**
	 * @param remainingTime the remainingTime to set
	 */
	public void setRemainingTime(String remainingTime) {
		this.remainingTime = remainingTime;
	}

	/**
	 * @return the transferAmount
	 */
	public BigDecimal getTransferAmount() {
		return transferAmount;
	}

	/**
	 * @param transferAmount the transferAmount to set
	 */
	public void setTransferAmount(BigDecimal transferAmount) {
		this.transferAmount = transferAmount;
	}

	/**
	 * @return the discount
	 */
	public BigDecimal getDiscount() {
		return discount;
	}

	/**
	 * @param discount the discount to set
	 */
	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	/**
	 * @return the status
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * @return the failFlag
	 */
	public Integer getFailFlag() {
		return failFlag;
	}

	/**
	 * @param failFlag the failFlag to set
	 */
	public void setFailFlag(Integer failFlag) {
		this.failFlag = failFlag;
	}

	/**
	 * @return the failTime
	 */
	public Date getFailTime() {
		return failTime;
	}

	/**
	 * @param failTime the failTime to set
	 */
	public void setFailTime(Date failTime) {
		this.failTime = failTime;
	}

	/**
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}

	/**
	 * @param remarks the remarks to set
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	/**
	 * @return the transferRate
	 */
	public BigDecimal getTransferRate() {
		return transferRate;
	}

	/**
	 * @param transferRate the transferRate to set
	 */
	public void setTransferRate(BigDecimal transferRate) {
		this.transferRate = transferRate;
	}

	/**
	 * @return the transferPrincipal
	 */
	public BigDecimal getTransferPrincipal() {
		return transferPrincipal;
	}

	/**
	 * @param transferPrincipal the transferPrincipal to set
	 */
	public void setTransferPrincipal(BigDecimal transferPrincipal) {
		this.transferPrincipal = transferPrincipal;
	}

	/**
	 * @return the transferIncome
	 */
	public BigDecimal getTransferIncome() {
		return transferIncome;
	}

	/**
	 * @param transferIncome the transferIncome to set
	 */
	public void setTransferIncome(BigDecimal transferIncome) {
		this.transferIncome = transferIncome;
	}

	/**
	 * @return the transferFee
	 */
	public BigDecimal getTransferFee() {
		return transferFee;
	}

	/**
	 * @param transferFee the transferFee to set
	 */
	public void setTransferFee(BigDecimal transferFee) {
		this.transferFee = transferFee;
	}
	
}
