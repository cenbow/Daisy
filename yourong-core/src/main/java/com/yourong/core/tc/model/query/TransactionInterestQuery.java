package com.yourong.core.tc.model.query;

import java.util.Date;

import com.yourong.common.domain.BaseQueryParam;

public class TransactionInterestQuery extends BaseQueryParam {

    /** 交易id**/
    private Long transactionId;

    /**计息开始时间**/
    private Date startDate;

    /**计息结束时间**/
    private Date endDate;

    /**用户ID**/
    private Long memberId;

    /**状态：0：待支付 1：已全部支付 2:部分支付 3:未支付**/
    private Integer status;
    
    /**不是某种状态**/
    private Integer noStatus;

    /**支付日期**/
    private Date payTime;
    
    private Long projectId;
    
    /**是否本金查询**/
    private boolean isPrincipal = false;
    
    /**
     * 是否升序
     */
    private boolean isAsc = false;

	/** 计息开始年月 **/
	private String startYM;

	/** 计息结束年月 **/
	private String endYM;
	
	/**投资类型（1-债权；2-直投）**/
    private Integer investType;

    /**还款类型（0-正常 1-提前还款 2-逾期还款 3-垫资还款 ）**/
    private Integer payType;
    
    /**还款日期**/
    private Date topayDate;
    
    private Date transactionTime;
    
	private boolean curdate = false;
    
    
	public Date getTransactionTime() {
		return transactionTime;
	}

	public void setTransactionTime(Date transactionTime) {
		this.transactionTime = transactionTime;
	}

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public boolean isPrincipal() {
		return isPrincipal;
	}

	public void setPrincipal(boolean isPrincipal) {
		this.isPrincipal = isPrincipal;
	}

	public boolean isAsc() {
		return isAsc;
	}

	public void setAsc(boolean isAsc) {
		this.isAsc = isAsc;
	}

	public String getStartYM() {
		return startYM;
	}

	public void setStartYM(String startYM) {
		this.startYM = startYM;
	}

	public String getEndYM() {
		return endYM;
	}

	public void setEndYM(String endYM) {
		this.endYM = endYM;
	}

	public Integer getInvestType() {
		return investType;
	}

	public void setInvestType(Integer investType) {
		this.investType = investType;
	}

	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public Date getTopayDate() {
		return topayDate;
	}

	public void setTopayDate(Date topayDate) {
		this.topayDate = topayDate;
	}

	public Integer getNoStatus() {
		return noStatus;
	}

	public void setNoStatus(Integer noStatus) {
		this.noStatus = noStatus;
	}

	public boolean isCurdate() {
		return curdate;
	}

	public void setCurdate(boolean curdate) {
		this.curdate = curdate;
	}
	
}