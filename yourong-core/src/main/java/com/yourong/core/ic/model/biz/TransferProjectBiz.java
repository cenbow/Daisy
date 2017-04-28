/**
 * 
 */
package com.yourong.core.ic.model.biz;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * @desc 转让项目 项目详情对象
 * @author zhanghao
 * 2016年9月9日下午3:46:06
 */
public class TransferProjectBiz extends AbstractBaseObject{

	private static final long serialVersionUID = 1L;
	
	/**项目id**/
    private Long projectId;
	
	 /**转让开始时间**/
    private Date transferStartDate;
    
    /**还款时间(年月日)**/
    private Date endDate;
    
    /**项目名称**/
    private String name;
    
    /**转让价格**/
    private BigDecimal transferAmount;
    
    /**折价**/
    private BigDecimal discount;
    
	/** 原项目年化率 */
	private BigDecimal originalAnnualizedRate;

	/**
	 * @return the transferStartDate
	 */
	public Date getTransferStartDate() {
		return transferStartDate;
	}

	/**
	 * @param transferStartDate the transferStartDate to set
	 */
	public void setTransferStartDate(Date transferStartDate) {
		this.transferStartDate = transferStartDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * @return the transferAmount
	 */
	public BigDecimal getTransferAmount() {
		return transferAmount;
	}

	/**
	 * @param transferAmount the transferAmount to set
	 */
	public void setTransferAmount(BigDecimal transferAmount) {
		this.transferAmount = transferAmount;
	}

	/**
	 * @return the discount
	 */
	public BigDecimal getDiscount() {
		return discount;
	}

	/**
	 * @param discount the discount to set
	 */
	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	/**
	 * @return the originalAnnualizedRate
	 */
	public BigDecimal getOriginalAnnualizedRate() {
		return originalAnnualizedRate;
	}

	/**
	 * @param originalAnnualizedRate the originalAnnualizedRate to set
	 */
	public void setOriginalAnnualizedRate(BigDecimal originalAnnualizedRate) {
		this.originalAnnualizedRate = originalAnnualizedRate;
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
	
}
