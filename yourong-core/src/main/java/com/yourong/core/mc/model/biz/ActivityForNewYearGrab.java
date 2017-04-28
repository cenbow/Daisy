package com.yourong.core.mc.model.biz;

import java.util.Date;
import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * 除夕抢压岁钱
 * Created by alan.zheng on 2017/1/22.
 */
public class ActivityForNewYearGrab extends AbstractBaseObject {
    /**
     * 压岁钱人气值数量
     */
    private Integer popularity;
    /**
     * 压岁钱数量
     */
    private Integer grabCount;
    /**
     * 剩余压岁钱数量
     */
    private Integer grabedCount;
    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 是否可抢(true:可以在抢，false：不可抢)
     */
    private boolean grabed;

    private List<ActivityGrabResultBiz> grabResult;

    public Integer getPopularity() {
        return popularity;
    }

    public void setPopularity(Integer popularity) {
        this.popularity = popularity;
    }

    public Integer getGrabCount() {
        return grabCount;
    }

    public void setGrabCount(Integer grabCount) {
        this.grabCount = grabCount;
    }

    public Integer getGrabedCount() {
        return grabedCount;
    }

    public void setGrabedCount(Integer grabedCount) {
        this.grabedCount = grabedCount;
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

    public boolean isGrabed() {
        return grabed;
    }

    public void setGrabed(boolean grabed) {
        this.grabed = grabed;
    }

    public List<ActivityGrabResultBiz> getGrabResult() {
        return grabResult;
    }

    public void setGrabResult(List<ActivityGrabResultBiz> grabResult) {
        this.grabResult = grabResult;
    }
}
