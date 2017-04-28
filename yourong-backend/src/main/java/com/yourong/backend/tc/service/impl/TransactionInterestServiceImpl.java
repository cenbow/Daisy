package com.yourong.backend.tc.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.yourong.core.tc.model.biz.BorrowInterestDetailBiz;
import com.yourong.core.tc.model.biz.BorrowRepayInterestBiz;
import com.yourong.core.tc.model.biz.BorrowRepayInterestCollect;
import com.yourong.core.tc.model.query.BorrowRepayQuery;
import com.yourong.core.tc.model.query.BorrowTransactionInterestQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.yourong.backend.tc.service.TransactionInterestService;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.core.ic.model.Debt;
import com.yourong.core.tc.manager.TransactionInterestManager;
import com.yourong.core.tc.model.TransactionInterest;
import com.yourong.core.tc.model.query.TransactionInterestQuery;

@Service
public class TransactionInterestServiceImpl implements TransactionInterestService {

	private static Logger logger = LoggerFactory.getLogger(TransactionInterestServiceImpl.class);
	
	@Resource
	private TransactionInterestManager transactionInterestManager;

	
	
	
	@Override
	public Page<TransactionInterest> findByPage(Page<TransactionInterest> pageRequest, Map<String, Object> map) {
		try {
			return transactionInterestManager.findByPage(pageRequest, map);
		} catch (Exception e) {
			logger.error("分页查询本息失败", e);
		}
		return null;
	}
	
	
	
	
	
	@Override
	public List<TransactionInterest> selectToBePaidTransactionInInterest(
			Date date) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized void scheduePayInterestAndPrincipal() {
//		List<TransactionInterest> transactionInterests = transactionInterestManager.selectToBePaidTransactionInterests();
//		if(Collections3.isNotEmpty(transactionInterests)){
//			for (TransactionInterest transactionInterest : transactionInterests){
//				try{
//					transactionInterestService.payInterestAndPrincipal(transactionInterest);
////					//将该笔交易利息状态设为已支付
////					transactionInterest.setRealPayInterest(transactionInterest.getPayableInterest());
////					transactionInterest.setRealPayPrincipal(transactionInterest.getPayablePrincipal());
////					transactionInterest.setPayTime(transactionInterest.getEndDate());
////					transactionInterest.setStatus(sta);
////					transactionInterestMapper.updateByPrimaryKeySelective(transactionInterest);
//					
//				} catch(Exception e){
//					logger.error("支付本金或利息失败，transactionInterestId=" + transactionInterest.getId(),e);
//				}
//			}
//		}
	}

	@Override
	public Page<BorrowRepayInterestBiz> findPageBorrowRepayInterest(BorrowRepayQuery query) {
		try {
			return transactionInterestManager.findPageBorrowRepayInterest(query);
		} catch (Exception e) {
			logger.error("查询借款人还本付息异常", e);
		}
		return null;
	}

	@Override
	public BorrowRepayInterestCollect findBorrowRepayInterestCollect(Date date) {
		return transactionInterestManager.findBorrowRepayInterestCollect(date);
	}

	@Override
	public Page<BorrowInterestDetailBiz> findDayBorrowRepayInterestByBorrowId(BorrowTransactionInterestQuery query,String projectidss) {
		return transactionInterestManager.findDayBorrowRepayInterestByBorrowId(query,projectidss);
	}
}
