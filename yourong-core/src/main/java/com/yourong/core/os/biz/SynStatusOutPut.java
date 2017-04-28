package com.yourong.core.os.biz;

import com.yourong.common.domain.AbstractBaseObject;

import java.util.Date;

public class SynStatusOutPut extends AbstractBaseObject {

	/**
	 * 外部业务号
	 */
	private String outBizNo;

	/**
	 * 状态
	 */
	private String status;
	/**
	 * 商品sku
	 */
	private String sku;
	/**
	 * 有融项目名称
	 */
	private String projectName;
	/**
	 * 放款时间
	 */
	private Date loanTime;
	/**
	 * 拒绝原因
	 */
	private String refuseInfo;

	public String getOutBizNo() {
		return outBizNo;
	}

	public void setOutBizNo(String outBizNo) {
		this.outBizNo = outBizNo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public Date getLoanTime() {
		return loanTime;
	}

	public void setLoanTime(Date loanTime) {
		this.loanTime = loanTime;
	}

	public String getRefuseInfo() {
		return refuseInfo;
	}

	public void setRefuseInfo(String refuseInfo) {
		this.refuseInfo = refuseInfo;
	}
}
