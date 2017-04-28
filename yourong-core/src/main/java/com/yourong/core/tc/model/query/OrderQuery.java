package com.yourong.core.tc.model.query;

import java.util.Date;

import com.yourong.common.domain.BaseQueryParam;

public class OrderQuery extends BaseQueryParam {

    /**内部交易号，由前台生成**/
    private String orderNo;

    /**外部交易号，由第三方返回**/
    private String outOrderNo;

    /**用户id**/
    private Long memberId;

    /****/
    private Long projectId;

    /**银行编号**/
    private String bankCode;


    /**1:待支付 2：已支付，投资失败 3：已支付，投资成功 4：支付失败**/
    private Integer status;

    /**1-新浪支付，2-盛付通**/
    private Integer payMethod;
    
    /**使用的现金券编号**/
    private String cashCouponNo;
    /**
     * 使用的收益权编号
     */
    private String profitCouponNo;
    

    /****/
    private Date orderStartTime;

    /****/
    private Date orderEndTime;
    
    /**投资类型（1-债权；2-直投）**/
    private Integer investType;

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getOutOrderNo() {
		return outOrderNo;
	}

	public void setOutOrderNo(String outOrderNo) {
		this.outOrderNo = outOrderNo;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getPayMethod() {
		return payMethod;
	}

	public void setPayMethod(Integer payMethod) {
		this.payMethod = payMethod;
	}

	public String getCashCouponNo() {
		return cashCouponNo;
	}

	public void setCashCouponNo(String cashCouponNo) {
		this.cashCouponNo = cashCouponNo;
	}

	public String getProfitCouponNo() {
		return profitCouponNo;
	}

	public void setProfitCouponNo(String profitCouponNo) {
		this.profitCouponNo = profitCouponNo;
	}

	public Date getOrderStartTime() {
		return orderStartTime;
	}

	public void setOrderStartTime(Date orderStartTime) {
		this.orderStartTime = orderStartTime;
	}

	public Date getOrderEndTime() {
		return orderEndTime;
	}

	public void setOrderEndTime(Date orderEndTime) {
		this.orderEndTime = orderEndTime;
	}

	public Integer getInvestType() {
		return investType;
	}

	public void setInvestType(Integer investType) {
		this.investType = investType;
	}

}