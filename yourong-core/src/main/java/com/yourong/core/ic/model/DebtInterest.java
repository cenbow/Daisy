package com.yourong.core.ic.model;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.yourong.common.domain.AbstractBaseObject;

public class DebtInterest extends AbstractBaseObject {
    /**
	 * 债权本息表
	 */
	private static final long serialVersionUID = 6185227493502905513L;

	/****/
    private Long id;

    /**债权id**/
    private Long debtId;
    
    /**项目id**/
    private Long projectId;

    /**计息开始日期**/
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date startDate;

    /**计息结束日期**/
	@DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    /**单位本金**/
    private BigDecimal unitPrincipal;

    /**单位利息**/
    private BigDecimal unitInterest;

    /**应付利息**/
    private BigDecimal payableInterest;

    /**应付本金**/
    private BigDecimal payablePrincipal;

    /**实付本金**/
    private BigDecimal realPayPrincipal;

    /**实付利息**/
    private BigDecimal realPayInterest;

    /**0 - 待支付，1-已支付 ，2 - 部分支付 3-已逾期**/
    private Integer status;

    /****/
    private Date payTime;

    /****/
    private Date createTime;

    /****/
    private Date updateTime;

    /**删除标记**/
    private Integer delFlag; 
    
    /**所属期数**/
    private String periods;
    
    /**实际还款时间**/
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date payDate;
    
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

    public BigDecimal getUnitPrincipal() {
        return unitPrincipal;
    }

    public Date getPayDate() {
		return payDate;
	}

	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}

	public void setUnitPrincipal(BigDecimal unitPrincipal) {
        this.unitPrincipal = unitPrincipal;
    }

    public BigDecimal getUnitInterest() {
        return unitInterest;
    }

    public void setUnitInterest(BigDecimal unitInterest) {
        this.unitInterest = unitInterest;
    }

    public BigDecimal getPayableInterest() {
        return payableInterest;
    }

    public void setPayableInterest(BigDecimal payableInterest) {
        this.payableInterest = payableInterest;
    }

    public BigDecimal getPayablePrincipal() {
        return payablePrincipal;
    }

    public void setPayablePrincipal(BigDecimal payablePrincipal) {
        this.payablePrincipal = payablePrincipal;
    }

    public BigDecimal getRealPayPrincipal() {
        return realPayPrincipal;
    }

    public void setRealPayPrincipal(BigDecimal realPayPrincipal) {
        this.realPayPrincipal = realPayPrincipal;
    }

    public BigDecimal getRealPayInterest() {
        return realPayInterest;
    }

    public void setRealPayInterest(BigDecimal realPayInterest) {
        this.realPayInterest = realPayInterest;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
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



	public Integer getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	
	public BigDecimal getPrincipalAndInterest(){
		if(payableInterest == null){
			payableInterest = BigDecimal.ZERO;
		}
		if(payablePrincipal == null){
			payablePrincipal = BigDecimal.ZERO;
		}
		return payableInterest.add(payablePrincipal);
	}
	public BigDecimal getRealPrincipalAndInterest(){
		if(realPayInterest == null){
			realPayInterest = BigDecimal.ZERO;
		}
		if(realPayPrincipal == null){
			realPayPrincipal = BigDecimal.ZERO;
		}
		return realPayInterest.add(realPayPrincipal);
	}
	public String getPeriods() {
		return periods;
	}

	public void setPeriods(String periods) {
		this.periods = periods;
	}
}