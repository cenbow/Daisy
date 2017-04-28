/**
 * 
 */
package com.yourong.core.tc.model.biz;

import java.math.BigDecimal;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * @desc 转让合同对象
 * @author zhanghao
 * 2016年9月23日下午3:55:02
 */
public class TransferContractBiz extends AbstractBaseObject {

	private static final long serialVersionUID = 1L;
	
	private Long transactionId;
	/**
	 * 合同编号(和交易id保持一致)
	 * 
	 */
	private String contractId;
	
	private String preservationLink;
	
	
	private String firstName;
	private String firstIdentity;
	private String firstMobile;
	private String firstAddress;
	private String firstEmail;
	
	private String secondName;
	private String secondIdentity;
	private String secondMobile;
	private String secondAddress;
	private String secondEmail;
	
	private String originalBorrower;
	
	/**
	 * 合同形成日期
	 * 
	 */
	private String transactionTime;
	
	private String originalTransactionId;
	
	private String originalTransactionAmount;
	
	private String originalTransactionTime;//原交易形成时间
	
	private String originalAnnualizedRate;
	
	  /**收益类型**/
    private String originalProfitType;
	
	private String originalEndDate;
	
	private String originalResidualDays;
	
	private String residualDays;
	
	private String projectValue;
	
	private String transferRate;//转让手续费率
	
	private String transferRateFee;//转让手续费

	private String currentInterest ;//当期利息
	
	private String transferCurrentInterest ;//受让人对应的当期利息
	
	private String residualPrincipal ;//剩余本金
	
	private String transferAmount;
	
	private String annualizedRate;

	/** 投资金额 **/
	private String investAmount;
	/**
	 * 转让本金
	 */
	private String transferPrincipal;

	/**起息日后X个自然日可以转让**/
	private Integer transferAfterInterest;
	
	/**
	 * 抵押物详细描述
	 * 
	 */
	private String collateralDetails;

	//原合同期限
	private String originalContractPeriod;
	//合同期限
	private String contractPeriod;
	//预期赚取
	private String expectedEarning;
	
