/**
 * 
 */
package com.yourong.core.ic.model.biz;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.constant.Config;
import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.StringUtil;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年9月10日下午2:55:51
 */
public class TransferRecordBiz extends AbstractBaseObject{

	private static final long serialVersionUID = 1L;
	
	/**交易id**/
    private Long id;
	
	/**原项目id**/
    private Long projectId;
    
    /**原项目名称*/
    private String projectName;
    
    /**投资状态（0-募集中 1-回款中 2-已完结 3-流标 4-转让中，5-已转让）**/
    private Integer status;
    
    /**项目缩略图**/
    private String thumbnail;
    
    private BigDecimal 	investAmount;
    
    /**已转让本金**/
    private BigDecimal 	transferPrincipal;
    
    /**累计获取**/
    private BigDecimal 	transferIncome;

    private Integer transferStatus;
    
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
	 * @return the status
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * @return the thumbnail
	 */
	public String getThumbnail() {
		return thumbnail;
	}

	/**
	 * @param thumbnail the thumbnail to set
	 */
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
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
	 * @return the transferPrincipal
	 */
	public BigDecimal getTransferPrincipal() {
		return transferPrincipal;
	}

	/**
	 * @param transferPrincipal the transferPrincipal to set
	 */
	public void setTransferPrincipal(BigDecimal transferPrincipal) {
		this.transferPrincipal = transferPrincipal;
	}

	/**
	 * @return the transferIncome
	 */
	public BigDecimal getTransferIncome() {
		return transferIncome;
	}

	/**
	 * @param transferIncome the transferIncome to set
	 */
	public void setTransferIncome(BigDecimal transferIncome) {
		this.transferIncome = transferIncome;
	}
    
	public String getStatusDesc(){
		
		if(status==null||status==StatusEnum.TRANSACTION_COMPLETE.getStatus()
				||status==StatusEnum.TRANSACTION_LOSE.getStatus()
				){
			return "已结束"; 
		}
		if(transferStatus==StatusEnum.TRANSACTION_TRANSFER_STATUS_TRANSFERING.getStatus()){
			return "转让中";
		}
		if(transferStatus==StatusEnum.TRANSACTION_TRANSFER_STATUS_NO.getStatus()||
				transferStatus==StatusEnum.TRANSACTION_TRANSFER_STATUS_PART_TRANSFERED.getStatus()
				){
			return "持有中";
		}
		
		return "已结束";
	}

	/**
	 * @return the transferStatus
	 */
	public Integer getTransferStatus() {
		return transferStatus;
	}

	/**
	 * @param transferStatus the transferStatus to set
	 */
	public void setTransferStatus(Integer transferStatus) {
		this.transferStatus = transferStatus;
	}
	
}

