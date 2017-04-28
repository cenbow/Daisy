package com.yourong.core.ic.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.core.bsc.model.AttachmentIndex;

public class ProjectBiz extends AbstractBaseObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    /**上线时间(年月日时分秒)**/
    private Date onlineTime;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    /**销售截止日期(年月日时分秒)**/
    private Date saleEndTime;

    /**起息日，T+1则存1**/
    private Integer interestFrom;

    /**状态码(1:待审核  2:审核失败 3:待发布 4:已发布 5:暂停 6: 销售结束 7:提前赎回  9:项目结束)**/
    private Integer status;

    /**发布人管理员ID**/
    private Long publishId;

    /**创建时间**/
    private Date publishTime;

    /**审核管理员ID**/
    private Long auditId;

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
    
    /**最大收益**/
    private BigDecimal maxInvestAmount;
    
    /**附件索引**/
    List<AttachmentIndex> attachmentIndexList;
    
    /**阶梯收益**/
    List<ProjectInterest> projectInterestList;
    
    /**债权号**/
    private String serialNumber;
    
    
    private DebtBiz debtBiz;

    /**项目描述，保障措施**/
    private String description;
    
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
    
    /**特殊活动标识**/
    private Integer activitySign;
    /**借款人姓名**/
    private String borrowerName;
    
    private Long mobile;
    
    private Integer transferFlag;
	
	/**起息日后X个自然日可以转让**/
	private Integer transferAfterInterest;
	
	/**距离最近一期还款日X天**/
	private Integer transferRecentRepayment;
	
	/**距离最后一期还款日X天**/
	private Integer transferLastRepayment;	
    
    
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


	public String getKeyword() {
		return keyword;
	}


	public void setKeyword(String keyword) {
		this.keyword = keyword;
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
		this.auditMessage = auditMessage;
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


	public BigDecimal getMaxInvestAmount() {
		return maxInvestAmount;
	}


	public void setMaxInvestAmount(BigDecimal maxInvestAmount) {
		this.maxInvestAmount = maxInvestAmount;
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


	public String getSerialNumber() {
		return serialNumber;
	}


	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}


	public DebtBiz getDebtBiz() {
		return debtBiz;
	}


	public void setDebtBiz(DebtBiz debtBiz) {
		this.debtBiz = debtBiz;
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




	public Integer getActivitySign() {
		return activitySign;
	}


	public void setActivitySign(Integer activitySign) {
		this.activitySign = activitySign;
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
    
}
