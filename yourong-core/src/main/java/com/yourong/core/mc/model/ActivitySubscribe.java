package com.yourong.core.mc.model;

import com.yourong.common.domain.AbstractBaseObject;

import java.util.Date;

/**
 * Created by alan.zheng on 2017/3/13.
 */
public class ActivitySubscribe extends AbstractBaseObject {
    private Long id;
    /**
     * 微信openid
     */
    private String openId;
    /**
     * 活动
     */
    private String activityName;
    /**
     * 唯一键
     */
    private String uniqueStr;
    /**
     * 创建时间
     */
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getUniqueStr() {
        return uniqueStr;
    }

    public void setUniqueStr(String uniqueStr) {
        this.uniqueStr = uniqueStr;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
