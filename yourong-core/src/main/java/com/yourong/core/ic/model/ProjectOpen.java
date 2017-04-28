package com.yourong.core.ic.model;

import com.yourong.common.domain.AbstractBaseObject;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by XR on 2016/11/1.
 */
public class ProjectOpen extends AbstractBaseObject {
    /**
     * id
     */
    private Long id;
    /**
     * 标的id
     */
    private String outBizNo;
    /**
     * 项目id
     */
    private Long projectId;
    
    /**
     * 对外类型(公司key)
     */
    private String openPlatformKey;
    /**
     * 借款金额
     */
    private BigDecimal totalAmount;
    /**
     * 年化利率
     */
    private BigDecimal annualizedRate;
    /**
     * 借款周期
     */
    private Integer borrowPeriod;
    /**
     * 借款周期类型（1-日；2-月；3-年；4-周）
     */
    private Integer borrowPeriodType;
    /**
     * 借款人姓名
     */
    private String borrowerName;
    /**
     * 身份证号
     */
    private String identityNumber;
    /**
     * 职业
     */
    private String job;
    /**
     * 月收入
     */
    private Integer income;
    /**
     * 个人基本信息
     */
    private String personalInfo;
    /**
     * 项目json字符串
     */
    private String projectbizJson;
    /**
     * 项目描述
     */
    private String shortDesc;
    /**
     * 借款详情用途
     */
    private String borrowDetail;
    /**
     * 项目附件json字符串
     */
    private String bscAttachmentsJson;
    /**
     * 商品sku
     */
    private String sku;
    /**
     * 状态， 1-已生成项目，2-未生成项目
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;
    /**
     * 拒绝原因
     */
    private String refuseCause;
    /**
     * 修改时间
     */
    private Date updateTime;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 删除状态， -1-删除，1-未删除
     */
    private Integer delFlag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOutBizNo() {
        return outBizNo;
    }

    public void setOutBizNo(String outBizNo) {
        this.outBizNo = outBizNo;
    }

    public String getOpenPlatformKey() {
		return openPlatformKey;
	}

	public void setOpenPlatformKey(String openPlatformKey) {
		this.openPlatformKey = openPlatformKey;
	}

	public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getAnnualizedRate() {
        return annualizedRate;
    }

    public void setAnnualizedRate(BigDecimal annualizedRate) {
        this.annualizedRate = annualizedRate;
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

    public String getBorrowerName() {
        return borrowerName;
    }

    public void setBorrowerName(String borrowerName) {
        this.borrowerName = borrowerName;
    }

    public String getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(String identityNumber) {
        this.identityNumber = identityNumber;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public Integer getIncome() {
        return income;
    }

    public void setIncome(Integer income) {
        this.income = income;
    }

    public String getPersonalInfo() {
        return personalInfo;
    }

    public void setPersonalInfo(String personalInfo) {
        this.personalInfo = personalInfo;
    }

    public String getProjectbizJson() {
        return projectbizJson;
    }

    public void setProjectbizJson(String projectbizJson) {
        this.projectbizJson = projectbizJson;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    public String getBorrowDetail() {
        return borrowDetail;
    }

    public void setBorrowDetail(String borrowDetail) {
        this.borrowDetail = borrowDetail;
    }

    public String getBscAttachmentsJson() {
        return bscAttachmentsJson;
    }

    public void setBscAttachmentsJson(String bscAttachmentsJson) {
        this.bscAttachmentsJson = bscAttachmentsJson;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRefuseCause() {
        return refuseCause;
    }

    public void setRefuseCause(String refuseCause) {
        this.refuseCause = refuseCause;
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

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }
}
