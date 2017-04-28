package com.yourong.core.msg.model.query;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

public class CustomMessagQuery {
	
	/**消息类型：1:短信、2:邮件、3:站内信**/
    private Integer msgType;

    /**状态：**/
    private Integer status;

    /**发送时间**/
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    private Date sendDate;

    /**过期时间**/
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    private Date invalidDate;
    
    /**通知类型：0-其它, 1-系统公告，2-操作通知, 3-收益通知, 4-:活动通知**/
    private Integer notifyType;

    /**目标用户：1-所有用户、2-投资过的用户、3-项目用户、4-自定义用户**/
    private Integer userType;

	public Integer getMsgType() {
		return msgType;
	}

	public void setMsgType(Integer msgType) {
		this.msgType = msgType;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getSendDate() {
		return sendDate;
	}

	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	public Date getInvalidDate() {
		return invalidDate;
	}

	public void setInvalidDate(Date invalidDate) {
		this.invalidDate = invalidDate;
	}

	public Integer getNotifyType() {
		return notifyType;
	}

	public void setNotifyType(Integer notifyType) {
		this.notifyType = notifyType;
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

    
}
