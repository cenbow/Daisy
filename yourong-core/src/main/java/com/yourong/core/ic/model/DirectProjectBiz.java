package com.yourong.core.ic.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.util.FormulaUtil;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.uc.model.Enterprise;
import com.yourong.core.uc.model.MemberBaseBiz;

/**
 * @desc 直投项目BIZ
 * @author fuyili 2015年12月30日下午7:20:11
 */
public class DirectProjectBiz extends AbstractBaseObject {
	private static final long serialVersionUID = 1L;

	/** 项目id **/
	private Long id;

	/** 项目类型code **/
	private String projectType;

	/** 项目名称 **/
	private String name;

	/** 项目描述 **/
	private String shortDesc;

	/** 收益类型 **/
	private String profitType;

	/** 风险等级 **/
	private String riskLevel;
	
	/**起投金额**/
    private BigDecimal minInvestAmount;

    /**递增单位金额**/
    private BigDecimal incrementAmount;
    
    /**最小收益**/
    private BigDecimal minAnnualizedRate;

    /**最大收益**/
    private BigDecimal maxAnnualizedRate;
    
	/** 投资总金额 **/
	private BigDecimal totalAmount;

	/** 项目开始日期(年月日) **/
	private Date startDate;

	/** 还款时间(年月日) **/
	private Date endDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	/**上线时间(年月日时分秒)**/
	private Date onlineTime;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	/**销售截止日期(年月日时分秒)**/
	private Date saleEndTime;

	/** 起息日，T+1则存1 **/
	private Integer interestFrom;

	/** 状态码(1:存盘,10:待审核,20:待发布,30:投资中,40:已暂停,50:已满额,60:已截止,70:已还款) **/
	private Integer status;

	/** 发布人管理员ID **/
	private Long publishId;
	
	/** 发布人管理员名字 **/
	private String publishName;
	
	/** 投资类型（1-债权；2-直投） **/
	private Integer investType;

	/** 管理费用 **/
	private BigDecimal manageFeeRate;

	/** 垫资罚息率 **/
	private BigDecimal overdueFeeRate;
	
	/** 逾期罚息 **/
	private BigDecimal lateFeeRate;

	/** 保证金费率 **/
	private BigDecimal guaranteeFeeRate;
	
	/** 风险金费率 **/
	private BigDecimal riskFeeRate;

	/** 介绍费率 **/
	private BigDecimal introducerFeeRate;

	/** 借款人id **/
	private Long borrowerId;

	/** 借款人类型（1：个人；2-企业） **/
	private Integer borrowerType;

	/** 企业id **/
	private Long enterpriseId;

	/** 借款周期 **/
	private Integer borrowPeriod;

	/** 借款周期类型（1-日；2-月；3-年） **/
	private Integer borrowPeriodType;

	/** 担保方式（pledge-质押；collateral-抵押；credit-信用） **/
	private String securityType;

	/** 是否分期（0-非分期；1-分期） **/
	private Integer instalment;

	/** 线下项目编号 **/
	private String originalProjectNumber;

	/** 年化利率 **/
	private BigDecimal annualizedRate;
	
   /**收益计算方式(1-阶梯收益， 2-利率随递增额递增)**/
    private Integer annualizedRateType;

	/** 创建时间 **/
	private Date publishTime;

	/** 是否推荐(0：不推荐 1：推荐) **/
	private Integer recommend;

	/** 推荐权重，值越小越靠前 **/
	private Integer recommendWeight;

	/** 创建时间 **/
	private Date createTime;

	/** 更新时间 **/
	private Date updateTime;

	/** 备注 **/
	private String remarks;

	/** 删除标记 **/
	private Integer delFlag;

	/** 项目描述，保障措施 **/
	private String description;

	/** 项目缩略图 **/
	private String thumbnail;

	/** 就否新手项目 **/
	private Integer isNovice;

	/** 是否参与租赁分红 **/
	private Integer joinLease;

	/** 上线是否通知 **/
	private Integer onlineNotice;

	/** 预告是否通知 **/
	private Integer noticeNotice;

	/** App推荐 **/
	private Integer appRecommend;

	/** APP推荐权重 **/
	private Integer appRecommendWeight;

