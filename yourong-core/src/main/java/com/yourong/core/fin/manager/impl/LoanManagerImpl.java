package com.yourong.core.fin.manager.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.Collections3;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.LoanManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.fin.model.biz.LoanBiz;
import com.yourong.core.ic.dao.DebtMapper;
import com.yourong.core.ic.dao.ProjectMapper;
import com.yourong.core.ic.manager.DebtManager;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.model.Debt;
import com.yourong.core.ic.model.Project;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.Member;

@Component
public class LoanManagerImpl implements LoanManager {
	@Autowired
	private ProjectManager projectManager;
	
	@Autowired
	private DebtManager debtManager;
	
	@Autowired
	private MemberManager memberManager;
	
	@Autowired
	private BalanceManager balanceManager;	
	
	@Autowired
	private TransactionManager transactionManager;
	
	@Autowired
	private ProjectMapper projectMapper;
	

	@Override
	public Page<LoanBiz> findByPage(Page<LoanBiz> pageRequest,
			Map<String, Object> map) throws ManagerException {
		try {
			map.put("startRow", pageRequest.getiDisplayStart());
			map.put("pageSize", pageRequest.getiDisplayLength());
			//根据用户名和用户手机模糊查询debt_id,拼接debt_id查询条件
			if(map.get("trueName")!=null || map.get("mobile")!=null){
				List<Long> debtIds = debtManager.findDebtIdByMemberInfo(map); 
				if(debtIds!=null){
					if(debtIds.size()==0){
						debtIds.add(-1L);
					}
					map.put("debtId", debtIds);
				}
			}
			//查询满足状态的项目
			if(map.get("loanStatus")!=null){
				List<Long> projectIds = projectManager.findProjectByLoanStatus(map);
				if(projectIds!=null){
					if(projectIds.size()==0){
						projectIds.add(-1L);
					}
					map.put("projectId", projectIds);
				}
			}
			List<Project> projects = projectMapper.selectForPagin(map);
			int totalCount = projectMapper.selectForPaginTotalCount(map);
			pageRequest.setiTotalDisplayRecords(totalCount);
			pageRequest.setiTotalRecords(totalCount);
			List<LoanBiz> loanBizs = Lists.newArrayList();
			//TODO 秒表计算时间
			for (Project project : projects) {
				loanBizs.add(findLoanByProject(project));
			}
			pageRequest.setData(loanBizs);
			return pageRequest;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public LoanBiz findLoanByProject(Project project) throws ManagerException {
		try {
			LoanBiz loanBiz = new LoanBiz();
			if(project!=null){
				//项目信息
				loanBiz.setProject(project);
				Debt debt = debtManager.selectByPrimaryKey(project.getDebtId());
				if(debt!=null && debt.getLenderId()!=null){
					//用户信息
					Member member = memberManager.selectByPrimaryKey(debt.getLenderId());
					loanBiz.setLenderMember(member);
				}
				//投资进度
				Balance balance = balanceManager.queryBalance(project.getId(),TypeEnum.BALANCE_TYPE_PROJECT);
				if(balance!=null && project.getTotalAmount()!=null){
					BigDecimal balanceAmount = project.getTotalAmount().subtract(balance.getAvailableBalance());
					loanBiz.setProgress(balanceAmount.divide(project.getTotalAmount(),10,BigDecimal.ROUND_HALF_UP).setScale(4, BigDecimal.ROUND_HALF_UP));
				}
				//最近一笔投资时间
				Transaction trans = transactionManager.selectLastTransactionByProject(project.getId());
				if(trans!=null){
					loanBiz.setLastTransDate(trans.getTransactionTime());
				}
				//获取可放款额度
				Transaction avdLoanTrans = transactionManager.selectAmountByProject(project.getId(), StatusEnum.TRANSACTION_LOAN_STATUS_WAIT_LOAN.getStatus());
				if(avdLoanTrans!=null){
					loanBiz.setLoanableUserCapital(avdLoanTrans.getUsedCapital()==null?BigDecimal.ZERO:avdLoanTrans.getUsedCapital());
					loanBiz.setLoanablePlatCapital(avdLoanTrans.getUsedCouponAmount()==null?BigDecimal.ZERO:avdLoanTrans.getUsedCouponAmount());
				}else{
					loanBiz.setLoanableUserCapital(BigDecimal.ZERO);
					loanBiz.setLoanablePlatCapital(BigDecimal.ZERO);
				}
				//获取已放款额度
				Transaction loanedTrans = transactionManager.selectAmountByProject(project.getId(), StatusEnum.TRANSACTION_LOAN_STATUS_LOANED.getStatus());
				if(loanedTrans!=null){
					loanBiz.setLoanedUserCapital(loanedTrans.getUsedCapital()==null?BigDecimal.ZERO:loanedTrans.getUsedCapital());
					loanBiz.setLoanedPlatCapital(loanedTrans.getUsedCouponAmount()==null?BigDecimal.ZERO:loanedTrans.getUsedCouponAmount());
				}else{
					loanBiz.setLoanedUserCapital(BigDecimal.ZERO);
					loanBiz.setLoanedPlatCapital(BigDecimal.ZERO);
				}
				BigDecimal loaned = loanBiz.getLoanedUserCapital().add(loanBiz.getLoanedPlatCapital());
				//获取状态
				if (loaned.equals(BigDecimal.ZERO)) {
					loanBiz.setStatus(StatusEnum.FIN_LOAN_WAIT_LOAN.getStatus());
				}else if(loaned.compareTo(loanBiz.getProject().getTotalAmount()) < 0){
					loanBiz.setStatus(StatusEnum.FIN_LOAN_PART_LOAN.getStatus());
				}else{
					loanBiz.setStatus(StatusEnum.FIN_LOAN_ALL_LOAN.getStatus());
				}
			}
			return loanBiz;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public LoanBiz findLoanBiz(Long projectId) throws ManagerException {
		try {
			Project project = projectManager.selectByPrimaryKey(projectId);
			LoanBiz loanBiz = findLoanByProject(project);
			return loanBiz;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	

}
