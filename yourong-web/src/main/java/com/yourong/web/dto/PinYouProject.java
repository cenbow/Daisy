package com.yourong.web.dto;

import java.math.BigDecimal;

public class PinYouProject {
	
	/**项目ID**/
	private Long productNo;
	/**项目名称**/
	private String name;
	/**分类（车有融，房有融，新车融）**/
	private String kind;
	/**产品详情页URL**/
	private String url;
	/**图片地址**/
	private String picture;
	/**类别**/
	private String type;
	/**还款方式**/
	private String paymentMethod;
	/**项目总额**/
	private BigDecimal totalProject;
	/**年收益**/
	private BigDecimal annualRevenue;
	/**收益天数**/
	private int incomeDay;
	/**还款日期**/
	private String repaymentTime;
	/**当前进度**/
	private String progress;
	/**状态**/
	private String status;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public BigDecimal getTotalProject() {
		return totalProject;
	}
	public void setTotalProject(BigDecimal totalProject) {
		this.totalProject = totalProject;
	}
	public BigDecimal getAnnualRevenue() {
		return annualRevenue;
	}
	public void setAnnualRevenue(BigDecimal annualRevenue) {
		this.annualRevenue = annualRevenue;
	}
	public int getIncomeDay() {
		return incomeDay;
	}
	public void setIncomeDay(int incomeDay) {
		this.incomeDay = incomeDay;
	}
	public String getRepaymentTime() {
		return repaymentTime;
	}
	public void setRepaymentTime(String repaymentTime) {
		this.repaymentTime = repaymentTime;
	}
	public String getProgress() {
		return progress;
	}
	public void setProgress(String progress) {
		this.progress = progress;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getProductNo() {
		return productNo;
	}
	public void setProductNo(Long productNo) {
		this.productNo = productNo;
	}
	
	
	
	
}
