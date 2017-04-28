package com.yourong.core.msg.model.biz;

import java.util.Date;

import org.apache.ibatis.type.Alias;

import com.alibaba.fastjson.annotation.JSONField;

@Alias(value = "MessageLogForMember")
public class MessageLogForMember {
	@JSONField(name="msgId")
	private Long id;
	@JSONField(name="time")
	private Date receiveDate;
	/** 冗余字段，消息类型：1-短信、2-邮件、3-站内信 **/
	private Integer msgType;
	/** 通知类型：0-其它, 1-系统公告，2-操作通知, 3-收益通知, 4-:活动通知 **/
	private Integer notifyType;
	
	/**业务类型**/
	private Integer serviceType;
	
	@JSONField(serialize=false)
	private Integer status;
	
	private String content;
	
	private boolean hasRead;
	
	private String title;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getReceiveDate() {
		return receiveDate;
	}

	public void setReceiveDate(Date receiveDate) {
		this.receiveDate = receiveDate;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public boolean isHasRead() {
		if(status == 0){
			hasRead = false;
		}else{
			hasRead = true;
		}
		return hasRead;
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

	/**
	 * @return the title
	 */
	public String getTitle() {
		if(serviceType==null){
			return "站内信";
		}
		if(serviceType==1){
			return "快投有奖通知";
		}
		if(serviceType==2){
			return "快投有奖通知";
		}
		if(serviceType==3){
			return "快投有奖通知";
		}
		if(serviceType==4){
			return "优惠券即将过期";
		}
		if(serviceType==5){
			return "项目通知";
		}
		if(serviceType==6){
			return "项目通知";
		}
		if(serviceType==9){
			return "自动投标";
		}
		if(serviceType==10){
			return "转让通知";
		}
		if(serviceType==11){
			return "转让通知";
		}
		if(serviceType==12){
			return "转让通知";
		}
		if(serviceType==13){
			return "转让通知";
		}
		if(serviceType==14){
			return "回款通知";
		}
		if(serviceType==15){
			return "回款通知";
		}
		if(serviceType==18){
			return "五重礼通知";
		}
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
}
