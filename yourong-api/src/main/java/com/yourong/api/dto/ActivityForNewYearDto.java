package com.yourong.api.dto;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.core.mc.model.biz.ActivityForNewYear;

import java.util.Date;

/**
 * Created by alan.zheng on 2017/1/3.
 */
public class ActivityForNewYearDto extends AbstractBaseObject {
    /**
     * 邀请注册用户数
     */
    private Integer referralCount;
    /**
     * 邀请注册且投资的用户数
     */
    private Integer referralTransactionCount;
    /**
     * 用户10月之后投资数
     */
    private Integer transactionCount;
    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 活动状态
     */
    private Integer status;
    /**
     * 用户注册时间
     */
    private Date registerTime;
    /**
     * 喜领压岁钱优惠券模板id集合
     */
    private String luckyMoneyTemplateIds;

    public Integer getReferralCount() {
        return referralCount;
    }

    public void setReferralCount(Integer referralCount) {
        this.referralCount = referralCount;
    }

    public Integer getReferralTransactionCount() {
        return referralTransactionCount;
    }

    public void setReferralTransactionCount(Integer referralTransactionCount) {
        this.referralTransactionCount = referralTransactionCount;
    }

    public Integer getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(Integer transactionCount) {
        this.transactionCount = transactionCount;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }

    public String getLuckyMoneyTemplateIds() {
        return luckyMoneyTemplateIds;
    }

    public void setLuckyMoneyTemplateIds(String luckyMoneyTemplateIds) {
        this.luckyMoneyTemplateIds = luckyMoneyTemplateIds;
    }
}
