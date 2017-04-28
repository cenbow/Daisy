package com.yourong.core.tc.model.query;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.yourong.common.domain.BaseQueryParam;

public class TransactionQuery extends BaseQueryParam {

    /**订单id**/
    private Long orderId;


    /**项目ID**/
    private Long projectId;


    /**投资状态（1-回款中 2-已完结）**/
    private Integer status;

    /**交易时间**/
    private Date transactionTime;
    
    /**创建时间**/
    private Date createdTime;

    /****/
    private Date updateTime;
    
    /**
     * 已收利息
     */
    private BigDecimal receivedInterest;
    
    /**投资类型（1-债权；2-直投）**/
    private Integer investType;
    
    /**
     * 已收本金
     */
    private BigDecimal receivedPrincipal;
    
    /**放款状态（0-未放款 1-放款中 2-已放款）**/
	private Integer loanStatus;
	
	/**代付交易号**/
    private String payTradeNo; 
    /**代收交易号**/
    private String collectTradeNo; 
    
    /**交易类型：1-普通项目交易 2-转让项目交易**/
    private Integer projectCategory;
    
    /**转让id**/
    private Long transferId;
    
    /**
	 * 查询起始时间
	 */
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date startDate;

	/**
	 * 查询结束时间
	 */
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date endDate;

	/**
	 * 查询交易起始时间
	 */
	private Date transactionStartTime;

	/**
	 * 查询交易结束时间
	 */
	private Date transactionEndTime;

	/**
	 * 0 我的交易列表, 1 我的募集中列表
	 */
	private Integer queryStatus;
	/**
	 * 会员ID
	 */
	private Long memberId;
	
	/**签署状态（0-初始化，1-未签署，2-已签署，3-已过期）**/
	private Integer signStatus;
	
	/**
	 * 分组
	 */
	private Integer groupType;
	
	private Long activityId;

	
	
	public Integer getGroupType() {
		return groupType;
	}

	public void setGroupType(Integer groupType) {
		this.groupType = groupType;
	}

	public Long getActivityId() {
		return activityId;
	}

	public void setActivityId(Long activityId) {
		this.activityId = activityId;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getTransactionTime() {
		return transactionTime;
	}

	public void setTransactionTime(Date transactionTime) {
		this.transactionTime = transactionTime;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	

	public BigDecimal getReceivedInterest() {
		return receivedInterest;
	}

	public void setReceivedInterest(BigDecimal receivedInterest) {
		this.receivedInterest = receivedInterest;
	}

	/**
	 * @return the investType
	 */
	public Integer getInvestType() {
		return investType;
	}

	/**
	 * @param investType the investType to set
	 */
	public void setInvestType(Integer investType) {
		this.investType = investType;
	}
	
	public BigDecimal getReceivedPrincipal() {
		return receivedPrincipal;
	}

	public void setReceivedPrincipal(BigDecimal receivedPrincipal) {
		this.receivedPrincipal = receivedPrincipal;
	}
	
	

	public Integer getLoanStatus() {
		return loanStatus;
	}

	public void setLoanStatus(Integer loanStatus) {
		this.loanStatus = loanStatus;
	}


	public String getPayTradeNo() {
		return payTradeNo;
	}

	public void setPayTradeNo(String payTradeNo) {
		this.payTradeNo = payTradeNo;
	}

	public String getCollectTradeNo() {
		return collectTradeNo;
	}

	public void setCollectTradeNo(String collectTradeNo) {
		this.collectTradeNo = collectTradeNo;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TransactionQuery [orderId=");
		builder.append(orderId);
		builder.append(", projectId=");
		builder.append(projectId);
		builder.append(", status=");
		builder.append(status);
		builder.append(", transactionTime=");
		builder.append(transactionTime);
		builder.append(", createdTime=");
		builder.append(createdTime);
		builder.append(", updateTime=");
		builder.append(updateTime);
		builder.append("]");
		return builder.toString();
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

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public Date getTransactionStartTime() {
		return transactionStartTime;
	}

	public void setTransactionStartTime(Date transactionStartTime) {
		this.transactionStartTime = transactionStartTime;
	}

	public Date getTransactionEndTime() {
		return transactionEndTime;
	}

	public void setTransactionEndTime(Date transactionEndTime) {
		this.transactionEndTime = transactionEndTime;
	}

	public Integer getQueryStatus() {
		return queryStatus;
	}

	public void setQueryStatus(Integer queryStatus) {
		this.queryStatus = queryStatus;
	}

	/**
	 * @return the signStatus
	 */
	public Integer getSignStatus() {
		return signStatus;
	}

	/**
	 * @param signStatus the signStatus to set
	 */
	public void setSignStatus(Integer signStatus) {
		this.signStatus = signStatus;
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
	 * @return the transferId
	 */
	public Long getTransferId() {
		return transferId;
	}

	/**
	 * @param transferId the transferId to set
	 */
	public void setTransferId(Long transferId) {
		this.transferId = transferId;
	}
	
	
}