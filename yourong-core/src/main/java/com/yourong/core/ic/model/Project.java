package com.yourong.core.ic.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.annotation.JSONField;
import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.enums.ProjectEnum;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;
import com.yourong.core.bsc.model.AttachmentIndex;
import com.yourong.core.bsc.model.BscAttachment;

public class Project extends AbstractBaseObject {
    /**
	 * 
	 */
	private static final long serialVersionUID = -4483649143973225991L;

	/**项目id**/
    private Long id;

    /**目前只有债权ID**/
    private Long debtId;

    /**项目类型code**/
    private String projectType;

    /**项目名称**/
    private String name;

    /**项目关键词**/
    private String keyword;

    /**项目描述**/
    private String shortDesc;

    /**收益类型**/
    private String profitType;

    /**风险等级**/
    private String riskLevel;

    /**投资总金额**/
    private BigDecimal totalAmount;

    /**起投金额**/
    private BigDecimal minInvestAmount;

    /**递增单位金额**/
    private BigDecimal incrementAmount;

    /**收益计算方式(1-阶梯收益， 2-利率随递增额递增)**/
    private Integer annualizedRateType;

    /**最小收益**/
    private BigDecimal minAnnualizedRate;

    /**最大收益**/
    private BigDecimal maxAnnualizedRate;

    /**递增收益**/
    private BigDecimal incrementAnnualizedRate;

    /**项目开始日期(年月日)**/
    private Date startDate;

    /**还款时间(年月日)**/
    private Date endDate;
  
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    /**上线时间(年月日时分秒)**/
    private Date onlineTime;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    /**销售截止日期(年月日时分秒)**/
    private Date saleEndTime;
    
    /**剩余收益日**/
    private Integer incomeDays;

    /**起息日，T+1则存1**/
    private Integer interestFrom;

    /**状态码(1:存盘,10:待审核,20:待发布,30:投资中,40:已暂停,50:已满额,60:已截止,70:已还款)**/
    private Integer status;

    /**发布人管理员ID**/
    private Long publishId;
    
	/** 发布人管理员名字 **/
	private String publishName;
        
    /**投资类型（1-债权；2-直投）**/
    private Integer investType;

    /**管理费用**/
    private BigDecimal manageFeeRate;

    /**垫资罚息率**/
    private BigDecimal overdueFeeRate;

    /**借款人id**/
    private Long borrowerId;
    
	/**借款人类型（1：个人；2-企业）**/

    /**借款人类型（1：个人；2-企业）**/
    private Integer borrowerType;

    /**企业id**/
    private Long enterpriseId;

    /**借款周期**/
    private Integer borrowPeriod;

    /**借款周期类型（1-日；2-月；3-年）**/
    private Integer borrowPeriodType;

    /**担保方式（pledge-质押；collateral-抵押；credit-信用）**/
    private String securityType;

    /**是否分期（0-非分期；1-分期）**/
    private Integer instalment;
    
    /**线下项目编号**/
    private String originalProjectNumber;
    
    /**年化利率**/
    private BigDecimal annualizedRate;
    
    /**项目销售完成时间*/
    private Date saleComplatedTime;

    private String openPlatformKey;
    
    /**待还本金**/
    private BigDecimal payablePrincipal;
    
    /**是否资产包项目**/
    private Integer packageFlag;
    
	public Integer getPackageFlag() {
		return packageFlag;
	}

	public void setPackageFlag(Integer packageFlag) {
		this.packageFlag = packageFlag;
	}

	public String getOpenPlatformKey() {
		return openPlatformKey;
	}

	public void setOpenPlatformKey(String openPlatformKey) {
		this.openPlatformKey = openPlatformKey;
	}

	public String getAuditName() {
		return auditName;
	}

	public void setAuditName(String auditName) {
		this.auditName = auditName;
	}

	/**创建时间**/
    private Date publishTime;

    /**审核管理员ID**/
    private Long auditId;

    /**审核管理员姓名**/
    private String auditName;
    
    
    /**出借人ID**/
    private Long lenderId;

    /**出借人姓名**/
    private String lenderName; 
    
    /****/
    private Date auditTime;

    /**审核信息**/
    private String auditMessage;

    /**是否推荐(0：不推荐  1：推荐)**/
    private Integer recommend;

    /**推荐权重，值越小越靠前**/
    private Integer recommendWeight;

    /**创建时间**/
    private Date createTime;

    /**更新时间**/
    private Date updateTime;

