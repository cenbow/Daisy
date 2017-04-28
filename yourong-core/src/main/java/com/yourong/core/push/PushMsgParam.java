package com.yourong.core.push;

public class PushMsgParam {
	private Integer msgExpires;
	private int messageType;
	private String message;
	private int deviceType;
	private String channelId;
	
	public Integer getMsgExpires() {
		return msgExpires;
	}
	public void setMsgExpires(Integer msgExpires) {
		this.msgExpires = msgExpires;
	}
	public int getMessageType() {
		return messageType;
	}
	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

}
