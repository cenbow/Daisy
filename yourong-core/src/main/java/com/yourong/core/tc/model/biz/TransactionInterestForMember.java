package com.yourong.core.tc.model.biz;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.ibatis.type.Alias;

import com.yourong.common.util.DateUtils;


@Alias(value="TransactionInterestForMember")
public class TransactionInterestForMember {
	
	private Long transactionId;

	private Long projectId;
	
	private String projectName;
	 
	/**应付利息**/
    private BigDecimal payableInterest;

    /**应付本金**/
    private BigDecimal payablePrincipal;
    
    /**计息开始时间**/
    private Date startDate;

    /**计息结束时间**/
    private Date endDate;
    
    /**状态**/
    private int status;
    
    /**还款类型**/
    private int payType;

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public BigDecimal getPayableInterest() {
		return payableInterest;
	}

	public void setPayableInterest(BigDecimal payableInterest) {
		this.payableInterest = payableInterest;
	}

	public BigDecimal getPayablePrincipal() {
		return payablePrincipal;
	}

	public void setPayablePrincipal(BigDecimal payablePrincipal) {
		this.payablePrincipal = payablePrincipal;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	/**
	 * 剩余多少天
	 * @return
	 */
	public String getInterestRemarks(){
		if(getEndDate() != null){
			int days = DateUtils.getIntervalDays(DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3), getEndDate());
			if(days < 0){
				return "逾期"+Math.abs(days)+"天";
			}
			if(days == 0){
				return "今天还款";
			}
			return "还有"+days+"天";
		}
		return "";
	}

	public int getPayType() {
		return payType;
	}

	public void setPayType(int payType) {
		this.payType = payType;
	}
    
}
