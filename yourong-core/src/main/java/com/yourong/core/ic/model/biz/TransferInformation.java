/**
 * 
 */
package com.yourong.core.ic.model.biz;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.yourong.common.constant.Constant;
import com.yourong.common.domain.AbstractBaseObject;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年9月12日下午2:10:05
 */
public class TransferInformation extends AbstractBaseObject{

	private static final long serialVersionUID = 1L;

	
    /**交易id**/
    private Long transactionId;
    
    /**剩余本金**/
    private BigDecimal residualPrincipal;
    
    /**剩余收益**/
    private BigDecimal residualInterest;
    
    /**转让剩余时间**/
    private Integer remainingTime;
    
    /**转让手续费率 */
	private BigDecimal transferRate;
    
    /**转让项目名称**/
    private String transferProjectName;
    
    /** 转让时间  小时数**/
    private Integer transferTime;
    
    /** 持有天数**/
    private Integer holdDays;
    
    /**转让价格**/
    private BigDecimal   transferAmount; 
    
    /**转让时间**/
    private Date   transferDate; 
    
    /**流标时间**/
    private Date   failDate; 
    
    /**当期利息**/
    private BigDecimal currentInterest;
    
    /**最终获得**/
    private BigDecimal totalIncome;
    
    
    /**投资进度**/
    private String investmentProgress;
    
    /**状态（30：投资中，50：已满额，51：转让中，52：已转让,70：已还款,80：流标中,90：流标,转让失败）**/
    private Integer status;

    /**原交易投资类型（1-债权；2-直投）**/
    private Integer investType; 
    
    /**备注**/
    private String remarks;
    
    private List<TransferRateList> transferRateList;

	/**
	 * @return the transactionId
	 */
	public Long getTransactionId() {
		return transactionId;
	}

	/**
	 * @param transactionId the transactionId to set
	 */
	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	/**
	 * @return the residualPrincipal
	 */
	public BigDecimal getResidualPrincipal() {
		return residualPrincipal;
	}

	/**
	 * @param residualPrincipal the residualPrincipal to set
	 */
	public void setResidualPrincipal(BigDecimal residualPrincipal) {
		this.residualPrincipal = residualPrincipal;
	}

	/**
	 * @return the currentInterest
	 */
	public BigDecimal getCurrentInterest() {
		return currentInterest;
	}

	/**
	 * @param currentInterest the currentInterest to set
	 */
	public void setCurrentInterest(BigDecimal currentInterest) {
		this.currentInterest = currentInterest;
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
	 * @return the investmentProgress
	 */
	public String getInvestmentProgress() {
		return investmentProgress;
	}

	/**
	 * @param investmentProgress the investmentProgress to set
	 */
	public void setInvestmentProgress(String investmentProgress) {
		this.investmentProgress = investmentProgress;
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
	 * @return the transferDate
	 */
	public Date getTransferDate() {
		return transferDate;
	}

	/**
	 * @param transferDate the transferDate to set
	 */
	public void setTransferDate(Date transferDate) {
		this.transferDate = transferDate;
	}

	/**
	 * @return the failDate
	 */
	public Date getFailDate() {
		return failDate;
	}

	/**
	 * @param failDate the failDate to set
	 */
	public void setFailDate(Date failDate) {
		this.failDate = failDate;
	}

	/**
	 * @return the totalIncome
	 */
	public BigDecimal getTotalIncome() {
		return totalIncome;
	}

	/**
	 * @param totalIncome the totalIncome to set
	 */
	public void setTotalIncome(BigDecimal totalIncome) {
		this.totalIncome = totalIncome;
	}
	
	/**
	 * 
	 * @Description:合同名称
	 * @return
	 * @author: zhanghao
	 * @time:2016年7月14日 上午11:14:33
	 */
	public String getContractTitle() {
		if(investType==null){
			return "";
		}
		if (2 == this.investType) {
			return Constant.CONTRACT_TRANSFER_P2P_TILE+"范本";
		}
		return Constant.CONTRACT_TRANSFER_DEBT_TILE+"范本";
	}

	/**
	 * @return the investType
	 */
	public Integer getInvestType() {
		return investType;
	}

	/**
	 * @param investType the investType to set
	 */
	public void setInvestType(Integer investType) {
		this.investType = investType;
	}

	public String getTransferProjectName() {
		return transferProjectName;
	}

	public void setTransferProjectName(String transferProjectName) {
		this.transferProjectName = transferProjectName;
	}

	/**
	 * @return the residualInterest
	 */
	public BigDecimal getResidualInterest() {
		return residualInterest;
	}

	/**
	 * @param residualInterest the residualInterest to set
	 */
	public void setResidualInterest(BigDecimal residualInterest) {
		this.residualInterest = residualInterest;
	}

	/**
	 * @return the remainingTime
	 */
	public Integer getRemainingTime() {
		return remainingTime;
	}

	/**
	 * @param remainingTime the remainingTime to set
	 */
	public void setRemainingTime(Integer remainingTime) {
		this.remainingTime = remainingTime;
	}

	/**
	 * @return the transferTime
	 */
	public Integer getTransferTime() {
		return transferTime;
	}

	/**
	 * @param transferTime the transferTime to set
	 */
	public void setTransferTime(Integer transferTime) {
		this.transferTime = transferTime;
	}

	/**
	 * @return the holdDays
	 */
	public Integer getHoldDays() {
		return holdDays;
	}

	/**
	 * @param holdDays the holdDays to set
	 */
	public void setHoldDays(Integer holdDays) {
		this.holdDays = holdDays;
	}

	/**
	 * @return the transferRateList
	 */
	public List<TransferRateList> getTransferRateList() {
		return transferRateList;
	}

	/**
	 * @param transferRateList the transferRateList to set
	 */
	public void setTransferRateList(List<TransferRateList> transferRateList) {
		this.transferRateList = transferRateList;
	}
	
	
}
