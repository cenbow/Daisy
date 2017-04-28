package com.yourong.core.fin.model.biz;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import com.yourong.common.util.FormulaUtil;
import com.yourong.core.ic.model.DebtInterest;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.uc.model.Member;

/**
 * 线上线下利息营收结算管理
 * 
 * @author fuyili 2014年12月29日下午3:15:20
 */
public class InterestSettlementBiz {

	/** 项目编号 */
	private Long projectId;

	/** 项目名称 */
	private String projectName;

	/** 项目总额 */
	private BigDecimal totalAmount;

	/** 线下付息率 */
	private BigDecimal offlineAnnualizedRate;

	/** 债权id */
	private Long debtId;

	/** 出借人信息 */
	private Member lenderMember;

	/** 项目开始时间 */
	private Date startDate;

	/** 项目结束时间 */
	private Date endDate;

	/** 项目周期 */
	private Integer period;

	/** 起息日 */
	private Integer interestFrom;

	/** 线下利息结算 */
	private BigDecimal interestSettlement;

	/** 线下利息结算公式字符串 */
	private String interestSettlementFormula;

	/** 现金券支出 */
	private BigDecimal usedCouponAmount;

	/** 总支出投资人利息 **/
	private BigDecimal payableInterest;

	/** 平台支付投资人利息 **/
	private BigDecimal extraInterest;

	/** 平台毛利润 */
	private BigDecimal grossProfit;

	/** 总期数 */
	private Integer totalPeriods;

	/** 项目的债权本息 */
	private List<DebtInterest> debtInterests;

	/** 线下利息分期结算值 */
	private List<BigDecimal> interestPeriods;

	/** 交易记录按照日期统计总额 */
	private List<Transaction> transactions;

	/** 线下利息分期结算公式字符串 */
	private List<String> interestPeriodsFormula;

	/**状态**/
	private String status;
	
	/**交易时间**/
	private Date transactionTime;
	
	/**还款方式**/
	private String profitType;
	
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

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getOfflineAnnualizedRate() {
		return offlineAnnualizedRate;
	}

	public void setOfflineAnnualizedRate(BigDecimal offlineAnnualizedRate) {
		this.offlineAnnualizedRate = offlineAnnualizedRate;
	}

	public Long getDebtId() {
		return debtId;
	}

	public void setDebtId(Long debtId) {
		this.debtId = debtId;
	}

	public Member getLenderMember() {
		return lenderMember;
	}

	public void setLenderMember(Member lenderMember) {
		this.lenderMember = lenderMember;
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

	public BigDecimal getInterestSettlement() {
		return interestSettlement;
	}

	public void setInterestSettlement(BigDecimal interestSettlement) {
		this.interestSettlement = interestSettlement;
	}

	public String getInterestSettlementFormula() {
		return interestSettlementFormula;
	}

	public void setInterestSettlementFormula(String interestSettlementFormula) {
		this.interestSettlementFormula = interestSettlementFormula;
	}

	public Integer getPeriod() {
		return period;
	}

	public void setPeriod(Integer period) {
		this.period = period;
	}

	public Integer getInterestFrom() {
		return interestFrom;
	}

	public void setInterestFrom(Integer interestFrom) {
		this.interestFrom = interestFrom;
	}

	public BigDecimal getUsedCouponAmount() {
		return usedCouponAmount;
	}

	public void setUsedCouponAmount(BigDecimal usedCouponAmount) {
		this.usedCouponAmount = usedCouponAmount;
	}

	public BigDecimal getPayableInterest() {
		return payableInterest;
	}

	public void setPayableInterest(BigDecimal payableInterest) {
		this.payableInterest = payableInterest;
	}

	public BigDecimal getExtraInterest() {
		return extraInterest;
	}

	public void setExtraInterest(BigDecimal extraInterest) {
		this.extraInterest = extraInterest;
	}

	public BigDecimal getGrossProfit() {
		return grossProfit;
	}

	public void setGrossProfit(BigDecimal grossProfit) {
		this.grossProfit = grossProfit;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}

	public Integer getTotalPeriods() {
		return totalPeriods;
	}

	public void setTotalPeriods(Integer totalPeriods) {
		this.totalPeriods = totalPeriods;
	}

	public List<DebtInterest> getDebtInterests() {
		return debtInterests;
	}

	public void setDebtInterests(List<DebtInterest> debtInterests) {
		this.debtInterests = debtInterests;
	}

	public List<BigDecimal> getInterestPeriods() {
		return interestPeriods;
	}

	public void setInterestPeriods(List<BigDecimal> interestPeriods) {
		this.interestPeriods = interestPeriods;
	}

	public List<String> getInterestPeriodsFormula() {
		return interestPeriodsFormula;
	}

	public void setInterestPeriodsFormula(List<String> interestPeriodsFormula) {
		this.interestPeriodsFormula = interestPeriodsFormula;
	}

	/** 获取支付投资人利息 */
	public String getUserInterestStr() {
		if (extraInterest == null) {
			extraInterest = BigDecimal.ZERO;
		}
		if (payableInterest == null) {
			payableInterest = BigDecimal.ZERO;
		}
		return FormulaUtil.formatCurrencyNoUnit(payableInterest.subtract(extraInterest));
	}
	/**
	 * 
	 * @Description:格式化平台毛利润
	 * @return
	 * @author: chaisen
	 * @time:2016年5月6日 下午2:49:33
	 */
	public String getPlaInterestStr() {
		if (interestSettlement == null) {
			interestSettlement = BigDecimal.ZERO;
		}
		if (payableInterest == null) {
			payableInterest = BigDecimal.ZERO;
		}
		if (extraInterest == null) {
			extraInterest = BigDecimal.ZERO;
		}
		return FormulaUtil.formatCurrencyNoUnit(interestSettlement.subtract(payableInterest).add(extraInterest));
	}
	/** 格式化毛利率 */
	public String getGrossProfitStr() {
		return FormulaUtil.formatCurrencyNoUnit(grossProfit);
	}

	/** 格式化现金券支出 */
	public String getUsedCouponAmountStr() {
		if (usedCouponAmount == null) {
			usedCouponAmount = BigDecimal.ZERO;
		}
		return FormulaUtil.formatCurrencyNoUnit(usedCouponAmount);
	}

	/** 格式化收益券支出 */
	public String getExtraInterestStr() {
		if (extraInterest == null) {
			extraInterest = BigDecimal.ZERO;
		}
		return FormulaUtil.formatCurrencyNoUnit(extraInterest);
	}

	/** 格式化线下利息结算 */
	public String getInterestSettlementStr() {
		return FormulaUtil.formatCurrencyNoUnit(interestSettlement);
	}

	/** 格式化每一期线下利息结算 */
	public List<String> getInterestPeriodsStr() {
		List<String> interestPeriodsStr = Lists.newArrayList();
		for (BigDecimal interest : interestPeriods) {
			interestPeriodsStr.add(FormulaUtil.formatCurrencyNoUnit(interest));
		}
		return interestPeriodsStr;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getTransactionTime() {
		return transactionTime;
	}

	public void setTransactionTime(Date transactionTime) {
		this.transactionTime = transactionTime;
	}

	public String getProfitType() {
		return profitType;
	}

	public void setProfitType(String profitType) {
		this.profitType = profitType;
	}

}
