/**
 * 
 */
package com.yourong.api.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.constant.Config;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.StringUtil;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年7月13日上午10:31:33
 */
public class UnSignContractDto {
	
	private Long id;
	
	private String projectName;
	
	/** 项目ID **/
	private Long projectId;
	
	private BigDecimal investAmount;
	
	private Date transactionTime;
	
	private String thumbnail;

	

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}


	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return projectName;
	}


	/**
	 * @param projectName the projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}


	/**
	 * @return the investAmount
	 */
	public BigDecimal getInvestAmount() {
		return investAmount;
	}


	/**
	 * @param investAmount the investAmount to set
	 */
	public void setInvestAmount(BigDecimal investAmount) {
		this.investAmount = investAmount;
	}


	/**
	 * @return the transactionTime
	 */
	public Date getTransactionTime() {
		return transactionTime;
	}


	/**
	 * @param transactionTime the transactionTime to set
	 */
	public void setTransactionTime(Date transactionTime) {
		this.transactionTime = transactionTime;
	}


	/**
	 * @return the projectId
	 */
	public Long getProjectId() {
		return projectId;
	}


	/**
	 * @param projectId the projectId to set
	 */
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public String getPrefixProjectName() {
		if (projectName.contains("期")) {
			return projectName.substring(0, projectName.indexOf("期") + 1);
		} else {
			return "";
		}
	}
	
	/**
	 * 交易时间格式化处理
	 *
	 * @return
	 */
	public String getTransactionTimeStr() {
		if (transactionTime != null) {
			return DateUtils.formatDatetoString(transactionTime, DateUtils.TIME_PATTERN);
		}
		return null;
	}
	
	/**
	 * @param thumbnail the thumbnail to set
	 */
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getThumbnail() {
		if(thumbnail != null)
		{
			if(getProjectId() != null && getProjectId() >= 989800156L){
				return Config.ossPicUrl+StringUtil.getFilePath(thumbnail,"300");
			}else{
				return Config.ossPicUrl+thumbnail;
			}
		}
		return null;
	}
	
}
