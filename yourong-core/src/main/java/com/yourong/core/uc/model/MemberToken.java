package com.yourong.core.uc.model;

import com.yourong.common.domain.AbstractBaseObject;

import java.util.Date;

public class MemberToken extends AbstractBaseObject {
    /****/
    private Long id;

    /**用户ID**/
    private Long memberId;

    /**类型 1-android,2-ios,3-微信**/
    private Integer tokenType;

    /**令牌**/
    private String token;

    /**设备型号**/
    private String equipment;

    /**设备ID**/
    private String device;

    /**通道ID**/
    private String channelId;

    /**创建时间**/
    private Date createTime;

    /**修改时间**/
    private Date updateTime;

    /**删除标志**/
    private Integer delFlag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Integer getTokenType() {
        return tokenType;
    }

    public void setTokenType(Integer tokenType) {
        this.tokenType = tokenType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token == null ? null : token.trim();
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment == null ? null : equipment.trim();
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device == null ? null : device.trim();
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

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
}