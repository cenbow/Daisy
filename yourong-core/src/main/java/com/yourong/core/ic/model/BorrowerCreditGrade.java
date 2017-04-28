package com.yourong.core.ic.model;

import com.yourong.common.domain.AbstractBaseObject;

import java.util.Date;

/**
 * Created by alan.zheng on 2017/3/1.
 */
public class BorrowerCreditGrade extends AbstractBaseObject {
    /**
     * 主键
     */
    private Long Id;
    /**
     * 借款人id
     */
    private Long borrowerId;
    /**
     * 身份证号
     */
    private String identityNumber;
    /**
     * 借款人姓名
     */
    private String borrowerTrueName;
    /**
     * 借款人手机号
     */
    private Long borrowerMobile;
    /**
     * 信用等级
     */
    private String creditLevel;
    /**
     * 是否是黑名单
     */
    private Integer blackLevel;
    /**
     * 黑名单原因
     */
    private String blackReason;
    /**
     * 查询时间
     */
    private Date blackQueryTime;

    /**
     * 查询时间字符串
     */
    private String blackQueryTimeStr;
    /**
     * 信用等级描述
     */
    private String creditLevelDes;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 删除标记
     */
    private Integer delFlag;

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public Long getBorrowerId() {
        return borrowerId;
    }

    public void setBorrowerId(Long borrowerId) {
        this.borrowerId = borrowerId;
    }

    public String getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(String identityNumber) {
        this.identityNumber = identityNumber;
    }

    public String getBorrowerTrueName() {
        return borrowerTrueName;
    }

    public void setBorrowerTrueName(String borrowerTrueName) {
        this.borrowerTrueName = borrowerTrueName;
    }

    public Long getBorrowerMobile() {
        return borrowerMobile;
    }

    public void setBorrowerMobile(Long borrowerMobile) {
        this.borrowerMobile = borrowerMobile;
    }

    public String getCreditLevel() {
        return creditLevel;
    }

    public void setCreditLevel(String creditLevel) {
        this.creditLevel = creditLevel;
    }

    public Integer getBlackLevel() {
        return blackLevel;
    }

    public void setBlackLevel(Integer blackLevel) {
        this.blackLevel = blackLevel;
    }

    public String getBlackReason() {
        return blackReason;
    }

    public void setBlackReason(String blackReason) {
        this.blackReason = blackReason;
    }

    public Date getBlackQueryTime() {
        return blackQueryTime;
    }

    public void setBlackQueryTime(Date blackQueryTime) {
        this.blackQueryTime = blackQueryTime;
    }

    public String getBlackQueryTimeStr() {
        return blackQueryTimeStr;
    }

    public void setBlackQueryTimeStr(String blackQueryTimeStr) {
        this.blackQueryTimeStr = blackQueryTimeStr;
    }

    public String getCreditLevelDes() {
        return creditLevelDes;
    }

    public void setCreditLevelDes(String creditLevelDes) {
        this.creditLevelDes = creditLevelDes;
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
}
