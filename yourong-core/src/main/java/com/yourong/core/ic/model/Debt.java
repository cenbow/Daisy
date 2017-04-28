package com.yourong.core.ic.model;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.util.FormulaUtil;

public class Debt extends AbstractBaseObject {
	/**
	 * 债权表
	 */
	private static final long serialVersionUID = -603012583505186742L;

	/****/
	private Long id;

	/** 公司债权编号 **/
	private String serialNumber;

	/** 原始债权编号 **/
	private String originalDebtNumber;

	/** 类型code **/
	private String debtType;

	/** 借款人类型（1:个人用户、2:企业用户） */
	private Integer borrowerType;
	
	/**出借人类型（1:个人用户、2:企业用户）*/
	private Integer lenderType;

	/** 质押或抵押的具体类型 **/
	private String guarantyType;

	/** 债权金额 **/
	private BigDecimal amount;

	/** 借款用途 **/
	private String debtUsage;

	/** 还款方式code **/
	private String returnType;

	/** 债权开始时间 **/
	private Date startDate;

	/** 债权结束时间 **/
	private Date endDate;

	/** 借款人 **/
	private Long borrowerId;

	/** 年化收益率 **/
	private BigDecimal annualizedRate;

	/** 线下年化收益 */
	private BigDecimal offlineAnnualizedRate;

	/** 手续费利率 **/
	private BigDecimal feeRate;

	/** 备注 **/
	private String remarks;

	/** 债权发布人 **/
	private Long publishId;

	/** 出借人 **/
	private Long lenderId;

	/** 债权状态，0-存盘，1-发布中，2-已还款 **/
	private Integer status;

	/** 起息日，T+1则存1 **/
	private Integer interestFrom;

	/** 删除标记 **/
	private Integer delFlag;

	/** 还款方式 **/
	private String returnSource;

	/** 借款用途 **/
	private String loanUse;

	/** 保证措施 **/
	private String safeguards;

	/****/
	private Date auditId;

	/****/
	private Date auditTime;

	/** 审核消息 **/
	private String auditMessage;

	/** 债权创建时间 **/
	private Date createdTime;

	/** 债权更新时间 **/
	private Date updatedTime;

	/** 风控备注 **/
	private String controlRemarks;

	/** ======extend==== **/

	/** 项目编号 **/
	private Long projectId;

	/** 项目名称 **/
	private String projectName;

	/** 项目状态 **/
	private String projectStatus;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	/**项目上线时间(年月日时分秒)**/
	private Date onlineTime;

	/** 借款人姓名 **/
	private String borrowerName;

	/** 债权人姓名 **/
	private String lenderName;

	/** 还款情况 **/
	private String repayment;

	/** 到期标识 **/
	private int endFlag;

	/** instalment **/
	private int instalment;
	
	/**发布人姓名**/
	private String publishName;
	
	/**借款人企业id**/
	private Long enterpriseId;
	
	/**出借人企业id**/
	private Long lenderEnterpriseId;
	
