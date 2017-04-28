package com.yourong.core.msg.model;

import java.util.Date;

public class MessageLog {
    /****/
    private Long id;

    /**用户编号**/
    private Long memberId;

    /**消息内容编号**/
    private Long msgBodyId;

    /**接收时间**/
    private Date receiveDate;

    /**状态：0-未读、1：已读**/
    private Integer status;

    /**删除标记：0-删除、1-正常**/
    private Integer delFlag;

    /**冗余字段，消息类型：1-短信、2-邮件、3-站内信**/
    private Integer msgType;

    /**通知类型：0-其它, 1-系统公告，2-操作通知, 3-收益通知, 4-:活动通知**/
    private Integer notifyType;
    
    /*业务类型*/
    private Integer serviceType;

    /**消息来源**/
    private Integer msgSource;
    
    private Long msgSourceId;

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

    public Long getMsgBodyId() {
        return msgBodyId;
    }

    public void setMsgBodyId(Long msgBodyId) {
        this.msgBodyId = msgBodyId;
    }

    public Date getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(Date receiveDate) {
        this.receiveDate = receiveDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public Integer getMsgType() {
        return msgType;
    }

    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }

    public Integer getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(Integer notifyType) {
        this.notifyType = notifyType;
    }

    public Integer getMsgSource() {
        return msgSource;
    }

    public void setMsgSource(Integer msgSource) {
        this.msgSource = msgSource;
    }

	public Long getMsgSourceId() {
		return msgSourceId;
	}

	public void setMsgSourceId(Long msgSourceId) {
		this.msgSourceId = msgSourceId;
	}

	/**
	 * @return the serviceType
	 */
	public Integer getServiceType() {
		return serviceType;
	}

	/**
	 * @param serviceType the serviceType to set
	 */
	public void setServiceType(Integer serviceType) {
		this.serviceType = serviceType;
	}
	
    
}