	 /**甲方是否签署（0否，1是）**/
    private Integer firstIsSign;
    /**乙方是否签署（0否，1是）**/
    private Integer secondIsSign;
    /**丙方是否签署（0否，1是）**/
    private Integer thirdIsSign;
    /**合同签署状态**/
    private Integer signStatus;
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
	 * @return the contractId
	 */
	public String getContractId() {
		return contractId;
	}
	/**
	 * @param contractId the contractId to set
	 */
	public void setContractId(String contractId) {
		this.contractId = contractId;
	}
	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}
	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	/**
	 * @return the firstIdentity
	 */
	public String getFirstIdentity() {
		return firstIdentity;
	}
	/**
	 * @param firstIdentity the firstIdentity to set
	 */
	public void setFirstIdentity(String firstIdentity) {
		this.firstIdentity = firstIdentity;
	}
	/**
	 * @return the firstMobile
	 */
	public String getFirstMobile() {
		return firstMobile;
	}
	/**
	 * @param firstMobile the firstMobile to set
	 */
	public void setFirstMobile(String firstMobile) {
		this.firstMobile = firstMobile;
	}
	/**
	 * @return the firstAddress
	 */
	public String getFirstAddress() {
		return firstAddress;
	}
	/**
	 * @param firstAddress the firstAddress to set
	 */
	public void setFirstAddress(String firstAddress) {
		this.firstAddress = firstAddress;
	}
	/**
	 * @return the firstEmail
	 */
	public String getFirstEmail() {
		return firstEmail;
	}
	/**
	 * @param firstEmail the firstEmail to set
	 */
	public void setFirstEmail(String firstEmail) {
		this.firstEmail = firstEmail;
	}
	/**
	 * @return the secondName
	 */
	public String getSecondName() {
		return secondName;
	}
	/**
	 * @param secondName the secondName to set
	 */
	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}
	/**
	 * @return the secondIdentity
	 */
	public String getSecondIdentity() {
		return secondIdentity;
	}
	/**
	 * @param secondIdentity the secondIdentity to set
	 */
	public void setSecondIdentity(String secondIdentity) {
		this.secondIdentity = secondIdentity;
	}
	/**
	 * @return the secondMobile
	 */
	public String getSecondMobile() {
		return secondMobile;
	}
	/**
	 * @param secondMobile the secondMobile to set
	 */
	public void setSecondMobile(String secondMobile) {
		this.secondMobile = secondMobile;
	}
	/**
	 * @return the secondAddress
	 */
	public String getSecondAddress() {
		return secondAddress;
	}
	/**
	 * @param secondAddress the secondAddress to set
	 */
	public void setSecondAddress(String secondAddress) {
		this.secondAddress = secondAddress;
	}
	/**
	 * @return the secondEmail
	 */
	public String getSecondEmail() {
		return secondEmail;
	}
	/**
	 * @param secondEmail the secondEmail to set
	 */
	public void setSecondEmail(String secondEmail) {
		this.secondEmail = secondEmail;
	}
	/**
	 * @return the transactionTime
	 */
	public String getTransactionTime() {
		return transactionTime;
	}
	/**
	 * @param transactionTime the transactionTime to set
	 */
	public void setTransactionTime(String transactionTime) {
		this.transactionTime = transactionTime;
	}
	/**
	 * @return the originalTransactionId
	 */
	public String getOriginalTransactionId() {
		return originalTransactionId;
	}
	/**
	 * @param originalTransactionId the originalTransactionId to set
	 */
	public void setOriginalTransactionId(String originalTransactionId) {
		this.originalTransactionId = originalTransactionId;
	}
	/**
	 * @return the originalTransactionAmount
	 */
	public String getOriginalTransactionAmount() {
		return originalTransactionAmount;
	}
	/**
	 * @param originalTransactionAmount the originalTransactionAmount to set
	 */
	public void setOriginalTransactionAmount(String originalTransactionAmount) {
		this.originalTransactionAmount = originalTransactionAmount;
	}
	/**
	 * @return the originalTransactionTime
	 */
	public String getOriginalTransactionTime() {
		return originalTransactionTime;
	}
	/**
	 * @param originalTransactionTime the originalTransactionTime to set
	 */
	public void setOriginalTransactionTime(String originalTransactionTime) {
		this.originalTransactionTime = originalTransactionTime;
	}
	/**
	 * @return the originalAnnualizedRate
	 */
	public String getOriginalAnnualizedRate() {
		return originalAnnualizedRate;
	}
	/**
	 * @param originalAnnualizedRate the originalAnnualizedRate to set
	 */
	public void setOriginalAnnualizedRate(String originalAnnualizedRate) {
		this.originalAnnualizedRate = originalAnnualizedRate;
	}
	/**
	 * @return the originalEndDate
	 */
	public String getOriginalEndDate() {
		return originalEndDate;
	}
	/**
	 * @param originalEndDate the originalEndDate to set
	 */
	public void setOriginalEndDate(String originalEndDate) {
		this.originalEndDate = originalEndDate;
	}
	/**
	 * @return the residualDays
	 */
	public String getResidualDays() {
		return residualDays;
	}
	/**
	 * @param residualDays the residualDays to set
	 */
	public void setResidualDays(String residualDays) {
		this.residualDays = residualDays;
	}
	/**
	 * @return the projectValue
	 */
	public String getProjectValue() {
		return projectValue;
	}
	/**
	 * @param projectValue the projectValue to set
	 */
	public void setProjectValue(String projectValue) {
		this.projectValue = projectValue;
	}
	/**
	 * @return the transferRate
	 */
	public String getTransferRate() {
		return transferRate;
	}
	/**
	 * @param transferRate the transferRate to set
	 */
	public void setTransferRate(String transferRate) {
		this.transferRate = transferRate;
	}
	/**
	 * @return the currentInterest
	 */
	public String getCurrentInterest() {
		return currentInterest;
	}
	/**
	 * @param currentInterest the currentInterest to set
	 */
	public void setCurrentInterest(String currentInterest) {
		this.currentInterest = currentInterest;
	}
	/**
	 * @return the residualPrincipal
	 */
	public String getResidualPrincipal() {
		return residualPrincipal;
	}
	/**
	 * @param residualPrincipal the residualPrincipal to set
	 */
	public void setResidualPrincipal(String residualPrincipal) {
		this.residualPrincipal = residualPrincipal;
	}
	/**
	 * @return the transferAmount
	 */
	public String getTransferAmount() {
		return transferAmount;
	}
	/**
	 * @param transferAmount the transferAmount to set
	 */
	public void setTransferAmount(String transferAmount) {
		this.transferAmount = transferAmount;
	}
	/**
	 * @return the firstIsSign
	 */
	public Integer getFirstIsSign() {
		return firstIsSign;
	}
	/**
	 * @param firstIsSign the firstIsSign to set
	 */
	public void setFirstIsSign(Integer firstIsSign) {
		this.firstIsSign = firstIsSign;
	}
	/**
	 * @return the secondIsSign
	 */
	public Integer getSecondIsSign() {
		return secondIsSign;
	}
	/**
	 * @param secondIsSign the secondIsSign to set
	 */
	public void setSecondIsSign(Integer secondIsSign) {
		this.secondIsSign = secondIsSign;
	}
	/**
	 * @return the thirdIsSign
	 */
	public Integer getThirdIsSign() {
		return thirdIsSign;
	}
	/**
	 * @param thirdIsSign the thirdIsSign to set
	 */
	public void setThirdIsSign(Integer thirdIsSign) {
		this.thirdIsSign = thirdIsSign;
	}
	/**
	 * @return the signStatus
	 */
	public Integer getSignStatus() {
		return signStatus;
	}
	/**
	 * @param signStatus the signStatus to set
	 */
	public void setSignStatus(Integer signStatus) {
		this.signStatus = signStatus;
	}
	/**
	 * @return the originalBorrower
	 */
	public String getOriginalBorrower() {
		return originalBorrower;
	}
	/**
	 * @param originalBorrower the originalBorrower to set
	 */
	public void setOriginalBorrower(String originalBorrower) {
		this.originalBorrower = originalBorrower;
	}
	/**
	 * @return the collateralDetails
	 */
	public String getCollateralDetails() {
		return collateralDetails;
	}
	/**
	 * @param collateralDetails the collateralDetails to set
	 */
	public void setCollateralDetails(String collateralDetails) {
		this.collateralDetails = collateralDetails;
	}
	/**
	 * @return the originalProfitType
	 */
	public String getOriginalProfitType() {
		return originalProfitType;
	}
	/**
	 * @param originalProfitType the originalProfitType to set
	 */
	public void setOriginalProfitType(String originalProfitType) {
		this.originalProfitType = originalProfitType;
	}
	/**
	 * @return the preservationLink
	 */
	public String getPreservationLink() {
		return preservationLink;
	}
	/**
	 * @param preservationLink the preservationLink to set
	 */
	public void setPreservationLink(String preservationLink) {
		this.preservationLink = preservationLink;
	}
	/**
	 * @return the originalContractPeriod
	 */
	public String getOriginalContractPeriod() {
		return originalContractPeriod;
	}
	/**
	 * @param originalContractPeriod the originalContractPeriod to set
	 */
	public void setOriginalContractPeriod(String originalContractPeriod) {
		this.originalContractPeriod = originalContractPeriod;
	}
	/**
	 * @return the contractPeriod
	 */
	public String getContractPeriod() {
		return contractPeriod;
	}
	/**
	 * @param contractPeriod the contractPeriod to set
	 */
	public void setContractPeriod(String contractPeriod) {
		this.contractPeriod = contractPeriod;
	}
	/**
	 * @return the expectedEarning
	 */
	public String getExpectedEarning() {
		return expectedEarning;
	}
	/**
	 * @param expectedEarning the expectedEarning to set
	 */
	public void setExpectedEarning(String expectedEarning) {
		this.expectedEarning = expectedEarning;
	}
	/**
	 * @return the transferRateFee
	 */
	public String getTransferRateFee() {
		return transferRateFee;
	}
	/**
	 * @param transferRateFee the transferRateFee to set
	 */
	public void setTransferRateFee(String transferRateFee) {
		this.transferRateFee = transferRateFee;
	}
	/**
	 * @return the transferPrincipal
	 */
	public String getTransferPrincipal() {
		return transferPrincipal;
	}
	/**
	 * @param transferPrincipal the transferPrincipal to set
	 */
	public void setTransferPrincipal(String transferPrincipal) {
		this.transferPrincipal = transferPrincipal;
	}

	public Integer getTransferAfterInterest() {
		return transferAfterInterest;
	}

	public void setTransferAfterInterest(Integer transferAfterInterest) {
		this.transferAfterInterest = transferAfterInterest;
	}

	/**
	 * @return the annualizedRate
	 */
	public String getAnnualizedRate() {
		return annualizedRate;
	}
	/**
	 * @param annualizedRate the annualizedRate to set
	 */
	public void setAnnualizedRate(String annualizedRate) {
		this.annualizedRate = annualizedRate;
	}
	/**
	 * @return the originalResidualDays
	 */
	public String getOriginalResidualDays() {
		return originalResidualDays;
	}
	/**
	 * @param originalResidualDays the originalResidualDays to set
	 */
	public void setOriginalResidualDays(String originalResidualDays) {
		this.originalResidualDays = originalResidualDays;
	}
	/**
	 * @return the investAmount
	 */
	public String getInvestAmount() {
		return investAmount;
	}
	/**
	 * @param investAmount the investAmount to set
	 */
	public void setInvestAmount(String investAmount) {
		this.investAmount = investAmount;
	}
	public String getTransferCurrentInterest() {
		return transferCurrentInterest;
	}
	public void setTransferCurrentInterest(String transferCurrentInterest) {
		this.transferCurrentInterest = transferCurrentInterest;
	}
	
}