    /**备注**/
    private String remarks;

    /**删除标记**/
    private Integer delFlag;
    
    /**项目描述，保障措施**/
    private String description;
    
    /**收益封顶**/
    private BigDecimal maxInvestAmount;
    
    /**附件索引**/
    List<AttachmentIndex> attachmentIndexList;
    
    /**阶梯收益**/
    List<ProjectInterest> projectInterestList;
    
    /**债权号**/
    private String serialNumber;
    
    /**但保方式**/
    private String debtType;
    
    /**原始债权编号**/
    private String originalDebtNumber;
    
    /**项目缩略图**/
    private String thumbnail;
    
    /**就否新手项目**/
    private Integer isNovice;
    
    /**是否参与租赁分红**/
    private Integer joinLease;
    
    /**上线是否通知**/
    private Integer onlineNotice;
    
    /**预告是否通知**/
    private Integer noticeNotice;

	/**App推荐**/
    private Integer appRecommend;
    
    /**APP推荐权重**/
    private Integer appRecommendWeight;  

    /**绿狗合同标识**/
    private Integer lvgouFlag;
    
	/** 特殊活动标识 */
	private Integer activitySign;
    
    /**借款人姓名**/
    private String borrowerName;
    
    /**借款人手机号**/
    private Long mobile;
    
    /**放款时间**/
    private Date loanTime;
    
    /**借款详情用途**/
	private String borrowDetail;
    
    private Long memberId;
    
    /**保证金费率**/
    private BigDecimal guaranteeFeeRate;
    
    /**风险金费率**/
    private BigDecimal riskFeeRate;
    
    /**逾期罚息率**/
    private BigDecimal lateFeeRate;
    
    /**介绍费率**/
    private BigDecimal introducerFeeRate;
    
    /**介绍人**/
   	private Long introducerId;
    
    /** 提前还款标识 */
	private Integer prepayment;
	
	/** 提前还款时间 */
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date prepaymentTime;
		
	/** 提前还款原因备注 */
	private String prepaymentRemark;
	
	/** 是否更新介绍人信息标示 **/
	private Integer isUpdateIntroFlag;
	
	/**借款人手机号**/
	private String borrowerMobile ;
	
	private Integer transferFlag;
	
	/**起息日后X个自然日可以转让**/
	private Integer transferAfterInterest;
	
	/** 附件信息 */
	private List<BscAttachment> bscAttachments;
    
    
	
    public List<BscAttachment> getBscAttachments() {
		return bscAttachments;
	}

