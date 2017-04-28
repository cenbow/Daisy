package com.yourong.core.tc.model.biz;

import java.math.BigDecimal;
import java.util.List;

import com.yourong.common.enums.DebtEnum;
import com.yourong.common.enums.ProjectEnum;
import com.yourong.common.util.FormulaUtil;
import com.yourong.common.util.StringUtil;

/**
 * 合同biz
 * 
 * @author Leon
 * @create 2013-11-4
 */
public class ContractBiz {
	
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
	 * 地址
	 * 
	 */
	private String address;
	
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
	
	
	/** 额外年化收益 **/
	private BigDecimal extraAnnualizedRate;
	
	/** 总计年化收益 **/
	private BigDecimal totalAnnualizedRate;
	
	   /**年化利率**/
    private BigDecimal p2pAnnualizedRate;
    
	/** 额外年化收益 **/
	private BigDecimal p2pExtraAnnualizedRate;
	
	/** p2p总计年化收益 **/
	private BigDecimal p2pTotalAnnualizedRate;
	
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
	
	
	/**出借人类型（1:个人用户、2:企业用户）*/
	private Integer lenderType;
	/**
	 * 出借企业法人名称
	 */
	private String legalName;
	/**
	 * 出借企业名称
	 */
	private String companyName;
	/**
	 * 出借企业电话 
	 */
	private String companyTelephone;
	
	
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

    private String guarantyType;
    
    private Integer instalment;
    
    
    /**投资总金额**/
    private BigDecimal totalAmount;
    
 
    
    /**起息日**/
    private String startTime;

    /**管理费用**/
    private BigDecimal manageFeeRate;



    /**借款周期**/
    private String profitPeriod;


    /**收益类型**/
    private String profitType;

    /**还款时间(年月日)**/
    private String endDate;
    
    /**借款目的**/
    private String usageOfLoan;
    
    /**垫资罚息**/
    private BigDecimal overdueFeeRate;
    
    /**保证金费率**/
    private BigDecimal guaranteeFeeRate;
    
    /**逾期罚息率**/
    private BigDecimal lateFeeRate;
    
    /**邮箱**/
    private String emailBorrower;
    
    /**邮箱**/
    private String emailLender;

    /**借款人类型（1：个人；2-企业）**/
    private Integer borrowerType;
    
    /**是否为企业**/
    private String isBorrowerTypeEnterprise;
    
    /**p2p是否使用收益券**/
    public String isP2pExtraAnnualiz;
    /**债权是否使用收益券**/
    public String isExtraAnnualiz;
    
    /**甲方是否签署（0否，1是）**/
    private Integer firstIsSign;
    /**乙方是否签署（0否，1是）**/
    private Integer secondIsSign;
    /**丙方是否签署（0否，1是）**/
    private Integer thirdIsSign;
    /**合同签署状态**/
    private Integer signStatus;
    
    /**直投合同起息日**/
    private String contractInterestDay;
    /**直投合同还款日**/
    private String contractRepaymentDay;
    
    
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

	public String getGuarantyType() {
		return guarantyType;
	}

	public void setGuarantyType(String guarantyType) {
		this.guarantyType = guarantyType;
	}
	
	
	
	/**
	 * @return the legalName
	 */
	public String getLegalName() {
		return legalName;
	}

	/**
	 * @param legalName the legalName to set
	 */
	public void setLegalName(String legalName) {
		this.legalName = legalName;
	}

	/**
	 * @return the lenderType
	 */
	public Integer getLenderType() {
		return lenderType;
	}

	/**
	 * @param lenderType the lenderType to set
	 */
	public void setLenderType(Integer lenderType) {
		this.lenderType = lenderType;
	}

	/**
	 * @return the companyName
	 */
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * @param companyName the companyName to set
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * @return the companyTelephone
	 */
	public String getCompanyTelephone() {
		return companyTelephone;
	}

	/**
	 * @param companyTelephone the companyTelephone to set
	 */
	public void setCompanyTelephone(String companyTelephone) {
		this.companyTelephone = companyTelephone;
	}
	
	/**
	 * 出借人是否企业
	 * @return
	 */
	public boolean isLenderEnterprise(){
		if(lenderType != null && lenderType == 2){
			return true;
		}
		return false;
	}

