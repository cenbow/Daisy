package com.yourong.core.fin.model.query;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.yourong.common.domain.BaseQueryParam;
import com.yourong.common.enums.TypeEnum;

/**
 * 用于我的账户 资金流水
 * @author fuyili
 *
 * 创建时间:2015年6月4日下午5:06:31
 */
public class CapitalQuery extends BaseQueryParam{
	/**
	 * 日期查询方式
	 */
	private Integer happenTimeSelect;
	
	/**
	 * 查询起始时间
	 */
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date startTime;

	/**
	 * 查询结束时间
	 */
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date endTime;
	
	/**
	 * 查询时间范围
	 */
	private Integer happenTimeRange;
	
	/**
	 * 用户ID
	 */
	private Long memberId;
	
	/**
	 * 垫资还款
	 */
	private boolean isLoaningRepayment = false;
	/**
	 * 项目放款
	 */
	private boolean isProjectLoan = false;
	/**
	 * 项目还款
	 */
	private boolean isProjectRepayment = false;

	public Integer getHappenTimeSelect() {
		return happenTimeSelect;
	}

	public void setHappenTimeSelect(Integer happenTimeSelect) {
		this.happenTimeSelect = happenTimeSelect;
	}

	public Integer getHappenTimeRange() {
		return happenTimeRange;
	}

	public void setHappenTimeRange(Integer happenTimeRange) {
		this.happenTimeRange = happenTimeRange;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
	public String getTypeDesc(){
		if(getType()==null){
			return "";
		}
		int typeInt = getType().intValue();
		if(typeInt==TypeEnum.FINCAPITALINOUT_TYPE_RECHARGE.getType()){
			return "充值";
		}
		if(typeInt==TypeEnum.FINCAPITALINOUT_TYPE_WITHDRAW.getType()){
			return "提现";
		}
		if(typeInt==TypeEnum.FINCAPITALINOUT_TYPE_INVEST.getType()){
			return "投资";
		}
		if(typeInt==TypeEnum.FINCAPITALINOUT_TYPE_INTEREST.getType()){
			return "利息回款";
		}
		if(typeInt == TypeEnum.FINCAPITALINOUT_TYPE_PRINCIPAL.getType()){
			return "本金回款";
		}
		if(typeInt == TypeEnum.FINCAPITALINOUT_TYPE_THIRDPAY.getType()){
			return "存钱罐收益";
		}
		if(typeInt == TypeEnum.FINCAPITALINOUT_TYPE_LOAN.getType()){
			return "项目放款";
		}
		if(typeInt == TypeEnum.FINCAPITALINOUT_TYPE_RETURN.getType()){
			return "项目还款";
		}
		if(typeInt == TypeEnum.FINCAPITALINOUT_TYPE_CAPITAL_FALLBACK.getType()){
			return "资金回退";
		}
		if(typeInt == TypeEnum.FINCAPITALINOUT_TYPE_UNDERWRITE_TYPE.getType()){
			return "垫资还款";
		}
		if(typeInt== TypeEnum.FINCAPITALINOUT_TYPE_DIRECT_PAY.getType()||typeInt== TypeEnum.FINCAPITALINOUT_TYPE_DIRECT_COLLECT.getType()||typeInt == TypeEnum.FINCAPITALINOUT_TYPE_PAY_LEASE_BONUS.getType()){
			return "其他";
		}
		return "";
	}

	/**
	 * @return the memberId
	 */
	public Long getMemberId() {
		return memberId;
	}

	/**
	 * @param memberId the memberId to set
	 */
	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	/**
	 * @return the isLoaningRepayment
	 */
	public boolean isLoaningRepayment() {
		return isLoaningRepayment;
	}

	/**
	 * @param isLoaningRepayment the isLoaningRepayment to set
	 */
	public void setLoaningRepayment(boolean isLoaningRepayment) {
		this.isLoaningRepayment = isLoaningRepayment;
	}

	/**
	 * @return the isProjectLoan
	 */
	public boolean isProjectLoan() {
		return isProjectLoan;
	}

	/**
	 * @param isProjectLoan the isProjectLoan to set
	 */
	public void setProjectLoan(boolean isProjectLoan) {
		this.isProjectLoan = isProjectLoan;
	}

	/**
	 * @return the isProjectRepayment
	 */
	public boolean isProjectRepayment() {
		return isProjectRepayment;
	}

	/**
	 * @param isProjectRepayment the isProjectRepayment to set
	 */
	public void setProjectRepayment(boolean isProjectRepayment) {
		this.isProjectRepayment = isProjectRepayment;
	}
	
}
