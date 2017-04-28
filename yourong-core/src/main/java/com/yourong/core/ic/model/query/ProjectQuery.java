package com.yourong.core.ic.model.query;

import com.yourong.common.domain.BaseQueryParam;
import com.yourong.common.util.StringUtil;

public class ProjectQuery extends BaseQueryParam {

	private String status;
	private String projectType;
	private String statusCode;
	private boolean isAppQuery = false;
	private boolean isbuyCarNotShow;
	private String investTypeCode;
	private Integer investType;
	private String querySource;
	private String investArea;
	private Integer number;
	
	
	
	
	
	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getInvestArea() {
		return investArea;
	}

	public void setInvestArea(String investArea) {
		this.investArea = investArea;
	}

	private Integer projectCategory;
	private String orderSource;
	
	public String getStatus() {
		if(status != null && status.equals("300")){
			return status;
		}
		if(getStatusCode() != null){
			return formatStatus(getStatusCode());
		}
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getProjectType() {
		return projectType;
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
			case "lose":
				s = "80";
				break;
			case "all":
				s = null;
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
				case "losing":
					s = "流标";
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

	public String getInvestTypeCode() {
		return investTypeCode;
	}

	public void setInvestTypeCode(String investTypeCode) {
		this.investTypeCode = investTypeCode;
	}

	public Integer getInvestType() {
		if("direct".equals(investTypeCode)){
			investType = 2;
		}else if ("debt".equals(investTypeCode)){
			investType = 1;
		}else{
			investType = null;
		}
		return investType;
	}

	public void setInvestType(Integer investType) {
		this.investType = investType;
	}

	public String getQuerySource() {
		return querySource;
	}

	public void setQuerySource(String querySource) {
		this.querySource = querySource;
	}

	/**
	 * @return the projectCategory
	 */
	public Integer getProjectCategory() {
		return projectCategory;
	}

	/**
	 * @param projectCategory the projectCategory to set
	 */
	public void setProjectCategory(Integer projectCategory) {
		this.projectCategory = projectCategory;
	}

	/**
	 * @return the orderSource
	 */
	public String getOrderSource() {
		return orderSource;
	}

	/**
	 * @param orderSource the orderSource to set
	 */
	public void setOrderSource(String orderSource) {
		this.orderSource = orderSource;
	}
	
}