	/**
	 * 收益类型的在合同中的描述
	 *
	 * @return
	 */
	public String getProfitTypeName() {
		if (DebtEnum.RETURN_TYPE_DAY.getCode().equals(profitTypeCode)) {
			return "本金包含在最后一次付息金额中，并于最后一次付息时一起支付与乙方。";
		}
		if (DebtEnum.RETURN_TYPE_ONCE.getCode().equals(profitTypeCode)) {
			return DebtEnum.RETURN_TYPE_ONCE.getDesc();
		}
		if (DebtEnum.RETURN_TYPE_AVG_PRINCIPAL.getCode().equals(profitTypeCode)) {
			return DebtEnum.RETURN_TYPE_AVG_PRINCIPAL.getDesc();
		}
		if (DebtEnum.RETURN_TYPE_AVG_INTEREST.getCode().equals(profitTypeCode)) {
			return DebtEnum.RETURN_TYPE_AVG_INTEREST.getDesc();
		}
		if (DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getCode().equals(profitTypeCode)) {
			return DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getDesc();
		}
		return null;
	} 
	
	
	/**
	 * “第二条 基础债权信息”收益类型不同显示的内容
	 *
	 * @return
	 */
	public String getProfitTypeTextOfBaseInfo() {
		if (DebtEnum.RETURN_TYPE_DAY.getCode().equals(profitTypeCode)) {
			return "2、上述债权按日计息，按月付息， 一年按360天计息。";
		}
		if (DebtEnum.RETURN_TYPE_ONCE.getCode().equals(profitTypeCode)) {
			return "2、"+DebtEnum.RETURN_TYPE_ONCE.getDesc();
		}
		if (DebtEnum.RETURN_TYPE_AVG_PRINCIPAL.getCode().equals(profitTypeCode)) {
			return "2、"+DebtEnum.RETURN_TYPE_AVG_PRINCIPAL.getDesc();
		}
		if (DebtEnum.RETURN_TYPE_AVG_INTEREST.getCode().equals(profitTypeCode)) {
			return "2、"+DebtEnum.RETURN_TYPE_AVG_INTEREST.getDesc();
		}
		if (DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getCode().equals(profitTypeCode)) {
			return "2、"+DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getDesc();
		}
		if (DebtEnum.RETURN_TYPE_DAY_SEASON.getCode().equals(profitType)) {
			return "2、"+DebtEnum.RETURN_TYPE_DAY_SEASON.getDesc();
		}
		if (DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST_WEEK.getCode().equals(profitType)) {
			return "2、"+DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST_WEEK.getDesc();
		}
		return null;
	} 
	
	/**
	 * 债权基本信息描述的一个标题
	 * @return
	 */
	public String getTdTitleOfBaseInfo() {
		String infoTitle = "";
		if (StringUtil.isBlank(debtTypeCode)) {
			return infoTitle;
		}
		if(debtTypeCode.equals(DebtEnum.DEBT_TYPE_COLLATERAL.getCode())) {
			if(guarantyType.equals("car") && instalment == 1){
				infoTitle = "购车分期基本信息";
			}else{
				infoTitle = "债权抵押物信息";
			}
			return infoTitle;
		}
		if(debtTypeCode.equals(DebtEnum.DEBT_TYPE_PLEDGE.getCode())) {
			infoTitle = "债权质押物信息";
			return infoTitle;
		}
		if(debtTypeCode.equals(DebtEnum.DEBT_TYPE_CREDIT.getCode())){
			if(guarantyType == "carPayIn" ){
				infoTitle = "车贷垫资基本信息";
			}else if(guarantyType == "carBusiness"){
				infoTitle = "财产保障信息";
			}else{
				infoTitle = "借款人财产保障信息";
			}
		}
		return infoTitle;
	}

	public Integer getInstalment() {
		return instalment;
	}

	public void setInstalment(Integer instalment) {
		this.instalment = instalment;
	}

	/**
	 * @return the totalAmount
	 */
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	/**
	 * @param totalAmount the totalAmount to set
	 */
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	/**
	 * @return the p2pAnnualizedRate
	 */
	public BigDecimal getP2pAnnualizedRate() {
		return p2pAnnualizedRate;
	}

	/**
	 * @param p2pAnnualizedRate the p2pAnnualizedRate to set
	 */
	public void setP2pAnnualizedRate(BigDecimal p2pAnnualizedRate) {
		this.p2pAnnualizedRate = p2pAnnualizedRate;
	}
	

	/**
	 * @return the profitPeriod
	 */
	public String getProfitPeriod() {
		return profitPeriod;
	}

	/**
	 * @param profitPeriod the profitPeriod to set
	 */
	public void setProfitPeriod(String profitPeriod) {
		this.profitPeriod = profitPeriod;
	}

	/**
	 * @return the profitType
	 */
	public String getProfitType() {
		return profitType;
	}

	/**
	 * @param profitType the profitType to set
	 */
	public void setProfitType(String profitType) {
		this.profitType = profitType;
	}

	

	

	/**
	 * @return the manageFeeRate
	 */
	public BigDecimal getManageFeeRate() {
		return manageFeeRate;
	}

	/**
	 * @param manageFeeRate the manageFeeRate to set
	 */
	public void setManageFeeRate(BigDecimal manageFeeRate) {
		this.manageFeeRate = manageFeeRate;
	}

	/**
	 * @return the endDate
	 */
	public String getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	

	/**
	 * @return the startTime
	 */
	public String getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the usageOfLoan
	 */
	public String getUsageOfLoan() {
		return usageOfLoan;
	}

