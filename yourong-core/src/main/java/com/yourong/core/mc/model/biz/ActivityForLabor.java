package com.yourong.core.mc.model.biz;

import com.yourong.common.domain.AbstractBaseObject;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by alan.zheng on 2017/4/11.
 */
public class ActivityForLabor extends AbstractBaseObject {
    /**
     * 签到日期
     */
    private List<Date> signDate;
    /**
     * 礼包领取日期
     */
    private List<Date> giftsDate;
    /**
     * 今日投资金额
     */
    private BigDecimal invest;
    /**
     * 51多劳多得赠送条件金额
     */
    private BigDecimal workInvest;
    /**
     * 51多劳多得礼包是否有领取资格
     */
    private boolean workRight;
    /**
     * 是否领取五一劳模奖章
     */
    private boolean receiveWorker;
    /**
     * 是否领取五一先锋奖章
     */
    private boolean receivePioneer;
    /**
     * 是否领取五一敬业奖章
     */
    private boolean receiveDedicated;
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

    public List<Date> getSignDate() {
        return signDate;
    }

    public void setSignDate(List<Date> signDate) {
        this.signDate = signDate;
    }

    public List<Date> getGiftsDate() {
        return giftsDate;
    }

    public void setGiftsDate(List<Date> giftsDate) {
        this.giftsDate = giftsDate;
    }

    public BigDecimal getInvest() {
        return invest;
    }

    public void setInvest(BigDecimal invest) {
        this.invest = invest;
    }

    public BigDecimal getWorkInvest() {
        return workInvest;
    }

    public void setWorkInvest(BigDecimal workInvest) {
        this.workInvest = workInvest;
    }

    public boolean isWorkRight() {
        return workRight;
    }

    public void setWorkRight(boolean workRight) {
        this.workRight = workRight;
    }

    public boolean isReceiveWorker() {
        return receiveWorker;
    }

    public void setReceiveWorker(boolean receiveWorker) {
        this.receiveWorker = receiveWorker;
    }

    public boolean isReceivePioneer() {
        return receivePioneer;
    }

    public void setReceivePioneer(boolean receivePioneer) {
        this.receivePioneer = receivePioneer;
    }

    public boolean isReceiveDedicated() {
        return receiveDedicated;
    }

    public void setReceiveDedicated(boolean receiveDedicated) {
        this.receiveDedicated = receiveDedicated;
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
}
