/**
 * 
 */
package com.yourong.core.ic.model.query;

import com.yourong.common.domain.BaseQueryParam;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年9月8日下午5:15:44
 */
public class TransferProjectQuery extends BaseQueryParam {

	private String status;
	
	private String projectType;
	
	private String statusCode;
	
	private boolean isAppQuery = false;

	private String orderSource;
	
	
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the projectType
	 */
	public String getProjectType() {
		return projectType;
	}

	/**
	 * @param projectType the projectType to set
	 */
	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}

	/**
	 * @return the statusCode
	 */
	public String getStatusCode() {
		return statusCode;
	}

	/**
	 * @param statusCode the statusCode to set
	 */
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * @return the isAppQuery
	 */
	public boolean isAppQuery() {
		return isAppQuery;
	}

	/**
	 * @param isAppQuery the isAppQuery to set
	 */
	public void setAppQuery(boolean isAppQuery) {
		this.isAppQuery = isAppQuery;
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