	/**
	 * @param usageOfLoan the usageOfLoan to set
	 */
	public void setUsageOfLoan(String usageOfLoan) {
		this.usageOfLoan = usageOfLoan;
	}

	/**
	 * @return the overdueFeeRate
	 */
	public BigDecimal getOverdueFeeRate() {
		return overdueFeeRate;
	}

	/**
	 * @param overdueFeeRate the overdueFeeRate to set
	 */
	public void setOverdueFeeRate(BigDecimal overdueFeeRate) {
		this.overdueFeeRate = overdueFeeRate;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the emailBorrower
	 */
	public String getEmailBorrower() {
		return emailBorrower;
	}

	/**
	 * @param emailBorrower the emailBorrower to set
	 */
	public void setEmailBorrower(String emailBorrower) {
		this.emailBorrower = emailBorrower== null ? null : emailBorrower.trim();;
	}

	/**
	 * @return the emailLender
	 */
	public String getEmailLender() {
		return emailLender;
	}

	/**
	 * @param emailLender the emailLender to set
	 */
	public void setEmailLender(String emailLender) {
		this.emailLender = emailLender== null ? null : emailLender.trim();;
		
	}
	
	
	public Integer getBorrowerType() {
		return borrowerType;
	}

	public void setBorrowerType(Integer borrowerType) {
		this.borrowerType = borrowerType;
	}
	
	public String getIsBorrowerTypeEnterprise() {
		if(borrowerType != null && borrowerType == ProjectEnum.PROJECT_BORROWER_ENTERPRISE_TYPE_DIRECT.getType()){
			return "Y";
		}
		return "N";
	}

	public void setIsBorrowerTypeEnterprise(String isBorrowerTypeEnterprise) {
		this.isBorrowerTypeEnterprise = isBorrowerTypeEnterprise;
	}

	public BigDecimal getGuaranteeFeeRate() {
		return guaranteeFeeRate;
	}

	public void setGuaranteeFeeRate(BigDecimal guaranteeFeeRate) {
		this.guaranteeFeeRate = guaranteeFeeRate;
	}

	public BigDecimal getLateFeeRate() {
		return lateFeeRate;
	}

	public void setLateFeeRate(BigDecimal lateFeeRate) {
		this.lateFeeRate = lateFeeRate;
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

	public String getContractInterestDay() {
		return contractInterestDay;
	}

	public void setContractInterestDay(String contractInterestDay) {
		this.contractInterestDay = contractInterestDay;
	}

	public String getContractRepaymentDay() {
		return contractRepaymentDay;
	}

	public void setContractRepaymentDay(String contractRepaymentDay) {
		this.contractRepaymentDay = contractRepaymentDay;
	}

	/**
	 * @return the extraAnnualizedRate
	 */
	public BigDecimal getExtraAnnualizedRate() {
		return extraAnnualizedRate;
	}

	/**
	 * @param extraAnnualizedRate the extraAnnualizedRate to set
	 */
	public void setExtraAnnualizedRate(BigDecimal extraAnnualizedRate) {
		this.extraAnnualizedRate = extraAnnualizedRate;
	}

	/**
	 * @return the p2pExtraAnnualizedRate
	 */
	public BigDecimal getP2pExtraAnnualizedRate() {
		return p2pExtraAnnualizedRate;
	}

	/**
	 * @param p2pExtraAnnualizedRate the p2pExtraAnnualizedRate to set
	 */
	public void setP2pExtraAnnualizedRate(BigDecimal p2pExtraAnnualizedRate) {
		this.p2pExtraAnnualizedRate = p2pExtraAnnualizedRate;
	}

	public BigDecimal getTotalAnnualizedRate() {
		return totalAnnualizedRate;
	}

	public void setTotalAnnualizedRate(BigDecimal totalAnnualizedRate) {
		this.totalAnnualizedRate = totalAnnualizedRate;
	}

	public BigDecimal getP2pTotalAnnualizedRate() {
		return p2pTotalAnnualizedRate;
	}

	public void setP2pTotalAnnualizedRate(BigDecimal p2pTotalAnnualizedRate) {
		this.p2pTotalAnnualizedRate = p2pTotalAnnualizedRate;
	}

	public String getIsP2pExtraAnnualiz() {
		if(p2pExtraAnnualizedRate != null){
			return "Y";
		}
		return "N";
	}

	public void setIsP2pExtraAnnualiz(String isP2pExtraAnnualiz) {
		this.isP2pExtraAnnualiz = isP2pExtraAnnualiz;
	}

	public String getIsExtraAnnualiz() {
		if(extraAnnualizedRate != null){
			return "Y";
		}
		return "N";
	}

	public void setIsExtraAnnualiz(String isExtraAnnualiz) {
		this.isExtraAnnualiz = isExtraAnnualiz;
	}
	
}