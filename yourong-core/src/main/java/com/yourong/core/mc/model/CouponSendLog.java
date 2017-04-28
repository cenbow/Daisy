package com.yourong.core.mc.model;

import com.yourong.common.domain.AbstractBaseObject;

import java.util.Date;

/**
 * 优惠券发放日志
 * Created by alan.zheng on 2017/1/11.
 */
public class CouponSendLog extends AbstractBaseObject {
    /**
     * id
     */
    private Long id;
    /**
     * 用户id
     */
    private Long memberId;
    /**
     * 优惠券模板id
     */
    private Long templateId;
    /**
     * 短信内容
     */
    private String smsContent;
    /**
     * 唯一标示
     */
    private String sign;
    /**
     * 发送状态 0-未发送、1：已发送
     */
    private Integer sendStatus;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改时间
     */
    private Date updateTime;

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

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getSmsContent() {
        return smsContent;
    }

    public void setSmsContent(String smsContent) {
        this.smsContent = smsContent;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Integer getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(Integer sendStatus) {
        this.sendStatus = sendStatus;
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
}
