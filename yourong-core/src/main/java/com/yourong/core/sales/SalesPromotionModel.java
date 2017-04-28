package com.yourong.core.sales;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class SalesPromotionModel implements Serializable {
	private Long   id;
	private String name;
	private Date   startTime;
	private Date   endTime;
	private List<SPRule> elements;
	private List<SPGift> giftList;
	private List<SPProject> projects;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public List<SPRule> getElements() {
		return elements;
	}
	public void setElements(List<SPRule> elements) {
		this.elements = elements;
	}
	public List<SPGift> getGiftList() {
		return giftList;
	}
	public void setGiftList(List<SPGift> giftList) {
		this.giftList = giftList;
	}
	public List<SPProject> getProjects() {
		return projects;
	}
	public void setProjects(List<SPProject> projects) {
		this.projects = projects;
	}
	
}