	public void setBscAttachments(List<BscAttachment> bscAttachments) {
		this.bscAttachments = bscAttachments;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public Long getMobile() {
		return mobile;
	}

	public void setMobile(Long mobile) {
		this.mobile = mobile;
	}

	public Integer getLvgouFlag() {
		return lvgouFlag;
	}

	public void setLvgouFlag(Integer lvgouFlag) {
		this.lvgouFlag = lvgouFlag;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDebtId() {
        return debtId;
    }

    public void setDebtId(Long debtId) {
        this.debtId = debtId;
    }

    public String getBorrowDetail() {
		return borrowDetail;
	}

	public void setBorrowDetail(String borrowDetail) {
		this.borrowDetail = borrowDetail;
	}

	public String getProjectType() {
        return projectType;
    }

    public BigDecimal getIntroducerFeeRate() {
		return introducerFeeRate;
	}

	public void setIntroducerFeeRate(BigDecimal introducerFeeRate) {
		this.introducerFeeRate = introducerFeeRate;
	}

	public void setProjectType(String projectType) {
        this.projectType = projectType == null ? null : projectType.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword == null ? null : keyword.trim();
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc == null ? null : shortDesc.trim();
    }

    public String getProfitType() {
        return profitType;
    }

    public void setProfitType(String profitType) {
        this.profitType = profitType == null ? null : profitType.trim();
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel == null ? null : riskLevel.trim();
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
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

    public Integer getAnnualizedRateType() {
        return annualizedRateType;
    }

    public void setAnnualizedRateType(Integer annualizedRateType) {
        this.annualizedRateType = annualizedRateType;
    }

    public BigDecimal getMinAnnualizedRate() {
        return minAnnualizedRate;
    }

    public Date getLoanTime() {
		return loanTime;
	}

	public void setLoanTime(Date loanTime) {
		this.loanTime = loanTime;
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

    public BigDecimal getIncrementAnnualizedRate() {
        return incrementAnnualizedRate;
    }

    public void setIncrementAnnualizedRate(BigDecimal incrementAnnualizedRate) {
        this.incrementAnnualizedRate = incrementAnnualizedRate;
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

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public Long getAuditId() {
        return auditId;
    }

    public void setAuditId(Long auditId) {
        this.auditId = auditId;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    public String getAuditMessage() {
        return auditMessage;
    }

    public void setAuditMessage(String auditMessage) {
        this.auditMessage = auditMessage == null ? null : auditMessage.trim();
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
        this.remarks = remarks == null ? null : remarks.trim();
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public BigDecimal getMaxInvestAmount() {
		return maxInvestAmount;
	}

	public void setMaxInvestAmount(BigDecimal maxInvestAmount) {
		this.maxInvestAmount = maxInvestAmount;
	}
	
	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public List<AttachmentIndex> getAttachmentIndexList() {
		return attachmentIndexList;
	}

	public void setAttachmentIndexList(List<AttachmentIndex> attachmentIndexList) {
		this.attachmentIndexList = attachmentIndexList;
	}

	public List<ProjectInterest> getProjectInterestList() {
		return projectInterestList;
	}

	public void setProjectInterestList(List<ProjectInterest> projectInterestList) {
		this.projectInterestList = projectInterestList;
	}

	

	public String getDebtType() {
		return debtType;
	}

	public void setDebtType(String debtType) {
		this.debtType = debtType;
	}
	
	public String getOriginalDebtNumber() {
		return originalDebtNumber;
	}

	public void setOriginalDebtNumber(String originalDebtNumber) {
		this.originalDebtNumber = originalDebtNumber;
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
	
	/**
	 * 是否新手项目
	 * @return
	 */
	public boolean isNoviceProject(){
		if(isNovice == 0){
			return true;
		}
		return false;
	}

	public Integer getJoinLease() {
		return joinLease;
	}

	public void setJoinLease(Integer joinLease) {
		this.joinLease = joinLease;
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



	//格式化总金额
	public String getTotalAmountStr() {
		return FormulaUtil.formatCurrencyNoUnit(totalAmount);
	}
	
	/**
	 * @return the lenderId
	 */
	public Long getLenderId() {
		return lenderId;
	}

	/**
	 * @param lenderId the lenderId to set
	 */
	public void setLenderId(Long lenderId) {
		this.lenderId = lenderId;
	}

	/**
	 * @return the lenderName
	 */
	public String getLenderName() {
		return lenderName;
	}

	/**
	 * @param lenderName the lenderName to set
	 */
	public void setLenderName(String lenderName) {
		this.lenderName = lenderName;
	}

	
	
	/**
	 * @return the incomeDays
	 */
	public Integer getIncomeDays() {
		return incomeDays;
	}

	/**
	 * @param incomeDays the incomeDays to set
	 */
	public void setIncomeDays(Integer incomeDays) {
		this.incomeDays = incomeDays;
	}
	
	/**
	 * 收益总天数
	 * @return
	 */
	@JSONField(serialize = false)
	public Integer getEarningsTotalDays() {
		int day = DateUtils.getIntervalDays(getStartDate(), getEndDate())+1 - getInterestFrom();
		return day > 0 ? day : 0;
	}
	
	/**
	 * 获得预告项目的收益总天数
	 * @return
	 */
	@JSONField(serialize = false)
	public Integer getNoticeProjectEarningsTotalDays() {
		int day = DateUtils.getIntervalDays(DateUtils.formatDate(getOnlineTime(), DateUtils.DATE_FMT_3), getEndDate())+1 - getInterestFrom();
		return day > 0 ? day : 0;
	}
	
	
	/**
	 * 剩余收益天数
	 * @return
	 */
	@JSONField(serialize = false)
	public Integer getResidualIncomeDays() {
		int day = DateUtils.getIntervalDays(DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3), getEndDate())+1 - getInterestFrom();
		return day > 0 ? day : 0;
	}
	
	/**
	 * 根据状态显示剩余收益天数或收益总天数
	 * @return
	 */
	@JSONField(serialize = false)
	public Integer getEarningsDaysByStatus(){
		if(isNotice()){//预告
			return getNoticeProjectEarningsTotalDays();
		}
		if(status == StatusEnum.PROJECT_STATUS_INVESTING.getStatus() || status == StatusEnum.PROJECT_STATUS_STOP.getStatus()){
			return getResidualIncomeDays();
		}
		return getEarningsTotalDays();
	}
	
	/**
	 * 是否是预告
	 * @return
	 */
	@JSONField(serialize = false)
	public boolean isNotice(){
		if(status >= StatusEnum.PROJECT_STATUS_INVESTING.getStatus()){
			return false;
		}
		return true;
	}

	public Integer getInvestType() {
		return investType;
	}
    public boolean isDirectProject(){
        if (this.investType == ProjectEnum.PROJECT_TYPE_DIRECT.getType()){
            return true;
        }
        return false;
    }
	public void setInvestType(Integer investType) {
		this.investType = investType;
	}

	public BigDecimal getManageFeeRate() {
		return manageFeeRate;
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

	public String getProfitPeriod() {
		if(this.borrowPeriod!=null && this.borrowPeriodType!=null){
			String borrowPeriodResult = this.borrowPeriod.toString();
			if (this.borrowPeriodType == TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_DAY.getType()) {
				return borrowPeriodResult + "天";
			}
			if (this.borrowPeriodType == TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_MONTH.getType()) {
				return borrowPeriodResult + "个月";
			}
			if (this.borrowPeriodType == TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_YEAR.getType()) {
				return borrowPeriodResult + "年";
			}
			if (this.borrowPeriodType == TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_WEEK.getType()) {
				return borrowPeriodResult + "周";
			}
			
			return borrowPeriodResult;
			
//			return this.borrowPeriod.toString()+(this.borrowPeriodType==TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_DAY.getType()?"天":
//				(this.borrowPeriodType==TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_MONTH.getType()?"个月":"年"));
		}
		return "";
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

	public String getBorrowerName() {
		return borrowerName;
	}

	public void setBorrowerName(String borrowerName) {
		this.borrowerName = borrowerName;
	}

	public Date getSaleComplatedTime() {
		return saleComplatedTime;
	}

	public void setSaleComplatedTime(Date saleComplatedTime) {
		this.saleComplatedTime = saleComplatedTime;
	}

    public int  countProjectDays(){
    	int days =0;
        if (borrowPeriodType == 1) {
            days = borrowPeriod;
        }
        //借款周期类型月
        if (borrowPeriodType == 2) {
            days = borrowPeriod  *  30;
        }
        //借款周期类型年
        if (borrowPeriodType == 3) {
           days = borrowPeriod   *  30 * 12;
        }
		// 借款周期类型年
		if (borrowPeriodType == 4) {
			days = borrowPeriod * 7;
		}
        return days;
    }

	public Integer getActivitySign() {
		return activitySign;
	}
	
	public void setActivitySign(Integer activitySign) {
		this.activitySign = activitySign;
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

	public BigDecimal getLateFeeRate() {
		return lateFeeRate;
	}

	public void setLateFeeRate(BigDecimal lateFeeRate) {
		this.lateFeeRate = lateFeeRate;
	}

	public Integer getPrepayment() {
		return prepayment;
	}

	public void setPrepayment(Integer prepayment) {
		this.prepayment = prepayment;
	}

	public Date getPrepaymentTime() {
		return prepaymentTime;
	}

	public void setPrepaymentTime(Date prepaymentTime) {
		this.prepaymentTime = prepaymentTime;
	}

	public String getPrepaymentRemark() {
		return prepaymentRemark;
	}

	public void setPrepaymentRemark(String prepaymentRemark) {
		this.prepaymentRemark = prepaymentRemark;
	}

	public Long getIntroducerId() {
		return introducerId;
	}

	public void setIntroducerId(Long introducerId) {
		this.introducerId = introducerId;
	}

	public Integer getIsUpdateIntroFlag() {
		return isUpdateIntroFlag;
	}

	public void setIsUpdateIntroFlag(Integer isUpdateIntroFlag) {
		this.isUpdateIntroFlag = isUpdateIntroFlag;
	}

	public String getBorrowerMobile() {
		return borrowerMobile;
	}

	public void setBorrowerMobile(String borrowerMobile) {
		this.borrowerMobile = borrowerMobile;
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

	public BigDecimal getPayablePrincipal() {
		return payablePrincipal;
	}

	public void setPayablePrincipal(BigDecimal payablePrincipal) {
		this.payablePrincipal = payablePrincipal;
	}
	
    public String getPublishName() {
		return publishName;
	}

	public void setPublishName(String publishName) {
		this.publishName = publishName;
	}
}