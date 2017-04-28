package com.yourong.core.ic.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberBaseBiz;

public class DebtBiz extends AbstractBaseObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/****/
	private Long id;

	/** 公司债权编号 **/
	private String serialNumber;

	/** 原始债权编号 **/
	private String originalDebtNumber;

	/** 类型code **/
	private String debtType;
	
	/**借款人类型（1:个人用户、2:企业用户）*/
	private Integer borrowerType;

	/**出借人类型（1:个人用户、2:企业用户）*/
	private Integer lenderType;
	
	/** 类型code **/
	private String guarantyType;
	
	/** 债权金额 **/
	private BigDecimal amount;

	/** 借款用途 **/
	private String debtUsage;

	/** 还款方式code **/
	private String returnType;

	/** 债权开始时间 **/
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date startDate;

	/** 债权结束时间 **/
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date endDate;

	/** 借款人 **/
	private Long borrowerId;

	/** 年化收益率 **/
	private BigDecimal annualizedRate;
	
	/** 线下付息率 **/
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
	
	/**==附属属性的内容==**/

	/** 质押物 */
	private DebtPledge debtPledge;
	
	/** 抵押物 */
	private DebtCollateral debtCollateral;
	
	/** 债权本息表 */
	private List<DebtInterest> debtInterests;
	
	/** 附件信息 */
	private List<BscAttachment> bscAttachments;

	/** 借款人信息 */
	private MemberBaseBiz borrowMemberBaseBiz;
	
	/** 出借人信息 */
	private Member lenderMember;
	
	/** 出借人信息,包含企业信息 */
	private MemberBaseBiz lenderMemberBaseBiz;
	
	/** 是否分期  **/
	private int instalment;
	
	/**借款人企业id**/
	private Long enterpriseId;
	
	/**出借人企业id**/
	private Long lenderEnterpriseId;

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
		this.serialNumber = serialNumber;
	}

	public String getOriginalDebtNumber() {
		return originalDebtNumber;
	}

	public void setOriginalDebtNumber(String originalDebtNumber) {
		this.originalDebtNumber = originalDebtNumber;
	}

	public String getDebtType() {
		return debtType;
	}

	public void setDebtType(String debtType) {
		this.debtType = debtType;
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
		this.debtUsage = debtUsage;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
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
		this.remarks = remarks;
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
		this.returnSource = returnSource;
	}

	public String getLoanUse() {
		return loanUse;
	}

	public void setLoanUse(String loanUse) {
		this.loanUse = loanUse;
	}

	public String getSafeguards() {
		return safeguards;
	}

	public void setSafeguards(String safeguards) {
		this.safeguards = safeguards;
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
		this.auditMessage = auditMessage;
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

	public DebtPledge getDebtPledge() {
		return debtPledge;
	}

	public void setDebtPledge(DebtPledge debtPledge) {
		this.debtPledge = debtPledge;
	}

	public DebtCollateral getDebtCollateral() {
		return debtCollateral;
	}

	public void setDebtCollateral(DebtCollateral debtCollateral) {
		this.debtCollateral = debtCollateral;
	}

	public List<DebtInterest> getDebtInterests() {
		return debtInterests;
	}

	public void setDebtInterests(List<DebtInterest> debtInterests) {
		this.debtInterests = debtInterests;
	}

	public List<BscAttachment> getBscAttachments() {
		return bscAttachments;
	}

	public void setBscAttachments(List<BscAttachment> bscAttachments) {
		this.bscAttachments = bscAttachments;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public MemberBaseBiz getBorrowMemberBaseBiz() {
		return borrowMemberBaseBiz;
	}

	public void setBorrowMemberBaseBiz(MemberBaseBiz borrowMemberBaseBiz) {
		this.borrowMemberBaseBiz = borrowMemberBaseBiz;
	}
	
	

	public Long getLenderEnterpriseId() {
		return lenderEnterpriseId;
	}

	public void setLenderEnterpriseId(Long lenderEnterpriseId) {
		this.lenderEnterpriseId = lenderEnterpriseId;
	}

	/**
	 * @return the lenderMemberBaseBiz
	 */
	public MemberBaseBiz getLenderMemberBaseBiz() {
		return lenderMemberBaseBiz;
	}

	/**
	 * @param lenderMemberBaseBiz the lenderMemberBaseBiz to set
	 */
	public void setLenderMemberBaseBiz(MemberBaseBiz lenderMemberBaseBiz) {
		this.lenderMemberBaseBiz = lenderMemberBaseBiz;
	}

	public Member getLenderMember() {
		return lenderMember;
	}

	public void setLenderMember(Member lenderMember) {
		this.lenderMember = lenderMember;
	}

	public int getInstalment() {
		return instalment;
	}

	public void setInstalment(int instalment) {
		this.instalment = instalment;
	}

	public Long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
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
