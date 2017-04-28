package com.yourong.core.fin.manager.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yourong.common.enums.DebtEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;
import com.yourong.core.fin.dao.InterestSettlementMapper;
import com.yourong.core.fin.manager.InterestSettlementManager;
import com.yourong.core.fin.model.biz.InterestSettlementBiz;
import com.yourong.core.ic.manager.DebtInterestManager;
import com.yourong.core.ic.manager.DebtManager;
import com.yourong.core.ic.model.Debt;
import com.yourong.core.ic.model.DebtInterest;
import com.yourong.core.tc.dao.TransactionMapper;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.Member;

@Component
public class InterestSettlementManagerImpl implements InterestSettlementManager {

	@Autowired
	private InterestSettlementMapper interestSettlementMapper;

	@Autowired
	private TransactionMapper transactionMapper;

	@Autowired
	private DebtInterestManager debtInterestManager;

	@Autowired
	private DebtManager debtManager;

	@Autowired
	private MemberManager memberManager;

	@Override
	public Page<InterestSettlementBiz> findByPage(Page<InterestSettlementBiz> pageRequest, Map<String, Object> map)
			throws ManagerException {
		try {
			map.put("startRow", pageRequest.getiDisplayStart());
			map.put("pageSize", pageRequest.getiDisplayLength());
			int count = interestSettlementMapper.selectForPaginTotalCount(map);
			List<InterestSettlementBiz> interestBizs=Lists.newArrayList();
			if(count>0){
				interestBizs = interestSettlementMapper.selectForPagin(map);
				for (InterestSettlementBiz payBiz : interestBizs) {
					findInterestSettlement(payBiz);
				}
			}
			pageRequest.setiTotalDisplayRecords(count);
			pageRequest.setiTotalRecords(count);
			pageRequest.setData(interestBizs);
			return pageRequest;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	// 设置线下利息结算、项目周期、平台毛利
	public void findInterestSettlement(InterestSettlementBiz interestBiz) throws ManagerException {

		Long projectId = interestBiz.getProjectId();
		if (projectId == null) {
			return;
		}
		Debt debt = debtManager.selectByPrimaryKey(interestBiz.getDebtId());
		if (debt != null && debt.getLenderId() != null) {
			// 用户信息
			Member member = memberManager.selectByPrimaryKey(debt.getLenderId());
			interestBiz.setLenderMember(member);
		}

		// 项目周期
		interestBiz.setPeriod(DateUtils.daysOfTwo(interestBiz.getStartDate(), interestBiz.getEndDate()) + 1);

		// 项目期数
		Map<String, Object> map = Maps.newHashMap();
		map.put("projectId", projectId);
		int totalPeriods = debtInterestManager.findPeriodsByProjectId(map);
		interestBiz.setTotalPeriods(totalPeriods);

		// 债权本息
		List<DebtInterest> debtInterests = debtInterestManager.findDebtInterestByProjectId(projectId);
		interestBiz.setDebtInterests(debtInterests);

		List<Transaction> transactions = transactionMapper.findDayTransactionByProject(projectId);
		interestBiz.setTransactions(transactions);

		// 线下分期利息结算
		getInterestSettlementPeriods(interestBiz, transactions, debtInterests, interestBiz.getInterestFrom(),
				interestBiz.getOfflineAnnualizedRate());

		// 线下利息结算
		BigDecimal interestSettlement = getInterestSettlement(interestBiz, debtInterests, transactions,
				interestBiz.getEndDate(), interestBiz.getInterestFrom(), interestBiz.getOfflineAnnualizedRate());
		interestBiz.setInterestSettlement(interestSettlement);
		// 平台毛利率
		interestBiz.setGrossProfit(getGrossProfit(interestSettlement, interestBiz.getPayableInterest(),
				interestBiz.getUsedCouponAmount()));
	}

	// 线下利息结算
	public BigDecimal getInterestSettlement(InterestSettlementBiz interestBiz, List<DebtInterest> debtInterests,
			List<Transaction> transactions, Date endDate, Integer interestFrom, BigDecimal offlineRate) {
		BigDecimal interestSettlement = BigDecimal.ZERO;
		StringBuffer iBuffer = new StringBuffer();
		if (DebtEnum.RETURN_TYPE_DAY.getCode().equals(interestBiz.getProfitType())
				|| DebtEnum.RETURN_TYPE_ONCE.getCode().equals(interestBiz.getProfitType())) {
			int size = transactions.size();
			for (int i = 0; i < size; i++) {
				// 获取计息天数
				int days = DateUtils.daysOfTwo(
						DateUtils.formatDate(transactions.get(i).getTransactionTime(), DateUtils.DATE_FMT_3), endDate)
						+ 1 - interestFrom;
				// 某一天线下利息结算
				BigDecimal interest = FormulaUtil.getDayInterestSettlement(transactions.get(i).getInvestAmount(), days,
						offlineRate);
				interestSettlement = interestSettlement.add(interest);
				if (i != 0) {
					iBuffer.append("+");
				}
				iBuffer.append("(");
				iBuffer.append(transactions.get(i).getInvestAmount());
				iBuffer.append("*");
				iBuffer.append(offlineRate);
				iBuffer.append("%/360*");
				iBuffer.append(days);
				iBuffer.append(")");
			}
		} else if (DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getCode().equals(interestBiz.getProfitType())) {
			int size = transactions.size();
			Transaction transaction = new Transaction();
			for (int i = 0; i < size; i++) {
				Date startInterestDate = DateUtils.addDate(
						DateUtils.formatDate(transactions.get(i).getTransactionTime(), DateUtils.DATE_FMT_3), interestFrom);
				transaction = transactions.get(i);
				int period = 0;
				for (DebtInterest debtInterest : debtInterests) {
					if (!startInterestDate.after(debtInterest.getEndDate())) {
						BigDecimal interest = FormulaUtil.getTransactionInterest(interestBiz.getProfitType(),
								transaction.getInvestAmount(), interestBiz.getOfflineAnnualizedRate(), period,
								debtInterest.getStartDate(), startInterestDate, debtInterest.getEndDate());
						interestSettlement = interestSettlement.add(interest);
						if (i != 0 || period != 0) {
							iBuffer.append("+");
						}
						iBuffer.append("(");
						iBuffer.append(transactions.get(i).getInvestAmount());
						iBuffer.append("*");
						iBuffer.append(offlineRate.divide(new BigDecimal(12),2, BigDecimal.ROUND_HALF_UP));
						iBuffer.append("%");
						if(period == 0){
							int investDays = DateUtils.getIntervalDays(startInterestDate, debtInterest.getEndDate())+1;
							int naturalDays = DateUtils.getIntervalDays(debtInterest.getStartDate(), debtInterest.getEndDate())+1;
							iBuffer.append("*");
							iBuffer.append(investDays);
							iBuffer.append("/");
							iBuffer.append(naturalDays);
						}
						iBuffer.append(")");
						period = period + 1;
					}
				}
			}
		}

		interestBiz.setInterestSettlementFormula(iBuffer.toString());
		return interestSettlement.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	// 平台毛利率计算公式
	public BigDecimal getGrossProfit(BigDecimal interestSettlement, BigDecimal payableInterest,
			BigDecimal usedCouponAmount) {
		payableInterest = payableInterest == null ? BigDecimal.ZERO : payableInterest;
		interestSettlement = interestSettlement == null ? BigDecimal.ZERO : interestSettlement;
		usedCouponAmount = usedCouponAmount == null ? BigDecimal.ZERO : usedCouponAmount;
		return interestSettlement.subtract(payableInterest).subtract(usedCouponAmount);
	}

	// 线下分期利息结算
	public void getInterestSettlementPeriods(InterestSettlementBiz interestBiz, List<Transaction> transactions,
			List<DebtInterest> debtInterests, Integer interestFrom, BigDecimal offlineRate) {
		List<BigDecimal> interests = Lists.newArrayList();
		List<String> periodsFormulaStr = Lists.newArrayList();
		int size = transactions.size();
		if(DebtEnum.RETURN_TYPE_DAY.getCode().equals(interestBiz.getProfitType())){
			int[] periodTag = new int[size];
			for (DebtInterest debtInterest : debtInterests) {
				BigDecimal interestSettlement = BigDecimal.ZERO;
				StringBuffer pBuffer = new StringBuffer();
				// 投资的天数
				for (int i = 0; i < size; i++) {
					Date startInterestDate = DateUtils.addDate(DateUtils.formatDate(transactions.get(i).getTransactionTime(), DateUtils.DATE_FMT_3), interestFrom);
					if (!startInterestDate.after(debtInterest.getEndDate())) { 
						int days = 0;
						BigDecimal interest = BigDecimal.ZERO;
						if(periodTag[i] == 0){
							// 获取计息天数
							days = DateUtils.daysOfTwo(DateUtils.formatDate(transactions.get(i).getTransactionTime(),
									DateUtils.DATE_FMT_3), debtInterest.getEndDate())
									+ 1 - interestFrom;
							// 某一天线下利息结算
							interest= FormulaUtil.getDayInterestSettlement(transactions.get(i)
									.getInvestAmount(), days, offlineRate);
						}else{
							days = DateUtils.daysOfTwo(debtInterest.getStartDate(), debtInterest.getEndDate()) + 1;
							interest = FormulaUtil.getDayInterestSettlement(transactions.get(i).getInvestAmount(),
									days, offlineRate);
						}
						interestSettlement = interestSettlement.add(interest);
						if (i != 0) {
							pBuffer.append("+");
						}
						pBuffer.append("(");
						pBuffer.append(transactions.get(i).getInvestAmount());
						pBuffer.append("*");
						pBuffer.append(offlineRate);
						pBuffer.append("%/360*");
						pBuffer.append(days);
						pBuffer.append(")");
						periodTag[i] = periodTag[i] + 1; 
					}
				}
				interests.add(interestSettlement);
				periodsFormulaStr.add(pBuffer.toString());
			}
		}else if(DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getCode().equals(interestBiz.getProfitType())){
			int[] periodTag = new int[size];
			for (DebtInterest debtInterest : debtInterests) {
				BigDecimal interestSettlement = BigDecimal.ZERO;
				StringBuffer pBuffer = new StringBuffer();
				Transaction transaction = new Transaction();
				// 投资的天数
				for (int i = 0; i < size; i++) {
					transaction = transactions.get(i);
					Date startInterestDate = DateUtils.addDate(DateUtils.formatDate(transactions.get(i).getTransactionTime(), DateUtils.DATE_FMT_3), interestFrom);
					if (!startInterestDate.after(debtInterest.getEndDate())) {
						BigDecimal interest = FormulaUtil.getTransactionInterest(interestBiz.getProfitType(),
								transaction.getInvestAmount(), offlineRate, periodTag[i], debtInterest.getStartDate(),
								startInterestDate, debtInterest.getEndDate());
						interestSettlement = interestSettlement.add(interest);
						if (i != 0) {
							pBuffer.append("+");
						}
						pBuffer.append("(");
						pBuffer.append(transactions.get(i).getInvestAmount());
						pBuffer.append("*");
						pBuffer.append(offlineRate.divide(new BigDecimal(12),2, BigDecimal.ROUND_HALF_UP));
						pBuffer.append("%");
						if(periodTag[i] == 0){
							int investDays = DateUtils.getIntervalDays(startInterestDate, debtInterest.getEndDate())+1;
							int naturalDays = DateUtils.getIntervalDays(debtInterest.getStartDate(), debtInterest.getEndDate())+1;
							pBuffer.append("*");
							pBuffer.append(investDays);
							pBuffer.append("/");
							pBuffer.append(naturalDays);
						}
						pBuffer.append(")");
						periodTag[i] = periodTag[i] + 1;
					}
					
				}
				interests.add(interestSettlement);
				periodsFormulaStr.add(pBuffer.toString());
			}
		}
		interestBiz.setInterestPeriods(interests);
		interestBiz.setInterestPeriodsFormula(periodsFormulaStr);
	}

}
