package com.yourong.core.mc.model.biz;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class ActivityForPublicWelfare implements Serializable  {


	private Date startTime;
	private Date endTime;
	private Integer status;	
	private Integer point;
	private Integer popularity;
	private boolean received=false;
	
	private List<ActivityForMember> memberList;
	
	
	public List<ActivityForMember> getMemberList() {
		return memberList;
	}
	public void setMemberList(List<ActivityForMember> memberList) {
		this.memberList = memberList;
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
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getPoint() {
		return point;
	}
	public void setPoint(Integer point) {
		this.point = point;
	}
	public Integer getPopularity() {
		return popularity;
	}
	public void setPopularity(Integer popularity) {
		this.popularity = popularity;
	}
	public boolean isReceived() {
		return received;
	}
	public void setReceived(boolean received) {
		this.received = received;
	}
	
	
	
	
	
}
