package com.yourong.core.tc.model.biz;

import java.math.BigDecimal;
import java.util.List;

import com.yourong.common.util.FormulaUtil;

/**
 * 合同biz
 * 
 * @author Leon
 * @create 2013-11-4
 */
public class ContractForProjectBiz {
	
	private Long transactionId;
	/**
	 * 合同编号(和交易id保持一致)
	 * 
	 */
	private String contractId;

	/**
	 * 新债权人
	 * 
	 */
	private String name;

	/**
	 * 身份证
	 * 
	 */
	private String identity;

	/**
	 * 手机号
	 * 
	 */
	private String mobile;

	/**
	 * 债权本金
	 * 
	 */
	private String debtAmount;

	/**
	 * 债权形成日期
	 * 
	 */
	private String debtStartTime;

	/**
	 * 债权年化利率
	 * 
	 */
	private String debtAnnualizedRate;

	/**
	 * 合同期限
	 * 
	 */
	private String debtEndTime;

	/**
	 * 合同剩余天数
	 */
	private Integer remainDays;

	/**
	 * 抵押物详细描述
	 * 
	 */
	private String collateralDetails;

	/**
	 * 本金额
	 */
	private double principalAmount;
	
	/**
	 * 用户债权额
	 */
	private double userDebtAmount;
	
	/**
	 * 用户投资额
	 */
	private double userInvestAmount;
	
	/**
	 * 用户获得利息总额
	 */
	private double userInterestTotal;
	
	/**
	 * 价格
	 */
	private double price;
	
	/**
	 * 价格中文大写
	 */
	private String priceCap;
	
	/**
	 * 用户年收益率
	 */
	private String annualizedRate;
	
	/**
	 * 付息表
	 */
	private List<ContractTransactionInterest> transactionInterestList;
	
	/**
	 * 原始债权人姓名
	 */
	private String originalCreditorName;
	/**
	 * 原始债权人身份证
	 */
	private String originalCreditorIdentityNumber;
	/**
	 * 原始债权人手机
	 */
	private String originalCreditorPhone;
	/**
	 * 原始债权人地址
	 */
	private String originalCreditorAddress;
	
	private String investDate;
	
	/**
     *收益类型
     */
    private String profitTypeCode;
    
    /**
     *债权类型
     */
    private String debtTypeCode;
    
    private String sealUrl;
    
    private String originalDebtNumber;
    
    private String preservationLink;
    
    /**
     * 绿狗合同投资人串
     */
    private String users;

    public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	/**
     * 合同图片路径
     * @return
     */
	public String getContractId() {
		return contractId;
	}

	public void setContractId(String contractId) {
		this.contractId = contractId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getDebtAmount() {
		return debtAmount;
	}

	public void setDebtAmount(String debtAmount) {
		this.debtAmount = debtAmount;
	}

	public String getDebtStartTime() {
		return debtStartTime;
	}

	public void setDebtStartTime(String debtStartTime) {
		this.debtStartTime = debtStartTime;
	}

	public String getDebtAnnualizedRate() {
		return debtAnnualizedRate;
	}

	public void setDebtAnnualizedRate(String debtAnnualizedRate) {
		this.debtAnnualizedRate = debtAnnualizedRate;
	}

	public String getDebtEndTime() {
		return debtEndTime;
	}

	public void setDebtEndTime(String debtEndTime) {
		this.debtEndTime = debtEndTime;
	}

	public Integer getRemainDays() {
		return remainDays;
	}

	public void setRemainDays(Integer remainDays) {
		this.remainDays = remainDays;
	}

	public String getCollateralDetails() {
		return collateralDetails;
	}

	public void setCollateralDetails(String collateralDetails) {
		this.collateralDetails = collateralDetails;
	}

	public double getPrincipalAmount() {
		return principalAmount;
	}

	public void setPrincipalAmount(double principalAmount) {
		this.principalAmount = principalAmount;
	}

	public double getUserDebtAmount() {
		return userDebtAmount;
	}

	public void setUserDebtAmount(double userDebtAmount) {
		this.userDebtAmount = userDebtAmount;
	}
	

	public double getUserInvestAmount() {
		return userInvestAmount;
	}

	public void setUserInvestAmount(double userInvestAmount) {
		this.userInvestAmount = userInvestAmount;
	}

	public double getUserInterestTotal() {
		return userInterestTotal;
	}

	public void setUserInterestTotal(double userInterestTotal) {
		this.userInterestTotal = userInterestTotal;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getPriceCap() {
		return priceCap;
	}

	public void setPriceCap(String priceCap) {
		this.priceCap = priceCap;
	}

	public String getAnnualizedRate() {
		return annualizedRate;
	}

	public void setAnnualizedRate(String annualizedRate) {
		this.annualizedRate = annualizedRate;
	}

	public List<ContractTransactionInterest> getTransactionInterestList() {
		return transactionInterestList;
	}

	public void setTransactionInterestList(
			List<ContractTransactionInterest> transactionInterestList) {
		this.transactionInterestList = transactionInterestList;
	}

	public String getOriginalCreditorName() {
		return originalCreditorName;
	}

	public void setOriginalCreditorName(String originalCreditorName) {
		this.originalCreditorName = originalCreditorName;
	}

	public String getOriginalCreditorIdentityNumber() {
		return originalCreditorIdentityNumber;
	}

	public void setOriginalCreditorIdentityNumber(
			String originalCreditorIdentityNumber) {
		this.originalCreditorIdentityNumber = originalCreditorIdentityNumber;
	}

	public String getOriginalCreditorPhone() {
		return originalCreditorPhone;
	}

	public void setOriginalCreditorPhone(String originalCreditorPhone) {
		this.originalCreditorPhone = originalCreditorPhone;
	}

	public String getOriginalCreditorAddress() {
		return originalCreditorAddress;
	}

	public void setOriginalCreditorAddress(String originalCreditorAddress) {
		this.originalCreditorAddress = originalCreditorAddress;
	}

	public String getInvestDate() {
		return investDate;
	}

	public void setInvestDate(String investDate) {
		this.investDate = investDate;
	}

	public String getProfitTypeCode() {
		return profitTypeCode;
	}

	public void setProfitTypeCode(String profitTypeCode) {
		this.profitTypeCode = profitTypeCode;
	}

	public String getDebtTypeCode() {
		return debtTypeCode;
	}

	public void setDebtTypeCode(String debtTypeCode) {
		this.debtTypeCode = debtTypeCode;
	}

	public String getSealUrl() {
		return sealUrl;
	}

	public void setSealUrl(String sealUrl) {
		this.sealUrl = sealUrl;
	}

	public String getOriginalDebtNumber() {
		return originalDebtNumber;
	}

	public void setOriginalDebtNumber(String originalDebtNumber) {
		this.originalDebtNumber = originalDebtNumber;
	}

	public String getPreservationLink() {
		return preservationLink;
	}

	public void setPreservationLink(String preservationLink) {
		this.preservationLink = preservationLink;
	}

	
	public String getUserDebtAmountStr(){
		return FormulaUtil.getFormatPriceRound(new BigDecimal(userDebtAmount));
	}

	public String getUserInvestAmountStr(){
		return FormulaUtil.getFormatPriceRound(new BigDecimal(userInvestAmount));
	}
	
	public String getUserInterestTotalStr(){
		return FormulaUtil.getFormatPriceRound(new BigDecimal(userInterestTotal));
	}
	
	public String getPriceStr(){
		return FormulaUtil.getFormatPriceRound(new BigDecimal(price));
	}

	public String getUsers() {
		return users;
	}

	public void setUsers(String users) {
		this.users = users;
	}
}