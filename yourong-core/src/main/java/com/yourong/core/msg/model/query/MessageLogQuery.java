package com.yourong.core.msg.model.query;

import java.util.Date;

import org.apache.ibatis.type.Alias;

import com.yourong.common.domain.BaseQueryParam;
import com.yourong.common.util.DateUtils;

@Alias("MessageLogQuery")
public class MessageLogQuery extends BaseQueryParam{
	private Long memberId;
	private Date receiveDate;
	private int notifyType = 0;
	private int[] notifyTypeIds;
	private int[] msgTypeIds;
	private int range = 1;
	private int filterDate;
	
	public Long getMemberId() {
		return memberId;
	}
	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}
	public Date getReceiveDate() {
		switch(filterDate){
			case 1://最近七天
				receiveDate =  DateUtils.addDate(DateUtils.getCurrentDate(), -7);
				break;
			case 2://最近三个月
				receiveDate =  DateUtils.addDate(DateUtils.getCurrentDate(), -30*3);
				break;
			case 3://最近一年
				receiveDate =  DateUtils.addDate(DateUtils.getCurrentDate(), -360);
				break;
			case 4://一年前
				receiveDate =  DateUtils.addDate(DateUtils.getCurrentDate(), -360);
				range = 0;
				break;
		}
		return receiveDate;
	}
	public int getNotifyType() {
		if(notifyTypeIds.length >= 4){
			notifyType = 1;
		}
		return notifyType;
	}
	public int[] getNotifyTypeIds() {
		return notifyTypeIds;
	}
	public void setNotifyTypeIds(int[] notifyTypeIds) {
		this.notifyTypeIds = notifyTypeIds;
	}
	
	/**
	 * @return the msgTypeIds
	 */
	public int[] getMsgTypeIds() {
		return msgTypeIds;
	}
	/**
	 * @param msgTypeIds the msgTypeIds to set
	 */
	public void setMsgTypeIds(int[] msgTypeIds) {
		this.msgTypeIds = msgTypeIds;
	}
	public int getRange() {
		return range;
	}
	public int getFilterDate() {
		return filterDate;
	}
	public void setFilterDate(int filterDate) {
		this.filterDate = filterDate;
	}
	
}