	/** 绿狗合同标识 **/
	private Integer lvgouFlag;
	/**
	 * 借款人信息
	 */
	private MemberBaseBiz borrowerMemberBaseBiz;
	
	/**
	 * 介绍人信息
	 */
	private MemberBaseBiz introducerMemberBaseBiz;
	
	/**
	 * 项目投资进度
	 */
	private String progress;
	
	  /**借款人姓名**/
    private String borrowerName;
    
    /**借款人手机号**/
    private Long mobile;
    
    /**项目销售完成时间（=项目最后一笔交易时间）**/
    private Date saleComplatedTime;
    
    private Long memberId;
    
    /**可放款金额**/
    private BigDecimal loanAmount;
    
    /**放款时间**/
    private Date loanDate;
    
    /**z借款人存钱罐账号**/
    private String sinaAccount;
    
    /**现金券总金额**/
    private BigDecimal totalCouponAmount;
    
    /** 投资金额 **/
	private BigDecimal investAmount;
	
	/**
	 * 直投项目相关附件
	 */
	private List<BscAttachment> bscAttachments;
	
	/**
	 * 抵质押物详细信息
	 */
	private DebtCollateral debtCollateral;
	
	/**
	 * 管理费(格式化之后的)
	 */
	private String manageFee;
	
	/**
	 * 可放款金额
	 */
	private String ableLoanAmount;
	
	/**
	 * 放款状态
	 */
	private Integer loanStatus;
	 
	/**
	 * 操作人姓名
	 */
    private String operateName;
	
	private Enterprise enterprise;
	
	/**借款详情用途**/
	private String borrowDetail;
	
	 /**介绍人Id**/
	private Long introducerId;
	
	/** 是否更新介绍人信息标示 **/
	private Integer isUpdateIntroFlag;
	
	private Integer transferFlag;
	
	/**起息日后X个自然日可以转让**/
	private Integer transferAfterInterest;
	
	/**距离最近一期还款日X天**/
	private Integer transferRecentRepayment;
	
	/**距离最后一期还款日X天**/
	private Integer transferLastRepayment;	
	
	private String channelBusiness;
	
	 private String openPlatformKey;

	private Long openId;
	
	 /**满标悬赏标识**/
	private Integer catalyzerFlag;
	
	 /**满标悬赏总金额**/
    private BigDecimal catalyzerExtraAmount;
    
    /**快投有奖奖励时间**/
    private String catalyzerHour;
    
    /**借款人上线标识， 1-正常上线，2-暂停上线，3-正常上线(超出授信额)，4-暂停上线(超出授信额)**/
    private Integer onlineFlag;
    
    /**渠道商名称**/
    private String openPlatformKeyName;
    

    /**项目加息标识**/
	private Integer addRateFlag;
    
	/**加息数值**/
    private BigDecimal addRate;
    
    /**快投奖励金额**/
    private  BigDecimal quickReward;
    
    

	public BigDecimal getQuickReward() {
		return quickReward;
	}

	public void setQuickReward(BigDecimal quickReward) {
		this.quickReward = quickReward;
	}

	public String getOpenPlatformKey() {
		return openPlatformKey;
	}

	public void setOpenPlatformKey(String openPlatformKey) {
		this.openPlatformKey = openPlatformKey;
	}

	public String getChannelBusiness() {
		return channelBusiness;
	}

	public void setChannelBusiness(String channelBusiness) {
		this.channelBusiness = channelBusiness;
	}

	public MemberBaseBiz getBorrowerMemberBaseBiz() {
		return borrowerMemberBaseBiz;
	}

