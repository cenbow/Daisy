package com.yourong.core.uc.model.biz;

import java.io.Serializable;

import com.yourong.common.constant.Constant;

/**
 * 用于发送消息的通知设置biz
 * @author Leon Ray
 * 2014年10月13日-下午7:20:57
 */
public class MemberNotifySettingsForSendMsg implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 3824549909227941574L;

    /**用户id**/
    private Long memberId;
    
    /**发送对象: 短信-手机号，邮件-邮箱地址**/
    private String sendForward;
    
    /**发送类型**/
    private int sendType = Constant.MSG_SEND_BY_MEMBERID;
    
    /**
     * 通知类型
     */
    private int notifyType;

    /**是否需要发送站内信通知**/
    private boolean isNeedSendSystemNotice = false;

    /**是否需要发送短信通知 **/
    private boolean isNeedSendSMSNotice = false;

    /**是否需要发送 邮件通知**/
    private boolean isNeedSendEmailNotice = false;
    
    /**是否需要发送 百度推送**/
    private boolean isNeedBaiduPushNotice = false;
    
    /**是否需要发送app站内信通知**/
    private boolean isNeedSendAPPNotice = false;
    
    /**
     * 需要发送的信息
     */
    private String message;
    
    private boolean isMsgTemplate = false;
    
    /**消息类型**/
    private int msgType;
    
    /**是否营销短信**/
    private boolean isMarketingSMS = false;

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}
	
	

	public int getNotiyType() {
		return notifyType;
	}

	public void setNotifyType(int notifyType) {
		this.notifyType = notifyType;
	}

	public boolean isNeedSendSystemNotice() {
		return isNeedSendSystemNotice;
	}

	public void setNeedSendSystemNotice(boolean isNeedSendSystemNotice) {
		this.isNeedSendSystemNotice = isNeedSendSystemNotice;
	}

	public boolean isNeedSendSMSNotice() {
		return isNeedSendSMSNotice;
	}

	public void setNeedSendSMSNotice(boolean isNeedSendSMSNotice) {
		this.isNeedSendSMSNotice = isNeedSendSMSNotice;
	}

	public boolean isNeedSendEmailNotice() {
		return isNeedSendEmailNotice;
	}

	public void setNeedSendEmailNotice(boolean isNeedSendEmailNotice) {
		this.isNeedSendEmailNotice = isNeedSendEmailNotice;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public boolean isMsgTemplate() {
		return isMsgTemplate;
	}

	public void setMsgTemplate(boolean isMsgTemplate) {
		this.isMsgTemplate = isMsgTemplate;
	}
	
	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}
	
	public boolean isMarketingSMS() {
		return isMarketingSMS;
	}

	public void setMarketingSMS(boolean isMarketingSMS) {
		this.isMarketingSMS = isMarketingSMS;
	}

	public String getSendForward() {
		return sendForward;
	}

	public void setSendForward(String sendForward) {
		this.sendForward = sendForward;
	}

	public Integer getSendType() {
		return sendType;
	}

	public void setSendType(Integer sendType) {
		this.sendType = sendType;
	}

	public boolean isNeedBaiduPushNotice() {
		return isNeedBaiduPushNotice;
	}

	public void setNeedBaiduPushNotice(boolean isNeedBaiduPushNotice) {
		this.isNeedBaiduPushNotice = isNeedBaiduPushNotice;
	}
	
	

	/**
	 * @return the isNeedSendAPPNotice
	 */
	public boolean isNeedSendAPPNotice() {
		return isNeedSendAPPNotice;
	}

	/**
	 * @param isNeedSendAPPNotice the isNeedSendAPPNotice to set
	 */
	public void setNeedSendAPPNotice(boolean isNeedSendAPPNotice) {
		this.isNeedSendAPPNotice = isNeedSendAPPNotice;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MemberNotifySettingsForSendMsg [memberId=");
		builder.append(memberId);
		builder.append(", notifyType=");
		builder.append(notifyType);
		builder.append(", isNeedSendSystemNotice=");
		builder.append(isNeedSendSystemNotice);
		builder.append(", isNeedSendSMSNotice=");
		builder.append(isNeedSendSMSNotice);
		builder.append(", isNeedSendEmailNotice=");
		builder.append(isNeedSendEmailNotice);
		builder.append(", isNeedBaiduPushNotice=");
		builder.append(isNeedBaiduPushNotice);
		builder.append(", isNeedSendAPPNotice=");
		builder.append(isNeedSendAPPNotice);
		builder.append(", message=");
		builder.append(message);
		builder.append("]");
		return builder.toString();
	}
    
    

}