package com.yourong.core.sales;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class SPParameter implements Serializable {
	private Long memberId;
	private List<SPProject> projects;
	private Date   startTime;
	private Date   endTime;
	private List<SPRuleMethod> spRuleMethod;
	private Long activityId;
	private Integer bizType;
	private Object biz;
	
	public Long getMemberId() {
		return memberId;
	}
	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}
	public List<SPRuleMethod> getSpRuleMethod() {
		return spRuleMethod;
	}
	public void setSpRuleMethod(List<SPRuleMethod> spRuleMethod) {
		this.spRuleMethod = spRuleMethod;
	}
	public Long getActivityId() {
		return activityId;
	}
	public void setActivityId(Long activityId) {
		this.activityId = activityId;
	}
	
	/**
	 * 检查方法是否配置
	 * @param methodName
	 * @return
	 */
	public SPRuleMethod getTargetMethod(String methodName){
		for(SPRuleMethod sprm : getSpRuleMethod()){
			if(sprm.getName().equals(methodName)){
				return sprm;
			}
		}
		return null;
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
	public List<SPProject> getProjects() {
		return projects;
	}
	public void setProjects(List<SPProject> projects) {
		this.projects = projects;
	}
	public Object getBiz() {
		return biz;
	}
	public void setBiz(Object biz) {
		this.biz = biz;
	}
	public Integer getBizType() {
		return bizType;
	}
	public void setBizType(Integer bizType) {
		this.bizType = bizType;
	}
	
}