	/**企业名称**/
	private String enterpriseName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber == null ? null : serialNumber.trim();
	}

	public String getOriginalDebtNumber() {
		return originalDebtNumber;
	}

	public void setOriginalDebtNumber(String originalDebtNumber) {
		this.originalDebtNumber = originalDebtNumber == null ? null : originalDebtNumber.trim();
	}

	public String getDebtType() {
		return debtType;
	}

	public void setDebtType(String debtType) {
		this.debtType = debtType == null ? null : debtType.trim();
	}

	public Integer getBorrowerType() {
		return borrowerType;
	}

	public void setBorrowerType(Integer borrowerType) {
		this.borrowerType = borrowerType;
	}

	public String getGuarantyType() {
		return guarantyType;
	}

	public void setGuarantyType(String guarantyType) {
		this.guarantyType = guarantyType;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getDebtUsage() {
		return debtUsage;
	}

	public void setDebtUsage(String debtUsage) {
		this.debtUsage = debtUsage == null ? null : debtUsage.trim();
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType == null ? null : returnType.trim();
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

	public Long getBorrowerId() {
		return borrowerId;
	}

	public void setBorrowerId(Long borrowerId) {
		this.borrowerId = borrowerId;
	}

	public BigDecimal getAnnualizedRate() {
		return annualizedRate;
	}

	public void setAnnualizedRate(BigDecimal annualizedRate) {
		this.annualizedRate = annualizedRate;
	}

	public BigDecimal getOfflineAnnualizedRate() {
		return offlineAnnualizedRate;
	}

	public void setOfflineAnnualizedRate(BigDecimal offlineAnnualizedRate) {
		this.offlineAnnualizedRate = offlineAnnualizedRate;
	}

	public BigDecimal getFeeRate() {
		return feeRate;
	}

	public void setFeeRate(BigDecimal feeRate) {
		this.feeRate = feeRate;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks == null ? null : remarks.trim();
	}

	public Long getPublishId() {
		return publishId;
	}

	public void setPublishId(Long publishId) {
		this.publishId = publishId;
	}

	public Long getLenderId() {
		return lenderId;
	}

	public void setLenderId(Long lenderId) {
		this.lenderId = lenderId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getInterestFrom() {
		return interestFrom;
	}

	public void setInterestFrom(Integer interestFrom) {
		this.interestFrom = interestFrom;
	}

	public Integer getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}

	public String getReturnSource() {
		return returnSource;
	}

	public void setReturnSource(String returnSource) {
		this.returnSource = returnSource == null ? null : returnSource.trim();
	}

	public String getLoanUse() {
		return loanUse;
	}

	public void setLoanUse(String loanUse) {
		this.loanUse = loanUse == null ? null : loanUse.trim();
	}

	public String getSafeguards() {
		return safeguards;
	}

	public void setSafeguards(String safeguards) {
		this.safeguards = safeguards == null ? null : safeguards.trim();
	}

	public Date getAuditId() {
		return auditId;
	}

	public void setAuditId(Date auditId) {
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

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getBorrowerName() {
		return borrowerName;
	}

	public void setBorrowerName(String borrowerName) {
		this.borrowerName = borrowerName;
	}

	public String getLenderName() {
		return lenderName;
	}

	public void setLenderName(String lenderName) {
		this.lenderName = lenderName;
	}

	public String getRepayment() {
		return repayment;
	}

	public void setRepayment(String repayment) {
		this.repayment = repayment;
	}

	public Date getOnlineTime() {
		return onlineTime;
	}

	public void setOnlineTime(Date onlineTime) {
		this.onlineTime = onlineTime;
	}

	public String getControlRemarks() {
		return controlRemarks;
	}

	public void setControlRemarks(String controlRemarks) {
		this.controlRemarks = controlRemarks;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public String getAmountStr() {
		return FormulaUtil.formatCurrency(this.amount);
	}

	public int getEndFlag() {
		return endFlag;
	}

	public void setEndFlag(int endFlag) {
		this.endFlag = endFlag;
	}

	public String getProjectStatus() {
		return projectStatus;
	}

	public void setProjectStatus(String projectStatus) {
		this.projectStatus = projectStatus;
	}

	public int getInstalment() {
		return instalment;
	}

	public void setInstalment(int instalment) {
		this.instalment = instalment;
	}

	public String getPublishName() {
		return publishName;
	}

	public void setPublishName(String publishName) {
		this.publishName = publishName;
	}

	public Long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public Long getLenderEnterpriseId() {
		return lenderEnterpriseId;
	}

	public void setLenderEnterpriseId(Long lenderEnterpriseId) {
		this.lenderEnterpriseId = lenderEnterpriseId;
	}

	public String getEnterpriseName() {
		return enterpriseName;
	}

	public void setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
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
	
	
}