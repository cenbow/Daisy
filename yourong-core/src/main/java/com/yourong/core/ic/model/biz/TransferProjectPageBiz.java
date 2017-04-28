package com.yourong.core.ic.model.biz;

import com.yourong.common.domain.AbstractBaseObject;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 转让项目列表biz
 * Created by XR on 2016/12/14.
 */
public class TransferProjectPageBiz extends AbstractBaseObject {
    /**
     * 转让项目ID
     */
    private Long id;

    /**
     * 转让项目名称
     */
    private String transferName;
    /**
     * 原项目ID
     */
    private Long projectId;

    /**
     * 转让人
     */
    private String transferMember;

    /**
     * 转让状态
     */
    private Integer status;
    /**
     * 原始项目名称
     */
    private String projectName;
    /**
     * 原始借款人
     */
    private String borrowName;
    /**
     * 转让价格
     */
    private BigDecimal transferAmount;
    /**
     * 转让人获得费用
     */
    private BigDecimal income;
    /**
     * 手续费率
     */
    private BigDecimal transferRate;
    /**
     * 手续费
     */
    private BigDecimal serviceFee;
    
    /**
     * 转让结束时间
     */
    private Date transferEndDate;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransferName() {
        return transferName;
    }

    public void setTransferName(String transferName) {
        this.transferName = transferName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getTransferMember() {
        return transferMember;
    }

    public void setTransferMember(String transferMember) {
        this.transferMember = transferMember;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getBorrowName() {
        return borrowName;
    }

    public void setBorrowName(String borrowName) {
        this.borrowName = borrowName;
    }

    public BigDecimal getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(BigDecimal transferAmount) {
        this.transferAmount = transferAmount;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public BigDecimal getTransferRate() {
        return transferRate;
    }

    public void setTransferRate(BigDecimal transferRate) {
        this.transferRate = transferRate;
    }

    public BigDecimal getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(BigDecimal serviceFee) {
        this.serviceFee = serviceFee;
    }
    
	public Date getTransferEndDate() {
		return transferEndDate;
	}

	public void setTransferEndDate(Date transferEndDate) {
		this.transferEndDate = transferEndDate;
	}
}
