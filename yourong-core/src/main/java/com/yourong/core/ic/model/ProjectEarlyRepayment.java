package com.yourong.core.ic.model;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.util.DateUtils;

public class ProjectEarlyRepayment {

	
	/**项目id**/
    private Long id;
    
    /**项目名称**/
    private String name;
    
    /**借款人姓名**/
    private String borrowerName;
    
    /**借款人手机号**/
    private String borrowerMobile;
    
    /**借款人id**/
    private Long borrowerId;

    /**借款人类型（1：个人；2-企业）**/
    private Integer borrowerType;

    /**企业id**/
    private Long enterpriseId;
    
    /**项目开始日期(年月日)**/
    private Date startDate;

    /**还款时间(年月日)**/
    private Date endDate;
    
    /**状态码(1:存盘,10:待审核,20:待发布,30:投资中,40:已暂停,50:已满额,60:已截止,70:已还款)**/
    private Integer status;
    
    /**提前还款 **/
    private Integer prepayment;
    
    /**提前还款日期**/
    private Date prepaymentTime;
    
    /**距离提前还款日**/
    private Integer prepaymentDay;
    
    /**操作人ID**/
    private Long operateId;
    
    /**操作人姓名**/
    private String operateName;
    
    /**提前还款日期**/
    private Date operateDate;
    
    /**操作备注**/
    private String operateRemarks;
    
    /** 剩余需支付本金 **/
	private BigDecimal remainingTotalPrincipal;

	/** 剩余需支付利息 **/
	private BigDecimal remainingTotalInterest;

	/** 借款人支付投资人利息 **/
	private BigDecimal remainingTotalInterestByBorrower;
	
	/** 平台贴息 **/
	private BigDecimal remainingExtraInterest;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBorrowerName() {
		return borrowerName;
	}

	public void setBorrowerName(String borrowerName) {
		this.borrowerName = borrowerName;
	}

	public String getBorrowerMobile() {
		return borrowerMobile;
	}

	public void setBorrowerMobile(String borrowerMobile) {
		this.borrowerMobile = borrowerMobile;
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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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

	public Long getOperateId() {
		return operateId;
	}

	public void setOperateId(Long operateId) {
		this.operateId = operateId;
	}

	public String getOperateName() {
		return operateName;
	}

	public void setOperateName(String operateName) {
		this.operateName = operateName;
	}

	public Date getOperateDate() {
		return operateDate;
	}

	public void setOperateDate(Date operateDate) {
		this.operateDate = operateDate;
	}

	public String getOperateRemarks() {
		return operateRemarks;
	}

	public void setOperateRemarks(String operateRemarks) {
		this.operateRemarks = operateRemarks;
	}

	public BigDecimal getRemainingTotalPrincipal() {
		return remainingTotalPrincipal;
	}

	public void setRemainingTotalPrincipal(BigDecimal remainingTotalPrincipal) {
		this.remainingTotalPrincipal = remainingTotalPrincipal;
	}

	public BigDecimal getRemainingTotalInterest() {
		return remainingTotalInterest;
	}

	public void setRemainingTotalInterest(BigDecimal remainingTotalInterest) {
		this.remainingTotalInterest = remainingTotalInterest;
	}

	public BigDecimal getRemainingTotalInterestByBorrower() {
		return remainingTotalInterestByBorrower;
	}

	public void setRemainingTotalInterestByBorrower(
			BigDecimal remainingTotalInterestByBorrower) {
		this.remainingTotalInterestByBorrower = remainingTotalInterestByBorrower;
	}

	public BigDecimal getRemainingExtraInterest() {
		return remainingExtraInterest;
	}

	public void setRemainingExtraInterest(BigDecimal remainingExtraInterest) {
		this.remainingExtraInterest = remainingExtraInterest;
	}

	public Integer getPrepaymentDay() {
		if(prepaymentTime!=null){
			return DateUtils.daysOfTwo(DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3), prepaymentTime)  ;
		}
		return null;
	}

	public void setPrepaymentDay(Integer prepaymentDay) {
		this.prepaymentDay = prepaymentDay;
	}
    
}
