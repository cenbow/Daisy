package com.yourong.backend.tc.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.Debt;
import com.yourong.core.tc.model.TransactionInterest;
import com.yourong.core.tc.model.biz.BorrowInterestDetailBiz;
import com.yourong.core.tc.model.biz.BorrowRepayInterestBiz;
import com.yourong.core.tc.model.biz.BorrowRepayInterestCollect;
import com.yourong.core.tc.model.query.BorrowRepayQuery;
import com.yourong.core.tc.model.query.BorrowTransactionInterestQuery;

public interface TransactionInterestService {

	
	/** 分页查询 **/
	Page<TransactionInterest> findByPage(Page<TransactionInterest> pageRequest, Map<String, Object> map);
	
	List<TransactionInterest> selectToBePaidTransactionInInterest(Date date);

	void scheduePayInterestAndPrincipal();

	Page<BorrowRepayInterestBiz> findPageBorrowRepayInterest(BorrowRepayQuery query);

	BorrowRepayInterestCollect findBorrowRepayInterestCollect(Date date);

	Page<BorrowInterestDetailBiz> findDayBorrowRepayInterestByBorrowId(BorrowTransactionInterestQuery query,String projectids);
}
