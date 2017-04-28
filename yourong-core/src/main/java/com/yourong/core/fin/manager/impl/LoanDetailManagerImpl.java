package com.yourong.core.fin.manager.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.common.enums.StatusEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.fin.dao.LoanDetailMapper;
import com.yourong.core.fin.manager.LoanDetailManager;
import com.yourong.core.fin.model.LoanDetail;
import com.yourong.core.fin.model.biz.LoanBiz;
import com.yourong.core.ic.dao.DebtMapper;
import com.yourong.core.ic.dao.ProjectMapper;
import com.yourong.core.ic.model.Debt;
import com.yourong.core.tc.dao.TransactionMapper;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.uc.dao.EnterpriseMapper;
import com.yourong.core.uc.dao.MemberMapper;
import com.yourong.core.uc.model.Enterprise;
import com.yourong.core.uc.model.Member;

@Component
public class LoanDetailManagerImpl implements LoanDetailManager {
	@Autowired
	private LoanDetailMapper loanDetailMapper; 
	
	@Autowired
	private ProjectMapper projectMapper; 
	

	@Autowired
	private DebtMapper debtMapper; 
	
	@Autowired
	private MemberMapper memberMapper; 
	
	@Autowired
	private EnterpriseMapper enterpriseMapper;
	
	@Autowired
	private TransactionMapper transactionMapper;
	

	

	@Override
	public int deleteByPrimaryKey(Long id) throws ManagerException {
		try {
			return loanDetailMapper.deleteByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int insert(LoanDetail record) throws ManagerException {
		try {
			return loanDetailMapper.insert(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int insertSelective(LoanDetail record) throws ManagerException {
		try {
			return loanDetailMapper.insertSelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public LoanDetail selectByPrimaryKey(Long id) throws ManagerException {
		try {
			return loanDetailMapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateByPrimaryKeySelective(LoanDetail record)
			throws ManagerException {
		try {
			return loanDetailMapper.updateByPrimaryKeySelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateByPrimaryKey(LoanDetail record) throws ManagerException {
		try {
			return loanDetailMapper.updateByPrimaryKey(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Page<LoanDetail> findByPage(Page<LoanDetail> pageRequest,
			Map<String, Object> map) throws ManagerException {
		try {
			map.put("startRow", pageRequest.getiDisplayStart());
			map.put("pageSize", pageRequest.getiDisplayLength());
			
			//针对前台传入时间，添加时分秒字段
			if(map.containsKey("onlineTimeStart")){
				map.put("onlineTimeStart", (map.get("onlineTimeStart")+" 00:00:00"));
			}
			if(map.containsKey("onlineTimeEnd")){
				map.put("onlineTimeEnd", (map.get("onlineTimeEnd")+" 23:59:59"));
			}
			if(map.containsKey("loanTimeStart")){
				map.put("loanTimeStart", (map.get("loanTimeStart")+" 00:00:00"));
			}
			if(map.containsKey("loanTimeEnd")){
				map.put("loanTimeEnd", (map.get("loanTimeEnd")+" 23:59:59"));
			}
			
			//查询满足状态的项目
			if(map.containsKey("loanStatus")){
				List<Long> projectIds = projectMapper.findProjectByLoanStatus(map);
				if(projectIds!=null){
					if(projectIds.size()==0){
						projectIds.add(-1L);
					}
					map.put("projectIds", projectIds);
				}
			}
			int totalCount = loanDetailMapper.selectForPaginTotalCount(map);
			pageRequest.setiTotalDisplayRecords(totalCount);
			pageRequest.setiTotalRecords(totalCount);
			List<LoanDetail> loanDetails = loanDetailMapper.selectForPagin(map);
			for(LoanDetail loanDetail:loanDetails){
				/*Debt debt = debtMapper.selectByPrimaryKey(loanDetail.getDebtId());
				//1-个人用户 2-企业用户
				if(debt.getLenderType()==1){
					Member member = memberMapper.selectByPrimaryKey(debt.getLenderId());
					loanDetail.setLenderName(member.getTrueName());
				}else if(debt.getLenderType()==2){
					Enterprise enterprise = enterpriseMapper.selectByMemberID(debt.getLenderId());
					loanDetail.setLenderName(enterprise.getFullName());
				}else{
					loanDetail.setLenderName("未知的出借人类型");
				}*/
				LoanBiz loanBiz = new LoanBiz();
				//获取已放款额度
				Transaction loanedTrans = transactionMapper.selectAmountByProject(loanDetail.getProjectId(), StatusEnum.TRANSACTION_LOAN_STATUS_LOANED.getStatus());
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
					loanDetail.setLoanStatus(StatusEnum.FIN_LOAN_WAIT_LOAN.getStatus());
				}else if(loaned.compareTo(loanDetail.getTotalAmount()) < 0){
					loanDetail.setLoanStatus(StatusEnum.FIN_LOAN_PART_LOAN.getStatus());
				}else{
					loanDetail.setLoanStatus(StatusEnum.FIN_LOAN_ALL_LOAN.getStatus());
				}
				
			}
			pageRequest.setData(loanDetails);
			return pageRequest;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Integer batchDelete(int[] ids) throws ManagerException {
		try {
			return loanDetailMapper.batchDelete(ids);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

}
