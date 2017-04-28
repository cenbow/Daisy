package com.yourong.core.ic.model.query;

import com.yourong.common.domain.BaseQueryParam;
import com.yourong.common.util.StringUtil;

public class ProjectBorrowQuery extends BaseQueryParam {

	private Integer status;
	private String projectType;
	private String statusCode;
	private Long borrowerId;
	private boolean isAppQuery = false;
	private boolean isbuyCarNotShow;
	private Integer type;

	public String getProjectType() {
		return projectType;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	
	public boolean isAppQuery() {
		return isAppQuery;
	}

	public void setAppQuery(boolean isAppQuery) {
		this.isAppQuery = isAppQuery;
	}

	public Long getBorrowerId() {
		return borrowerId;
	}

	public void setBorrowerId(Long borrowerId) {
		this.borrowerId = borrowerId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	/**
	 * 状态转换
	 * @param statusCode
	 * @return
	 */
	private String formatStatus(String statusCode){
		String s = null;
		switch(statusCode){
			case "investing":
				s = "30";
				break;
			case "performance":
				s = "999";
				break;
			case "repayment":
				s = "70";
				break;
			default:
				s = null;
				break;
		}
		return s;
	}
	
	/**
	 * 状态转换成中文
	 * @return
	 */
	private String formatStatus(){
		String s ="";
		if(getStatusCode() != null && !getStatusCode().equals("all")){
			switch(getStatusCode()){
				case "investing":
					s = "投资中";
					break;
				case "performance":
					s = "履约中";
					break;
				case "repayment":
					s = "已还款";
					break;
				default:
					s = "";
					break;
			}
		}
		return s;
	}

	/**
	 * 项目类型转换成中文
	 * @return
	 */
	private String formatProjectType(){
		if(StringUtil.isNotBlank(getProjectType())){
			if(getProjectType().equals("car")){
				return "车有融项目";
			}else if(getProjectType().equals("house")){
				return "房有融项目";
			}else if(getProjectType().equals("newCar")){
				return "新车融项目";
			}
		}
		return "理财项目";
	}
	
	/**
	 * 项目列表页面seo title部分
	 * @return
	 */
	public String getSeoTitle(){
		StringBuffer sb = new StringBuffer();
		if(StringUtil.isNotBlank(formatStatus())){
			sb.append(formatStatus()).append("-").append(formatProjectType()).append("-");
		}else{
			sb.append(formatProjectType()).append("-");
		}
		return sb.toString();
	}

	/**
	 * 项目列表页面seo words部分
	 * @return
	 */
	public String getSeoKeywords(){
		if(StringUtil.isNotBlank(formatStatus())){
			return formatStatus()+",";
		}
		return "";
	}

	public boolean isIsbuyCarNotShow() {
		return isbuyCarNotShow;
	}

	public void setIsbuyCarNotShow(boolean isbuyCarNotShow) {
		this.isbuyCarNotShow = isbuyCarNotShow;
	}


}
