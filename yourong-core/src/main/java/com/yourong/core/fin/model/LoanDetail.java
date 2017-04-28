package com.yourong.core.fin.model;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.yourong.common.util.FormulaUtil;
import com.yourong.core.ic.model.Project;

public class LoanDetail {
    /**主键**/
    private Long id;

    /**项目id**/
    private Long projectId;
    
    /**项目名称**/
    private String projectName;

    /**出借人姓名**/
    private String lenderName;
    
    /**放款时间**/
    private Date loanTime;

    /**放款金额**/
    private BigDecimal loanAmount;
    
    /**放款状态**/
    private Integer loanStatus;

    /**状态码(1:存盘,10:待审核,20:待发布,30:投资中,40:已暂停,50:已满额,60:已截止,70:已还款)**/
    private Integer projectStatus;
    
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    /**上线时间(年月日时分秒)**/
    private Date onlineTime;
    
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    /**销售截止日期(年月日时分秒)**/
    private Date saleEndTime;
    
    /**目前只有债权ID**/
    private Long debtId;
    
    /**投资总金额**/
    private BigDecimal totalAmount;
    
    /**用户支出**/
    private BigDecimal userPay;

    /**平台支出**/
    private BigDecimal platformPay;
    
    /**放款类型1-放款给出借人 2-放款给借款人*/
    private Integer loanType;
    
	/****/
    private String remarks;
    
    private Date updateTime;
    
    private Date createTime;

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

    public Date getLoanTime() {
        return loanTime;
    }

    public void setLoanTime(Date loanTime) {
        this.loanTime = loanTime;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }

    public BigDecimal getUserPay() {
        return userPay;
    }

    public void setUserPay(BigDecimal userPay) {
        this.userPay = userPay;
    }

    public BigDecimal getPlatformPay() {
        return platformPay;
    }

    public void setPlatformPay(BigDecimal platformPay) {
        this.platformPay = platformPay;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks == null ? null : remarks.trim();
    }
    
    public String getLoanAmountStr() {
		return FormulaUtil.formatCurrency(loanAmount);
	}
    
    public String getUserPayStr() {
    	return FormulaUtil.formatCurrency(userPay);
	}
    
    public String getPlatformPayStr() {
		return FormulaUtil.formatCurrency(platformPay);
	}
    

	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return projectName;
	}

	public Integer getLoanType() {
		return loanType;
	}

	public void setLoanType(Integer loanType) {
		this.loanType = loanType;
	}

	/**
	 * @param projectName the projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * @return the projectStatus
	 */
	public Integer getProjectStatus() {
		return projectStatus;
	}

	/**
	 * @param projectStatus the projectStatus to set
	 */
	public void setProjectStatus(Integer projectStatus) {
		this.projectStatus = projectStatus;
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
	 * @return the debtId
	 */
	public Long getDebtId() {
		return debtId;
	}

	/**
	 * @param debtId the debtId to set
	 */
	public void setDebtId(Long debtId) {
		this.debtId = debtId;
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
	 * @return the loanStatus
	 */
	public Integer getLoanStatus() {
		return loanStatus;
	}

	/**
	 * @param loanStatus the loanStatus to set
	 */
	public void setLoanStatus(Integer loanStatus) {
		this.loanStatus = loanStatus;
	}
	
	

	

	public Date getUpdateTime() {
		return updateTime;
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
}