package com.yourong.core.mc.model.biz;

import com.yourong.common.domain.AbstractBaseObject;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by alan.zheng on 2017/2/16.
 */
public class ActivityForWomensDay extends AbstractBaseObject {
    /**
     * 38节日期
     */
    private String womensDate;
    /**
     * 签到优惠券id
     */
    private String signTemplateIds;
    /**
     * 投资累计优惠券id
     */
    private String transactionTemplateIds;
    /**
     * 登录赠送优惠券id
     */
    private String loginTemplateIds;
    /**
     * 人气值数量
     */
    private Integer popularity;
    /**
     * 今日投资额
     */
    private BigDecimal investment;
    /**
     * 是否已领取专属礼包
     */
    private boolean bag;
    /**
     * 性别
     */
    private Integer sex;
    /**
     * 签到日期
     */
    private List<Date> signDate;
    /**
     * 开始日期
     */
    private Date startDate;
    /**
     * 结束日期
     */
    private Date endDate;

    public String getWomensDate() {
        return womensDate;
    }

    public void setWomensDate(String womensDate) {
        this.womensDate = womensDate;
    }

    public String getSignTemplateIds() {
        return signTemplateIds;
    }

    public void setSignTemplateIds(String signTemplateIds) {
        this.signTemplateIds = signTemplateIds;
    }

    public String getTransactionTemplateIds() {
        return transactionTemplateIds;
    }

    public void setTransactionTemplateIds(String transactionTemplateIds) {
        this.transactionTemplateIds = transactionTemplateIds;
    }

    public String getLoginTemplateIds() {
        return loginTemplateIds;
    }

    public void setLoginTemplateIds(String loginTemplateIds) {
        this.loginTemplateIds = loginTemplateIds;
    }

    public Integer getPopularity() {
        return popularity;
    }

    public void setPopularity(Integer popularity) {
        this.popularity = popularity;
    }

    public BigDecimal getInvestment() {
        return investment;
    }

    public void setInvestment(BigDecimal investment) {
        this.investment = investment;
    }

    public boolean isBag() {
        return bag;
    }

    public void setBag(boolean bag) {
        this.bag = bag;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public List<Date> getSignDate() {
        return signDate;
    }

    public void setSignDate(List<Date> signDate) {
        this.signDate = signDate;
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
}