	public void setBorrowerMemberBaseBiz(MemberBaseBiz borrowerMemberBaseBiz) {
		this.borrowerMemberBaseBiz = borrowerMemberBaseBiz;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProjectType() {
		return projectType;
	}

	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortDesc() {
		return shortDesc;
	}

	public void setShortDesc(String shortDesc) {
		this.shortDesc = shortDesc;
	}

	public String getProfitType() {
		return profitType;
	}

	public String getBorrowDetail() {
		return borrowDetail;
	}

	public void setBorrowDetail(String borrowDetail) {
		this.borrowDetail = borrowDetail;
	}

	public void setProfitType(String profitType) {
		this.profitType = profitType;
	}

	public String getRiskLevel() {
		return riskLevel;
	}

	public void setRiskLevel(String riskLevel) {
		this.riskLevel = riskLevel;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getOnlineTime() {
		return onlineTime;
	}

	public void setOnlineTime(Date onlineTime) {
		this.onlineTime = onlineTime;
	}

	public Date getSaleEndTime() {
		return saleEndTime;
	}

	public void setSaleEndTime(Date saleEndTime) {
		this.saleEndTime = saleEndTime;
	}

	public Integer getInterestFrom() {
		return interestFrom;
	}

	public void setInterestFrom(Integer interestFrom) {
		this.interestFrom = interestFrom;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Long getPublishId() {
		return publishId;
	}

	public void setPublishId(Long publishId) {
		this.publishId = publishId;
	}

	public Integer getInvestType() {
		return investType;
	}

	public void setInvestType(Integer investType) {
		this.investType = investType;
	}

	public BigDecimal getManageFeeRate() {
		return manageFeeRate;
	}

	public String getOperateName() {
		return operateName;
	}

	public void setOperateName(String operateName) {
		this.operateName = operateName;
	}

	public void setManageFeeRate(BigDecimal manageFeeRate) {
		this.manageFeeRate = manageFeeRate;
	}

	public BigDecimal getOverdueFeeRate() {
		return overdueFeeRate;
	}

	public void setOverdueFeeRate(BigDecimal overdueFeeRate) {
		this.overdueFeeRate = overdueFeeRate;
	}

	public Long getBorrowerId() {
		return borrowerId;
	}

	public void setBorrowerId(Long borrowerId) {
		this.borrowerId = borrowerId;
	}

	public Integer getBorrowerType() {
		return borrowerType;
	}

	public void setBorrowerType(Integer borrowerType) {
		this.borrowerType = borrowerType;
	}

	public Long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public Integer getBorrowPeriod() {
		return borrowPeriod;
	}

	public void setBorrowPeriod(Integer borrowPeriod) {
		this.borrowPeriod = borrowPeriod;
	}

	public Integer getBorrowPeriodType() {
		return borrowPeriodType;
	}

	public void setBorrowPeriodType(Integer borrowPeriodType) {
		this.borrowPeriodType = borrowPeriodType;
	}

	public String getSecurityType() {
		return securityType;
	}

	public void setSecurityType(String securityType) {
		this.securityType = securityType;
	}

	public Integer getInstalment() {
		return instalment;
	}

	public void setInstalment(Integer instalment) {
		this.instalment = instalment;
	}

	public String getOriginalProjectNumber() {
		return originalProjectNumber;
	}

	public void setOriginalProjectNumber(String originalProjectNumber) {
		this.originalProjectNumber = originalProjectNumber;
	}

	public BigDecimal getAnnualizedRate() {
		return annualizedRate;
	}

	public void setAnnualizedRate(BigDecimal annualizedRate) {
		this.annualizedRate = annualizedRate;
	}

	public Date getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}

	public Integer getRecommend() {
		return recommend;
	}

	public void setRecommend(Integer recommend) {
		this.recommend = recommend;
	}

	public Integer getRecommendWeight() {
		return recommendWeight;
	}

	public void setRecommendWeight(Integer recommendWeight) {
		this.recommendWeight = recommendWeight;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Integer getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public Integer getIsNovice() {
		return isNovice;
	}

	public void setIsNovice(Integer isNovice) {
		this.isNovice = isNovice;
	}

	public Integer getJoinLease() {
		return joinLease;
	}

	public void setJoinLease(Integer joinLease) {
		this.joinLease = joinLease;
	}

	public Integer getOnlineNotice() {
		return onlineNotice;
	}

	public void setOnlineNotice(Integer onlineNotice) {
		this.onlineNotice = onlineNotice;
	}

	public Integer getNoticeNotice() {
		return noticeNotice;
	}

	public void setNoticeNotice(Integer noticeNotice) {
		this.noticeNotice = noticeNotice;
	}

	public Integer getAppRecommend() {
		return appRecommend;
	}

	public void setAppRecommend(Integer appRecommend) {
		this.appRecommend = appRecommend;
	}

	public Integer getAppRecommendWeight() {
		return appRecommendWeight;
	}

	public void setAppRecommendWeight(Integer appRecommendWeight) {
		this.appRecommendWeight = appRecommendWeight;
	}

	public Integer getLvgouFlag() {
		return lvgouFlag;
	}

	public void setLvgouFlag(Integer lvgouFlag) {
		this.lvgouFlag = lvgouFlag;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getProgress() {
		return progress;
	}

	public void setProgress(String progress) {
		this.progress = progress;
	}

	public String getBorrowerName() {
		return borrowerName;
	}

	public void setBorrowerName(String borrowerName) {
		this.borrowerName = borrowerName;
	}

	public Long getMobile() {
		return mobile;
	}

	public void setMobile(Long mobile) {
		this.mobile = mobile;
	}

	public Date getSaleComplatedTime() {
		return saleComplatedTime;
	}

	public void setSaleComplatedTime(Date saleComplatedTime) {
		this.saleComplatedTime = saleComplatedTime;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public BigDecimal getLoanAmount() {
		return loanAmount;
	}

	public void setLoanAmount(BigDecimal loanAmount) {
		this.loanAmount = loanAmount;
	}

	public Date getLoanDate() {
		return loanDate;
	}

	public void setLoanDate(Date loanDate) {
		this.loanDate = loanDate;
	}

	public String getSinaAccount() {
		return sinaAccount;
	}

	public void setSinaAccount(String sinaAccount) {
		this.sinaAccount = sinaAccount;
	}

	public BigDecimal getTotalCouponAmount() {
		return totalCouponAmount;
	}

	public void setTotalCouponAmount(BigDecimal totalCouponAmount) {
		this.totalCouponAmount = totalCouponAmount;
	}
	
	public List<BscAttachment> getBscAttachments() {
		return bscAttachments;
	}

	public void setBscAttachments(List<BscAttachment> bscAttachments) {
		this.bscAttachments = bscAttachments;
	}

	public BigDecimal getMinInvestAmount() {
		return minInvestAmount;
	}

	public void setMinInvestAmount(BigDecimal minInvestAmount) {
		this.minInvestAmount = minInvestAmount;
	}

	public BigDecimal getIncrementAmount() {
		return incrementAmount;
	}

	public void setIncrementAmount(BigDecimal incrementAmount) {
		this.incrementAmount = incrementAmount;
	}

	public DebtCollateral getDebtCollateral() {
		return debtCollateral;
	}

	public void setDebtCollateral(DebtCollateral debtCollateral) {
		this.debtCollateral = debtCollateral;
	}

	public Enterprise getEnterprise() {
		return enterprise;
	}

	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}

	public BigDecimal getInvestAmount() {
		return investAmount;
	}

	public void setInvestAmount(BigDecimal investAmount) {
		this.investAmount = investAmount;
	}

	public Integer getAnnualizedRateType() {
		return annualizedRateType;
	}

	public void setAnnualizedRateType(Integer annualizedRateType) {
		this.annualizedRateType = annualizedRateType;
	}

	public BigDecimal getMinAnnualizedRate() {
		return minAnnualizedRate;
	}

	public void setMinAnnualizedRate(BigDecimal minAnnualizedRate) {
		this.minAnnualizedRate = minAnnualizedRate;
	}

	public BigDecimal getMaxAnnualizedRate() {
		return maxAnnualizedRate;
	}

	public void setMaxAnnualizedRate(BigDecimal maxAnnualizedRate) {
		this.maxAnnualizedRate = maxAnnualizedRate;
	}

	public String getManageFee() {
		return manageFee;
	}

	public void setManageFee(String manageFee) {
		this.manageFee = manageFee;
	}
	
	
	public String getAbleLoanAmount() {
		return ableLoanAmount;
	}

	public void setAbleLoanAmount(String ableLoanAmount) {
		this.ableLoanAmount = ableLoanAmount;
	}

	public Integer getLoanStatus() {
		return loanStatus;
	}

	public void setLoanStatus(Integer loanStatus) {
		this.loanStatus = loanStatus;
	}

	/**
	 * 
	 * @Description:借款金额格式化
	 * @return
	 * @author: chaisen
	 * @time:2016年2月16日 下午1:22:34
	 */
	public String getFormatTotalAmount() {
		if(totalAmount == null || BigDecimal.ZERO.compareTo(totalAmount)==0){
			return "￥0";
		}
		return "￥"+FormulaUtil.getFormatPrice(totalAmount);
	}
	/**
	 * 
	 * @Description:用户投资格式化
	 * @return
	 * @author: chaisen
	 * @time:2016年3月2日 下午1:39:08
	 */
	public String getInvesAmountStr() {
		if(investAmount == null || BigDecimal.ZERO.compareTo(investAmount)==0){
			return "￥0";
		}
		return "￥"+FormulaUtil.getFormatPrice(investAmount);
	}
	/**
	 * 
	 * @Description:现金券格式化
	 * @return
	 * @author: chaisen
	 * @time:2016年3月2日 下午1:43:15
	 */
	public String getTotalCouponAmountStr() {
		if(totalCouponAmount == null || BigDecimal.ZERO.compareTo(totalCouponAmount)==0){
			return "￥0";
		}
		return "￥"+FormulaUtil.getFormatPrice(totalCouponAmount);
	}

	public BigDecimal getLateFeeRate() {
		return lateFeeRate;
	}

	public void setLateFeeRate(BigDecimal lateFeeRate) {
		this.lateFeeRate = lateFeeRate;
	}

	public BigDecimal getGuaranteeFeeRate() {
		return guaranteeFeeRate;
	}

	public void setGuaranteeFeeRate(BigDecimal guaranteeFeeRate) {
		this.guaranteeFeeRate = guaranteeFeeRate;
	}

	public BigDecimal getRiskFeeRate() {
		return riskFeeRate;
	}

	public void setRiskFeeRate(BigDecimal riskFeeRate) {
		this.riskFeeRate = riskFeeRate;
	}

	public BigDecimal getIntroducerFeeRate() {
		return introducerFeeRate;
	}

	public void setIntroducerFeeRate(BigDecimal introducerFeeRate) {
		this.introducerFeeRate = introducerFeeRate;
	}

	public Long getIntroducerId() {
		return introducerId;
	}

	public void setIntroducerId(Long introducerId) {
		this.introducerId = introducerId;
	}

	public MemberBaseBiz getIntroducerMemberBaseBiz() {
		return introducerMemberBaseBiz;
	}

	public void setIntroducerMemberBaseBiz(MemberBaseBiz introducerMemberBaseBiz) {
		this.introducerMemberBaseBiz = introducerMemberBaseBiz;
	}
	/**项目各费用总和**/
	public String getProjectFeeStr(){
		BigDecimal manage = BigDecimal.ZERO;
		BigDecimal guarantee = BigDecimal.ZERO;
		BigDecimal risk = BigDecimal.ZERO;
		BigDecimal introducer = BigDecimal.ZERO;
		if(manageFeeRate==null){
			manage=BigDecimal.ZERO;
		}else{
			manage=FormulaUtil.getManagerAmount(totalAmount,manageFeeRate);
		}
		if(guaranteeFeeRate==null){
			guarantee=BigDecimal.ZERO;
		}else{
			guarantee=FormulaUtil.getManagerAmount(totalAmount,guaranteeFeeRate);
		}
		if(riskFeeRate==null){
			risk=BigDecimal.ZERO;
		}else{
			risk=FormulaUtil.getManagerAmount(totalAmount,riskFeeRate);
		}
		if(introducerFeeRate==null){
			introducer=BigDecimal.ZERO;
		}else{
			introducer=FormulaUtil.getManagerAmount(totalAmount,introducerFeeRate);
		}
		if(quickReward==null){
			quickReward=BigDecimal.ZERO;
		}
		return "￥"+FormulaUtil.getFormatPrice(manage.add(guarantee).add(risk).add(introducer).add(quickReward));
	}
	
	public String getAbleLoanAmountStr(){
		BigDecimal manage = BigDecimal.ZERO;
		BigDecimal guarantee = BigDecimal.ZERO;
		BigDecimal risk = BigDecimal.ZERO;
		BigDecimal introducer = BigDecimal.ZERO;
		if(manageFeeRate==null){
			manage=BigDecimal.ZERO;
		}else{
			manage=FormulaUtil.getManagerAmount(totalAmount,manageFeeRate);
		}
		if(guaranteeFeeRate==null){
			guarantee=BigDecimal.ZERO;
		}else{
			guarantee=FormulaUtil.getManagerAmount(totalAmount,guaranteeFeeRate);
		}
		if(riskFeeRate==null){
			risk=BigDecimal.ZERO;
		}else{
			risk=FormulaUtil.getManagerAmount(totalAmount,riskFeeRate);
		}
		if(introducerFeeRate==null){
			introducer=BigDecimal.ZERO;
		}else{
			introducer=FormulaUtil.getManagerAmount(totalAmount,introducerFeeRate);
		}
		return "￥"+FormulaUtil.getFormatPrice(totalAmount.subtract(manage).subtract(guarantee).subtract(risk).subtract(introducer));
	}

	public Integer getIsUpdateIntroFlag() {
		return isUpdateIntroFlag;
	}

	public void setIsUpdateIntroFlag(Integer isUpdateIntroFlag) {
		this.isUpdateIntroFlag = isUpdateIntroFlag;
	}

	/**
	 * @return the transferFlag
	 */
	public Integer getTransferFlag() {
		return transferFlag;
	}

	/**
	 * @param transferFlag the transferFlag to set
	 */
	public void setTransferFlag(Integer transferFlag) {
		this.transferFlag = transferFlag;
	}

	/**
	 * @return the transferAfterInterest
	 */
	public Integer getTransferAfterInterest() {
		return transferAfterInterest;
	}

	/**
	 * @param transferAfterInterest the transferAfterInterest to set
	 */
	public void setTransferAfterInterest(Integer transferAfterInterest) {
		this.transferAfterInterest = transferAfterInterest;
	}

	/**
	 * @return the transferRecentRepayment
	 */
	public Integer getTransferRecentRepayment() {
		return transferRecentRepayment;
	}

	/**
	 * @param transferRecentRepayment the transferRecentRepayment to set
	 */
	public void setTransferRecentRepayment(Integer transferRecentRepayment) {
		this.transferRecentRepayment = transferRecentRepayment;
	}

	/**
	 * @return the transferLastRepayment
	 */
	public Integer getTransferLastRepayment() {
		return transferLastRepayment;
	}

	/**
	 * @param transferLastRepayment the transferLastRepayment to set
	 */
	public void setTransferLastRepayment(Integer transferLastRepayment) {
		this.transferLastRepayment = transferLastRepayment;
	}

	public Integer getCatalyzerFlag() {
		return catalyzerFlag;
	}

	public void setCatalyzerFlag(Integer catalyzerFlag) {
		this.catalyzerFlag = catalyzerFlag;
	}

	public BigDecimal getCatalyzerExtraAmount() {
		return catalyzerExtraAmount;
	}

	public void setCatalyzerExtraAmount(BigDecimal catalyzerExtraAmount) {
		this.catalyzerExtraAmount = catalyzerExtraAmount;
	}

	public Long getOpenId() {
		return openId;
	}

	public void setOpenId(Long openId) {
		this.openId = openId;
	}

	public Integer getOnlineFlag() {
		return onlineFlag;
	}

	public void setOnlineFlag(Integer onlineFlag) {
		this.onlineFlag = onlineFlag;
	}

	public String getOpenPlatformKeyName() {
		return openPlatformKeyName;
	}

	public void setOpenPlatformKeyName(String openPlatformKeyName) {
		this.openPlatformKeyName = openPlatformKeyName;
	}

	public Integer getAddRateFlag() {
		return addRateFlag;
	}

	public void setAddRateFlag(Integer addRateFlag) {
		this.addRateFlag = addRateFlag;
	}

	public BigDecimal getAddRate() {
		return addRate;
	}

	public void setAddRate(BigDecimal addRate) {
		this.addRate = addRate;
	}

	public String getCatalyzerHour() {
		return catalyzerHour;
	}

	public void setCatalyzerHour(String catalyzerHour) {
		this.catalyzerHour = catalyzerHour;
	}

	public String getPublishName() {
		return publishName;
	}

	public void setPublishName(String publishName) {
		this.publishName = publishName;
	}
	
}
