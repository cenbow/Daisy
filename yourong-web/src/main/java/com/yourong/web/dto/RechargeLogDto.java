package com.yourong.web.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;
import com.yourong.common.util.StringUtil;

public class RechargeLogDto {
	/**
		 * 
		 */

	/****/
	private Long id;

	/** 充值金额 **/
	private BigDecimal amount;

	/** 充值时间 **/
	private Date rechargeTime;

	/** 会员id **/
	private Long memberId;

	/** 支付方式 1-新浪支付，1-盛付通 **/
	private Integer payMethod;

	/** 备注 **/
	private String remarks;

	/** 银行编码 **/
	private String bankCode;

	/** 交易号 **/
	private String rechargeNo;

	/** 外部交易号 **/
	private String outerRechargeNo;

	/** -2-失败 -1-拒绝 0-冻结 1-处理中 5-操作成功 **/
	private Integer status;

	/****/
	private Date updateTime;

	/** 充值类型：1-直接充值 2-交易充值 **/
	
	private Integer type;

	/** 订单号 **/
	private String orderNo;

	/***ticket**/
	private String ticket;

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public void setRechargeTime(Date rechargeTime) {
		this.rechargeTime = rechargeTime;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public Integer getPayMethod() {
		return payMethod;
	}

	public void setPayMethod(Integer payMethod) {
		this.payMethod = payMethod;
	}

	public String getRemarks() {
		if (StringUtil.isBlank(remarks)) {
			return "";
		}
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getRechargeNo() {
		return rechargeNo;
	}

	public void setRechargeNo(String rechargeNo) {
		this.rechargeNo = rechargeNo;
	}

	public String getOuterRechargeNo() {
		return outerRechargeNo;
	}

	public void setOuterRechargeNo(String outerRechargeNo) {
		this.outerRechargeNo = outerRechargeNo;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getAmountToS() {
		return FormulaUtil.formatCurrency(amount);
	}

	public String getRechargeTimeToS() {
		if (rechargeTime == null) {
			return "";
		}
		String data = DateUtils.formatDatetoString(rechargeTime,
				DateUtils.DATE_FMT_3);
		String time = DateUtils.formatDatetoString(rechargeTime,
				DateUtils.DATE_FMT_8);
		return "<span>" + data + "</span><span>" + time + "</span>";
	}

	/** -2-失败 -1-拒绝 0-冻结 1-处理中 5-操作成功 **/
	public String getStatusToSt() {
		String value = "";
		if (status == null) {
			return value;
		}
		if (status == -2) {
			value = "失败";
		}
		if (status == -1) {
			value = "拒绝";
		}
		if (status == 0) {
			value = "冻结";
		}
		if (status == 1) {
			value = "处理中";
		}
		if (status == 5) {
			value = "操作成功";
		}
		return value;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getType() {
		return type;
	}

	public String getTypeToS() {
		/** 充值类型：1-直接充值 2-交易充值 **/
		String message = "";
		if (type == null) {
			return message;
		}
		if (type == 1) {
			message = "直接充值";
		} else {
			message = "交易充值";
		}
		return message;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public Date getRechargeTime() {
		return rechargeTime;
	}

	public Integer getStatus() {
		return status;
	}

}
