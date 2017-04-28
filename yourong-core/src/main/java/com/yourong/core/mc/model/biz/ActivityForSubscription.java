package com.yourong.core.mc.model.biz;

import com.yourong.common.domain.AbstractBaseObject;

import java.util.Date;

/**
 * 订阅号活动
 * Created by alan.zheng on 2017/2/13.
 */
public class ActivityForSubscription extends AbstractBaseObject {
    /**
     * 优惠券ids
     */
    private String templateIds;
    /**
     * 今日是否可领取
     */
    private boolean receive;
    /**开始时间**/
    private Date startTime;

    /**结束时间**/
    private Date endTime;
    /**
     * 活动状态
     */
    private Integer status;

    public String getTemplateIds() {
        return templateIds;
    }

    public void setTemplateIds(String templateIds) {
        this.templateIds = templateIds;
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

    public boolean isReceive() {
        return receive;
    }

    public void setReceive(boolean receive) {
        this.receive = receive;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
