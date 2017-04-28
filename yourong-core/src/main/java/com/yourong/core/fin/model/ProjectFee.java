package com.yourong.core.fin.model;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.yourong.common.enums.TypeEnum;

public class ProjectFee {
    /****/
    private Long id;

    /**项目ID**/
    private Long projectId;

    /**服务费**/
    private BigDecimal amount;

    /**实际收取付金额**/
    private BigDecimal realManagementFee;

    /**服务费状态[1:待收取，2：收取中，3 已收取 ，4 归还中 ，5 已归还]*/
    private Integer feeStatus;
    
    /**服务费类型（1：项目管理费 2：风险金 3：保证金 4:介绍费）**/
    private Integer feeType;

    /**收取时间**/
    private Date gatherTime;
    
    /**归还时间**/
    private Date returnTime;

    /**备注**/
    private String remarks;

    /****/
    private Date updateTime;

    /****/
    private Date createTime;

    /**-1:删除，1：正常**/
    private Integer delFlag;

    /**项目名称**/
    private String projectName;

    /**担保方式（pledge-质押；collateral-抵押；credit-信用）**/
    private String securityType;
    
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    /**上线时间(年月日时分秒)**/
    private Date onlineTime;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    /**销售截止日期(年月日时分秒)**/
    private Date saleEndTime;
	
	/** 项目总额 */
	private BigDecimal totalAmount;
	
	/**借款周期**/
    private Integer borrowPeriod;
	
    /**借款周期类型（1-日；2-月；3-年）**/
    private Integer borrowPeriodType;
    
    /**年化利率**/
	private BigDecimal annualizedRate;
    
	 /**借款人姓名**/
    private String borrowerName;
	
    /*借款人手机号*/
    private Long mobile;
    
    /**管理费用*（取自项目表）*/
    private BigDecimal manageFeeRate;
    
    /**保证金**/
    private BigDecimal guaranteeFeeRate;
    
    /**风险金**/
    private BigDecimal riskFeeRate;
    
    /**
     * 介绍费
     */
    private BigDecimal introducerFeeRate;
    
    /**身份证号**/
    private String identityNumber;
    
    /**社会统一代码**/
	private String organizationCode;
	
	 /**借款人类型（1：个人；2-企业）**/
    private Integer borrowerType;
    
    /*收取人id*/
    private Long memberId;
    
    private String chargeMemberName;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
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

	public String getChargeMemberName() {
		return chargeMemberName;
	}

	public void setChargeMemberName(String chargeMemberName) {
		this.chargeMemberName = chargeMemberName;
	}

	public BigDecimal getRealManagementFee() {
        return realManagementFee;
    }

    public void setRealManagementFee(BigDecimal realManagementFee) {
        this.realManagementFee = realManagementFee;
    }

   

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public Date getGatherTime() {
        return gatherTime;
    }

    public void setGatherTime(Date gatherTime) {
        this.gatherTime = gatherTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks == null ? null : remarks.trim();
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    
    public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Integer getFeeStatus() {
		return feeStatus;
	}

	public void setFeeStatus(Integer feeStatus) {
		this.feeStatus = feeStatus;
	}

	public Integer getFeeType() {
		return feeType;
	}

	public void setFeeType(Integer feeType) {
		this.feeType = feeType;
	}

	public Date getReturnTime() {
		return returnTime;
	}

	public void setReturnTime(Date returnTime) {
		this.returnTime = returnTime;
	}

	public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @param projectName the projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * @return the securityType
	 */
	public String getSecurityType() {
		return securityType;
	}

	/**
	 * @param securityType the securityType to set
	 */
	public void setSecurityType(String securityType) {
		this.securityType = securityType;
	}

	/**
	 * @return the onlineTime
	 */
	public Date getOnlineTime() {
		return onlineTime;
	}

	/**
	 * @param onlineTime the onlineTime to set
	 */
	public void setOnlineTime(Date onlineTime) {
		this.onlineTime = onlineTime;
	}

	

	/**
	 * @return the saleEndTime
	 */
	public Date getSaleEndTime() {
		return saleEndTime;
	}

	/**
	 * @param saleEndTime the saleEndTime to set
	 */
	public void setSaleEndTime(Date saleEndTime) {
		this.saleEndTime = saleEndTime;
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
	 * @return the borrowPeriod
	 */
	public Integer getBorrowPeriod() {
		return borrowPeriod;
	}

	/**
	 * @param borrowPeriod the borrowPeriod to set
	 */
	public void setBorrowPeriod(Integer borrowPeriod) {
		this.borrowPeriod = borrowPeriod;
	}

	/**
	 * @return the borrowPeriodType
	 */
	public Integer getBorrowPeriodType() {
		return borrowPeriodType;
	}

	/**
	 * @param borrowPeriodType the borrowPeriodType to set
	 */
	public void setBorrowPeriodType(Integer borrowPeriodType) {
		this.borrowPeriodType = borrowPeriodType;
	}

	
	/**
	 * @return the annualizedRate
	 */
	public BigDecimal getAnnualizedRate() {
		return annualizedRate;
	}

	/**
	 * @param annualizedRate the annualizedRate to set
	 */
	public void setAnnualizedRate(BigDecimal annualizedRate) {
		this.annualizedRate = annualizedRate;
	}

	/**
	 * @return the borrowerName
	 */
	public String getBorrowerName() {
		return borrowerName;
	}

	/**
	 * @param borrowerName the borrowerName to set
	 */
	public void setBorrowerName(String borrowerName) {
		this.borrowerName = borrowerName;
	}

	/**
	 * @return the mobile
	 */
	public Long getMobile() {
		return mobile;
	}

	/**
	 * @param mobile the mobile to set
	 */
	public void setMobile(Long mobile) {
		this.mobile = mobile;
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
	

	public String getProfitPeriod() {
		if(this.borrowPeriod!=null&&this.borrowPeriodType!=null){
//			return this.borrowPeriod.toString()+(this.borrowPeriodType==TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_DAY.getType()?"天"
//					:(this.borrowPeriodType==TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_MONTH.getType()?"个月":"年"));
			
			String borrowPeriodResult = this.borrowPeriod.toString();
			if(TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_DAY.getType()==borrowPeriodType){
				return borrowPeriodResult + "天";
			}
			if(TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_MONTH.getType()==borrowPeriodType){
				return borrowPeriodResult + "个月";
			}
			if(TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_YEAR.getType()==borrowPeriodType){
				return borrowPeriodResult + "年";
			}
			if(TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_WEEK.getType()==borrowPeriodType){
				return borrowPeriodResult + "周";
			}
		}
		return "";
	}

	/**
	 * @return the identityNumber
	 */
	public String getIdentityNumber() {
		return identityNumber;
	}

	/**
	 * @param identityNumber the identityNumber to set
	 */
	public void setIdentityNumber(String identityNumber) {
		this.identityNumber = identityNumber;
	}

	/**
	 * @return the organizationCode
	 */
	public String getOrganizationCode() {
		return organizationCode;
	}

	/**
	 * @param organizationCode the organizationCode to set
	 */
	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}

	/**
	 * @return the borrowerType
	 */
	public Integer getBorrowerType() {
		return borrowerType;
	}

	/**
	 * @param borrowerType the borrowerType to set
	 */
	public void setBorrowerType(Integer borrowerType) {
		this.borrowerType = borrowerType;
	}
	
}